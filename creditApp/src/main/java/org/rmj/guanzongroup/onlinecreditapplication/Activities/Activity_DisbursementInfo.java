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

import com.google.android.material.textfield.TextInputEditText;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import org.json.JSONException;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicantInfo;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.Disbursement;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppConstants;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.OnParseListener;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMDisbursement;

import java.util.Objects;

public class Activity_DisbursementInfo extends AppCompatActivity {

    private VMDisbursement mViewModel;
    private MessageBox poMessage;
    private MaterialAutoCompleteTextView spnTypex;
    private String typeX = "";
    private TextInputEditText tieFoodx, tieElctx, tieWater, tieLoans, tieBankN, tieCCBnk, tieLimit, tieYearS;
    private MaterialButton btnPrev, btnNext;
    private MaterialToolbar toolbar;


    private String TransNox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(Activity_DisbursementInfo.this).get(VMDisbursement.class);
        poMessage = new MessageBox(Activity_DisbursementInfo.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disbursement_info);
        initWidgets();
        mViewModel.InitializeApplication(getIntent());
        mViewModel.GetApplication().observe(Activity_DisbursementInfo.this, new Observer<ECreditApplicantInfo>() {
            @Override
            public void onChanged(ECreditApplicantInfo app) {
                try {
                    TransNox = app.getTransNox();
                    mViewModel.getModel().setTransNox(app.getTransNox());
                    mViewModel.setCvlStatus(app.getIsSpouse());
                    mViewModel.ParseData(app, new OnParseListener() {
                        @Override
                        public void OnParse(Object args) {
                            Disbursement loDetail = (Disbursement) args;
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

        spnTypex.setAdapter(new ArrayAdapter<>(Activity_DisbursementInfo.this,
                android.R.layout.simple_list_item_1, CreditAppConstants.ACCOUNT_TYPE));
        spnTypex.setDropDownBackgroundResource(R.drawable.bg_gradient_light);
        spnTypex.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.getModel().setAcctType(String.valueOf(position));
            }
        });

        btnNext.setOnClickListener(v -> SaveDisbursementInfo());
        btnPrev.setOnClickListener(v -> {
            returnPrevious();
        });

    }

    private void SaveDisbursementInfo() {
        if (tieElctx.getText().toString().trim().isEmpty()) {
            mViewModel.getModel().setElectric(0);
        } else {
            mViewModel.getModel().setElectric(Double.parseDouble(tieElctx.getText().toString().trim()));
        }

        if (tieWater.getText().toString().trim().isEmpty()) {
            mViewModel.getModel().setWaterExp(0);
        } else {
            mViewModel.getModel().setWaterExp(Double.parseDouble(tieWater.getText().toString().trim()));
        }

        if (tieFoodx.getText().toString().trim().isEmpty()) {
            mViewModel.getModel().setFoodExps(0);
        } else {
            mViewModel.getModel().setFoodExps(Double.parseDouble((tieFoodx.getText()).toString().trim()));
        }

        if (tieLoans.getText().toString().trim().isEmpty()) {
            mViewModel.getModel().setLoanExps(0);
        } else {
            mViewModel.getModel().setLoanExps(Double.parseDouble(tieLoans.getText().toString().trim()));
        }

        mViewModel.getModel().setBankName(tieBankN.getText().toString().trim());
        mViewModel.getModel().setCrdtBank(tieCCBnk.getText().toString().trim());

        if (tieLimit.getText().toString().trim().isEmpty()) {
            mViewModel.getModel().setCrdtLimt(0);
        } else {
            mViewModel.getModel().setCrdtLimt(Double.parseDouble(tieLimit.getText().toString().trim()));
        }
        if (tieYearS.getText().toString().trim().isEmpty()) {
            mViewModel.getModel().setCrdtYear(0);
        } else {
            mViewModel.getModel().setCrdtYear(Integer.parseInt(tieYearS.getText().toString().trim()));
        }

        mViewModel.SaveData(new OnSaveInfoListener() {
            @Override
            public void OnSave(String args) {
                Intent loIntent = new Intent(Activity_DisbursementInfo.this, Activity_Dependent.class);
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
                poMessage.setPositiveButton("Dismiss", (view1, dialog) -> dialog.dismiss());
                poMessage.show();
            }
        });
    }


    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar_DisbursementInfo);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        tieElctx = findViewById(R.id.tie_cap_dbmElectricity);
        tieWater = findViewById(R.id.tie_cap_dbmWater);
        tieFoodx = findViewById(R.id.tie_cap_dbmFood);
        tieLoans = findViewById(R.id.tie_cap_dbmLoans);
        tieBankN = findViewById(R.id.tie_cap_dbmBankName);

        spnTypex = findViewById(R.id.spinner_cap_dbmAccountType);

        tieCCBnk = findViewById(R.id.tie_cap_dbmBankNameCC);
        tieLimit = findViewById(R.id.tie_cap_dbmCreditLimit);
        tieYearS = findViewById(R.id.tie_cap_dbmYearStarted);

        btnNext = findViewById(R.id.btn_creditAppNext);
        btnPrev = findViewById(R.id.btn_creditAppPrvs);

    }

    @SuppressLint("NewApi")
    public void setUpFieldsFromLocalDB(Disbursement infoModel) throws JSONException {
        if(infoModel != null) {
            tieElctx.setText(String.valueOf(infoModel.getElectric()));
            tieWater.setText(String.valueOf(infoModel.getWaterExp()));
            tieFoodx.setText(String.valueOf(infoModel.getFoodExps()));
            tieLoans.setText(String.valueOf(infoModel.getLoanExps()));

            //Bank Account
            tieBankN.setText(infoModel.getBankName());
            if(infoModel.getAcctType() != null) {
                if(!infoModel.getAcctType().equalsIgnoreCase("")) {
                    spnTypex.setText(CreditAppConstants.ACCOUNT_TYPE[Integer.parseInt(infoModel.getAcctType())]);
//                    spnTypex.setSelection(Integer.parseInt(infoModel.getAcctType()));
                    mViewModel.getModel().setAcctType(infoModel.getAcctType());
                }
            }

            typeX = infoModel.getCrdtBank();
            //Credit Card Account
            tieCCBnk.setText(infoModel.getCrdtBank());
            tieLimit.setText(String.valueOf(infoModel.getCrdtLimt()));
            tieYearS.setText(String.valueOf(infoModel.getCrdtYear()));


        } else {
            tieElctx.setText("");
            tieWater.setText("");
            tieFoodx.setText("");
            tieLoans.setText("");

            //Bank Account
            tieBankN.setText("");
            spnTypex.getText().clear();
            spnTypex.getText().clear();
            //Credit Card Account
            tieCCBnk.setText("");
            tieLimit.setText("");
            tieYearS.setText("");
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
        Intent loIntent;
        if(mViewModel.getCvlStatus().equalsIgnoreCase("1") ||
                mViewModel.getCvlStatus().equalsIgnoreCase("5")){
            loIntent = new Intent(Activity_DisbursementInfo.this, Activity_SpousePensionInfo.class);
        }else{
            loIntent = new Intent(Activity_DisbursementInfo.this, Activity_PensionInfo.class);
        }
        loIntent.putExtra("sTransNox", TransNox);
        startActivity(loIntent);
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
        finish();
    }
}