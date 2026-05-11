package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Build;
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
import com.google.android.material.textfield.TextInputLayout;
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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class Activity_MC_Contract extends AppCompatActivity {

    private final HashMap<String, String> loTerms = CreditAppConstants.TERMS_BY_CODE;

    private VMARContact mViewModel;
    private LoadDialog poDialogx;
    private MessageBox poMessage;

    ECreditApplication loApp;
    private EMCContractInfo loContract = new EMCContractInfo();
    private List<CreditOnlineApplication.MCSerial> laSerials = new ArrayList<>();
    private InstallmentInfo loInstallment;
    private DGanadoOnline.CashPrice loCashPrice;

    private TextInputLayout layout_serial, layout_terms;
    private TextInputEditText tie_branch, tie_transaction, tie_client, tie_account, tie_downpay, tie_monthly, tie_purchasedt, tie_drno, tie_remarks;
    private MaterialAutoCompleteTextView auto_serial, auto_term;
    private MaterialButton btn_submit;
    private MaterialTextView mtv_refresh, mtv_confirmdp, mtv_confirmdrno, mtv_confirmRemarks;

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

        layout_serial = findViewById(R.id.layout_serial);
        layout_terms = findViewById(R.id.layout_terms);

        tie_branch = findViewById(R.id.tie_branch);
        tie_transaction = findViewById(R.id.tie_transaction);
        tie_client = findViewById(R.id.tie_client);
        tie_account = findViewById(R.id.tie_account);
        tie_downpay = findViewById(R.id.tie_downpay);
        tie_monthly = findViewById(R.id.tie_monthly);
        tie_purchasedt = findViewById(R.id.tie_purchasedt);
        tie_drno = findViewById(R.id.tie_drno);
        tie_remarks = findViewById(R.id.tie_remarks);
        auto_serial = findViewById(R.id.auto_serial);
        auto_term = findViewById(R.id.auto_term);
        mtv_refresh = findViewById(R.id.mtv_refresh);
        btn_submit = findViewById(R.id.btn_submit);
        mtv_confirmdp = findViewById(R.id.mtv_confirmdp);
        mtv_confirmdrno = findViewById(R.id.mtv_confirmdrno);
        mtv_confirmRemarks = findViewById(R.id.mtv_confirmRemarks);
    }

    private void InitData(){

        try {

            //do not proceed if credit app is empty
            loApp = mViewModel.GetApplication(getIntent().getStringExtra("sTransNox"));
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

            Thread.sleep(500);

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

                        JSONObject loDetail = new JSONObject(loApp.getDetlInfo());

                        String lsSerialtoSearch;
                        if(mViewModel.GetCreditContract(getIntent().getStringExtra("sTransNox")) == null){

                            //set contract object values
                            loContract.setsReferNox(getIntent().getStringExtra("sTransNox")); //reference number
                            loContract.setsBranchCd(loApp.getBranchCd()); //branch code
                            loContract.setnAcctTerm(Integer.parseInt(loDetail.getString("nAcctTerm"))); //terms
                            loContract.setnDownPaym(loDetail.getDouble("nDownPaym")); //downpayment
                            loContract.setsTransNox(mViewModel.CreateIDForContract()); //transaction number
                            loContract.setsClientID(mViewModel.CreateIDForClient()); //client id
                            loContract.setsAcctNmbr(mViewModel.CreateIDForAccountNumber()); //account number
                            loContract.setdPurchase(GetDateToday()); //purchase date
                            loContract.setsSendStat("0");
                            loContract.setcTranStat("0"); //status to new entry

                            mViewModel.GetProductModel().setTermIDxx(loDetail.getString("nAcctTerm")); //installment model terms
                            mViewModel.GetProductModel().setPaymForm("1"); //model installment pay type
                            mViewModel.GetProductModel().setDownPaym(String.valueOf(loDetail.getDouble("nDownPaym"))); //model installment downpayment

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
                            mViewModel.GetSerials(lsSerialtoSearch, new VMARContact.OnSearchLSerial() {
                                @Override
                                public void OnSuccess(List<CreditOnlineApplication.MCSerial> laResult) {

                                    poDialogx.dismiss();

                                    //initialize global result
                                    laSerials = laResult;

                                    //initialize adapter
                                    auto_serial.setAdapter(new MCAdapter(Activity_MC_Contract.this, R.layout.list_item_mcserial, laSerials));
                                    auto_serial.setThreshold(0);
                                    auto_serial.showDropDown();

                                    //set model and serial id
                                    loContract.setsSerialID(laSerials.get(0).lsSerialID);
                                    mViewModel.SetModelIDxx(laSerials.get(0).lsModelIDx);
                                }

                                @Override
                                public void OnFailed(String message) {
                                    poDialogx.dismiss();

                                    InitMessage(0, R.drawable.baseline_error_24, message, "Okay", "", new OnMessageButton() {
                                        @Override
                                        public void OnPositive() {
                                            try {
                                                mViewModel.SetModelIDxx(loDetail.getString("sModelIDx"));
                                            }catch (Exception e){
                                                mViewModel.SaveError("MC Contract", e.getMessage());
                                            }
                                        }

                                        @Override
                                        public void OnNegative() {}
                                    });
                                }
                            });

                        }

                        //display all fields value
                        InitDisplay(0);

                    }catch (Exception e){
                        mViewModel.SaveError("MC Contract", e.getMessage());
                    }
                }
            });

        }catch (Exception e){
            mViewModel.SaveError("Activity_MC_Contract", e.getMessage());
        }

    }

    @SuppressLint("SimpleDateFormat")
    private void InitDisplay(int fnIndex){

        try {

            switch (fnIndex){

                case  1: //unit, downpayment and monthly

                    //engine number or model id
                    if (loApp != null){

                        //if serial id is not initialized, display model id from credit app, else, display serial id
                        JSONObject loDetail  = new JSONObject(loApp.getDetlInfo());
                        if (loContract.getsSerialID() == null || loContract.getsSerialID().isEmpty()){
                            layout_serial.setHint("Model ID");
                            auto_serial.setText(loDetail.getString("sModelIDx"));
                        }else {
                            laSerials
                                    .stream()
                                    .filter(mcSerial -> mcSerial.lsSerialID.equalsIgnoreCase(loContract.getsSerialID()))
                                    .findFirst()
                                    .ifPresent(mcSerial -> {
                                        layout_serial.setHint("Engine Number");
                                        auto_serial.setText(mcSerial.lsEngine);
                                    });
                        }
                    }

                    tie_downpay.setText(String.valueOf(loContract.getnDownPaym()));
                    tie_monthly.setText(String.valueOf(loContract.getnMonAmort()));
                    break;

                case 2: //purchase date
                    if (loContract.getdPurchase() != null){

                        //if purchase date is not initialized, display current date, else, display purchase date
                        if (!loContract.getdPurchase().isEmpty()){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                tie_purchasedt.setText(LocalDate.parse(loContract.getdPurchase(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
                            }else {
                                tie_purchasedt.setText(new SimpleDateFormat("MMMM dd, yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(loContract.getdPurchase())));
                            }

                        }

                    }
                    break;
                case 3: //dr number
                    tie_drno.setText(loContract.getDrNo());
                    break;
                case 4: //remarks
                    tie_remarks.setText(loContract.getsRemarksx());
                    break;
                default: //all fields

                    Toast.makeText(Activity_MC_Contract.this, "Information has been refreshed", Toast.LENGTH_SHORT).show();

                    //branch
                    if (mViewModel.getBranchInfo(loContract.getsBranchCd()) == null){
                        tie_branch.setText(loContract.getsBranchCd());
                    }else {

                        EBranchInfo loBranch = mViewModel.getBranchInfo(loContract.getsBranchCd());
                        tie_branch.setText(loBranch.getBranchNm());
                    }

                    //generated ids
                    tie_transaction.setText(loContract.getsTransNox());
                    tie_client.setText(loContract.getsClientID());
                    tie_account.setText(loContract.getsAcctNmbr());

                    //engine number or model id
                    if (loApp != null){

                        //if serial id is not initialized, display model id from credit app, else, display serial id
                        JSONObject loDetail  = new JSONObject(loApp.getDetlInfo());
                        if (loContract.getsSerialID() == null || loContract.getsSerialID().isEmpty()){
                            layout_serial.setHint("Model ID");
                            auto_serial.setText(loDetail.getString("sModelIDx"));
                        }else {
                            laSerials
                                    .stream()
                                    .filter(mcSerial -> mcSerial.lsSerialID.equalsIgnoreCase(loContract.getsSerialID()))
                                    .findFirst()
                                    .ifPresent(mcSerial -> {
                                        layout_serial.setHint("Engine Number");
                                        auto_serial.setText(mcSerial.lsEngine);
                                    });
                        }
                    }

                    //purchase date
                    if (loContract.getdPurchase() != null){

                        //if purchase date is not initialized, display current date, else, display purchase date
                        if (!loContract.getdPurchase().isEmpty()){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                tie_purchasedt.setText(LocalDate.parse(loContract.getdPurchase(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
                            }else {
                                tie_purchasedt.setText(new SimpleDateFormat("MMMM dd, yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(loContract.getdPurchase())));
                            }

                        }

                    }

                    //dp and monthly
                    tie_downpay.setText(String.valueOf(loContract.getnDownPaym()));
                    tie_monthly.setText(String.valueOf(loContract.getnMonAmort()));

                    //loan term
                    loTerms.entrySet().forEach(new Consumer<Map.Entry<String, String>>() {
                        @Override
                        public void accept(Map.Entry<String, String> stringStringEntry) {

                            if (stringStringEntry.getValue().equalsIgnoreCase(String.valueOf(loContract.getnAcctTerm()))){
                                auto_term.setText(stringStringEntry.getKey(), false);
                            }
                        }
                    });

                    //dr number and remarks
                    tie_drno.setText(loContract.getDrNo());
                    tie_remarks.setText(loContract.getsRemarksx());

                    break;
            }

            //CLOSE status
            if (loContract.getcTranStat().equalsIgnoreCase("1")){

                //disable inputs
                layout_serial.setEnabled(false);
                layout_terms.setEnabled(false);

                tie_downpay.setEnabled(false);
                tie_remarks.setEnabled(false);

                //allow to resend, if not uploaded, else, disable
                btn_submit.setEnabled(false);
                if (loContract.getsSendStat().equalsIgnoreCase("0")){
                    btn_submit.setEnabled(true);
                }
                return;
            }

            //allow inputs if OPEN status
            layout_serial.setEnabled(true);
            layout_terms.setEnabled(true);

            tie_downpay.setEnabled(true);
            tie_remarks.setEnabled(true);

            btn_submit.setEnabled(true);

        }catch (Exception e){
            mViewModel.SaveError("Activity_MC_Contract", e.getMessage());
        }

    }

    private void InitListener(){

        mtv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String lsSerial;
                boolean fByModel;
                if (auto_serial.getText() == null || auto_serial.getText().toString().trim().isEmpty()){

                    if (loContract.getsSerialID() == null || loContract.getsSerialID().isEmpty()){

                        InitMessage(0, R.drawable.ic_toast_warning, "Please enter the last 5 characters of the serial number or exact model or serial id", "Okay", "", new OnMessageButton() {
                            @Override
                            public void OnPositive() {}

                            @Override
                            public void OnNegative() {}
                        });
                        return;

                    }
                    lsSerial = loContract.getsSerialID();
                }else {
                    lsSerial = auto_serial.getText().toString();
                }

                InitMessage(1, R.drawable.ic_baseline_confirmation_pin_24, "Reload serial numbers?", "Yes", "No", new OnMessageButton() {
                    @Override
                    public void OnPositive() {

                        poDialogx.initDialog("MC Serials","Loading serials. Please wait . .", false);
                        poDialogx.show();

                        mViewModel.GetSerials(lsSerial, new VMARContact.OnSearchLSerial() {
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

        tie_downpay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() < 1 || charSequence.toString().equalsIgnoreCase(String.valueOf(loContract.getnDownPaym()))){
                    mtv_confirmdp.setVisibility(GONE);
                    return;
                }
                mtv_confirmdp.setVisibility(VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        mtv_confirmdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                InitializePayment();
            }
        });

        tie_drno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() < 1 || charSequence.toString().equalsIgnoreCase(String.valueOf(loContract.getDrNo()))){
                    mtv_confirmdrno.setVisibility(GONE);
                    return;
                }
                mtv_confirmdrno.setVisibility(VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        mtv_confirmdrno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                view.setVisibility(GONE);

                if (tie_drno.getText() == null || tie_drno.getText().toString().isEmpty()){
                    return;
                }
                loContract.setDrNo(tie_drno.getText().toString());

                InitDisplay(3);
            }
        });

        tie_remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() < 1 || charSequence.toString().equalsIgnoreCase(String.valueOf(loContract.getsRemarksx()))){
                    mtv_confirmRemarks.setVisibility(GONE);
                    return;
                }
                mtv_confirmRemarks.setVisibility(VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        mtv_confirmRemarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                view.setVisibility(GONE);

                if (tie_remarks.getText() == null || tie_remarks.getText().toString().isEmpty()){
                    return;
                }
                loContract.setsRemarksx(tie_remarks.getText().toString());

                InitDisplay(4);
            }
        });

        tie_purchasedt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                @SuppressLint("SimpleDateFormat")
                final Calendar newCalendar = Calendar.getInstance(Locale.getDefault());

                final DatePickerDialog StartTime = new DatePickerDialog(Activity_MC_Contract.this,
                        android.R.style.Theme_Holo_Dialog, (view131, year, monthOfYear, dayOfMonth) -> {

                    try {

                        Calendar loCalendar = Calendar.getInstance();
                        loCalendar.set(year, monthOfYear, dayOfMonth);

                        String lsMonth = String.valueOf(loCalendar.get(Calendar.MONTH) + 1);
                        String lsDate = String.valueOf(loCalendar.get(Calendar.DATE));

                        if (loCalendar.get(Calendar.MONTH) < 10){
                            lsMonth = "0" + String.valueOf(loCalendar.get(Calendar.MONTH) + 1);
                        }

                        if (loCalendar.get(Calendar.DATE) < 10){
                            lsDate = "0" + String.valueOf(loCalendar.get(Calendar.DATE));
                        }


                        String lsDateSelect = String.valueOf(loCalendar.get(Calendar.YEAR)) + "/" + lsMonth + "/" + lsDate;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            LocalDate localDate = LocalDate.now();

                            LocalDate currentDate = LocalDate.parse(localDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                            LocalDate compDate = LocalDate.parse(lsDateSelect, DateTimeFormatter.ofPattern("yyyy/MM/dd"));

                            //purchase date should be on or after current day
                            if (currentDate.isAfter(compDate)){
                                Toast.makeText(Activity_MC_Contract.this, "Purchase Date should On or After this day " + compDate, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            loContract.setdPurchase(compDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                        }else {

                            Date currentDate = loCalendar.getTime();
                            Date compDate = new SimpleDateFormat("yyyy/MM/dd").parse(lsDateSelect);

                            //purchase date should be on or after current day
                            if (currentDate.after(compDate)){
                                Toast.makeText(Activity_MC_Contract.this, "Purchase Date should be On or After this day", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            loContract.setdPurchase(new SimpleDateFormat("yyyy-MM-dd").format(compDate));
                        }
                        InitDisplay(2);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                StartTime.show();
            }
        });

        auto_serial.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //set to 0 as it is filtered on every value selected
                CreditOnlineApplication.MCSerial loSerial = (CreditOnlineApplication.MCSerial) parent.getItemAtPosition(0);

                //set model and serial id
                loContract.setsSerialID(loSerial.lsSerialID);
                mViewModel.SetModelIDxx(loSerial.lsModelIDx);
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

                if (mViewModel.GetProductModel().getModelIDx() == null || mViewModel.GetProductModel().getModelIDx().isEmpty()){
                    Toast.makeText(Activity_MC_Contract.this, "Please select a model", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (loContract.getsSerialID() == null || loContract.getsSerialID().isEmpty()){
                    Toast.makeText(Activity_MC_Contract.this, "Please select a model", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (loContract.getdPurchase() == null || loContract.getdPurchase().isEmpty()){
                    Toast.makeText(Activity_MC_Contract.this, "Please enter purchase date", Toast.LENGTH_SHORT).show();
                    return;
                }

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
                                        finish();
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
            Toast.makeText(Activity_MC_Contract.this, "Could not find installment info", Toast.LENGTH_SHORT).show();
            return;
        }

        if (loCashPrice == null){
            Toast.makeText(Activity_MC_Contract.this, "Could not find cash info", Toast.LENGTH_SHORT).show();
            return;
        }

        //set default minimum down, if downpayment is lesser than requried amount
        if (Double.parseDouble(mViewModel.GetProductModel().getDownPaym()) < loInstallment.getMinimumDownpayment()){

            InitMessage(0, R.drawable.baseline_error_24, "Downpayment does not meet the required amount", "Okay", "", new OnMessageButton() {
                @Override
                public void OnPositive() {
                    mViewModel.GetProductModel().setDownPaym(String.valueOf(loInstallment.getMinimumDownpayment()));
                    InitDisplay(1);
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
                    InitDisplay(1);
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

                        //set computation to contract object
                        loContract.setnDownPaym(Double.parseDouble(mViewModel.GetProductModel().getDownPaym()));
                        loContract.setnAcctTerm(Integer.parseInt(mViewModel.GetProductModel().getTermIDxx()));
                        loContract.setnMonAmort(Double.parseDouble(mViewModel.GetProductModel().getnMonthAmr()));

                        InitDisplay(1);
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
    }

    @SuppressLint("SimpleDateFormat")
    private String GetDateToday(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }else {
            return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        }
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
