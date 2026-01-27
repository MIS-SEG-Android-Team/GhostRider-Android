package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppConstants;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditOnlineApplication;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DMcModel;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplication;
import org.rmj.g3appdriver.GCircle.room.Entities.EMCContractInfo;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.onlinecreditapplication.Adapter.MCAdapter;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMARContact;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMCreditApplications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_MC_Contract extends AppCompatActivity {

    private VMARContact mViewModel;
    private LoadDialog poDialogx;
    private MessageBox poMessage;
    private List<CreditOnlineApplication.MCSerial> laSerials = new ArrayList<>();
    private HashMap<String, String> loTerms = CreditAppConstants.TERMS_BY_CODE;

    private TextInputEditText tie_branch, tie_transaction, tie_client, tie_account, tie_downpay, tie_monthly, tie_remarks;
    private MaterialAutoCompleteTextView auto_serial, auto_term;
    private MaterialButton btn_submit;
    private MaterialTextView mtv_refresh;

    public interface OnMessageButton {
        void OnPositive();
        void OnNegative();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mcontract);

        mViewModel = new ViewModelProvider(Activity_MC_Contract.this).get(VMARContact.class);
        poDialogx = new LoadDialog(Activity_MC_Contract.this);
        poMessage = new MessageBox(Activity_MC_Contract.this);

        setContentView(R.layout.activity_mcontract);

        if (!getIntent().hasExtra("sTransNox")){
            InitMessage(0, R.drawable.baseline_error_24, "Invalid transactio number", "Okay", "", new OnMessageButton() {
                @Override
                public void OnPositive() {
                    finish();
                }

                @Override
                public void OnNegative() {}
            });
            return;
        }

        InitWidgets();
        InitObservers();
        InitData();
        InitListener();
    }

    private void InitWidgets(){
        tie_branch = findViewById(R.id.tie_branch);
        tie_transaction = findViewById(R.id.tie_transaction);
        tie_client = findViewById(R.id.tie_client);
        tie_account = findViewById(R.id.tie_account);
        tie_downpay = findViewById(R.id.tie_downpay);
        tie_monthly = findViewById(R.id.tie_monthly);
        tie_remarks = findViewById(R.id.tie_remarks);
        auto_serial = findViewById(R.id.auto_serial);
        auto_term = findViewById(R.id.auto_term);
        mtv_refresh = findViewById(R.id.mtv_refresh);
        btn_submit = findViewById(R.id.btn_submit);
    }

    private void InitData(){

        try {

            //do not proceed if credit app is empty
            ECreditApplication loApp = mViewModel.GetApplication(getIntent().getStringExtra("sTransNox"));
            if (loApp == null){
                return;
            }

            JSONObject loDetail = new JSONObject(loApp.getDetlInfo());

            //initialize adapter
            if (mViewModel.getBranchInfo(loApp.getBranchCd()) == null){
                tie_branch.setText(loApp.getBranchCd());
            }else {
                EBranchInfo loBranch = mViewModel.getBranchInfo(loApp.getBranchCd());
                tie_branch.setText(loBranch.getBranchNm());
            }

            //initialize transaction ids
            tie_transaction.setText(mViewModel.CreateIDForContract());
            tie_client.setText(mViewModel.CreateIDForClient());
            tie_account.setText(mViewModel.CreateIDForAccountNumber());

            //initialize terms
            String[] laTerms = new String[loTerms.size()];
            int lnCnt = 0;

            for (Map.Entry<String, String> loEntry: loTerms.entrySet()){
                laTerms[lnCnt] = loEntry.getKey();

                if (loDetail.getInt("nAcctTerm") == Integer.parseInt(loEntry.getValue())){
                    mViewModel.GetModel().setAccTermxx(lnCnt);
                    auto_term.setText(String.valueOf(loEntry.getKey()));
                }
                lnCnt += 1;
            }
            auto_term.setAdapter(CreditAppConstants.getAdapter(Activity_MC_Contract.this, laTerms));

            //initialize to model
            mViewModel.GetModel().setDownPaymt(loDetail.getDouble("nDownPaym"));

            //set model id
            mViewModel.SetModelIDxx(loDetail.getString("sModelIDx"));

            //initialize serial models
            mViewModel.GetSerials(loDetail.getString("sModelIDx"), true, new VMARContact.OnSearchLSerial() {
                @Override
                public void OnSuccess(List<CreditOnlineApplication.MCSerial> laResult) {

                    laSerials = laResult;

                    //initialize adapter
                    auto_serial.setAdapter(new MCAdapter(Activity_MC_Contract.this, R.layout.list_item_mcserial, laSerials));
                    auto_serial.showDropDown();

                    poDialogx.dismiss();
                }

                @Override
                public void OnFailed(String message) {
                    poDialogx.dismiss();

                    InitMessage(0, R.drawable.baseline_error_24, message, "Okay", "", new OnMessageButton() {
                        @Override
                        public void OnPositive() {}

                        @Override
                        public void OnNegative() {}
                    });
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void InitListener(){

        mtv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (auto_serial.getText() == null || auto_serial.getText().toString().trim().isEmpty()){

                    InitMessage(0, R.drawable.ic_toast_warning, "Please enter the last 5 characters of the serial number", "Okay", "", new OnMessageButton() {
                        @Override
                        public void OnPositive() {}

                        @Override
                        public void OnNegative() {}
                    });
                    return;
                }

                InitMessage(1, R.drawable.ic_baseline_confirmation_pin_24, "Reload serial numbers?", "Yes", "No", new OnMessageButton() {
                    @Override
                    public void OnPositive() {

                        poDialogx.initDialog("MC Serials","Loading serials. Please wait . .", false);
                        poDialogx.show();

                        mViewModel.GetSerials(auto_serial.getText().toString(), false, new VMARContact.OnSearchLSerial() {
                            @Override
                            public void OnSuccess(List<CreditOnlineApplication.MCSerial> laResult) {

                                laSerials = laResult;
                                auto_serial.setAdapter(new MCAdapter(Activity_MC_Contract.this, R.layout.list_item_mcserial, laSerials));
                                auto_serial.showDropDown();
                                poDialogx.dismiss();
                            }

                            @Override
                            public void OnFailed(String message) {
                                poDialogx.dismiss();

                                InitMessage(0, R.drawable.baseline_error_24, message, "Okay", "", new OnMessageButton() {
                                    @Override
                                    public void OnPositive() {}

                                    @Override
                                    public void OnNegative() {}
                                });
                            }
                        });

                    }

                    @Override
                    public void OnNegative() {}
                });
            }
        });

        auto_serial.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                MCAdapter loAdapter = ((MCAdapter) auto_serial.getAdapter());
                if (loAdapter == null){
                    return;
                }

                if (loAdapter.getCount() > 1){
                    loAdapter.getFilter().filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        tie_downpay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus){

                    if (tie_downpay.getText() == null || tie_downpay.getText().toString().trim().isEmpty()){
                        InitMessage(0, R.drawable.baseline_error_24, "Please enter downpayment", "Okay", "", new OnMessageButton() {
                            @Override
                            public void OnPositive() {}

                            @Override
                            public void OnNegative() {}
                        });
                        return;
                    }

                    //initialize downpayment
                    double lnDownPaym = FormatUIText.getParseDouble(tie_downpay.getText().toString());
                    mViewModel.GetModel().setDownPaymt(lnDownPaym);

                    //trigger coputation by downpayment
                    InitializePayment(false);

                }
            }
        });

        auto_serial.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CreditOnlineApplication.MCSerial loSerial = (CreditOnlineApplication.MCSerial) parent.getItemAtPosition(0);
                mViewModel.SetModelIDxx(loSerial.lsModelIDx);
            }
        });

        auto_term.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //initialize monthly and display computed amount
                mViewModel.GetModel().setAccTermxx(position);
                InitializePayment(true);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EMCContractInfo loContract = new EMCContractInfo();
                loContract.setsTransNox("");

                //mViewModel.SubmitMContract();
            }
        });
    }

    private void InitializePayment(Boolean fByTerms){

        //set computed minimum downpayment
        double ldbl_down = mViewModel.GetMinimumDownpayment();

        //set defualt minimum down, if downpayment is lesser than requried amount
        if (mViewModel.GetModel().getDownPaymt() < ldbl_down){

            InitMessage(0, R.drawable.baseline_error_24, "Downpayment does not meet the required amount", "Okay", "", new OnMessageButton() {
                @Override
                public void OnPositive() {
                    mViewModel.GetModel().setDownPaymt(ldbl_down);
                    tie_downpay.setText(String.valueOf(ldbl_down));
                }

                @Override
                public void OnNegative() {}
            });

            return;
        }

        //compute monthly, triggered by terms or downpayment
        double ldbl_monthly = mViewModel.GetMonthlyPayment(mViewModel.GetModel().getAccTermxx());
        if (!fByTerms){
            ldbl_monthly = mViewModel.GetMonthlyPayment(mViewModel.GetModel().getDownPaymt());
        }
        mViewModel.GetModel().setMonthlyAm(ldbl_monthly);
        tie_monthly.setText(String.valueOf(ldbl_monthly));

        Log.d("this is model", mViewModel.GetModel().getModelIDxx());
        Log.d("this is dp", String.valueOf(mViewModel.GetModel().getDownPaymt()));
        Log.d("this is terms", String.valueOf(mViewModel.GetModel().getAccTermxx()));
        Log.d("this is monthly", String.valueOf(ldbl_monthly));
    }

    private void InitObservers(){

        //observe model id every selection
        mViewModel.GetModelIDxx().observe(Activity_MC_Contract.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s == null || s.isEmpty()){
                    return;
                }

                //set model id to model
                mViewModel.GetModel().setModelIDxx(s);

                //get installment details
                mViewModel.GetInstallmentPlanDetail(mViewModel.GetModel().getModelIDxx()).observe(Activity_MC_Contract.this, new Observer<DMcModel.McDPInfo>() {
                    @Override
                    public void onChanged(DMcModel.McDPInfo mcDPInfo) {

                        if (mcDPInfo == null){
                            return;
                        }

                        //initialize terms and downpayment
                        if (mViewModel.InitializeTermAndDownpayment(mcDPInfo)){

                            //get amortization details
                            mViewModel.GetAmortizationDetail(mViewModel.GetModel().getModelIDxx(), mViewModel.GetModel().getAccTermxx()).observe(Activity_MC_Contract.this, new Observer<DMcModel.McAmortInfo>() {
                                @Override
                                public void onChanged(DMcModel.McAmortInfo mcAmortInfo) {

                                    if (mcAmortInfo == null){
                                        return;
                                    }

                                    //initialize model for monthly amortization
                                    mViewModel.SetModelAmortization(mcAmortInfo);

                                    //initialize payment
                                    InitializePayment(true);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void InitMessage(int messageType, int statusIcon, String message, String posText, String negText, OnMessageButton callback){

        poMessage.initDialog();
        poMessage.setTitle("Credit Application");
        poMessage.setIcon(statusIcon);
        poMessage.setMessage(message);

        poMessage.setPositiveButton(posText, (view, dialog) -> {
            dialog.dismiss();
            callback.OnPositive();
        });

        if (messageType == 1){
            poMessage.setNegativeButton(negText, (view, dialog) -> {
                dialog.dismiss();
                callback.OnNegative();

            });
        }

        poMessage.show();
    }
}
