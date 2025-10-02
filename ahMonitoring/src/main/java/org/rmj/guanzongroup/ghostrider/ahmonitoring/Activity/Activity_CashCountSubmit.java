/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 6/8/21 4:50 PM
 * project file last modified : 6/8/21 4:50 PM
 */

package org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CashCount.QuickSearchNames;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Etc.DialogKwikSearch;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel.VMCashCountSubmit;

import java.util.List;
import java.util.Objects;

public class Activity_CashCountSubmit extends AppCompatActivity {
    public static final String TAG = Activity_CashCountSubmit.class.getSimpleName();
    private VMCashCountSubmit mViewModel;

    private TextInputEditText txtCurr_DateTime,
            txtRequestID,
            txtOfficialReceipt,
            txtSalesInvoice,
            txtProvisionalReceipt,
            txtCollectionReceipt,
            txtORNorthPoint,
            txtPRNorthPoint,
            txtDeliveryRcpt,
            txtPettyCashxxx,
            txtTotalSalesxx,
            txtTransNox,
            txtRemarksx;
    private MaterialButton btnSendToServer;

    private MaterialTextView lblBranch, lblAddxx;

    private MaterialButton btnQuickSearch;

    private LoadDialog poDialogx;
    private MessageBox poMessage;

    private String BranchCd = "";
    private String EmployID = ""; //EmployeeID of requesting employee
    
