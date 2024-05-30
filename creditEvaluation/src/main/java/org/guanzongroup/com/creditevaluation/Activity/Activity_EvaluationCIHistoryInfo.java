/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.creditEvaluation
 * Electronic Personnel Access Control Security System
 * project file created : 4/8/22, 9:09 AM
 * project file last modified : 4/8/22, 9:09 AM
 */

package org.guanzongroup.com.creditevaluation.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;


import org.guanzongroup.com.creditevaluation.Adapter.Adapter_CIEvaluation_Headers;
import org.guanzongroup.com.creditevaluation.Adapter.EvaluationCIHistoryInfoAdapter;
import org.guanzongroup.com.creditevaluation.Core.FindingsParser;
import org.guanzongroup.com.creditevaluation.Dialog.DialogCIReason;
import org.guanzongroup.com.creditevaluation.R;
import org.guanzongroup.com.creditevaluation.ViewModel.VMEvaluationCIHistoryInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditOnlineApplicationCI;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;

import java.util.Objects;

public class Activity_EvaluationCIHistoryInfo extends AppCompatActivity {

    private VMEvaluationCIHistoryInfo mViewModel;
    private EvaluationCIHistoryInfoAdapter poAdapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private MaterialButton btnApprove;
    private MaterialTextView txtClient, txtTransD, txtBranch, lblCIRcmnd, lblBHRcmnd;

    private String psTransNo = "";
    private String psClientN = "";
    private String dTransact = "";
    private String psBranchx = "";

