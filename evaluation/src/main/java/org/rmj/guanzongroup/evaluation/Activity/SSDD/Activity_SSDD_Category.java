package org.rmj.guanzongroup.evaluation.Activity.SSDD;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCallerLauncher;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.EErrorLogs;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDImages;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDetail;
import org.rmj.g3appdriver.etc.DialogDisclosure;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.etc.OnInitializeCameraCallback;
import org.rmj.guanzongroup.evaluation.Adapter.SSDD.Adapter_SSDDCategories;
import org.rmj.guanzongroup.evaluation.R;
import org.rmj.guanzongroup.evaluation.ViewModel.SSDD.VMSSDEvaluation;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Activity_SSDD_Category extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private VMSSDEvaluation mViewModel;
    private LoadDialog poDialog;
    private MessageBox poMessage;
    private DialogDisclosure dialogDisclosure;
    private ActivityResultLauncher<Intent> poCamera;

    private MaterialTextView mtv_dept, mtv_evaluation, mtv_ratings;
    private RecyclerView rcv_adapter;
    private MaterialButton btn_submit;

    private Adapter_SSDDCategories loAdapter;
    private ESSDDMaster loMaster = new ESSDDMaster();
    private List<ESSDDetail> laDetails = new ArrayList<>();


    private interface onMessageButton{
        void onPositive();
        void onNegative();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ssdd_category);

        mViewModel = new ViewModelProvider(this).get(VMSSDEvaluation.class);
        poDialog = new LoadDialog(this);
        poMessage = new MessageBox(this);
        dialogDisclosure = new DialogDisclosure(this);

        mtv_dept = findViewById(R.id.mtv_dept);
        mtv_evaluation = findViewById(R.id.mtv_evaluation);
        mtv_ratings = findViewById(R.id.mtv_ratings);
        btn_submit = findViewById(R.id.btn_submit);

        rcv_adapter = findViewById(R.id.rcv_adapter);

        InitActivity();
    }

    private List<Adapter_SSDDCategories.SSDD_Evaluation_Categories> GetList(){

        //initialize list for adapter
        List<Adapter_SSDDCategories.SSDD_Evaluation_Categories> laCategories = new ArrayList<>();
        for (ESSDDetail loDetal : laDetails){

            Adapter_SSDDCategories.SSDD_Evaluation_Categories loCategory = new Adapter_SSDDCategories.SSDD_Evaluation_Categories(
                    loMaster.getcTranStat(),
                    loDetal.getsCategrID(),
                    mViewModel.GetCategory(loDetal.getsCategrID()).getsDescript(),
                    Double.parseDouble(loDetal.getnRatingxx()),
                    loDetal.getsRemarksx(),
                    loDetal.getdEvaluate()

            );
            laCategories.add(loCategory);
        }
        return laCategories;

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
                    mViewModel.SaveError(TAG, e.getMessage());
                }

                dialogDisclosure.dismiss();

            }

            @Override
            public void onDecline() {
                Toast.makeText(Activity_SSDD_Category.this, "Please allow missing permissions to continue", Toast.LENGTH_LONG).show();
                dialogDisclosure.dismiss();
            }
        });
        dialogDisclosure.setMessage("Guanzon Circle requires camera and storage permissions to take SSDD images when the app is in use.");
        dialogDisclosure.show();
    }

    private void DownloadData(){

        try {

            //download evaluation details
            mViewModel.DownloadEvaluationDetails(getIntent().getStringExtra("transnox"), new VMSSDEvaluation.OnDownloadCallback() {
                @Override
                public void OnLoad(String fsTitlexx, String fsMessage) {
                    poDialog.initDialog(fsTitlexx, fsMessage, false);
                    poDialog.show();
                }

                @Override
                public void OnSuccess() {
                    Toast.makeText(Activity_SSDD_Category.this, "Successfully downloaded details", Toast.LENGTH_SHORT).show();
                    poDialog.dismiss();
                }

                @Override
                public void OnFailed(String fsMessage) {

                    InitMessage(0, R.drawable.baseline_error_24, fsMessage, "Okay", "", new onMessageButton() {
                        @Override
                        public void onPositive() {
                            finish();
                        }

                        @Override
                        public void onNegative() {}
                    });
                }
            });

        }catch (Exception e){
            mViewModel.SaveError(TAG, e.getMessage());
        }

    }

    private void InitActivity(){

        try {

            if (!getIntent().hasExtra("transnox")) {

                InitMessage(0, R.drawable.baseline_error_24, "No transaction number has been detected", "Okay", "", new onMessageButton() {
                    @Override
                    public void onPositive() { finish(); }

                    @Override
                    public void onNegative() {}
                });
            }else if(!getIntent().hasExtra("deptid")){

                InitMessage(0, R.drawable.baseline_error_24, "No department id has been detected", "Okay", "", new onMessageButton() {
                    @Override
                    public void onPositive() { finish(); }

                    @Override
                    public void onNegative() {}
                });
            }else {

                //initialize data
                InitObservers();

                //do not continue if master transaction is not set after data observation
                if (loMaster == null){
                    InitMessage(0, R.drawable.baseline_error_24, "Master transaction is not initialized", "Okay", "", new onMessageButton() {
                        @Override
                        public void onPositive() {
                            finish();
                        }

                        @Override
                        public void onNegative() {}
                    });
                    return;
                }

                //initialize adapter
                InitAdapter();

                //re download details, if list is not initialized and empty
                if (laDetails == null || laDetails.size() < 1){

                    //count details
                    if (mViewModel.CountDetails(getIntent().getStringExtra("transnox")) < 1){

                        //ask user to download details, if not found
                        InitMessage(1, R.drawable.baseline_error_24, "No details found for this transaction. Download details?", "Yes", "No", new onMessageButton() {
                            @Override
                            public void onPositive() {
                                DownloadData();
                            }

                            @Override
                            public void onNegative() {
                                finish();
                            }
                        });

                    }

                }
            }


            //initialize other methods
            InitListener();
            InitCameraLauncher();

        }catch (Exception e){
            mViewModel.SaveError(TAG, e.getMessage());
        }

    }

    private void InitObservers(){

        //initliaze master
        mViewModel.GetMaster(getIntent().getStringExtra("transnox"), getIntent().getStringExtra("deptid")).observe(Activity_SSDD_Category.this, new Observer<ESSDDMaster>() {
            @Override
            public void onChanged(ESSDDMaster essddMaster) {

                //reset to null, signal that observation has been triggered after default value has been set
                loMaster = null;

                if (essddMaster == null){
                    return;
                }

                loMaster = essddMaster;

                if (essddMaster.getcTranStat().equals("3")){
                    btn_submit.setEnabled(false);
                }else {
                    btn_submit.setEnabled(true);
                }

                //set department name
                mtv_dept.setText(mViewModel.GetDepartment(loMaster.getsDeptIDxx()).getsDescript());

                //do not allow modifications, if evaluation is already posted
                if (loMaster.getcTranStat().equals("3")){
                    rcv_adapter.setEnabled(false);
                    btn_submit.setEnabled(false);
                    return;
                }
                rcv_adapter.setEnabled(true);
                btn_submit.setEnabled(true);
            }
        });

        //initialize detail
        mViewModel.GetDetail(getIntent().getStringExtra("transnox")).observe(Activity_SSDD_Category.this, new Observer<List<ESSDDetail>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(List<ESSDDetail> essdDetails) {

                //reset to null, signal that observation has been triggered after default value has been set
                laDetails = null;

                if (essdDetails == null){
                    return;
                }

                //initialize list, and update the list
                laDetails= essdDetails;

                //check if adapter is initialized
                if (loAdapter == null){
                    return;
                }

                //initialize master details
                double ldbl_totalRating = 0.0;
                int ntotalEvaluated = 0;
                for (ESSDDetail loDetal : laDetails){

                    if (loDetal.getdEvaluate() != null && !loDetal.getdEvaluate().isEmpty()){
                        ldbl_totalRating += Double.parseDouble(loDetal.getnRatingxx());
                        ntotalEvaluated++;
                    }
                }

                //update adapter item list
                loAdapter.SetDataList(GetList());

                //display total
                mtv_evaluation.setText(ntotalEvaluated + " out of " + laDetails.size());
                mtv_ratings.setText(String.valueOf(ldbl_totalRating));

            }
        });
    }

    private void InitListener(){

        btn_submit.setOnClickListener(v -> SubmitEvaluation() );
    }

    @SuppressLint("NotifyDataSetChanged")
    private void InitAdapter(){

        //initialize categories
        loAdapter = new Adapter_SSDDCategories(GetList(), new Adapter_SSDDCategories.OnItemClickListener() {
            @Override
            public void OnRate(int fnPosition, Adapter_SSDDCategories.SSDD_Evaluation_Categories foDetail, double ffTotalRate) {

                try {

                    //validate evaluation date, if already evaluated, else proceed to rate
                    if (foDetail.lsEvaluated != null && !foDetail.lsEvaluated.isEmpty()){

                        //do not allow if category is already evaluated before this day
                        if (mViewModel.IsEvaluated(foDetail.lsEvaluated)){

                            InitMessage(0, R.drawable.baseline_error_24, "You are not allowed to re evaluate categories before the day", "Okay", "", new onMessageButton() {
                                @Override
                                public void onPositive() {
                                    loAdapter.SetDataList(GetList());
                                }

                                @Override
                                public void onNegative() {}
                            });
                            return;
                        }

                        //confirm rating, if result is zero
                        if (ffTotalRate <= 0.0){

                            InitMessage(1, R.drawable.ic_baseline_confirmation_pin_24, "You are rating this category to zero. Continue?", "Yes", "No", new onMessageButton() {
                                @Override
                                public void onPositive() {
                                    RateEvaluation(foDetail.sCategryID, String.valueOf(ffTotalRate), foDetail.lsRemarks);
                                }

                                @Override
                                public void onNegative() {}
                            });
                            return;
                        }

                    }
                    RateEvaluation(foDetail.sCategryID, String.valueOf(ffTotalRate), foDetail.lsRemarks);

                }catch (Exception e){
                    mViewModel.SaveError(TAG, e.getMessage());
                }
            }

            @Override
            public void OnRemarks(int fnPosition, Adapter_SSDDCategories.SSDD_Evaluation_Categories foDetail, String fsRemarks) {

                try {

                    //validate evaluation date, if already evaluated, else proceed to rate
                    if (foDetail.lsEvaluated != null && !foDetail.lsEvaluated.isEmpty()){

                        //do not allow if category is already evaluated before this day
                        if (mViewModel.IsEvaluated(foDetail.lsEvaluated)){

                            InitMessage(0, R.drawable.baseline_error_24, "You are not allowed to re evaluate categories before the day", "Okay", "", new onMessageButton() {
                                @Override
                                public void onPositive() {
                                    loAdapter.SetDataList(GetList());
                                }

                                @Override
                                public void onNegative() {}
                            });
                            return;
                        }

                    }
                    RateEvaluation(foDetail.sCategryID, String.valueOf(foDetail.ldbl_rating), fsRemarks);

                }catch (Exception e){
                    mViewModel.SaveError(TAG, e.getMessage());
                }
            }

            @Override
            public void OnCamera() {

                //check permission
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                    OpenPermission();
                    return;
                }

                //initliaze camera
                mViewModel.InitCamera(new OnInitializeCameraCallback() {
                    @Override
                    public void OnInit() {
                        poDialog.initDialog("Camera Launch", "Initializing camera. Please wait . .", false);
                        poDialog.show();
                    }

                    @Override
                    public void OnSuccess(Intent intent, String[] args) {
                        poDialog.dismiss();

                        intent.putExtra("sFilePath", args[0]); //pass file path
                        intent.putExtra("sFilename", args[1]); //pass file name

                        //start camera intent
                        poCamera.launch(intent);
                    }

                    @Override
                    public void OnFailed(String message, Intent intent, String[] args) {
                        poDialog.dismiss();

                        InitMessage(0, R.drawable.baseline_error_24, message, "Okay", "", new onMessageButton() {
                            @Override
                            public void onPositive() {}

                            @Override
                            public void onNegative() {}
                        });
                    }
                });

            }

            @Override
            public void OnDetails(String fsCategoryID) {

                //proceed to category details
                Intent loIntent = new Intent(Activity_SSDD_Category.this, Activity_SSDD_Category_Details.class);
                loIntent.putExtra("transnox", getIntent().getStringExtra("transnox"));
                loIntent.putExtra("categoryid", fsCategoryID);
                startActivity(loIntent);

            }
        });

        rcv_adapter.setAdapter(loAdapter);
        rcv_adapter.setLayoutManager(new LinearLayoutManager(Activity_SSDD_Category.this, LinearLayoutManager.VERTICAL, false));
    }

    private void InitCameraLauncher(){

        poCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {

                if (o.getResultCode() == RESULT_OK){

                    Intent camIntent = o.getData();

                    if (!camIntent.hasExtra("sFilePath")){
                        Toast.makeText(Activity_SSDD_Category.this, "File path not found!", Toast.LENGTH_SHORT);
                        return;
                    }else if (!camIntent.hasExtra("sFileName")){
                        Toast.makeText(Activity_SSDD_Category.this, "File name not found!", Toast.LENGTH_SHORT);
                        return;
                    }
                    String lsFilePath = camIntent.getStringExtra("sFilePath");
                    String lsFileName = camIntent.getStringExtra("sFileName");

                    //save image details
                    ESSDDImages loImage = new ESSDDImages();
                    loImage.setsReferNox(getIntent().getStringExtra("transnox"));
                    loImage.setsImagePth(lsFilePath);
                    loImage.setsImageNme(lsFileName);
                    loImage.setsCategrID("");
                    loImage.setcImgeStat("");
                    loImage.setdImgeDate("");
                    loImage.setsMD5Hashx("");
                    loImage.setsScanndID("");


                    mViewModel.SaveImage(loImage);
                }
            }
        });
    }

    private void RateEvaluation(String fsCatgrID, String fsRating, String fsRemarks){

        mViewModel.Rate(getIntent().getStringExtra("transnox"), fsCatgrID, String.valueOf(fsRating), fsRemarks, mViewModel.GetDateToday());
    }

    private void SubmitEvaluation(){

        try {

            if (loMaster == null){

                InitMessage(1, R.drawable.baseline_error_24, "Master is empty", "Okay", "", new onMessageButton() {
                    @Override
                    public void onPositive() {}

                    @Override
                    public void onNegative() {}
                });
                return;
            }else if (laDetails == null || laDetails.size() < 1){

                InitMessage(1, R.drawable.baseline_error_24, "Details is empty", "Okay", "", new onMessageButton() {
                    @Override
                    public void onPositive() {}

                    @Override
                    public void onNegative() {}
                });
                return;
            }

            mViewModel.SubmitEvaluation(loMaster, laDetails, new VMSSDEvaluation.OnSubmitCallback() {
                @Override
                public void OnLoad(String fsTitlexx, String fsMessage) {
                    poDialog.initDialog(fsTitlexx, fsMessage, false);
                    poDialog.show();
                }

                @Override
                public void OnSuccess(String fsTransNox) {
                    poDialog.dismiss();
                    InitMessage(0, R.drawable.baseline_message_24, "Evaluation submitted successfully", "Okay", "", new onMessageButton() {
                        @Override
                        public void onPositive() {

                            int ntotalEvaluated = 0;
                            for (ESSDDetail loDetail: laDetails) {

                                if (loDetail.getdEvaluate() != null && !loDetail.getdEvaluate().trim().isEmpty()){
                                    ntotalEvaluated++;
                                }
                            }

                            //if all are evaluated and status is not posted '3', close evaluation status '1'
                            if (ntotalEvaluated == laDetails.size() && !loMaster.getcTranStat().equals("3")){
                                mViewModel.UpdateMasterStatus("1", loMaster.getsTransNox());
                            }
                            finish();
                        }

                        @Override
                        public void onNegative() {}
                    });
                }

                @Override
                public void OnFailed(String fsMessage) {

                    poDialog.dismiss();
                    InitMessage(0, R.drawable.baseline_error_24, fsMessage, "Okay", "", new onMessageButton() {
                        @Override
                        public void onPositive() {}

                        @Override
                        public void onNegative() {}
                    });
                }
            });

        }catch (Exception e){
            mViewModel.SaveError(TAG, e.getMessage());
        }
    }

    private void InitMessage(int messageType, int statusIcon, String message, String posText, String negText, onMessageButton callback){

        poMessage.initDialog();
        poMessage.setTitle("SSDD Evaluation");
        poMessage.setIcon(statusIcon);
        poMessage.setMessage(message);

        poMessage.setPositiveButton(posText, (view, dialog) -> {
            dialog.dismiss();
            callback.onPositive();
        });

        if (messageType == 1){
            poMessage.setNegativeButton(negText, (view, dialog) -> {
                dialog.dismiss();
                callback.onNegative();

            });
        }

        poMessage.show();
    }
}