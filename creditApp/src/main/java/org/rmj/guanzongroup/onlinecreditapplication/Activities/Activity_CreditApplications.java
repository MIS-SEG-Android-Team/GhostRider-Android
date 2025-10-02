package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;


import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCreditApplication;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_CreditApplications.this).get(VMCreditApplications.class);
        poDialogx = new LoadDialog(Activity_CreditApplications.this);
        poMessage = new MessageBox(Activity_CreditApplications.this);
        setContentView(R.layout.activity_credit_applications);
        initWidgets();
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
                LinearLayoutManager loLayout = new LinearLayoutManager(Activity_CreditApplications.this);
                loLayout.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(loLayout);
                recyclerView.setAdapter(new CreditApplicationsAdapter(eCreditApplications, new CreditApplicationsAdapter.OnItemActionClickListener() {
                    @Override
                    public void Resend(DCreditApplication.ApplicationLog creditapp) {
                        String lsTransNo = creditapp.sTransNox;
                        resendApp(lsTransNo);
                    }

                    @Override
                    public void OnPreview(DCreditApplication.ApplicationLog creditapp) {
                        DialogPreviewApplication loDialog = new DialogPreviewApplication(Activity_CreditApplications.this);
                        loDialog.initDialog(creditapp, new DialogPreviewApplication.OnDialogActionClickListener() {
                            @Override
                            public void DocumentScan(DCreditApplication.ApplicationLog creditApp) {
                                Toast.makeText(Activity_CreditApplications.this, "Under development.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        loDialog.show();
                    }
                }));
            } catch (Exception e){
                e.printStackTrace();

            }
        });
    }

    private void initWidgets(){
        toolbar = findViewById(R.id.toolbar_applicationHistory);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        txtSearch = findViewById(R.id.txt_Search);
        recyclerView = findViewById(R.id.rectangles_applicationHistory);
        noRecord = findViewById(R.id.layout_application_history_noRecord);
    }

    @Override
    public void onBackPressed() {
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
                poMessage.initDialog();
                poMessage.setIcon(R.drawable.baseline_message_24);
                poMessage.setTitle("Credit Online Application");
                poMessage.setMessage("Applicant sent!");
                poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                poMessage.show();
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
    }
}