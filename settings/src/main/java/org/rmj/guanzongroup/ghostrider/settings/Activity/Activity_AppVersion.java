package org.rmj.guanzongroup.ghostrider.settings.Activity;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.lib.Version.VersionInfo;
import org.rmj.guanzongroup.ghostrider.settings.Dialog.DialogUpdateNotice;
import org.rmj.guanzongroup.ghostrider.settings.ViewModel.VMAppVersion;
import org.rmj.guanzongroup.ghostrider.settings.R;
import org.rmj.guanzongroup.ghostrider.settings.adapter.RecyclerViewAppVersionAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Activity_AppVersion extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private MaterialTextView build_version;
    private MaterialTextView date_build;
    private MaterialTextView lbl_updatedLog;
    private MaterialTextView lbl_updatedFixedConcerns;
    private MaterialButton btn_checkupdate;
    private RecyclerView rec_updatedNewFeatures;
    private RecyclerView rec_updatedFixedConcerns;
    private RecyclerViewAppVersionAdapter versionAdapter;
    private VMAppVersion mViewModel;
    private LoadDialog poload;
    private MessageBox pomessage;
    List<VersionInfo> versionInfoList;
    private DialogUpdateNotice poUpdateNotice;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_version);

        //declare View Model for checking/downloading app version updates
        mViewModel = new ViewModelProvider(Activity_AppVersion.this).get(VMAppVersion.class);

        versionInfoList = new ArrayList<>();

        //instantiate dialog and message box to appear upon checking update
        poload = new LoadDialog(Activity_AppVersion.this);
        pomessage = new MessageBox(Activity_AppVersion.this);

        //initialize dialog for displaying message
        pomessage.initDialog();

        //declare ui from layout
        toolbar = findViewById(R.id.toolbar);
        build_version = findViewById(R.id.build_version);
        date_build = findViewById(R.id.date_build);
        lbl_updatedLog = findViewById(R.id.lbl_updatedLog);
        lbl_updatedFixedConcerns = findViewById(R.id.lbl_updatedFixedConcerns);

        btn_checkupdate = findViewById(R.id.btn_checkupdate);
        rec_updatedNewFeatures = findViewById(R.id.rec_updatelogs);
        rec_updatedFixedConcerns = findViewById(R.id.rec_updatedFixedConcerns);

        //set toolbar and action
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       /*set by default, the current build version of  app*/
       String build_name = AppConfigPreference.getInstance(Activity_AppVersion.this).getVersionName(); //get build version
       build_version.setText(build_name); //display build version

        //call method to get the list of versions
        getAppVersion("Check Updates", "Getting Updates . . .", "Check Updates", "Successfully Get Updates");
        //call method for button listener
        btnCheckUpdate();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void btnCheckUpdate(){
        btn_checkupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get button text
                String btnText = btn_checkupdate.getHint().toString();
                if(btnText.equals("Download Updates")){
                    //call dialog for download notice
                    poUpdateNotice = new DialogUpdateNotice(Activity_AppVersion.this, versionInfoList);
                    poUpdateNotice.initDialog();
                    poUpdateNotice.show();
                }
            }
        });
    }
    public void getAppVersion(String poloadTitle, String poloadMsg, String poMsgTitle, String poMsg){
        mViewModel.getVersionList(new VMAppVersion.onDownloadVersionList() {
            @Override
            public void onDownload() {
                //show dialog
                poload.initDialog(poloadTitle, poloadMsg, false);
                poload.show();
            }
            @Override
            public void onSuccess(List<VersionInfo> list) {
                //set the list value
                versionInfoList= list;

                //set list to Adapter, New Features and Fixed Concerns
                setListToAdapter(Activity_AppVersion.this, R.layout.update_version_logs, rec_updatedNewFeatures, getListFromVersionInfo("New Features"));
                setListToAdapter(Activity_AppVersion.this, R.layout.update_version_logs_fixed_concerns, rec_updatedFixedConcerns, getListFromVersionInfo("Fixed Concerns"));

                //set button text
                btn_checkupdate.setHint("Download Updates");

                pomessage.setIcon(R.drawable.baseline_message_24);

                //set dialog message and button
                pomessage.setTitle(poMsgTitle);
                pomessage.setMessage(poMsg);

                //show message
                pomessage.show();

                //close message
                poload.dismiss();

                //set action button to close message dialog
                pomessage.setPositiveButton("Dismiss", new MessageBox.DialogButton() {
                    @Override
                    public void OnButtonClick(View view, AlertDialog dialog) {
                        //close dialogs upon clicking
                        dialog.dismiss();
                    }
                });
            }
            @Override
            public void onFailed(String message) {
                pomessage.setIcon(R.drawable.baseline_error_24);

                //set dialog message and button
                pomessage.setTitle(poMsgTitle);
                pomessage.setMessage(message);

                //show message
                pomessage.show();

                //close message
                poload.dismiss();

                pomessage.setPositiveButton("Dismiss", new MessageBox.DialogButton() {
                    @Override
                    public void OnButtonClick(View view, AlertDialog dialog) {
                        //close dialogs upon clicking
                        dialog.dismiss();
                    }
                });
            }
        });
    }
    public void setListToAdapter(Context context, @LayoutRes int res, RecyclerView recyclerView, List<HashMap<String, String>> listVersionInfo){
        //attach list of version updates to the Adapter and ListView Object
        versionAdapter = new RecyclerViewAppVersionAdapter(listVersionInfo, context);
        versionAdapter.resource = res;

        //validate recycler view obj if visible before attaching the adapter
        if(recyclerView.getVisibility() == View.VISIBLE) {
            recyclerView.setAdapter(versionAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
    }
    public List<HashMap<String, String>> getListFromVersionInfo(String verioninfoCategory){
        HashMap<String, String> hmVersionInfo = new HashMap<>();
        List<HashMap<String, String>> listVersionInfo = new ArrayList<>();

        String feature;
        String descript;
        String fixedconcerns;

        //if return list of version updates are available, continue
        if(versionInfoList.size() > 0){

            //set button text
            btn_checkupdate.setHint("Download Updates");

            //loop through versioninfo list, then loop again on its sublist
            for (int x = 0; x < versionInfoList.size(); x++){
                try {
                    if(verioninfoCategory.equals("New Features")){
                        //set text on recycler view label
                        lbl_updatedLog.setText("New Features");
                        //if passed arg is New Features, get the list size of New Features from VersionInfo
                        for (int y = 0; y < versionInfoList.get(x).getNewFeatures().size(); y++){
                            feature = versionInfoList.get(x).getNewFeatures().get(y).getsFeaturex();
                            descript = versionInfoList.get(x).getNewFeatures().get(y).getsDescript();
                            //map feature as KEY, descript as VALUE
                            hmVersionInfo.put(feature, descript);
                            //add to list
                            listVersionInfo.add(hmVersionInfo);
                        }
                    }
                    //if passed arg is New Features, get the list size of Fixed Concerns from VersionInfo
                    if(verioninfoCategory.equals("Fixed Concerns")){
                        //set text on recycler view label
                        lbl_updatedFixedConcerns.setText("Fixed Concerns");
                        for (int y = 0; y < versionInfoList.get(x).getOthers().size(); y++){
                            fixedconcerns = versionInfoList.get(x).getOthers().get(y);
                            //map fixed concerns as both KEY and VALUE
                            hmVersionInfo.put(fixedconcerns, fixedconcerns);
                            //add to list
                            listVersionInfo.add(hmVersionInfo);
                        }
                    }
                }catch (Exception e){
                    Log.d( this.getClass().getSimpleName(), e.getMessage());
                }
            }
        }
        return listVersionInfo;
    }
}