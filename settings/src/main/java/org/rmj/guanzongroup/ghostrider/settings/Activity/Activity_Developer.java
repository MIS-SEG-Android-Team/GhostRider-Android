/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.settings
 * Electronic Personnel Access Control Security System
 * project file created : 8/4/21 4:50 PM
 * project file last modified : 8/4/21 4:50 PM
 */

package org.rmj.guanzongroup.ghostrider.settings.Activity;

import static org.rmj.g3appdriver.GCircle.Etc.DeptCode.Departments;
import static org.rmj.g3appdriver.GCircle.Etc.PositionCode.Positions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.GCircle.Etc.PositionCode;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.settings.R;
import org.rmj.guanzongroup.ghostrider.settings.ViewModel.VMDevMode;

import java.util.Objects;

public class Activity_Developer extends AppCompatActivity {

    private VMDevMode mViewModel;

    private Toolbar toolbar;

    private Spinner spnLevl;
    private AutoCompleteTextView txtDept, txtPost;
    private MaterialButton btnSave, btnRestore;
    private SwitchMaterial smTestMode;
    private TextInputLayout til_ipadd;
    private TextInputEditText tie_ipadd;

   private LinearLayout linear_layout3;
    private LinearLayout  linear_layout4;

    private EEmployeeInfo poInfo;
    private MessageBox poMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_developer);

        mViewModel = new ViewModelProvider(this).get(VMDevMode.class);
        poMessage = new MessageBox(this);

        initViews();
        initAdapter();
        initListener();
        initObservables();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews(){

        toolbar = findViewById(R.id.toolbar_deveMode);
        spnLevl = findViewById(R.id.spn_employeeLevel);
        btnRestore = findViewById(R.id.btn_restoreDefault);
        txtDept = findViewById(R.id.txt_department);
        txtPost = findViewById(R.id.txt_position);
        btnSave = findViewById(R.id.btn_Save);
        smTestMode = findViewById(R.id.sm_TestMode);
        til_ipadd = findViewById(R.id.til_ipadd);
        tie_ipadd = findViewById(R.id.tie_ipadd);

        linear_layout3 = findViewById(R.id.linear_layout3);
        linear_layout4 = findViewById(R.id.linear_layout4);

    }
    private void initAdapter(){

        spnLevl.setAdapter(new ArrayAdapter<>(Activity_Developer.this, android.R.layout.simple_dropdown_item_1line, DeptCode.Employee_Levels));

        txtDept.setAdapter(new ArrayAdapter<>(Activity_Developer.this, android.R.layout.simple_spinner_dropdown_item, Departments));

        txtPost.setAdapter(new ArrayAdapter<>(Activity_Developer.this, android.R.layout.simple_spinner_dropdown_item, Positions));
    }
    private void initListener(){

        txtDept.setOnItemClickListener((parent, view, position, id) -> poInfo.setDeptIDxx(DeptCode.getDepartmentCode(txtDept.getText().toString())));
        txtPost.setOnItemClickListener((parent, view, position, id) -> poInfo.setPositnID(PositionCode.getPositionCode(txtPost.getText().toString())));
        spnLevl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                poInfo.setEmpLevID(spnLevl.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSave.setOnClickListener(v -> {

            initMessage(3, "Are you sure you want to save changes?", new onMessageButton() {
                @Override
                public void onPositive() {

                    String ipAdd = "https://restgk.guanzongroup.com.ph/";
                    if (tie_ipadd.getText() != null && !tie_ipadd.getText().toString().isEmpty()){
                        ipAdd = tie_ipadd.getText().toString();
                    }

                    btnSave.setEnabled(false);
                    btnRestore.setEnabled(false);

                    mViewModel.SaveChanges(poInfo, ipAdd, new VMDevMode.OnChangeListener() {
                        @Override
                        public void OnChanged(String args, Boolean isSuccess) {

                            if (isSuccess){
                                initMessage(1, "Changes has been saved.\nLogout to take this effect.", new onMessageButton() {
                                    @Override
                                    public void onPositive() {

                                        new EmployeeMaster(getApplication()).LogoutUserSession();
                                        AppConfigPreference.getInstance(getApplication()).setIsAppFirstLaunch(false);

                                        System.exit(0);

                                        btnSave.setEnabled(true);
                                        btnRestore.setEnabled(true);
                                    }

                                    @Override
                                    public void onNegative() {
                                        btnSave.setEnabled(true);
                                        btnRestore.setEnabled(true);
                                    }
                                });

                                return;
                            }

                            initMessage(2, args, new onMessageButton() {
                                @Override
                                public void onPositive() {
                                    btnSave.setEnabled(true);
                                    btnRestore.setEnabled(true);
                                }

                                @Override
                                public void onNegative() {
                                    btnSave.setEnabled(true);
                                    btnRestore.setEnabled(true);
                                }
                            });

                        }
                    });

                }

                @Override
                public void onNegative() {}
            });
        });

        btnRestore.setOnClickListener(v -> {

            initMessage(3, "Restore to default settings?", new onMessageButton() {
                @Override
                public void onPositive() {

                    mViewModel.RestoreDefault("https://restgk.guanzongroup.com.ph/", new VMDevMode.OnChangeListener() {
                        @Override
                        public void OnChanged(String args, Boolean isSuccess) {

                            Toast.makeText(Activity_Developer.this, args, Toast.LENGTH_SHORT).show();

                            if (isSuccess){
                                initMessage(1, "Changes has been saved.\nAccount will be automatically logout. Please restart the application ", new onMessageButton() {
                                    @Override
                                    public void onPositive() {

                                        new EmployeeMaster(getApplication()).LogoutUserSession();
                                        AppConfigPreference.getInstance(getApplication()).setIsAppFirstLaunch(false);

                                        System.exit(0);
                                    }

                                    @Override
                                    public void onNegative() {

                                    }
                                });

                                return;
                            }

                            initMessage(2, "Cannot restore default settings", new onMessageButton() {
                                @Override
                                public void onPositive() {}

                                @Override
                                public void onNegative() {

                                }
                            });

                        }
                    });

                }

                @Override
                public void onNegative() {}
            });
        });

        smTestMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    til_ipadd.setVisibility(View.VISIBLE);
                    return;
                }

                til_ipadd.setVisibility(View.GONE);
            }
        });
    }
    private void initObservables(){

        mViewModel.GetUserInfo().observe(Activity_Developer.this, info -> {
            try{
                poInfo = info;
                for(int x = 0; x < DeptCode.Employee_Levels.length; x++){
                    if(DeptCode.parseUserLevel(info.getEmpLevID()).equalsIgnoreCase(DeptCode.Employee_Levels[x])){
                        spnLevl.setSelection(x);
                        break;
                    }
                }

                txtDept.setText(DeptCode.getDepartmentName(info.getDeptIDxx()));
                txtPost.setText(PositionCode.getPositionName(info.getPositnID()));

                if (info.getDeptIDxx().equalsIgnoreCase("026")){
                    linear_layout3.setVisibility(View.VISIBLE);
                    linear_layout4.setVisibility(View.VISIBLE);
                    return;
                }

                linear_layout3.setVisibility(View.GONE);
                linear_layout4.setVisibility(View.GONE);

            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    private void initMessage(int mode, String message, onMessageButton callback){

        poMessage.initDialog();
        poMessage.setTitle("Guanzon Circle");
        poMessage.setMessage(message);

        switch (mode){

            case 1, 2:
                if (mode == 1){
                    poMessage.setIcon(R.drawable.baseline_message_24);
                }

                if (mode == 2){
                    poMessage.setIcon(R.drawable.baseline_error_24);
                }

                poMessage.setPositiveButton("Okay", new MessageBox.DialogButton() {
                    @Override
                    public void OnButtonClick(View view, AlertDialog dialog) {
                        dialog.dismiss();
                        callback.onPositive();
                    }
                });

                break;

            case 3:
                poMessage.setIcon(R.drawable.baseline_contact_support_24);
                poMessage.setPositiveButton("Yes", new MessageBox.DialogButton() {
                    @Override
                    public void OnButtonClick(View view, AlertDialog dialog) {
                        dialog.dismiss();
                        callback.onPositive();
                    }
                });
                poMessage.setNegativeButton("No", new MessageBox.DialogButton() {
                    @Override
                    public void OnButtonClick(View view, AlertDialog dialog) {
                        dialog.dismiss();
                        callback.onNegative();
                    }
                });

                break;
        }

        poMessage.show();
    }
    private interface onMessageButton{
        void onPositive();
        void onNegative();
    }
}