package org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitChecklist;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitDetail;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Adapter.Adapter_Branch_Vist_Checklist;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel.VMBranchVisit;

import java.util.List;

public class Activity_Branch_Visit_Checklist extends AppCompatActivity {

    private String lsSourceNo;
    private List<EBranchVisitChecklist> laChecklist;

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

        InitWidgets();
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

        //do not continue if transaction type is empty
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
        }

        //do not continue if source number is empty
        if (!getIntent().hasExtra("sSourceNo")){

            InitMessage(2, "Source number not found!", new onMessageButton() {
                @Override
                public void onPositive() {
                    finish();
                }
                @Override
                public void onNegative() {}
            });
            return;
        }
        lsSourceNo = getIntent().getStringExtra("sSourceNo");

        //always reload checklist
        mviewModel.ImportChecklistDetails(lsSourceNo, new VMBranchVisit.OnImportChecklist() {
            @Override
            public void OnLoad() {
                poDialog.initDialog(getClass().getSimpleName(), "Downloading checklist details . . .", false);
                poDialog.show();
            }

            @Override
            public void OnSuccess() {
                poDialog.dismiss();

                //start data observation
                InitObservers();
                Toast.makeText(Activity_Branch_Visit_Checklist.this, "Checklist details downloaded successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnFailed(String fsMessage) {
                poDialog.dismiss();

                //show error and ask user to try again, else, finish activity
                InitMessage(3, fsMessage + ". Do you want to try downloading again?", new onMessageButton() {
                    @Override
                    public void onPositive() {
                        InitData();
                    }

                    @Override
                    public void onNegative() { finish(); }
                });
            }
        });
    }

    private void InitObservers(){

        //do not proceed, if source no is empty
        if (lsSourceNo == null || lsSourceNo.isEmpty()) return;

        mviewModel.GetChecklist().observe(Activity_Branch_Visit_Checklist.this, new Observer<List<EBranchVisitChecklist>>() {
            @Override
            public void onChanged(List<EBranchVisitChecklist> eBranchVisitChecklists) {

                //if checklist is empty, ask user to re download checklist, if declined, finish activity
                if (eBranchVisitChecklists.size() <= 0){

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

                //initialize adapter
                loAdapter = new Adapter_Branch_Vist_Checklist(lsSourceNo, eBranchVisitChecklists, new Adapter_Branch_Vist_Checklist.OnItemListener() {
                    @Override
                    public void OnCamera(String fsCategrID) {

                    }

                    @Override
                    public void OnViewDetails(String fsSourceNo, String fsCategrID) {

                    }

                    @Override
                    public void OnRemarks(String fsCategrID, String sRemarks) {

                    }
                });

                recyclerview_checklist.setAdapter(loAdapter);
                recyclerview_checklist.setLayoutManager(new LinearLayoutManager(Activity_Branch_Visit_Checklist.this,  LinearLayoutManager.VERTICAL, false));
            }
        });

        //do not proceed if checklist is empty
        if (laChecklist == null || laChecklist.size() <= 0) return;

        mviewModel.GetDetails(lsSourceNo).observe(Activity_Branch_Visit_Checklist.this, new Observer<List<EBranchVisitDetail>>() {
            @Override
            public void onChanged(List<EBranchVisitDetail> eBranchVisitDetails) {

                //if checklist is empty, ask user to re download checklist, if declined, finish activity
                if (eBranchVisitDetails.size() <= 0){

                    InitMessage(3, "Details not found! Do you want to re-download data?", new onMessageButton() {
                        @Override
                        public void onPositive() {
                            InitData();
                        }
                        @Override
                        public void onNegative() { finish(); }
                    });
                }
            }
        });
    }
}