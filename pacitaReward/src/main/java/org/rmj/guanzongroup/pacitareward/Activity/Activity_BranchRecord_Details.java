package org.rmj.guanzongroup.pacitareward.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.EPacitaEvaluation;
import org.rmj.g3appdriver.GCircle.room.Entities.EPacitaRule;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.GawadPacita.Obj.PacitaRule;
import org.rmj.g3appdriver.GCircle.Apps.GawadPacita.pojo.BranchRate;
import org.rmj.guanzongroup.pacitareward.Adapter.RecyclerViewAdapter_RecordDetails;
import org.rmj.guanzongroup.pacitareward.R;
import org.rmj.guanzongroup.pacitareward.ViewModel.VMBranchRecordDetails;
import org.rmj.guanzongroup.pacitareward.ViewModel.VMBranchRecordDetails.BranchRecordDetailsCallBack;

import java.util.List;

public class Activity_BranchRecord_Details extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private MaterialTextView mtv_branchname;
    private VMBranchRecordDetails mViewModel;
    private String intentDataBranchcd;
    private String intentDataBranchName;
    private String intentDataTransactNo;
    private RecyclerView branch_rec;
    private LoadDialog poLoad;
    private MessageBox poMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branchrecord_details);

        mViewModel = new ViewModelProvider(this).get(VMBranchRecordDetails.class);

        poLoad = new LoadDialog(Activity_BranchRecord_Details.this);
        poMessage = new MessageBox(Activity_BranchRecord_Details.this);

        intentDataBranchcd = getIntent().getStringExtra("Branch Code");
        intentDataBranchName = getIntent().getStringExtra("Branch Name");
        intentDataTransactNo = getIntent().getStringExtra("Transaction No");

        toolbar = findViewById(R.id.toolbar);
        branch_rec = findViewById(R.id.branch_rec);
        mtv_branchname = findViewById(R.id.mtv_branchname);

        setSupportActionBar(toolbar); //set object toolbar as default action bar for activity
        getSupportActionBar().setTitle(""); //set default title for action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set back button to toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true); //enable the back button set on toolbar

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mtv_branchname.setText(intentDataBranchName);

        mViewModel.onEvaluationRecords(intentDataBranchcd, new BranchRecordDetailsCallBack() {
            @Override
            public void onInitialize(String message) {
                poLoad.initDialog("Record Details", message, false);
                poLoad.show();
            }

            @Override
            public void onSuccess(String message) {
                poLoad.dismiss();
                mViewModel.getBranchEvaluation(intentDataTransactNo).observe(Activity_BranchRecord_Details.this, new Observer<EPacitaEvaluation>() {
                    @Override
                    public void onChanged(EPacitaEvaluation ePacitaEvaluation) {
                        if(ePacitaEvaluation == null){
                            return;
                        }

                        mViewModel.GetCriteria().observe(Activity_BranchRecord_Details.this, new Observer<List<EPacitaRule>>() {
                            @Override
                            public void onChanged(List<EPacitaRule> ePacitaRules) {
                                if(ePacitaRules == null){
                                    return;
                                }
                                if(ePacitaRules.size() <= 0){
                                    return;
                                }

                                String lsPayload = ePacitaEvaluation.getPayloadx();
                                List<BranchRate> loRate = PacitaRule.ParseBranchRate(lsPayload, ePacitaRules);

                                RecyclerViewAdapter_RecordDetails loAdapter =
                                        new RecyclerViewAdapter_RecordDetails(loRate);
                                branch_rec.setLayoutManager(new LinearLayoutManager(Activity_BranchRecord_Details.this, RecyclerView.VERTICAL, false));
                                branch_rec.setAdapter(loAdapter);
                                Log.d("Adapter Item Count", "Count: " + loAdapter.getItemCount());
                            }
                        });
                    }
                });

            }

            @Override
            public void onError(String message) {
                poLoad.dismiss();
                poMessage.initDialog();
                poMessage.setIcon(R.drawable.baseline_error_24);
                poMessage.setTitle("Transaction Result");
                poMessage.setMessage(message);
                poMessage.setPositiveButton("Dismiss", new MessageBox.DialogButton() {
                    @Override
                    public void OnButtonClick(View view, AlertDialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                });
                poMessage.show();
            }
        });
    }
}
