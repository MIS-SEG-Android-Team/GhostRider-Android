package org.rmj.g3appdriver.GCircle.Apps.User_Guide.Activitiy;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.GCircle.Apps.User_Guide.Adapter.RecyclerviewUserGuideAdapter;
import org.rmj.g3appdriver.GCircle.Apps.User_Guide.Dialog.Dialog_File_Upload;
import org.rmj.g3appdriver.GCircle.Apps.User_Guide.ViewModel.VMGuide;
import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.GCircle.Etc.PositionCode;
import org.rmj.g3appdriver.GCircle.room.Entities.EGuides;
import org.rmj.g3appdriver.R;
import org.rmj.g3appdriver.etc.FileUtility;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;

import java.io.File;
import java.util.List;
import java.util.Objects;


public class Activity_Manual extends AppCompatActivity {

    private EmployeeSession poSession;
    private VMGuide mViewmodel;
    private LoadDialog poDialog;
    private MessageBox poMessage;
    private Dialog_File_Upload dialogFileUpload;
    private FileUtility poFileUtil;
    private RecyclerviewUserGuideAdapter loAdapter;

    private LinearLayout layout_nodisp;
    private RecyclerView rcv_guides;
    private Toolbar toolbar;
    private TextInputEditText txt_fileSearch;

    private File fileResult;

