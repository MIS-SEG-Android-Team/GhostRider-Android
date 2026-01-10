package org.rmj.guanzongroup.evaluation.Activity.SSDD;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDetail;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
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

    private VMSSDEvaluation mViewModel;
    private LoadDialog poDialog;
    private MessageBox poMessage;

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

    private void DownloadData(){

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

    }

    private void InitActivity(){

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

                InitMessage(0, R.drawable.baseline_error_24, "Could not find transaction", "Okay", "", new onMessageButton() {
                    @Override
                    public void onPositive() {
                        finish();
                    }

                    @Override
                    public void onNegative() {}
                });
                return;
            }

            //ask for user to download details, if not found
            if (laDetails == null){

                InitMessage(1, R.drawable.baseline_error_24, "No details found for this transaction. Download details?", "Yes", "No", new onMessageButton() {
                    @Override
                    public void onPositive() {
                        DownloadData();
                    }

                    @Override
                    public void onNegative() {}
                });
            }
        }

        InitListener();
        InitAdapter();

    }

    private void InitObservers(){

        //initliaze master
        mViewModel.GetMaster(getIntent().getStringExtra("transnox"), getIntent().getStringExtra("deptid")).observe(
                Activity_SSDD_Category.this, new Observer<ESSDDMaster>() {
            @Override
            public void onChanged(ESSDDMaster essddMaster) {

                loMaster= essddMaster;

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

                //initialize list, and update the list
                laDetails= essdDetails;
                loAdapter.SetDataList(GetList());

                //initialize master details
                double ldbl_totalRating = 0.0;
                int ntotalEvaluated = 0;
                for (ESSDDetail loDetal : laDetails){

                    if (loDetal.getdEvaluate() != null && !loDetal.getdEvaluate().isEmpty()){
                        ldbl_totalRating += Double.parseDouble(loDetal.getnRatingxx());
                        ntotalEvaluated++;
                    }
                }

                //update master

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
                    e.printStackTrace();
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
                    e.printStackTrace();
                }
            }

            @Override
            public void OnCamera() {

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

    private void RateEvaluation(String fsCatgrID, String fsRating, String fsRemarks){

        mViewModel.Rate(getIntent().getStringExtra("transnox"), fsCatgrID, String.valueOf(fsRating), fsRemarks, mViewModel.GetDateToday());
    }

    private void SubmitEvaluation(){

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

        mViewModel.SubmitEvaluation(loMaster, laDetails, new VMSSDEvaluation.OnDownloadCallback() {
            @Override
            public void OnLoad(String fsTitlexx, String fsMessage) {
                poDialog.initDialog(fsTitlexx, fsMessage, false);
                poDialog.show();
            }

            @Override
            public void OnSuccess() {
                poDialog.dismiss();
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