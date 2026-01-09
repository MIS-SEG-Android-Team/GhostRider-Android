package org.rmj.guanzongroup.evaluation.Fragments.SSDD;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDepartments;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDetail;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.evaluation.Activity.SSDD.Activity_SSDD_Category;
import org.rmj.guanzongroup.evaluation.Adapter.SSDD.Adapter_SSDDHistory;
import org.rmj.guanzongroup.evaluation.Adapter.SSDD.Adapter_SSDDepartments;
import org.rmj.guanzongroup.evaluation.Callback.OnSSDDItemClick;
import org.rmj.guanzongroup.evaluation.R;
import org.rmj.guanzongroup.evaluation.ViewModel.SSDD.VMSSDEvaluation;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

public class Fragment_SSDD_Evaluation extends Fragment{

    private Bundle loBundle;

    private VMSSDEvaluation mViewModel;
    private LoadDialog poDialog;
    private MessageBox poMessage;

    private RecyclerView rcv_list;
    private TextInputEditText tie_search;
    private ImageButton ib_filter;
    private FloatingActionButton fbtn_evaluate;

    private String lsDfrom, lsDto, lsTranstat;
    private OnSSDDItemClick callback;

    public interface OnMessageButton {
        void OnPositive();
        void OnNegative();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ssdd_evaluation, container, false);

        mViewModel = new ViewModelProvider(this).get(VMSSDEvaluation.class);
        poDialog = new LoadDialog(requireActivity());
        poMessage = new MessageBox(requireActivity());

        rcv_list = view.findViewById(R.id.rcv_list);
        tie_search = view.findViewById(R.id.tie_search);
        ib_filter = view.findViewById(R.id.ib_filter);
        fbtn_evaluate = view.findViewById(R.id.fbtn_evaluate);

        //initiliaze default date range parameters
        lsDfrom = GetFirstQuarter();
        lsDto = GetDateToday();

        lsTranstat = "cTranStat IN ('0', '1', '3')";

        //initalize callback
        callback = (OnSSDDItemClick) getActivity();

        InitFragment();
        InitListener();

