package org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitDetail;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitChecklist;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;
import org.rmj.g3appdriver.etc.DialogDisclosure;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Adapter.Adapter_Branch_Vist_Checklist;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel.VMBranchVisit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity_Branch_Visit_Checklist extends AppCompatActivity {

    private String lsSourceNo;
    private List<EBranchVisitChecklist> laChecklist = new ArrayList<>();
    private EBranchVisitMaster loMaster;
    private List<DBranchVisitDetail.BranchVisitDetail> laDetails;
    private EImageInfo loImage;
    private List<EImageInfo> laImages;

    private VMBranchVisit mviewModel;
    private Adapter_Branch_Vist_Checklist loAdapter;
    private DialogDisclosure dialogDisclosure;

    //record detail objects
    private MaterialToolbar toolbar;
    private FrameLayout frame_record;
    private LinearLayout layout_noRecord;
    private MaterialTextView mtv_branch, mtv_transactionno, mtv_status, mtv_date, mtv_send;
    private RecyclerView recyclerview_checklist;
    private MaterialButton btn_submit;

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

    private final ActivityResultLauncher<Intent> poCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            try {

                if (o.getResultCode() == RESULT_OK){

                    //TODO: 1. GET COORDINATES OF CAPTURED IMAGE'S PROPERTIES,
                    // NOTE: TO GET THIS PROPERLY. ENABLE MANUALLY THE TAG LOCATION SETTINGS ON CAMERA WITHIN THE APP
                    @SuppressLint({"NewApi", "LocalSuppress"}) ExifInterface exifInterface =
                            new ExifInterface(
                                    Objects.requireNonNull(getContentResolver().openInputStream(
                                            MediaStore.setRequireOriginal(Uri.fromFile(new File(loImage.getFileLoct())))
                                    ))
                            );

                    //TODO: 2. SET IMAGE COORDINATES, IF NOT EMPTY
                    if (exifInterface.getLatLong() != null){

                        Log.d("Activity_Branch_Visit_Checklist", "Image Longitude is " + String.valueOf(Objects.requireNonNull(exifInterface.getLatLong())[1])
                                + " and Image Latitude is " + String.valueOf(exifInterface.getLatLong()[0]));

                        loImage.setLatitude(String.valueOf(exifInterface.getLatLong()[0]));
                        loImage.setLongitud(String.valueOf(exifInterface.getLatLong()[1]));
                    }

                    //TODO: 3. VALIDATE SAVED COORDINATES
                    if (loImage.getLongitud() == null || loImage.getLatitude() == null){
                        InitMessage(2, "Unable to get location coordinates. Please inform your superior for this matter.", new onMessageButton() {
                            @Override
                            public void onPositive() {}

                            @Override
                            public void onNegative() {}
                        });
                        return;
                    }

                    if (loImage.getLongitud().isEmpty() || loImage.getLatitude().isEmpty()){
                        InitMessage(2, "Unable to get location coordinates. Please inform your superior for this matter.", new onMessageButton() {
                            @Override
                            public void onPositive() {}

                            @Override
                            public void onNegative() {}
                        });
                        return;
                    }

                    if (loImage.getLongitud().equalsIgnoreCase("0.00000000000") || loImage.getLatitude().equalsIgnoreCase("0.00000000000")) {
                        InitMessage(2, "Location coordinates is invalid. Please inform your superior for this matter.", new onMessageButton() {
                            @Override
                            public void onPositive() {}

                            @Override
                            public void onNegative() {}
                        });
                        return;
                    }

                    mviewModel.SaveImage(lsSourceNo, loImage.getImageNme(), loImage.getFileLoct(), loImage.getLongitud(), loImage.getLatitude(), loImage.getFileCode());

                    Toast.makeText(Activity_Branch_Visit_Checklist.this, "Image saved succesfully", Toast.LENGTH_SHORT).show();
                }

            }catch (Exception e){
                mviewModel.SaveError("Branch Visit Selfie", e.getMessage());
            }

        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_branch_checklist);

        mviewModel = new ViewModelProvider(this).get(VMBranchVisit.class);
        poDialog = new LoadDialog(this);
        loMessage = new MessageBox(this);
        dialogDisclosure = new DialogDisclosure(this);
        loImage = new EImageInfo();
        laImages = new ArrayList<>();

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
        btn_submit = findViewById(R.id.btn_submit);

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

        try {

            //always reload checklist to get updated checklists
            mviewModel.ImportChecklist(new VMBranchVisit.OnImport() {
                @Override
                public void OnLoad() {
                    poDialog.initDialog(getClass().getSimpleName(), "Downloading checklist details . . .", false);
                    poDialog.show();

                    btn_submit.setEnabled(false);
                }
                @Override
                public void OnFinished(String fsMessage) {
                    poDialog.dismiss();
                    Toast.makeText(Activity_Branch_Visit_Checklist.this, fsMessage, Toast.LENGTH_SHORT).show();

                    btn_submit.setEnabled(true);

                    //initialize observers
                    InitObservers();
                }
            });
            Thread.sleep(500);

            if (loMaster == null) return; //do not proceed if master is empty

            //download details & images if master transaction is from database
            if (loMaster.getcSendStat().equalsIgnoreCase("1")){

                if (lsSourceNo == null || lsSourceNo.isEmpty()) return;

                mviewModel.ImportDetails(lsSourceNo, new VMBranchVisit.OnImport() {
                    @Override
                    public void OnLoad() {
                        Toast.makeText(Activity_Branch_Visit_Checklist.this, "Downloading details . . .", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnFinished(String fsMessage) {
                        Toast.makeText(Activity_Branch_Visit_Checklist.this, fsMessage, Toast.LENGTH_SHORT).show();

                        //initialize observers
                        InitObservers();
                    }
                });
                Thread.sleep(1000);

                mviewModel.ImportBranchVisitSelfie(lsSourceNo, new VMBranchVisit.OnImport() {
                    @Override
                    public void OnLoad() {
                        Toast.makeText(Activity_Branch_Visit_Checklist.this, "Downloading images . . .", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnFinished(String fsMessage) {
                        Toast.makeText(Activity_Branch_Visit_Checklist.this, fsMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }catch (Exception e){
            mviewModel.SaveError("Branch Visit Checklist", e.getMessage());
        }
    }

    private void InitDisplayMaster(){

        if (loMaster == null) return;

        mtv_branch.setText(mviewModel.GetBranchName(loMaster.getsBranchCd()).getBranchNm());
        mtv_transactionno.setText(lsSourceNo);
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

                        //enable submit button if transaction is not open(0)
                        btn_submit.setEnabled(loMaster.getcTranStat().equalsIgnoreCase("0") && loMaster.getdTransact().equals(mviewModel.GetCurrentDate()));

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
                                        InitCamera(fsCategrID);
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

                                //allow modification, if transaction is open(0)
                                loAdapter.AllowEdit(loMaster.getcTranStat().equalsIgnoreCase("0") && loMaster.getdTransact().equals(mviewModel.GetCurrentDate()));

                                recyclerview_checklist.setAdapter(loAdapter);
                                recyclerview_checklist.setLayoutManager(new LinearLayoutManager(Activity_Branch_Visit_Checklist.this,  LinearLayoutManager.VERTICAL, false));
                            }
                        });
                    }
                });

                mviewModel.GetTransactionImagesForUpload(lsSourceNo, "BVS").observe(Activity_Branch_Visit_Checklist.this, new Observer<List<EImageInfo>>() {
                    @Override
                    public void onChanged(List<EImageInfo> eImageInfos) {

                        //do not proceed, if empty
                        if (eImageInfos == null || eImageInfos.size() < 1){
                            return;
                        }
                        laImages = eImageInfos;
                    }
                });
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void InitListener(){

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitPreviewImage(false);
                InitShowImageDetails(false);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mviewModel.SubmitBranchVisit(loMaster, laDetails, new VMBranchVisit.OnSubmit() {
                            @Override
                            public void OnLoad() {
                                poDialog.initDialog("Branch Visit Checklist", "Submitting Details. Please wait . . .", false);
                                poDialog.show();
                            }

                            @Override
                            public void OnSuccess() {
                                poDialog.dismiss();

                                try {

                                    for (EImageInfo loImage : laImages){

                                        //upload images
                                        mviewModel.SubmitImage(loImage, new VMBranchVisit.OnSubmitImageCallback() {
                                            @Override
                                            public void OnLoad(String fsTitlexx, String fsMessage) {
                                                Toast.makeText(Activity_Branch_Visit_Checklist.this, "Uploading image. Please wait . .", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void OnSuccess(String fsTransnox) {
                                                Toast.makeText(Activity_Branch_Visit_Checklist.this, "Uploading successful", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void OnFailed(String fsMessage) {
                                                Toast.makeText(Activity_Branch_Visit_Checklist.this, fsMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        Thread.sleep(1000);
                                    }
                                    finish();

                                }catch (Exception e){
                                    mviewModel.SaveError("Branch Visit Checklist", e.getMessage());
                                }
                            }

                            @Override
                            public void OnFailed(String fsMessage) {
                                poDialog.dismiss();

                                InitMessage(2, fsMessage, new onMessageButton() {
                                    @Override
                                    public void onPositive() { finish(); }

                                    @Override
                                    public void onNegative() {}
                                });
                            }
                        });
                    }
                });
            }
        });

        layout_imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitShowImageDetails(false);
            }
        });

        img_selfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitShowImageDetails(true);
            }
        });
    }

    private void OpenPermission(){

        //ask user to grant permission
        dialogDisclosure.initDialog(new DialogDisclosure.onDisclosure() {
            @Override
            public void onAccept() {

                try {

                    Intent loIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    loIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    loIntent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(loIntent);

                }catch (ActivityNotFoundException e){

                    Intent loIntent = new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
                    loIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loIntent);

                }catch (Exception e){
                    mviewModel.SaveError("Branch Visit Checklist", e.getMessage());
                }

                dialogDisclosure.dismiss();

            }

            @Override
            public void onDecline() {
                Toast.makeText(Activity_Branch_Visit_Checklist.this, "Please allow missing permissions to continue", Toast.LENGTH_LONG).show();
                dialogDisclosure.dismiss();
            }
        });
        dialogDisclosure.setMessage("Guanzon Circle requires camera and storage permissions to take SSDD images when the app is in use.");
        dialogDisclosure.show();
    }

    private void InitCamera(String fsCategrID){

        //check permission
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            OpenPermission();
            return;
        }

        mviewModel.InitCamera(lsSourceNo, fsCategrID, new VMBranchVisit.OnInitializeCameraListner() {
            @Override
            public void OnInit() {
                poDialog.initDialog("Camera Launch", "Initializing camera. Please wait . .", false);
                poDialog.show();
            }

            @Override
            public void OnSuccess(Intent intent, String[] args) {

                poDialog.dismiss();

                loImage.setSourceNo(lsSourceNo);
                loImage.setFileLoct(args[0]); //pass file path
                loImage.setImageNme(args[1]); //pass file name
                loImage.setFileCode(args[2]); //pass category id
                loImage.setLongitud(args[3]); //pass longitude
                loImage.setLatitude(args[4]); //pass latitude

                //start camera intent
                poCamera.launch(intent);
            }

            @Override
            public void OnFailed(String message, Intent intent, String[] args) {

                poDialog.dismiss();

                loImage.setSourceNo(lsSourceNo);
                loImage.setFileLoct(args[0]); //pass file path
                loImage.setImageNme(args[1]); //pass file name
                loImage.setFileCode(args[2]); //pass category id
                loImage.setLongitud(args[3]); //pass longitude
                loImage.setLatitude(args[4]); //pass latitude

                //start camera intent
                poCamera.launch(intent);

            }

            @Override
            public void OnError(String fsMessage) {

                poDialog.dismiss();

                InitMessage(2, fsMessage, new onMessageButton() {
                    @Override
                    public void onPositive() {}

                    @Override
                    public void onNegative() {}
                });

            }
        });

    }
}