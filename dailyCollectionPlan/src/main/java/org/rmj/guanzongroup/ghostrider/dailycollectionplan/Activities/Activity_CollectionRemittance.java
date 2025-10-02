/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.dailyCollectionPlan
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;


import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.Remittance;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.VMCollectionRemittance;

import java.util.Objects;

public class Activity_CollectionRemittance extends AppCompatActivity {

    private VMCollectionRemittance mViewModel;
    private LoadDialog poDialogx;
    private MessageBox poMessage;
    private Remittance poRemit;

    private MaterialToolbar toolbar;
    private MaterialTextView lblBranch,
            lblAddrss,
            lblCltdCash,
            lblCltdChck,
            lblRmtBrnch,
            lblRmtBankx,
            lblRmtOther,
            lblOHCashxx,
            lblOHCheckx;
    public MaterialAutoCompleteTextView txtBranch, txtAccNox;
    private TextInputEditText txtAmount, txtAccName, txtRefNox;
    private LinearLayout linearBrnch, linearBank;
    private TextInputLayout tilRefNox;
    private RadioGroup rgRemitType, rgRemitance;
    private MaterialButton btnRemitAll, btnRemit;

    private String psCltCashx = "0.0",
            psCltCheck = "0.0";

    private boolean isCheck = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VMCollectionRemittance.class);
        setContentView(R.layout.activity_collection_remittance);
        poDialogx = new LoadDialog(Activity_CollectionRemittance.this);
        poMessage = new MessageBox(Activity_CollectionRemittance.this);
        poRemit = new Remittance();
        initWidgets();

        mViewModel.GetUserInfo().observe(this, user -> {
            try{
                lblBranch.setText(user.sBranchNm);
                lblAddrss.setText(user.sAddressx);
                txtBranch.setText(user.sBranchNm);

                poRemit.setsBankAcct(user.sBranchCd);
                poRemit.setcPaymForm("0");
                poRemit.setcRemitTyp("0");
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.GetCollectionMaster().observe(Activity_CollectionRemittance.this, master -> {
            try{
                poRemit.setsTransNox(master.getTransNox());
                mViewModel.GetCashCollection(master.getTransNox()).observe(Activity_CollectionRemittance.this, s -> {
                    try{
                        lblCltdCash.setText("Total Collected Cash: " + FormatUIText.getCurrencyUIFormat(s));
                    } catch (Exception e){
                        e.printStackTrace();
                        lblCltdCash.setText("Total Collected Cash: 0.00");
                    }
                });

                mViewModel.GetCheckCollection(master.getTransNox()).observe(Activity_CollectionRemittance.this, s -> {
                    try{
                        lblCltdChck.setText("Total Collected Check: " + FormatUIText.getCurrencyUIFormat(s));
                    } catch (Exception e){
                        e.printStackTrace();
                        lblCltdChck.setText("Total Collected Check: 0.00");
                    }
                });

                mViewModel.GetBranchRemittance(master.getTransNox()).observe(Activity_CollectionRemittance.this, s -> {
                    try{
                        lblRmtBrnch.setText("Remitted on Branch: " + FormatUIText.getCurrencyUIFormat(s));
                    } catch (Exception e){
                        e.printStackTrace();
                        lblRmtBrnch.setText("Remitted on Branch: 0.00");
                    }
                });

                mViewModel.GetBankRemittance(master.getTransNox()).observe(Activity_CollectionRemittance.this, s -> {
                    try{
                        lblRmtBankx.setText("Remitted on Bank: " + FormatUIText.getCurrencyUIFormat(s));
                    } catch (Exception e){
                        e.printStackTrace();
                        lblRmtBankx.setText("Remitted on Bank: 0.00");
                    }
                });

                mViewModel.GetOtherRemittance(master.getTransNox()).observe(Activity_CollectionRemittance.this, s -> {
                    try{
                        lblRmtOther.setText("Remitted on Payment Partner: " + FormatUIText.getCurrencyUIFormat(s));
                    } catch (Exception e){
                        e.printStackTrace();
                        lblRmtOther.setText("Remitted on Payment Partner: 0.00");
                    }
                });

                mViewModel.GetCashOnHand(master.getTransNox()).observe(Activity_CollectionRemittance.this, s -> {
                    try{
                        lblOHCashxx.setText("Cash-On-Hand: " + FormatUIText.getCurrencyUIFormat(s));
                        psCltCashx = s;
                        poRemit.setnCollectd(Double.parseDouble(psCltCashx));
                        btnRemitAll.setText("Total Cash-On-Hand" + FormatUIText.getCurrencyUIFormat(psCltCashx));
                    } catch (Exception e){
                        e.printStackTrace();
                        lblOHCashxx.setText("Cash-On-Hand: 0.00");
                    }
                });

                mViewModel.GetCheckOnHand(master.getTransNox()).observe(Activity_CollectionRemittance.this, s -> {
                    try{
                        lblOHCheckx.setText("Check-On-Hand: " + FormatUIText.getCurrencyUIFormat(s));
                        psCltCheck = s;
                    } catch (Exception e){
                        e.printStackTrace();
                        lblOHCheckx.setText("Check-On-Hand: 0.00");
                    }
                });

            } catch (Exception e){
                e.printStackTrace();
            }
        });

        rgRemitType.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.rb_remitCash){
                isCheck = false;
                poRemit.setcPaymForm("0");
                poRemit.setnCollectd(Double.parseDouble(psCltCashx));
                txtAmount.setText("");
                if(psCltCashx != null) {
                    if (!psCltCashx.isEmpty()) {
                        btnRemitAll.setText("Total Cash-On-Hand" + FormatUIText.getCurrencyUIFormat(psCltCashx));
                    } else {
                        btnRemitAll.setText("No Cash On Hand");
                    }
                } else {
                    btnRemitAll.setText("No Cash On Hand");
                }
            } else if(checkedId == R.id.rb_remitCheck){
                isCheck = true;
                poRemit.setcPaymForm("1");
                poRemit.setnCollectd(Double.parseDouble(psCltCheck));
                txtAmount.setText("");
                if(psCltCheck != null) {
                    if (!psCltCheck.isEmpty()) {
                        btnRemitAll.setText("Total Check-On-Hand" + FormatUIText.getCurrencyUIFormat(psCltCheck));
                    } else {
                        btnRemitAll.setText("No Check Payment On Hand");
                    }
                } else {
                    btnRemitAll.setText("No Check Payment On Hand");
                }
            }
        });

        rgRemitance.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_remitBranch) {
                linearBrnch.setVisibility(View.VISIBLE);
                linearBank.setVisibility(View.GONE);
                tilRefNox.setVisibility(View.GONE);
                poRemit.setcRemitTyp("0");
                txtAccName.setText("");
                txtAccNox.setText("");
            } else if(checkedId == R.id.rb_remitBank){
                linearBrnch.setVisibility(View.GONE);
                linearBank.setVisibility(View.VISIBLE);
                tilRefNox.setVisibility(View.VISIBLE);
                txtAccName.setText("");
                txtAccNox.setText("");
                poRemit.setcRemitTyp("1");

                mViewModel.GetBankAccounts().observe(Activity_CollectionRemittance.this, accounts -> {
                    try{
                        String[] lsAccounts = new String[accounts.size()];
                        for(int x = 0; x < accounts.size(); x++){
                            lsAccounts[x] = accounts.get(x).getActNumbr() + " (" + accounts.get(x).getActNamex() +")";
                        }
                        txtAccNox.setAdapter(new ArrayAdapter<>(Activity_CollectionRemittance.this, android.R.layout.simple_spinner_dropdown_item, lsAccounts));

                        txtAccNox.setOnItemClickListener((parent, view, position, id) -> {
                            String[] lsSplitStr = txtAccNox.getText().toString().split(" ");
                            String lsAcctNox = lsSplitStr[0];
                            for(int x = 0; x < accounts.size(); x++){
                                if(lsAcctNox.equalsIgnoreCase(accounts.get(x).getActNumbr())){
                                    txtAccName.setText(accounts.get(x).getActNamex());
                                    txtAccNox.setText(accounts.get(x).getActNumbr());
                                    break;
                                }
                            }
                        });
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                });
            } else {
                linearBrnch.setVisibility(View.GONE);
                linearBank.setVisibility(View.VISIBLE);
                tilRefNox.setVisibility(View.VISIBLE);
                poRemit.setcRemitTyp("2");
                txtAccName.setText("");
                txtAccNox.setText("");

                mViewModel.GetOtherAccounts().observe(Activity_CollectionRemittance.this, accounts -> {
                    try{
                        String[] lsAccounts = new String[accounts.size()];
                        for(int x = 0; x < accounts.size(); x++){
                            lsAccounts[x] = accounts.get(x).getActNumbr() + " (" + accounts.get(x).getActNamex() +")";
                        }
                        txtAccNox.setAdapter(new ArrayAdapter<>(Activity_CollectionRemittance.this, android.R.layout.simple_spinner_dropdown_item, lsAccounts));
                        txtAccNox.setOnItemClickListener((parent, view, position, id) -> {
                            String[] lsSplitStr = txtAccNox.getText().toString().split(" ");
                            String lsAcctNox = lsSplitStr[0];
                            for(int x = 0; x < accounts.size(); x++){
                                if(lsAcctNox.equalsIgnoreCase(accounts.get(x).getActNumbr())){
                                    txtAccName.setText(accounts.get(x).getActNamex());
                                    txtAccNox.setText(accounts.get(x).getActNumbr());
                                    break;
                                }
                            }
                        });
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                });
            }
        });

        btnRemitAll.setOnClickListener(v -> {
            if(poRemit.getcPaymForm().equalsIgnoreCase("0")){
                txtAmount.setText(psCltCashx);
            } else {
                txtAmount.setText(psCltCheck);
            }
        });

        btnRemit.setOnClickListener(v ->{

            poRemit.setcRemitTyp(poRemit.getcRemitTyp());

            switch (poRemit.getcRemitTyp()) {
                case "0":
                    poRemit.setsCompnyNm(txtBranch.getText().toString());
                    break;
                case "1":
                case "2":
                    poRemit.setsCompnyNm(Objects.requireNonNull(txtAccName.getText()).toString());
                    poRemit.setsBankAcct(Objects.requireNonNull(txtAccNox.getText()).toString());
                    poRemit.setsReferNox(Objects.requireNonNull(txtRefNox.getText()).toString());
                    break;
            }

            poRemit.setnAmountxx(FormatUIText.getParseDouble(Objects.requireNonNull(txtAmount.getText()).toString().replace(",", "")));
            mViewModel.RemitCollection(poRemit, new VMCollectionRemittance.OnRemitCollectionCallback() {
                @Override
                public void OnRemit() {
                    poDialogx.initDialog("Collection Remittance", "Saving collection remittance. Please wait...", false);
                    poDialogx.show();
                }

                @Override
                public void OnSuccess(String message) {
                    poDialogx.dismiss();
                    poMessage.initDialog();
                    poMessage.setIcon(R.drawable.baseline_message_24);
                    poMessage.setTitle("Collection Remittance");
                    poMessage.setMessage("Your remittance has been posted successfully.");
                    poMessage.setPositiveButton("Okay", (view, dialog) -> {
                        dialog.dismiss();
                        finish();
                    });
                    poMessage.show();
                }

                @Override
                public void OnFailed(String message) {
                    poDialogx.dismiss();
                    poMessage.initDialog();
                    poMessage.setIcon(R.drawable.baseline_error_24);
                    poMessage.setTitle("Collection Remittance");
                    poMessage.setMessage(message);
                    poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                    poMessage.show();
                }
            });
        });
    }

    private void initWidgets(){
        lblBranch = findViewById(R.id.lbl_headerBranch);
        lblAddrss = findViewById(R.id.lbl_headerAddress);

        toolbar = findViewById(R.id.toolbar_collectionRemit);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        rgRemitType = findViewById(R.id.rg_remittance);
        rgRemitance = findViewById(R.id.rg_remittanceOutlet);
        txtBranch = findViewById(R.id.txt_remitBranch);
        txtAccNox = findViewById(R.id.txt_remitAcctNox);
        txtAmount = findViewById(R.id.txt_remitAmount);
        txtAccName = findViewById(R.id.txt_remitAccName);
        txtRefNox = findViewById(R.id.txt_remitReferenceNo);
        tilRefNox = findViewById(R.id.til_remitReferenceNo);
        linearBrnch = findViewById(R.id.linear_remitBranch);
        linearBank = findViewById(R.id.linear_remitBankOthers);
        lblCltdCash = findViewById(R.id.lbl_collectedCash);
        lblCltdChck = findViewById(R.id.lbl_collectedCheck);
        lblRmtBrnch = findViewById(R.id.lbl_remittanceBranch);
        lblRmtBankx = findViewById(R.id.lbl_remittanceBank);
        lblRmtOther = findViewById(R.id.lbl_remittanceOthers);
        lblOHCashxx = findViewById(R.id.lbl_remittanceCash);
        lblOHCheckx = findViewById(R.id.lbl_remittanceCheck);
        btnRemitAll = findViewById(R.id.btn_remitAll);
        btnRemit = findViewById(R.id.btn_confirm);

        txtAmount.addTextChangedListener(new FormatUIText.CurrencyFormat(txtAmount));
        txtAccName.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}