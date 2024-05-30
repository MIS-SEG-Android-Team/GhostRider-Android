package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.imageview.ShapeableImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.ReviewAppDetail;
import org.rmj.guanzongroup.onlinecreditapplication.Adapter.LoanAppDetailReviewAdapter;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.OnParseListener;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMReviewLoanApp;

import java.util.List;
import java.util.Objects;

public class Activity_ReviewLoanApp extends AppCompatActivity {

    private String TransNox;
    private MaterialTextView lblClientNm;
//    private ListView recyclerView;
    private RecyclerView recyclerView;
    private ShapeableImageView imgClient;
    private MaterialButton btnCamera;
    private MaterialButton btnSave, btnPrvs;

    private LoadDialog poDialogx;
    private MessageBox poMessage;

    private VMReviewLoanApp mViewModel;
    private MaterialToolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_ReviewLoanApp.this).get(VMReviewLoanApp.class);
        poMessage = new MessageBox(Activity_ReviewLoanApp.this);
        poDialogx = new LoadDialog(Activity_ReviewLoanApp.this);
        setContentView(R.layout.activity_review_loan_app);
        initWidgets();
        mViewModel.InitializeApplication(getIntent());

        mViewModel.GetApplication().observe(Activity_ReviewLoanApp.this, app -> {
            try {
                TransNox = app.getTransNox();
                mViewModel.setInfo(app);
                lblClientNm.setText(app.getClientNm());
                mViewModel.ParseData(app, new OnParseListener() {
                    @Override
                    public void OnParse(Object args) {
                        List<ReviewAppDetail> loDetail =  (List<ReviewAppDetail>) args;
                        try {
                            setUpFieldsFromLocalDB(loDetail);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });



        btnPrvs.setOnClickListener(v -> finish());
    }


    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar_ReviewLoanApp);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        lblClientNm = findViewById(R.id.lbl_clientNme);

        recyclerView = findViewById(R.id.recyclerview_applicationInfo);

        imgClient = findViewById(R.id.img_loanApplicant);

        btnCamera = findViewById(R.id.btn_camera);

        btnSave = findViewById(R.id.btn_loanAppSave);

        btnPrvs = findViewById(R.id.btn_creditAppPrvs);

        btnPrvs = findViewById(R.id.btn_creditAppPrvs);

        btnSave.setOnClickListener(v -> mViewModel.SaveData(new VMReviewLoanApp.OnSaveCreditAppListener() {
            @Override
            public void OnSave() {
                poDialogx.initDialog("Credit Online Application", "Saving application. Please wait...", false);
                poDialogx.show();
            }

            @Override
            public void OnSuccess(String args) {
                poDialogx.dismiss();
                poMessage.initDialog();
                poMessage.setTitle("Credit Online Application");
                poMessage.setMessage(args);
                poMessage.setPositiveButton("Okay", (view, dialog) -> {
                    dialog.dismiss();
                    startActivity(new Intent(Activity_ReviewLoanApp.this, Activity_CreditApplications.class));
                    finish();
                });
                poMessage.show();
            }

            @Override
            public void OnSaveLocal(String message) {
                poDialogx.dismiss();
                poMessage.initDialog();
                poMessage.setTitle("Credit Online Application");
                poMessage.setMessage(message);
                poMessage.setPositiveButton("Okay", (view, dialog) -> {
                    dialog.dismiss();
                    startActivity(new Intent(Activity_ReviewLoanApp.this, Activity_CreditApplications.class));
                    finish();
                });
                poMessage.show();
            }

            @Override
            public void OnFailed(String message) {
                poDialogx.dismiss();
                poMessage.initDialog();
                poMessage.setTitle("Credit Online Application");
                poMessage.setMessage(message);
                poMessage.setPositiveButton("Okay", (view1, dialog) -> dialog.dismiss());
                poMessage.show();
            }
        }));
        btnPrvs.setOnClickListener(v -> returnPrevious());

    }


    @SuppressLint("NewApi")
    public void setUpFieldsFromLocalDB(List<ReviewAppDetail> infoList) throws JSONException {
        LoanAppDetailReviewAdapter loAdapter = new LoanAppDetailReviewAdapter(infoList, () -> {

        });
        LinearLayoutManager loManager = new LinearLayoutManager(Activity_ReviewLoanApp.this);
        loManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(loManager);
        recyclerView.setAdapter(loAdapter);

    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            returnPrevious();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        returnPrevious();
    }

    @Override
    protected void onDestroy() {
        getViewModelStore().clear();
        super.onDestroy();
    }

    private void returnPrevious(){
        Intent loIntent = new Intent(Activity_ReviewLoanApp.this, Activity_ComakerResidence.class);
        loIntent.putExtra("sTransNox", TransNox);
        startActivity(loIntent);
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
        finish();
    }
}