package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import  com.google.android.material.checkbox.MaterialCheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DTownInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EOccupationInfo;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.SpouseEmployments;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppConstants;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.OnParseListener;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMSpouseEmployment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity_SpouseEmploymentInfo extends AppCompatActivity {


    private VMSpouseEmployment mViewModel;
    private MessageBox poMessage;

    private MaterialAutoCompleteTextView spnCmpLvl, spnEmpLvl, spnBusNtr, spnEmpSts, spnServce;
    private MaterialAutoCompleteTextView txtCntryx, txtProvNm, txtTownNm, txtJobNme;
    private TextInputLayout tilCntryx, tilCompNm, tilJobTitle, tilCmpLvl, tilBizNature, tilEmpLvl;
    private TextInputEditText txtCompNm, txtCompAd, txtSpcfJb, txtLngthS, txtEsSlry, txtCompCn;
    private LinearLayout lnGovInfo, lnEmpInfo;
    private MaterialButton btnNext, btnPrvs;
    private RadioButton rbPrivate, rbGovernment, rbOFW;

    private MaterialCheckBox cbUniformYes, cbMilitaryYes;
    private MaterialTextView lblBizNature;
    private RadioGroup rgSectorx;
    private MaterialToolbar toolbar;

    private String TransNox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(Activity_SpouseEmploymentInfo.this).get(VMSpouseEmployment.class);
        poMessage = new MessageBox(Activity_SpouseEmploymentInfo.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spouse_employment_info);
        initWidgets();
        mViewModel.InitializeApplication(getIntent());
        mViewModel.GetApplication().observe(Activity_SpouseEmploymentInfo.this, app -> {
            try {
                TransNox = app.getTransNox();
                mViewModel.getModel().setTransNox(app.getTransNox());
                mViewModel.ParseData(app, new OnParseListener() {
                    @Override
                    public void OnParse(Object args) {
                        SpouseEmployments loDetail = (SpouseEmployments) args;
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

        cbMilitaryYes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                mViewModel.getModel().setMilitaryPersonal("1");
                return;
            }

            mViewModel.getModel().setMilitaryPersonal("0");
        });

        cbUniformYes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                mViewModel.getModel().setUniformPersonal("1");
                return;
            }

            mViewModel.getModel().setUniformPersonal("0");
        });

        spnCmpLvl.setAdapter(new ArrayAdapter<>(Activity_SpouseEmploymentInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.COMPANY_LEVEL));
        spnCmpLvl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.getModel().setCompanyLevel(String.valueOf(position));
            }
        });

        spnEmpLvl.setAdapter(new ArrayAdapter<>(Activity_SpouseEmploymentInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.EMPLOYEE_LEVEL));
        spnEmpLvl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.getModel().setEmployeeLevel(String.valueOf(position));
            }
        });

        spnBusNtr.setAdapter(new ArrayAdapter<>(Activity_SpouseEmploymentInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.BUSINESS_NATURE));
        spnBusNtr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.getModel().setBusinessNature(String.valueOf(position));
            }
        });

        spnEmpSts.setAdapter(new ArrayAdapter<>(Activity_SpouseEmploymentInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.EMPLOYMENT_STATUS));
        spnEmpSts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        mViewModel.getModel().setEmployeeStatus("R");
                        break;
                    case 1:
                        mViewModel.getModel().setEmployeeStatus("P");
                        break;
                    case 2:
                        mViewModel.getModel().setEmployeeStatus("C");
                        break;
                    default:
                        mViewModel.getModel().setEmployeeStatus("S");
                        break;
                }
            }
        });

        spnServce.setAdapter(new ArrayAdapter<>(Activity_SpouseEmploymentInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.LENGTH_OF_STAY));
        spnServce.setOnItemClickListener((parent, view, position, id) ->
                mViewModel.getModel().setIsYear(String.valueOf(position)));


        mViewModel.GetTownProvinceList().observe(Activity_SpouseEmploymentInfo.this, new Observer<List<DTownInfo.TownProvinceInfo>>() {
            @Override
            public void onChanged(List<DTownInfo.TownProvinceInfo> loList) {
                try {
                    ArrayList<String> strings = new ArrayList<>();
                    for (int x = 0; x < loList.size(); x++) {
                        String lsProv = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                        strings.add(lsProv);

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_SpouseEmploymentInfo.this, android.R.layout.simple_spinner_dropdown_item, strings.toArray(new String[0]));
                    txtTownNm.setAdapter(adapter);
                    txtTownNm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            for (int x = 0; x < loList.size(); x++) {
                                String lsLabel = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                                String lsSlctd = txtTownNm.getText().toString().trim();
                                if (lsSlctd.equalsIgnoreCase(lsLabel)) {
                                    mViewModel.getModel().setTownID(loList.get(x).sTownIDxx);
                                    mViewModel.getModel().setTownName(lsLabel);
                                    break;
                                }
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mViewModel.GetOccupations().observe(Activity_SpouseEmploymentInfo.this, new Observer<List<EOccupationInfo>>() {
            @Override
            public void onChanged(List<EOccupationInfo> loList) {
                try{
                    ArrayList<String> string = new ArrayList<>();
                    for (int x = 0; x < loList.size(); x++) {
                        string.add(loList.get(x).getOccptnNm());
                    }

                    ArrayAdapter<String> adapters = new ArrayAdapter<>(Activity_SpouseEmploymentInfo.this, android.R.layout.simple_spinner_dropdown_item, string.toArray(new String[0]));
                    txtJobNme.setAdapter(adapters);

                    txtJobNme.setOnItemClickListener((parent, view, position, id) -> {
                        for (int x = 0; x < loList.size(); x++) {
                            String lsLabel = loList.get(x).getOccptnNm().trim();
                            String lsSlctd = txtJobNme.getText().toString().trim();
                            if (lsSlctd.equalsIgnoreCase(lsLabel)) {
                                mViewModel.getModel().setJobTitle(loList.get(x).getOccptnID());
                                mViewModel.getModel().setsJobName(lsLabel);
                                break;
                            }
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        btnNext.setOnClickListener(v -> SaveSpouseEmploymentInfo());
        btnPrvs.setOnClickListener(v -> {
            returnPrevious();
        });

    }

    private void SaveSpouseEmploymentInfo() {

        mViewModel.getModel().setCompanyName(Objects.requireNonNull(txtCompNm.getText()).toString());
        mViewModel.getModel().setsCountryN(Objects.requireNonNull(txtCntryx.getText()).toString());
        mViewModel.getModel().setCompanyAddress(Objects.requireNonNull(txtCompAd.getText()).toString());
        //mViewModel.getModel().setJobTitle(Objects.requireNonNull(mViewModel.getModel().getJobTitle()));
        //mViewModel.getModel().setSpecificJob(Objects.requireNonNull(txtSpcfJb.getText()).toString());

        if (txtLngthS.getText().toString().isEmpty()) {
            mViewModel.getModel().setLengthOfService(0);
        } else {
            mViewModel.getModel().setLengthOfService(Double.parseDouble(Objects.requireNonNull(txtLngthS.getText()).toString()));
        }

        if (txtEsSlry.getText().toString().isEmpty()) {
            mViewModel.getModel().setMonthlyIncome(0);
        } else {
            mViewModel.getModel().setMonthlyIncome(Long.parseLong(Objects.requireNonNull(txtEsSlry.getText()).toString()));
        }

        mViewModel.getModel().setContact(Objects.requireNonNull(txtCompCn.getText()).toString());

        mViewModel.SaveData(new OnSaveInfoListener() {
            @Override
            public void OnSave(String args) {
                Intent loIntent = new Intent(Activity_SpouseEmploymentInfo.this, Activity_SpouseSelfEmploymentInfo.class);
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
        toolbar = findViewById(R.id.toolbar_SpouseEmploymentInfo);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        rgSectorx = findViewById(R.id.rg_sector);


        spnCmpLvl = findViewById(R.id.spn_employmentLevel);
        spnEmpLvl = findViewById(R.id.spn_employeeLevel);
        spnBusNtr = findViewById(R.id.spn_businessNature);
        spnEmpSts = findViewById(R.id.spn_employmentStatus);
        spnServce = findViewById(R.id.spn_lengthService);

        txtCntryx = findViewById(R.id.txt_countryNme);
//        txtProvNm = findViewById(R.id.txt_province);
        txtTownNm = findViewById(R.id.txt_town);
        txtJobNme = findViewById(R.id.txt_jobPosition);

        tilCntryx = findViewById(R.id.til_countryNme);
        tilCompNm = findViewById(R.id.til_companyNme);
        tilJobTitle = findViewById(R.id.til_job_title);
        tilCmpLvl = findViewById(R.id.til_cmpLevel);
        tilBizNature = findViewById(R.id.til_bizNature);
        tilEmpLvl = findViewById(R.id.til_empLvl);

        txtCompNm = findViewById(R.id.txt_companyNme);
        txtCompAd = findViewById(R.id.txt_companyAdd);
        txtSpcfJb = findViewById(R.id.txt_specificJob);
        txtLngthS = findViewById(R.id.txt_lenghtService);
        txtEsSlry = findViewById(R.id.txt_monthlySalary);
        txtCompCn = findViewById(R.id.txt_companyContact);

        rbPrivate = findViewById(R.id.rb_private);
        rbGovernment = findViewById(R.id.rb_government);
        rbOFW = findViewById(R.id.rb_ofw);

        cbUniformYes = findViewById(R.id.cb_UniformPersonnelYes);
        cbMilitaryYes = findViewById(R.id.cb_MilitaryPersonalYes);

        lnGovInfo = findViewById(R.id.linear_governmentSector);
        lnEmpInfo = findViewById(R.id.linear_employmentInfo);

        btnPrvs = findViewById(R.id.btn_creditAppPrvs);
        btnNext = findViewById(R.id.btn_creditAppNext);

        rgSectorx.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rbGovernment.isChecked()) {
                    mViewModel.getModel().setEmploymentSector("0");
                    lnGovInfo.setVisibility(View.VISIBLE);
                    tilCntryx.setVisibility(View.GONE);

                } else if (rbPrivate.isChecked()) {
                    mViewModel.getModel().setEmploymentSector("1");
                    lnGovInfo.setVisibility(View.GONE);
                    tilCntryx.setVisibility(View.GONE);

                } else if (rbOFW.isChecked()) {
                    mViewModel.getModel().setEmploymentSector("2");
                    lnGovInfo.setVisibility(View.GONE);
                    tilCntryx.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @SuppressLint("NewApi")
    public void setUpFieldsFromLocalDB(SpouseEmployments infoModel) throws JSONException {
        if (infoModel != null){

            if (infoModel.getEmploymentSector().equalsIgnoreCase("0")) {
                rgSectorx.check(R.id.rb_government);
                mViewModel.getModel().setEmploymentSector("0");
                lnGovInfo.setVisibility(View.VISIBLE);
                lnEmpInfo.setVisibility(View.VISIBLE);
                tilCntryx.setVisibility(View.GONE);
                tilCompNm.setHint("Government Level");
                spnBusNtr.setVisibility(View.GONE);
                tilBizNature.setVisibility(View.GONE);
            } else if (infoModel.getEmploymentSector().equalsIgnoreCase("1")) {
                rgSectorx.check(R.id.rb_private);
                lnGovInfo.setVisibility(View.GONE);
                lnEmpInfo.setVisibility(View.VISIBLE);
                tilCntryx.setVisibility(View.GONE);
                tilCompNm.setHint("Company Name");
                spnBusNtr.setVisibility(View.VISIBLE);
                tilBizNature.setVisibility(View.VISIBLE);
                mViewModel.getModel().setEmploymentSector("1");
            } else if (infoModel.getEmploymentSector().equalsIgnoreCase("2")) {
                rgSectorx.check(R.id.rb_ofw);
                mViewModel.getModel().setEmploymentSector("2");
                lnGovInfo.setVisibility(View.GONE);
                lnEmpInfo.setVisibility(View.GONE);
                tilCntryx.setVisibility(View.VISIBLE);
                spnBusNtr.setVisibility(View.GONE);
                tilBizNature.setVisibility(View.GONE);
            }
            if(!"".equalsIgnoreCase(infoModel.getCompanyLevel())) {
                spnCmpLvl.setText(CreditAppConstants.COMPANY_LEVEL[Integer.parseInt(infoModel.getCompanyLevel())], false);
//                spnCmpLvl.setSelection(Integer.parseInt(infoModel.getCompanyLevel()));
                mViewModel.getModel().setCompanyLevel(infoModel.getCompanyLevel());
            }

            if(!"".equalsIgnoreCase(infoModel.getEmployeeLevel())) {
                spnEmpLvl.setText(CreditAppConstants.EMPLOYEE_LEVEL[Integer.parseInt(infoModel.getEmployeeLevel())], false);
//                spnEmpLvl.setSelection(Integer.parseInt(infoModel.getEmployeeLevel()));
                mViewModel.getModel().setEmployeeLevel(infoModel.getEmployeeLevel());
            }

            if(!"".equalsIgnoreCase(infoModel.getBusinessNature())) {
                spnBusNtr.setText(CreditAppConstants.BUSINESS_NATURE[Integer.parseInt(infoModel.getBusinessNature())], false);
                mViewModel.getModel().setBusinessNature(infoModel.getBusinessNature());
            }

            if(!"".equalsIgnoreCase(infoModel.getTownID())) {
                txtTownNm.setText(infoModel.getsTownName());
                mViewModel.getModel().setTownID(infoModel.getTownID());
                mViewModel.getModel().setTownName(infoModel.getsTownName());
            }

            txtCompNm.setText(infoModel.getCompanyName());
            txtJobNme.setText(infoModel.getJobTitle());
            mViewModel.getModel().setJobTitle(infoModel.getJobTitle());
            txtCompAd.setText(infoModel.getCompanyAddress());
            txtSpcfJb.setText(infoModel.getSpecificJob());
            if(infoModel.getEmployeeStatus().equalsIgnoreCase("0")){
                spnEmpSts.setText(CreditAppConstants.EMPLOYMENT_STATUS[0], false);
//                spnEmpSts.setSelection(0);
                mViewModel.getModel().setEmployeeStatus("0");
            }else if (infoModel.getEmployeeStatus().equalsIgnoreCase("1")){
                spnEmpSts.setText(CreditAppConstants.EMPLOYMENT_STATUS[1], false);
//                spnEmpSts.setSelection(1);
                mViewModel.getModel().setEmployeeStatus("1");
            }else if (infoModel.getEmployeeStatus().equalsIgnoreCase("2")){
                spnEmpSts.setText(CreditAppConstants.EMPLOYMENT_STATUS[2], false);
//                spnEmpSts.setSelection(2);
                mViewModel.getModel().setEmployeeStatus("2");
            }else if (infoModel.getEmployeeStatus().equalsIgnoreCase("3")){
                spnEmpSts.setText(CreditAppConstants.EMPLOYMENT_STATUS[3], false);
//                spnEmpSts.setSelection(3);
                mViewModel.getModel().setEmployeeStatus("3");
            }

            int nlength = (int)(infoModel.getLengthOfService() * 12);
            if (nlength < 12){
                txtLngthS.setText(String.valueOf(nlength));
                spnServce.setText(CreditAppConstants.LENGTH_OF_STAY[0], false);
                mViewModel.getModel().setIsYear(String.valueOf(0));
                mViewModel.getModel().setLengthOfService(nlength);
            }else{
                txtLngthS.setText(String.valueOf(infoModel.getLengthOfService()));
                spnServce.setText(CreditAppConstants.LENGTH_OF_STAY[1], false);
                mViewModel.getModel().setIsYear(String.valueOf(1));
                mViewModel.getModel().setLengthOfService(infoModel.getLengthOfService());
            }
            txtEsSlry.setText( !"".equalsIgnoreCase(String.valueOf(infoModel.getMonthlyIncome())) ? String.valueOf(infoModel.getMonthlyIncome()) : "");
            txtCompCn.setText( !"".equalsIgnoreCase(infoModel.getContact()) ? infoModel.getContact() : "");

//            infoModel.setIsYear(String.valueOf(i));
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
        Intent loIntent = new Intent(Activity_SpouseEmploymentInfo.this, Activity_SpouseResidenceInfo.class);
        loIntent.putExtra("sTransNox", TransNox);
        startActivity(loIntent);
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
        finish();
    }
}