package org.rmj.guanzongroup.evaluation.Activity.SSDD;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDetail;
import org.rmj.g3appdriver.etc.DialogDisclosure;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.etc.OnInitializeCameraCallback;
import org.rmj.guanzongroup.evaluation.Adapter.SSDD.Adapter_SSDDCategories;
import org.rmj.guanzongroup.evaluation.R;
import org.rmj.guanzongroup.evaluation.ViewModel.SSDD.VMSSDEvaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private EImageInfo loImage;
    private Adapter_SSDDCategories.SSDD_Evaluation_Categories foCamDetail;

    private String lsTransNox;

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
        loImage = new EImageInfo();

        mtv_dept = findViewById(R.id.mtv_dept);
        mtv_evaluation = findViewById(R.id.mtv_evaluation);
        mtv_ratings = findViewById(R.id.mtv_ratings);
        btn_submit = findViewById(R.id.btn_submit);

        rcv_adapter = findViewById(R.id.rcv_adapter);

        InitActivity();
    }

    private String GetTransNox(){
        if (lsTransNox == null){
            return getIntent().getStringExtra("transnox");
        }
        return lsTransNox;
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
            mViewModel.DownloadEvaluationDetails(GetTransNox(), new VMSSDEvaluation.OnDownloadCallback() {
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

                //initialize observer
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
                    if (mViewModel.CountDetails(GetTransNox()) < 1){

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
        mViewModel.GetMaster(GetTransNox(), getIntent().getStringExtra("deptid")).observe(Activity_SSDD_Category.this, new Observer<ESSDDMaster>() {
            @Override
            public void onChanged(ESSDDMaster essddMaster) {

                //reset to null, signal that observation has been triggered after default value has been set
                loMaster = null;

                if (essddMaster == null){
                    return;
                }

                loMaster = essddMaster;

                if (loMaster.getcTranStat().equals("3")){
                    btn_submit.setEnabled(false);
                }else {
                    btn_submit.setEnabled(true);
                }

                //set department name
                mtv_dept.setText(mViewModel.GetDepartment(loMaster.getsDeptIDxx()).getsDescript());
            }
        });

        //initialize detail
        mViewModel.GetDetail(GetTransNox()).observe(Activity_SSDD_Category.this, new Observer<List<ESSDDetail>>() {
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

                //initialize master details
                double ldbl_totalRating = 0.0;
                int ntotalEvaluated = 0;
                for (ESSDDetail loDetal : laDetails){

                    if (loDetal.getdEvaluate() != null && !loDetal.getdEvaluate().isEmpty()){
                        ldbl_totalRating += Double.parseDouble(loDetal.getnRatingxx());
                        ntotalEvaluated++;
                    }
                }

                //check if adapter is initialized
                if (loAdapter == null){
                    return;
                }

                //update adapter item list
                loAdapter.SetDataList(GetList());

                //if all are evaluated and status is not posted '3', close evaluation status '1'
                if (ntotalEvaluated == laDetails.size()){
                    mViewModel.UpdateMasterStatus("1", GetTransNox());
                }

                //display total
                mtv_evaluation.setText(ntotalEvaluated + " out of " + laDetails.size());
                mtv_ratings.setText(String.valueOf(ldbl_totalRating));

            }
        });

        //get pending images for upload, send to database
        mViewModel.GetTransactionImagesForUpload(GetTransNox()).observe(Activity_SSDD_Category.this, new Observer<List<EImageInfo>>() {
            @Override
            public void onChanged(List<EImageInfo> eImageInfos) {

                try {

                    //do not proceed, if empty
                    if (eImageInfos == null || eImageInfos.size() < 1){
                        return;
                    }

                    //upload images
                    for (EImageInfo loImage : eImageInfos){

                        mViewModel.SubmitImage(loImage, new VMSSDEvaluation.OnSubmitCallback() {
                            @Override
                            public void OnLoad(String fsTitlexx, String fsMessage) {
                                Toast.makeText(Activity_SSDD_Category.this, "Uploading image. Please wait . .", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void OnSuccess(String fsTransnox) {
                                Toast.makeText(Activity_SSDD_Category.this, "Uploading successful", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void OnFailed(String fsMessage) {
                                Toast.makeText(Activity_SSDD_Category.this, fsMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                        Thread.sleep(1000);
                    }

                }catch (Exception e){
                    mViewModel.SaveError("SSDD Evaluation", e.getMessage());
                }
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

                    //validate evaluation date if already evaluated, else proceed to rate
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

                    //validate evaluation date if already evaluated, else proceed to rate
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
            public void OnCamera(Adapter_SSDDCategories.SSDD_Evaluation_Categories foDetail) {

                try {

                    //validate evaluation date if already evaluated, else proceed to camera
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

                    //check permission
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                        OpenPermission();
                        return;
                    }

                    //initliaze camera
                    mViewModel.InitCamera(GetTransNox(), foDetail, new OnInitializeCameraCallback() {
                        @Override
                        public void OnInit() {
                            poDialog.initDialog("Camera Launch", "Initializing camera. Please wait . .", false);
                            poDialog.show();
                        }

                        @Override
                        public void OnSuccess(Intent intent, String[] args) {
                            poDialog.dismiss();

                            loImage.setSourceNo(GetTransNox());
                            loImage.setFileLoct(args[0]); //pass file path
                            loImage.setImageNme(args[1]); //pass file name
                            loImage.setFileCode(args[2]); //pass category id
                            loImage.setLongitud(args[3]); //pass longitude
                            loImage.setLatitude(args[4]); //pass latitude

                            foCamDetail = foDetail;

                            //start camera intent
                            poCamera.launch(intent);
                        }

                        @Override
                        public void OnFailed(String message, Intent intent, String[] args) {
                            poDialog.dismiss();

                            InitMessage(0, R.drawable.baseline_error_24, message, "Okay", "", new onMessageButton() {
                                @Override
                                public void onPositive() {

                                    loImage.setSourceNo(GetTransNox());
                                    loImage.setFileLoct(args[0]); //pass file name
                                    loImage.setImageNme(args[1]); //pass file name
                                    loImage.setFileCode(args[2]); //pass category id
                                    loImage.setLongitud(args[3]); //pass longitude
                                    loImage.setLatitude(args[4]); //pass latitude

                                    foCamDetail = foDetail;

                                    //start camera intent
                                    poCamera.launch(intent);

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

            @Override
            public void OnDetails(String fsCategoryID) {

                //proceed to category details
                Intent loIntent = new Intent(Activity_SSDD_Category.this, Activity_SSDD_Category_Details.class);
                loIntent.putExtra("transnox", GetTransNox());
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

                            Log.d(TAG, "Image Longitude is " + String.valueOf(Objects.requireNonNull(exifInterface.getLatLong())[1])
                                    + " and Image Latitude is " + String.valueOf(exifInterface.getLatLong()[0]));

                            loImage.setLatitude(String.valueOf(exifInterface.getLatLong()[0]));
                            loImage.setLongitud(String.valueOf(exifInterface.getLatLong()[1]));
                        }

                        //TODO: 3. VALIDATE SAVED COORDINATES
                        if (loImage.getLongitud() == null || loImage.getLatitude() == null){
                            InitMessage(0, R.drawable.baseline_error_24, "Unable to get location coordinates. Please inform your superior for this matter.", "Okay", "", new onMessageButton() {
                                @Override
                                public void onPositive() {

                                }

                                @Override
                                public void onNegative() {

                                }
                            });
                            return;
                        }

                        if (loImage.getLongitud().isEmpty() || loImage.getLatitude().isEmpty()){
                            InitMessage(0, R.drawable.baseline_error_24, "Location coordinates is empty. Please inform your superior for this matter.", "Okay", "", new onMessageButton() {
                                @Override
                                public void onPositive() {

                                }

                                @Override
                                public void onNegative() {

                                }
                            });
                            return;
                        }

                        if (loImage.getLongitud().equalsIgnoreCase("0.00000000000") || loImage.getLatitude().equalsIgnoreCase("0.00000000000")) {
                            InitMessage(0, R.drawable.baseline_error_24, "Location coordinates is invalid. Please inform your superior for this matter.", "Okay", "", new onMessageButton() {
                                @Override
                                public void onPositive() {

                                }

                                @Override
                                public void onNegative() {

                                }
                            });
                            return;
                        }

                        //save image
                        mViewModel.SaveImage(loImage.getSourceNo(), loImage.getImageNme(), loImage.getFileLoct(), loImage.getLongitud(), loImage.getLatitude(), loImage.getFileCode());

                        //if selected detail from camera is not null, update evaluation
                        if (foCamDetail != null){
                            RateEvaluation(foCamDetail.sCategryID, String.valueOf(foCamDetail.ldbl_rating), foCamDetail.lsRemarks);
                        }

                        Toast.makeText(Activity_SSDD_Category.this, "Image saved succesfully", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    mViewModel.SaveError("SSSDD Evaluation", e.getMessage());
                }
            }
        });
    }

    private void RateEvaluation(String fsCatgrID, String fsRating, String fsRemarks){
        mViewModel.Rate(loMaster.getsTransNox(), fsCatgrID, String.valueOf(fsRating), fsRemarks, mViewModel.GetDateToday());
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
                            lsTransNox = fsTransNox;
                            InitObservers();
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