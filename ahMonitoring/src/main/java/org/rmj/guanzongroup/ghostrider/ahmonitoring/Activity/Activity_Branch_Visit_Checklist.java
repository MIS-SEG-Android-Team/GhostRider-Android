package org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

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

    //record detail objects
    private MaterialToolbar toolbar;
    private FrameLayout frame_record;
    private LinearLayout layout_noRecord;
    private MaterialTextView mtv_branch, mtv_transactionno, mtv_status, mtv_date, mtv_send;
    private RecyclerView recyclerview_checklist;

    //image preview objects
    private ConstraintLayout layout_imgPreview, layout_imgInfo;
    private ImageButton btn_close, btn_upload;
    private MaterialTextView mtv_category, mtv_imgNm, mtv_imgDate;
    private ShapeableImageView img_selfie;

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
        InitData();
        InitListener();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
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

        //record detail objects
        toolbar = findViewById(R.id.toolbar);
        frame_record = findViewById(R.id.frame_record);
        layout_noRecord = findViewById(R.id.layout_noRecord);
        mtv_branch = findViewById(R.id.mtv_branch);
        mtv_transactionno = findViewById(R.id.mtv_transactionno);
        mtv_status = findViewById(R.id.mtv_status);
        mtv_date = findViewById(R.id.mtv_date);
        mtv_send = findViewById(R.id.mtv_send);
        recyclerview_checklist = findViewById(R.id.recyclerview_checklist);

        //image preview objects
        layout_imgPreview = findViewById(R.id.layout_imgPreview);
        layout_imgInfo = findViewById(R.id.layout_imgInfo);
        btn_close = findViewById(R.id.btn_close);
        mtv_category = findViewById(R.id.mtv_category);
        mtv_imgNm = findViewById(R.id.mtv_imgNm);
        mtv_imgDate = findViewById(R.id.mtv_imgDate);
        btn_upload = findViewById(R.id.btn_upload);
        img_selfie= findViewById(R.id.img_selfie);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
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

                //initialize observers
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
            }
        });
    }

    private void InitDisplayMaster(){

        if (loMaster == null) return;

        mtv_branch.setText(mviewModel.GetBranchName(loMaster.getsBranchCd()).getBranchNm());
        mtv_transactionno.setText(loMaster.getsTransNox());
        mtv_date.setText(loMaster.getdTransact());

        switch (loMaster.getcTranStat()){

            case "0":
                mtv_status.setText("Open");
                break;
            case "1":
                mtv_status.setText("Closed");
                break;
            case "2":
                mtv_status.setText("Posted");
                break;
        }

        if (loMaster.getcSendStat().equalsIgnoreCase("1")){
            mtv_send.setText("Sent");
            mtv_send.setTextColor(Color.GREEN);
        }else {
            mtv_send.setText("Pending");
            mtv_send.setTextColor(Color.RED);
        }
    }

    private void InitDisplayRecord(Boolean fbisDisplay){
        if (fbisDisplay){
            frame_record.setVisibility(View.VISIBLE);
            layout_noRecord.setVisibility(View.GONE);
        }else {
            frame_record.setVisibility(View.GONE);
            layout_noRecord.setVisibility(View.VISIBLE);
        }
    }

    private void InitPreviewImage(Boolean fbisDisplay){
        if (fbisDisplay){
            if (layout_imgPreview.getVisibility() == View.VISIBLE) return;
            layout_imgPreview.setVisibility(View.VISIBLE);
        }else {
            if (layout_imgPreview.getVisibility() == View.GONE) return;
            layout_imgPreview.setVisibility(View.GONE);
        }
    }

    private void InitShowImageDetails(Boolean fbisDisplay){
        if (fbisDisplay){
            if (layout_imgInfo.getVisibility() == View.VISIBLE) return;
            layout_imgInfo.setVisibility(View.VISIBLE);
        }else {
            if (layout_imgInfo.getVisibility() == View.GONE) return;
            layout_imgInfo.setVisibility(View.GONE);
        }
    }

    private void InitObservers(){

        //show no record
        InitDisplayRecord(false);

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

                //do not proceed if checklist is empty
                if (laChecklist.size() <= 0){
                    return;
                }

                //'0' create new entry master & detail then initialize transaction number, '1' initialize only transaction number from intent
                if (getIntent().getStringExtra("type").equalsIgnoreCase("0")) {

                    //create new entry if no existing record today, else, initialize source number only
                    EBranchVisitMaster loMaster = mviewModel.GetEntryToday();
                    if (loMaster == null){
                        lsSourceNo = mviewModel.SaveNewMaster(getIntent().getStringExtra("branch"));

                        laChecklist.forEach(eBranchVisitChecklist -> {
                            mviewModel.SaveNewDetail(lsSourceNo, eBranchVisitChecklist.getsCategrID(), "");
                        });
                    }else {
                        lsSourceNo = loMaster.getsTransNox();
                    }

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

                //do not proceed if transaction number is not initialized
                if (lsSourceNo == null || lsSourceNo.isEmpty()) {
                    return;
                }

                //initialize master and details
                mviewModel.GetMasterTransaction(lsSourceNo).observe(Activity_Branch_Visit_Checklist.this, new Observer<EBranchVisitMaster>() {
                    @Override
                    public void onChanged(EBranchVisitMaster eBranchVisitMaster) {

                        loMaster = eBranchVisitMaster;

                        if (loMaster == null){
                            return;
                        }

                        //initialize details
                        mviewModel.GetDetails(lsSourceNo).observe(Activity_Branch_Visit_Checklist.this, new Observer<List<DBranchVisitDetail.BranchVisitDetail>>() {
                            @Override
                            public void onChanged(List<DBranchVisitDetail.BranchVisitDetail> eBranchVisitDetails) {

                                if (eBranchVisitDetails == null || eBranchVisitDetails.size() <= 0){

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

                                //show record list
                                InitDisplayRecord(true);

                                //display master info
                                InitDisplayMaster();

                                ///initialize adapter
                                loAdapter = new Adapter_Branch_Vist_Checklist(laDetails, new Adapter_Branch_Vist_Checklist.OnItemListener() {
                                    @Override
                                    public void OnCamera(String fsCategrID) {

                                    }

                                    @Override
                                    public void OnPreviewImage(String fsCategrID) {
                                        InitPreviewImage(true);
                                        InitShowImageDetails(true);
                                    }

                                    @Override
                                    public void OnRemarks(String fsCategrID, String sRemarks) {
                                        mviewModel.UpdateRemarks(sRemarks, lsSourceNo, fsCategrID);
                                    }
                                });

                                //if transaction is from previous date, else if not "OPEN", do not allow modification
                                if (!loMaster.getdTransact().equalsIgnoreCase(mviewModel.GetCurrentDate())){
                                    loAdapter.AllowEdit(false);
                                } else if (!loMaster.getcTranStat().equalsIgnoreCase("0")) {
                                    loAdapter.AllowEdit(false);
                                }else {
                                    loAdapter.AllowEdit(true);
                                }

                                recyclerview_checklist.setAdapter(loAdapter);
                                recyclerview_checklist.setLayoutManager(new LinearLayoutManager(Activity_Branch_Visit_Checklist.this,  LinearLayoutManager.VERTICAL, false));
                            }
                        });
                    }
                });
            }
        });
    }

    private void InitListener(){

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitPreviewImage(false);
                InitShowImageDetails(false);
            }
        });

        layout_imgPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InitShowImageDetails(true);
                return false;
            }
        });

        layout_imgInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InitShowImageDetails(false);
                return false;
            }
        });
    }
}