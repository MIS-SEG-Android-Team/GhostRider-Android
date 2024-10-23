package org.rmj.guanzongroup.pacitareward.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.rmj.g3appdriver.GCircle.room.Entities.EPacitaEvaluation;
import org.rmj.g3appdriver.GCircle.room.Entities.EPacitaRule;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.GawadPacita.Obj.PacitaRule;
import org.rmj.g3appdriver.GCircle.Apps.GawadPacita.pojo.BranchRate;
import org.rmj.guanzongroup.pacitareward.Adapter.RecyclerViewAdapter_BranchRate;
import org.rmj.guanzongroup.pacitareward.R;
import org.rmj.guanzongroup.pacitareward.ViewModel.VMBranchRate;

import java.util.List;

public class Activity_Branch_Rate extends AppCompatActivity {
    private String TAG = Activity_Branch_Rate.class.getSimpleName();
    private RecyclerView rate_list;
    private MaterialToolbar toolbar;
    private MaterialTextView rate_title;
    private VMBranchRate mViewModel;
    private MessageBox poMessage;
    private LoadDialog poLoad;
    private String intentDataBranchcd;
    private String intentDataBranchName;
    private MaterialButton btn_submit;
    private String lsPayload;
    private RecyclerViewAdapter_BranchRate loAdapter;
    private EPacitaEvaluation loEvaluation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_rate);

        mViewModel = new ViewModelProvider(this).get(VMBranchRate.class);

        poLoad = new LoadDialog(Activity_Branch_Rate.this);

        poMessage = new MessageBox(Activity_Branch_Rate.this);

        intentDataBranchcd = getIntent().getStringExtra("Branch Code");
        intentDataBranchName = getIntent().getStringExtra("Branch Name");

        toolbar = findViewById(R.id.toolbar);
        rate_list = findViewById(R.id.rate_list);
        rate_title = findViewById(R.id.rate_title);
        btn_submit = findViewById(R.id.btn_submit);

        rate_title.setText(intentDataBranchName);

        /*TOOL BAR*/
        setSupportActionBar(toolbar); //set object toolbar as default action bar for activity
        toolbar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set back button to toolbar

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mViewModel.InitializeEvaluation(intentDataBranchcd, new VMBranchRate.OnInitializeBranchEvaluationListener() {
            @Override
            public void onInitialize(String message) {
                poLoad.initDialog("Evaluation List", message, false);
                poLoad.show();
            }
            @Override
            public void OnSuccess(String transactNo, String message) {

                poLoad.dismiss();

                mViewModel.getBranchEvaluation(transactNo).observe(Activity_Branch_Rate.this, new Observer<EPacitaEvaluation>() {
                    @Override
                    public void onChanged(EPacitaEvaluation ePacitaEvaluation) {
                        if(ePacitaEvaluation == null){
                            return;
                        }

                        loEvaluation = ePacitaEvaluation;
                        loEvaluation.setTransNox(transactNo);

                    }
                });

                mViewModel.GetCriteria().observe(Activity_Branch_Rate.this, new Observer<List<EPacitaRule>>() {

                    @Override
                    public void onChanged(List<EPacitaRule> ePacitaRules) {
                        if(ePacitaRules == null){
                            return;
                        }
                        if(ePacitaRules.size() <= 0){
                            return;
                        }

                        lsPayload = loEvaluation.getPayloadx();
                        List<BranchRate> loRate = PacitaRule.ParseBranchRate(lsPayload, ePacitaRules);

                        loAdapter = new RecyclerViewAdapter_BranchRate(Activity_Branch_Rate.this, loRate, new RecyclerViewAdapter_BranchRate.onSelect() {
                            @Override
                            public void onItemSelect(String EntryNox, String result) {
                                try {

                                    JSONArray loArray = new JSONArray(lsPayload);

                                    for (int x = 0;  x < loArray.length(); x++){

                                        int lnEntryNo = loArray.getJSONObject(x).getInt("nEntryNox");

                                        if(Integer.parseInt(EntryNox) == lnEntryNo){

                                            loArray.getJSONObject(x).put("xRatingxx", result);

                                        }

                                    }

                                    //todo: collection of ratings result
                                    lsPayload = loArray.toString();

                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        });


                        rate_list.setAdapter(loAdapter);
                        rate_list.setLayoutManager(new LinearLayoutManager(Activity_Branch_Rate.this, LinearLayoutManager.VERTICAL, false));
                    }
                });

                btn_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!transactNo.isEmpty()){

                            mViewModel.setEvaluationResult(transactNo, lsPayload);

                            mViewModel.saveBranchRatings(transactNo, new VMBranchRate.BranchRatingsCallback() {
                                @Override
                                public void onSave(String title, String message) {
                                    poLoad.initDialog(title, message, false);
                                    poLoad.show();
                                }

                                @Override
                                public void onSuccess(String message) {
                                    poLoad.dismiss();
                                    poMessage.initDialog();
                                    poMessage.setIcon(R.drawable.baseline_message_24);
                                    poMessage.setTitle("Save Evaluation");
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

                                @Override
                                public void onFailed(String message) {
                                    poLoad.dismiss();
                                    poMessage.initDialog();
                                    poMessage.setIcon(R.drawable.ic_toast_error);
                                    poMessage.setTitle("Error Saving Application");
                                    poMessage.setMessage(message);
                                    poMessage.setPositiveButton("Dismiss", new MessageBox.DialogButton() {
                                        @Override
                                        public void OnButtonClick(View view, AlertDialog dialog) {
                                            dialog.dismiss();
                                        }
                                    });
                                    poMessage.show();
                                }
                            });
                        }
                    }
                });
            }
            @Override
            public void OnError(String message) {
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
                btn_submit.setEnabled(false);
            }
        });
    }
}