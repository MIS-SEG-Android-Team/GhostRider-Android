/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.creditEvaluation
 * Electronic Personnel Access Control Security System
 * project file created : 3/17/22, 1:28 PM
 * project file last modified : 3/17/22, 1:28 PM
 */

package org.guanzongroup.com.creditevaluation.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import com.google.gson.Gson;

import org.guanzongroup.com.creditevaluation.Adapter.CreditEvaluationListAdapter;
import org.guanzongroup.com.creditevaluation.Dialog.DialogAddApplication;
import org.guanzongroup.com.creditevaluation.R;
import org.guanzongroup.com.creditevaluation.ViewModel.VMEvaluationList;
import org.guanzongroup.com.creditevaluation.ViewModel.ViewModelCallback;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCreditOnlineApplicationCI;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity_CIEvaluationList extends AppCompatActivity  implements VMEvaluationList.OnImportCallBack {
    private static final String TAG = Activity_CIEvaluationList.class.getSimpleName();
    private RecyclerView recyclerViewClient;
    private VMEvaluationList mViewModel;
    private LinearLayoutManager layoutManager;
    private CreditEvaluationListAdapter adapter;
    private LinearLayout loading;
    private List<DCreditOnlineApplicationCI.oDataEvaluationInfo> ciEvaluationList;
    private LoadDialog poDialogx;
    private MessageBox poMessage;
    private String userBranch;
    private TextInputEditText txtSearch;
    private MaterialTextView layoutNoRecord;
    private MaterialTextView lblBranch,lblAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_CIEvaluationList.this).get(VMEvaluationList.class);
        setContentView(R.layout.activity_cievaluation_list);
        initWidgets();
        mViewModel.DownloadForCIApplications(Activity_CIEvaluationList.this);

        mViewModel.GetUserInfo().observe(this, eBranchInfo -> {
            try {
                lblBranch.setText(eBranchInfo.sBranchNm);
                lblAddress.setText(eBranchInfo.sAddressx);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu_credit_evaluation_list, menu);

        return true;
    }

    private void initWidgets() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_creditEvalutionList);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        poDialogx = new LoadDialog(Activity_CIEvaluationList.this);
        poMessage = new MessageBox(Activity_CIEvaluationList.this);

        recyclerViewClient = findViewById(R.id.recyclerview_ciList);
        txtSearch = findViewById(R.id.txt_ci_search);
        layoutManager = new LinearLayoutManager(Activity_CIEvaluationList.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        loading = findViewById(R.id.linear_progress);
        layoutNoRecord = findViewById(R.id.layout_ci_noRecord);
        lblBranch = findViewById(R.id.lbl_Branch);
        lblAddress = findViewById(R.id.lbl_Address);

    }

    @Override
    public void onStartImport() {
        poDialogx.initDialog("CI Evaluation List", "Importing latest data. Please wait...", false);
        poDialogx.show();
//        initData();
    }

    @Override
    public void onSuccessImport() {
        poDialogx.dismiss();
    }

    @Override
    public void onImportFailed(String message) {
        poDialogx.dismiss();
        poMessage.initDialog();
        poMessage.setTitle("CI Evaluation List");
        poMessage.setMessage(message);
        poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
        poMessage.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_menu_add_application) {
            try {
                DialogAddApplication loDialog = new DialogAddApplication(Activity_CIEvaluationList.this);
                loDialog.initDialog(new DialogAddApplication.OnDialogButtonClickListener() {
                    @Override
                    public void OnDownloadClick(Dialog Dialog, String args) {
                        mViewModel.AddForCIApplication(args, new ViewModelCallback() {
                            @Override
                            public void OnStartSaving() {
                                poDialogx.initDialog("Add Application", "Downloading client info. Please wait...", false);
                                poDialogx.show();
                            }

                            @Override
                            public void OnSuccessResult() {
                                Dialog.dismiss();
                                poDialogx.dismiss();
                                poMessage.initDialog();
                                poMessage.setTitle("Add Application");
                                poMessage.setMessage("Credit Application saved successfully");
                                poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                                poMessage.show();
                            }

                            @Override
                            public void OnFailedResult(String message) {
                                poDialogx.dismiss();
                                poMessage.initDialog();
                                poMessage.setTitle("Add Application");
                                poMessage.setMessage(message);
                                poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                                poMessage.show();
                            }
                        });
                    }

                    @Override
                    public void OnCancel(Dialog Dialog) {
                        Dialog.dismiss();
                    }
                });
                loDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void initData() {
        mViewModel.getForEvaluationListData().observe(Activity_CIEvaluationList.this, ciList -> {
            Log.e("size", String.valueOf(ciList.size()));
            try {
                if (ciList.size() > 0) {
                    layoutNoRecord.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                    ciEvaluationList = new ArrayList<>();
                    for (int x = 0; x < ciList.size(); x++) {
                        DCreditOnlineApplicationCI.oDataEvaluationInfo loan = new DCreditOnlineApplicationCI.oDataEvaluationInfo();
                        loan.sTransNox = (ciList.get(x).sTransNox);
                        loan.dTransact = (ciList.get(x).dTransact);
                        loan.sCredInvx = (ciList.get(x).sCredInvx);
                        loan.sClientNm = (ciList.get(x).sClientNm);
//                        loan.nAcctTerm = (ciList.get(x).nAcctTerm);
                        loan.nDownPaym = (ciList.get(x).nDownPaym);
                        loan.sAddressx = (ciList.get(x).sAddressx);
                        loan.sAddrFndg = (ciList.get(x).sAddrFndg);
                        loan.sAssetsxx = (ciList.get(x).sAssetsxx);
                        loan.sAsstFndg = (ciList.get(x).sAsstFndg);
                        loan.sIncomexx = (ciList.get(x).sIncmFndg);
                        loan.cHasRecrd = (ciList.get(x).cHasRecrd);
                        loan.sBranchNm = (ciList.get(x).sBranchNm);
                        loan.sRecrdRem = (ciList.get(x).sRecrdRem);
                        loan.sRcmdtnx1 = (ciList.get(x).sRcmdtnx1);
                        ciEvaluationList.add(loan);
                    }

                    String json = new Gson().toJson(ciEvaluationList);
                    adapter = new CreditEvaluationListAdapter(ciEvaluationList, "CI Evaluation List", new CreditEvaluationListAdapter.OnApplicationClickListener() {
                        @Override
                        public void OnClick(int position, List<DCreditOnlineApplicationCI.oDataEvaluationInfo> ciEvaluationLists) {
                            Intent loIntent = new Intent(Activity_CIEvaluationList.this, Activity_Evaluation.class);
                            loIntent.putExtra("transno", ciEvaluationLists.get(position).sTransNox);
                            loIntent.putExtra("ClientNm", ciEvaluationLists.get(position).sClientNm);
                            loIntent.putExtra("dTransact", ciEvaluationLists.get(position).dTransact);
                            loIntent.putExtra("Branch", ciEvaluationLists.get(position).sBranchNm);
                            startActivity(loIntent);
                        }
                    });
                    LinearLayoutManager layoutManager = new LinearLayoutManager(Activity_CIEvaluationList.this);
                    recyclerViewClient.setAdapter(adapter);
                    recyclerViewClient.setLayoutManager(layoutManager);
                    adapter.notifyDataSetChanged();
                    txtSearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try {
                                adapter.getFilter().filter(s.toString());
                                if (adapter.getItemCount() == 0) {
                                    layoutNoRecord.setVisibility(View.VISIBLE);
                                } else {
                                    layoutNoRecord.setVisibility(View.GONE);
                                }
                                adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (adapter.getItemCount() == 0) {
                                layoutNoRecord.setVisibility(View.VISIBLE);
                            } else {
                                layoutNoRecord.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    layoutNoRecord.setVisibility(View.VISIBLE);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }
}