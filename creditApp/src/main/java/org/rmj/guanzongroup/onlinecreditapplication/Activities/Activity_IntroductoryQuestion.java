/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.creditApp
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.lib.integsys.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.lib.integsys.CreditApp.CreditAppConstants;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMIntroductoryQuestion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class Activity_IntroductoryQuestion extends AppCompatActivity {
    public static final String TAG = Activity_IntroductoryQuestion.class.getSimpleName();
    private VMIntroductoryQuestion mViewModel;
    private MessageBox poMessage;

    private TextView lblBranchNm, lblBrandAdd, lblDate;
    private AutoCompleteTextView txtBranchNm, txtBrandNm, txtModelNm;
    private TextInputLayout tilApplType;
    private TextInputEditText txtDownPymnt, txtAmort, txtDTarget;
    private AutoCompleteTextView spnApplType, spnCustType, spnAcctTerm;
    private MaterialButton btnCreate;

    public static Activity_IntroductoryQuestion newInstance() {
        return new Activity_IntroductoryQuestion();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VMIntroductoryQuestion.class);
        poMessage = new MessageBox(Activity_IntroductoryQuestion.this);
        setContentView(R.layout.activity_introductory_question);
        initWidgets();

        mViewModel.GetUserInfo().observe(this, eBranchInfo -> {
            try {
                lblBranchNm.setText(eBranchInfo.sBranchNm);
                lblBrandAdd.setText(eBranchInfo.sAddressx);
                txtBranchNm.setText(eBranchInfo.sBranchNm);
                mViewModel.getModel().setBranchCde(eBranchInfo.sBranchCd);
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        spnApplType.setText(CreditAppConstants.APPLICATION_TYPE[0]);
        spnCustType.setText(CreditAppConstants.CUSTOMER_TYPE[0]);
        spnAcctTerm.setText(CreditAppConstants.INSTALLMENT_TERM[0]);
        mViewModel.GetApplicationType().observe(this, stringArrayAdapter -> {
            spnApplType.setAdapter(stringArrayAdapter);
            spnApplType.setDropDownBackgroundResource(R.drawable.bg_gradient_light);
            mViewModel.getModel().setAppTypex(1);
        });

        mViewModel.GetCustomerType().observe(this, stringArrayAdapter -> {
            spnCustType.setAdapter(stringArrayAdapter);
            spnCustType.setDropDownBackgroundResource(R.drawable.bg_gradient_light);
            mViewModel.getModel().setCustTypex(1);
        });

        mViewModel.GetInstallmentTerm().observe(this, stringArrayAdapter -> {
            spnAcctTerm.setAdapter(stringArrayAdapter);
            spnAcctTerm.setDropDownBackgroundResource(R.drawable.bg_gradient_light);

            //Default value has been set here instead inside of the model in order
            // to calculate monthly amortization upon selection of model.
            mViewModel.getModel().setAccTermxx(0);
        });

        txtDTarget.setText(new AppConstants().CURRENT_DATE_WORD);

        txtDTarget.setOnClickListener(v -> {
            final Calendar newCalendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
            final DatePickerDialog  StartTime = new DatePickerDialog(this, (view131, year, monthOfYear, dayOfMonth) -> {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String lsDate = dateFormatter.format(newDate.getTime());
                txtDTarget.setText(lsDate);
                mViewModel.getModel().setTargetDte(lsDate);
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            StartTime.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            StartTime.show();
        });

        mViewModel.GetAllBranchInfo().observe(this, loList -> {
            try{
                ArrayList<String> strings = new ArrayList<>();
                for(int x = 0; x < loList.size(); x++){
                    strings.add(loList.get(x).getBranchNm());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_IntroductoryQuestion.this, android.R.layout.simple_spinner_dropdown_item, strings.toArray(new String[0]));
                txtBranchNm.setAdapter(adapter);
                txtBranchNm.setDropDownBackgroundResource(R.drawable.bg_gradient_light);

                txtBranchNm.setOnItemClickListener((adapterView, view, i, l) -> {
                    for(int x = 0; x < loList.size(); x++){
                        if(txtBranchNm.getText().toString().equalsIgnoreCase(loList.get(x).getBranchNm())){
                            mViewModel.getModel().setBranchCde(loList.get(x).getBranchCd());
                            break;
                        }
                    }
                });

            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.GetAllMcBrand().observe(this, loList -> {
            try{
                ArrayList<String> strings = new ArrayList<>();
                for(int x = 0; x < loList.size(); x++){
                    strings.add(loList.get(x).getBrandNme());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_IntroductoryQuestion.this, android.R.layout.simple_spinner_dropdown_item, strings.toArray(new String[0]));
                txtBrandNm.setAdapter(adapter);
                txtBrandNm.setDropDownBackgroundResource(R.drawable.bg_gradient_light);

                txtBrandNm.setOnItemClickListener((adapterView, view, i, l) -> {
                    for(int x = 0; x < loList.size(); x++){
                        if(txtBrandNm.getText().toString().equalsIgnoreCase(loList.get(x).getBrandNme())){
                            mViewModel.setBrandID(loList.get(x).getBrandIDx());
                            mViewModel.getModel().setBrandIDxx(loList.get(x).getBrandIDx());
                            break;
                        }
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.GetBrandID().observe(Activity_IntroductoryQuestion.this, brandID -> {
            try {
                mViewModel.GetAllBrandModelInfo(brandID).observe(Activity_IntroductoryQuestion.this, loList -> {
                    ArrayList<String> strings = new ArrayList<>();
                    for(int x = 0; x < loList.size(); x++){
                        strings.add(loList.get(x).getModelNme() +" "+ loList.get(x).getModelCde());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, strings.toArray(new String[0]));
                    txtModelNm.setAdapter(adapter);
                    txtModelNm.setDropDownBackgroundResource(R.drawable.bg_gradient_light);

                    txtModelNm.setOnItemClickListener((adapterView, view, i, l) -> {
                        for(int x = 0; x < loList.size(); x++){
                            if(txtModelNm.getText().toString().equalsIgnoreCase(loList.get(x).getModelNme() +" "+ loList.get(x).getModelCde())){
                                mViewModel.getModel().setModelIDxx(loList.get(x).getModelIDx());
                                mViewModel.setModelID(loList.get(x).getModelIDx());
                                break;
                            }
                        }
                    });

                });
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.GetModelID().observe(Activity_IntroductoryQuestion.this, modelID -> {
            try{
                String lsModel = mViewModel.getModel().getModelIDxx();
                int lnTermxx = mViewModel.getModel().getAccTermxx();
                mViewModel.GetInstallmentPlanDetail(modelID).observe(Activity_IntroductoryQuestion.this, mcDPInfo -> {
                    try{
                        if(mViewModel.InitializeTermAndDownpayment(mcDPInfo)) {

                            mViewModel.GetAmortizationDetail(lsModel, lnTermxx).observe(Activity_IntroductoryQuestion.this, mcAmortInfo -> {

                                mViewModel.setModelAmortization(mcAmortInfo);

                                double lnMinDp = mViewModel.GetMinimumDownpayment();
                                mViewModel.getModel().setDownPaymt(lnMinDp);
                                txtDownPymnt.setText(String.valueOf(lnMinDp));

                                double lnAmort = mViewModel.GetMonthlyPayment(mViewModel.getModel().getAccTermxx());
                                mViewModel.getModel().setMonthlyAm(lnAmort);
                                txtAmort.setText(FormatUIText.getCurrencyUIFormat(String.valueOf(lnAmort)));

                            });
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                });

            } catch (Exception e){
                e.printStackTrace();
            }
        });

        spnApplType.setOnItemClickListener(new OnItemClickListener(spnApplType));
        spnCustType.setOnItemClickListener(new OnItemClickListener(spnCustType));
        spnAcctTerm.setOnItemClickListener(new OnItemClickListener(spnAcctTerm));

        txtDownPymnt.addTextChangedListener(new FormatUIText.CurrencyFormat(txtDownPymnt));

        txtDownPymnt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    if (!Objects.requireNonNull(txtDownPymnt.getText()).toString().trim().isEmpty()) {

                        txtDownPymnt.removeTextChangedListener(this);

                        String lsInput = txtDownPymnt.getText().toString().trim();

                        Double lnInput = FormatUIText.getParseDouble(lsInput);

                        mViewModel.getModel().setDownPaymt(lnInput);

                        double lnVal = mViewModel.getModel().getDownPaymt();

                        double lnMonthly = mViewModel.GetMonthlyPayment(lnVal);

                        mViewModel.getModel().setMonthlyAm(lnMonthly);

                        txtAmort.setText(FormatUIText.getCurrencyUIFormat(String.valueOf(lnMonthly)));

                        txtDownPymnt.addTextChangedListener(this);
                    }
                } catch (Exception e){
                    e.printStackTrace();

                    txtDownPymnt.addTextChangedListener(this);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnCreate.setOnClickListener(view -> {
            mViewModel.SaveData(new OnSaveInfoListener() {
                @Override
                public void OnSave(String args) {
                    Intent loIntent = new Intent(Activity_IntroductoryQuestion.this, Activity_PersonalInfo.class);
                    loIntent.putExtra("sTransNox", args);
                    startActivity(loIntent);
                    overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
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
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }

    private void initWidgets(){
        Toolbar toolbar = findViewById(R.id.toolbar_introduction);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        lblBranchNm = findViewById(R.id.lbl_headerBranch);
        lblBrandAdd = findViewById(R.id.lbl_headerAddress);
        lblDate = findViewById(R.id.lbl_headerDate);
        txtBranchNm = findViewById(R.id.txt_branchName);
        txtBrandNm = findViewById(R.id.txt_brandName);
        txtModelNm = findViewById(R.id.txt_modelName);
        txtDownPymnt = findViewById(R.id.txt_downpayment);
        txtAmort = findViewById(R.id.txt_monthlyAmort);
        txtDTarget = findViewById(R.id.txt_dateTarget);
        spnApplType = findViewById(R.id.spn_applicationType);
        tilApplType = findViewById(R.id.til_customerType);
        spnCustType = findViewById(R.id.spn_customerType);
        spnAcctTerm = findViewById(R.id.spn_installmentTerm);

        btnCreate = findViewById(R.id.btn_createCreditApp);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        getViewModelStore().clear();
        super.onDestroy();
    }

    private class OnItemClickListener implements AdapterView.OnItemClickListener {

        private final View loView;

        public OnItemClickListener(View loView) {
            this.loView = loView;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(loView == spnApplType){
                mViewModel.getModel().setAppTypex(i);
            } else if(loView == spnCustType){
                mViewModel.getModel().setCustTypex(i);
            } else if(loView == spnAcctTerm){
                mViewModel.getModel().setAccTermxx(i);
                double lnMonthly = mViewModel.GetMonthlyPayment(mViewModel.getModel().getAccTermxx());

                txtAmort.setText(FormatUIText.getCurrencyUIFormat(String.valueOf(lnMonthly)));
            }
        }
    }
}