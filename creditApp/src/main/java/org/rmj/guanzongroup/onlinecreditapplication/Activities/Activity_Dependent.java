package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import  com.google.android.material.checkbox.MaterialCheckBox;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.Dependent;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppConstants;
import org.rmj.guanzongroup.onlinecreditapplication.Adapter.DependentAdapter;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.OnParseListener;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMDependent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity_Dependent extends AppCompatActivity {

    private VMDependent mViewModel;
    private MessageBox poMessage;
    private Dependent.DependentInfo poDpndt;

    private TextInputEditText tieFullname, tieDpdAgexx, tieSchoolNm, tieSchlAddx;
    private MaterialAutoCompleteTextView tieSchlTown;
    private TextInputEditText tieCompName;
    private LinearLayout linearStudent,
                        linearEmployd;
    private RecyclerView recyclerView;

    private MaterialAutoCompleteTextView actRelationx,
                                actSchoolLvl;
    private RadioGroup rgSchoolTpe, rgEmpSctr;
    private MaterialRadioButton rbScPblc,
            rbScPrvt,
            rbEmPblc,
            rbEmPrvt,
            rbEmOFW;

    private MaterialButton btnAddDependent;
    private MaterialCheckBox cbStudent,
                    cbEmployee,
                    cbScholarxx,
                    cbDependent,
                    cbHouseHold,
                    cbIsMarried;
    private MaterialButton btnAdd,
                    btnPrev,
                    btnNext;
    private MaterialToolbar toolbar;

    private String TransNox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_Dependent.this).get(VMDependent.class);
        poMessage = new MessageBox(Activity_Dependent.this);
        poDpndt = new Dependent.DependentInfo();
        setContentView(R.layout.activity_dependent);
        initWidgets();
        mViewModel.InitializeApplication(getIntent());
        mViewModel.GetApplication().observe(Activity_Dependent.this, app -> {
            try {
                TransNox = app.getTransNox();
                mViewModel.getModel().setTransNox(app.getTransNox());
                mViewModel.ParseData(app, new OnParseListener() {
                    @Override
                    public void OnParse(Object args) {
                        Dependent loDetail = (Dependent) args;
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

        mViewModel.GetDependents().observe(Activity_Dependent.this, dependentInfos -> {
            try{
                LinearLayoutManager loLayout = new LinearLayoutManager(Activity_Dependent.this);
                loLayout.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(loLayout);
                recyclerView.setAdapter(new DependentAdapter(dependentInfos, args -> mViewModel.removeDependent(args)));
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        btnAddDependent.setOnClickListener(v -> AddDependent());
        btnNext.setOnClickListener(v -> SaveDependentInfo());
        btnPrev.setOnClickListener(v -> returnPrevious());
    }

    private void AddDependent(){
        poDpndt.setFullName(tieFullname.getText().toString());
        int lnInptAge = 0;
        if(!tieDpdAgexx.getText().toString().trim().isEmpty()){
            lnInptAge = Integer.parseInt(tieDpdAgexx.getText().toString());
        }
        poDpndt.setDpdntAge(lnInptAge);
        poDpndt.setSchoolNm(tieSchoolNm.getText().toString());
        poDpndt.setSchlAddx(tieSchlAddx.getText().toString());
        poDpndt.setCompName(tieCompName.getText().toString());
        mViewModel.addDependent(poDpndt, new VMDependent.OnAddDependetListener() {
            @Override
            public void OnAdd(String args) {
                Toast.makeText(Activity_Dependent.this, args, Toast.LENGTH_SHORT).show();
                poDpndt = new Dependent.DependentInfo();
                tieFullname.setText("");
                tieDpdAgexx.setText("");
                tieSchoolNm.setText("");
                tieSchlAddx.setText("");
                tieSchlTown.setText("");
                tieCompName.setText("");
                actRelationx.setText("");
                actSchoolLvl.setText("");
                cbStudent.setChecked(false);
                cbEmployee.setChecked(false);
                rbScPblc.setChecked(false);
                rbScPrvt.setChecked(false);
                rbEmPblc.setChecked(false);
                rbEmPrvt.setChecked(false);
                rbEmOFW.setChecked(false);
                cbDependent.setChecked(false);
                cbHouseHold.setChecked(false);
                cbIsMarried.setChecked(false);
            }

            @Override
            public void OnFailed(String message) {
                Toast.makeText(Activity_Dependent.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SaveDependentInfo() {
        mViewModel.SaveData(new OnSaveInfoListener() {
            @Override
            public void OnSave(String args) {
                Intent loIntent = new Intent(Activity_Dependent.this, Activity_Properties.class);
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


    @SuppressLint("WrongViewCast")
    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar_Dependent);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        tieFullname = findViewById(R.id.tie_cap_dpdFullname);
        tieDpdAgexx = findViewById(R.id.tie_cap_dpdAge);
        tieCompName = findViewById(R.id.tie_cap_dpdCompanyName);
        tieSchoolNm = findViewById(R.id.tie_cap_dpdSchoolName);
        tieSchlAddx = findViewById(R.id.tie_cap_dpdSchoolAddress);
        tieSchlTown = findViewById(R.id.tie_cap_dpdSchoolTown);

        actRelationx = findViewById(R.id.spinner_cap_dpdRelation);

        rgSchoolTpe = findViewById(R.id.rg_schoolType);
        rgEmpSctr = findViewById(R.id.rg_empSector);
        actSchoolLvl = findViewById(R.id.spinner_cap_educLevel);

        linearEmployd = findViewById(R.id.linearEmployd);
        linearStudent = findViewById(R.id.linearStudent);

        cbDependent = findViewById(R.id.cb_cap_Dependent);
        cbHouseHold = findViewById(R.id.cb_cap_HouseHold);
        cbIsMarried = findViewById(R.id.cb_cap_Married);
        cbScholarxx = findViewById(R.id.cb_cap_dpdScholar);
        cbStudent = findViewById(R.id.cbStudent);
        cbEmployee = findViewById(R.id.cbEmployee);

        rbScPblc = findViewById(R.id.rb_public);
        rbScPrvt = findViewById(R.id.rb_private);
        rbEmPblc = findViewById(R.id.rb_empPublic);
        rbEmPrvt = findViewById(R.id.rb_empPrivate);
        rbEmOFW = findViewById(R.id.rb_empOFW);

        recyclerView = findViewById(R.id.recyclerview_dependencies);

        btnAddDependent = findViewById(R.id.btn_dpd_add);
        btnNext = findViewById(R.id.btn_creditAppNext);
        btnPrev = findViewById(R.id.btn_creditAppPrvs);

        cbStudent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                findViewById(R.id.linearStudent).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.linearStudent).setVisibility(View.GONE);
            }
        });

        cbEmployee.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                findViewById(R.id.linearEmployd).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.linearEmployd).setVisibility(View.GONE);
            }
        });

        actRelationx.setAdapter(new ArrayAdapter<>(Activity_Dependent.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.DEPENDENT_RELATIONSHIP));
        actRelationx.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    poDpndt.setRelation("0");
                    break;
                case 1:
                    poDpndt.setRelation("1");
                    break;
                case 2:
                    poDpndt.setRelation("2");
                    break;
                case 3:
                    poDpndt.setRelation("3");
                    break;
            }
        });

        rgSchoolTpe.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.rb_public){
                poDpndt.setSchoolTp("0");
            } else {
                poDpndt.setSchoolTp("1");
            }
        });

        actSchoolLvl.setAdapter(new ArrayAdapter<>(Activity_Dependent.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.SCHOOL_LEVEL));
        actSchoolLvl.setOnItemClickListener((parent, view, position, id) -> {
            switch (position){
                case 0:
                    poDpndt.setEduLevel("0");
                    break;
                case 1:
                    poDpndt.setEduLevel("1");
                    break;
                case 2:
                    poDpndt.setEduLevel("2");
                    break;
                case 3:
                    poDpndt.setEduLevel("3");
                    break;
                case 4:
                    poDpndt.setEduLevel("4");
                    break;
            }
        });

        rgEmpSctr.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.rb_empPublic){
                poDpndt.setEmpSctor("0");
            } else if(checkedId == R.id.rb_private){
                poDpndt.setEmpSctor("1");
            } else {
                poDpndt.setEmpSctor("2");
            }
        });

        cbDependent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                poDpndt.setDependnt("1");
            } else {
                poDpndt.setDependnt("0");
            }
        });

        cbHouseHold.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!isChecked){
                poDpndt.setHouseHld("0");
            } else {
                poDpndt.setHouseHld("1");
            }
        });

        cbScholarxx.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!isChecked){
                poDpndt.setSchoolar("0");
            } else {
                poDpndt.setSchoolar("1");
            }
        });

        cbIsMarried.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!isChecked){
                poDpndt.setMarriedx("0");
            } else {
                poDpndt.setMarriedx("1");
            }
        });

        mViewModel.GetTownProvince().observe(Activity_Dependent.this, town -> {
            try{
                ArrayList<String> loList = new ArrayList<>();
                for (int x = 0; x < town.size(); x++) {
                    String lsTown = town.get(x).sTownName + ", " + town.get(x).sProvName;
                    loList.add(lsTown);
                }

                ArrayAdapter<String> loAdapter = new ArrayAdapter<>(Activity_Dependent.this, android.R.layout.simple_spinner_dropdown_item, loList.toArray(new String[0]));
                tieSchlTown.setAdapter(loAdapter);
                tieSchlTown.setOnItemClickListener((parent, view, position, id) -> {
                    for (int x = 0; x < town.size(); x++) {
                        String lsLabel = town.get(x).sTownName + ", " + town.get(x).sProvName;
                        String lsSlctd = tieSchlTown.getText().toString().trim();
                        if (lsSlctd.equalsIgnoreCase(lsLabel)) {
                            poDpndt.setSchlTown(town.get(x).sTownIDxx);
                            break;
                        }

                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public void setUpFieldsFromLocalDB(Dependent infoModel) throws JSONException {
        if(infoModel != null) {
            if(infoModel.getDependentList().size() > 0) {
                mViewModel.setDependent(infoModel.getDependentList());
                List<Dependent.DependentInfo> poDependnt = new ArrayList<>();
//                for (int x = 0; x < infoModel.getDependentList().size(); x++) {
//                    Dependent.DependentInfo loDependnt = infoModel.getDependentList().get(x);
//                    if (!loDependnt.isDataValid()) {
//                        poDependnt.remove(x);
//                    } else {
//                        poDependnt.add(loDependnt);
//                    }
//                }
            }
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
        Intent loIntent = new Intent(Activity_Dependent.this, Activity_DisbursementInfo.class);
        loIntent.putExtra("sTransNox", TransNox);
        startActivity(loIntent);
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
        finish();
    }
}