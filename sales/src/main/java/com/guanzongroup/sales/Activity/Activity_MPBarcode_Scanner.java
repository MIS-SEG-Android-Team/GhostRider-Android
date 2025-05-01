package com.guanzongroup.sales.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.guanzongroup.sales.Adapter.RecyclerView_Barcodes;
import com.guanzongroup.sales.R;
import com.guanzongroup.sales.ViewModel.VMBarcode;

import org.rmj.g3appdriver.GCircle.room.Entities.EBarcode;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.settings.Activity.Activity_QrCodeScanner;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Activity_MPBarcode_Scanner extends AppCompatActivity {

    private MaterialToolbar toolbar;

    private ConstraintLayout layout_barcodelist;
    private RecyclerView rcv_barcodes;
    private FloatingActionButton fbtn_scan;

    private ScrollView scv_details;

    private LinearLayout layout_payment;
    private MaterialAutoCompleteTextView tie_paytype;
    private LinearLayout layout_finance;
    private MaterialAutoCompleteTextView tie_financer;
    private TextInputEditText tie_amount;

    private LinearLayout layout_personaldetails;
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

    private MutableLiveData<List<String>> barcodeEntries = new MutableLiveData<>();
    private MutableLiveData<Integer> btnState = new MutableLiveData<>(1);

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
        tie_paytype = findViewById(R.id.tie_paytype);
        layout_finance = findViewById(R.id.layout_finance);
        tie_financer = findViewById(R.id.tie_financer);
        tie_amount = findViewById(R.id.tie_amount);

        //todo personal details objects
        layout_personaldetails = findViewById(R.id.layout_personaldetails);
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

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnState.isInitialized()){

                    int state = btnState.getValue() + 1;

                    if (state >= 3){
                        state = 3;
                    }

                    btnState.setValue(state);

                }else {
                    btnState.setValue(1);
                }
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
                            public void onCheckAction(List<String> entries) {
                                barcodeEntries.setValue(entries);
                            }

                            @Override
                            public void onDelete(EBarcode barcode) {

                                initMessage("Delete Barcode?", "Yes", "No", 3, true, new onMessage() {
                                    @Override
                                    public void onPosBtnListener() {

                                        if (barcodeEntries.isInitialized()){
                                            barcodeEntries.getValue().remove(barcode);
                                        }

                                        mViewModel.deleteBarcode(barcode.getBarcodeIdxx());

                                    }

                                    @Override
                                    public void onNegBtnListener() {

                                    }
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

                        btn_next.setText("NEXT");
                        break;

                    case 2:
                        layout_barcodelist.setVisibility(View.GONE);
                        scv_details.setVisibility(View.VISIBLE);

                        layout_payment.setVisibility(View.VISIBLE);
                        layout_personaldetails.setVisibility(View.GONE);

                        btn_next.setText("NEXT");
                        break;

                    case 3:
                        layout_barcodelist.setVisibility(View.GONE);
                        scv_details.setVisibility(View.VISIBLE);

                        layout_payment.setVisibility(View.GONE);
                        layout_personaldetails.setVisibility(View.VISIBLE);

                        btn_next.setText("GENERATE QR");
                        break;

                }

            }
        });
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

}