package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
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
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DGanadoOnline;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplication;
import org.rmj.g3appdriver.GCircle.room.Entities.EMCContractInfo;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.lib.Ganado.pojo.InstallmentInfo;
import org.rmj.guanzongroup.onlinecreditapplication.Adapter.MCAdapter;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMARContact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Activity_MC_Contract extends AppCompatActivity {

    private final HashMap<String, String> loTerms = CreditAppConstants.TERMS_BY_CODE;

    private VMARContact mViewModel;
    private LoadDialog poDialogx;
    private MessageBox poMessage;

    private EMCContractInfo loContract = new EMCContractInfo();
    private List<CreditOnlineApplication.MCSerial> laSerials = new ArrayList<>();
    private InstallmentInfo loInstallment;
    private DGanadoOnline.CashPrice loCashPrice;

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
                Toast.makeText(Activity_MC_Contract.this, "Credit application not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            //initialize terms
            String[] laTerms = new String[loTerms.size()];
            int lnCnt = 0;

            //initialize terms adaper
            for (Map.Entry<String, String> loEntry: loTerms.entrySet()){
                laTerms[lnCnt] = loEntry.getKey();
                lnCnt += 1;
            }
            auto_term.setAdapter(CreditAppConstants.getAdapter(Activity_MC_Contract.this, laTerms));

            Thread.sleep(1000);

            //download existing mc contract
            mViewModel.DownloadMContract(getIntent().getStringExtra("sTransNox"), new VMARContact.OnDownload() {
                @Override
                public void OnLoad(String fsTitlexx, String fsMessage) {
                    poDialogx.initDialog(fsTitlexx, fsMessage, false);
                    poDialogx.show();
                }

                @Override
                public void OnFinished(String message) {
                    poDialogx.dismiss();
                    Toast.makeText(Activity_MC_Contract.this, message, Toast.LENGTH_SHORT).show();

                    try {

                        String lsSerialtoSearch = "";
                        if(mViewModel.GetCreditContract(getIntent().getStringExtra("sTransNox")) == null){

                            JSONObject loDetail = new JSONObject(loApp.getDetlInfo());

                            //set contract object values
                            loContract.setsReferNox(getIntent().getStringExtra("sTransNox")); //reference number
                            loContract.setsBranchCd(loApp.getBranchCd()); //branch code
                            loContract.setnAcctTerm(Integer.parseInt(loDetail.getString("nAcctTerm"))); //terms
                            loContract.setnDownPaym(loDetail.getDouble("nDownPaym")); //downpayment
                            loContract.setsTransNox(mViewModel.CreateIDForContract()); //transaction number
                            loContract.setsClientID(mViewModel.CreateIDForClient()); //client id
                            loContract.setsAcctNmbr(mViewModel.CreateIDForAccountNumber()); //account number
                            loContract.setsSendStat("0");
                            loContract.setcTranStat("0"); //status to new entry

                            mViewModel.GetProductModel().setTermIDxx(loDetail.getString("nAcctTerm")); //installment model terms
                            mViewModel.GetProductModel().setPaymForm("1"); //model installment pay type
                            mViewModel.GetProductModel().setDownPaym(String.valueOf(loDetail.getDouble("nDownPaym"))); //model installment downpayment

                            mViewModel.SetModelIDxx(loDetail.getString("sModelIDx")); //model installment product id

                            lsSerialtoSearch = loDetail.getString("sModelIDx");
                        }else {
                            loContract = mViewModel.GetCreditContract(getIntent().getStringExtra("sTransNox"));

                            mViewModel.GetProductModel().setDownPaym(String.valueOf(loContract.getnDownPaym()));
                            mViewModel.GetProductModel().setTermIDxx(String.valueOf(loContract.getnAcctTerm()));
                            mViewModel.GetProductModel().setMonthAmr(String.valueOf(loContract.getnMonAmort()));

                            lsSerialtoSearch = loContract.getsSerialID();
                        }

                        //initialize serials
                        if (!lsSerialtoSearch.isEmpty()){

                            Thread.sleep(1000);

                            //initialize serial models
                            mViewModel.GetSerials(lsSerialtoSearch, true, new VMARContact.OnSearchLSerial() {
                                @Override
                                public void OnSuccess(List<CreditOnlineApplication.MCSerial> laResult) {

                                    poDialogx.dismiss();

                                    //initialize global result
                                    laSerials = laResult;

                                    //initialize adapter
                                    auto_serial.setAdapter(new MCAdapter(Activity_MC_Contract.this, R.layout.list_item_mcserial, laSerials));
                                    auto_serial.setThreshold(0);
                                    auto_serial.showDropDown();

                                    mViewModel.SetModelIDxx(laSerials.getLast().lsModelIDx);
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

                        //CLOSE status
                        if (loContract.getcTranStat().equalsIgnoreCase("1")){

                            //disable inputs
                            auto_serial.setEnabled(false);
                            tie_downpay.setEnabled(false);
                            tie_remarks.setEnabled(false);

                            //allow to resend, if not uploaded, else, disable
                            if (loContract.getsSendStat().equalsIgnoreCase("0")){
                                btn_submit.setEnabled(true);
                                return;
                            }
                            btn_submit.setEnabled(false);
                        }

                        //allow inputs if OPEN status
                        auto_serial.setEnabled(true);
                        tie_downpay.setEnabled(true);
                        tie_remarks.setEnabled(true);

                        InitDisplay();

                    }catch (Exception e){
                        mViewModel.SaveError("MC Contract", e.getMessage());
                    }
                }
            });

        }catch (Exception e){
            mViewModel.SaveError("Activity_MC_Contract", e.getMessage());
        }

    }

    private void InitDisplay(){

        //initialize values to display
        if (mViewModel.getBranchInfo(loContract.getsBranchCd()) == null){
            tie_branch.setText(loContract.getsBranchCd());
        }else {

            EBranchInfo loBranch = mViewModel.getBranchInfo(loContract.getsBranchCd());
            tie_branch.setText(loBranch.getBranchNm());
        }

        tie_transaction.setText(loContract.getsTransNox());
        tie_client.setText(loContract.getsClientID());
        tie_account.setText(loContract.getsAcctNmbr());
        tie_remarks.setText(loContract.getsRemarksx());

        tie_downpay.setText(String.valueOf(loContract.getnDownPaym()));
        tie_monthly.setText(String.valueOf(loContract.getnMonAmort()));

        loTerms.entrySet().forEach(new Consumer<Map.Entry<String, String>>() {
            @Override
            public void accept(Map.Entry<String, String> stringStringEntry) {

                if (stringStringEntry.getValue().equalsIgnoreCase(String.valueOf(loContract.getnAcctTerm()))){
                    auto_term.setText(stringStringEntry.getKey(), false);
                }
            }
        });

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

                                poDialogx.dismiss();

                                laSerials = laResult;
                                auto_serial.setAdapter(new MCAdapter(Activity_MC_Contract.this, R.layout.list_item_mcserial, laSerials));
                                auto_serial.showDropDown();
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
                    mViewModel.GetProductModel().setDownPaym(String.valueOf(lnDownPaym));

                    //trigger coputation by downpayment
                    InitializePayment();

                }
            }
        });

        tie_remarks.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus){

                    if (tie_remarks.getText() == null || tie_remarks.getText().toString().isEmpty()){
                        return;
                    }
                    loContract.setsRemarksx(tie_remarks.getText().toString());
                }
            }
        });

        auto_serial.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //set to 0 as it is filtered on every value selected
                CreditOnlineApplication.MCSerial loSerial = (CreditOnlineApplication.MCSerial) parent.getItemAtPosition(0);

                //set model and serial id
                mViewModel.SetModelIDxx(loSerial.lsModelIDx);
                loContract.setsSerialID(loSerial.lsSerialID);
            }
        });

        auto_term.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String lsTerm = loTerms.get((String) parent.getItemAtPosition(position));

                mViewModel.GetProductModel().setTermIDxx(lsTerm);

                InitializePayment();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InitMessage(1, R.drawable.baseline_error_24, "Are you sure you want to submit the contract?", "Yes", "No", new OnMessageButton() {
                    @Override
                    public void OnPositive() {

                        mViewModel.SubmitMContract(loContract, new VMARContact.OnSubmit() {
                            @Override
                            public void OnLoad(String fsTitlexx, String fsMessage) {
                                poDialogx.initDialog(fsTitlexx, fsMessage, false);
                                poDialogx.show();
                            }

                            @Override
                            public void OnSuccess() {
                                poDialogx.dismiss();

                                InitMessage(0, R.drawable.baseline_message_24, "Successfully submitted", "Okay", "", new OnMessageButton() {
                                    @Override
                                    public void OnPositive() {
                                        InitData();
                                    }

                                    @Override
                                    public void OnNegative() {}
                                });
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
    }

    private void InitObservers(){

        //observe model id every selection
        mViewModel.GetModelIDxx().observe(Activity_MC_Contract.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                //set model id to model
                mViewModel.GetProductModel().setModelIDx(s);

                //get cash price of model
                mViewModel.GetCashPrice(mViewModel.GetProductModel().getModelIDx()).observe(Activity_MC_Contract.this, new Observer<DGanadoOnline.CashPrice>() {
                    @Override
                    public void onChanged(DGanadoOnline.CashPrice cashPrice) {

                        if (cashPrice == null){
                            return;
                        }
                        loCashPrice = cashPrice;
                    }
                });

                //get installment details of model
                mViewModel.GetMinimumDownpayment(mViewModel.GetProductModel().getModelIDx(), new VMARContact.OnRetrieveInstallmentInfo() {
                    @Override
                    public void OnRetrieve(InstallmentInfo loResult) {

                        if (loResult == null){
                            Toast.makeText(Activity_MC_Contract.this, "Installment info not found00!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        loInstallment = loResult;
                    }

                    @Override
                    public void OnFailed(String message) {
                        Toast.makeText(Activity_MC_Contract.this, message, Toast.LENGTH_LONG).show();
                    }
                });
                InitializePayment();
            }
        });
    }

    private void InitializePayment(){

        //validate mc payment info
        if (loInstallment == null){
            Toast.makeText(Activity_MC_Contract.this, "Could not find installment info", Toast.LENGTH_SHORT);
            return;
        }

        if (loCashPrice == null){
            Toast.makeText(Activity_MC_Contract.this, "Could not find cash info", Toast.LENGTH_SHORT);
            return;
        }

        //set default minimum down, if downpayment is lesser than requried amount
        if (Double.parseDouble(mViewModel.GetProductModel().getDownPaym()) < loInstallment.getMinimumDownpayment()){

            InitMessage(0, R.drawable.baseline_error_24, "Downpayment does not meet the required amount", "Okay", "", new OnMessageButton() {
                @Override
                public void OnPositive() {
                    mViewModel.GetProductModel().setDownPaym(String.valueOf(loInstallment.getMinimumDownpayment()));
                    InitDisplay();
                }

                @Override
                public void OnNegative() {}
            });
            return;
        } else if (Double.parseDouble(mViewModel.GetProductModel().getDownPaym()) >= loCashPrice.CashPrce){

            InitMessage(0, R.drawable.baseline_error_24, "Downpayment cannot exceed or be equal to the cash price", "Okay", "", new OnMessageButton() {
                @Override
                public void OnPositive() {
                    mViewModel.GetProductModel().setDownPaym(String.valueOf(loInstallment.getMinimumDownpayment()));
                    InitDisplay();
                }

                @Override
                public void OnNegative() {}
            });
            return;
        }

        //compute monthly, triggered by terms or downpayment
        double ldbl_monthly = mViewModel.GetMonthlyAmortization(Integer.parseInt(mViewModel.GetProductModel().getTermIDxx()));

        mViewModel.GetProductModel().setMonthAmr(String.valueOf(ldbl_monthly));
        mViewModel.CalculateNewDownpayment(mViewModel.GetProductModel().getModelIDx(), Integer.parseInt(mViewModel.GetProductModel().getTermIDxx()),
                Double.parseDouble(mViewModel.GetProductModel().getDownPaym()), new VMARContact.OnCalculateNewDownpayment() {
                    @Override
                    public void OnCalculate(double lnResult) {
                        mViewModel.GetProductModel().setMonthAmr(String.valueOf(lnResult));
                        tie_monthly.setText(String.valueOf(lnResult));
                        btn_submit.setEnabled(true);
                    }

                    @Override
                    public void OnFailed(String message) {

                        btn_submit.setEnabled(false);
                        if (message == null || message.isEmpty()){
                            Toast.makeText(Activity_MC_Contract.this, "Failed to calculate downpayment", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(Activity_MC_Contract.this, message, Toast.LENGTH_SHORT).show();
                    }
                });

        //set computation to contract object
        loContract.setnDownPaym(Double.parseDouble(mViewModel.GetProductModel().getDownPaym()));
        loContract.setnAcctTerm(Integer.parseInt(mViewModel.GetProductModel().getTermIDxx()));
        loContract.setnMonAmort(Double.parseDouble(mViewModel.GetProductModel().getnMonthAmr()));

        InitDisplay();
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