    private final ActivityResultLauncher<Intent> poDocument = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            try {

                if (result.getResultCode() == RESULT_OK){

                    if (result.getData() != null){
                        Uri uri = Objects.requireNonNull(result.getData().getData());

                        if (!poFileUtil.GetMimeType(uri).equals("application/pdf")){
                            dialogFileUpload.SetResult(false, "File type is not pdf");
                            dialogFileUpload.btn_upload.setEnabled(false);
                            return;
                        }

                        fileResult = poFileUtil.GetFileFromUri(uri);

                        dialogFileUpload.load_prog.setVisibility(GONE);
                        dialogFileUpload.btn_upload.setEnabled(true);

                        dialogFileUpload.SetResult(true, poFileUtil.GetDisplayName(uri));
                    }
                }else {
                    if (result.getResultCode() != RESULT_CANCELED){
                        dialogFileUpload.SetResult(false,"Error attaching file");
                    }
                }

            }catch (Exception e){
                dialogFileUpload.SetResult(false,e.getMessage());
            }
        }
    });

    private interface OnDialogButtonCallback{
        void OnPositive();
        void OnNegative();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manual);

        poSession = EmployeeSession.getInstance(this);
        mViewmodel = new ViewModelProvider(this).get(VMGuide.class);
        poDialog = new LoadDialog(this);
        poMessage = new MessageBox(this);
        poFileUtil = new FileUtility(this);

        InitViews();
        InitObserver();
        InitListener();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_guidelines, menu);

        //TODO ADD GUIDE, allow access to MIS Department only
        if (Objects.equals(poSession.getDeptID(), DeptCode.MANAGEMENT_INFORMATION_SYSTEM)){

            //TODO AREA HEAD / SUPERVISORS
            if (Integer.parseInt(poSession.getEmployeeLevel()) > DeptCode.LEVEL_RANK_FILE){
                menu.findItem(R.id.action_add_guideline).setEnabled(true);
            }else {

                //TODO RANKED LEVEL, DEVELOPERS ONLY
                if (Objects.equals(poSession.getPositionID(), PositionCode.Code_Junior_Programmer) ||
                        Objects.equals(poSession.getPositionID(), PositionCode.Code_Senior_Programmer)){

                    menu.findItem(R.id.action_add_guideline).setVisible(true);
                }
            }
        }else {
            menu.findItem(R.id.action_add_guideline).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }else if (item.getItemId() == R.id.action_add_guideline){

            dialogFileUpload = new Dialog_File_Upload(Activity_Manual.this, new Dialog_File_Upload.OnAction() {
                @Override
                public void OnSelectFile(Intent intent) {
                    poDocument.launch(Intent.createChooser(intent, "Select a file"));
                }

                @Override
                public void OnUpload(String filename) {
                    try {

                        if (poFileUtil.ReadFileToBytes(fileResult) == null){
                            InitMessage(0, R.drawable.baseline_error_24, "Cannot read file properly", "Okay", "", new OnDialogButtonCallback() {
                                @Override
                                public void OnPositive() {}

                                @Override
                                public void OnNegative() {}
                            });
                            return;
                        }

                        mViewmodel.UploadGuide(fileResult.getAbsolutePath(), filename, new VMGuide.OnUploadGuide() {
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void OnSuccess() {
                                dialogFileUpload.load_prog.setIndeterminate(false);
                                dialogFileUpload.load_prog.setProgress(100);
                                dialogFileUpload.load_prog.setIndicatorColor(ContextCompat.getColor(Activity_Manual.this, R.color.check_green));

                                dialogFileUpload.btn_upload.setEnabled(false);
                                dialogFileUpload.btn_select.setEnabled(true);
                                dialogFileUpload.btn_cancel.setEnabled(true);
                            }

                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void OnFailed(String message) {

                                dialogFileUpload.mtv_file.setText(message);

                                dialogFileUpload.load_prog.setIndeterminate(false);
                                dialogFileUpload.load_prog.setProgress(100);
                                dialogFileUpload.load_prog.setIndicatorColor(ContextCompat.getColor(Activity_Manual.this, R.color.cross_red));

                                dialogFileUpload.btn_upload.setEnabled(false);
                                dialogFileUpload.btn_select.setEnabled(true);
                                dialogFileUpload.btn_cancel.setEnabled(true);
                            }
                        });

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            dialogFileUpload.Show();

        } else if (item.getItemId() == R.id.action_download_guideline) {

            poDialog.initDialog("User Guide", "Downloading Guides", false);

            mViewmodel.DownloadGuides(new VMGuide.OnDownloadGuides() {
                @Override
                public void OnDownloading() {
                    poDialog.show();
                }

                @Override
                public void OnSuccess() {
                    poDialog.dismiss();
                }

                @Override
                public void OnFailed(String message) {
                    poDialog.dismiss();

                    InitMessage(0, R.drawable.baseline_error_24, message, "Okay", "", new OnDialogButtonCallback() {
                        @Override
                        public void OnPositive() {}

                        @Override
                        public void OnNegative() {}
                    });
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressLint("NotifyDataSetChanged")
    private void InitAdapter(List<EGuides> loGuides){

        loAdapter = new RecyclerviewUserGuideAdapter(loGuides, new RecyclerviewUserGuideAdapter.OnViewGuide() {
            @Override
            public void OnView(EGuides loGuide) {

                Intent loIntent = new Intent(Activity_Manual.this, Activity_PDFViewer.class);
                loIntent.putExtra("pdf_url", loGuide.getsURlxx());
                startActivity(loIntent);
                overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
            }
        });

        loAdapter.notifyDataSetChanged();
        rcv_guides.setAdapter(loAdapter);
        rcv_guides.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
    }
    private void InitMessage(int messageType, int statusIcon, String message, String posText, String negText, OnDialogButtonCallback callback){

        poMessage.initDialog();
        poMessage.setTitle("User Guide");
        poMessage.setIcon(statusIcon);
        poMessage.setMessage(message);

        poMessage.setPositiveButton(posText, (view, dialog) -> {
            dialog.dismiss();
            callback.OnPositive();
        });

        if (messageType == 1){
            poMessage.setNegativeButton(negText, (view, dialog) -> {
                dialog.dismiss();
                callback.OnNegative();

            });
        }

        poMessage.show();
    }
    private void InitViews(){

        layout_nodisp = findViewById(R.id.layout_nodisp);
        rcv_guides = findViewById(R.id.rcv_guides);
        toolbar = findViewById(R.id.toolbar_collectionList);
        txt_fileSearch = findViewById(R.id.txt_fileSearch);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
    }
    private void InitObserver(){

        mViewmodel.GetGuides().observe(Activity_Manual.this, new Observer<List<EGuides>>() {
            @Override
            public void onChanged(List<EGuides> eGuides) {

                if (eGuides.size() > 0){

                    layout_nodisp.setVisibility(GONE);
                    rcv_guides.setVisibility(VISIBLE);

                    InitAdapter(eGuides);
                }else {
                    layout_nodisp.setVisibility(VISIBLE);
                    rcv_guides.setVisibility(GONE);
                }
            }
        });
    }
    private void InitListener(){

        txt_fileSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    String query = s.toString();
                    loAdapter.GetFilter().filter(query);
                    loAdapter.notifyDataSetChanged();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

}