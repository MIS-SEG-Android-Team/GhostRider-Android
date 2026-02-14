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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.etc.GToast;
import org.rmj.g3appdriver.etc.OnDateSetListener;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;

import java.util.Objects;

public class DialogEWallet {
    private static final String TAG = DialogEWallet.class.getSimpleName();

    private AlertDialog poDialogx;
    private final Context context;

    private String lsBank = "";

    public interface OnEWalletDialogListener{
        void OnConfirm(AlertDialog dialog, String bank, String RefNo);
        void OnCancel(AlertDialog dialog);
    }

    public DialogEWallet(Context context) {
        this.context = context;
    }

    public void initDialog(String[] bankList, OnEWalletDialogListener listener){

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_e_wallet, null, false);
        AlertDialog.Builder loBuilder = new AlertDialog.Builder(context);
        loBuilder.setView(view)
                .setCancelable(false);
        poDialogx = loBuilder.create();
        poDialogx.setCancelable(false);

        MaterialAutoCompleteTextView txtBankNme = view.findViewById(R.id.txt_dcpBankName);
        TextInputEditText txt_reference = view.findViewById(R.id.txt_reference);

        MaterialButton btnConfirm = view.findViewById(R.id.btn_dcpConfirm);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);

        ArrayAdapter<String> loAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, bankList);
        txtBankNme.setAdapter(loAdapter);

        txtBankNme.setOnItemClickListener((parent, view1, position, id) -> lsBank = txtBankNme.getText().toString());

        btnConfirm.setOnClickListener(v -> {

            String ls_reference = Objects.requireNonNull(txt_reference.getText()).toString();

            if(lsBank.trim().isEmpty()){
                GToast.CreateMessage(context, "Please select bank name on listed suggestions", GToast.ERROR).show();
            } else if(ls_reference.trim().isEmpty()){
                GToast.CreateMessage(context, "Please enter reference number", GToast.ERROR).show();
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
