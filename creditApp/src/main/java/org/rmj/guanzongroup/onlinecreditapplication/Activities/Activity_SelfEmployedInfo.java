package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicantInfo;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppConstants;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.Business;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.OnParseListener;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMBusinessInfo;

import java.util.ArrayList;
import java.util.Objects;

public class Activity_SelfEmployedInfo extends AppCompatActivity {


    private VMBusinessInfo mViewModel;
    private MessageBox poMessage;
    private MaterialAutoCompleteTextView spnBussNtr, spnBussTyp, spnBussSze, spnLngSrvc;

    private TextInputEditText txtBussName, txtBussAdds, txtLnghtSrv, txtMnthlyIn, txtMnthlyEx;
    private MaterialAutoCompleteTextView txtProvnc, txtTownxx;
    private MaterialCheckBox cb_isYear;

    private MaterialButton btnNext, btnPrvs;
    private MaterialToolbar toolbar;

    private String TransNox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_SelfEmployedInfo.this).get(VMBusinessInfo.class);
        poMessage = new MessageBox(Activity_SelfEmployedInfo.this);
        setContentView(R.layout.activity_self_employed_info);
        initWidgets();
        mViewModel.InitializeApplication(getIntent());
        mViewModel.GetApplication().observe(Activity_SelfEmployedInfo.this, new Observer<ECreditApplicantInfo>() {
            @Override
            public void onChanged(ECreditApplicantInfo app) {
                try {
                    TransNox = app.getTransNox();
                    mViewModel.getModel().setTransNox(app.getTransNox());
                    mViewModel.getModel().setMeanInfo(app.getAppMeans());

                    mViewModel.ParseData(app, new OnParseListener() {
                        @Override
                        public void OnParse(Object args) {
                            Business loDetail = (Business) args;
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

        mViewModel.GetTownProvinceList().observe(Activity_SelfEmployedInfo.this, loList -> {
            try {
                ArrayList<String> string = new ArrayList<>();
                for (int x = 0; x < loList.size(); x++) {
                    String lsTown = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
//                        String lsTown =  loList.get(x).sProvName ;
                    string.add(lsTown);
                }

                ArrayAdapter<String> adapters = new ArrayAdapter<>(Activity_SelfEmployedInfo.this, android.R.layout.simple_spinner_dropdown_item, string.toArray(new String[0]));
                txtTownxx.setAdapter(adapters);
                txtTownxx.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        for (int x = 0; x < loList.size(); x++) {
                            String lsLabel = loList.get(x).sTownName + ", " + loList.get(x).sProvName;
                            String lsSlctd = txtTownxx.getText().toString().trim();
                            if (lsSlctd.equalsIgnoreCase(lsLabel)) {
                                mViewModel.getModel().setTown(loList.get(x).sTownIDxx);
                                mViewModel.getModel().setTownName(lsLabel);
                                break;
                            }
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        spnBussNtr.setAdapter(new ArrayAdapter<>(Activity_SelfEmployedInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.BUSINESS_NATURE));
        spnBussNtr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.getModel().setNatureOfBusiness(String.valueOf(position));
            }
        });

        spnBussTyp.setAdapter(new ArrayAdapter<>(Activity_SelfEmployedInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.BUSINESS_TYPE));
        spnBussTyp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.getModel().setTypeOfBusiness(String.valueOf(position));
            }
        });

        spnBussSze.setAdapter(new ArrayAdapter<>(Activity_SelfEmployedInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.BUSINESS_SIZE));
        spnBussSze.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.getModel().setSizeOfBusiness(String.valueOf(position));
            }
        });
        spnLngSrvc.setAdapter(new ArrayAdapter<>(Activity_SelfEmployedInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.LENGTH_OF_STAY));
        spnLngSrvc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.getModel().setIsYear(String.valueOf(position));
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveSelfEmploymentInfo();
            }
        });

        btnPrvs.setOnClickListener(v -> {
            Intent loIntent = new Intent(Activity_SelfEmployedInfo.this, Activity_EmploymentInfo.class);
            loIntent.putExtra("sTransNox", TransNox);
            startActivity(loIntent);
            overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
            finish();
        });
    }

    private void SaveSelfEmploymentInfo() {
        mViewModel.getModel().setNameOfBusiness(txtBussName.getText().toString().trim());
        mViewModel.getModel().setBusinessAddress(txtBussAdds.getText().toString().trim());
//        mViewModel.getModel().setProvince(txtProvnc.getText().toString().trim());
//        mViewModel.getModel().setTown(txtTownxx.getText().toString().trim());

        if (txtLnghtSrv.getText().toString().trim().isEmpty()) {
            mViewModel.getModel().setLengthOfService(0);
        } else {
            mViewModel.getModel().setLengthOfService(Double.parseDouble(txtLnghtSrv.getText().toString().trim()));
        }

        if (txtMnthlyIn.getText().toString().trim().isEmpty()) {
            mViewModel.getModel().setMonthlyIncome(0);
        } else {
            mViewModel.getModel().setMonthlyIncome(Long.parseLong(txtMnthlyIn.getText().toString()));
        }

        if (txtMnthlyEx.getText().toString().isEmpty()) {
            mViewModel.getModel().setMonthlyExpense(0);
        } else {
            mViewModel.getModel().setMonthlyExpense(Long.parseLong(txtMnthlyEx.getText().toString()));
        }

        mViewModel.SaveData(new OnSaveInfoListener() {
            @Override
            public void OnSave(String args) {
                Intent loIntent = new Intent(Activity_SelfEmployedInfo.this, Activity_Finance.class);
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
        toolbar = findViewById(R.id.toolbar_SelfEmployedInfo);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        spnBussNtr = findViewById(R.id.spn_businessNature);
        spnBussTyp = findViewById(R.id.spn_businessType);
        spnBussSze = findViewById(R.id.spn_businessSize);
        spnLngSrvc = findViewById(R.id.spn_lenghtSrvc);

        txtBussName = findViewById(R.id.txt_businessName);
        txtBussAdds = findViewById(R.id.txt_businessAddress);
        txtLnghtSrv = findViewById(R.id.txt_lenghtService);
        txtMnthlyIn = findViewById(R.id.txt_monthlyInc);
        txtMnthlyEx = findViewById(R.id.txt_monthlyExp);
//        txtProvnc = findViewById(R.id.txt_province);
        txtTownxx = findViewById(R.id.txt_town);

        btnPrvs = findViewById(R.id.btn_creditAppPrvs);
        btnNext = findViewById(R.id.btn_creditAppNext);

//        txtMnthlyIn.addTextChangedListener(new FormatUIText.CurrencyFormat(txtMnthlyIn));
//        txtMnthlyEx.addTextChangedListener(new FormatUIText.CurrencyFormat(txtMnthlyEx));

    }


    @SuppressLint("NewApi")
    public void setUpFieldsFromLocalDB(Business infoModel) throws JSONException {
        if (infoModel != null){

            if(!"".equalsIgnoreCase(infoModel.getNatureOfBusiness())) {
                spnBussNtr.setText(CreditAppConstants.BUSINESS_NATURE[Integer.parseInt(infoModel.getNatureOfBusiness())], false);
                mViewModel.getModel().setNatureOfBusiness(infoModel.getNatureOfBusiness());
            }

            if(!"".equalsIgnoreCase(infoModel.getTypeOfBusiness())) {
                spnBussTyp.setText(CreditAppConstants.BUSINESS_TYPE[Integer.parseInt(infoModel.getTypeOfBusiness())], false);
                mViewModel.getModel().setTypeOfBusiness(infoModel.getTypeOfBusiness());
            }


            if(!"".equalsIgnoreCase(infoModel.getSizeOfBusiness())) {
                spnBussSze.setText(CreditAppConstants.BUSINESS_SIZE[Integer.parseInt(infoModel.getSizeOfBusiness())], false);
                mViewModel.getModel().setSizeOfBusiness(infoModel.getSizeOfBusiness());
            }
            int nlength = (int)(infoModel.getLenghtOfService() * 12);
            if (nlength < 12){
                txtLnghtSrv.setText(String.valueOf(nlength));
                spnLngSrvc.setText(CreditAppConstants.LENGTH_OF_STAY[0], false);
                mViewModel.getModel().setIsYear(String.valueOf(0));
                mViewModel.getModel().setLengthOfService(nlength);
            }else{
                txtLnghtSrv.setText(String.valueOf(infoModel.getLenghtOfService()));
                spnLngSrvc.setText(CreditAppConstants.LENGTH_OF_STAY[1], false);
                mViewModel.getModel().setIsYear(String.valueOf(1));
                mViewModel.getModel().setLengthOfService(infoModel.getLenghtOfService());
            }

            if(!"".equalsIgnoreCase(infoModel.getBusinessAddress())) {
                txtBussAdds.setText(infoModel.getBusinessAddress());
                mViewModel.getModel().setBusinessAddress(infoModel.getBusinessAddress());
            }

            if(!"".equalsIgnoreCase(infoModel.getTown())) {
                txtTownxx.setText(infoModel.getBusinessAddress());
                mViewModel.getModel().setBusinessAddress(infoModel.getBusinessAddress());
                mViewModel.getModel().setTown(infoModel.getTown());
            }
            txtBussName.setText(!"".equalsIgnoreCase(String.valueOf(infoModel.getNameOfBusiness())) ? String.valueOf(infoModel.getNameOfBusiness()) : "");
            txtMnthlyIn.setText( !"".equalsIgnoreCase(String.valueOf(infoModel.getMonthlyIncome())) ? String.valueOf(infoModel.getMonthlyIncome()) : "");
            txtMnthlyEx.setText( !"".equalsIgnoreCase(String.valueOf(infoModel.getMonthlyExpense())) ? String.valueOf(infoModel.getMonthlyExpense()) : "");

//            infoModel.setIsYear(String.valueOf(i));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent loIntent = new Intent(Activity_SelfEmployedInfo.this, Activity_EmploymentInfo.class);
            loIntent.putExtra("sTransNox", TransNox);
            startActivity(loIntent);
            overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent loIntent = new Intent(Activity_SelfEmployedInfo.this, Activity_EmploymentInfo.class);
        loIntent.putExtra("sTransNox", TransNox);
        startActivity(loIntent);
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
        finish();
    }

    @Override
    protected void onDestroy() {
        getViewModelStore().clear();
        super.onDestroy();
    }
}