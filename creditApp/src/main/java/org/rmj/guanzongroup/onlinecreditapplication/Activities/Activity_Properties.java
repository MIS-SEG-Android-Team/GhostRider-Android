package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import  com.google.android.material.checkbox.MaterialCheckBox;


import org.json.JSONException;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.Properties;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.OnParseListener;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMProperties;

import java.util.Objects;

public class Activity_Properties extends AppCompatActivity {
    private VMProperties mViewModel;
    private MessageBox poMessage;
    private TextInputEditText txtLot1, txtLot2, txtLot3;
    private MaterialCheckBox cb4Wheels, cb3Wheels, cb2Wheels, cbAircon, cbRefxx, cbTelevsn;
    private MaterialButton btnPrvs, btnNext;
    private MaterialToolbar toolbar;

    private String TransNox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_properties);

        mViewModel = new ViewModelProvider(Activity_Properties.this).get(VMProperties.class);
        poMessage = new MessageBox(Activity_Properties.this);

        initWidgets();

        mViewModel.InitializeApplication(getIntent());
        mViewModel.GetApplication().observe(Activity_Properties.this, app -> {
            try {
                TransNox = app.getTransNox();
                mViewModel.getModel().setTransNox(app.getTransNox());
                mViewModel.ParseData(app, new OnParseListener() {
                    @Override
                    public void OnParse(Object args) {
                        Properties loDetail = (Properties) args;
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

        cb4Wheels.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (cb4Wheels.isChecked()) {
                mViewModel.getModel().setPs4Wheelsx("1");
            } else {
                mViewModel.getModel().setPs4Wheelsx("0");
            }
        });

        cb3Wheels.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (cb3Wheels.isChecked()) {
                mViewModel.getModel().setPs3Wheelsx("1");
            } else {
                mViewModel.getModel().setPs3Wheelsx("0");
            }
        });

        cb2Wheels.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (cb2Wheels.isChecked()) {
                mViewModel.getModel().setPs2Wheelsx("1");
            } else {
                mViewModel.getModel().setPs2Wheelsx("0");
            }
        });

        cbAircon.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (cbAircon.isChecked()) {
                mViewModel.getModel().setPsAirConxx("1");
            } else {
                mViewModel.getModel().setPsAirConxx("0");
            }
        });

        cbRefxx.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (cbRefxx.isChecked()) {
                mViewModel.getModel().setPsFridgexx("1");
            } else {
                mViewModel.getModel().setPsFridgexx("0");
            }
        });

        cbTelevsn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (cbTelevsn.isChecked()) {
                mViewModel.getModel().setPsTelevsnx("1");
            } else {
                mViewModel.getModel().setPsTelevsnx("0");
            }
        });


        btnNext.setOnClickListener(v -> SavePropertiesInfo());
        btnPrvs.setOnClickListener(v -> {
            returnPrevious();
        });

    }

    private void SavePropertiesInfo() {
        mViewModel.getModel().setPsLot1Addx(txtLot1.getText().toString().trim());
        mViewModel.getModel().setPsLot2Addx(txtLot2.getText().toString().trim());
        mViewModel.getModel().setPsLot3Addx(txtLot3.getText().toString().trim());

        mViewModel.SaveData(new OnSaveInfoListener() {
            @Override
            public void OnSave(String args) {
                Intent loIntent = new Intent(Activity_Properties.this, Activity_OtherInfo.class);
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

        toolbar = findViewById(R.id.toolbar_Properties);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        txtLot1 = findViewById(R.id.tie_cap_propertyLot1);
        txtLot2 = findViewById(R.id.tie_cap_propertyLot2);
        txtLot3 = findViewById(R.id.tie_cap_propertyLot3);

        cb4Wheels = findViewById(R.id.cb_4Wheels);
        cb3Wheels = findViewById(R.id.cb_3Wheels);
        cb2Wheels = findViewById(R.id.cb_2Wheels);
        cbAircon = findViewById(R.id.cb_Aircon);
        cbRefxx = findViewById(R.id.cb_refrigerator);
        cbTelevsn = findViewById(R.id.cb_television);

        btnPrvs = findViewById(R.id.btn_creditAppPrvs);
        btnNext = findViewById(R.id.btn_creditAppNext);
    }

    @SuppressLint("NewApi")
    public void setUpFieldsFromLocalDB(Properties infoModel) throws JSONException {
        if(infoModel != null) {
            txtLot1.setText(!"".equalsIgnoreCase(String.valueOf(infoModel.getPsLot1Addx())) ? String.valueOf(infoModel.getPsLot1Addx()) : "");
            txtLot2.setText(!"".equalsIgnoreCase(String.valueOf(infoModel.getPsLot2Addx())) ? String.valueOf(infoModel.getPsLot2Addx()) : "");
            txtLot3.setText(!"".equalsIgnoreCase(String.valueOf(infoModel.getPsLot3Addx())) ? String.valueOf(infoModel.getPsLot3Addx()) : "");
            if(infoModel.getPs4Wheelsx().equalsIgnoreCase("1")) {
                mViewModel.getModel().setPs4Wheelsx("1");
                cb4Wheels.setChecked(true);
            }
            if(infoModel.getPs3Wheelsx().equalsIgnoreCase("1")) {
                mViewModel.getModel().setPs3Wheelsx("1");
                cb3Wheels.setChecked(true);
            }
            if(infoModel.getPs2Wheelsx().equalsIgnoreCase("1")) {
                mViewModel.getModel().setPs2Wheelsx("1");
                cb2Wheels.setChecked(true);
            }
            if(infoModel.getPsAirConxx().equalsIgnoreCase("1")){
                mViewModel.getModel().setPsAirConxx("1");
                cbAircon.setChecked(true);
            }

            if(infoModel.getPsFridgexx().equalsIgnoreCase("1")){
                mViewModel.getModel().setPsFridgexx("1");
                cbRefxx.setChecked(true);
            }
            if(infoModel.getPsTelevsnx().equalsIgnoreCase("1")){
                mViewModel.getModel().setPsTelevsnx("1");
                cbTelevsn.setChecked(true);
            }

        } else {
            txtLot1.getText().clear();
            txtLot2.getText().clear();
            txtLot3.getText().clear();
            cb4Wheels.setChecked(false);
            cb3Wheels.setChecked(false);
            cb2Wheels.setChecked(false);
            cbAircon.setChecked(false);
            cbRefxx.setChecked(false);
            cbTelevsn.setChecked(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent loIntent = new Intent(Activity_Properties.this, Activity_DisbursementInfo.class);
            loIntent.putExtra("sTransNox", TransNox);
            startActivity(loIntent);
            finish();
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
        Intent loIntent = new Intent(Activity_Properties.this, Activity_Dependent.class);
        loIntent.putExtra("sTransNox", TransNox);
        startActivity(loIntent);
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
        finish();
    }
}