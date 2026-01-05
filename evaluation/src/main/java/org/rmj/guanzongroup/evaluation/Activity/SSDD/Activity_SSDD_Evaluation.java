package org.rmj.guanzongroup.evaluation.Activity.SSDD;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDCategories;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDepartments;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.evaluation.Adapter.SSDD.Adapter_SSDDHistory;
import org.rmj.guanzongroup.evaluation.Adapter.SSDD.Adapter_SSDDepartments;
import org.rmj.guanzongroup.evaluation.R;
import org.rmj.guanzongroup.evaluation.ViewModel.SSDD.VMSSDEvaluation;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

public class Activity_SSDD_Evaluation extends AppCompatActivity {

    private VMSSDEvaluation mViewModel;
    private LoadDialog poDialog;
    private MessageBox poMessage;

    private RecyclerView rcv_list;
    private TextInputEditText tie_search;
    private ImageButton ib_filter;
    private FloatingActionButton fbtn_evaluate;

    private String lsDfrom, lsDto, lsTranstat;

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

        //initiliaze default date range parameters
        lsDfrom = GetFirstQuarter();
        lsDto = GetDateToday();

        lsTranstat = "*";

        ImportData();
        InitObservers();
        InitFilterOptions();

    }

    private void ImportData(){

        //passed department id, download transaction history, else, download department list
        if (getIntent().hasExtra("dept_id")){

            //import data
            mViewModel.DownloadEvaluation(getIntent().getStringExtra("dept_id"), lsDfrom, lsDto, new VMSSDEvaluation.OnDownloadCallback() {
                @Override
                public void OnLoad(String fsTitlexx, String fsMessage) {
                    poDialog.initDialog(fsTitlexx, fsMessage, false);
                    poDialog.show();
                }

                @Override
                public void OnSuccess() {
                    poDialog.dismiss();
                    Toast.makeText(Activity_SSDD_Evaluation.this, "Successfully downloaded", Toast.LENGTH_SHORT).show();
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
        }{

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

    private void InitObservers(){

        //passed department id, load transaction history, else, load department list
        if (getIntent().hasExtra("dept_id")){

            //initialize master list
            mViewModel.GetMasterList(lsDfrom, lsDto, getIntent().getStringExtra("dept_id"), lsTranstat).observe(Activity_SSDD_Evaluation.this, new Observer<List<ESSDDMaster>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onChanged(List<ESSDDMaster> essddMasters) {
                    if (essddMasters == null){
                        return;
                    }
                    Adapter_SSDDHistory loAdapter = new Adapter_SSDDHistory(essddMasters, new Adapter_SSDDHistory.OnItemClickListener() {
                        @Override
                        public void OnClick(ESSDDMaster loHistory) {

                        }
                    });
                    loAdapter.notifyDataSetChanged();

                    rcv_list.setAdapter(loAdapter);
                    rcv_list.setLayoutManager(new LinearLayoutManager(Activity_SSDD_Evaluation.this, LinearLayoutManager.VERTICAL, false));

                    //search listener
                    tie_search.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            loAdapter.GetFilter().filter(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {}
                    });

                    //show filtering option
                    ib_filter.setVisibility(View.VISIBLE);
                }
            });

            //initialize categories
            mViewModel.GetCategories().observe(Activity_SSDD_Evaluation.this, new Observer<List<ESSDDCategories>>() {
                @Override
                public void onChanged(List<ESSDDCategories> essddCategories) {

                    fbtn_evaluate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (essddCategories == null){

                                InitMessage(1, R.drawable.baseline_error_24, "No categories found. Could not create evaluation.", "Okay", "", new onMessageButton() {
                                    @Override
                                    public void onPositive() {}

                                    @Override
                                    public void onNegative() {}
                                });
                                return;
                            }

                            String lsTransNox = mViewModel.CreateEvaluation(getIntent().getStringExtra("dept_id"), essddCategories);
                            Intent loIntent = new Intent(Activity_SSDD_Evaluation.this, Activity_SSDD_Category.class);
                            loIntent.putExtra("transnox", lsTransNox);
                            loIntent.putExtra("deptid", lsTransNox);

                            startActivity(loIntent);
                        }
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
                    Adapter_SSDDepartments loAdapter = new Adapter_SSDDepartments(essdDepartments, new Adapter_SSDDepartments.OnItemClickListener() {
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

                    //search listener
                    tie_search.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            loAdapter.GetFilter().filter(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {}
                    });

                    //show filtering option
                    ib_filter.setVisibility(View.GONE);
                }
            });
        }

    }

    private void InitFilterOptions(){

        ib_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu loMenu = new PopupMenu(Activity_SSDD_Evaluation.this, v);
                loMenu.getMenuInflater().inflate(R.menu.menu_ssdd_filter, loMenu.getMenu());
                loMenu.show();

                loMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.action_by_date){

                            MaterialDatePicker.Builder<Pair<Long, Long>> loBuilder = MaterialDatePicker.Builder.dateRangePicker();
                            loBuilder.setTitleText("Select Date Range");

                            MaterialDatePicker<Pair<Long, Long>> loPicker = loBuilder.build();
                            loPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                                @Override
                                public void onPositiveButtonClick(Pair<Long, Long> selection) {

                                    //set date range parameters for downloading history
                                    lsDfrom = GetDateFormat(selection.first);
                                    lsDto = GetDateFormat(selection.second);

                                    //ask user before downloadinh
                                    InitMessage(1, R.drawable.ic_baseline_confirmation_pin_24, "Download transactions from " + lsDfrom + " to " + lsDto + "?",
                                            "Yes", "No", new onMessageButton() {
                                                @Override
                                                public void onPositive() {
                                                    ImportData();
                                                }

                                                @Override
                                                public void onNegative() {
                                                    InitObservers();
                                                }
                                            });
                                }
                            });
                            loPicker.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");

                            return true;
                        }else if (item.getItemId() == R.id.action_item_all){
                            lsTranstat = "*";
                            return true;
                        }else if (item.getItemId() == R.id.action_item_open){
                            lsTranstat = "0";
                            return true;
                        }else if (item.getItemId() == R.id.action_item_closed){
                            lsTranstat = "1";
                            return true;
                        }else if (item.getItemId() == R.id.action_item_posted){
                            lsTranstat = "3";
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
    }

    private String GetFirstQuarter(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            return LocalDateTime.now().minusMonths(4).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }else {
            Calendar today = Calendar.getInstance();
            today.add(Calendar.MONTH, -4);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(today.getTime());
        }
    }

    private String GetDateToday(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        }
    }

    private String GetDateFormat(Long fsDate){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fsDate);
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