package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicantInfo;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppConstants;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.Pension;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.OnParseListener;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMPensionInfo;

import java.util.Objects;

public class Activity_PensionInfo extends AppCompatActivity {

    private VMPensionInfo mViewModel;
    private MessageBox poMessage;

    private MaterialAutoCompleteTextView spnSector;
    private String sectorPosition = "-1";
    private TextInputEditText txtRangxx, txtYearxx, txtOthInc, txtRngInc;
    private MaterialButton btnPrvs, btnNext;
    private MaterialToolbar toolbar;

    private String TransNox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_PensionInfo.this).get(VMPensionInfo.class);
        setContentView(R.layout.activity_pension_info);
        poMessage = new MessageBox(Activity_PensionInfo.this);
        initWidgets();
        mViewModel.InitializeApplication(getIntent());
        mViewModel.GetApplication().observe(Activity_PensionInfo.this, new Observer<ECreditApplicantInfo>() {
            @Override
            public void onChanged(ECreditApplicantInfo app) {
                try {
                    TransNox = app.getTransNox();
                    mViewModel.getModel().setTransNox(app.getTransNox());
                    mViewModel.getModel().setcMeanInfo(app.getAppMeans());
                    mViewModel.setCvlStatus(app.getIsSpouse());
                    mViewModel.ParseData(app, new OnParseListener() {
                        @Override
                        public void OnParse(Object args) {
                            Pension loDetail = (Pension) args;
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
            }
        });

        spnSector.setAdapter(new ArrayAdapter<>(Activity_PensionInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.PENSION_SECTOR));
        spnSector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.getModel().setPensionSector(String.valueOf(position));
            }
        });

        btnNext.setOnClickListener(v -> SavePensionInfo());
        btnPrvs.setOnClickListener(v -> {
            Intent loIntent = new Intent(Activity_PensionInfo.this, Activity_Finance.class);
            loIntent.putExtra("sTransNox", TransNox);
            startActivity(loIntent);
            overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
            finish();
        });
    }

    private void SavePensionInfo() {
        if (txtRangxx.getText().toString().trim().isEmpty()) {
            mViewModel.getModel().setPensionIncomeRange(0);
        } else {
            mViewModel.getModel().setPensionIncomeRange(Long.parseLong(txtRangxx.getText().toString().trim()));

        }
        mViewModel.getModel().setRetirementYear(Objects.requireNonNull(txtYearxx.getText()).toString().trim());
        mViewModel.getModel().setNatureOfIncome(Objects.requireNonNull(txtOthInc.getText()).toString().trim());

        if (txtRngInc.getText().toString().trim().isEmpty()) {
            mViewModel.getModel().setRangeOfIncom(0);
        } else {
            mViewModel.getModel().setRangeOfIncom(Long.parseLong(txtRngInc.getText().toString().trim()));
        }
        mViewModel.SaveData(new OnSaveInfoListener() {
            @Override
            public void OnSave(String args) {
                Intent loIntent;
                if(mViewModel.getCvlStatus().equalsIgnoreCase("1") ||
                        mViewModel.getCvlStatus().equalsIgnoreCase("5")){
                    loIntent = new Intent(Activity_PensionInfo.this, Activity_SpouseInfo.class);
                }else{
                    loIntent = new Intent(Activity_PensionInfo.this, Activity_DisbursementInfo.class);
                }
                loIntent.putExtra("sTransNox", args);
                startActivity(loIntent);
                overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
                finish();

            }

            @Override
            public void OnFailed(String message) {
                poMessage.initDialog();
                poMessage.setIcon(R.drawable.baseline_error_24);
                poMessage.setTitle("Credit Online Application");
                poMessage.setMessage(message);
                poMessage.setPositiveButton("Okay", (view1, dialog) -> dialog.dismiss());
                poMessage.show();
            }
        });
    }

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar_PensionInfo);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        spnSector = findViewById(R.id.spn_psnSector);

        txtRangxx = findViewById(R.id.txt_psnIncRange);
        txtYearxx = findViewById(R.id.txt_psnRtrmntYear);
        txtOthInc = findViewById(R.id.txt_natureOfIncome);
        txtRngInc = findViewById(R.id.txt_incRange);

        btnPrvs = findViewById(R.id.btn_creditAppPrvs);
        btnNext = findViewById(R.id.btn_creditAppNext);

    }

    @SuppressLint("NewApi")
    public void setUpFieldsFromLocalDB(Pension foDetail) throws JSONException {
        if (foDetail != null){
            if(!foDetail.getPensionSector().isEmpty()){
                spnSector.setText(CreditAppConstants.PENSION_SECTOR[Integer.parseInt(foDetail.getPensionSector())], false);
//                spnSector.setSelection(Integer.parseInt(foDetail.getPensionSector()));
                sectorPosition = foDetail.getPensionSector();
                mViewModel.getModel().setPensionSector(foDetail.getPensionSector());
            }

            txtRangxx.setText(!"".equalsIgnoreCase(String.valueOf(foDetail.getPensionIncomeRange())) ? String.valueOf(foDetail.getPensionIncomeRange()) : "");
            txtYearxx.setText(!"".equalsIgnoreCase(String.valueOf(foDetail.getRetirementYear())) ? String.valueOf(foDetail.getRetirementYear()) : "");
            txtOthInc.setText(!"".equalsIgnoreCase(String.valueOf(foDetail.getNatureOfIncome())) ? String.valueOf(foDetail.getNatureOfIncome()) : "");
            txtRngInc.setText(!"".equalsIgnoreCase(String.valueOf(foDetail.getRangeOfIncome())) ? String.valueOf(foDetail.getRangeOfIncome()) : "");


        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent loIntent = new Intent(Activity_PensionInfo.this, Activity_Finance.class);
            loIntent.putExtra("sTransNox", TransNox);
            startActivity(loIntent);
            overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent loIntent = new Intent(Activity_PensionInfo.this, Activity_Finance.class);
        loIntent.putExtra("sTransNox", TransNox);
        startActivity(loIntent);
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
        finish();
    }

    @Override
    protected void onDestroy() {
        getViewModelStore().clear();
        super.onDestroy();
    }
}