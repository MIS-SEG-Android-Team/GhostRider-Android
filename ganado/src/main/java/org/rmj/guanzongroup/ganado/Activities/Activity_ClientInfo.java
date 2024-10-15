package org.rmj.guanzongroup.ganado.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DTownInfo;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ganado.R;
import org.rmj.guanzongroup.ganado.ViewModel.VMPersonalInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Activity_ClientInfo extends AppCompatActivity {
    private VMPersonalInfo mViewModel;
    private MessageBox poMessage;
    private LoadDialog poDialogx;
    private TextInputEditText txtLastNm, txtFrstNm, txtMiddNm, txtSuffixx,  txtBirthDt,
            txtEmailAdd, txtMobileNo,  txtHouseNox, txtAddress;
    private MaterialAutoCompleteTextView txtMunicipl,txtBPlace;
    private RadioGroup rgGender;
    private MaterialAutoCompleteTextView spinner_relation;
    private MaterialButton btnContinue;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_client_info);

        initWidgets();

        poMessage = new MessageBox(Activity_ClientInfo.this);
        poDialogx = new LoadDialog(Activity_ClientInfo.this);

        mViewModel.InitializeApplication(getIntent());

        mViewModel.InitLocation();

        mViewModel.getRelation().observe(Activity_ClientInfo.this, eRelations->{
            try {
                ArrayList<String> string = new ArrayList<>();
                for (int x = 0; x < eRelations.size(); x++) {
                    String lsColor = eRelations.get(x).getRelatnDs();
                    string.add(lsColor);

                }
                ArrayAdapter<String> adapters = new ArrayAdapter<>(Activity_ClientInfo.this, android.R.layout.simple_spinner_dropdown_item, string.toArray(new String[0]));
                spinner_relation.setText(eRelations.get(0).getRelatnDs());
                spinner_relation.setText(eRelations.get(0).getRelatnDs());
                mViewModel.getModel().setsReltionx(eRelations.get(0).getRelatnID());
                spinner_relation.setAdapter(adapters);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        spinner_relation.setOnItemClickListener(new Activity_ClientInfo.OnItemClickListener(spinner_relation));

        mViewModel.GetTownProvinceList().observe(Activity_ClientInfo.this, new Observer<List<DTownInfo.TownProvinceInfo>>() {
            @Override
            public void onChanged(List<DTownInfo.TownProvinceInfo> loList) {
                try {
                    ArrayList<String> string = new ArrayList<>();
                    for (int x = 0; x < loList.size(); x++) {
                        String lsTown = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                        string.add(lsTown);

                    }
                    ArrayAdapter<String> adapters = new ArrayAdapter<>(Activity_ClientInfo.this, android.R.layout.simple_spinner_dropdown_item, string.toArray(new String[0]));
                    txtBPlace.setAdapter(adapters);
                    txtBPlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            for (int x = 0; x < loList.size(); x++) {
                                String lsLabel = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                                String lsSlctd = txtBPlace.getText().toString().trim();
                                if (lsSlctd.equalsIgnoreCase(lsLabel)) {
                                    mViewModel.getModel().setBirthPlc(loList.get(x).sTownIDxx);
                                    mViewModel.getModel().setsBirthIDx(lsLabel);
                                    break;
                                }

                            }
                        }
                    });
                    ArrayAdapter<String> adapters1 = new ArrayAdapter<>(Activity_ClientInfo.this, android.R.layout.simple_spinner_dropdown_item, string.toArray(new String[0]));

                    txtMunicipl.setAdapter(adapters1);
                    txtMunicipl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            for (int x = 0; x < loList.size(); x++) {
                                String lsLabel = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                                String lsSlctd = txtMunicipl.getText().toString().trim();
                                if (lsSlctd.equalsIgnoreCase(lsLabel)) {
                                    mViewModel.getModel().setTownIDxx(loList.get(x).sTownIDxx);
                                    mViewModel.getModel().setsMncplNme(lsLabel);
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

        txtBirthDt.setOnClickListener(v -> {
            final Calendar newCalendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
            final DatePickerDialog StartTime = new DatePickerDialog(Activity_ClientInfo.this,
                    android.R.style.Theme_Holo_Dialog, (view131, year, monthOfYear, dayOfMonth) -> {
                try {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    String lsDate = dateFormatter.format(newDate.getTime());
                    txtBirthDt.setText(lsDate);
                    Date loDate = new SimpleDateFormat("MMMM dd, yyyy").parse(lsDate);
                    lsDate = new SimpleDateFormat("yyyy-MM-dd").format(loDate);
                    mViewModel.getModel().setBirthDte(lsDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            StartTime.getDatePicker().setMaxDate(new Date().getTime());
            StartTime.show();
        });

        rgGender.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.rb_male) {
                mViewModel.getModel().setGenderCd("0");
            }
            if (i == R.id.rb_female) {
                mViewModel.getModel().setGenderCd("1");
            }
        });

        btnContinue.setOnClickListener(v ->{

            mViewModel.InitLocation();

            mViewModel.getModel().setFrstName(txtFrstNm.getText().toString());
            mViewModel.getModel().setMiddName(txtMiddNm.getText().toString());
            mViewModel.getModel().setLastName(txtLastNm.getText().toString());
            mViewModel.getModel().setSuffixNm(txtSuffixx.getText().toString());
            mViewModel.getModel().setHouseNox(txtHouseNox.getText().toString());
            mViewModel.getModel().setAddressx(txtAddress.getText().toString());
            mViewModel.getModel().setEmailAdd(txtEmailAdd.getText().toString());
            mViewModel.getModel().setMobileNo(txtMobileNo.getText().toString());

            if (!mViewModel.InitLocation()){
                poMessage.initDialog();
                poMessage.setTitle("BENTA");
                poMessage.setMessage(mViewModel.GetMessage());
                poMessage.setPositiveButton("Dismiss", (view, dialog) -> {
                    dialog.dismiss();
                });
                poMessage.show();
            }else {
                mViewModel.SaveData(new VMPersonalInfo.OnSaveInquiry() {
                    @Override
                    public void OnSave() {
                        poDialogx.initDialog("BENTA", "Saving inquiry. Please wait...", false);
                        poDialogx.show();
                    }
                    @Override
                    public void OnSuccess(String args) {
                        poDialogx.dismiss();

                        poMessage.initDialog();
                        poMessage.setTitle("BENTA");
                        poMessage.setMessage(args);
                        poMessage.setPositiveButton("Okay", (view, dialog) -> {
                            dialog.dismiss();
                            Intent loIntent = new Intent(Activity_ClientInfo.this, Activity_Inquiries.class);
                            loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(loIntent);
                            overridePendingTransition(org.rmj.g3appdriver.R.anim.anim_intent_slide_in_right, org.rmj.g3appdriver.R.anim.anim_intent_slide_out_left);

                            finish();
                        });
                        poMessage.show();
                    }
                    @Override
                    public void OnFailed(String message) {
                        poDialogx.dismiss();

                        poMessage.initDialog();
                        poMessage.setTitle("BENTA");
                        poMessage.setMessage(message);
                        poMessage.setPositiveButton("Okay", (view1, dialog) -> dialog.dismiss());
                        poMessage.show();
                    }
                });
            }
        });
    }
    private void initWidgets() {

        toolbar = findViewById(R.id.toolbar_PersonalInfo);
        mViewModel = new ViewModelProvider(Activity_ClientInfo.this).get(VMPersonalInfo.class);
        poMessage = new MessageBox(Activity_ClientInfo.this);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        txtLastNm = findViewById(R.id.txt_lastname);
        txtFrstNm = findViewById(R.id.txt_firstname);
        txtMiddNm = findViewById(R.id.txt_middname);
        txtSuffixx = findViewById(R.id.txt_suffix);
        txtBirthDt = findViewById(R.id.txt_birthdate);
        txtBPlace = findViewById(R.id.txt_bpTown);
        rgGender = findViewById(R.id.rg_gender);
        txtMobileNo = findViewById(R.id.txt_mobile);
        txtEmailAdd = findViewById(R.id.txt_emailAdd);
        txtHouseNox = findViewById(R.id.txt_houseNox);
        txtAddress = findViewById(R.id.txt_address);
        txtMunicipl = findViewById(R.id.txt_town);
        spinner_relation = findViewById(R.id.spinner_relation);

        btnContinue = findViewById(R.id.btnContinue);
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private class OnItemClickListener implements AdapterView.OnItemClickListener {

        private final View loView;

        public OnItemClickListener(View loView) {
            this.loView = loView;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            if(loView == spinner_relation){

                mViewModel.getRelation().observe(Activity_ClientInfo.this, relations->{
                    mViewModel.getModel().setsReltionx(relations.get(i).getRelatnID());

                });
            }
        }
    }

}