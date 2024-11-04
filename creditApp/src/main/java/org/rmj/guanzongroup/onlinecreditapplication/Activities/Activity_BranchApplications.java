/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.creditApp
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;


import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.BranchApplicationModel;
import org.rmj.guanzongroup.onlinecreditapplication.Adapter.BranchApplicationsAdapter;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.OnDownloadApplicationsListener;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMBranchApplications;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity_BranchApplications extends AppCompatActivity {
    private RecyclerView recyclerViewClient;
    private VMBranchApplications mViewModel;
    private LinearLayoutManager layoutManager;
    private BranchApplicationsAdapter adapter;
    private LinearLayout loading;
    private List<BranchApplicationModel> loanList;
    private LoadDialog poDialogx;
    private MessageBox poMessage;
    private String userBranch;
    private TextInputEditText txtSearch;
    private LinearLayout layoutNoRecord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_BranchApplications.this).get(VMBranchApplications.class);
        setContentView(R.layout.activity_branch_applications);
        initWidgets();
        mViewModel.ImportBranchApplications(new OnDownloadApplicationsListener() {
            @Override
            public void OnDownload(String title, String message) {
                poDialogx.initDialog(title, message, false);
                poDialogx.show();
            }

            @Override
            public void OnSuccess(String message) {
                poDialogx.dismiss();
            }

            @Override
            public void OnFailed(String message) {
                poDialogx.dismiss();
                poMessage.initDialog();
                poMessage.setIcon(R.drawable.baseline_error_24);
                poMessage.setTitle("Credit Online Application");
                poMessage.setMessage(message);
                poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                poMessage.show();
            }
        });
        initData();
    }
    private void initWidgets(){
        MaterialToolbar toolbar = findViewById(R.id.toolbar_branchApplications);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        poDialogx = new LoadDialog(Activity_BranchApplications.this);
        poMessage = new MessageBox(Activity_BranchApplications.this);

        recyclerViewClient = findViewById(R.id.recyclerview_branchApplications);
        txtSearch = findViewById(R.id.txt_branch_search);
        layoutManager = new LinearLayoutManager(Activity_BranchApplications.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        loading = findViewById(R.id.linear_progress);
        layoutNoRecord = findViewById(R.id.layout_branch_noRecord);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void initData(){
        mViewModel.GetBranchApplications().observe(Activity_BranchApplications.this, applications -> {
            try{
                if(applications.size()>0) {
                    layoutNoRecord.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                    loanList = new ArrayList<>();
                    for (int x = 0; x < applications.size(); x++) {
                        BranchApplicationModel loan = new BranchApplicationModel();
                        loan.setsTransNox(applications.get(x).getTransNox());
                        loan.setdTransact(applications.get(x).getTransact());
                        loan.setsCredInvx(applications.get(x).getCredInvx());
                        loan.setsCompnyNm(applications.get(x).getCompnyNm());
                        loan.setsSpouseNm(applications.get(x).getSpouseNm());
                        loan.setsAddressx(applications.get(x).getAddressx());
                        loan.setsMobileNo(applications.get(x).getMobileNo());
                        loan.setsQMAppCde(applications.get(x).getQMAppCde());
                        loan.setsModelNme(applications.get(x).getModelNme());
                        loan.setnDownPaym(applications.get(x).getDownPaym());
                        loan.setnAcctTerm(applications.get(x).getAcctTerm());
                        loan.setcTranStat(applications.get(x).getTranStat());
                        loan.setdTimeStmp(applications.get(x).getTimeStmp());
                        loanList.add(loan);
                    }
                    adapter = new BranchApplicationsAdapter(loanList, new BranchApplicationsAdapter.OnApplicationClickListener() {
                        @Override
                        public void OnClick(int position, List<BranchApplicationModel> loanLists) {
                            Intent loIntent = new Intent(Activity_BranchApplications.this, Activity_ApplicantDocuments.class);
                            loIntent.putExtra("TransNox",loanLists.get(position).getsTransNox());
                            loIntent.putExtra("ClientNm",loanLists.get(position).getsCompnyNm());
                            loIntent.putExtra("dTransact",loanLists.get(position).getdTransact());
                            loIntent.putExtra("ModelName",loanLists.get(position).getsModelNme());
                            loIntent.putExtra("AccntTerm",loanLists.get(position).getnAcctTerm());
                            loIntent.putExtra("MobileNo",loanLists.get(position).getsMobileNo());
                            loIntent.putExtra("Status",loanLists.get(position).getTransactionStatus());
                            loIntent.putExtra("SubFolder", AppConstants.SUB_FOLDER_CREDIT_APP);
                            startActivity(loIntent);
                        }

                    });
                    LinearLayoutManager layoutManager = new LinearLayoutManager(Activity_BranchApplications.this);
                    recyclerViewClient.setAdapter(adapter);
                    recyclerViewClient.setLayoutManager(layoutManager);

                    txtSearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try {
                                adapter.getFilter().filter(s.toString());
                                adapter.notifyDataSetChanged();
                                if (adapter.getItemCount() == 0){
                                    layoutNoRecord.setVisibility(View.VISIBLE);
                                }else {
                                    layoutNoRecord.setVisibility(View.GONE);
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                } else {
                    layoutNoRecord.setVisibility(View.VISIBLE);
                }
            } catch (Exception e){
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