    private JSONObject params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VMCashCountSubmit.class);
        setContentView(R.layout.activity_cash_count_submit);
        initWidgets();

        BranchCd = getIntent().getStringExtra("BranchCd");
        mViewModel.getSelfieLogBranchInfo().observe(Activity_CashCountSubmit.this, eBranchInfo -> {
            try {
                params = new JSONObject(getIntent().getStringExtra("params"));
                txtTotalSalesxx.setText(FormatUIText.getCurrencyUIFormat(params.getString("nTotSales")));
                txtCurr_DateTime.setText(new AppConstants().CURRENT_DATE_WORD);
                lblBranch.setText(eBranchInfo.getBranchNm());
                lblAddxx.setText(eBranchInfo.getAddressx());
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        
        btnQuickSearch.setOnClickListener(v -> mViewModel.GetSearchList(Objects.requireNonNull(txtRequestID.getText()).toString(), new VMCashCountSubmit.OnKwikSearchCallBack() {
            @Override
            public void onStartKwikSearch() {
                poDialogx.initDialog("Cash Count", "Searching name of employee. Please wait...", false);
                poDialogx.show();
            }

            @Override
            public void onSuccessKwikSearch(List<QuickSearchNames> infoList) {
                poDialogx.dismiss();
                DialogKwikSearch loDialog = new DialogKwikSearch(Activity_CashCountSubmit.this, infoList);
                loDialog.initDialogKwikSearch((dialog, name, employID) -> {
                    txtRequestID.setText(name);
                    EmployID = employID;
                    loDialog.dismiss();
                });
                loDialog.show();
            }

            @Override
            public void onKwikSearchFailed(String message) {
                poDialogx.dismiss();
                poMessage.initDialog();
                poMessage.setIcon(R.drawable.baseline_error_24);
                poMessage.setTitle("Cash Count");
                poMessage.setMessage(message);
                poMessage.setPositiveButton("Okay", (view, dialog) ->{
                    dialog.dismiss();
                });
                poMessage.show();
            }
        }));

        btnSendToServer.setOnClickListener(v->{
            try{
                double lnPetty = 0.0;
                if(!txtPettyCashxxx.getText().toString().trim().isEmpty()){
                    lnPetty = Double.parseDouble(Objects.requireNonNull(txtPettyCashxxx.getText()).toString().replace(",", ""));
                }
                params.put("sTransNox", Objects.requireNonNull(txtTransNox.getText()).toString());
                params.put("sBranchCd", BranchCd);
                params.put("nPettyAmt", lnPetty);
                params.put("sORNoxxxx", Objects.requireNonNull(txtOfficialReceipt.getText()).toString());
                params.put("sSINoxxxx", Objects.requireNonNull(txtSalesInvoice.getText()).toString());
                params.put("sPRNoxxxx", Objects.requireNonNull(txtProvisionalReceipt.getText()).toString());
                params.put("sCRNoxxxx", Objects.requireNonNull(txtCollectionReceipt.getText()).toString());
                params.put("sORNoxNPt", Objects.requireNonNull(txtORNorthPoint.getText()).toString());
                params.put("sPRNoxNPt", Objects.requireNonNull(txtPRNorthPoint.getText()).toString());
                params.put("sDRNoxxxx", Objects.requireNonNull(txtDeliveryRcpt.getText()).toString());
                params.put("dTransact", AppConstants.CURRENT_DATE());
                params.put("dEntryDte", new AppConstants().DATE_MODIFIED);
                params.put("sReqstdBy", EmployID);
                params.put("sRemarksx", Objects.requireNonNull(txtRemarksx.getText()).toString());
                
                mViewModel.SaveCashCount(params, new VMCashCountSubmit.OnSaveCashCountCallBack() {
                    @Override
                    public void OnSaving() {
                        poDialogx.initDialog("Cash Count", "Saving cash count entry. Please wait...", false);
                        poDialogx.show();
                    }

                    @Override
                    public void OnSuccess() {
                        poDialogx.dismiss();
                        poMessage.initDialog();
                        poMessage.setIcon(R.drawable.baseline_message_24);
                        poMessage.setTitle("Cast Count");
                        poMessage.setMessage("Cash count has been saved successfully.");
                        poMessage.setPositiveButton("Okay", (view, dialog) ->{
                            if(BranchCd.charAt(0) == 'M') {
                                Intent loIntent = new Intent(Activity_CashCountSubmit.this, Activity_Inventory.class);
                                loIntent.putExtra("BranchCd", BranchCd);
                                startActivity(loIntent);
                            }
                            dialog.dismiss();
                            try {
                                Activity_CashCounter.getInstance().finish();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            finish();
                        });
                        poMessage.show();
                    }

                    @Override
                    public void OnSaveToLocal(String message) {
                        poDialogx.dismiss();
                        poMessage.initDialog();
                        poMessage.setIcon(R.drawable.baseline_message_24);
                        poMessage.setTitle("Cast Count");
                        poMessage.setMessage("Cash count has been saved successfully.");
                        poMessage.setPositiveButton("Okay", (view, dialog) ->{
                            if(BranchCd.charAt(0) == 'M') {
                                Intent loIntent = new Intent(Activity_CashCountSubmit.this, Activity_Inventory.class);
                                loIntent.putExtra("BranchCd", BranchCd);
                                startActivity(loIntent);
                            }
                            dialog.dismiss();
                            Activity_CashCounter.getInstance().finish();
                            finish();
                        });
                        poMessage.show();
                    }

                    @Override
                    public void OnFailed(String message) {
                        poDialogx.dismiss();
                        poMessage.initDialog();
                        poMessage.setIcon(R.drawable.baseline_error_24);
                        poMessage.setTitle("Cash Count");
                        poMessage.setMessage(message);
                        poMessage.setPositiveButton("Okay", (view, dialog) -> {
                            dialog.dismiss();
                        });
                        poMessage.show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    public void initWidgets(){
        Toolbar toolbar = findViewById(R.id.toolbar_cashCountSubmit);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        poDialogx = new LoadDialog(Activity_CashCountSubmit.this);
        poMessage = new MessageBox(Activity_CashCountSubmit.this);
        lblBranch = findViewById(R.id.lbl_headerBranch);
        lblAddxx = findViewById(R.id.lbl_headerAddress);

        txtOfficialReceipt = findViewById(R.id.txtOfficialReceipt);
        txtSalesInvoice = findViewById(R.id.txtSalesInvoice);
        txtProvisionalReceipt = findViewById(R.id.txtProvisionalReceipt);
        txtCollectionReceipt = findViewById(R.id.txtCollectionReceipt);
        txtORNorthPoint = findViewById(R.id.txtORNorthPoint);
        txtPRNorthPoint = findViewById(R.id.txtPRNorthPoint);
        txtDeliveryRcpt = findViewById(R.id.txtDeliveryReceipt);
        txtPettyCashxxx = findViewById(R.id.txtPettyCash);
        txtTotalSalesxx = findViewById(R.id.txtTotalSales);
        txtTransNox = findViewById(R.id.txtTransNox);
        txtRemarksx = findViewById(R.id.txtccRemarks);
        btnSendToServer = findViewById(R.id.btnSendToServer);
        txtCurr_DateTime = findViewById(R.id.txtCurrentDateTime);
        txtRequestID = findViewById(R.id.txt_nameSearch);

        btnQuickSearch = findViewById(R.id.btn_quick_search);

        txtPettyCashxxx.addTextChangedListener(new FormatUIText.CurrencyFormat(txtPettyCashxxx));
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}