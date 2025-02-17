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

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import  com.google.android.material.checkbox.MaterialCheckBox;

import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.etc.GToast;
import org.rmj.g3appdriver.etc.OnDateSetListener;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;

import java.util.Objects;

public class DialogCheckPayment {
    private static final String TAG = DialogCheckPayment.class.getSimpleName();

    private AlertDialog poDialogx;
    private final Context context;

    private String lsBank = "";

    public interface OnCheckPaymentDialogListener{
        void OnConfirm(AlertDialog dialog, String bank, String date, String checkNo, String AcctNo);
        void OnCancel(AlertDialog dialog);
    }

    public DialogCheckPayment(Context context) {
        this.context = context;
    }

    public void initDialog(String[] bankList, OnCheckPaymentDialogListener listener){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_check_payment, null, false);
        AlertDialog.Builder loBuilder = new AlertDialog.Builder(context);
        loBuilder.setView(view)
                .setCancelable(false);
        poDialogx = loBuilder.create();
        poDialogx.setCancelable(false);

        MaterialAutoCompleteTextView txtBankNme = view.findViewById(R.id.txt_dcpBankName);
        TextInputEditText txtCheckDt = view.findViewById(R.id.txt_dcpCheckDate);
        TextInputEditText txtCheckNo = view.findViewById(R.id.txt_dcpCheckNo);
        TextInputEditText txtAccntNo = view.findViewById(R.id.txt_dcpAcctNumber);

        MaterialButton btnConfirm = view.findViewById(R.id.btn_dcpConfirm);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);

        ArrayAdapter<String> loAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, bankList);
        txtBankNme.setAdapter(loAdapter);

        txtBankNme.setOnItemClickListener((parent, view1, position, id) -> lsBank = txtBankNme.getText().toString());

        txtCheckDt.addTextChangedListener(new OnDateSetListener(txtCheckDt));

        btnConfirm.setOnClickListener(v -> {
            String lsCheckDt = Objects.requireNonNull(txtCheckDt.getText()).toString();
            String lsCheckNo = Objects.requireNonNull(txtCheckNo.getText()).toString();
            String lsAccntNo = Objects.requireNonNull(txtAccntNo.getText()).toString();
            if(lsBank.trim().isEmpty()){
                GToast.CreateMessage(context, "Please select bank name on listed suggestions", GToast.ERROR).show();
            } else if(lsCheckDt.trim().isEmpty()){
                GToast.CreateMessage(context, "Please enter check date", GToast.ERROR).show();
            } else if(lsCheckNo.trim().isEmpty()){
                GToast.CreateMessage(context, "Please enter check no.", GToast.ERROR).show();
            } else if(lsAccntNo.trim().isEmpty()){
                GToast.CreateMessage(context, "Please enter check account no.", GToast.ERROR).show();
            } else {
                listener.OnConfirm(poDialogx, lsBank, lsCheckDt, lsCheckNo, lsAccntNo);
            }
        });

        btnCancel.setOnClickListener(v -> listener.OnCancel(poDialogx));
    }

    public void show(){
        poDialogx.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        poDialogx.getWindow().getAttributes().windowAnimations = org.rmj.g3appdriver.R.style.PopupAnimation;
        poDialogx.show();
    }
}