        return view;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);

        if (args != null){
            loBundle = args;
        }
    }

    public void InitFragment(){

        if (loBundle == null){

            //show filtering option
            ib_filter.setVisibility(View.GONE);

            //show button evaluate
            fbtn_evaluate.setVisibility(View.GONE);

            //load department list
            mViewModel.GetDepartments().observe(getViewLifecycleOwner(), new Observer<List<ESSDDepartments>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onChanged(List<ESSDDepartments> essdDepartments) {

                    if (essdDepartments == null){

                        //if null, ask user to re download data
                        InitMessage(1, R.drawable.baseline_error_24, "No departments found for evaluation. Re download data?", "Yes", "No", new OnMessageButton() {
                            @Override
                            public void OnPositive() {
                                ReDownloadData();
                            }

                            @Override
                            public void OnNegative() {}
                        });
                        return;
                    }

                    //initialize adapter for department list
                    Adapter_SSDDepartments loAdapter = new Adapter_SSDDepartments(essdDepartments, new Adapter_SSDDepartments.OnItemClickListener() {
                        @Override
                        public void OnClick(ESSDDepartments loDepartment) {
                            callback.OnSelectDepartment(loDepartment);
                        }
                    });
                    loAdapter.notifyDataSetChanged();

                    rcv_list.setAdapter(loAdapter);
                    rcv_list.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

                    tie_search.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            loAdapter.GetFilter().filter(s);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                }
            });

        }else {

            //show filtering option
            ib_filter.setVisibility(View.VISIBLE);

            //show button evaluate
            fbtn_evaluate.setVisibility(View.VISIBLE);

            //check selected department id if passed
            if (loBundle.containsKey("dept_id")){

                //load master list
                mViewModel.GetMasterList(lsDfrom, lsDto, loBundle.getString("dept_id"), lsTranstat).observe(requireActivity(), new Observer<List<ESSDDMaster>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChanged(List<ESSDDMaster> essddMasters) {

                        if (essddMasters == null){

                            InitMessage(0, R.drawable.baseline_error_24, "No transactions found", "Okay", "", new OnMessageButton() {
                                @Override
                                public void OnPositive() {}

                                @Override
                                public void OnNegative() {}
                            });
                            return;
                        }

                        //initialize list adapter for history
                        Adapter_SSDDHistory loAdapter = new Adapter_SSDDHistory(essddMasters, new Adapter_SSDDHistory.OnItemClickListener() {
                            @Override
                            public void OnClick(ESSDDMaster loHistory) {

                                //Continue to evaluation details
                                Intent loIntent = new Intent(requireActivity(), Activity_SSDD_Category.class);
                                loIntent.putExtra("transnox", loHistory.getsTransNox());
                                loIntent.putExtra("deptid", loBundle.getString("dept_id"));
                                startActivity(loIntent);
                            }
                        });
                        loAdapter.notifyDataSetChanged();

                        rcv_list.setAdapter(loAdapter);
                        rcv_list.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

                        tie_search.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                loAdapter.GetFilter().filter(s);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                    }
                });
                return;
            }

            Toast.makeText(requireActivity(), "Invalid arguments. Department id not detected!", Toast.LENGTH_LONG).show();
        }

    }

    private void ReDownloadData(){

        if (loBundle == null){

            //import data for department
            mViewModel.DownloadDepartments(new VMSSDEvaluation.OnDownloadCallback() {
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
                    InitMessage(0, R.drawable.baseline_error_24, fsMessage, "Okay", "", new OnMessageButton() {
                        @Override
                        public void OnPositive() {

                        }

                        @Override
                        public void OnNegative() {

                        }
                    });
                }
            });
        }else {

            //import data for categories
            mViewModel.DownloadCategories(new VMSSDEvaluation.OnDownloadCallback() {
                @Override
                public void OnLoad(String fsTitlexx, String fsMessage) {
                    poDialog.initDialog(fsTitlexx, fsMessage, false);
                    poDialog.show();
                }

                @Override
                public void OnSuccess() {
                    poDialog.dismiss();
                    Toast.makeText(requireActivity(), "Successfully downloaded categories", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void OnFailed(String fsMessage) {
                    poDialog.dismiss();
                    Toast.makeText(requireActivity(), "Failed to download categories", Toast.LENGTH_SHORT).show();
                }
            });
        }
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

    private void InitListener(){

        ib_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //initialize pop up object, menu object holder
                PopupMenu loMenu = new PopupMenu(requireContext(), v);
                loMenu.getMenuInflater().inflate(R.menu.menu_ssdd_filter, loMenu.getMenu());
                loMenu.show();

                //object listener
                loMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.action_by_date){ //filter by date

                            MaterialDatePicker.Builder<Pair<Long, Long>> loBuilder = MaterialDatePicker.Builder.dateRangePicker();
                            loBuilder.setTitleText("Select Date Range");

                            MaterialDatePicker<Pair<Long, Long>> loPicker = loBuilder.build();
                            loPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                                @Override
                                public void onPositiveButtonClick(Pair<Long, Long> selection) {

                                    //set date range parameters for downloading history
                                    lsDfrom = GetDateFormat(selection.first);
                                    lsDto = GetDateFormat(selection.second);

                                    //ask user before downloading
                                    InitMessage(1, R.drawable.ic_baseline_confirmation_pin_24, "Download transactions from " + lsDfrom + " to " + lsDto + "?",
                                            "Yes", "No", new OnMessageButton() {
                                                @Override
                                                public void OnPositive() {
                                                    ReDownloadData();
                                                }

                                                @Override
                                                public void OnNegative() {}
                                            });
                                }
                            });
                            loPicker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");

                            return true;
                        }else if (item.getItemId() == R.id.action_item_all){ //filter by status
                            lsTranstat = "cTranStat IN ('0', '1', '3')";
                            InitFragment();
                            return true;
                        }else if (item.getItemId() == R.id.action_item_open){ //filter by status
                            lsTranstat = "cTranStat= '0'";
                            InitFragment();
                            return true;
                        }else if (item.getItemId() == R.id.action_item_closed){ //filter by status
                            lsTranstat = "cTranStat= '1'";
                            InitFragment();
                            return true;
                        }else if (item.getItemId() == R.id.action_item_posted){ //filter by status
                            lsTranstat = "cTranStat= '3'";
                            InitFragment();
                            return true;
                        }
                        return false;
                    }
                });
            }
        });

        fbtn_evaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                InitMessage(1, R.drawable.ic_baseline_confirmation_pin_24, "Create a new evaluation?", "Yes", "No", new OnMessageButton() {
                    @Override
                    public void OnPositive() {

                        if (mViewModel.GetCategoriesNonLive() == null){

                            InitMessage(1, R.drawable.baseline_error_24, "Could not find categories for evaluation. Re download data?", "Yes", "No", new OnMessageButton() {
                                @Override
                                public void OnPositive() {
                                    ReDownloadData();
                                }

                                @Override
                                public void OnNegative() {}
                            });
                        }

                        String lsTransNox = mViewModel.CreateEvaluation(loBundle.getString("dept_id"), mViewModel.GetCategoriesNonLive());

                        Intent loIntent = new Intent(requireActivity(), Activity_SSDD_Category.class);
                        loIntent.putExtra("transnox", lsTransNox);
                        loIntent.putExtra("deptid", loBundle.getString("dept_id"));

                        startActivity(loIntent);
                    }

                    @Override
                    public void OnNegative() {

                    }
                });
            }
        });

    }

    private String GetDateFormat(Long fsDate){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fsDate);
    }

    private void InitMessage(int messageType, int statusIcon, String message, String posText, String negText, OnMessageButton callback){

        poMessage.initDialog();
        poMessage.setTitle("SSDD Evaluation");
        poMessage.setIcon(statusIcon);
        poMessage.setMessage(message);

        poMessage.setPositiveButton(posText, (view, dialog) -> {
            dialog.dismiss();
            callback.OnPositive();
        });

        if (messageType == 1){
            poMessage.setNegativeButton(negText, (view, dialog) -> {
                dialog.dismiss();
                callback.OnNegative();

            });
        }

        poMessage.show();
    }
}
