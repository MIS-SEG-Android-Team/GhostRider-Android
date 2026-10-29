package org.rmj.guanzongroup.ghostrider.approvalcode.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.ECASRequests;
import org.rmj.g3appdriver.etc.FileViewer.Activity.Activity_FileViewer;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.approvalcode.Activity.Activity_TransactionApproval_Details;
import org.rmj.guanzongroup.ghostrider.approvalcode.Etc.Adapter_Transaction_History;
import org.rmj.guanzongroup.ghostrider.approvalcode.R;
import org.rmj.guanzongroup.ghostrider.approvalcode.ViewModel.VMApprovalSelection;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

public class Fragment_Transaction_History extends Fragment {

    private VMApprovalSelection mViewModel;
    private Adapter_Transaction_History loAdapter;

    private TextInputEditText tie_search;
    private ImageButton ib_filter;
    private RecyclerView rcv_list;

    private String lsArgType;
    private String lsArgCode;
    private String lsArgSource;

    private interface OnDialog{
        void OnSubmit(String fsEntry);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.layout_transaction_history, container, false);

        mViewModel = new ViewModelProvider(this).get(VMApprovalSelection.class);

        tie_search = v.findViewById(R.id.tie_search);
        ib_filter = v.findViewById(R.id.ib_filter);
        rcv_list = v.findViewById(R.id.rcv_list);

        ImportTransactionRequests(GetLastMonth(), GetDateToday());
        InitListener();

