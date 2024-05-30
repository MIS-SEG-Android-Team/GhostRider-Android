package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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
import org.rmj.g3appdriver.GCircle.room.Entities.EBarangayInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicantInfo;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppConstants;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.ClientResidence;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.OnParseListener;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMResidenceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity_ResidenceInfo extends AppCompatActivity {

    private VMResidenceInfo mViewModel;
    private MessageBox poMessage;

    private TextInputEditText txtLandMark, txtHouseNox, txtAddress1, txtAddress2, txtRelationship,
            txtLgnthStay, txtMonthlyExp, txtPLandMark, txtPHouseNox, txtPAddress1, txtPAddress2;
    private MaterialAutoCompleteTextView txtBarangay,
            txtMunicipality, txtProvince, txtPBarangay, txtPMunicipl, txtPProvince;
    private MaterialCheckBox cbOneAddress;
    private MaterialAutoCompleteTextView spnLgnthStay, spnHouseHold, spnHouseType;

    private TextInputLayout tilRelationship;
    private LinearLayout lnOtherInfo, lnPermaAddx;
    private MaterialButton btnNext;
    private MaterialButton btnPrvs;
    private RadioGroup rgOwnsership, rgGarage;
    private MaterialToolbar toolbar;

    private String TransNox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_ResidenceInfo.this).get(VMResidenceInfo.class);
        poMessage = new MessageBox(Activity_ResidenceInfo.this);
        setContentView(R.layout.activity_residence_info);
        initWidgets();
        mViewModel.InitializeApplication(getIntent());
        mViewModel.GetApplication().observe(Activity_ResidenceInfo.this, new Observer<ECreditApplicantInfo>() {
            @Override
            public void onChanged(ECreditApplicantInfo app) {
                try {
                    TransNox = app.getTransNox();
                    mViewModel.getModel().setTransNox(app.getTransNox());
                    mViewModel.ParseData(app, new OnParseListener() {
                        @Override
                        public void OnParse(Object args) {
                            ClientResidence loDetail = (ClientResidence) args;
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

        /**
         * CLIENT RESIDENCE
         * */

        mViewModel.GetTownProvinceList().observe(Activity_ResidenceInfo.this, new Observer<List<DTownInfo.TownProvinceInfo>>() {
            @Override
            public void onChanged(List<DTownInfo.TownProvinceInfo> loList) {
                try {
                    ArrayList<String> string = new ArrayList<>();
                    for (int x = 0; x < loList.size(); x++) {
                        String lsTown = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
//                        String lsTown =  loList.get(x).sProvName ;
                        string.add(lsTown);

                    }
                    ArrayAdapter<String> adapters = new ArrayAdapter<>(Activity_ResidenceInfo.this, android.R.layout.simple_spinner_dropdown_item, string.toArray(new String[0]));
                    txtMunicipality.setAdapter(adapters);
                    txtMunicipality.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            for (int x = 0; x < loList.size(); x++) {
                                String lsLabel = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                                String lsSlctd = txtMunicipality.getText().toString().trim();
                                if (lsSlctd.equalsIgnoreCase(lsLabel)) {
                                    mViewModel.getModel().setMunicipalID(loList.get(x).sTownIDxx);
                                    mViewModel.getModel().setMunicipalNm(lsLabel);
                                    break;
                                }

                            }

                            mViewModel.GetBarangayList(mViewModel.getModel().getMunicipalID()).observe(Activity_ResidenceInfo.this, new Observer<List<EBarangayInfo>>() {
                                @Override
                                public void onChanged(List<EBarangayInfo> BrgyList) {
                                    ArrayList<String> string = new ArrayList<>();
                                    for (int x = 0; x < BrgyList.size(); x++) {
                                        String lsBrgy = BrgyList.get(x).getBrgyName();
                                        string.add(lsBrgy);
                                    }
                                    ArrayAdapter<String> adapters = new ArrayAdapter<>(Activity_ResidenceInfo.this,
                                            android.R.layout.simple_spinner_dropdown_item, string.toArray(new String[0]));
                                    txtBarangay.setAdapter(adapters);
                                    txtBarangay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            for (int x = 0; x < BrgyList.size(); x++) {
                                                String lsLabel = BrgyList.get(x).getBrgyName();
                                                String lsSlctd = txtBarangay.getText().toString().trim();
                                                if (lsSlctd.equalsIgnoreCase(lsLabel)) {
                                                    mViewModel.getModel().setBarangayID(BrgyList.get(x).getBrgyIDxx());
                                                    mViewModel.getModel().setBarangayName(lsLabel);
                                                }
                                            }
                                        }
                                    });
                                }
                            });

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /**
         * CLIENT PERMANENT RESIDENCE
         * */

        mViewModel.GetTownProvinceList().observe(Activity_ResidenceInfo.this, new Observer<List<DTownInfo.TownProvinceInfo>>() {
            @Override
            public void onChanged(List<DTownInfo.TownProvinceInfo> loList) {
                try {
                    ArrayList<String> string = new ArrayList<>();
                    for (int x = 0; x < loList.size(); x++) {
                        String lsTown = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
//                        String lsTown =  loList.get(x).sProvName ;
                        string.add(lsTown);
                    }
                    ArrayAdapter<String> adapters = new ArrayAdapter<>(Activity_ResidenceInfo.this, android.R.layout.simple_spinner_dropdown_item, string.toArray(new String[0]));
                    txtPMunicipl.setAdapter(adapters);
                    txtPMunicipl.setOnItemClickListener((parent, view, position, id) -> {
                        for (int x = 0; x < loList.size(); x++) {
                            String lsLabel = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                            String lsSlctd = txtPMunicipl.getText().toString().trim();
                            if (lsSlctd.equalsIgnoreCase(lsLabel)) {
                                mViewModel.getModel().setPermanentMunicipalID(loList.get(x).sTownIDxx);
                                mViewModel.getModel().setPermanentMunicipalNm(lsLabel);
                                break;
                            }
                        }

                        mViewModel.GetBarangayList(mViewModel.getModel().getMunicipalID()).observe(Activity_ResidenceInfo.this, new Observer<List<EBarangayInfo>>() {
                            @Override
                            public void onChanged(List<EBarangayInfo> BrgyList) {
                                ArrayList<String> string1 = new ArrayList<>();
                                for (int x = 0; x < BrgyList.size(); x++) {
                                    String lsBrgy = BrgyList.get(x).getBrgyName();
                                    string1.add(lsBrgy);
                                }
                                ArrayAdapter<String> adapters1 = new ArrayAdapter<>(Activity_ResidenceInfo.this,
                                        android.R.layout.simple_spinner_dropdown_item, string1.toArray(new String[0]));
                                txtPBarangay.setAdapter(adapters1);
                                txtPBarangay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        for (int x = 0; x < BrgyList.size(); x++) {
                                            String lsLabel = BrgyList.get(x).getBrgyName();
                                            String lsSlctd = txtPBarangay.getText().toString().trim();
                                            if (lsSlctd.equalsIgnoreCase(lsLabel)) {
                                                mViewModel.getModel().setPermanentBarangayID(BrgyList.get(x).getBrgyIDxx());
                                                mViewModel.getModel().setPermanentBarangayName(lsLabel);
                                            }
                                        }
                                    }
                                });
                            }
                        });

                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        spnHouseHold.setAdapter(new ArrayAdapter<>(Activity_ResidenceInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.HOUSEHOLDS));
        spnHouseHold.setOnItemClickListener((parent, view, position, id) ->
                mViewModel.getModel().setHouseHold(String.valueOf(position)));
//                mViewModel.getModel().setHouseHold(spnHouseHold.getText().toString().trim()));

        spnHouseType.setAdapter(new ArrayAdapter<>(Activity_ResidenceInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.HOUSE_TYPE));
        spnHouseType.setOnItemClickListener((parent, view, position, id) ->
                mViewModel.getModel().setHouseType(String.valueOf(position)));
//                mViewModel.getModel().setHouseType(spnHouseType.getText().toString().trim()));

        spnLgnthStay.setAdapter(new ArrayAdapter<>(Activity_ResidenceInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.LENGTH_OF_STAY));
        spnLgnthStay.setOnItemClickListener((parent, view, position, id) ->
                mViewModel.getModel().setIsYear(position));


        btnNext.setOnClickListener(v -> SaveResidenceInfo());
        btnPrvs.setOnClickListener(v -> {
            Intent loIntent = new Intent(Activity_ResidenceInfo.this, Activity_PersonalInfo.class);
            loIntent.putExtra("sTransNox", TransNox);
            startActivity(loIntent);
            overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
            finish();
        });

    }

    private void SaveResidenceInfo() {

        mViewModel.getModel().setOneAddress(cbOneAddress.isChecked());

        mViewModel.getModel().setLandMark((txtLandMark.getText()).toString());
        mViewModel.getModel().setHouseNox((txtHouseNox.getText()).toString());
        mViewModel.getModel().setAddress1((txtAddress1.getText()).toString());
        mViewModel.getModel().setAddress2((txtAddress2.getText()).toString());
        mViewModel.getModel().setOwnerRelation((txtRelationship.getText()).toString());

        mViewModel.getModel().setPermanentLandMark((txtPLandMark.getText()).toString());
        mViewModel.getModel().setPermanentHouseNo((txtPHouseNox.getText()).toString());
        mViewModel.getModel().setPermanentAddress1((txtPAddress1.getText()).toString());
        mViewModel.getModel().setPermanentAddress2((txtPAddress2.getText()).toString());


        if (mViewModel.getModel().getHouseOwn().equalsIgnoreCase("1") ||
                mViewModel.getModel().getHouseOwn().equalsIgnoreCase("2")) {
            if (txtLgnthStay.getText().toString().isEmpty()) {
                mViewModel.getModel().setLenghtOfStay(0);
            } else {
                mViewModel.getModel().setLenghtOfStay(Double.parseDouble(txtLgnthStay.getText().toString()));
            }
            if (txtMonthlyExp.getText().toString().isEmpty()) {
                mViewModel.getModel().setMonthlyExpenses(0);
            } else {
                mViewModel.getModel().setMonthlyExpenses(Double.parseDouble(txtMonthlyExp.getText().toString()));
            }
        }

        mViewModel.SaveData(new OnSaveInfoListener() {
            @Override
            public void OnSave(String args) {
                Intent loIntent = new Intent(Activity_ResidenceInfo.this, Activity_MeansInfoSelection.class);
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

    private class OnHouseOwnershipSelectListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if (radioGroup.getId() == R.id.rg_ownership) {
                if (i == R.id.rb_owned) {
                    lnOtherInfo.setVisibility(View.GONE);
                    mViewModel.getModel().setHouseOwn("0");
                }
                if (i == R.id.rb_rent) {
                    lnOtherInfo.setVisibility(View.VISIBLE);
                    tilRelationship.setVisibility(View.GONE);
                    mViewModel.getModel().setHouseOwn("1");
                }
                if (i == R.id.rb_careTaker) {
                    lnOtherInfo.setVisibility(View.VISIBLE);
                    tilRelationship.setVisibility(View.VISIBLE);
                    mViewModel.getModel().setHouseOwn("2");


                }
            } else {
                if (i == R.id.rb_yes) {
                    mViewModel.getModel().setHasGarage("1");
                }
                if (i == R.id.rb_no) {
                    mViewModel.getModel().setHasGarage("0");
                }
            }
        }
    }

    private class OnAddressSetListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                lnPermaAddx.setVisibility(View.GONE);
            } else {
                lnPermaAddx.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initWidgets() {

        toolbar = findViewById(R.id.toolbar_ResidenceInfo);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        cbOneAddress = findViewById(R.id.cb_oneAddress);

        txtLandMark = findViewById(R.id.txt_landmark);
        txtHouseNox = findViewById(R.id.txt_houseNox);
        txtAddress1 = findViewById(R.id.txt_address);
        txtAddress2 = findViewById(R.id.txt_address2);
        txtBarangay = findViewById(R.id.txt_barangay);
        txtMunicipality = findViewById(R.id.txt_town);
//        txtProvince = findViewById(R.id.txt_province);
        txtRelationship = findViewById(R.id.txt_relationship);
        txtLgnthStay = findViewById(R.id.txt_lenghtStay);
        txtMonthlyExp = findViewById(R.id.txt_monthlyExp);
        txtPLandMark = findViewById(R.id.txt_perm_landmark);
        txtPHouseNox = findViewById(R.id.txt_perm_houseNox);
        txtPAddress1 = findViewById(R.id.txt_perm_address);
        txtPAddress2 = findViewById(R.id.txt_perm_address2);
        txtPBarangay = findViewById(R.id.txt_perm_barangay);
        txtPMunicipl = findViewById(R.id.txt_perm_town);
//        txtPProvince = findViewById(R.id.txt_perm_province);

        spnLgnthStay = findViewById(R.id.spn_lenghtStayx);
        spnHouseHold = findViewById(R.id.spn_houseHoldx);
        spnHouseType = findViewById(R.id.spn_houseTypex);

        rgOwnsership = findViewById(R.id.rg_ownership);
        rgGarage = findViewById(R.id.rg_garage);

        tilRelationship = findViewById(R.id.til_relationship);

        lnOtherInfo = findViewById(R.id.linear_otherInfo);
        lnPermaAddx = findViewById(R.id.linear_permanentAdd);

        cbOneAddress.setOnCheckedChangeListener(new OnAddressSetListener());
        rgOwnsership.setOnCheckedChangeListener(new OnHouseOwnershipSelectListener());
        rgGarage.setOnCheckedChangeListener(new OnHouseOwnershipSelectListener());

        btnNext = findViewById(R.id.btn_creditAppNext);
        btnPrvs = findViewById(R.id.btn_creditAppPrvs);

    }

    @SuppressLint("NewApi")
    public void setUpFieldsFromLocalDB(ClientResidence infoModel) throws JSONException {
        if(infoModel != null) {

            if(infoModel.isOneAddress()){
                cbOneAddress.setChecked(true);
                mViewModel.getModel().setOneAddress(infoModel.isOneAddress());
            }
            if(!"".equalsIgnoreCase(infoModel.getPermanentAddress1())){
                txtPAddress1.setText(infoModel.getPermanentAddress1());
            }
            if(!"".equalsIgnoreCase(infoModel.getPermanentAddress2())){
                txtPAddress2.setText(infoModel.getPermanentAddress2());
            }
            if(!"".equalsIgnoreCase(infoModel.getPermanentLandMark())){
                txtPLandMark.setText(infoModel.getPermanentLandMark());
            }
            if(!"".equalsIgnoreCase(infoModel.getPermanentHouseNo())){
                txtPHouseNox.setText(infoModel.getHouseNox());
            }

            if(!"".equalsIgnoreCase(infoModel.getPermanentMunicipalID())) {
                txtPMunicipl.setText(infoModel.getPermanentMunicipalNm());
                mViewModel.getModel().setPermanentMunicipalID(infoModel.getPermanentMunicipalID());
                mViewModel.getModel().setPermanentMunicipalNm(infoModel.getPermanentMunicipalNm());
            }

            if(!"".equalsIgnoreCase(infoModel.getPermanentBarangayID())) {
                txtPBarangay.setText(infoModel.getPermanentBarangayName());
                mViewModel.getModel().setPermanentBarangayID(infoModel.getPermanentBarangayID());
                mViewModel.getModel().setPermanentBarangayName(infoModel.getPermanentBarangayName());
            }

            if(!"".equalsIgnoreCase(infoModel.getAddress1())){
                txtAddress1.setText(infoModel.getAddress1());
            }
            if(!"".equalsIgnoreCase(infoModel.getAddress2())){
                txtAddress2.setText(infoModel.getAddress2());
            }
            if(!"".equalsIgnoreCase(infoModel.getLandMark())){
                txtLandMark.setText(infoModel.getLandMark());
            }
            if(!"".equalsIgnoreCase(infoModel.getHouseNox())){
                txtHouseNox.setText(infoModel.getHouseNox());
            }

            if(!"".equalsIgnoreCase(infoModel.getMunicipalID())) {
                txtMunicipality.setText(infoModel.getMunicipalNm());
                mViewModel.getModel().setMunicipalID(infoModel.getMunicipalID());
                mViewModel.getModel().setMunicipalNm(infoModel.getMunicipalNm());
            }

            if(!"".equalsIgnoreCase(infoModel.getBarangayID())) {
                txtBarangay.setText(infoModel.getBarangayName());
                mViewModel.getModel().setBarangayID(infoModel.getBarangayID());
                mViewModel.getModel().setBarangayName(infoModel.getBarangayName());
            }

            if (infoModel.getHouseOwn().equalsIgnoreCase("0")) {
                rgOwnsership.check(R.id.rb_owned);
                mViewModel.getModel().setHouseOwn("0");
            } else if (infoModel.getHouseOwn().equalsIgnoreCase("1") ||
                    infoModel.getHouseOwn().equalsIgnoreCase("2")) {

                if (infoModel.getHouseOwn().equalsIgnoreCase("1")) {
                    lnOtherInfo.setVisibility(View.VISIBLE);
                    tilRelationship.setVisibility(View.GONE);
                    rgOwnsership.check(R.id.rb_rent);
                    mViewModel.getModel().setHouseOwn("1");
                }else{
                    lnOtherInfo.setVisibility(View.VISIBLE);
                    tilRelationship.setVisibility(View.VISIBLE);
                    rgOwnsership.check(R.id.rb_careTaker);
                    mViewModel.getModel().setHouseOwn("2");
                    if(!"".equalsIgnoreCase(infoModel.getOwnerRelation())){
                        txtRelationship.setText(infoModel.getOwnerRelation());
                    }
                }
                if(infoModel.getMonthlyExpenses() > 0){
                    txtMonthlyExp.setText(FormatUIText.getCurrencyUIFormat(String.valueOf(infoModel.getMonthlyExpenses())));
                }

                int nlength = (int)(infoModel.getLenghtofStay() * 12);
                if (nlength < 12){
                    txtLgnthStay.setText(String.valueOf(nlength));
                    spnLgnthStay.setText(CreditAppConstants.LENGTH_OF_STAY[0], false);
                    mViewModel.getModel().setIsYear(0);
                    mViewModel.getModel().setLenghtOfStay(nlength);
                }else{
                    txtLgnthStay.setText(String.valueOf(infoModel.getLenghtofStay()));
                    spnLgnthStay.setText(CreditAppConstants.LENGTH_OF_STAY[1], false);
                    mViewModel.getModel().setIsYear(1);
                    mViewModel.getModel().setLenghtOfStay(infoModel.getLenghtofStay());
                }

            }

            if (infoModel.getHasGarage().equalsIgnoreCase("0")){
                rgGarage.check(R.id.rb_no);
                mViewModel.getModel().setHasGarage("0");
            }else {
                rgGarage.check(R.id.rb_yes);
                mViewModel.getModel().setHasGarage("1");
            }
            if(!"".equalsIgnoreCase(infoModel.getHouseOwn())){
                spnHouseHold.setText(CreditAppConstants.HOUSEHOLDS[Integer.parseInt(infoModel.getHouseOwn())]);
//                spnHouseHold.setSelection(Integer.parseInt(infoModel.getHouseOwn()));
                mViewModel.getModel().setHouseHold(infoModel.getHouseOwn());
            }

            if(!"".equalsIgnoreCase(infoModel.getHouseType())){
                spnHouseType.setText(CreditAppConstants.HOUSE_TYPE[Integer.parseInt(infoModel.getHouseType())]);
//                spnHouseType.setSelection(Integer.parseInt(infoModel.getHouseType()));
                mViewModel.getModel().setHouseType(infoModel.getHouseType());
            }
            txtMonthlyExp.setText(!"".equalsIgnoreCase(String.valueOf(infoModel.getMonthlyExpenses())) ? String.valueOf(infoModel.getMonthlyExpenses()) : "");

        }else{
            cbOneAddress.setChecked(false);
            txtLandMark.getText().clear();
            txtHouseNox.getText().clear();
            txtAddress1.getText().clear();
            txtAddress2.getText().clear();
            txtBarangay.getText().clear();
            txtMunicipality.getText().clear();
            txtProvince.getText().clear();
            txtRelationship.getText().clear();
            txtLgnthStay.getText().clear();
            txtMonthlyExp.getText().clear();
            txtPLandMark.getText().clear();
            txtPHouseNox.getText().clear();
            txtPAddress1.getText().clear();
            txtPAddress2.getText().clear();
            txtPBarangay.getText().clear();
            txtPMunicipl.getText().clear();
            txtPProvince.getText().clear();
            spnLgnthStay.getText().clear();
            spnHouseHold.getText().clear();
            spnHouseType.getText().clear();
            rgOwnsership.clearCheck();
            rgGarage .clearCheck();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent loIntent = new Intent(Activity_ResidenceInfo.this, Activity_PersonalInfo.class);
            loIntent.putExtra("sTransNox", TransNox);
            startActivity(loIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent loIntent = new Intent(Activity_ResidenceInfo.this, Activity_PersonalInfo.class);
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