package org.rmj.guanzongroup.pacitareward.Activity.SSDD;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.GCircle.Apps.User_Guide.Activitiy.Activity_Manual;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDepartments;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.pacitareward.Adapter.SSDD.Adapter_SSDDepartments;
import org.rmj.guanzongroup.pacitareward.R;
import org.rmj.guanzongroup.pacitareward.ViewModel.SSDD.VMSSDEvaluation;

import java.util.List;

public class Activity_SSDD_Evaluation extends AppCompatActivity {

    private VMSSDEvaluation mViewModel;
    private LoadDialog poDialog;
    private MessageBox poMessage;

    private Adapter_SSDDepartments loAdapter;

    private RecyclerView rcv_list;
    private TextInputEditText tie_search;
    private ImageButton ib_filter;
    private FloatingActionButton fbtn_evaluate;

    private interface onMessageButton{
        void onPositive();
        void onNegative();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ssdd_evaluation);

        mViewModel = new ViewModelProvider(this).get(VMSSDEvaluation.class);
        poDialog = new LoadDialog(this);
        poMessage = new MessageBox(this);

        rcv_list = findViewById(R.id.rcv_list);
        tie_search = findViewById(R.id.tie_search);
        ib_filter = findViewById(R.id.ib_filter);
        fbtn_evaluate = findViewById(R.id.fbtn_evaluate);

        InitObservers();
        InitListener();
    }

    public void InitObservers(){

        //passed department id, load categories, else, load department list
        if (getIntent().hasExtra("dept_id")){

            //import categories
            mViewModel.DownloadSSDDCategories(new VMSSDEvaluation.OnDownloadCallback() {
                @Override
                public void OnLoad(String fsTitlexx, String fsMessage) {
                    poDialog.initDialog(fsTitlexx, fsMessage, false);
                    poDialog.show();
                }

                @Override
                public void OnSuccess() {
                    Toast.makeText(Activity_SSDD_Evaluation.this, "Successfully downloaded", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void OnFailed(String fsMessage) {

                    InitMessage(0, R.drawable.baseline_error_24, fsMessage, "Okay", "", new onMessageButton() {
                        @Override
                        public void onPositive() {}

                        @Override
                        public void onNegative() {}
                    });
                }
            });
        }{

            //initialize data
            mViewModel.GetDepartments().observe(Activity_SSDD_Evaluation.this, new Observer<List<ESSDDepartments>>() {
                @Override
                public void onChanged(List<ESSDDepartments> essdDepartments) {

                    if (essdDepartments == null){
                        return;
                    }
                    loAdapter = new Adapter_SSDDepartments(essdDepartments, new Adapter_SSDDepartments.OnItemClickListener() {
                        @Override
                        public void OnClick(ESSDDepartments loDepartment) {
                            Intent loIntent = new Intent(Activity_SSDD_Evaluation.this, Activity_SSDD_Evaluation.class);
                            loIntent.putExtra("dept_id", loDepartment.getsDeptIDxx());

                            startActivity(loIntent);
                        }
                    });
                    loAdapter.notifyDataSetChanged();

                    rcv_list.setAdapter(loAdapter);
                    rcv_list.setLayoutManager(new LinearLayoutManager(Activity_SSDD_Evaluation.this, LinearLayoutManager.VERTICAL, false));
                }
            });

            //import departments
            mViewModel.DownloadSSDDepartments(new VMSSDEvaluation.OnDownloadCallback() {
                @Override
                public void OnLoad(String fsTitlexx, String fsMessage) {
                    poDialog.initDialog(fsTitlexx, fsMessage, false);
                    poDialog.show();
                }

                @Override
                public void OnSuccess() {
                    Toast.makeText(Activity_SSDD_Evaluation.this, "Successfully downloaded", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void OnFailed(String fsMessage) {

                    InitMessage(0, R.drawable.baseline_error_24, fsMessage, "Okay", "", new onMessageButton() {
                        @Override
                        public void onPositive() {}

                        @Override
                        public void onNegative() {}
                    });
                }
            });
        }

    }

    public void InitListener(){

        tie_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (getIntent().hasExtra("dept_id")){
                    if (loAdapter == null){
                        return;
                    }
                    loAdapter.GetFilter().filter(s.toString());
                }else {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
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