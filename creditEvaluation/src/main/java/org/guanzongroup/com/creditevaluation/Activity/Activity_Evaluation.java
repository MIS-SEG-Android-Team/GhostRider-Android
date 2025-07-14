package org.guanzongroup.com.creditevaluation.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.guanzongroup.com.creditevaluation.Adapter.Adapter_CIEvaluation_Headers;
import org.guanzongroup.com.creditevaluation.Adapter.onEvaluate;
import org.guanzongroup.com.creditevaluation.Adapter.onSelectResultListener;
import org.guanzongroup.com.creditevaluation.Adapter.onValidateListener;
import org.guanzongroup.com.creditevaluation.Core.FindingsParser;
import org.guanzongroup.com.creditevaluation.Dialog.DialogCIReason;
import org.guanzongroup.com.creditevaluation.R;
import org.guanzongroup.com.creditevaluation.ViewModel.OnInitializeCameraCallback;
import org.guanzongroup.com.creditevaluation.ViewModel.VMEvaluation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditOnlineApplicationCI;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.CreditInvestigator.pojo.BarangayRecord;
import org.rmj.g3appdriver.GCircle.Apps.CreditInvestigator.pojo.CIImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Activity_Evaluation extends AppCompatActivity {

    private static final String TAG = Activity_Evaluation.class.getSimpleName();
    private VMEvaluation mViewModel;
    private TextInputEditText txtRecordRemarks,txtPersonnel, txtPosition, txtMobileNo;
    private TextInputEditText txtNeighbor1, txtNeighbor2, txtNeighbor3;
    private TextInputLayout tilRecordRemarks;
    private RadioGroup rgHasRecord;
    private MaterialTextView lblTransNox,lblClientName,lblDTransact, lblBranch;
    private MaterialButton btnSaveNeighbor1,

    btnSaveNeighbor2,btnSaveNeighbor3;
    private MaterialButton btnSave,btnUpload, btnSaveBrgyRcrd;

    private BarangayRecord poBrgy;
    private LoadDialog poDialogx;
    private MessageBox poMessage;

    private CIImage poImage;

    private String psTransNo;
    private List<String> poList = new ArrayList<>();

    private onEvaluate poAdLstnr;

    private final ActivityResultLauncher<Intent> poCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        try{
            if(result.getResultCode() == RESULT_OK) {
                mViewModel.SaveAddressImage(poImage, new VMEvaluation.OnSaveAddressResult() {
                    @Override
                    public void OnSuccess(String args, String args1, String args2) {
                        SaveCIResult(poList, args, args1, args2);
                        poAdLstnr.OnSetResult(true);
                    }

                    @Override
                    public void OnError(String message) {
                        poAdLstnr.OnSetResult(false);
                        Toast.makeText(Activity_Evaluation.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                poAdLstnr.OnSetResult(false);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    });

    private final ActivityResultLauncher<String[]> poRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Boolean fineLoct = result.getOrDefault(
                        Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseL = result.getOrDefault(
                        Manifest.permission.ACCESS_COARSE_LOCATION, false);
                Boolean camerax = result.getOrDefault(
                        Manifest.permission.CAMERA, false);
                if(Boolean.FALSE.equals(camerax)){
                    Toast.makeText(Activity_Evaluation.this, "Please allow camera permission to proceed.", Toast.LENGTH_SHORT).show();
                } else if(Boolean.FALSE.equals(fineLoct)){
                    Toast.makeText(Activity_Evaluation.this, "Please allow permission to get device location.", Toast.LENGTH_SHORT).show();
                } else if(Boolean.FALSE.equals(coarseL)){
                    Toast.makeText(Activity_Evaluation.this, "Please allow permission to get device location.", Toast.LENGTH_SHORT).show();
                } else {
                    showDialogImg();
                }
            } else {
                if(ActivityCompat.checkSelfPermission(Activity_Evaluation.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(Activity_Evaluation.this, "Please allow camera permission to proceed.", Toast.LENGTH_SHORT).show();
                } else if(ActivityCompat.checkSelfPermission(Activity_Evaluation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(Activity_Evaluation.this, "Please allow permission to get device location.", Toast.LENGTH_SHORT).show();
                } else if(ActivityCompat.checkSelfPermission(Activity_Evaluation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(Activity_Evaluation.this, "Please allow permission to get device location.", Toast.LENGTH_SHORT).show();
                } else {
                    showDialogImg();
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_evaluation);

        mViewModel = new ViewModelProvider(this).get(VMEvaluation.class);

        initWidgets();
        initIntentData();

        psTransNo = getIntent().getStringExtra("transno");
        mViewModel.GetApplicationDetail(psTransNo).observe(Activity_Evaluation.this, ci -> {
            try{
                poImage.setTransNox(ci.getTransNox());
                poBrgy.setsTransNox(ci.getTransNox());
                setupEvaluationAdapter(ci);

                if(ci.getPrsnBrgy() != null){
                    txtPersonnel.setText(ci.getPrsnBrgy());
                    poBrgy.setsBrgyPrsn(ci.getPrsnBrgy());
                }
                if(ci.getPrsnPstn() != null){
                    txtPosition.setText(ci.getPrsnPstn());
                    poBrgy.setsBrgyPstn(ci.getPrsnPstn());
                }

                if(ci.getPrsnNmbr() != null){
                    txtMobileNo.setText(ci.getPrsnNmbr());
                    poBrgy.setsPrsnNmbr(ci.getPrsnNmbr());
                }

                if(ci.getPrsnBrgy() != null &&
                        ci.getPrsnPstn() != null &&
                        ci.getPrsnNmbr() != null) {
                    findViewById(R.id.rb_ci_noRecord).setEnabled(false);
                    findViewById(R.id.rb_ci_withRecord).setEnabled(false);
                    txtRecordRemarks.setEnabled(false);
                    txtPersonnel.setEnabled(false);
                    txtPosition.setEnabled(false);
                    txtMobileNo.setEnabled(false);
                    findViewById(R.id.btnSaveBrgyRecords).setVisibility(View.GONE);
                    findViewById(R.id.btnEditBrgyRecords).setVisibility(View.VISIBLE);
                }

                if(ci.getNeighBr1() != null){
                    txtNeighbor1.setText(ci.getNeighBr1());
                    txtNeighbor1.setEnabled(false);
                    findViewById(R.id.btnSaveNeighbor1).setVisibility(View.GONE);
                    findViewById(R.id.btnEditNeighbor1).setVisibility(View.VISIBLE);
                }

                if(ci.getNeighBr2() != null){
                    txtNeighbor2.setText(ci.getNeighBr2());
                    txtNeighbor2.setEnabled(false);
                    findViewById(R.id.btnSaveNeighbor2).setVisibility(View.GONE);
                    findViewById(R.id.btnEditNeighbor2).setVisibility(View.VISIBLE);
                }

                if(ci.getNeighBr3() != null){
                    txtNeighbor3.setText(ci.getNeighBr3());
                    txtNeighbor3.setEnabled(false);
                    findViewById(R.id.btnSaveNeighbor3).setVisibility(View.GONE);
                    findViewById(R.id.btnEditNeighbor3).setVisibility(View.VISIBLE);
                }

                if (ci.getHasRecrd() != null) {
                    poBrgy.setcHasRecrd(ci.getHasRecrd());
                    if(ci.getHasRecrd().equalsIgnoreCase("0")){
                        rgHasRecord.check(R.id.rb_ci_noRecord);
                    }else{
                        rgHasRecord.check(R.id.rb_ci_withRecord);
                    }
                }

                if (ci.getRecrdRem() != null) {
                    poBrgy.setsRecrdRem(ci.getRecrdRem());
                    if(ci.getHasRecrd().equalsIgnoreCase("1")){
                        tilRecordRemarks.setVisibility(View.VISIBLE);
                        txtRecordRemarks.setText(ci.getRecrdRem());
                    }
                }

                if(ci.getUploaded().equalsIgnoreCase("1")){
                    findViewById(R.id.linear_approve).setVisibility(View.VISIBLE);
                    findViewById(R.id.linear_upload).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.linear_approve).setVisibility(View.GONE);
                    findViewById(R.id.linear_upload).setVisibility(View.VISIBLE);
                }

                btnSaveBrgyRcrd.setOnClickListener(v -> {
                    poBrgy.setsRecrdRem(Objects.requireNonNull(txtRecordRemarks.getText()).toString());
                    poBrgy.setsBrgyPrsn(Objects.requireNonNull(txtPersonnel.getText()).toString());
                    poBrgy.setsBrgyPstn(Objects.requireNonNull(txtPosition.getText()).toString());
                    poBrgy.setsPrsnNmbr(Objects.requireNonNull(txtMobileNo.getText()).toString());
                    mViewModel.SaveBarangayRecord(poBrgy, new VMEvaluation.OnUpdateOtherDetail() {
                        @Override
                        public void OnSuccess() {
                            Toast.makeText(Activity_Evaluation.this, "Record saved!", Toast.LENGTH_SHORT).show();
                            findViewById(R.id.rb_ci_noRecord).setEnabled(false);
                            findViewById(R.id.rb_ci_withRecord).setEnabled(false);
                            txtRecordRemarks.setEnabled(false);
                            txtPersonnel.setEnabled(false);
                            txtPosition.setEnabled(false);
                            txtMobileNo.setEnabled(false);
                            findViewById(R.id.btnSaveBrgyRecords).setVisibility(View.GONE);
                            findViewById(R.id.btnEditBrgyRecords).setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void OnError(String message) {
                            Toast.makeText(Activity_Evaluation.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                btnSaveNeighbor1.setOnClickListener(v -> {
                    String lsVal = txtNeighbor1.getText().toString();
                    mViewModel.UpdateOtherDetail(psTransNo, "n1", lsVal, new VMEvaluation.OnUpdateOtherDetail() {
                        @Override
                        public void OnSuccess() {
                            Toast.makeText(Activity_Evaluation.this, "Record saved", Toast.LENGTH_SHORT).show();
                            txtNeighbor1.setEnabled(false);
                            findViewById(R.id.btnSaveNeighbor1).setVisibility(View.GONE);
                            findViewById(R.id.btnEditNeighbor1).setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void OnError(String message) {
                            Toast.makeText(Activity_Evaluation.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                btnSaveNeighbor2.setOnClickListener(v -> {
                    String lsVal = txtNeighbor2.getText().toString();
                    mViewModel.UpdateOtherDetail(psTransNo, "n2", lsVal, new VMEvaluation.OnUpdateOtherDetail() {
                        @Override
                        public void OnSuccess() {
                            Toast.makeText(Activity_Evaluation.this, "Record saved", Toast.LENGTH_SHORT).show();
                            txtNeighbor2.setEnabled(false);
                            findViewById(R.id.btnSaveNeighbor2).setVisibility(View.GONE);
                            findViewById(R.id.btnEditNeighbor2).setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void OnError(String message) {
                            Toast.makeText(Activity_Evaluation.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                btnSaveNeighbor3.setOnClickListener(v -> {
                    String lsVal = txtNeighbor3.getText().toString();
                    mViewModel.UpdateOtherDetail(psTransNo, "n3", lsVal, new VMEvaluation.OnUpdateOtherDetail() {
                        @Override
                        public void OnSuccess() {
                            Toast.makeText(Activity_Evaluation.this, "Record saved", Toast.LENGTH_SHORT).show();
                            txtNeighbor3.setEnabled(false);
                            findViewById(R.id.btnSaveNeighbor3).setVisibility(View.GONE);
                            findViewById(R.id.btnEditNeighbor3).setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void OnError(String message) {
                            Toast.makeText(Activity_Evaluation.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                btnUpload.setOnClickListener(v -> mViewModel.UploadResult(psTransNo, new VMEvaluation.OnUploadResultListener() {
                    @Override
                    public void OnUpload() {
                        poDialogx.initDialog("CI Evaluation", "Uploading ci result. Please wait...", false);
                        poDialogx.show();
                    }

                    @Override
                    public void OnSuccess() {
                        poDialogx.dismiss();
                        poMessage.initDialog();
                        poMessage.setIcon(R.drawable.baseline_message_24);
                        poMessage.setTitle("CI Evaluation");
                        poMessage.setMessage("CI Result uploaded successfully.");
                        poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                        poMessage.show();
                    }

                    @Override
                    public void OnFailed(String message) {
                        poDialogx.dismiss();
                        poMessage.initDialog();
                        poMessage.setIcon(R.drawable.baseline_error_24);
                        poMessage.setTitle("CI Evaluation List");
                        poMessage.setMessage(message);
                        poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                        poMessage.show();
                    }
                }));

                if(ci.getTransfer().equalsIgnoreCase("1")){
                    findViewById(R.id.btnEditBrgyRecords).setVisibility(View.GONE);
                    findViewById(R.id.btnEditNeighbor1).setVisibility(View.GONE);
                    findViewById(R.id.btnEditNeighbor2).setVisibility(View.GONE);
                    findViewById(R.id.btnEditNeighbor3).setVisibility(View.GONE);
                    btnSave.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        rgHasRecord.setOnCheckedChangeListener(new ONCIHasRecord(rgHasRecord,mViewModel));

        btnSave.setOnClickListener(v ->  {
            DialogCIReason loDialog = new DialogCIReason(Activity_Evaluation.this);
            loDialog.initDialogCIReason((dialog, transtat, reason) -> {
                dialog.dismiss();
                mViewModel.SaveRecommendation(psTransNo, transtat, reason, new VMEvaluation.OnPostRecommendationCallback() {
                    @Override
                    public void OnPost() {
                        poDialogx.initDialog("CI Evaluation", "Saving recommendation. Please wait...", false);
                        poDialogx.show();
                    }

                    @Override
                    public void OnSuccess() {
                        poDialogx.dismiss();
                        poMessage.initDialog();
                        poMessage.setIcon(R.drawable.baseline_message_24);
                        poMessage.setTitle("CI Evaluation");
                        poMessage.setMessage("Your recommendation has been saved to server.");
                        poMessage.setPositiveButton("Okay", (view, dialog) -> {
                            dialog.dismiss();
                            finish();
                        });
                        poMessage.show();
                    }

                    @Override
                    public void OnFailed(String message) {
                        poDialogx.dismiss();
                        poMessage.initDialog();
                        poMessage.setIcon(R.drawable.baseline_error_24);
                        poMessage.setTitle("CI Evaluation List");
                        poMessage.setMessage(message);
                        poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                        poMessage.show();
                    }
                });
            });
            loDialog.show();
        });

        findViewById(R.id.btnEditBrgyRecords).setOnClickListener(v -> {
            findViewById(R.id.rb_ci_noRecord).setEnabled(true);
            findViewById(R.id.rb_ci_withRecord).setEnabled(true);
            txtRecordRemarks.setEnabled(true);
            txtPersonnel.setEnabled(true);
            txtPosition.setEnabled(true);
            txtMobileNo.setEnabled(true);
            findViewById(R.id.btnSaveBrgyRecords).setVisibility(View.VISIBLE);
            findViewById(R.id.btnEditBrgyRecords).setVisibility(View.GONE);
        });
        findViewById(R.id.btnEditNeighbor1).setOnClickListener(v -> {
            txtNeighbor1.setEnabled(true);
            findViewById(R.id.btnEditNeighbor1).setVisibility(View.GONE);
            findViewById(R.id.btnSaveNeighbor1).setVisibility(View.VISIBLE);
        });
        findViewById(R.id.btnEditNeighbor2).setOnClickListener(v -> {
            txtNeighbor2.setEnabled(true);
            findViewById(R.id.btnEditNeighbor2).setVisibility(View.GONE);
            findViewById(R.id.btnSaveNeighbor2).setVisibility(View.VISIBLE);
        });
        findViewById(R.id.btnEditNeighbor3).setOnClickListener(v -> {
            txtNeighbor3.setEnabled(true);
            findViewById(R.id.btnEditNeighbor3).setVisibility(View.GONE);
            findViewById(R.id.btnSaveNeighbor3).setVisibility(View.VISIBLE);
        });
    }
    private void initIntentData(){
        lblTransNox.setText("Transaction No.: " + getIntent().getStringExtra("transno"));
        lblClientName.setText(getIntent().getStringExtra("ClientNm"));
        lblDTransact.setText(getIntent().getStringExtra("dTransact"));
        lblBranch.setText(getIntent().getStringExtra("Branch"));
    }
    private void initWidgets(){
        MaterialToolbar toolbar = findViewById(R.id.toolbar_ci_evaluation);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        poImage = new CIImage();
        poBrgy = new BarangayRecord();

        poDialogx = new LoadDialog(Activity_Evaluation.this);
        poMessage = new MessageBox(Activity_Evaluation.this);
        txtPersonnel = findViewById(R.id.tie_ci_asstPersonel);
        txtPosition = findViewById(R.id.tie_ci_asstPosition);
        txtMobileNo = findViewById(R.id.tie_ci_asstPhoneNumber);
        txtNeighbor1 = findViewById(R.id.tie_ci_neighborName1);
        txtNeighbor2 = findViewById(R.id.tie_ci_neighborName2);
        txtNeighbor3 = findViewById(R.id.tie_ci_neighborName3);
        tilRecordRemarks = findViewById(R.id.til_ci_record);
        txtRecordRemarks = findViewById(R.id.tie_ci_record);
        rgHasRecord = findViewById(R.id.rg_ci_brgyRecord);
        lblTransNox = findViewById(R.id.lbl_ci_transNox);
        lblClientName = findViewById(R.id.lbl_ci_applicantName);
        lblDTransact = findViewById(R.id.lbl_ci_applicationDate);
        lblBranch = findViewById(R.id.lbl_ci_branch);
        btnSaveNeighbor1 = findViewById(R.id.btnSaveNeighbor1);
        btnSaveNeighbor2 = findViewById(R.id.btnSaveNeighbor2);
        btnSaveNeighbor3 = findViewById(R.id.btnSaveNeighbor3);
        btnSaveBrgyRcrd = findViewById(R.id.btnSaveBrgyRecords);

        btnUpload = findViewById(R.id.btnUpload);
        btnSave = findViewById(R.id.btnSave);

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class ONCIHasRecord implements RadioGroup.OnCheckedChangeListener{

        View rbView;
        VMEvaluation mViewModel;
        ONCIHasRecord(View view, VMEvaluation viewModel)
        {
            this.rbView = view;
            this.mViewModel = viewModel;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(rbView.getId() == R.id.rg_ci_brgyRecord){
                if(checkedId == R.id.rb_ci_noRecord) {
                    poBrgy.setcHasRecrd("0");
                    txtRecordRemarks.setText("");
                    tilRecordRemarks.setVisibility(View.GONE);
                } else {
                    poBrgy.setcHasRecrd("1");
                    tilRecordRemarks.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void validateCameraPermissions(){
        if(ActivityCompat.checkSelfPermission(Activity_Evaluation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(Activity_Evaluation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(Activity_Evaluation.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            poRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA});
        } else {
            showDialogImg();
        }
    }

    public void showDialogImg(){
        poMessage.initDialog();
        poMessage.setIcon(R.drawable.baseline_message_24);
        poMessage.setTitle("Residence Info");
        poMessage.setMessage("Please take a picture of the applicant residence area. \n");
        poMessage.setPositiveButton("Okay", (view, dialog) -> {
            dialog.dismiss();
            mViewModel.InitCameraLaunch(Activity_Evaluation.this, psTransNo, new OnInitializeCameraCallback() {
                @Override
                public void OnInit() {
                    poDialogx.initDialog("CI Evaluation", "Initializing camera. Please wait...", false);
                    poDialogx.show();
                }

                @Override
                public void OnSuccess(Intent intent, String[] args) {
                    poDialogx.dismiss();
                    poImage.setFilePath(args[0]);
                    poImage.setFileName(args[1]);
                    poImage.setLatitude(args[2]);
                    poImage.setLongtude(args[3]);
                    poCamera.launch(intent);
                }

                @Override
                public void OnFailed(String message, Intent intent, String[] args) {
                    poDialogx.dismiss();
                    poMessage.initDialog();
                    poMessage.setIcon(R.drawable.baseline_contact_support_24);
                    poMessage.setTitle("CI Evaluation");
                    poMessage.setMessage(message + "\n Proceed taking picture?");
                    poMessage.setPositiveButton("Continue", (view, dialog) -> {
                        dialog.dismiss();
                        poImage.setFilePath(args[0]);
                        poImage.setFileName(args[1]);
                        poImage.setLatitude(args[2]);
                        poImage.setLongtude(args[3]);
                        poCamera.launch(intent);
                    });
                    poMessage.setNegativeButton("Cancel", (view1, dialog) -> dialog.dismiss());
                    poMessage.show();
                }
            });
        });
        poMessage.show();
    }

    private void ValidateTagging(List<String> foList, String fsKey, onValidateListener listener){
        mViewModel.ValidateTagging(psTransNo, fsKey, foList, new VMEvaluation.OnValidateTaggingResult() {
            @Override
            public void OnValid() {
                listener.OnValid(true);
            }

            @Override
            public void OnInvalid(String message) {
                listener.OnValid(false);
                Toast.makeText(Activity_Evaluation.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SaveCIResult(List<String> foList, String fsPar, String fsKey, String fsRes){
        mViewModel.SaveCIResult(psTransNo, fsPar, fsKey, fsRes, foList, new VMEvaluation.OnSaveCIResultListener() {
            @Override
            public void OnSuccess() {
                Toast.makeText(Activity_Evaluation.this, "Record save!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnError(String message) {
                poAdLstnr.OnSetResult(false);
                Toast.makeText(Activity_Evaluation.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupEvaluationAdapter(ECreditOnlineApplicationCI ci){
        try {
            JSONArray laEval = new JSONArray();
            JSONObject loDetail = FindingsParser.scanForEvaluation(ci.getAddressx(), ci.getAddrFndg());

            if(poList.size() == 0 && ci.getTransfer().equalsIgnoreCase("1")) {
                if (loDetail.getJSONArray("detail").length() > 0) {
                    laEval.put(FindingsParser.scanForEvaluation(ci.getAddressx(), ci.getAddrFndg()));
                    poList.addAll(FindingsParser.ScanForEvaluationTransferredApplication(ci.getAddressx(), ci.getAddrFndg()));
                }

                loDetail = FindingsParser.scanForEvaluation(ci.getIncomexx(), ci.getIncmFndg());
                if (loDetail.getJSONArray("detail").length() > 0) {
                    laEval.put(FindingsParser.scanForEvaluation(ci.getIncomexx(), ci.getIncmFndg()));
                    poList.addAll(FindingsParser.ScanForEvaluationTransferredApplication(ci.getIncomexx(), ci.getIncmFndg()));
                }

                loDetail = FindingsParser.scanForEvaluation(ci.getAssetsxx(), ci.getAsstFndg());
                if (loDetail.getJSONArray("detail").length() > 0) {
                    laEval.put(FindingsParser.scanForEvaluation(ci.getAssetsxx(), ci.getAsstFndg()));
                    poList.addAll(FindingsParser.ScanForEvaluationTransferredApplication(ci.getAssetsxx(), ci.getAsstFndg()));
                }
            } else {
                if (loDetail.getJSONArray("detail").length() > 0) {
                    laEval.put(FindingsParser.scanForEvaluation(ci.getAddressx(), ci.getAddrFndg()));
                }

                loDetail = FindingsParser.scanForEvaluation(ci.getIncomexx(), ci.getIncmFndg());
                if (loDetail.getJSONArray("detail").length() > 0) {
                    laEval.put(FindingsParser.scanForEvaluation(ci.getIncomexx(), ci.getIncmFndg()));
                }

                loDetail = FindingsParser.scanForEvaluation(ci.getAssetsxx(), ci.getAsstFndg());
                if (loDetail.getJSONArray("detail").length() > 0) {
                    laEval.put(FindingsParser.scanForEvaluation(ci.getAssetsxx(), ci.getAsstFndg()));
                }
            }
            Log.d(TAG, laEval.toString());

            mViewModel.GetOccupationList().observe(Activity_Evaluation.this, eOccupationInfos -> {
                Adapter_CIEvaluation_Headers loAdapter = new Adapter_CIEvaluation_Headers(
                        Activity_Evaluation.this,
                        laEval,
                        eOccupationInfos,
                        false,
                        new onSelectResultListener() {
                            @Override
                            public void OnUpdate(String fsKey, onValidateListener listener) {
                                ValidateTagging(poList, fsKey, listener);
                            }

                            @Override
                    public void OnCorrect(String fsPar, String fsKey, String fsRes, onEvaluate listener) {
                        if(fsPar.equalsIgnoreCase("primary_address")){
                            poAdLstnr = listener;
                            poImage.setsParentxx("primary_address");
                            poImage.setsKeyNamex(fsKey);
                            poImage.setcResultxx(fsRes);
                            validateCameraPermissions();
                        } else if(fsPar.equalsIgnoreCase("present_address")){
                            poAdLstnr = listener;
                            poImage.setsParentxx("present_address");
                            poImage.setsKeyNamex(fsKey);
                            poImage.setcResultxx(fsRes);
                            validateCameraPermissions();
                        } else {
                            SaveCIResult(poList, fsPar, fsKey, fsRes);
                            listener.OnSetResult(true);
                        }
                    }

                    @Override
                    public void OnIncorrect(String fsPar, String fsKey, String fsRes, onEvaluate listener) {
                        if(fsPar.equalsIgnoreCase("primary_address")){
                            poAdLstnr = listener;
                            poImage.setsParentxx("primary_address");
                            poImage.setsKeyNamex(fsKey);
                            poImage.setcResultxx(fsRes);
                            validateCameraPermissions();
                        } else if(fsPar.equalsIgnoreCase("present_address")){
                            poAdLstnr = listener;
                            poImage.setsParentxx("present_address");
                            poImage.setsKeyNamex(fsKey);
                            poImage.setcResultxx(fsRes);
                            validateCameraPermissions();
                        } else {
                            SaveCIResult(poList, fsPar, fsKey, fsRes);
                            listener.OnSetResult(true);
                        }
                    }
                });

                RecyclerView recyclerView = findViewById(R.id.recyclerview);
                LinearLayoutManager loManager = new LinearLayoutManager(Activity_Evaluation.this);
                loManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(loManager);
                recyclerView.setAdapter(loAdapter);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}