    private boolean cPreviewx = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_ci_history_info);
        initParams();
        getExtras();
        displayData();
        btnApprove.setOnClickListener(new OnApproveListener());
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

    private void initParams() {
        mViewModel = new ViewModelProvider(Activity_EvaluationCIHistoryInfo.this).get(VMEvaluationCIHistoryInfo.class);
        layoutManager = new LinearLayoutManager(Activity_EvaluationCIHistoryInfo.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        txtClient = findViewById(R.id.txt_client_name);
        txtTransD = findViewById(R.id.txt_transaction_date);
        txtBranch = findViewById(R.id.txt_branch);
        lblCIRcmnd = findViewById(R.id.txt_ci_rcmnd);
        lblBHRcmnd = findViewById(R.id.txt_bh_rcmnd);
        recyclerView = findViewById(R.id.recyclerview);
        btnApprove = findViewById(R.id.btn_approve);
    }

    private void getExtras() {
        psTransNo = Objects.requireNonNull(getIntent().getStringExtra("sTransNox"));
        psClientN = Objects.requireNonNull(getIntent().getStringExtra("sClientNm"));
        dTransact = Objects.requireNonNull(getIntent().getStringExtra("dTransact"));
        psBranchx = Objects.requireNonNull(getIntent().getStringExtra("sBranchxx"));
        cPreviewx = getIntent().getBooleanExtra("cPreviewx", true);
    }

    private void displayData() {
        setHeaderData();
        mViewModel.RetrieveApplicationData(psTransNo).observe(Activity_EvaluationCIHistoryInfo.this, ciDetails -> {
            try {
                setupEvaluationAdapter(ciDetails);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setHeaderData() {
        txtClient.setText(psClientN);
        txtTransD.setText(FormatUIText.formatGOCasBirthdate(dTransact));
        txtBranch.setText(psBranchx);
    }

    private class OnApproveListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            DialogCIReason loDialog = new DialogCIReason(Activity_EvaluationCIHistoryInfo.this);
            loDialog.initDialogCIReason((dialog, transtat, reason) -> {
                dialog.dismiss();
                    LoadDialog loading = new LoadDialog(Activity_EvaluationCIHistoryInfo.this);
                    mViewModel.PostBHApproval(psTransNo, transtat, reason, new VMEvaluationCIHistoryInfo.OnTransactionCallBack() {
                        @Override
                        public void onSuccess(String fsMessage) {
                            loading.dismiss();
                            MessageBox msgBox = new MessageBox(Activity_EvaluationCIHistoryInfo.this);
                            msgBox.initDialog();
                            msgBox.setTitle("CI Evaluation");
                            msgBox.setMessage(fsMessage);
                            msgBox.setPositiveButton("Okay", (v, diags) -> {
                                diags.dismiss();
                                finish();
                            });
                            msgBox.show();
                        }

                        @Override
                        public void onFailed(String fsMessage) {
                            loading.dismiss();
                            MessageBox msgBox = new MessageBox(Activity_EvaluationCIHistoryInfo.this);
                            msgBox.initDialog();
                            msgBox.setTitle("CI Evaluation");
                            msgBox.setMessage(fsMessage);
                            msgBox.setPositiveButton("Okay", (v, diags) -> {
                                diags.dismiss();
                            });
                            msgBox.show();
                        }

                        @Override
                        public void onLoad() {
                            loading.initDialog("CI Evaluation", "CI Evalation Approval Processing...Please wait.", false);
                            loading.show();
                        }
                    });
            });
            loDialog.show();
        }
    }

    private void setupEvaluationAdapter(ECreditOnlineApplicationCI ci){
        try {
            JSONArray laEval = new JSONArray();
            JSONObject loDetail = FindingsParser.scanForEvaluation(ci.getAddressx(), ci.getAddrFndg());
            if(!loDetail.getJSONArray("detail").getJSONObject(0).toString().equalsIgnoreCase("{}")){
                laEval.put(FindingsParser.scanForEvaluation(ci.getAddressx(), ci.getAddrFndg()));
            }

            loDetail = FindingsParser.scanForEvaluation(ci.getIncomexx(), ci.getIncmFndg());
            if(!loDetail.getJSONArray("detail").getJSONObject(0).toString().equalsIgnoreCase("{}")){
                laEval.put(FindingsParser.scanForEvaluation(ci.getIncomexx(), ci.getIncmFndg()));
            }

            loDetail = FindingsParser.scanForEvaluation(ci.getAssetsxx(), ci.getAsstFndg());
            if(!loDetail.getJSONArray("detail").getJSONObject(0).toString().equalsIgnoreCase("{}")){
                laEval.put(FindingsParser.scanForEvaluation(ci.getAssetsxx(), ci.getAsstFndg()));
            }

            mViewModel.GetOccupationList().observe(Activity_EvaluationCIHistoryInfo.this, eOccupationInfos -> {

                Adapter_CIEvaluation_Headers loAdapter = new Adapter_CIEvaluation_Headers(
                        Activity_EvaluationCIHistoryInfo.this,
                        laEval,
                        eOccupationInfos,
                        true,
                        null);

                LinearLayoutManager loManager = new LinearLayoutManager(Activity_EvaluationCIHistoryInfo.this);
                loManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(loManager);
                recyclerView.setAdapter(loAdapter);

                if(cPreviewx){
                    btnApprove.setVisibility(View.GONE);
                    findViewById(R.id.linear_recommendations).setVisibility(View.VISIBLE);
                    if(ci.getRcmdtnc1().equalsIgnoreCase("null")){
                        lblCIRcmnd.setText("N/A");
                    } else if(ci.getRcmdtnc1().equalsIgnoreCase("1")){
                        lblCIRcmnd.setText("Approved");
                    } else {
                        lblCIRcmnd.setText("Disapproved");
                    }

                    if(ci.getRcmdtnc2() == null){
                        lblBHRcmnd.setText("N/A");
                    } else if(ci.getRcmdtnc2().equalsIgnoreCase("null")){
                        lblBHRcmnd.setText("N/A");
                    } else if(ci.getRcmdtnc2().equalsIgnoreCase("1")){
                        lblBHRcmnd.setText("Approved");
                    } else {
                        lblBHRcmnd.setText("Disapproved");
                    }
                } else {
                    if(ci.getRcmdtnc2() == null) {
                        btnApprove.setVisibility(View.VISIBLE);
                        findViewById(R.id.linear_recommendations).setVisibility(View.GONE);
                    } else {
                        btnApprove.setVisibility(View.GONE);
                        findViewById(R.id.linear_recommendations).setVisibility(View.GONE);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}