        return v;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);

        if(args != null){
            if (!args.containsKey("code") && !args.containsKey("source")){
                return;
            }
            lsArgType = args.getString("type");
            lsArgCode = args.getString("code");
            lsArgSource = args.getString("source");
        }
    }

    private void ImportTransactionRequests(String fsStartDt, String fsEndDt){

        mViewModel.importCASRequests(lsArgCode, fsStartDt, fsEndDt, lsArgType.equals("4"),  new VMApprovalSelection.onDownload() {
            @Override
            public void onFinished(String message) {

                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                InitTransactionRequests(fsStartDt, fsEndDt);
            }
        });
    }

    private void InitListener(){

        tie_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (loAdapter == null) return;
                InitFilter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ib_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //initialize pop up object, menu object holder
                PopupMenu loMenu = new PopupMenu(requireActivity(), v);
                loMenu.getMenuInflater().inflate(org.rmj.g3appdriver.R.menu.menu_cas_approval, loMenu.getMenu());
                loMenu.show();

                loMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getItemId() == org.rmj.g3appdriver.R.id.action_download){

                            MaterialDatePicker.Builder<Pair<Long, Long>> loBuilder = MaterialDatePicker.Builder.dateRangePicker();
                            loBuilder.setTitleText("Select Date Range");

                            MaterialDatePicker<Pair<Long, Long>> loPicker = loBuilder.build();
                            loPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                                @Override
                                public void onPositiveButtonClick(Pair<Long, Long> selection) {
                                    Long startRange = selection.first;
                                    Long endRange = selection.second;

                                    ImportTransactionRequests(GetDateFormat(startRange), GetDateFormat(endRange));
                                }
                            });
                            loPicker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");
                            
                        } else if (menuItem.getItemId() == org.rmj.g3appdriver.R.id.action_amount_below) {

                            ShowDialogEntry("List of Approvals BELOW", "Filter", "Please enter amount", InputType.TYPE_NUMBER_FLAG_DECIMAL, new OnDialog() {
                                @Override
                                public void OnSubmit(String fsEntry) {

                                }
                            });
                            
                        } else if (menuItem.getItemId() == org.rmj.g3appdriver.R.id.action_amount_between) {

                            ShowDialogEntry("List of Approvals BETWEEN", "Filter", "Please enter amount", InputType.TYPE_NUMBER_FLAG_DECIMAL, new OnDialog() {
                                @Override
                                public void OnSubmit(String fsEntry) {

                                }
                            });

                        } else if (menuItem.getItemId() == org.rmj.g3appdriver.R.id.action_amount_above) {

                            ShowDialogEntry("List of Approvals ABOVE", "Filter", "Please enter amount", InputType.TYPE_NUMBER_FLAG_DECIMAL, new OnDialog() {
                                @Override
                                public void OnSubmit(String fsEntry) {

                                }
                            });

                        }

                        return false;
                    }
                });
            }
        });
    }

    private void InitTransactionRequests(String lsStartDt, String lsEndDt){

        if (lsArgType.equals("3")){

            mViewModel.getCASRequests(lsArgCode, lsStartDt, lsEndDt).observe(getViewLifecycleOwner(), new Observer<List<ECASRequests>>() {
                @Override
                public void onChanged(List<ECASRequests> ecasRequests) {
                    InitAdapter(ecasRequests);
                }
            });
        }else if (lsArgType.equals("4")){

            mViewModel.GetCASHistory(lsArgCode, lsStartDt, lsEndDt).observe(getViewLifecycleOwner(), new Observer<List<ECASRequests>>() {
                @Override
                public void onChanged(List<ECASRequests> ecasRequests) {
                    InitAdapter(ecasRequests);
                }
            });
        }
    }

    private void InitAdapter(List<ECASRequests> faRequests){

        loAdapter = new Adapter_Transaction_History(requireActivity().getApplication(), faRequests, new Adapter_Transaction_History.OnItemClick() {
            @Override
            public void OnClick(ECASRequests loRequest) {
                Intent loIntent = new Intent(requireContext(), Activity_TransactionApproval_Details.class);
                loIntent.putExtra("transnox", loRequest.getsTransNox());
                loIntent.putExtra("mode", lsArgType);
                loIntent.putExtra("source", lsArgSource);

                startActivity(loIntent);
            }
        });
        loAdapter.notifyDataSetChanged();

        rcv_list.setAdapter(loAdapter);
        rcv_list.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        if (!tie_search.getText().toString().isEmpty()){
            InitFilter(tie_search.getText().toString());
        }

    }

    private void InitFilter(String fsFilter){
        try{
            String query = fsFilter.toString();
            loAdapter.GetFilter().filter(query);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void ShowDialogEntry(String fsTitle, String fsBtn, String fsHint, int fnType, OnDialog foCallback){

        AlertDialog.Builder poBuilder = new AlertDialog.Builder(requireActivity());
        View view = LayoutInflater.from(requireActivity()).inflate(org.rmj.g3appdriver.R.layout.layout_single_field, null);

        poBuilder.setCancelable(false)
                .setView(view);

        AlertDialog poDialog = poBuilder.create();
        poDialog.setCancelable(true);

        MaterialTextView lblTitle = view.findViewById(org.rmj.g3appdriver.R.id.mtv_title);
        lblTitle.setText(fsTitle);

        TextInputEditText tie_filter = view.findViewById(org.rmj.g3appdriver.R.id.tie_filter);
        tie_filter.setHint(fsHint);
        tie_filter.setInputType(fnType);

        MaterialButton btm_submit = view.findViewById(org.rmj.g3appdriver.R.id.btn_submit);
        btm_submit.setText(fsBtn);

        btm_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tie_filter.getText() == null || tie_filter.getText().toString().isEmpty()){
                    Toast.makeText(requireActivity(), "Field is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                foCallback.OnSubmit(tie_filter.getText().toString());
                if(poDialog.isShowing()){
                    poDialog.dismiss();
                }
            }
        });

        if (!poDialog.isShowing()){
            poDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            poDialog.getWindow().getAttributes().windowAnimations = org.rmj.g3appdriver.R.style.PopupAnimation;
            poDialog.show();
        }
    }

    private String GetLastMonth(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            return LocalDateTime.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }else {
            Calendar today = Calendar.getInstance();
            today.add(Calendar.MONTH, -1);
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
}
