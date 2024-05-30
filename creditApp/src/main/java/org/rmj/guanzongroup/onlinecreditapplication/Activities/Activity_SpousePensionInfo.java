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
import androidx.lifecycle.ViewModelProvider;


import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;


import org.json.JSONException;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.SpousePension;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppConstants;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.OnParseListener;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMSpousePensionInfo;

import java.util.Objects;

public class Activity_SpousePensionInfo extends AppCompatActivity {

    private VMSpousePensionInfo mViewModel;
    private MessageBox poMessage;

    private TextInputEditText txtPensionAmt, txtRetirementYr, txtOtherSrc, txtOtherSrcInc;
    private MaterialAutoCompleteTextView spnSector;
    private MaterialButton btnNext, btnPrvs;
    private MaterialToolbar toolbar;

    private String TransNox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_SpousePensionInfo.this).get(VMSpousePensionInfo.class);
        poMessage = new MessageBox(Activity_SpousePensionInfo.this);
        setContentView(R.layout.activity_spouse_pension_info);
        initWidgets();
        mViewModel.InitializeApplication(getIntent());
        mViewModel.GetApplication().observe(Activity_SpousePensionInfo.this, app -> {
            TransNox = app.getTransNox();
            mViewModel.getModel().setTransNox(app.getTransNox());
            mViewModel.ParseData(app, new OnParseListener() {
                @Override
                public void OnParse(Object args) {
                    SpousePension loDetail = (SpousePension) args;
                    try {
                        setUpFieldsFromLocalDB(loDetail);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });


        spnSector.setAdapter(new ArrayAdapter<>(Activity_SpousePensionInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.PENSION_SECTOR));
        spnSector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.getModel().setPensionSector(String.valueOf(position));
            }
        });

        btnNext.setOnClickListener(v -> SaveSpousePensionInfo());
        btnPrvs.setOnClickListener(v -> {
            returnPrevious();
        });
    }

    private void SaveSpousePensionInfo() {
        if (txtPensionAmt.getText().toString().isEmpty()) {
            mViewModel.getModel().setPensionIncomeRange(0);
        } else {
            mViewModel.getModel().setPensionIncomeRange(Long.parseLong(
                    Objects.requireNonNull(txtPensionAmt.getText()).toString().trim()));
        }

        mViewModel.getModel().setRetirementYear(Objects.requireNonNull(txtRetirementYr.getText()).toString().trim());
        mViewModel.getModel().setNatureOfIncome(Objects.requireNonNull(txtOtherSrc.getText()).toString().trim());

        if (txtOtherSrcInc.getText().toString().trim().isEmpty()) {
            mViewModel.getModel().setRangeOfIncom(0);
        } else {
            mViewModel.getModel().setRangeOfIncom(Long.parseLong((Objects.requireNonNull(txtOtherSrcInc.getText()).toString().trim())));
        }

        mViewModel.SaveData(new OnSaveInfoListener() {
            @Override
            public void OnSave(String args) {
                Intent loIntent = new Intent(Activity_SpousePensionInfo.this, Activity_DisbursementInfo.class);
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

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar_SpousePensionInfo);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        spnSector = findViewById(R.id.spn_psnSector);

        txtPensionAmt = findViewById(R.id.txt_pension_amt);
        txtRetirementYr = findViewById(R.id.txt_retirement_yr);
        txtOtherSrc = findViewById(R.id.txt_other_source);
        txtOtherSrcInc = findViewById(R.id.txt_other_source_income);

        btnNext = findViewById(R.id.btn_creditAppNext);
        btnPrvs = findViewById(R.id.btn_creditAppPrvs);


    }

    @SuppressLint("NewApi")
    public void setUpFieldsFromLocalDB(SpousePension foDetail) throws JSONException {
        if (foDetail != null){
            if(!foDetail.getPensionSector().isEmpty()){
                spnSector.setText(CreditAppConstants.PENSION_SECTOR[Integer.parseInt(foDetail.getPensionSector())], false);
//                spnSector.setSelection(Integer.parseInt(foDetail.getPensionSector()));
                mViewModel.getModel().setPensionSector(foDetail.getPensionSector());
            }

            txtPensionAmt.setText( !"".equalsIgnoreCase(String.valueOf(foDetail.getPensionIncomeRange())) ? String.valueOf(foDetail.getPensionIncomeRange()) : "");
            txtRetirementYr.setText( !"".equalsIgnoreCase(String.valueOf(foDetail.getRetirementYear())) ? String.valueOf(foDetail.getRetirementYear()) : "");
            txtOtherSrc.setText( !"".equalsIgnoreCase(String.valueOf(foDetail.getNatureOfIncome())) ? String.valueOf(foDetail.getNatureOfIncome()) : "");
            txtOtherSrcInc.setText( !"".equalsIgnoreCase(String.valueOf(foDetail.getRangeOfIncome())) ? String.valueOf(foDetail.getRangeOfIncome()) : "");
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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
        Intent loIntent = new Intent(Activity_SpousePensionInfo.this, Activity_SpouseSelfEmploymentInfo.class);
        loIntent.putExtra("sTransNox", TransNox);
        startActivity(loIntent);
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
        finish();
    }
}