package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.Means;
import org.rmj.guanzongroup.onlinecreditapplication.Etc.RadioGridGroup;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.OnParseListener;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMMeasnInfo;

import java.util.Objects;

public class Activity_MeansInfoSelection extends AppCompatActivity  {

    private MaterialRadioButton rbEmployed, rbSEmployd, rbFinancex, rbPensionx;
    private RadioGridGroup rgMeans;
    private MaterialButton btnNext, btnPrvs;
    private MaterialToolbar toolbar;
    private VMMeasnInfo mViewModel;

    private MessageBox poMessage;

    private String TransNox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_means_info_selection);
        initWidgets();
        mViewModel = new ViewModelProvider(Activity_MeansInfoSelection.this).get(VMMeasnInfo.class);
        poMessage = new MessageBox(Activity_MeansInfoSelection.this);
        mViewModel.InitializeApplication(getIntent());
        mViewModel.GetApplication().observe(Activity_MeansInfoSelection.this, app -> {
            try {
                TransNox = app.getTransNox();
                mViewModel.getModel().setTransNox(app.getTransNox());

                mViewModel.getModel().setIncmeSrc(app.getAppMeans());
                if(app.getAppMeans().equalsIgnoreCase("0")){
                    rbEmployed.setChecked(true);
                }else if(app.getAppMeans().equalsIgnoreCase("1")){
                    rbSEmployd.setChecked(true);
                }else if(app.getAppMeans().equalsIgnoreCase("2")){
                    rbFinancex.setChecked(true);
                }else if(app.getAppMeans().equalsIgnoreCase("3")){
                    rbPensionx.setChecked(true);
                }
                mViewModel.ParseData(app, new OnParseListener() {
                    @Override
                    public void OnParse(Object args) {
                        Means loDetail = (Means) args;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        btnNext.setOnClickListener(v -> SaveMeansInfo());
        btnPrvs.setOnClickListener(v -> {
            Intent loIntent = new Intent(Activity_MeansInfoSelection.this, Activity_ResidenceInfo.class);
            loIntent.putExtra("sTransNox", TransNox);
            startActivity(loIntent);
            finish();
        });

    }

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar_MeansInfoSelection);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        rbEmployed = findViewById(R.id.rb_employed);
        rbSEmployd = findViewById(R.id.rb_sEmployed);
        rbFinancex = findViewById(R.id.rb_finance);
        rbPensionx = findViewById(R.id.rb_pension);
        btnNext = findViewById(R.id.btn_creditAppNext);
        btnPrvs = findViewById(R.id.btn_creditAppPrvs);
        rgMeans = findViewById(R.id.rgMeans);
        rgMeans.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_employed){
                mViewModel.getModel().setIncmeSrc("0");
            }else if (checkedId == R.id.rb_sEmployed){
                mViewModel.getModel().setIncmeSrc("1");
            }else if (checkedId == R.id.rb_finance){
                mViewModel.getModel().setIncmeSrc("2");
            }else if (checkedId == R.id.rb_pension){
                mViewModel.getModel().setIncmeSrc("3");
            }
        });

    }
    private void SaveMeansInfo() {

        mViewModel.SaveData(new OnSaveInfoListener() {
            @Override
            public void OnSave(String args) {
                Intent loIntent = new Intent(Activity_MeansInfoSelection.this, Activity_EmploymentInfo.class);
                loIntent.putExtra("sTransNox", args);
                startActivity(loIntent);
                overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
                finish();
            }

            @Override
            public void OnFailed(String message) {
                poMessage.initDialog();
                poMessage.setTitle("Credit Online Application");
                poMessage.setMessage(message);
                poMessage.setPositiveButton("Okay", (view1, dialog) -> dialog.dismiss());
                poMessage.show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent loIntent = new Intent(Activity_MeansInfoSelection.this, Activity_ResidenceInfo.class);
            loIntent.putExtra("sTransNox", TransNox);
            startActivity(loIntent);
            overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent loIntent = new Intent(Activity_MeansInfoSelection.this, Activity_ResidenceInfo.class);
        loIntent.putExtra("sTransNox", TransNox);
        startActivity(loIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        getViewModelStore().clear();
        super.onDestroy();
    }
}