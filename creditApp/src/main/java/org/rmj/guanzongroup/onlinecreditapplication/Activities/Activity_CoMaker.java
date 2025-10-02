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


import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import  com.google.android.material.checkbox.MaterialCheckBox;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DTownInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicantInfo;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.CoMaker;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.MobileNo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppConstants;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.OnParseListener;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMCoMaker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Activity_CoMaker extends AppCompatActivity {
    private static final String TAG = Activity_CoMaker.class.getSimpleName();

    private VMCoMaker mViewModel;
    private MessageBox poMessage;
    private MobileNo[] poMobile = new MobileNo[3];

    private TextInputEditText tieLastname, tieFrstname, tieMiddname, tiePrmCntct, tieScnCntct,
            tieTrtCntct, tieNickname, tieBrthDate, tiePrmCntctPlan, tieScnCntctPlan, tieTrtCntctPlan,
            tieFbAcctxx;
    private MaterialAutoCompleteTextView tieSuffixxx, tieBrthProv, tieBrthTown, spnIncmSrce, spnBrwrRltn;
    private MaterialCheckBox cbPrmCntct, cbScnCntct, cbTrtCntct;
    private TextInputLayout tilPrmCntctPlan, tilScnCntctPlan, tilTrtCntctPlan;

    private MaterialButton btnPrvs, btnNext;
    private MaterialToolbar toolbar;

    private String TransNox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_CoMaker.this).get(VMCoMaker.class);
        poMessage = new MessageBox(Activity_CoMaker.this);
        poMobile[0] = new MobileNo();
        poMobile[1] = new MobileNo();
        poMobile[2] = new MobileNo();
        setContentView(R.layout.activity_co_maker);
        initWidgets();
        initSpinner();
        mViewModel.InitializeApplication(getIntent());
        mViewModel.GetApplication().observe(Activity_CoMaker.this, new Observer<ECreditApplicantInfo>() {
            @Override
            public void onChanged(ECreditApplicantInfo app) {
                try {
                    TransNox = app.getTransNox();
                    mViewModel.getModel().setTransNox(app.getTransNox());
                    mViewModel.ParseData(app, new OnParseListener() {
                        @Override
                        public void OnParse(Object args) {
                            CoMaker loDetail = (CoMaker) args;
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

        mViewModel.GetTownProvinceList().observe(Activity_CoMaker.this, new Observer<List<DTownInfo.TownProvinceInfo>>() {
            @Override
            public void onChanged(List<DTownInfo.TownProvinceInfo> loList) {
                try {
                    ArrayList<String> strings = new ArrayList<>();
                    for (int x = 0; x < loList.size(); x++) {
                        String lsTown = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                        strings.add(lsTown);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_CoMaker.this, android.R.layout.simple_spinner_dropdown_item, strings.toArray(new String[0]));
                    tieBrthTown.setAdapter(adapter);
                    tieBrthTown.setDropDownBackgroundResource(R.drawable.bg_gradient_light);

                    tieBrthTown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            for (int x = 0; x < loList.size(); x++) {
                                String lsLabel = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                                String lsSlctd = tieBrthTown.getText().toString().trim();
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




        cbPrmCntct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbPrmCntct.isChecked()) {
                    poMobile[0].setIsPostPd("1");
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.35f
                    );
                    cbPrmCntct.setLayoutParams(param);
                    tilPrmCntctPlan.setVisibility(View.VISIBLE);
                } else {
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            2.0f
                    );
                    cbPrmCntct.setLayoutParams(param);
                    poMobile[0].setIsPostPd("0");
                    tilPrmCntctPlan.setVisibility(View.GONE);
                }
            }
        });

        cbScnCntct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbScnCntct.isChecked()) {
                    poMobile[1].setIsPostPd("1");
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.35f
                    );
                    cbScnCntct.setLayoutParams(param);
                    tilScnCntctPlan.setVisibility(View.VISIBLE);
                } else {
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            2.0f
                    );
                    cbScnCntct.setLayoutParams(param);
                    poMobile[1].setIsPostPd("0");
                    tilScnCntctPlan.setVisibility(View.GONE);
                }
            }
        });

        cbTrtCntct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbTrtCntct.isChecked()) {
                    poMobile[2].setIsPostPd("1");
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.35f
                    );
                    cbTrtCntct.setLayoutParams(param);
                    tilTrtCntctPlan.setVisibility(View.VISIBLE);
                } else {
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            2.0f
                    );
                    cbTrtCntct.setLayoutParams(param);
                    poMobile[2].setIsPostPd("0");
                    tilTrtCntctPlan.setVisibility(View.GONE);
                }
            }
        });
        btnNext.setOnClickListener(v -> SaveCoMakerInfo());
        btnPrvs.setOnClickListener(v -> {
            returnPrevious();
        });

    }
    private void initSpinner(){
        spnIncmSrce.setAdapter(new ArrayAdapter<>(Activity_CoMaker.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.CO_MAKER_INCOME_SOURCE));
        spnIncmSrce.setDropDownBackgroundResource(R.drawable.bg_gradient_light);
        spnIncmSrce.setOnItemClickListener((parent, view, position, id) ->
                mViewModel.getModel().setIncomexx(String.valueOf(position)));

        spnBrwrRltn.setAdapter(new ArrayAdapter<>(Activity_CoMaker.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.CO_MAKER_RELATIONSHIP));
        spnBrwrRltn.setDropDownBackgroundResource(R.drawable.bg_gradient_light);
        spnBrwrRltn.setOnItemClickListener((parent, view, position, id) ->
                mViewModel.getModel().setRelation(String.valueOf(position)));

    }
    private void SaveCoMakerInfo() {

        mViewModel.getModel().setLastName(tieLastname.getText().toString().trim());
        mViewModel.getModel().setFrstName(tieFrstname.getText().toString().trim());
        mViewModel.getModel().setMiddName(tieMiddname.getText().toString().trim());
        mViewModel.getModel().setSuffix(tieSuffixxx.getText().toString().trim());
        mViewModel.getModel().setNickName(tieNickname.getText().toString().trim());

        if (tiePrmCntct != null || !Objects.requireNonNull(tiePrmCntct.getText()).toString().trim().isEmpty()) {
            poMobile[0].setMobileNo(tiePrmCntct.getText().toString());
//            loMobile[1].setIsPostPd();
            if (poMobile[0].getIsPostPd().equalsIgnoreCase("1")) {
                if (tiePrmCntctPlan.getText().toString().isEmpty()) {
                    poMobile[0].setPostYear(0);
                } else {
                    poMobile[0].setPostYear(Integer.parseInt(tiePrmCntctPlan.getText().toString()));
                }
            }
            mViewModel.getModel().setMobileNo1(poMobile[0]);
        }

        if (!Objects.requireNonNull(tieScnCntct.getText()).toString().trim().isEmpty()) {
            poMobile[1].setMobileNo(tieScnCntct.getText().toString());
//            loMobile[1].setIsPostPd();
            if (poMobile[1].getIsPostPd().equalsIgnoreCase("1")) {
                if (tieScnCntctPlan.getText().toString().isEmpty()) {
                    poMobile[1].setPostYear(0);
                } else {
                    poMobile[1].setPostYear(Integer.parseInt(tieScnCntctPlan.getText().toString()));
                }
            }
            mViewModel.getModel().setMobileNo2(poMobile[1]);
        }
        if (!Objects.requireNonNull(tieTrtCntct.getText()).toString().trim().isEmpty()) {
            poMobile[2].setMobileNo(tieTrtCntct.getText().toString());
//            poMobile[2].setIsPostPd();
            if (poMobile[2].getIsPostPd().equalsIgnoreCase("1")) {
                if (tieTrtCntctPlan.getText().toString().isEmpty()) {
                    poMobile[2].setPostYear(0);
                } else {
                    poMobile[2].setPostYear(Integer.parseInt(tieTrtCntctPlan.getText().toString()));
                }
            }
            mViewModel.getModel().setMobileNo3(poMobile[2]);
        }

        mViewModel.getModel().setFbAccntx(tieFbAcctxx.getText().toString().trim());


        mViewModel.SaveData(new OnSaveInfoListener() {
            @Override
            public void OnSave(String args) {
                Intent loIntent = new Intent(Activity_CoMaker.this, Activity_ComakerResidence.class);
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
        toolbar = findViewById(R.id.toolbar_CoMaker);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        tieLastname = findViewById(R.id.tie_cap_cmrLastname);
        tieFrstname = findViewById(R.id.tie_cap_cmrFirstname);
        tieMiddname = findViewById(R.id.tie_cap_cmrMiddname);
        tieSuffixxx = findViewById(R.id.tie_cap_cmrSuffix);
        tieNickname = findViewById(R.id.tie_cap_cmrNickname);
        tieBrthDate = findViewById(R.id.tie_cap_cmrBirthdate);
//        tieBrthProv = findViewById(R.id.tie_cap_cmrBirthProv);
        tieBrthTown = findViewById(R.id.tie_cap_cmrBirthTown);
        tiePrmCntct = findViewById(R.id.tie_cap_cmrPrimaryContactNo);
        tieScnCntct = findViewById(R.id.tie_cap_cmrSecondaryContactNo);
        tieTrtCntct = findViewById(R.id.tie_cap_cmrTertiaryContactNo);

        tilPrmCntctPlan = findViewById(R.id.til_cap_cmrPrimaryCntctPlan);
        tilScnCntctPlan = findViewById(R.id.til_cap_cmrSecondaryCntctPlan);
        tilTrtCntctPlan = findViewById(R.id.til_cap_cmrTertiaryCntctPlan);

        tiePrmCntctPlan = findViewById(R.id.tie_cap_cmrPrimaryCntctPlan);
        tieScnCntctPlan = findViewById(R.id.tie_cap_cmrSecondaryCntctPlan);
        tieTrtCntctPlan = findViewById(R.id.tie_cap_cmrTertiaryCntctPlan);

        tieFbAcctxx = findViewById(R.id.tie_cap_cmrFacebookacc);

        spnIncmSrce = findViewById(R.id.spinner_cap_cmrIncomeSrc);
        spnBrwrRltn = findViewById(R.id.spinner_cap_cmrBarrowerRelation);

        cbPrmCntct = findViewById(R.id.cb_cap_cmrPrimaryCntctStats);
        cbScnCntct = findViewById(R.id.cb_cap_cmrSecondaryCntctStats);
        cbTrtCntct = findViewById(R.id.cb_cap_cmrTertiaryCntctStats);

        btnNext = findViewById(R.id.btn_creditAppNext);
        btnPrvs = findViewById(R.id.btn_creditAppPrvs);

        tieBrthDate.setOnClickListener(v -> {
            final Calendar newCalendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
            final DatePickerDialog StartTime = new DatePickerDialog(Activity_CoMaker.this,
                    android.R.style.Theme_Holo_Dialog, (view131, year, monthOfYear, dayOfMonth) -> {
                try {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    String lsDate = dateFormatter.format(newDate.getTime());
                    tieBrthDate.setText(lsDate);
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


    }

    @SuppressLint("NewApi")
    public void setUpFieldsFromLocalDB(CoMaker infoModel) throws JSONException {
        if(infoModel != null) {
            if(!"".equalsIgnoreCase(infoModel.getRelation())){
                spnBrwrRltn.setText(CreditAppConstants.CO_MAKER_RELATIONSHIP[Integer.parseInt(infoModel.getRelation())]);
//                spnBrwrRltn.setSelection(Integer.parseInt(infoModel.getRelation()));
                mViewModel.getModel().setRelation(infoModel.getRelation());
            }
            tieLastname.setText(infoModel.getLastName());
            tieFrstname.setText(infoModel.getFrstName());
            tieMiddname.setText(infoModel.getMiddName());
            tieSuffixxx.setText(infoModel.getSuffix());
            tieNickname.setText(infoModel.getNickName());
//
//            if(!"".equalsIgnoreCase(infoModel.getBirthDte())){
//                tieBrthDate.setText(FormatUIText.formatGOCasBirthdate(infoModel.getBirthDte()));
//                mViewModel.getModel().setBrthDate(infoModel.getBirthDte());
//            }
            if(!"".equalsIgnoreCase(infoModel.getIncomexx())){
                spnIncmSrce .setText(CreditAppConstants.CO_MAKER_INCOME_SOURCE[Integer.parseInt(infoModel.getIncomexx())]);
//                spnIncmSrce.setSelection(Integer.parseInt(infoModel.getIncomexx()));
                mViewModel.getModel().setIncomexx(infoModel.getIncomexx());
            }
            if(!"".equalsIgnoreCase(infoModel.getBrthPlce())) {
                tieBrthTown.setText(infoModel.getBirthPlc());
                mViewModel.getModel().setBirthPlc(infoModel.getBirthPlc());
                mViewModel.getModel().setBrthPlce(infoModel.getBrthPlce());
            }
            if(infoModel.getMobileNo1() != null){
                MobileNo info = infoModel.getMobileNo1();
                mViewModel.getModel().setMobileNo1(info);
                tiePrmCntct.setText(info.getMobileNo());
                poMobile[0].setMobileNo(info.getMobileNo());
                poMobile[0].setIsPostPd(info.getIsPostPd());
                poMobile[0].setPostYear(info.getPostYear());
                if(info.getIsPostPd().equalsIgnoreCase("1")){
                    Log.e("getIsPostPd","visible");
                    tilPrmCntctPlan.setVisibility(View.VISIBLE);
                    cbPrmCntct.setChecked(false);
                }else{
                    Log.e("getIsPostPd","gone");
                    tilPrmCntctPlan.setVisibility(View.GONE);
                    cbPrmCntct.setChecked(false);
                }
            }
            if(infoModel.getMobileNo2() != null){
                MobileNo info = infoModel.getMobileNo2();
                tieScnCntct.setText(info.getMobileNo());
                mViewModel.getModel().setMobileNo2(info);
                poMobile[1].setMobileNo(info.getMobileNo());
                poMobile[1].setIsPostPd(info.getIsPostPd());
                poMobile[1].setPostYear(info.getPostYear());
                if(info.getIsPostPd().equalsIgnoreCase("0")){
                    tilScnCntctPlan.setVisibility(View.GONE);
                    cbScnCntct.setChecked(false);
                }else{
                    tilScnCntctPlan.setVisibility(View.VISIBLE);
                    cbScnCntct.setChecked(true);
                }
            }
            if(infoModel.getMobileNo3() != null){
                MobileNo info = infoModel.getMobileNo3();
                tieTrtCntct.setText(info.getMobileNo());
                poMobile[2].setMobileNo(info.getMobileNo());
                poMobile[2].setIsPostPd(info.getIsPostPd());
                poMobile[2].setPostYear(info.getPostYear());
                mViewModel.getModel().setMobileNo3(info);
                if(info.getIsPostPd().equalsIgnoreCase("0")){
                    tilTrtCntctPlan.setVisibility(View.GONE);
                    cbTrtCntct.setChecked(false);
                }else{
                    tilTrtCntctPlan.setVisibility(View.VISIBLE);
                    cbTrtCntct.setChecked(true);
                }
            }

            tieFbAcctxx.setText(infoModel.getFbAccntx());
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
        Intent loIntent = new Intent(Activity_CoMaker.this, Activity_OtherInfo.class);
        loIntent.putExtra("sTransNox", TransNox);
        startActivity(loIntent);
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
        finish();
    }
}