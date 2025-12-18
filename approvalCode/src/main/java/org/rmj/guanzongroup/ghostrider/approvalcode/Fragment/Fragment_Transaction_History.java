package org.rmj.guanzongroup.ghostrider.approvalcode.Fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.datepicker.RangeDateSelector;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.ECASRequests;
import org.rmj.guanzongroup.ghostrider.approvalcode.Activity.Activity_TransactionApproval_Details;
import org.rmj.guanzongroup.ghostrider.approvalcode.Etc.Adapter_Transaction_History;
import org.rmj.guanzongroup.ghostrider.approvalcode.R;
import org.rmj.guanzongroup.ghostrider.approvalcode.ViewModel.VMApprovalSelection;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.layout_transaction_history, container, false);

        mViewModel = new ViewModelProvider(this).get(VMApprovalSelection.class);

        tie_search = v.findViewById(R.id.tie_search);
        ib_filter = v.findViewById(R.id.ib_filter);
        rcv_list = v.findViewById(R.id.rcv_list);

        ImportTransactionRequests();
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

    private void ImportTransactionRequests(){

        mViewModel.importCASRequests(lsArgCode, new VMApprovalSelection.onDownload() {
            @Override
            public void onFinished(String message) {

                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                InitTransactionRequests(GetLastMonth(), GetDateToday());
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
                InitFilter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ib_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder<Pair<Long, Long>> loBuilder = MaterialDatePicker.Builder.dateRangePicker();
                loBuilder.setTitleText("Select Date Range");

                MaterialDatePicker<Pair<Long, Long>> loPicker = loBuilder.build();
                loPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        Long startRange = selection.first;
                        Long endRange = selection.second;

                        InitTransactionRequests(GetDateFormat(startRange), GetDateFormat(endRange));
                    }
                });
                loPicker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");
            }
        });
    }

    private void InitTransactionRequests(String lsStartDt, String lsEndDt){

        mViewModel.getCASRequests(lsArgCode, lsStartDt, lsEndDt).observe(getViewLifecycleOwner(), new Observer<List<ECASRequests>>() {
            @Override
            public void onChanged(List<ECASRequests> ecasRequests) {

                if (ecasRequests != null){

                    loAdapter = new Adapter_Transaction_History(ecasRequests, new Adapter_Transaction_History.OnItemClick() {
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
            }
        });

    }

    private void InitFilter(String fsFilter){
        try{
            String query = fsFilter.toString();

            loAdapter.GetFilter().filter(query);
            loAdapter.notifyDataSetChanged();
        } catch (Exception e){
            e.printStackTrace();
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
