/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.creditEvaluation
 * Electronic Personnel Access Control Security System
 * project file created : 3/17/22, 10:36 AM
 * project file last modified : 3/17/22, 10:36 AM
 */

package org.guanzongroup.com.creditevaluation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;


import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.guanzongroup.com.creditevaluation.Adapter.CreditEvaluationListAdapter;
import org.guanzongroup.com.creditevaluation.R;
import org.guanzongroup.com.creditevaluation.ViewModel.VMEvaluationHistory;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCreditOnlineApplicationCI;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;

import java.util.List;
import java.util.Objects;

public class Activity_EvaluationCIHistory extends AppCompatActivity {
    private static final String TAG = Activity_EvaluationCIHistory.class.getSimpleName();
    private RecyclerView recyclerViewClient;
    private VMEvaluationHistory mViewModel;
    private LinearLayoutManager layoutManager;
    private CreditEvaluationListAdapter adapter;
    private LinearLayout loading;
    private LoadDialog poDialogx;
    private MessageBox poMessage;
    private TextInputEditText txtSearch;
    private MaterialTextView layoutNoRecord;
    private MaterialTextView lblBranch,lblAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(Activity_EvaluationCIHistory.this).get(VMEvaluationHistory.class);
        setContentView(R.layout.activity_evaluation_cihistory);
        initWidgets();
        mViewModel.GetUserInfo().observe(this, user -> {
            try {
                lblBranch.setText(user.sBranchNm);
                lblAddress.setText(user.sAddressx);
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.DownloadApplicationsForBHApproval(new VMEvaluationHistory.OnTransactionCallback() {
            @Override
            public void onSuccess(String message) {
                poDialogx.dismiss();
            }

            @Override
            public void onFailed(String message) {
                poDialogx.dismiss();
                Log.e(Activity_EvaluationCIHistory.class.getSimpleName(), message);
            }

            @Override
            public void onLoad() {
                poDialogx.initDialog("CI Evaluation", "CI Evaluation downloading.. Please wait.", true);
                poDialogx.show();
            }
        });
        initData();

    }
    private void initWidgets() {
        Toolbar toolbar = findViewById(R.id.toolbar_creditEvalutionList);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        poDialogx = new LoadDialog(Activity_EvaluationCIHistory.this);
        poMessage = new MessageBox(Activity_EvaluationCIHistory.this);

        recyclerViewClient = findViewById(R.id.recyclerview_ciList);
        txtSearch = findViewById(R.id.txt_ci_search);
        layoutManager = new LinearLayoutManager(Activity_EvaluationCIHistory.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        loading = findViewById(R.id.linear_progress);
        layoutNoRecord = findViewById(R.id.layout_ci_noRecord);
        lblBranch = findViewById(R.id.lblBranch);
        lblAddress = findViewById(R.id.lblAddress);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void initData() {
        mViewModel.getForEvaluationListDataPreview().observe(Activity_EvaluationCIHistory.this, ciList -> {
            Log.e("size", String.valueOf(ciList.size()));
            try {
                if (ciList.size() > 0) {
                    loading.setVisibility(View.GONE);

                    adapter = new CreditEvaluationListAdapter(ciList, "CI Evaluation History", new CreditEvaluationListAdapter.OnApplicationClickListener() {
                        @Override
                        public void OnClick(int position, List<DCreditOnlineApplicationCI.oDataEvaluationInfo> ciEvaluationLists) {
                            Intent loIntent = new Intent(Activity_EvaluationCIHistory.this, Activity_EvaluationCIHistoryInfo.class);
                            loIntent.putExtra("sTransNox", ciEvaluationLists.get(position).sTransNox);
                            loIntent.putExtra("sClientNm", ciEvaluationLists.get(position).sClientNm);
                            loIntent.putExtra("dTransact", ciEvaluationLists.get(position).dTransact);
                            loIntent.putExtra("sBranchxx", ciEvaluationLists.get(position).sBranchNm);
                            if(getIntent().hasExtra("cPreviewx")){
                                loIntent.putExtra("cPreviewx", getIntent().getBooleanExtra("cPreviewx", true));
                            }
                            startActivity(loIntent);
                        }

                    });
                    LinearLayoutManager layoutManager = new LinearLayoutManager(Activity_EvaluationCIHistory.this);
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