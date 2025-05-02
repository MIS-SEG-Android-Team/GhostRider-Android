package com.guanzongroup.sales.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.guanzongroup.sales.Adapter.RecyclerView_Barcodes;
import com.guanzongroup.sales.R;
import com.guanzongroup.sales.ViewModel.VMBarcode;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DTownInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBarcode;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.settings.Activity.Activity_QrCodeScanner;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_MPBarcode_Scanner extends AppCompatActivity {

    private MaterialToolbar toolbar;

    private ConstraintLayout layout_barcodelist;
    private RecyclerView rcv_barcodes;
    private FloatingActionButton fbtn_scan;

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
    private TextInputEditText tie_address;
    private MaterialAutoCompleteTextView tie_towncity;

    private ConstraintLayout layout_buttons;
    private MaterialButton btn_previous;
    private MaterialButton btn_next;

    private VMBarcode mViewModel;
    private LoadDialog poLoad;
    private MessageBox poMessage;

    private final HashMap<String, String> loTownMap = new HashMap<>();
    private final HashMap<String, String> laFinancer = new HashMap<>();

    private JSONArray loIEMI = new JSONArray();

    private final MutableLiveData<Integer> btnState = new MutableLiveData<>(1);

    @SuppressLint("NewApi")
    private final ActivityResultLauncher<Intent> poArlBarcode =  registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                Log.d("PhoneBarcode", String.valueOf(result.getResultCode()));

                //todo: check intent action
                if (result.getResultCode() == Activity.RESULT_OK){

                    //todo: check result data
                    if (result.getData().getStringExtra("qrdata") == null){

                        //todo: show message, failed to read empty barcode
                        initMessage("Failed to read barcode", "Okay", "",
                                2, false, new onMessage() {
                                    @Override
                                    public void onPosBtnListener() {

                                    }

                                    @Override
                                    public void onNegBtnListener() {

                                    }
                                });

                    }else {

                        //todo: double check result data
                        if (!result.getData().getStringExtra("qrdata").isEmpty()){

                            //todo: show confirmation, save barcode
                            initMessage("Save Barcode?", "Yes", "No",
                                    3, true, new onMessage() {
                                        @Override
                                        public void onPosBtnListener() {

                                            String barcodeid = "MX01" +
                                                    LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss")) +
                                                    mViewModel.countBarcode() + 1;

                                            EBarcode barcode = new EBarcode();
                                            barcode.setBarcodeIdxx(barcodeid);
                                            barcode.setBarcode(result.getData().getStringExtra("qrdata"));

                                            mViewModel.saveBarcode(barcode);

                                            Toast.makeText(Activity_MPBarcode_Scanner.this, "Barcode saved successfully", Toast.LENGTH_LONG).show();

                                        }

                                        @Override
                                        public void onNegBtnListener() {
                                            Toast.makeText(Activity_MPBarcode_Scanner.this, "Barcode saving cancelled", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }else {

                            //todo: show message, failed to read empty barcode
                            initMessage("Failed to read barcode", "Okay",
                                    "", 2, false, new onMessage() {
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

        initViews();
        initListener();
        initObserVables();

        initPayType();
        initFinancer();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {

        //todo toolbar object
        toolbar = findViewById(R.id.toolbar);

        //todo barcode scan list objects
        layout_barcodelist = findViewById(R.id.layout_barcodelist);
        rcv_barcodes = findViewById(R.id.rcv_barcodes);
        fbtn_scan = findViewById(R.id.fbtn_scan);

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
        tie_address = findViewById(R.id.tie_address);
        tie_towncity = findViewById(R.id.tie_towncity);

        //todo buttons objects
        layout_buttons = findViewById(R.id.layout_buttons);
        btn_previous = findViewById(R.id.btn_previous);
        btn_next = findViewById(R.id.btn_next);

    }

    private void initListener(){

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fbtn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mViewModel.CheckPermission(new VMBarcode.onCheckPermission() {
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

                        initMessage(message, "Okay", "", 2, false, new onMessage() {
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

        fbtn_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkPersonalDetails()){
                    return;
                }

                GenerateQRInfo();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!btnState.isInitialized()){
                    btnState.setValue(1);
                }

                Log.d("PhoneBarcode", String.valueOf(loIEMI));

                Boolean allowNext = false;
                switch (btnState.getValue()){

                    case 1:

                        if (!checkSelectedBarcodes()){

                            allowNext = false;
                            btnState.setValue(1);

                            break;
                        }

                        allowNext = true;
                        break;

                    case 2:

                        if (!checkPaymentDetails()){

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

    private void initObserVables(){

        mViewModel.observeBarcodeList().observe(Activity_MPBarcode_Scanner.this, new Observer<List<EBarcode>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<EBarcode> eBarcodes) {

                if (eBarcodes == null){
                    return;
                }

                RecyclerView_Barcodes adapterBcode =
                        new RecyclerView_Barcodes(Activity_MPBarcode_Scanner.this, eBarcodes, new RecyclerView_Barcodes.onAction() {
                            @Override
                            public void onCheckAction(String lastIDChecked, Integer state) {
                                mViewModel.selectBarcode(lastIDChecked, state);
                            }

                            @Override
                            public void onDelete(String barcodeIDxx) {

                                initMessage("Delete Barcode?", "Yes", "No", 3, true, new onMessage() {
                                    @Override
                                    public void onPosBtnListener() {
                                        mViewModel.deleteBarcode(barcodeIDxx);
                                    }

                                    @Override
                                    public void onNegBtnListener() {}
                                });
                            }
                        });

                adapterBcode.notifyDataSetChanged();

                rcv_barcodes.setAdapter(adapterBcode);
                rcv_barcodes.setLayoutManager(
                        new LinearLayoutManager(Activity_MPBarcode_Scanner.this, LinearLayoutManager.VERTICAL, false));

                if (eBarcodes.size() > 0){
                    layout_buttons.setVisibility(View.VISIBLE);
                    return;
                }

                layout_buttons.setVisibility(View.GONE);
            }
        });

        mViewModel.observeTownProvinceInfo().observe(Activity_MPBarcode_Scanner.this, new Observer<List<DTownInfo.TownProvinceInfo>>() {
            @Override
            public void onChanged(List<DTownInfo.TownProvinceInfo> townProvinceInfos) {

                if (townProvinceInfos == null){
                    return;
                }

                for (DTownInfo.TownProvinceInfo loVal: townProvinceInfos){
                    loTownMap.put(loVal.sTownIDxx, loVal.sTownName +" "+loVal.sProvName);
                }

                List<String> loTowns = new ArrayList<>(loTownMap.values());
                ArrayAdapter<String> loAdapter = new ArrayAdapter<>(Activity_MPBarcode_Scanner.this, R.layout.support_simple_spinner_dropdown_item, loTowns);

                tie_towncity.setAdapter(loAdapter);

            }
        });

        mViewModel.observeCheckedBarcodeList().observe(Activity_MPBarcode_Scanner.this, new Observer<List<EBarcode>>() {
            @Override
            public void onChanged(List<EBarcode> eBarcodes) {

                if (eBarcodes == null){
                    return;
                }

                List<String> barcodeEntries = new ArrayList<>();
                for (EBarcode loVal: eBarcodes){
                    barcodeEntries.add(loVal.getBarcode());
                }

                if (barcodeEntries.size() > 0){
                    loIEMI = new JSONArray(barcodeEntries);
                }else {
                    loIEMI = new JSONArray();
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

    private void initPayType(){

        List<String> loPayTypes = List.of(
                "Cash",
                "Credit Card",
                "Financing"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, loPayTypes);
        tie_paytype.setAdapter(adapter);
    }

    private void initFinancer(){

        laFinancer.put("C00118000296", "NorthPoint Excelsior Credit Corporation");
        laFinancer.put("C0W110000001", "Samsung Electronics Philippines Corporation");
        laFinancer.put("GCO116000731", "Home Credit Philippines");
        laFinancer.put("GCO116000734", "Flexi, Finance");
        laFinancer.put("GCO121000006", "GCash");

        List<String> financers = new ArrayList<>(laFinancer.values());
        tie_financer.setAdapter(new ArrayAdapter<String>(Activity_MPBarcode_Scanner.this, R.layout.support_simple_spinner_dropdown_item, financers));

    }

    private Boolean checkSelectedBarcodes(){

        if (loIEMI == null || loIEMI.length() <= 0){

            initMessage("Please select barcodes from list", "Okay", "", 2, false, new onMessage() {
                @Override
                public void onPosBtnListener() {}

                @Override
                public void onNegBtnListener() {}
            });

            return false;
        }

        return true;

    }

    private Boolean checkPaymentDetails(){

        if (tie_paytype.getText() == null || tie_paytype.getText().toString().isEmpty()){

            initMessage("Payment type is required", "Okay", "", 2, false, new onMessage() {
                @Override
                public void onPosBtnListener() {}
                @Override
                public void onNegBtnListener() {}
            });

            return false;
        }

        if (tie_paytype.getText().toString().equalsIgnoreCase("financing")){

            if (tie_financer.getText() == null){

                initMessage("Financer is required", "Okay", "", 2, false, new onMessage() {
                    @Override
                    public void onPosBtnListener() {}
                    @Override
                    public void onNegBtnListener() {}
                });

                return false;
            }

            if (getAdapterID(laFinancer, tie_financer.getText().toString()).isEmpty()){

                initMessage("Financer is invalid", "Okay", "", 2, false, new onMessage() {
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

    private Boolean checkPersonalDetails(){

        if (tie_lname.getText() == null || tie_lname.getText().toString().isEmpty()){

            initMessage("Lastname is required", "Okay", "", 2, false, new onMessage() {
                @Override
                public void onPosBtnListener() {}

                @Override
                public void onNegBtnListener() {}
            });

            return false;
        }

        if (tie_fname.getText() == null || tie_fname.getText().toString().isEmpty()){

            initMessage("Firstname is required", "Okay", "", 2, false, new onMessage() {
                @Override
                public void onPosBtnListener() {}

                @Override
                public void onNegBtnListener() {}
            });

            return false;
        }

        if (tie_towncity.getText() == null || tie_towncity.getText().toString().isEmpty()){

            initMessage("Town/City is required", "Okay", "", 2, false, new onMessage() {
                @Override
                public void onPosBtnListener() {}

                @Override
                public void onNegBtnListener() {}
            });

            return false;
        }

        if (getAdapterID(loTownMap, tie_towncity.getText().toString()).isEmpty()){

            initMessage("Town/City is invalid", "Okay", "", 2, false, new onMessage() {
                @Override
                public void onPosBtnListener() {}

                @Override
                public void onNegBtnListener() {}
            });

            return false;
        }

        return true;
    }

    private void GenerateQRInfo(){

        try {

            if (loIEMI != null){

                if (loIEMI.length() > 0){

                    JSONObject loQRInfo = new JSONObject();

                    loQRInfo.put("sPaymInfo", ParsePaymentInfo());
                    loQRInfo.put("sCustInfo", ParsePersonalInfo());

                    loQRInfo.put("sSerialNo", loIEMI);

                    mViewModel.generateQR(loQRInfo, new VMBarcode.onGenerateQR() {
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

                            initMessage(message, "Okay", "", 2, false, new onMessage() {
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
                loPaymentInfo.put("financer", getAdapterID(laFinancer, tie_financer.getText().toString()));
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
        loPersonalInfo.put("address", tie_address.getText().toString());
        loPersonalInfo.put("townID", getAdapterID(loTownMap, tie_towncity.getText().toString()));

        return loPersonalInfo;
    }

    private String getAdapterID(HashMap<String, String> map, String value){

        String returnID = "";
        for (Map.Entry<String, String> entry : map.entrySet()){

            if (entry.getValue().equalsIgnoreCase(value)){
                returnID = entry.getKey();
                break;
            }
        }

        return returnID;
    }

    private void initMessage(String message, String btnPos, String btnNeg,
                             int type, Boolean forConfirm, onMessage callback){

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
    private interface onMessage{
        void onPosBtnListener();
        void onNegBtnListener();
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

}