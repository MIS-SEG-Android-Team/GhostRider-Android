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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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


import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DGanadoOnline;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EMcBrand;
import org.rmj.g3appdriver.GCircle.room.Entities.EMcModel;
import org.rmj.g3appdriver.etc.AppConstants;
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
import java.util.List;
import java.util.Objects;

public class Activity_IntroductoryQuestion extends AppCompatActivity {

    public static final String TAG = Activity_IntroductoryQuestion.class.getSimpleName();
    private VMIntroductoryQuestion mViewModel;
    private MessageBox poMessage;

    private DGanadoOnline.CashPrice loCashPrice;
    private InstallmentInfo loInstallment;
    private DEmployeeInfo.EmployeeBranch loEmployee = new DEmployeeInfo.EmployeeBranch();

    private List<EBranchInfo> laBranch = new ArrayList<>();
    private List<EMcBrand> laBrand = new ArrayList<>();
    private List<EMcModel> laModel = new ArrayList<>();

    private MaterialTextView lblBranchNm, lblBrandAdd;
    private MaterialAutoCompleteTextView txtBranchNm, txtBrandNm, txtModelNm;
    private TextInputEditText txtDownPymnt, txtAmort, txtDTarget, txt_remarks;
    private MaterialAutoCompleteTextView spnApplType, spnCustType, spnAcctTerm;
    private MaterialButton btnCreate;
    private ImageButton btn_compute;

    private Double ldblMinDown = 0.00;

    private Integer lnUnitSelect = 0;
    private Integer lnCustSelect = 0;
    private String lsBranchSelect;
    private String lsBrandSelect;
    private String lsModelSelect;
    private Integer lnTermSelect = 0;

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
        btn_compute = findViewById(R.id.btn_compute);
        txt_remarks = findViewById(R.id.txt_remarks);

