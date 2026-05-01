package org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitDetail;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitChecklist;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitDetail;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitMaster;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Adapter.Adapter_Branch_Vist_Checklist;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel.VMBranchVisit;

import java.util.ArrayList;
import java.util.List;

public class Activity_Branch_Visit_Checklist extends AppCompatActivity {

    private String lsSourceNo;
    private List<EBranchVisitChecklist> laChecklist = new ArrayList<>();
    private EBranchVisitMaster loMaster;
    private List<DBranchVisitDetail.BranchVisitDetail> laDetails;

    private VMBranchVisit mviewModel;
    private Adapter_Branch_Vist_Checklist loAdapter;

    private RecyclerView recyclerview_checklist;

    private LoadDialog poDialog;
    private MessageBox loMessage;

    private interface onMessageButton{
        void onPositive();
        void onNegative();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_branch_checklist);

        mviewModel = new ViewModelProvider(this).get(VMBranchVisit.class);
        poDialog = new LoadDialog(this);
        loMessage = new MessageBox(this);

        //do not continue if required arguments is empty
        if (!getIntent().hasExtra("type")){

            InitMessage(2, "Could not verify type of transaction", new onMessageButton() {
                @Override
                public void onPositive() {
                    finish();
                }
                @Override
                public void onNegative() {}
            });
            return;
        }else if (!getIntent().hasExtra("branch")){

            InitMessage(2, "Could not verify source branch", new onMessageButton() {
                @Override
                public void onPositive() {
                    finish();
                }
                @Override
                public void onNegative() {}
            });
            return;
        }

