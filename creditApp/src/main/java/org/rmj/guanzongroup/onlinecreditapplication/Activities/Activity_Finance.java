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
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.rmj.g3appdriver.GCircle.room.Entities.ECountryInfo;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppConstants;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.Financier;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.OnParseListener;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMFinancierInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity_Finance extends AppCompatActivity {

    private VMFinancierInfo mViewModel;
    private MaterialAutoCompleteTextView spnRelation;
    private String relationX = "-1";
    private TextInputEditText txtFNamex, txtFIncme, txtFMoble, txtFFacbk, txtFEmail;
    private MessageBox poMessage;
    private MaterialAutoCompleteTextView txtFCntry;

    private MaterialButton btnNext, btnPrvs;
    private MaterialToolbar toolbar;

    private String TransNox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(Activity_Finance.this).get(VMFinancierInfo.class);
        poMessage = new MessageBox(Activity_Finance.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance);
        initWidgets();
        mViewModel.InitializeApplication(getIntent());
        mViewModel.GetApplication().observe(Activity_Finance.this, app -> {
            try {
                TransNox = app.getTransNox();
                mViewModel.getModel().setTransNox(app.getTransNox());
                mViewModel.getModel().setcMeanInfo(app.getAppMeans());
                mViewModel.ParseData(app, new OnParseListener() {
                    @Override
                    public void OnParse(Object args) {
                        Financier loDetail = (Financier) args;
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

        spnRelation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.getModel().setFinancierRelation(String.valueOf(position));
            }
        });


        mViewModel.GetCountryList().observe(Activity_Finance.this, new Observer<List<ECountryInfo>>() {
            @Override
            public void onChanged(List<ECountryInfo> loList) {
                try {
                    ArrayList<String> strings = new ArrayList<>();
                    for (int x = 0; x < loList.size(); x++) {
                        strings.add(loList.get(x).getCntryNme());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_Finance.this,
                            android.R.layout.simple_spinner_dropdown_item, strings.toArray(new String[0]));
                    txtFCntry.setAdapter(adapter);

                    txtFCntry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            for (int x = 0; x < loList.size(); x++) {
                                String lsLabel = loList.get(x).getCntryNme();
                                String lsSlctd = txtFCntry.getText().toString().trim();
                                if (lsSlctd.equalsIgnoreCase(lsLabel)) {
                                    mViewModel.getModel().setCountry(loList.get(x).getCntryCde());
                                    mViewModel.getModel().setCountryName(lsLabel);
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

        btnNext.setOnClickListener(v -> SaveFinanceInfo());
        btnPrvs.setOnClickListener(v -> {
            returnPrevious();
        });

    }

    private void SaveFinanceInfo() {

        mViewModel.getModel().setFinancierName(Objects.requireNonNull(txtFNamex.getText()).toString().trim());

        if (txtFIncme.getText().toString().trim().isEmpty()) {
            mViewModel.getModel().setRangeOfIncome(0);
        } else {
            mViewModel.getModel().setRangeOfIncome(Long.parseLong((txtFIncme.getText()).toString().trim()));
        }

        mViewModel.getModel().setMobileNo(Objects.requireNonNull(txtFMoble.getText()).toString().trim());
        mViewModel.getModel().setFacebook(Objects.requireNonNull(txtFFacbk.getText()).toString().trim());
        mViewModel.getModel().setEmail(Objects.requireNonNull(txtFEmail.getText()).toString().trim());

        mViewModel.SaveData(new OnSaveInfoListener() {
            @Override
            public void OnSave(String args) {
                Intent loIntent = new Intent(Activity_Finance.this, Activity_PensionInfo.class);
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
        toolbar = findViewById(R.id.toolbar_Finance);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        spnRelation = findViewById(R.id.spn_financierRelation);

        txtFNamex = findViewById(R.id.txt_financierName);
        txtFIncme = findViewById(R.id.txt_financierInc);
        txtFCntry = findViewById(R.id.txt_financierCntry);
        txtFMoble = findViewById(R.id.txt_financierContact);
        txtFFacbk = findViewById(R.id.txt_financierFb);
        txtFEmail = findViewById(R.id.txt_financierEmail);

        btnNext = findViewById(R.id.btn_creditAppNext);
        btnPrvs = findViewById(R.id.btn_creditAppPrvs);

        spnRelation.setAdapter(new ArrayAdapter<>(Activity_Finance.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.FINANCE_SOURCE));
        spnRelation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.getModel().setFinancierRelation(String.valueOf(position));
            }
        });

    }

    @SuppressLint("NewApi")
    public void setUpFieldsFromLocalDB(Financier infoModel) throws JSONException {
        if (infoModel != null){
            if(!infoModel.getFinancierRelation().isEmpty()){
                spnRelation.setText(CreditAppConstants.FINANCE_SOURCE[Integer.parseInt(infoModel.getFinancierRelation())], false);
                mViewModel.getModel().setFinancierRelation(infoModel.getFinancierRelation());
            }
            relationX = infoModel.getFinancierRelation();

            txtFNamex.setText(infoModel.getFinancierName());
            txtFIncme.setText(String.valueOf(infoModel.getRangeOfIncome()));
            txtFMoble.setText(infoModel.getMobileNo());
            txtFEmail.setText(infoModel.getEmail());
            txtFFacbk.setText(infoModel.getFacebook());
            if(!"".equalsIgnoreCase(infoModel.getCountry())){
                txtFCntry.setText(infoModel.getCountryName());
                mViewModel.getModel().setCountry(infoModel.getCountry());
                mViewModel.getModel().setCountryName(infoModel.getCountryName());
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
        Intent loIntent = new Intent(Activity_Finance.this, Activity_SelfEmployedInfo.class);
        loIntent.putExtra("sTransNox", TransNox);
        startActivity(loIntent);
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
        finish();
    }
}