        btnCreate = findViewById(R.id.btn_createCreditApp);
    }

    private void initObservers(){

        //initialize adapters
        spnApplType.setAdapter(CreditAppConstants.getAdapter(Activity_IntroductoryQuestion.this, CreditAppConstants.APPLICATION_TYPE));
        spnCustType.setAdapter(CreditAppConstants.getAdapter(Activity_IntroductoryQuestion.this, CreditAppConstants.CUSTOMER_TYPE));
        spnAcctTerm.setAdapter(CreditAppConstants.getAdapter(Activity_IntroductoryQuestion.this, CreditAppConstants.INSTALLMENT_TERM));

        mViewModel.GetUserInfo().observe(Activity_IntroductoryQuestion.this, eBranchInfo -> {
            loEmployee = eBranchInfo;

            initModel();
        });

        mViewModel.GetAllBranchInfo().observe(Activity_IntroductoryQuestion.this, loList -> {
            laBranch = loList;

            ArrayList<String> laBranchNms = new ArrayList<>();
            for(int x = 0; x < laBranch.size(); x++){
                laBranchNms.add(laBranch.get(x).getBranchNm());
            }

            ArrayAdapter<String> branchadapter = new ArrayAdapter<>(Activity_IntroductoryQuestion.this, android.R.layout.simple_spinner_dropdown_item, laBranchNms.toArray(new String[0]));
            txtBranchNm.setAdapter(branchadapter);

            initModel();
        });

        mViewModel.GetAllMcBrand().observe(Activity_IntroductoryQuestion.this, loList -> {
            laBrand = loList;

            ArrayList<String> laBrandNms = new ArrayList<>();
            for(int x = 0; x < laBrand.size(); x++){
                laBrandNms.add(laBrand.get(x).getBrandNme());
            }
            ArrayAdapter<String> brandadapter = new ArrayAdapter<>(Activity_IntroductoryQuestion.this, android.R.layout.simple_spinner_dropdown_item, laBrandNms.toArray(new String[0]));
            txtBrandNm.setAdapter(brandadapter);
        });

        mViewModel.GetBrandID().observe(Activity_IntroductoryQuestion.this, brandID -> {
            try {

                mViewModel.GetAllBrandModelInfo(brandID).observe(Activity_IntroductoryQuestion.this, loList -> {
                    laModel = loList;

                    ArrayList<String> laModelNms = new ArrayList<>();
                    for(int x = 0; x < laModel.size(); x++){
                        laModelNms.add(laModel.get(x).getModelNme() +" "+ laModel.get(x).getModelCde());
                    }

                    ArrayAdapter<String> modeladapter = new ArrayAdapter<>(Activity_IntroductoryQuestion.this, android.R.layout.simple_spinner_dropdown_item, laModelNms.toArray(new String[0]));
                    txtModelNm.setAdapter(modeladapter);
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

                        //get installment details of model
                        mViewModel.GetMinimumDownpayment(mViewModel.GetProductModel().getModelIDx(), new VMARContact.OnRetrieveInstallmentInfo() {
                            @Override
                            public void OnRetrieve(InstallmentInfo loResult) {

                                if (loResult == null){
                                    Toast.makeText(Activity_IntroductoryQuestion.this, "Installment info not found!", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                loInstallment = loResult;

                                initModel();
                            }

                            @Override
                            public void OnFailed(String message) {
                                Toast.makeText(Activity_IntroductoryQuestion.this, message, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });

            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private void initModel(){

        //set loan type
        mViewModel.getModel().setAppTypex(String.valueOf(lnUnitSelect));

        //set payment type
        mViewModel.GetProductModel().setPaymForm(String.valueOf(lnUnitSelect));

        //set customer type
        mViewModel.getModel().setCustTypex(String.valueOf(lnCustSelect));

        //set target date
        mViewModel.getModel().setTargetDte(txtDTarget.getText().toString());

        //set remarks
        if (txt_remarks.getText() == null || txt_remarks.getText().toString().isEmpty()){
            mViewModel.getModel().setsRemarks("");
        }else {
            mViewModel.getModel().setsRemarks(txt_remarks.getText().toString());
        }

        //set installment term
        mViewModel.getModel().setAccTermxx(lnTermSelect);
        mViewModel.GetProductModel().setTermIDxx(String.valueOf(mViewModel.getModel().getAccTermxx()));

        //set branch code
        if(lsBranchSelect == null || lsBranchSelect.isEmpty()){
            mViewModel.getModel().setBranchCde(loEmployee.sBranchCd);
        }else {
            laBranch
                    .stream()
                    .filter(eBranchInfo -> eBranchInfo.getBranchNm().equalsIgnoreCase(lsBranchSelect))
                    .findFirst()
                    .ifPresent(eBranchInfo ->   mViewModel.getModel().setBranchCde(eBranchInfo.getBranchCd()));
        }

        //set brand id
        if(lsBrandSelect == null || lsBrandSelect.isEmpty()){
            initDisplay();
            return;
        }
        laBrand
                .stream()
                .filter(eMcBrand -> eMcBrand.getBrandNme().equalsIgnoreCase(lsBrandSelect))
                .findFirst()
                .ifPresent(eMcBrand -> {
                    mViewModel.setBrandID(eMcBrand.getBrandIDx());
                    mViewModel.getModel().setBrandIDxx(eMcBrand.getBrandIDx());
                });

        //set model id
        if (lsModelSelect == null || lsModelSelect.isEmpty()){
            initDisplay();
            return;
        }
        laModel
                .stream()
                .filter(eMcModel -> (eMcModel.getModelNme() + " " + eMcModel.getModelCde()).equalsIgnoreCase(lsModelSelect))
                .findFirst()
                .ifPresent(eMcModel -> {

                    //this validation is to avoid repetitive call of this method initModel() from livedata observation, when selecting model
                    if (!eMcModel.getModelIDx().equalsIgnoreCase(mViewModel.getModel().getModelIDxx())){
                        mViewModel.setModelID(eMcModel.getModelIDx());
                    }
                });

        //initialize downpayment
        if (loInstallment == null){

            //set to zero if installment is null
            mViewModel.GetProductModel().setDownPaym("0.00");
            mViewModel.getModel().setDownPaymt(0.00);
        }else{

            //set to minimum downpayment if downpayment is empty or zero, else, set to user input
            if (mViewModel.GetProductModel().getDownPaym() == null || mViewModel.GetProductModel().getDownPaym().isEmpty() || mViewModel.getModel().getDownPaymt() <= 0.00){

                mViewModel.GetProductModel().setDownPaym(String.valueOf(loInstallment.getMinimumDownpayment()));
                mViewModel.getModel().setDownPaymt(loInstallment.getMinimumDownpayment());
            }else {

                //validate installment detail, minimum dp vs current dp, set minimum if invalid
                if (!validateInstallment(mViewModel.getModel().getDownPaymt())){
                    mViewModel.GetProductModel().setDownPaym(String.valueOf(loInstallment.getMinimumDownpayment()));
                    mViewModel.getModel().setDownPaymt(loInstallment.getMinimumDownpayment());
                }else {
                    mViewModel.GetProductModel().setDownPaym(txtDownPymnt.getText().toString());
                    mViewModel.getModel().setDownPaymt(Double.parseDouble(txtDownPymnt.getText().toString()));
                }
            }
        }

        //initialize monthly
        double ldbl_monthly = mViewModel.GetMonthlyAmortization(Integer.parseInt(mViewModel.GetProductModel().getTermIDxx()));

        mViewModel.CalculateNewDownpayment(mViewModel.GetProductModel().getModelIDx(), Integer.parseInt(mViewModel.GetProductModel().getTermIDxx()),
                Double.parseDouble(mViewModel.GetProductModel().getDownPaym()), new VMARContact.OnCalculateNewDownpayment() {
                    @Override
                    public void OnCalculate(double lnResult) {

                        //set new monthly computation
                        mViewModel.getModel().setMonthlyAm(lnResult);
                        mViewModel.GetProductModel().setMonthAmr(String.valueOf(lnResult));

                        initDisplay();
                    }

                    @Override
                    public void OnFailed(String message) {

                        //set monthly
                        mViewModel.getModel().setMonthlyAm(ldbl_monthly);
                        mViewModel.GetProductModel().setMonthAmr(String.valueOf(ldbl_monthly));

                        if (message == null || message.isEmpty()){
                            Toast.makeText(Activity_IntroductoryQuestion.this, "Failed to calculate downpayment", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(Activity_IntroductoryQuestion.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initDisplay(){

        //display default text values
        lblBranchNm.setText(loEmployee.sBranchNm);
        lblBrandAdd.setText(loEmployee.sAddressx);

        laBranch
                .stream()
                .filter(eBranchInfo -> eBranchInfo.getBranchCd().equals(mViewModel.getModel().getBranchCde()))
                .findFirst()
                .ifPresent(eBranchInfo -> txtBranchNm.setText(eBranchInfo.getBranchNm()));

        laBrand
                .stream()
                .filter(eMcBrand -> eMcBrand.getBrandIDx().equals(mViewModel.getModel().getBrandIDxx()))
                .findFirst()
                .ifPresent(eMcBrand -> txtBrandNm.setText(eMcBrand.getBrandNme()));

        laModel
                .stream()
                .filter(eMcModel -> eMcModel.getModelIDx().equals(mViewModel.getModel().getModelIDxx()))
                .findFirst()
                .ifPresent(eMcModel -> txtModelNm.setText(eMcModel.getModelNme()));

        spnApplType.setText(CreditAppConstants.APPLICATION_TYPE[lnUnitSelect], false);
        spnCustType.setText(CreditAppConstants.CUSTOMER_TYPE[lnCustSelect], false);
        spnAcctTerm.setText(CreditAppConstants.INSTALLMENT_TERM[lnTermSelect], false);

        txtDTarget.setText(new AppConstants().CURRENT_DATE_WORD);
        txtDownPymnt.setText(String.valueOf(mViewModel.getModel().getDownPaymt()));
        txtAmort.setText(String.valueOf(mViewModel.getModel().getMonthlyAm()));
    }

    private void initListener(){

        txtDownPymnt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (!b){

                    if (txtDownPymnt.getText() == null || txtDownPymnt.getText().toString().trim().isEmpty()){

                        InitMessage(0, R.drawable.baseline_error_24, "Please enter downpayment", "Okay", "", new OnMessageButton() {
                            @Override
                            public void OnPositive() {}

                            @Override
                            public void OnNegative() {}
                        });
                        return;
                    }

                    if (validateInstallment(Double.parseDouble(txtDownPymnt.getText().toString()))) initModel();
                }
            }
        });

        txtDTarget.setOnClickListener(v -> {

            final Calendar newCalendar = Calendar.getInstance();

            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
            final DatePickerDialog  StartTime = new DatePickerDialog(Activity_IntroductoryQuestion.this, (view131, year, monthOfYear, dayOfMonth) -> {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String lsDate = dateFormatter.format(newDate.getTime());

                txtDTarget.setText(lsDate);
                //mViewModel.getModel().setTargetDte(lsDate);

            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

            StartTime.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            StartTime.show();
        });

        btnCreate.setOnClickListener(view -> {

            if (validateInstallment(Double.parseDouble(txtDownPymnt.getText().toString()))){

                initModel();

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

        spnApplType.setOnItemClickListener(new OnItemClickListener(spnApplType));
        spnCustType.setOnItemClickListener(new OnItemClickListener(spnCustType));
        txtBranchNm.setOnItemClickListener(new OnItemClickListener(txtBranchNm));
        txtBrandNm.setOnItemClickListener(new OnItemClickListener(txtBrandNm));
        txtModelNm.setOnItemClickListener(new OnItemClickListener(txtModelNm));
        spnAcctTerm.setOnItemClickListener(new OnItemClickListener(spnAcctTerm));

    }

    private Boolean validateInstallment(double fdblDownPaym){

        //validate cash price
        if (loCashPrice == null){
            Toast.makeText(Activity_IntroductoryQuestion.this, "Could not find cash info", Toast.LENGTH_SHORT).show();
            return false;
        }

        //validate mc payment info
        if (loInstallment == null){
            Toast.makeText(Activity_IntroductoryQuestion.this, "Could not find installment info", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fdblDownPaym < loInstallment.getMinimumDownpayment()){

            InitMessage(0, R.drawable.baseline_error_24, "Downpayment does not meet the required amount", "Okay", "", new OnMessageButton() {
                @Override
                public void OnPositive() { initDisplay(); }

                @Override
                public void OnNegative() {}
            });

            return false;
        } else if (fdblDownPaym >= loCashPrice.CashPrce){

            InitMessage(0, R.drawable.baseline_error_24, "Downpayment cannot exceed or be equal to the cash price", "Okay", "", new OnMessageButton() {
                @Override
                public void OnPositive() { initDisplay(); }

                @Override
                public void OnNegative() {}
            });
            return false;
        }
        return true;
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
                lnUnitSelect = i;
            } else if(loView == spnCustType){
                lnCustSelect = i;
            } else if (loView == txtBranchNm){
                lsBranchSelect = txtBranchNm.getAdapter().getItem(i).toString();
            } else if (loView == txtBrandNm) {
                lsBrandSelect = txtBrandNm.getAdapter().getItem(i).toString();
            } else if (loView == txtModelNm) {
                lsModelSelect = txtModelNm.getAdapter().getItem(i).toString();
            } else if(loView == spnAcctTerm) {
                lnTermSelect = i;
            }
            initModel();
        }
    }
}