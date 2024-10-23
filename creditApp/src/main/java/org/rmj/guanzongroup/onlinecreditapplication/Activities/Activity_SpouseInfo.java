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
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import  com.google.android.material.checkbox.MaterialCheckBox;


import org.json.JSONException;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DTownInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECountryInfo;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.ClientSpouseInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.MobileNo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppConstants;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMSpouseInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Activity_SpouseInfo extends AppCompatActivity {
    private static final String TAG = Activity_SpouseInfo.class.getSimpleName();

    private VMSpouseInfo mViewModel;
    private MobileNo[] poMobile = new MobileNo[3];
    private MessageBox poMessage;

    private MaterialAutoCompleteTextView
            txtProvince, txtTownxx, txtCitizenx;
    private TextInputEditText
            txtLastName, txtFirstName, txtSuffix, txtMiddName, txtNickName,
            txtBDate, txtPrimeCntc, txtPrimeCntcYr, txtSecCntct, txtSecCntctYr, txtThirCntct,
            txtThirCntctYr, txtTelNox, txtEmailAdd, txtFbAcct, txtViberAcct, txtMobileYr1,
            txtMobileYr2, txtMobileYr3;

    private String transnox;

    private TextInputLayout tilMobileYr1, tilMobileYr2, tilMobileYr3;
    private MaterialCheckBox cbMobile1, cbMobile2, cbMobile3;


    private MaterialButton btnNext, btnPrvs;
    private MaterialToolbar toolbar;

    private String TransNox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_SpouseInfo.this).get(VMSpouseInfo.class);
        poMessage = new MessageBox(Activity_SpouseInfo.this);
        poMobile[0] = new MobileNo();
        poMobile[1] = new MobileNo();
        poMobile[2] = new MobileNo();
        setContentView(R.layout.activity_spouse_info);
        initWidgets();
        mViewModel.InitializeApplication(getIntent());
        mViewModel.GetApplication().observe(Activity_SpouseInfo.this, app -> {
            try {
                TransNox = app.getTransNox();
                mViewModel.getModel().setTransNox(app.getTransNox());
                mViewModel.ParseData(app, args -> {
                    ClientSpouseInfo loDetail = (ClientSpouseInfo) args;
                    try {
                        setUpFieldsFromLocalDB(loDetail);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mViewModel.GetTownProvinceList().observe(Activity_SpouseInfo.this, new Observer<List<DTownInfo.TownProvinceInfo>>() {
            @Override
            public void onChanged(List<DTownInfo.TownProvinceInfo> loList) {
                try {
                    ArrayList<String> strings = new ArrayList<>();
                    for (int x = 0; x < loList.size(); x++) {
                        String lsTown = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                        strings.add(lsTown);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_SpouseInfo.this, android.R.layout.simple_spinner_dropdown_item, strings.toArray(new String[0]));
                    txtTownxx.setAdapter(adapter);

                    txtTownxx.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            for (int x = 0; x < loList.size(); x++) {
                                String lsLabel = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                                String lsSlctd = txtTownxx.getText().toString().trim();
                                if (lsSlctd.equalsIgnoreCase(lsLabel)) {
                                    mViewModel.getModel().setBrthPlce(loList.get(x).sTownIDxx);
                                    mViewModel.getModel().setBirthPlc(lsLabel);
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

        ArrayAdapter<String> loAdapter = new ArrayAdapter<>(Activity_SpouseInfo.this, android.R.layout.simple_spinner_dropdown_item, CreditAppConstants.CIVIL_STATUS);
        txtCitizenx.setAdapter(loAdapter);

        //TODO: Replace the spinner(dropdown list) for selection of mobile number type into check box with label 'PostPaid'
        // The default value for mobile no type will be prepaid if not check.
//            pnMobile[0] = position;
        cbMobile1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbMobile1.isChecked()) {
                    poMobile[0].setIsPostPd("1");
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.35f
                    );
                    cbMobile1.setLayoutParams(param);
                    tilMobileYr1.setVisibility(View.VISIBLE);
                } else {
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            2.0f
                    );
                    cbMobile1.setLayoutParams(param);
                    poMobile[0].setIsPostPd("0");
                    tilMobileYr1.setVisibility(View.GONE);
                }
            }
        });

//        txtMobileType[1].setOnItemClickListener((parent, view, position, id) -> {
//            //TODO: Replace the spinner(dropdown list) for selection of mobile number type into check box with label 'PostPaid'
//            // The default value for mobile no type will be prepaid if not check.
////            pnMobile[1] = position;
        cbMobile2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbMobile2.isChecked()) {
                    poMobile[1].setIsPostPd("1");
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.35f
                    );
                    cbMobile2.setLayoutParams(param);
                    tilMobileYr2.setVisibility(View.VISIBLE);
                } else {
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            2.0f
                    );
                    cbMobile2.setLayoutParams(param);
                    poMobile[1].setIsPostPd("0");
                    tilMobileYr2.setVisibility(View.GONE);
                }
            }
        });
