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

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textview.MaterialTextView;

import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_TransactionDetail;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.VMLogPaidTransaction;

public class Fragment_Log_Paid_Transaction extends Fragment {
    private VMLogPaidTransaction mViewModel;
    private MaterialTextView txtAcctNo, txtClientName, txtClientAddress, txtTransNo;
    private MaterialTextView txtPaymentTp, txtPRNoxx, txtTransAmtx, txtDiscount, txtPenalty, txtTotalAmtx,
            txtRemarksx, txtCheckPayment, txtListHeader, txtBank, txtChckDt, txtChckNm, txtChckAc;
    private LinearLayout lnBank, lnChckDt, lnChckNm, lnChckAc;

    public Fragment_Log_Paid_Transaction() { }

    public static Fragment_Log_Paid_Transaction newInstance() {
        return new Fragment_Log_Paid_Transaction();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_paid_log, container, false);
        mViewModel = new ViewModelProvider(this).get(VMLogPaidTransaction.class);
        initWidgets(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: Functions
        txtAcctNo.setText(Activity_TransactionDetail.acctNox);
        txtClientName.setText(Activity_TransactionDetail.fullNme);
        txtClientAddress.setText(Activity_TransactionDetail.clientAddress);
        txtTransNo.setText(Activity_TransactionDetail.transNox);
        txtListHeader.setText(Activity_TransactionDetail.psTransTp + " Transaction");
        mViewModel.setParameters(Activity_TransactionDetail.transNox,
                Activity_TransactionDetail.acctNox,
                Activity_TransactionDetail.remCodex);

        mViewModel.getPostedCollectionDetail().observe(getViewLifecycleOwner(), collectPaidDetl -> {
            try {
                if(!collectPaidDetl.getBankIDxx().equalsIgnoreCase("")) {
                    txtCheckPayment.setVisibility(View.VISIBLE);
                    lnBank.setVisibility(View.VISIBLE);
                    lnChckDt.setVisibility(View.VISIBLE);
                    lnChckNm.setVisibility(View.VISIBLE);
                    lnChckAc.setVisibility(View.VISIBLE);

                    mViewModel.getBankNameFromId(collectPaidDetl.getBankIDxx()).observe(getViewLifecycleOwner(), bankName -> {
                        try {
                            txtBank.setText(bankName);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    });
                    txtChckDt.setText(collectPaidDetl.getCheckDte());
                    txtChckNm.setText(collectPaidDetl.getCheckNox());
                    txtChckAc.setText(collectPaidDetl.getCheckAct());
                } else {
                    txtCheckPayment.setVisibility(View.GONE);
                    lnBank.setVisibility(View.GONE);
                    lnChckDt.setVisibility(View.GONE);
                    lnChckNm.setVisibility(View.GONE);
                    lnChckAc.setVisibility(View.GONE);
                }
                txtPaymentTp.setText(collectPaidDetl.getTranType());
                txtPRNoxx.setText(collectPaidDetl.getPRNoxxxx());
                txtTransAmtx.setText(getString(R.string.peso_sign) + collectPaidDetl.getTranAmtx());
                txtDiscount.setText(getString(R.string.peso_sign) + collectPaidDetl.getDiscount());
                txtPenalty.setText(getString(R.string.peso_sign) + collectPaidDetl.getOthersxx());
                txtTotalAmtx.setText(getString(R.string.peso_sign) + collectPaidDetl.getTranTotl());
                txtRemarksx.setText(collectPaidDetl.getRemarksx());
            } catch(NullPointerException e) {
                e.printStackTrace();
            }
        });
    }

    private void initWidgets(View v) {
        // TODO: Initialize Widgets
        txtAcctNo = v.findViewById(R.id.txt_acctNo);
        txtClientName = v.findViewById(R.id.txt_clientName);
        txtClientAddress = v.findViewById(R.id.txt_client_address);
        txtTransNo = v.findViewById(R.id.txt_transno);
        txtListHeader = v.findViewById(R.id.lbl_list_header);

        txtPaymentTp = v.findViewById(R.id.txt_payment_type);
        txtPRNoxx = v.findViewById(R.id.txt_pr_no);
        txtTransAmtx = v.findViewById(R.id.txt_trans_amount);
        txtDiscount = v.findViewById(R.id.txt_discount);
        txtPenalty = v.findViewById(R.id.txt_penalty);
        txtTotalAmtx = v.findViewById(R.id.txt_total_amount);
        txtRemarksx = v.findViewById(R.id.txt_remarks);
        txtCheckPayment = v.findViewById(R.id.lbl_check_payment);

        txtBank = v.findViewById(R.id.txt_bank);
        txtChckDt = v.findViewById(R.id.txt_check_date);
        txtChckNm = v.findViewById(R.id.txt_check_numbr);
        txtChckAc = v.findViewById(R.id.txt_check_accNumbr);

        lnBank = v.findViewById(R.id.ln_bank);
        lnChckDt = v.findViewById(R.id.ln_check_date);
        lnChckNm = v.findViewById(R.id.ln_check_number);
        lnChckAc = v.findViewById(R.id.ln_check_accNo);
    }
}