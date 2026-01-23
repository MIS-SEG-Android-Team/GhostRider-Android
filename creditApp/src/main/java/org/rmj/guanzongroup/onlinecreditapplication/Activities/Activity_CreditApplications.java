package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;


import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCreditApplication;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplication;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.onlinecreditapplication.Adapter.CreditApplicationsAdapter;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMCreditApplications;
import org.rmj.guanzongroup.onlinecreditapplication.dialog.DialogPreviewApplication;

import java.util.Objects;

public class Activity_CreditApplications extends AppCompatActivity {

    private VMCreditApplications mViewModel;

    private MaterialToolbar toolbar;
    private TextInputEditText txtSearch;
    private RecyclerView recyclerView;
    private LinearLayout noRecord;

    private LoadDialog poDialogx;
    private MessageBox poMessage;

    public interface OnMessageButton {
        void OnPositive();
        void OnNegative();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_credit_applications);

        mViewModel = new ViewModelProvider(Activity_CreditApplications.this).get(VMCreditApplications.class);
        poDialogx = new LoadDialog(Activity_CreditApplications.this);
        poMessage = new MessageBox(Activity_CreditApplications.this);

        initWidgets();
        initData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initWidgets(){

        toolbar = findViewById(R.id.toolbar_applicationHistory);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        txtSearch = findViewById(R.id.txt_Search);
        recyclerView = findViewById(R.id.rectangles_applicationHistory);
        noRecord = findViewById(R.id.layout_application_history_noRecord);
    }

    private void initData(){

        mViewModel.ImportApplications(new VMCreditApplications.OnImportApplicationsListener() {
            @Override
            public void OnImport() {
                poDialogx.initDialog("Credit Online Application", "Importing applications. Please wait...", false);
                poDialogx.show();
            }

            @Override
            public void OnSuccess() {
                poDialogx.dismiss();
            }

            @Override
            public void OnFailed(String message) {
                poDialogx.dismiss();
                poMessage.initDialog();
                poMessage.setIcon(R.drawable.baseline_error_24);
                poMessage.setTitle("Credit Online Application");
                poMessage.setMessage(message);
                poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                poMessage.show();
            }
        });

        mViewModel.GetUserInfo().observe(Activity_CreditApplications.this, employeeBranch -> {
            try{

            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.GetApplicationList().observe(Activity_CreditApplications.this, eCreditApplications -> {

            try{

                //show record not found
                if (eCreditApplications == null || eCreditApplications.size() < 1){
                    noRecord.setVisibility(LinearLayout.VISIBLE);
                    recyclerView.setVisibility(LinearLayout.GONE);
                    return;
                }
                recyclerView.setVisibility(LinearLayout.VISIBLE);
                noRecord.setVisibility(LinearLayout.GONE);

                //show previww, if not approved (GOCAS Number is empty)
                CreditApplicationsAdapter loAdapter = new CreditApplicationsAdapter(eCreditApplications, new CreditApplicationsAdapter.OnItemActionClickListener() {
                    @Override
                    public void OnPreview(DCreditApplication.ApplicationLog creditapp) {

                        if (creditapp.sGOCASNox == null || creditapp.sGOCASNox.trim().isEmpty() || creditapp.sGOCASNox.equalsIgnoreCase("null")){

                            //show details on preview
                            DialogPreviewApplication loDialog = new DialogPreviewApplication(Activity_CreditApplications.this);
                            loDialog.initDialog(creditapp, new DialogPreviewApplication.OnDialogActionClickListener() {
                                @Override
                                public void DocumentScan(DCreditApplication.ApplicationLog creditApp) {
                                    Toast.makeText(Activity_CreditApplications.this, "Under development.", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void SendApplication(DCreditApplication.ApplicationLog creditApp) {
                                    String lsTransNo = creditapp.sTransNox;
                                    resendApp(lsTransNo);
                                }
                            });
                            loDialog.show();

                            return;
                        }

                        //ask user for next action, MC AR Contract Emtry or Preview Only
                        InitMessage(1, R.drawable.ic_baseline_confirmation_pin_24, "Confirm next action", "AR Contract", "Preview Only", new OnMessageButton() {
                            @Override
                            public void OnPositive() {

                                //get credit application
                                if (mViewModel.GetApplication(creditapp.sTransNox) == null){
                                    Toast.makeText(Activity_CreditApplications.this, "Could not find application", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                //proceed to mc contract
                                Intent loIntent = new Intent(Activity_CreditApplications.this, Activity_MC_Contract.class);
                                loIntent.putExtra("sTransNox", creditapp.sTransNox);
                                startActivity(loIntent);

                            }

                            @Override
                            public void OnNegative() {

                                //show details on preview
                                DialogPreviewApplication loDialog = new DialogPreviewApplication(Activity_CreditApplications.this);
                                loDialog.initDialog(creditapp, new DialogPreviewApplication.OnDialogActionClickListener() {
                                    @Override
                                    public void DocumentScan(DCreditApplication.ApplicationLog creditApp) {
                                        Toast.makeText(Activity_CreditApplications.this, "Under development.", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void SendApplication(DCreditApplication.ApplicationLog creditApp) {
                                        String lsTransNo = creditapp.sTransNox;
                                        resendApp(lsTransNo);
                                    }
                                });
                                loDialog.show();
                            }
                        });

                    }
                });

                //initialize adapter
                LinearLayoutManager loLayout = new LinearLayoutManager(Activity_CreditApplications.this);
                loLayout.setOrientation(RecyclerView.VERTICAL);

                recyclerView.setLayoutManager(loLayout);
                recyclerView.setAdapter(loAdapter);

                txtSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        loAdapter.GetFilter().filter(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

            } catch (Exception e){
                e.printStackTrace();

            }
        });

    }

    private void resendApp(String args){

        mViewModel.ResendApplication(args, new VMCreditApplications.OnResendApplicationListener() {
            @Override
            public void OnResend() {
                poDialogx.initDialog("Credit Online Application", "Importing applications. Please wait...", false);
                poDialogx.show();
            }

            @Override
            public void OnSuccess() {
                poDialogx.dismiss();

                InitMessage(0, R.drawable.baseline_message_24, "Application has been sent", "Okay", "", new OnMessageButton() {
                    @Override
                    public void OnPositive() {}

                    @Override
                    public void OnNegative() {}
                });
            }

            @Override
            public void OnFailed(String message) {
                poDialogx.dismiss();

                InitMessage(0, R.drawable.baseline_message_24, message, "Okay", "", new OnMessageButton() {
                    @Override
                    public void OnPositive() {}

                    @Override
                    public void OnNegative() {}
                });
            }
        });
    }

    private void InitMessage(int messageType, int statusIcon, String message, String posText, String negText, OnMessageButton callback){

        poMessage.initDialog();
        poMessage.setTitle("Credit Application");
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
}