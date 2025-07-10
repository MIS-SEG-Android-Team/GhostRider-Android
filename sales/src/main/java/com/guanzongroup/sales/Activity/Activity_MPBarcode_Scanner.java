package com.guanzongroup.sales.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.guanzongroup.sales.Adapter.ExpandableBarcodeAdapter;
import com.guanzongroup.sales.Dialogs.Dialog_TransactionPIN;
import com.guanzongroup.sales.R;
import com.guanzongroup.sales.ViewModel.VMBarcode;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.room.Entities.EBarcode;
import org.rmj.g3appdriver.GCircle.room.Entities.EBarcodeDetail;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.settings.Activity.Activity_QrCodeScanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_MPBarcode_Scanner extends AppCompatActivity {

    private MaterialToolbar toolbar;

    private ConstraintLayout layout_barcodelist;
    private ExpandableListView exp_barcodes;
    private FloatingActionButton fbtn_manual;
    private FloatingActionButton fbtn_scan;
    private FloatingActionButton fbtn_delete;

    private ScrollView scv_details;

    private LinearLayout layout_payment;
    private TextInputLayout til_financer;
    private MaterialAutoCompleteTextView tie_paytype;
    private MaterialAutoCompleteTextView tie_financer;

    private ConstraintLayout layout_personaldetails;
    private FloatingActionButton fbtn_generate;
    private TextInputEditText tie_lname;
    private TextInputEditText tie_fname;
    private TextInputEditText tie_mname;
    private TextInputEditText tie_suffix;
    private TextInputEditText tie_mobile;

    private MaterialButton btn_previous;
    private MaterialButton btn_next;

    private VMBarcode mViewModel;
    private LoadDialog poLoad;
    private MessageBox poMessage;

    private final HashMap<String, String> laFinancer = new HashMap<>();
    private JSONArray loIEMI = new JSONArray();

    private final MutableLiveData<Integer> btnState = new MutableLiveData<>(1);

    private final ActivityResultLauncher<Intent> poArlBarcode =  registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                //todo: check intent action
                if (result.getResultCode() == Activity.RESULT_OK){

                    //todo: check result data
                    if (result.getData().getStringExtra("qrdata") == null){

                        //todo: show message, failed to read empty barcode
                        InitMessage("Failed to read barcode", "Okay", "",
                                2, false, new OnMessage() {
                                    @Override
                                    public void onPosBtnListener() {}
                                    @Override
                                    public void onNegBtnListener() {}
                                });

                    }else {

                        //todo: double check result data
                        if (!result.getData().getStringExtra("qrdata").isEmpty()){
                            SaveBarcode(result.getData().getStringExtra("qrdata"));
                        }else {

                            //todo: show message, failed to read empty barcode
                            InitMessage("Failed to read barcode", "Okay",
                                    "", 2, false, new OnMessage() {
                                        @Override
                                        public void onPosBtnListener() {

                                        }

                                        @Override
                                        public void onNegBtnListener() {

                                        }
                                    });
                        }

                    }

                }else {
                    if (result.getResultCode() == Activity.RESULT_CANCELED){
                        //todo: show toast message, barcode cancelled
                        Toast.makeText(Activity_MPBarcode_Scanner.this, "Barcode reading cancelled", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> poArlPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), granted-> {

                //todo: check camera permission, if granted then launch barcode scanner else show toast
                if (granted){
                    Intent loIntent = new Intent(this, Activity_MPBarcode_Scanner.class);
                    poArlBarcode.launch(loIntent);
                }else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mpbarcode_scanner);

        mViewModel = new ViewModelProvider(this).get(VMBarcode.class);
        poLoad = new LoadDialog(this);
        poMessage = new MessageBox(this);

        poMessage.initDialog();

        InitViews();
        InitListener();
        InitObserVables();

        InitPayType();
        InitFinancer();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void InitViews() {

        //todo toolbar object
        toolbar = findViewById(R.id.toolbar);

        //todo barcode scan list objects
        layout_barcodelist = findViewById(R.id.layout_barcodelist);
        exp_barcodes = findViewById(R.id.exp_barcodes);
        fbtn_scan = findViewById(R.id.fbtn_scan);
        fbtn_manual = findViewById(R.id.fbtn_manual);
        fbtn_delete = findViewById(R.id.fbtn_delete);

        //todo details objects
        scv_details = findViewById(R.id.scv_details);

        //todo payment objects
        layout_payment = findViewById(R.id.layout_payment);
        til_financer = findViewById(R.id.til_financer);
        tie_paytype = findViewById(R.id.tie_paytype);
        tie_financer = findViewById(R.id.tie_financer);

        //todo personal details objects
        layout_personaldetails = findViewById(R.id.layout_personaldetails);
        fbtn_generate = findViewById(R.id.fbtn_generate);
        tie_lname = findViewById(R.id.tie_lname);
        tie_fname = findViewById(R.id.tie_fname);
        tie_mname = findViewById(R.id.tie_mname);
        tie_suffix = findViewById(R.id.tie_suffix);
        tie_mobile = findViewById(R.id.tie_mobile);

        //todo buttons objects
        btn_previous = findViewById(R.id.btn_previous);
        btn_next = findViewById(R.id.btn_next);

    }

    private void InitMessage(String message, String btnPos, String btnNeg,
                             int type, Boolean forConfirm, OnMessage callback){

        poMessage.initDialog();
        poMessage.setTitle("Guanzon Connect");
        poMessage.setMessage(message);

        switch (type){

            case 1: //todo: success message
                poMessage.setIcon(R.drawable.baseline_message_24);
                break;
            case 2: //todo: error message
                poMessage.setIcon(R.drawable.baseline_error_24);
                break;
            case 3: //todo: confirm message
                poMessage.setIcon(R.drawable.baseline_contact_support_24);
                break;
            default:
                poMessage.setIcon(R.drawable.baseline_message_24);
                break;
        }

        poMessage.setPositiveButton(btnPos, new MessageBox.DialogButton() {
            @Override
            public void OnButtonClick(View view, AlertDialog dialog) {
                dialog.dismiss();
                callback.onPosBtnListener();
            }
        });

        if (forConfirm){
            poMessage.setNegativeButton(btnNeg, new MessageBox.DialogButton() {
                @Override
                public void OnButtonClick(View view, AlertDialog dialog) {
                    dialog.dismiss();
                    callback.onNegBtnListener();
                }
            });
        }

        poMessage.show();

    }

    private void InitListener(){

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fbtn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mViewModel.CheckPermission(new VMBarcode.OnCheckPermission() {
                    @Override
                    public void onChecking(String message) {
                        poLoad.initDialog("Guanzon Connect",message, false);
                        poLoad.show();
                    }

                    @Override
                    public void onPermissionGranted() {
                        poLoad.dismiss();

                        poArlBarcode.launch(new Intent(Activity_MPBarcode_Scanner.this, Activity_QrCodeScanner.class));
                    }

                    @Override
                    public void onPermissionDenied(String message) {
                        poLoad.dismiss();

                        InitMessage(message, "Okay", "", 2, false, new OnMessage() {
                            @Override
                            public void onPosBtnListener() {
                                poArlPermission.launch(Manifest.permission.CAMERA);
                            }

                            @Override
                            public void onNegBtnListener() {

                            }
                        });
                    }
                });
            }
        });

        fbtn_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_BarcodeManual dialog_barcodeManual = new Dialog_BarcodeManual();
                dialog_barcodeManual.initDialogBarcodeManual(new Dialog_BarcodeManual.onConfirm() {
                    @Override
                    public void onConfirm(String serial) {
                        SaveBarcode(serial);
                    }
                });
            }
        });

        fbtn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InitMessage("Are you sure you want to delete all selected barcodes?", "Yes", "No", 2, true, new OnMessage() {
                    @Override
                    public void onPosBtnListener() {

                        List<EBarcode> loVal = mViewModel.GetCheckedBarcodeList();
                        for (EBarcode barcode: loVal){
                            mViewModel.DeleteBarcode(barcode.getBarcodeIdxx());
                        }
                    }

                    @Override
                    public void onNegBtnListener() {}
                });
            }
        });

        fbtn_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!CheckPersonalDetails()){
                    return;
                }

                StringBuilder loSerials = new StringBuilder();
                for (int i = 0; i < loIEMI.length(); i++){
                    loSerials.append("• "+loIEMI.optString(i)).append("\n");
                }

                InitMessage("Select action for your data", "SEND DETAILS", "GENERATE QR", 3, true, new OnMessage() {
                    @Override
                    public void onPosBtnListener() {

                        InitMessage("Is your information correct?\n"+"Here is the summarry of your selected entries\n\n"+loSerials+"\n\nSave transaction now?", "Yes", "No", 3, true, new OnMessage() {
                            @Override
                            public void onPosBtnListener() {
                                SubmitBarcodes();
                            }

                            @Override
                            public void onNegBtnListener() {}
                        });
                    }

                    @Override
                    public void onNegBtnListener() {
                        GenerateQRInfo();
                    }
                });
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!btnState.isInitialized()){
                    btnState.setValue(1);
                }

                boolean allowNext = false;
                switch (btnState.getValue()){

                    case 1:

                        if (!CheckSelectedBarcodes()){
                            allowNext = false;
                            btnState.setValue(1);
                            break;
                        }
                        allowNext = true;
                        break;

                    case 2:

                        if (!CheckPaymentDetails()){
                            allowNext = false;
                            btnState.setValue(2);
                            break;
                        }
                        allowNext = true;
                        break;

                }

                if (!allowNext){
                    return;
                }

                int state = btnState.getValue() + 1;
                if (state >= 3){
                    state = 3;
                }

                btnState.setValue(state);

            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnState.isInitialized()){

                    int state = btnState.getValue() - 1;

                    if (state <= 0){
                        state = 1;
                    }

                    btnState.setValue(state);

                }else {
                    btnState.setValue(1);
                }
            }
        });

        tie_paytype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 2){
                    til_financer.setVisibility(View.VISIBLE);
                }else {
                    til_financer.setVisibility(View.GONE);
                }

            }
        });
    }

    private void InitObserVables(){

        mViewModel.ObserveBarcodeList().observe(Activity_MPBarcode_Scanner.this, new Observer<List<EBarcode>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<EBarcode> eBarcodes) {

                if (eBarcodes == null){
                    return;
                }

                HashMap<String, List<EBarcodeDetail>> barcodeITems = new HashMap<>();
                for (EBarcode barcode: eBarcodes){

                    mViewModel.GetBarcodeItems(barcode.getBarcodeIdxx()).observe(Activity_MPBarcode_Scanner.this, new Observer<List<EBarcodeDetail>>() {
                        @Override
                        public void onChanged(List<EBarcodeDetail> eBarcodeDetails) {
                            if (eBarcodeDetails == null){
                                return;
                            }
                            barcodeITems.put(barcode.getBarcodeIdxx(), eBarcodeDetails);
                        }
                    });
                }

                ExpandableBarcodeAdapter expAdapter = new ExpandableBarcodeAdapter(
                        Activity_MPBarcode_Scanner.this, eBarcodes, barcodeITems, new ExpandableBarcodeAdapter.OnCheckedListener() {
                    @Override
                    public void OnChecked(Integer checkStatus, String barcode) {
                        mViewModel.SelectBarcode(barcode, checkStatus);
                    }
                });

                exp_barcodes.setAdapter(expAdapter);
                expAdapter.notifyDataSetChanged();
            }
        });

        mViewModel.ObserveCheckedBarcodeList().observe(Activity_MPBarcode_Scanner.this, new Observer<List<EBarcodeDetail>>() {
            @Override
            public void onChanged(List<EBarcodeDetail> eBarcodes) {

                List<String> barcodeEntries = new ArrayList<>();
                loIEMI = new JSONArray();

                for (EBarcodeDetail loVal: eBarcodes){
                    barcodeEntries.add(loVal.getsSerialID());
                }

                loIEMI = new JSONArray(barcodeEntries);

                if (loIEMI.length() > 0){
                    btn_next.setEnabled(true);
                    fbtn_delete.setVisibility(View.VISIBLE);
                }else {
                    btn_next.setEnabled(false);
                    fbtn_delete.setVisibility(View.GONE);
                    return;
                }
            }
        });

        btnState.observe(Activity_MPBarcode_Scanner.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                if (integer == null){
                    return;
                }

                switch (integer){

                    case 1:

                        layout_barcodelist.setVisibility(View.VISIBLE);
                        scv_details.setVisibility(View.GONE);

                        btn_next.setEnabled(true);
                        btn_previous.setEnabled(false);

                        break;

                    case 2:

                        layout_barcodelist.setVisibility(View.GONE);
                        scv_details.setVisibility(View.VISIBLE);

                        layout_payment.setVisibility(View.VISIBLE);
                        layout_personaldetails.setVisibility(View.GONE);

                        btn_next.setEnabled(true);
                        btn_previous.setEnabled(true);

                        break;

                    case 3:

                        layout_barcodelist.setVisibility(View.GONE);
                        scv_details.setVisibility(View.VISIBLE);

                        layout_payment.setVisibility(View.GONE);
                        layout_personaldetails.setVisibility(View.VISIBLE);

                        btn_next.setEnabled(false);
                        btn_previous.setEnabled(true);

                        break;

                }

            }
        });

    }

    private void InitPayType(){

        List<String> loPayTypes = List.of(
                "Cash",
                "Credit Card",
                "Financing"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, loPayTypes);
        tie_paytype.setAdapter(adapter);
    }

    private void InitFinancer(){

        laFinancer.put("C00118000296", "NorthPoint Excelsior Credit Corporation");
        laFinancer.put("C0W110000001", "Samsung Electronics Philippines Corporation");
        laFinancer.put("GCO116000731", "Home Credit Philippines");
        laFinancer.put("GCO116000734", "Flexi, Finance");
        laFinancer.put("GCO121000006", "GCash");

        List<String> financers = new ArrayList<>(laFinancer.values());
        tie_financer.setAdapter(new ArrayAdapter<String>(Activity_MPBarcode_Scanner.this, R.layout.support_simple_spinner_dropdown_item, financers));

    }

    private void SaveBarcode(String sBarcodeIdxx){

        mViewModel.DownloadBarcode(sBarcodeIdxx, new VMBarcode.OnDownloadBundles() {
            @Override
            public void OnLoad(String title, String message) {
                poLoad.initDialog(title, message, false);
                poLoad.show();
            }

            @Override
            public void OnSuccess() {
                poLoad.dismiss();
                Toast.makeText(Activity_MPBarcode_Scanner.this, "Barcode saved successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnFailed(String message) {
                poLoad.dismiss();

                InitMessage(message, "Okay", "", 2, false, new OnMessage() {
                    @Override
                    public void onPosBtnListener() {}

                    @Override
                    public void onNegBtnListener() {}
                });
            }
        });

    }

    private void SubmitBarcodes(){

        try {

            if (loIEMI != null) {

                if (loIEMI.length() > 0) {

                    JSONObject loQRInfo = new JSONObject();

                    loQRInfo.put("sPaymInfo", ParsePaymentInfo());
                    loQRInfo.put("sCustInfo", ParsePersonalInfo());

                    loQRInfo.put("sSerialNo", loIEMI);

                    mViewModel.SubmitBarcodes(loQRInfo, new VMBarcode.OnSubmitBarcodes() {
                        @Override
                        public void onLoad(String title, String message) {
                            poLoad.initDialog(title, message, false);
                            poLoad.show();
                        }

                        @Override
                        public void onSuccess(String transNox) {
                            poLoad.dismiss();

                            new Dialog_TransactionPIN(Activity_MPBarcode_Scanner.this)
                                    .initDialog(transNox,
                                            "Transaction PIN. \n \n Please proceed to counter and submit this reference number."
                                    );
                        }

                        @Override
                        public void onFailed(String message) {
                            poLoad.dismiss();

                            InitMessage(message, "Okay", "", 2, false, new OnMessage() {
                                @Override
                                public void onPosBtnListener() {}

                                @Override
                                public void onNegBtnListener() {}
                            });
                        }
                    });
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void GenerateQRInfo(){

        try {

            if (loIEMI != null){

                if (loIEMI.length() > 0){

                    JSONObject loQRInfo = new JSONObject();

                    loQRInfo.put("sPaymInfo", ParsePaymentInfo());
                    loQRInfo.put("sCustInfo", ParsePersonalInfo());

                    loQRInfo.put("sSerialNo", loIEMI);

                    mViewModel.GenerateQR(loQRInfo, new VMBarcode.OnGenerateQR() {
                        @Override
                        public void onGenerating() {

                            poLoad.initDialog("Guanzon Connect", "Generating QR...", false);
                            poLoad.show();
                        }

                        @Override
                        public void onQRGenerated(Bitmap bitmap) {
                            poLoad.dismiss();

                            Dialog_QRImage loQRImage = new Dialog_QRImage();
                            loQRImage.initDialogQRImage(bitmap);
                        }

                        @Override
                        public void onQRGenerationFailed(String message) {
                            poLoad.dismiss();

                            InitMessage(message, "Okay", "", 2, false, new OnMessage() {
                                @Override
                                public void onPosBtnListener() {}

                                @Override
                                public void onNegBtnListener() {}
                            });
                        }
                    });

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private Boolean CheckSelectedBarcodes(){

        if (loIEMI == null || loIEMI.length() <= 0){

            InitMessage("Please select barcodes from list", "Okay", "", 2, false, new OnMessage() {
                @Override
                public void onPosBtnListener() {}

                @Override
                public void onNegBtnListener() {}
            });

            return false;
        }

        return true;

    }

    private Boolean CheckPaymentDetails(){

        if (tie_paytype.getText() == null || tie_paytype.getText().toString().isEmpty()){

            InitMessage("Payment type is required", "Okay", "", 2, false, new OnMessage() {
                @Override
                public void onPosBtnListener() {}
                @Override
                public void onNegBtnListener() {}
            });

            return false;
        }

        if (tie_paytype.getText().toString().equalsIgnoreCase("financing")){

            if (tie_financer.getText() == null){

                InitMessage("Financer is required", "Okay", "", 2, false, new OnMessage() {
                    @Override
                    public void onPosBtnListener() {}
                    @Override
                    public void onNegBtnListener() {}
                });

                return false;
            }

            if (GetAdapterID(laFinancer, tie_financer.getText().toString()).isEmpty()){

                InitMessage("Financer is invalid", "Okay", "", 2, false, new OnMessage() {
                    @Override
                    public void onPosBtnListener() {}
                    @Override
                    public void onNegBtnListener() {}
                });

                return false;
            }
        }

        return true;
    }

    private Boolean CheckPersonalDetails(){

        if (tie_lname.getText() == null || tie_lname.getText().toString().isEmpty()){

            InitMessage("Lastname is required", "Okay", "", 2, false, new OnMessage() {
                @Override
                public void onPosBtnListener() {}

                @Override
                public void onNegBtnListener() {}
            });

            return false;
        }

        if (tie_fname.getText() == null || tie_fname.getText().toString().isEmpty()){

            InitMessage("Firstname is required", "Okay", "", 2, false, new OnMessage() {
                @Override
                public void onPosBtnListener() {}

                @Override
                public void onNegBtnListener() {}
            });

            return false;
        }

        if (tie_mobile.getText() != null && !tie_mobile.getText().toString().isEmpty()){

            String sMobile = tie_mobile.getText().toString();

            if (!sMobile.matches("[0-9]{11}")){

                InitMessage("Mobile must be 11 digits and contain only numbers", "Okay", "", 2, false, new OnMessage() {
                    @Override
                    public void onPosBtnListener() {}

                    @Override
                    public void onNegBtnListener() {}
                });

                return false;
            }

        }

        return true;
    }

    private JSONObject ParsePaymentInfo() throws Exception{

        JSONObject loPaymentInfo = new JSONObject();

        switch (tie_paytype.getText().toString().toLowerCase()){

            case "cash":
                loPaymentInfo.put("paymentForm", "0");
                break;
            case "credit card":
                loPaymentInfo.put("paymentForm", "1");
                break;
            case "financing":
                loPaymentInfo.put("paymentForm", "2");
                loPaymentInfo.put("financer", GetAdapterID(laFinancer, tie_financer.getText().toString()));
                break;
            default:
                loPaymentInfo.put("paymentForm", "0");
                break;
        }

        return loPaymentInfo;

    }

    private JSONObject ParsePersonalInfo() throws Exception{

        JSONObject loPersonalInfo = new JSONObject();

        loPersonalInfo.put("firstName", tie_fname.getText().toString());
        loPersonalInfo.put("middleName", tie_mname.getText().toString());
        loPersonalInfo.put("lastName", tie_lname.getText().toString());
        loPersonalInfo.put("suffixName", tie_suffix.getText().toString());
        loPersonalInfo.put("mobileNumber", tie_mobile.getText().toString());

        return loPersonalInfo;
    }

    private String GetAdapterID(HashMap<String, String> map, String value){

        String returnID = "";
        for (Map.Entry<String, String> entry : map.entrySet()){

            if (entry.getValue().equalsIgnoreCase(value)){
                returnID = entry.getKey();
                break;
            }
        }

        return returnID;
    }

    public class Dialog_QRImage{

        private AlertDialog poDialogx;

        private ImageView img_QRCode;
        private MaterialButton btn_close;

        public void initDialogQRImage(Bitmap bitmapQR){

            View view = LayoutInflater.from(Activity_MPBarcode_Scanner.this).inflate(R.layout.dialog_qrbarcode, null, false);

            AlertDialog.Builder loBuilder =  new AlertDialog.Builder(Activity_MPBarcode_Scanner.this);
            loBuilder.setCancelable(false)
                    .setView(view);
            poDialogx = loBuilder.create();

            img_QRCode = view.findViewById(R.id.img_QRCode);
            btn_close = view.findViewById(R.id.btn_close);

            img_QRCode.setImageBitmap(bitmapQR);
            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    poDialogx.dismiss();
                }
            });

            poDialogx.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            poDialogx.getWindow().getAttributes().windowAnimations = org.rmj.g3appdriver.R.style.PopupAnimation;
            poDialogx.show();

        }
    }

    public class Dialog_BarcodeManual{

        private AlertDialog poDialogx;

        private TextInputEditText tie_paytype;
        private MaterialButton btn_add;

        public void initDialogBarcodeManual(onConfirm callback){

            View view = LayoutInflater.from(Activity_MPBarcode_Scanner.this).inflate(R.layout.dialog_manual_barcode, null, false);

            AlertDialog.Builder loBuilder =  new AlertDialog.Builder(Activity_MPBarcode_Scanner.this);
            loBuilder.setCancelable(true)
                    .setView(view);
            poDialogx = loBuilder.create();

            tie_paytype = view.findViewById(R.id.tie_paytype);
            btn_add = view.findViewById(R.id.btn_add);

            tie_paytype.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                        if (!s.isEmpty()){
                            btn_add.setEnabled(true);
                        }else {
                            btn_add.setEnabled(false);
                        }
                    }else {
                        if (s.length() > 0){
                            btn_add.setEnabled(true);
                        }else {
                            btn_add.setEnabled(false);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    poDialogx.dismiss();
                    callback.onConfirm(tie_paytype.getText().toString());
                }
            });

            poDialogx.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            poDialogx.getWindow().getAttributes().windowAnimations = org.rmj.g3appdriver.R.style.PopupAnimation;
            poDialogx.show();

        }

        interface onConfirm{
            void onConfirm(String serial);
        }

    }

    private interface OnMessage {
        void onPosBtnListener();
        void onNegBtnListener();
    }

}