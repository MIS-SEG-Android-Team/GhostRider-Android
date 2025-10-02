package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import  com.google.android.material.checkbox.MaterialCheckBox;


import org.json.JSONException;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DTownInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBarangayInfo;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.SpouseResidenceInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.ClientResidence;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.SpouseResidence;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMSpouseResidence;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity_SpouseResidenceInfo extends AppCompatActivity {

    private VMSpouseResidence mViewModel;
    private MessageBox poMessage;

    private TextInputEditText txtLandMark, txtHouseNox, txtAddress1, txtAddress2;
    private MaterialAutoCompleteTextView txtBarangay, txtTown;
    private MaterialCheckBox cbSameAdd;
    private MaterialButton btnNext, btnPrvs;
    private MaterialToolbar toolbar;

    private String TransNox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_SpouseResidenceInfo.this).get(VMSpouseResidence.class);
        poMessage = new MessageBox(Activity_SpouseResidenceInfo.this);
        setContentView(R.layout.activity_spouse_residence_info);
        initWidgets();
        mViewModel.InitializeApplication(getIntent());
        mViewModel.GetApplication().observe(Activity_SpouseResidenceInfo.this, app -> {
            try {
                TransNox = app.getTransNox();
                mViewModel.getModel().setTransNox(app.getTransNox());
                mViewModel.ParseData(app, args -> {
                    SpouseResidenceInfo.oResidence loDetail = (SpouseResidenceInfo.oResidence) args;
                    try {
                        setUpFieldsFromLocalDB(loDetail.getSpouseResidence());

                        cbSameAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                ClientResidence loClient = loDetail.getApplResidence();
                                if(isChecked){
                                    mViewModel.getModel().setAddress1(loClient.getAddress1());
                                    mViewModel.getModel().setAddress2(loClient.getAddress2());
                                    mViewModel.getModel().setBarangayID(loClient.getBarangayID());
                                    mViewModel.getModel().setBarangayName(loClient.getBarangayName());
                                    mViewModel.getModel().setMunicipalID(loClient.getMunicipalID());
                                    mViewModel.getModel().setMunicipalNm(loClient.getMunicipalNm());
                                    mViewModel.getModel().setHouseNox(loClient.getHouseNox());
                                    mViewModel.getModel().setLandMark(loClient.getLandMark());
                                    txtLandMark.setText(loClient.getLandMark());
                                    txtHouseNox.setText(loClient.getHouseNox());
                                    txtAddress1.setText(loClient.getAddress1());
                                    txtAddress2.setText(loClient.getAddress2());
                                    txtTown.setText(loClient.getMunicipalNm());
                                    txtBarangay.setText(loClient.getBarangayName());
                                } else {
                                    mViewModel.getModel().setAddress1("");
                                    mViewModel.getModel().setAddress2("");
                                    mViewModel.getModel().setBarangayID("");
                                    mViewModel.getModel().setBarangayName("");
                                    mViewModel.getModel().setMunicipalID("");
                                    mViewModel.getModel().setMunicipalNm("");
                                    mViewModel.getModel().setHouseNox("");
                                    mViewModel.getModel().setLandMark("");
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        mViewModel.GetTownProvinceList().observe(Activity_SpouseResidenceInfo.this, new Observer<List<DTownInfo.TownProvinceInfo>>() {
            @Override
            public void onChanged(List<DTownInfo.TownProvinceInfo> loList) {
                try {
                    ArrayList<String> strings = new ArrayList<>();
                    for (int x = 0; x < loList.size(); x++) {
                        String lsTown = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                        strings.add(lsTown);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_SpouseResidenceInfo.this,
                            android.R.layout.simple_spinner_dropdown_item, strings.toArray(new String[0]));
                    txtTown.setAdapter(adapter);
                    txtTown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            for (int x = 0; x < loList.size(); x++) {
                                String lsLabel = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                                String lsSlctd = txtTown.getText().toString().trim();
                                if (lsSlctd.equalsIgnoreCase(lsLabel)) {
                                    mViewModel.getModel().setMunicipalID(loList.get(x).sTownIDxx);
                                    mViewModel.getModel().setMunicipalNm(lsLabel);
                                }
                            }

                            mViewModel.GetBarangayList(mViewModel.getModel().getMunicipalID()).observe(Activity_SpouseResidenceInfo.this, new Observer<List<EBarangayInfo>>() {
                                @Override
                                public void onChanged(List<EBarangayInfo> BrgyList) {
                                    ArrayList<String> string = new ArrayList<>();
                                    for (int x = 0; x < BrgyList.size(); x++) {
                                        String lsBrgy = BrgyList.get(x).getBrgyName();
                                        string.add(lsBrgy);
                                    }
                                    ArrayAdapter<String> adapters = new ArrayAdapter<>(Activity_SpouseResidenceInfo.this,
                                            android.R.layout.simple_spinner_dropdown_item, string.toArray(new String[0]));
                                    txtBarangay.setAdapter(adapters);
                                    txtBarangay.setOnItemClickListener((parent1, view1, position1, id1) -> {
                                        for (int x = 0; x < BrgyList.size(); x++) {
                                            String lsLabel = BrgyList.get(x).getBrgyName();
                                            String lsSlctd = txtBarangay.getText().toString().trim();
                                            if (lsSlctd.equalsIgnoreCase(lsLabel)) {
                                                mViewModel.getModel().setBarangayID(BrgyList.get(x).getBrgyIDxx());
                                                mViewModel.getModel().setBarangayName(lsLabel);
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


        btnNext.setOnClickListener(v -> SaveSpouseResidenceInfo());
        btnPrvs.setOnClickListener(v -> {
            returnPrevious();
        });
    }

    private void SaveSpouseResidenceInfo() {

        mViewModel.getModel().setLandMark(txtLandMark.getText().toString().trim());
        mViewModel.getModel().setHouseNox(txtHouseNox.getText().toString().trim());
        mViewModel.getModel().setAddress1(txtAddress1.getText().toString().trim());
        mViewModel.getModel().setAddress2(txtAddress2.getText().toString().trim());

        mViewModel.SaveData(new OnSaveInfoListener() {
            @Override
            public void OnSave(String args) {
                Intent loIntent = new Intent(Activity_SpouseResidenceInfo.this, Activity_SpouseEmploymentInfo.class);
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
        toolbar = findViewById(R.id.toolbar_SpouseResidenceInfo);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        txtLandMark = findViewById(R.id.txt_landmark);
        txtHouseNox = findViewById(R.id.txt_houseNox);
        txtAddress1 = findViewById(R.id.txt_address);
        txtAddress2 = findViewById(R.id.txt_address2);
        txtTown = findViewById(R.id.txt_town);
        txtBarangay = findViewById(R.id.txt_barangay);
        cbSameAdd = findViewById(R.id.cb_sameAdd);

        btnNext = findViewById(R.id.btn_creditAppNext);
        btnPrvs = findViewById(R.id.btn_creditAppPrvs);
    }

    @SuppressLint("NewApi")
    public void setUpFieldsFromLocalDB(SpouseResidence infoModel) throws JSONException {
        if(infoModel != null) {

            if(!"".equalsIgnoreCase(infoModel.getLandMark())){
                txtLandMark.setText(infoModel.getLandMark());
            }
            if(!"".equalsIgnoreCase(infoModel.getHouseNox())){
                txtHouseNox.setText(infoModel.getHouseNox());
            }

            if(!"".equalsIgnoreCase(infoModel.getAddress1())){
                txtAddress1.setText(infoModel.getAddress1());

            }
            if(!"".equalsIgnoreCase(infoModel.getAddress2())){
                txtAddress2.setText(infoModel.getAddress2());
            }

            if(!"".equalsIgnoreCase(infoModel.getMunicipalID())) {
                txtTown.setText(infoModel.getMunicipalNm());
                mViewModel.getModel().setMunicipalID(infoModel.getMunicipalID());
                mViewModel.getModel().setMunicipalNm(infoModel.getMunicipalNm());
            }

            if(!"".equalsIgnoreCase(infoModel.getBarangayID())) {
                txtBarangay.setText(infoModel.getBarangayName());
                mViewModel.getModel().setBarangayID(infoModel.getBarangayID());
                mViewModel.getModel().setBarangayName(infoModel.getBarangayName());
            }


        }else{
            txtLandMark.getText().clear();
            txtHouseNox.getText().clear();
            txtAddress1.getText().clear();
            txtAddress2.getText().clear();
            txtBarangay.getText().clear();
            txtTown.getText().clear();

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
        Intent loIntent = new Intent(Activity_SpouseResidenceInfo.this, Activity_SpouseInfo.class);
        loIntent.putExtra("sTransNox", TransNox);
        startActivity(loIntent);
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
        finish();
    }

}