        InitWidgets();
        InitObservers();
        InitData();
    }

    private void InitMessage(int mode, String message, onMessageButton callback){

        loMessage.initDialog();
        loMessage.setTitle("Guanzon Circle");
        loMessage.setMessage(message);

        switch (mode){

            case 1, 2:
                if (mode == 1){
                    loMessage.setIcon(R.drawable.baseline_message_24);
                }

                if (mode == 2){
                    loMessage.setIcon(R.drawable.baseline_error_24);
                }

                loMessage.setPositiveButton("Okay", new MessageBox.DialogButton() {
                    @Override
                    public void OnButtonClick(View view, AlertDialog dialog) {
                        dialog.dismiss();
                        callback.onPositive();
                    }
                });

                break;

            case 3:
                loMessage.setIcon(R.drawable.baseline_contact_support_24);
                loMessage.setPositiveButton("Yes", new MessageBox.DialogButton() {
                    @Override
                    public void OnButtonClick(View view, AlertDialog dialog) {
                        dialog.dismiss();
                        callback.onPositive();
                    }
                });
                loMessage.setNegativeButton("No", new MessageBox.DialogButton() {
                    @Override
                    public void OnButtonClick(View view, AlertDialog dialog) {
                        dialog.dismiss();
                        callback.onNegative();
                    }
                });

                break;
        }

        loMessage.show();
    }

    private void InitWidgets(){
        recyclerview_checklist = findViewById(R.id.recyclerview_checklist);
    }

    private void InitData(){

        //always reload checklist to get updated checklists
        mviewModel.ImportChecklist(new VMBranchVisit.OnImportChecklist() {
            @Override
            public void OnLoad() {
                poDialog.initDialog(getClass().getSimpleName(), "Downloading checklist details . . .", false);
                poDialog.show();
            }
            @Override
            public void OnFinished(String fsMessage) {
                poDialog.dismiss();
                Toast.makeText(Activity_Branch_Visit_Checklist.this, fsMessage, Toast.LENGTH_SHORT).show();

                if (laChecklist.size() <= 0){
                    return;
                }

                //'0' create new entry master & detail then initialize transaction number, '1' initialize only transaction number from intent
                if (getIntent().getStringExtra("type").equalsIgnoreCase("0")) {

                    EBranchVisitMaster loMaster = mviewModel.GetEntryToday();
                    if (loMaster == null){
                        lsSourceNo = mviewModel.SaveNewMaster(getIntent().getStringExtra("branch"));
                    }else {
                        lsSourceNo = loMaster.getsTransNox();
                    }

                    laChecklist.forEach(eBranchVisitChecklist -> {
                        mviewModel.SaveNewDetail(lsSourceNo, eBranchVisitChecklist.getsCategrID(), "");
                    });

                } else if (getIntent().getStringExtra("type").equalsIgnoreCase("1")) {

                    //if transaction number not set from history, finish activity
                    if (!getIntent().hasExtra("source")){

                        InitMessage(2, "Could not verify transaction number", new onMessageButton() {
                            @Override
                            public void onPositive() {
                                finish();
                            }
                            @Override
                            public void onNegative() {}
                        });
                        return;
                    }
                    lsSourceNo = getIntent().getStringExtra("source");
                }
                InitObservers();


                if (loMaster == null) return; //do not proceed if master is empty

                //download details if master transaction is from database
                if (loMaster.getcSendStat().equalsIgnoreCase("1")){

                    mviewModel.ImportDetails(lsSourceNo, new VMBranchVisit.OnImportChecklist() {
                        @Override
                        public void OnLoad() {
                            Toast.makeText(Activity_Branch_Visit_Checklist.this, "Downloading details . . .", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void OnFinished(String fsMessage) {
                            Toast.makeText(Activity_Branch_Visit_Checklist.this, fsMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                InitObservers();
            }
        });
    }

    private void InitObservers(){

        mviewModel.GetChecklist().observe(Activity_Branch_Visit_Checklist.this, new Observer<List<EBranchVisitChecklist>>() {
            @Override
            public void onChanged(List<EBranchVisitChecklist> eBranchVisitChecklists) {

                //if checklist is empty, ask user to re download checklist, if declined, finish activity
                if (eBranchVisitChecklists == null || eBranchVisitChecklists.size() <= 0){

                    InitMessage(3, "Checklist not found! Do you want to re-download data?", new onMessageButton() {
                        @Override
                        public void onPositive() {
                            InitData();
                        }
                        @Override
                        public void onNegative() { finish(); }
                    });
                }

                //initialize checklist
                laChecklist = eBranchVisitChecklists;
            }
        });

        Log.d("Branch checklist size is  ", String.valueOf(laChecklist.size()));
        if (laChecklist.size() <= 0){
            return;
        } else if (lsSourceNo == null || lsSourceNo.isEmpty()) {
            return;
        }

        mviewModel.GetMasterTransaction(lsSourceNo).observe(Activity_Branch_Visit_Checklist.this, new Observer<EBranchVisitMaster>() {
            @Override
            public void onChanged(EBranchVisitMaster eBranchVisitMaster) {
                loMaster = eBranchVisitMaster;
            }
        });

        if (loMaster == null){
            return;
        }

        mviewModel.GetDetails(lsSourceNo).observe(Activity_Branch_Visit_Checklist.this, new Observer<List<DBranchVisitDetail.BranchVisitDetail>>() {
            @Override
            public void onChanged(List<DBranchVisitDetail.BranchVisitDetail> eBranchVisitDetails) {

                if (eBranchVisitDetails == null || eBranchVisitDetails.size() <= 0){

                    Log.d("Branch checklist status is  ", loMaster.getcSendStat());
                    if (loMaster.getcSendStat().equalsIgnoreCase("1")){

                        InitMessage(3, "Details not found! Do you want to re-download data?", new onMessageButton() {
                            @Override
                            public void onPositive() {
                                InitData();
                            }
                            @Override
                            public void onNegative() { finish(); }
                        });

                    }
                    return;
                }

                //initialize checklist
                laDetails = eBranchVisitDetails;

                ///initialize adapter
                loAdapter = new Adapter_Branch_Vist_Checklist(laDetails, new Adapter_Branch_Vist_Checklist.OnItemListener() {
                    @Override
                    public void OnCamera(String fsCategrID) {

                    }

                    @Override
                    public void OnViewDetails(String fsCategrID) {

                    }

                    @Override
                    public void OnRemarks(String fsCategrID, String sRemarks) {

                    }
                });

                recyclerview_checklist.setAdapter(loAdapter);
                recyclerview_checklist.setLayoutManager(new LinearLayoutManager(Activity_Branch_Visit_Checklist.this,  LinearLayoutManager.VERTICAL, false));
            }
        });
    }
}