//        txtMobileType[2].setOnItemClickListener((parent, view, position, id) -> {
//            //TODO: Replace the spinner(dropdown list) for selection of mobile number type into check box with label 'PostPaid'
//            // The default value for mobile no type will be prepaid if not check.
////            pnMobile[2] = position;
        cbMobile3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbMobile3.isChecked()) {
                    poMobile[2].setIsPostPd("1");
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.35f
                    );
                    cbMobile3.setLayoutParams(param);
                    tilMobileYr3.setVisibility(View.VISIBLE);
                } else {
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            2.0f
                    );
                    cbMobile3.setLayoutParams(param);
                    poMobile[2].setIsPostPd("0");
                    tilMobileYr3.setVisibility(View.GONE);
                }
            }
        });

        mViewModel.GetCountryList().observe(Activity_SpouseInfo.this, new Observer<List<ECountryInfo>>() {
            @Override
            public void onChanged(List<ECountryInfo> loList) {
                try {
                    ArrayList<String> strings = new ArrayList<>();
                    for (int x = 0; x < loList.size(); x++) {
                        strings.add(loList.get(x).getNational());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_SpouseInfo.this, android.R.layout.simple_spinner_dropdown_item, strings.toArray(new String[0]));
                    txtCitizenx.setAdapter(adapter);

                    txtCitizenx.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            for (int x = 0; x < loList.size(); x++) {
                                String lsLabel = loList.get(x).getNational();
                                String lsSlctd = txtCitizenx.getText().toString().trim();
                                if (lsSlctd.equalsIgnoreCase(lsLabel)) {
                                    mViewModel.getModel().setCitizenx(loList.get(x).getCntryCde());
                                    mViewModel.getModel().setCtznShip(loList.get(x).getNational());
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

        btnNext.setOnClickListener(v -> SaveSpouseInfo());
        btnPrvs.setOnClickListener(v -> {
            returnPrevious();
        });
    }

    private void SaveSpouseInfo() {
        mViewModel.getModel().setLastName(Objects.requireNonNull(txtLastName.getText()).toString().trim());
        mViewModel.getModel().setFrstName(Objects.requireNonNull(txtFirstName.getText()).toString().trim());
        mViewModel.getModel().setMiddName(Objects.requireNonNull(txtMiddName.getText()).toString().trim());
        mViewModel.getModel().setSuffix(Objects.requireNonNull(txtSuffix.getText()).toString().trim());
        mViewModel.getModel().setNickName(Objects.requireNonNull(txtNickName.getText()).toString().trim());

        if (txtPrimeCntc != null || !Objects.requireNonNull(txtPrimeCntc.getText()).toString().trim().isEmpty()) {
            poMobile[0].setMobileNo(txtPrimeCntc.getText().toString());
//            loMobile[1].setIsPostPd();
            if (poMobile[0].getIsPostPd().equalsIgnoreCase("1")) {
                if (txtPrimeCntcYr.getText().toString().isEmpty()) {
                    poMobile[0].setPostYear(0);
                } else {
                    poMobile[0].setPostYear(Integer.parseInt(txtPrimeCntcYr.getText().toString()));
                }

            }
            mViewModel.getModel().setMobileNo1(poMobile[0]);
        }
        if (!Objects.requireNonNull(txtSecCntct.getText()).toString().trim().isEmpty()) {
            poMobile[1].setMobileNo(txtSecCntct.getText().toString());
//            loMobile[1].setIsPostPd();
            if (poMobile[1].getIsPostPd().equalsIgnoreCase("1")) {
                if (txtSecCntctYr.getText().toString().isEmpty()) {
                    poMobile[1].setPostYear(0);
                } else {
                    poMobile[1].setPostYear(Integer.parseInt(txtSecCntctYr.getText().toString()));
                }
            }
            mViewModel.getModel().setMobileNo2(poMobile[1]);
        }
        if (!Objects.requireNonNull(txtThirCntct.getText()).toString().trim().isEmpty()) {
            poMobile[2].setMobileNo(txtThirCntct.getText().toString());
//            poMobile[2].setIsPostPd();
            if (poMobile[2].getIsPostPd().equalsIgnoreCase("1")) {
                if (txtThirCntctYr.getText().toString().isEmpty()) {
                    poMobile[2].setPostYear(0);
                } else {
                    poMobile[2].setPostYear(Integer.parseInt(txtThirCntctYr.getText().toString()));
                }
            }
            mViewModel.getModel().setMobileNo3(poMobile[2]);
        }

        mViewModel.getModel().setPhoneNox(Objects.requireNonNull(txtTelNox.getText()).toString());
        mViewModel.getModel().setEmailAdd(Objects.requireNonNull(txtEmailAdd.getText()).toString());
        mViewModel.getModel().setFbAccntx(Objects.requireNonNull(txtFbAcct.getText()).toString());
        mViewModel.getModel().setVbrAccnt(Objects.requireNonNull(txtViberAcct.getText()).toString());

        mViewModel.SaveData(new OnSaveInfoListener() {
            @Override
            public void OnSave(String args) {
                Intent loIntent = new Intent(Activity_SpouseInfo.this, Activity_SpouseResidenceInfo.class);
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
                poMessage.setPositiveButton("Dismiss", (view1, dialog) -> dialog.dismiss());
                poMessage.show();
            }
        });

    }


    private void initWidgets() {

        toolbar = findViewById(R.id.toolbar_SpouseInfo);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        txtProvince = findViewById(R.id.txt_bpProvince);
        txtTownxx = findViewById(R.id.txt_bpTown);

        txtLastName = findViewById(R.id.tie_last_name);
        txtFirstName = findViewById(R.id.tie_first_name);
        txtSuffix = findViewById(R.id.tie_suffix);
        txtMiddName = findViewById(R.id.tie_midd_name);
        txtNickName = findViewById(R.id.tie_nick_name);
        txtBDate = findViewById(R.id.tie_birth_date);
        txtCitizenx = findViewById(R.id.tie_citizenship);

        txtPrimeCntc = findViewById(R.id.txt_mobileNo1);
        txtPrimeCntcYr = findViewById(R.id.txt_mobileNo1Year);

        txtSecCntct = findViewById(R.id.txt_mobileNo2);
        txtSecCntctYr = findViewById(R.id.txt_mobileNo2Year);

        txtThirCntct = findViewById(R.id.txt_mobileNo3);
        txtThirCntctYr = findViewById(R.id.txt_mobileNo3Year);

        txtTelNox = findViewById(R.id.tie_tel_no);
        txtEmailAdd = findViewById(R.id.tie_emailAdd);
        txtFbAcct = findViewById(R.id.tie_fbAcct);
        txtViberAcct = findViewById(R.id.tie_viberAcct);

        cbMobile1 = findViewById(R.id.cb_mobile1Type);
        cbMobile2 = findViewById(R.id.cb_mobile2Type);
        cbMobile3 = findViewById(R.id.cb_mobile3Type);

        txtMobileYr1 = txtPrimeCntcYr;
        tilMobileYr1 = findViewById(R.id.til_mobileNo1Year);

        txtMobileYr2 = txtSecCntctYr;
        tilMobileYr2 = findViewById(R.id.til_mobileNo2Year);

        txtMobileYr3 = txtThirCntctYr;
        tilMobileYr3 = findViewById(R.id.til_mobileNo3Year);

        txtBDate.setOnClickListener(v -> {
            final Calendar newCalendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
            final DatePickerDialog StartTime = new DatePickerDialog(Activity_SpouseInfo.this,
                    android.R.style.Theme_Holo_Dialog, (view131, year, monthOfYear, dayOfMonth) -> {
                try {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    String lsDate = dateFormatter.format(newDate.getTime());
                    txtBDate.setText(lsDate);
                    Date loDate = new SimpleDateFormat("MMMM dd, yyyy").parse(lsDate);
                    lsDate = new SimpleDateFormat("yyyy-MM-dd").format(loDate);
                    Log.d(TAG, "Save formatted time: " + lsDate);
                    mViewModel.getModel().setBrthDate(lsDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            StartTime.getDatePicker().setMaxDate(new Date().getTime());
            StartTime.show();
        });

        btnNext = findViewById(R.id.btn_creditAppNext);
        btnPrvs = findViewById(R.id.btn_creditAppPrvs);
    }


    @SuppressLint("NewApi")
    public void setUpFieldsFromLocalDB(ClientSpouseInfo infoModel) throws JSONException {
        if(infoModel != null) {
            txtLastName.setText(infoModel.getLastName());
            txtFirstName.setText(infoModel.getFrstName());
            txtMiddName.setText(infoModel.getMiddName());
            txtSuffix.setText(infoModel.getSuffix());
            txtNickName.setText(infoModel.getNickName());
            txtBDate.setText(infoModel.getBirthDte());
            if(!"".equalsIgnoreCase(infoModel.getBrthPlce())) {
                txtTownxx.setText(infoModel.getBrthPlce());
                mViewModel.getModel().setBirthPlc(infoModel.getBirthPlc());
                mViewModel.getModel().setBrthPlce(infoModel.getBrthPlce());
            }
            if(!"".equalsIgnoreCase(infoModel.getCitizenx())) {
                txtCitizenx.setText(infoModel.getCtznShip());
                mViewModel.getModel().setCitizenx(infoModel.getCitizenx());
                mViewModel.getModel().setCtznShip(infoModel.getCtznShip());
            }
            if(infoModel.getMobileNo1() != null){
                MobileNo info = infoModel.getMobileNo1();
                txtPrimeCntc.setText(info.getMobileNo());
                poMobile[0].setMobileNo(info.getMobileNo());
                poMobile[0].setIsPostPd(info.getIsPostPd());
                poMobile[0].setPostYear(info.getPostYear());
                mViewModel.getModel().setMobileNo1(info);
                if(info.getIsPostPd().equalsIgnoreCase("0")){
                    txtPrimeCntcYr.setVisibility(View.GONE);
                    cbMobile1.setChecked(false);
                }else{
                    txtMobileYr1.setVisibility(View.VISIBLE);
                    cbMobile1.setChecked(true);
                }
            }
            if(infoModel.getMobileNo2() != null){
                MobileNo info = infoModel.getMobileNo2();
                txtSecCntct.setText(info.getMobileNo());
                poMobile[1].setMobileNo(info.getMobileNo());
                poMobile[1].setIsPostPd(info.getIsPostPd());
                poMobile[1].setPostYear(info.getPostYear());
                mViewModel.getModel().setMobileNo3(info);
                if(info.getIsPostPd().equalsIgnoreCase("0")){
                    txtSecCntctYr.setVisibility(View.GONE);
                    cbMobile2.setChecked(false);
                }else{
                    txtSecCntctYr.setVisibility(View.VISIBLE);
                    cbMobile2.setChecked(true);
                }
            }
            if(infoModel.getMobileNo3() != null){
                MobileNo info = infoModel.getMobileNo3();
                txtThirCntct.setText(info.getMobileNo());
                poMobile[2].setMobileNo(info.getMobileNo());
                poMobile[2].setIsPostPd(info.getIsPostPd());
                poMobile[2].setPostYear(info.getPostYear());
                mViewModel.getModel().setMobileNo3(info);
                if(info.getIsPostPd().equalsIgnoreCase("0")){
                    txtThirCntctYr.setVisibility(View.GONE);
                    cbMobile3.setChecked(false);
                }else{
                    txtThirCntctYr.setVisibility(View.VISIBLE);
                    cbMobile3.setChecked(true);
                }
            }

            txtEmailAdd.setText(infoModel.getEmailAdd());
            txtFbAcct.setText(infoModel.getFbAccntx());
            txtTelNox.setText(infoModel.getPhoneNox());
            txtViberAcct.setText(infoModel.getVbrAccnt());
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
        Intent loIntent = new Intent(Activity_SpouseInfo.this, Activity_PensionInfo.class);
        loIntent.putExtra("sTransNox", TransNox);
        startActivity(loIntent);
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
        finish();
    }
}