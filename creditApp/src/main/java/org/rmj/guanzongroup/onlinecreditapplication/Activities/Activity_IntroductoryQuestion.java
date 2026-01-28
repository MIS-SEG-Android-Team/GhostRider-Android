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
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textview.MaterialTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DGanadoOnline;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppConstants;
import org.rmj.g3appdriver.lib.Ganado.pojo.InstallmentInfo;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMARContact;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMIntroductoryQuestion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class Activity_IntroductoryQuestion extends AppCompatActivity {

    public static final String TAG = Activity_IntroductoryQuestion.class.getSimpleName();
    private VMIntroductoryQuestion mViewModel;
    private MessageBox poMessage;

    private DGanadoOnline.CashPrice loCashPrice;
    private InstallmentInfo loInstallment;

    private MaterialTextView lblBranchNm, lblBrandAdd;
    private MaterialAutoCompleteTextView txtBranchNm, txtBrandNm, txtModelNm;
    private TextInputEditText txtDownPymnt, txtAmort, txtDTarget, txt_remarks;
    private MaterialAutoCompleteTextView spnApplType, spnCustType, spnAcctTerm;
    private MaterialButton btnCreate;

    private Double ldblMinDown = 0.00;

    public static Activity_IntroductoryQuestion newInstance() {
        return new Activity_IntroductoryQuestion();
    }

    public interface OnMessageButton {
        void OnPositive();
        void OnNegative();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_introductory_question);

        mViewModel = new ViewModelProvider(Activity_IntroductoryQuestion.this).get(VMIntroductoryQuestion.class);
        poMessage = new MessageBox(Activity_IntroductoryQuestion.this);

        initWidgets();
        initObservers();
        initData();
        initListener();
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }

    private void initWidgets(){

        MaterialToolbar toolbar = findViewById(R.id.toolbar_introduction);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        lblBranchNm = findViewById(R.id.lbl_headerBranch);
        lblBrandAdd = findViewById(R.id.lbl_headerAddress);
        txtBranchNm = findViewById(R.id.txt_branchName);
        txtBrandNm = findViewById(R.id.txt_brandName);
        txtModelNm = findViewById(R.id.txt_modelName);
        txtDownPymnt = findViewById(R.id.txt_downpayment);
        txtAmort = findViewById(R.id.txt_monthlyAmort);
        txtDTarget = findViewById(R.id.txt_dateTarget);
        spnApplType = findViewById(R.id.spn_applicationType);
        spnCustType = findViewById(R.id.spn_customerType);
        spnAcctTerm = findViewById(R.id.spn_installmentTerm);
        txt_remarks = findViewById(R.id.txt_remarks);

        btnCreate = findViewById(R.id.btn_createCreditApp);
    }

    private void initObservers(){

        mViewModel.GetUserInfo().observe(Activity_IntroductoryQuestion.this, eBranchInfo -> {
            try {
                lblBranchNm.setText(eBranchInfo.sBranchNm);
                lblBrandAdd.setText(eBranchInfo.sAddressx);
                txtBranchNm.setText(eBranchInfo.sBranchNm);
                mViewModel.getModel().setBranchCde(eBranchInfo.sBranchCd);
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.GetAllBranchInfo().observe(Activity_IntroductoryQuestion.this, loList -> {
            try{
                ArrayList<String> strings = new ArrayList<>();
                for(int x = 0; x < loList.size(); x++){
                    strings.add(loList.get(x).getBranchNm());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_IntroductoryQuestion.this, android.R.layout.simple_spinner_dropdown_item, strings.toArray(new String[0]));
                txtBranchNm.setAdapter(adapter);

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

        mViewModel.GetAllMcBrand().observe(Activity_IntroductoryQuestion.this, loList -> {
            try{
                ArrayList<String> strings = new ArrayList<>();
                for(int x = 0; x < loList.size(); x++){
                    strings.add(loList.get(x).getBrandNme());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_IntroductoryQuestion.this, android.R.layout.simple_spinner_dropdown_item, strings.toArray(new String[0]));
                txtBrandNm.setAdapter(adapter);

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

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_IntroductoryQuestion.this, android.R.layout.simple_spinner_dropdown_item, strings.toArray(new String[0]));
                    txtModelNm.setAdapter(adapter);

                    txtModelNm.setOnItemClickListener((adapterView, view, i, l) -> {

                        for(int x = 0; x < loList.size(); x++){

                            if(txtModelNm.getText().toString().equalsIgnoreCase(loList.get(x).getModelNme() +" "+ loList.get(x).getModelCde())){
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

                //set model id to model
                mViewModel.getModel().setModelIDxx(modelID);
                mViewModel.GetProductModel().setModelIDx(modelID);

                //get cash price of model
                mViewModel.GetCashPrice(mViewModel.GetProductModel().getModelIDx()).observe(Activity_IntroductoryQuestion.this, new Observer<DGanadoOnline.CashPrice>() {
                    @Override
                    public void onChanged(DGanadoOnline.CashPrice cashPrice) {

                        if (cashPrice == null){
                            return;
                        }
                        loCashPrice = cashPrice;
                    }
                });

                //get installment details of model
                mViewModel.GetMinimumDownpayment(mViewModel.GetProductModel().getModelIDx(), new VMARContact.OnRetrieveInstallmentInfo() {
                    @Override
                    public void OnRetrieve(InstallmentInfo loResult) {

                        if (loResult == null){
                            Toast.makeText(Activity_IntroductoryQuestion.this, "Installment info not found!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        loInstallment = loResult;
                    }

                    @Override
                    public void OnFailed(String message) {
                        Toast.makeText(Activity_IntroductoryQuestion.this, message, Toast.LENGTH_LONG).show();
                    }
                });
                InitializePayment();

//                String lsModel = mViewModel.getModel().getModelIDxx();
//                int lnTermxx = mViewModel.getModel().getAccTermxx();
//
//                mViewModel.GetInstallmentPlanDetail(modelID).observe(Activity_IntroductoryQuestion.this, mcDPInfo -> {
//                    try{
//
//                        //todo: parse data retrieved
//                        if(mViewModel.InitializeTermAndDownpayment(mcDPInfo)) {
//
//                            //todo: get amortization detail
//                            mViewModel.GetAmortizationDetail(lsModel, lnTermxx).observe(Activity_IntroductoryQuestion.this, mcAmortInfo -> {
//
//                                mViewModel.setModelAmortization(mcAmortInfo);
//
//                                //todo: get minimum downpayment
//                                ldblMinDown = mViewModel.GetMinimumDownpayment();
//
//                                mViewModel.getModel().setDownPaymt(ldblMinDown);
//                                txtDownPymnt.setText(String.valueOf(ldblMinDown));
//
//                                double lnAmort = mViewModel.GetMonthlyPayment(mViewModel.getModel().getAccTermxx());
//
//                                mViewModel.getModel().setMonthlyAm(lnAmort);
//                                txtAmort.setText(FormatUIText.getCurrencyUIFormat(String.valueOf(lnAmort)));
//
//                            });
//                        }
//                    } catch (Exception e){
//                        e.printStackTrace();
//                    }
//                });

            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private void initData(){

        //display default values
        spnApplType.setText(CreditAppConstants.APPLICATION_TYPE[0]);
        spnCustType.setText(CreditAppConstants.CUSTOMER_TYPE[0]);
        spnAcctTerm.setText(CreditAppConstants.INSTALLMENT_TERM[0]);

        //initialize application type adapter
        spnApplType.setAdapter(CreditAppConstants.getAdapter(Activity_IntroductoryQuestion.this, CreditAppConstants.APPLICATION_TYPE));
        mViewModel.getModel().setAppTypex("1");
        mViewModel.GetProductModel().setPaymForm("1");

        //initialize customer type adapter
        spnCustType.setAdapter(CreditAppConstants.getAdapter(Activity_IntroductoryQuestion.this, CreditAppConstants.CUSTOMER_TYPE));
        mViewModel.getModel().setCustTypex("1");

        //initialize target date
        txtDTarget.setText(new AppConstants().CURRENT_DATE_WORD);

        //initialize installment term adapter
        spnAcctTerm.setAdapter(CreditAppConstants.getAdapter(Activity_IntroductoryQuestion.this, CreditAppConstants.INSTALLMENT_TERM));

        //Default value has been set here instead inside of the model in order
        // to calculate monthly amortization upon selection of model.
        mViewModel.getModel().setAccTermxx(0);
        mViewModel.GetProductModel().setTermIDxx(String.valueOf(mViewModel.getModel().getAccTermxx()));

        //initialie default dp and monthly
        mViewModel.getModel().setDownPaymt(Double.parseDouble("0.00"));
        mViewModel.GetProductModel().setDownPaym("0.00");
        mViewModel.GetProductModel().setMonthAmr("0.00");
    }

    private void initListener(){

        txtDTarget.setOnClickListener(v -> {

            final Calendar newCalendar = Calendar.getInstance();

            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
            final DatePickerDialog  StartTime = new DatePickerDialog(Activity_IntroductoryQuestion.this, (view131, year, monthOfYear, dayOfMonth) -> {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String lsDate = dateFormatter.format(newDate.getTime());

                txtDTarget.setText(lsDate);
                mViewModel.getModel().setTargetDte(lsDate);

            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

            StartTime.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            StartTime.show();
        });

        spnApplType.setOnItemClickListener(new OnItemClickListener(spnApplType));
        spnCustType.setOnItemClickListener(new OnItemClickListener(spnCustType));
        spnAcctTerm.setOnItemClickListener(new OnItemClickListener(spnAcctTerm));

        txtDownPymnt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus){

                    if (txtDownPymnt.getText() == null || txtDownPymnt.getText().toString().trim().isEmpty()){

                        InitMessage(0, R.drawable.baseline_error_24, "Please enter downpayment", "Okay", "", new OnMessageButton() {
                            @Override
                            public void OnPositive() {}

                            @Override
                            public void OnNegative() {}
                        });
                        return;
                    }

                    mViewModel.getModel().setDownPaymt(Double.parseDouble(txtDownPymnt.getText().toString()));
                    mViewModel.GetProductModel().setDownPaym(txtDownPymnt.getText().toString());

                    InitializePayment();
                }
            }
        });

        //txtDownPymnt.addTextChangedListener(new FormatUIText.CurrencyFormat(txtDownPymnt));
//        txtDownPymnt.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {            }
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                try{
//                    if (!Objects.requireNonNull(txtDownPymnt.getText()).toString().trim().isEmpty()) {
//
//                        txtDownPymnt.removeTextChangedListener(this);
//
//                        String lsInput = txtDownPymnt.getText().toString().trim();
//
//                        Double lnInput = FormatUIText.getParseDouble(lsInput);
//
//                        mViewModel.getModel().setDownPaymt(lnInput);
//
//                        double lnVal = mViewModel.getModel().getDownPaymt();
//
//                        double lnMonthly = mViewModel.GetMonthlyPayment(lnVal);
//
//                        mViewModel.getModel().setMonthlyAm(lnMonthly);
//
//                        txtAmort.setText(FormatUIText.getCurrencyUIFormat(String.valueOf(lnMonthly)));
//
//                        txtDownPymnt.addTextChangedListener(this);
//                    }
//                } catch (Exception e){
//                    e.printStackTrace();
//
//                    txtDownPymnt.addTextChangedListener(this);
//                }
//            }
//            @Override
//            public void afterTextChanged(Editable editable) {}
//        });

        btnCreate.setOnClickListener(view -> {

            if (ldblMinDown > mViewModel.getModel().getDownPaymt()){
                poMessage.initDialog();
                poMessage.setIcon(R.drawable.baseline_error_24);
                poMessage.setTitle("Credit Online Application");
                poMessage.setMessage("Minimum down is not sufficient.");
                poMessage.setPositiveButton("Okay", (view1, dialog) -> dialog.dismiss());
                poMessage.show();

            }else {

                mViewModel.getModel().setsRemarks(Objects.requireNonNull(txt_remarks.getText()).toString());

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
                        poMessage.setIcon(R.drawable.baseline_error_24);
                        poMessage.setTitle("Credit Online Application");
                        poMessage.setMessage(message);
                        poMessage.setPositiveButton("Okay", (view1, dialog) -> dialog.dismiss());
                        poMessage.show();
                    }
                });
            }
        });

    }

    private void InitializePayment(){

        //check mc model, before initializing computation
        if (mViewModel.GetProductModel().getModelIDx() == null || mViewModel.GetProductModel().getModelIDx().isEmpty()){
            Toast.makeText(Activity_IntroductoryQuestion.this, "Please select MC model", Toast.LENGTH_SHORT).show();
            return;
        }

        //validate mc payment info
        if (loInstallment == null){
            Toast.makeText(Activity_IntroductoryQuestion.this, "Could not find installment info", Toast.LENGTH_SHORT);
            return;
        }

        if (loCashPrice == null){
            Toast.makeText(Activity_IntroductoryQuestion.this, "Could not find cash info", Toast.LENGTH_SHORT);
            return;
        }

        //initialize default value, if downpayment is empty
        if (mViewModel.GetProductModel().getDownPaym() == null || Double.parseDouble(mViewModel.GetProductModel().getDownPaym()) == 0.00){
            mViewModel.GetProductModel().setDownPaym(String.valueOf(loInstallment.getMinimumDownpayment()));
        }else if (mViewModel.getModel().getDownPaymt() == 0.00){
            mViewModel.getModel().setDownPaymt(loInstallment.getMinimumDownpayment());
        }
        txtDownPymnt.setText(String.valueOf(mViewModel.GetProductModel().getDownPaym()));

        //set defualt minimum down, if downpayment is lesser than requried amount
        if (Double.parseDouble(mViewModel.GetProductModel().getDownPaym()) < loInstallment.getMinimumDownpayment()){

            InitMessage(0, R.drawable.baseline_error_24, "Downpayment does not meet the required amount", "Okay", "", new OnMessageButton() {
                @Override
                public void OnPositive() {
                    mViewModel.GetProductModel().setDownPaym(String.valueOf(loInstallment.getMinimumDownpayment()));
                    txtDownPymnt.setText(String.valueOf(loInstallment.getMinimumDownpayment()));
                }

                @Override
                public void OnNegative() {}
            });

            return;
        } else if (Double.parseDouble(mViewModel.GetProductModel().getDownPaym()) >= loCashPrice.CashPrce){

            InitMessage(0, R.drawable.baseline_error_24, "Downpayment cannot exceed or be equal to the cash price", "Okay", "", new OnMessageButton() {
                @Override
                public void OnPositive() {
                    mViewModel.GetProductModel().setDownPaym(String.valueOf(loInstallment.getMinimumDownpayment()));
                    txtDownPymnt.setText(String.valueOf(loInstallment.getMinimumDownpayment()));
                }

                @Override
                public void OnNegative() {}
            });
            return;
        }

        //compute monthly, triggered by terms or downpayment
        double ldbl_monthly = mViewModel.GetMonthlyAmortization(Integer.parseInt(mViewModel.GetProductModel().getTermIDxx()));

        mViewModel.getModel().setMonthlyAm(ldbl_monthly);
        mViewModel.GetProductModel().setMonthAmr(String.valueOf(ldbl_monthly));

        txtAmort.setText(String.valueOf(ldbl_monthly));

        mViewModel.CalculateNewDownpayment(mViewModel.GetProductModel().getModelIDx(), Integer.parseInt(mViewModel.GetProductModel().getTermIDxx()),
                Double.parseDouble(mViewModel.GetProductModel().getDownPaym()), new VMARContact.OnCalculateNewDownpayment() {
                    @Override
                    public void OnCalculate(double lnResult) {
                        mViewModel.getModel().setMonthlyAm(lnResult);
                        mViewModel.GetProductModel().setMonthAmr(String.valueOf(lnResult));

                        txtAmort.setText(String.valueOf(lnResult));
                    }

                    @Override
                    public void OnFailed(String message) {
                        if (message == null || message.isEmpty()){
                            Toast.makeText(Activity_IntroductoryQuestion.this, "Failted to calculate downpayment", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(Activity_IntroductoryQuestion.this, message, Toast.LENGTH_SHORT).show();
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

    private class OnItemClickListener implements AdapterView.OnItemClickListener {

        private final View loView;

        public OnItemClickListener(View loView) {
            this.loView = loView;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(loView == spnApplType){
                mViewModel.getModel().setAppTypex(String.valueOf(i));
                mViewModel.GetProductModel().setPaymForm(String.valueOf(i));
            } else if(loView == spnCustType){
                mViewModel.getModel().setCustTypex(String.valueOf(i));
            } else if(loView == spnAcctTerm){
                mViewModel.getModel().setAccTermxx(i);
                mViewModel.GetProductModel().setTermIDxx(String.valueOf(mViewModel.getModel().getAccTermxx()));

                InitializePayment();

//                double lnMonthly = mViewModel.GetMonthlyPayment(mViewModel.getModel().getAccTermxx());
//                txtAmort.setText(FormatUIText.getCurrencyUIFormat(String.valueOf(lnMonthly)));
            }
        }
    }
}