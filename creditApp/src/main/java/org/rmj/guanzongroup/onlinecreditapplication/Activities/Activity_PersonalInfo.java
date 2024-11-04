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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DTownInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECountryInfo;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppConstants;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.MobileNo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.Personal;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.OnParseListener;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMPersonalInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Activity_PersonalInfo extends AppCompatActivity {
    private static final String TAG = Activity_PersonalInfo.class.getSimpleName();

    private VMPersonalInfo mViewModel;
    private MobileNo[] poMobile = new MobileNo[3];

    private MessageBox poMessage;

    private TextInputEditText txtLastNm, txtFrstNm, txtMiddNm, txtSuffixx, txtNickNm, txtBirthDt,
            txtMothNm, txtTellNox, txtEmailAdd, txtFbAccount, txtViberAccount;

    private TextInputLayout tilMothNm;
    private MaterialAutoCompleteTextView txtTown, txtCitizen;
    private RadioGroup rgGender;
    private MaterialAutoCompleteTextView spnCivilStatus;
    private MaterialButton btnNext, btnPrev;
    private MaterialCheckBox txtMobileType1, txtMobileType2, txtMobileType3;

    private TextInputEditText[] txtMobileNo;
    private TextInputEditText[] txtMobileYear;
    private TextInputLayout[] tilMobileYear;
    private MaterialToolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_PersonalInfo.this).get(VMPersonalInfo.class);
        poMobile[0] = new MobileNo();
        poMessage = new MessageBox(Activity_PersonalInfo.this);
        setContentView(R.layout.activity_personal_info);
        initWidgets();
        mViewModel.InitializeApplication(getIntent());

        mViewModel.GetApplication().observe(Activity_PersonalInfo.this, app -> {
            try {
                mViewModel.getModel().setTransNox(app.getTransNox());
                mViewModel.ParseData(app, new OnParseListener() {
                    @Override
                    public void OnParse(Object args) {
                        Personal loDetail = (Personal) args;
                        try {
                            setUpFieldsFromLocalDB(loDetail);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
        });


        mViewModel.GetTownProvinceList().observe(Activity_PersonalInfo.this, new Observer<List<DTownInfo.TownProvinceInfo>>() {
            @Override
            public void onChanged(List<DTownInfo.TownProvinceInfo> loList) {
                try {
                    ArrayList<String> strings = new ArrayList<>();
                    for (int x = 0; x < loList.size(); x++) {
                        String lsTown = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                        strings.add(lsTown);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_PersonalInfo.this, android.R.layout.simple_spinner_dropdown_item, strings.toArray(new String[0]));
                    txtTown.setAdapter(adapter);
                    txtTown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            for (int x = 0; x < loList.size(); x++) {
                                String lsLabel = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                                String lsSlctd = txtTown.getText().toString().trim();
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

        mViewModel.GetCountryList().observe(Activity_PersonalInfo.this, new Observer<List<ECountryInfo>>() {
            @Override
            public void onChanged(List<ECountryInfo> loList) {
                try {
                    ArrayList<String> strings = new ArrayList<>();
                    for (int x = 0; x < loList.size(); x++) {
                        strings.add(loList.get(x).getNational());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_PersonalInfo.this, android.R.layout.simple_spinner_dropdown_item, strings.toArray(new String[0]));
                    txtCitizen.setAdapter(adapter);

                    txtCitizen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            for (int x = 0; x < loList.size(); x++) {
                                String lsLabel = loList.get(x).getNational();
                                String lsSlctd = txtCitizen.getText().toString().trim();
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

        ArrayAdapter<String> loAdapter = new ArrayAdapter<>(Activity_PersonalInfo.this,
                android.R.layout.simple_spinner_dropdown_item, CreditAppConstants.CIVIL_STATUS);
        spnCivilStatus.setAdapter(loAdapter);
        spnCivilStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.getModel().setCvlStats(String.valueOf(position));
                SetupMaidenEntry();
            }
        });


        //TODO: Replace the spinner(dropdown list) for selection of mobile number type into check box with label 'PostPaid'
        // The default value for mobile no type will be prepaid if not check.
//            pnMobile[0] = position;
        txtMobileType1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (txtMobileType1.isChecked()) {
                    poMobile[0].setIsPostPd("1");
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.35f
                    );
                    txtMobileType1.setLayoutParams(param);
                    tilMobileYear[0].setVisibility(View.VISIBLE);
                } else {
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            2.0f
                    );
                    txtMobileType1.setLayoutParams(param);
                    poMobile[0].setIsPostPd("0");
                    tilMobileYear[0].setVisibility(View.GONE);
                }
            }
        });

//        });
//        txtMobileType[1].setOnItemClickListener((parent, view, position, id) -> {
//            //TODO: Replace the spinner(dropdown list) for selection of mobile number type into check box with label 'PostPaid'
//            // The default value for mobile no type will be prepaid if not check.
////            pnMobile[1] = position;
        txtMobileType2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (txtMobileType2.isChecked()) {
                    poMobile[1].setIsPostPd("1");
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.35f
                    );
                    txtMobileType2.setLayoutParams(param);
                    tilMobileYear[1].setVisibility(View.VISIBLE);
                } else {
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            2.0f
                    );
                    txtMobileType2.setLayoutParams(param);
                    poMobile[1].setIsPostPd("0");
                    tilMobileYear[1].setVisibility(View.GONE);
                }
            }
        });
//        txtMobileType[2].setOnItemClickListener((parent, view, position, id) -> {
//            //TODO: Replace the spinner(dropdown list) for selection of mobile number type into check box with label 'PostPaid'
//            // The default value for mobile no type will be prepaid if not check.
////            pnMobile[2] = position;
        txtMobileType3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (txtMobileType3.isChecked()) {
                    poMobile[2].setIsPostPd("1");
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.35f
                    );
                    txtMobileType3.setLayoutParams(param);
                    tilMobileYear[2].setVisibility(View.VISIBLE);
                } else {
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            2.0f
                    );
                    txtMobileType3.setLayoutParams(param);
                    poMobile[2].setIsPostPd("0");
                    tilMobileYear[2].setVisibility(View.GONE);
                }
            }
        });

        rgGender.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.rb_male) {
                mViewModel.getModel().setGender("0");
            }
            if (i == R.id.rb_female) {
                mViewModel.getModel().setGender("1");
            }
            if (i == R.id.rb_lgbt) {
                mViewModel.getModel().setGender("2");
            }
            SetupMaidenEntry();
        });


        btnNext.setOnClickListener(v -> SavePersonalInfo());
        btnPrev.setOnClickListener(v -> finish());
    }

    private void SavePersonalInfo() {
        try {
            mViewModel.getModel().setLastName(Objects.requireNonNull(txtLastNm.getText()).toString().trim());
            mViewModel.getModel().setFrstName(Objects.requireNonNull(txtFrstNm.getText()).toString().trim());
            mViewModel.getModel().setMiddName(Objects.requireNonNull(txtMiddNm.getText()).toString().trim());
            mViewModel.getModel().setSuffix(Objects.requireNonNull(txtSuffixx.getText()).toString().trim());
            mViewModel.getModel().setNickName(Objects.requireNonNull(txtNickNm.getText()).toString().trim());
            mViewModel.getModel().setMotherNm(Objects.requireNonNull(txtMothNm.getText()).toString());

            if (!Objects.requireNonNull(txtMobileNo[0].getText()).toString().trim().isEmpty()) {
                poMobile[0].setMobileNo(txtMobileNo[0].getText().toString());
//            loMobile[1].setIsPostPd();
                if (poMobile[0].getIsPostPd().equalsIgnoreCase("1")) {
                    if (txtMobileYear[0].getText().toString().trim().isEmpty())
                        poMobile[0].setPostYear(0);
                    else {
                        poMobile[0].setPostYear(Integer.parseInt(txtMobileYear[0].getText().toString().trim()));
                    }
                }
                mViewModel.getModel().setMobileNo1(poMobile[0]);
            }
            if (!Objects.requireNonNull(txtMobileNo[1].getText()).toString().trim().isEmpty()) {
                poMobile[1] = new MobileNo();
                poMobile[1].setMobileNo(txtMobileNo[1].getText().toString());
//            loMobile[1].setIsPostPd();
                if (poMobile[1].getIsPostPd().equalsIgnoreCase("1")) {
                    if (txtMobileYear[1].getText().toString().trim().isEmpty()) {
                        poMobile[1].setPostYear(0);
                    } else {

                        poMobile[1].setPostYear(Integer.parseInt(txtMobileYear[1].getText().toString().trim()));
                    }
                }
                mViewModel.getModel().setMobileNo2(poMobile[1]);
            }
            if (!Objects.requireNonNull(txtMobileNo[2].getText()).toString().trim().isEmpty()) {
                poMobile[2] = new MobileNo();
                poMobile[2].setMobileNo(txtMobileNo[2].getText().toString());
                if (poMobile[2].getIsPostPd().equalsIgnoreCase("1")) {
                    if (txtMobileYear[2].getText().toString().trim().isEmpty()) {
                        poMobile[2].setPostYear(0);
                    } else {
                        poMobile[1].setPostYear(Integer.parseInt(txtMobileYear[1].getText().toString().trim()));
                    }
                }
                mViewModel.getModel().setMobileNo3(poMobile[2]);
            }

            mViewModel.getModel().setPhoneNox(Objects.requireNonNull(txtTellNox.getText()).toString());
            mViewModel.getModel().setEmailAdd(Objects.requireNonNull(txtEmailAdd.getText()).toString());
            mViewModel.getModel().setFbAccntx(Objects.requireNonNull(txtFbAccount.getText()).toString());
            mViewModel.getModel().setVbrAccnt(Objects.requireNonNull(txtViberAccount.getText()).toString());

            mViewModel.SaveData(new OnSaveInfoListener() {
                @Override
                public void OnSave(String args) {
                    Intent loIntent = new Intent(Activity_PersonalInfo.this, Activity_ResidenceInfo.class);
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar_PersonalInfo);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        txtLastNm = findViewById(R.id.txt_lastname);
        txtFrstNm = findViewById(R.id.txt_firstname);
        txtMiddNm = findViewById(R.id.txt_middname);
        txtSuffixx = findViewById(R.id.txt_suffix);
        txtNickNm = findViewById(R.id.txt_nickname);
        txtBirthDt = findViewById(R.id.txt_birthdate);
        tilMothNm = findViewById(R.id.til_motherNme);
        txtMothNm = findViewById(R.id.txt_motherNme);

        txtTellNox = findViewById(R.id.txt_telephoneNo);
        txtEmailAdd = findViewById(R.id.txt_emailAdd);
        txtFbAccount = findViewById(R.id.txt_fbAccount);
        txtViberAccount = findViewById(R.id.txt_viberAccount);

        txtTown = findViewById(R.id.txt_bpTown);
        txtCitizen = findViewById(R.id.txt_citizenship);
        rgGender = findViewById(R.id.rg_gender);
        spnCivilStatus = findViewById(R.id.spn_civilStatus);

        txtBirthDt.setOnClickListener(v -> {
            final Calendar newCalendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
            final DatePickerDialog StartTime = new DatePickerDialog(Activity_PersonalInfo.this,
                    android.R.style.Theme_Holo_Dialog, (view131, year, monthOfYear, dayOfMonth) -> {
                try {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    String lsDate = dateFormatter.format(newDate.getTime());
                    txtBirthDt.setText(lsDate);
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
        btnPrev = findViewById(R.id.btn_creditAppPrvs);

        txtMobileNo = new TextInputEditText[]{
                findViewById(R.id.txt_mobileNo1),
                findViewById(R.id.txt_mobileNo2),
                findViewById(R.id.txt_mobileNo3),
        };
        tilMobileYear = new TextInputLayout[]{
                findViewById(R.id.til_mobileNo1Year),
                findViewById(R.id.til_mobileNo2Year),
                findViewById(R.id.til_mobileNo3Year)
        };
        txtMobileYear = new TextInputEditText[]{
                findViewById(R.id.txt_mobileNo1Year),
                findViewById(R.id.txt_mobileNo2Year),
                findViewById(R.id.txt_mobileNo3Year)
        };
        txtMobileType1 = findViewById(R.id.cb_mobile1Type);
        txtMobileType2 = findViewById(R.id.cb_mobile2Type);
        txtMobileType3 = findViewById(R.id.cb_mobile3Type);
    }

    private void SetupMaidenEntry() {
        if (mViewModel.getModel().getGender().equalsIgnoreCase("1")) {
            if (mViewModel.getModel().getCvlStats().equalsIgnoreCase("1") ||
                    mViewModel.getModel().getCvlStats().equalsIgnoreCase("3")) {
                tilMothNm.setVisibility(View.VISIBLE);
            } else {
                tilMothNm.setVisibility(View.GONE);
            }
        } else {
            tilMothNm.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NewApi")
    public void setUpFieldsFromLocalDB(Personal infoModel) throws JSONException {
        if(infoModel != null) {
            txtLastNm.setText(infoModel.getLastName());
            txtFrstNm.setText(infoModel.getFrstName());
            txtMiddNm.setText(infoModel.getMiddName());
            txtSuffixx.setText(infoModel.getSuffix());
            txtNickNm.setText(infoModel.getNickName());
            if(!"".equalsIgnoreCase(infoModel.getBirthDte())){
                txtBirthDt.setText(FormatUIText.formatGOCasBirthdate(infoModel.getBirthDte()));
                mViewModel.getModel().setBrthDate(infoModel.getBirthDte());
            }
            if(!"".equalsIgnoreCase(infoModel.getCvlStats())){
                spnCivilStatus.setText(CreditAppConstants.CIVIL_STATUS[Integer.parseInt(infoModel.getCvlStats())], false);
//                spnCivilStatus.setSelection(Integer.parseInt(infoModel.getCvlStats()));
                mViewModel.getModel().setCvlStats(infoModel.getCvlStats());
            }
            if(!"".equalsIgnoreCase(infoModel.getBrthPlce())) {
                txtTown.setText(infoModel.getBirthPlc());
                mViewModel.getModel().setBirthPlc(infoModel.getBirthPlc());
                mViewModel.getModel().setBrthPlce(infoModel.getBrthPlce());
            }
            if(!"".equalsIgnoreCase(infoModel.getCitizenx())) {
                txtCitizen.setText(infoModel.getCtznShip());
                mViewModel.getModel().setCitizenx(infoModel.getCitizenx());
                mViewModel.getModel().setCtznShip(infoModel.getCtznShip());
            }
            if(!"".equalsIgnoreCase(infoModel.getGender())){
                for(int i = 0; i < rgGender.getChildCount(); i++){
                    if (i == Integer.parseInt(infoModel.getGender())){
                        mViewModel.getModel().setGender(infoModel.getGender());
                        ((RadioButton)rgGender.getChildAt(i)).setChecked(true);
                    }
                }
            }
            if(infoModel.getMobileNo1() != null){
                MobileNo info = infoModel.getMobileNo1();
                txtMobileNo[0].setText(info.getMobileNo());
                poMobile[0].setMobileNo(info.getMobileNo());
                poMobile[0].setIsPostPd(info.getIsPostPd());
                poMobile[0].setPostYear(info.getPostYear());
                mViewModel.getModel().setMobileNo1(info);
                if(info.getIsPostPd().equalsIgnoreCase("0")){
                    tilMobileYear[0].setVisibility(View.GONE);
                    txtMobileType1.setChecked(false);
                }else if(info.getIsPostPd().equalsIgnoreCase("1")){
                    tilMobileYear[0].setVisibility(View.VISIBLE);
                    txtMobileType1.setChecked(true);
                }
            }
            if(infoModel.getMobileNo2() != null){
                MobileNo info = infoModel.getMobileNo2();
                txtMobileNo[1].setText(info.getMobileNo());
                poMobile[1].setMobileNo(info.getMobileNo());
                poMobile[1].setIsPostPd(info.getIsPostPd());
                poMobile[1].setPostYear(info.getPostYear());
                mViewModel.getModel().setMobileNo2(info);
                if(info.getIsPostPd().equalsIgnoreCase("0")){
                    tilMobileYear[1].setVisibility(View.GONE);
                    txtMobileType2.setChecked(false);
                }else if(info.getIsPostPd().equalsIgnoreCase("1")){
                    tilMobileYear[1].setVisibility(View.VISIBLE);
                    txtMobileType2.setChecked(true);
                }
            }
            if(infoModel.getMobileNo3() != null){
                MobileNo info = infoModel.getMobileNo3();
                txtMobileNo[2].setText(info.getMobileNo());
                poMobile[2].setMobileNo(info.getMobileNo());
                poMobile[2].setIsPostPd(info.getIsPostPd());
                poMobile[2].setPostYear(info.getPostYear());
                mViewModel.getModel().setMobileNo3(info);
                if(info.getIsPostPd().equalsIgnoreCase("0")){
                    tilMobileYear[2].setVisibility(View.GONE);
                    txtMobileType3.setChecked(false);
                }else if(info.getIsPostPd().equalsIgnoreCase("1")){
                    tilMobileYear[2].setVisibility(View.VISIBLE);
                    txtMobileType3.setChecked(true);
                }
            }

            txtEmailAdd.setText(infoModel.getEmailAdd());
            txtFbAccount.setText(infoModel.getFbAccntx());
            txtMothNm.setText(infoModel.getMotherNm());
            txtTellNox.setText(infoModel.getPhoneNox());
            txtViberAccount.setText(infoModel.getVbrAccnt());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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
}