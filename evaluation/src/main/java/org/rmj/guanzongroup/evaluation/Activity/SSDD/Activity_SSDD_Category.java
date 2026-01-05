package org.rmj.guanzongroup.evaluation.Activity.SSDD;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDetail;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.evaluation.Adapter.SSDD.Adapter_SSDDCategories;
import org.rmj.guanzongroup.evaluation.R;
import org.rmj.guanzongroup.evaluation.ViewModel.SSDD.VMSSDEvaluation;

import java.util.ArrayList;
import java.util.List;

public class Activity_SSDD_Category extends AppCompatActivity {

    private VMSSDEvaluation mViewModel;
    private LoadDialog poDialog;
    private MessageBox poMessage;

    private MaterialTextView mtv_dept, mtv_evaluation, mtv_ratings;
    private RecyclerView rcv_adapter;

    private Adapter_SSDDCategories loAdapter;

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
        rcv_adapter = findViewById(R.id.rcv_adapter);

        if (!getIntent().hasExtra("transnox")) {

            InitMessage(1, R.drawable.baseline_error_24, "No transaction number has been detected", "Okay", "", new onMessageButton() {
                @Override
                public void onPositive() { finish(); }

                @Override
                public void onNegative() {}
            });
        }else if(!getIntent().hasExtra("deptid")){

            InitMessage(1, R.drawable.baseline_error_24, "No department id has been detected", "Okay", "", new onMessageButton() {
                @Override
                public void onPositive() { finish(); }

                @Override
                public void onNegative() {}
            });
        }else {
            InitObservers();
        }
    }

    private void InitObservers(){

        //initliaze master
        mViewModel.GetMaster(getIntent().getStringExtra("transnox"), getIntent().getStringExtra("deptid")).observe(
                Activity_SSDD_Category.this, new Observer<ESSDDMaster>() {
            @Override
            public void onChanged(ESSDDMaster essddMaster) {

                if (essddMaster == null){

                    InitMessage(1, R.drawable.baseline_error_24, "Could not find transaction", "Okay", "", new onMessageButton() {
                        @Override
                        public void onPositive() {
                            finish();
                        }

                        @Override
                        public void onNegative() {}
                    });
                    return;
                }
                mtv_dept.setText(mViewModel.GetDepartment(essddMaster.getsDeptIDxx()).getsDescript());
            }
        });

        //initialize detail
        mViewModel.GetDetail(getIntent().getStringExtra("transnox")).observe(Activity_SSDD_Category.this, new Observer<List<ESSDDetail>>() {
            @Override
            public void onChanged(List<ESSDDetail> essdDetails) {

                if (essdDetails == null){

                    InitMessage(1, R.drawable.baseline_error_24, "No details found for this transaction", "Okay", "", new onMessageButton() {
                        @Override
                        public void onPositive() {
                            finish();
                        }

                        @Override
                        public void onNegative() {}
                    });
                    return;
                }

                //initialize list for adapter
                List<Adapter_SSDDCategories.SSDD_Evaluation_Categories> laCategories = new ArrayList<>();
                for (ESSDDetail loDetal : essdDetails){

                    Adapter_SSDDCategories.SSDD_Evaluation_Categories loCategory = new Adapter_SSDDCategories.SSDD_Evaluation_Categories(
                            loDetal.getsCategrID(),
                            mViewModel.GetCategory(loDetal.getsCategrID()).getsDescript(),
                            Double.parseDouble(loDetal.getdEvaluate()),
                            loDetal.getsRemarksx(),
                            loDetal.getdEvaluate()
                    );
                    laCategories.add(loCategory);
                }


                //initialize categories
                loAdapter = new Adapter_SSDDCategories(laCategories, new Adapter_SSDDCategories.OnItemClickListener() {
                    @Override
                    public void OnRate(float ffTotalRate) {

                    }

                    @Override
                    public void OnCamera() {

                    }

                    @Override
                    public void OnDetails(String fsCategoryID) {

                        Intent loIntent = new Intent(Activity_SSDD_Category.this, Activity_SSDD_Category_Details.class);
                        loIntent.putExtra("transnox", getIntent().getStringExtra("transnox"));
                        loIntent.putExtra("categoryid", fsCategoryID);
                        startActivity(loIntent);

                    }
                });
                rcv_adapter.setAdapter(loAdapter);
                rcv_adapter.setLayoutManager(new LinearLayoutManager(Activity_SSDD_Category.this, LinearLayoutManager.VERTICAL, false));

                loAdapter.notifyDataSetChanged();

                //initialize master details
                double ldbl_totalRating = 0.0;
                int ntotalEvaluated = 0;
                for (ESSDDetail loDetal : essdDetails){

                    if (loDetal.getdEvaluate() != null){
                        ldbl_totalRating += Double.parseDouble(loDetal.getdEvaluate());
                        ntotalEvaluated++;
                    }
                }

                //display text details
                mtv_evaluation.setText(ntotalEvaluated + " out of " + essdDetails.size());
                mtv_ratings.setText(String.format("%.1f", ldbl_totalRating / ntotalEvaluated));
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