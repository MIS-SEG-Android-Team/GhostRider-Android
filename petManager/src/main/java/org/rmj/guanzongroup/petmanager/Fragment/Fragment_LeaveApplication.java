/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.petmanager.Fragment;

import static org.rmj.g3appdriver.etc.AppConstants.GetLeaveTypeIndex;
import static org.rmj.g3appdriver.etc.AppConstants.LEAVE_TYPE;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.etc.GToast;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.pojo.LeaveApplication;
import org.rmj.guanzongroup.petmanager.R;
import org.rmj.guanzongroup.petmanager.ViewModel.VMLeaveApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Fragment_LeaveApplication extends Fragment {

    private VMLeaveApplication mViewModel;
    private LeaveApplication poLeave;
    private MaterialTextView lblUsername, lblPosition, lblBranch;
    private MaterialAutoCompleteTextView spnType;
    private TextInputEditText txtDateFrom, txtDateTo, txtNoDays, txtRemarks;
    private MaterialButton btnSubmit;

    private LoadDialog poProgress;
    private MessageBox poMessage;

    private long mLastClickTime = 0;

    public static Fragment_LeaveApplication newInstance() {
        return new Fragment_LeaveApplication();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(VMLeaveApplication.class);
        View view = inflater.inflate(R.layout.fragment_leave_application, container, false);

        lblUsername = view.findViewById(R.id.lbl_username);
        lblPosition = view.findViewById(R.id.lbl_userPosition);
        lblBranch = view.findViewById(R.id.lbl_userBranch);

        spnType = view.findViewById(R.id.spn_leaveType);
        txtDateFrom = view.findViewById(R.id.txt_dateFrom);
        txtDateTo = view.findViewById(R.id.txt_dateTo);
        txtNoDays = view.findViewById(R.id.txt_noOfDays);
        txtRemarks = view.findViewById(R.id.txt_remarks);
        btnSubmit = view.findViewById(R.id.btn_submit);

        poProgress = new LoadDialog(getActivity());
        poMessage = new MessageBox(getActivity());

        poLeave = new LeaveApplication();
        mViewModel.GetUserInfo().observe(getViewLifecycleOwner(), eEmployeeInfo -> {
            try{
                lblUsername.setText(eEmployeeInfo.sUserName);
                lblPosition.setText(DeptCode.getDepartmentName(eEmployeeInfo.sDeptIDxx));
                poLeave.setEmploName(eEmployeeInfo.sUserName);

                lblBranch.setText(eEmployeeInfo.sBranchNm);
                poLeave.setBranchNme(eEmployeeInfo.sBranchNm);
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        spnType.setAdapter(new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, LEAVE_TYPE));
        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                poLeave.setLeaveType(String.valueOf(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtDateFrom.setOnClickListener(v -> {
            final Calendar newCalendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
            final DatePickerDialog dateFrom = new DatePickerDialog(getActivity(), (viewx, year, month, dayOfMonth) -> {
                try {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, month, dayOfMonth);
                    if(!txtDateTo.getText().toString().isEmpty()){
                        Date dateTo = dateFormatter.parse(Objects.requireNonNull(txtDateTo.getText()).toString());
                        String lsFrom = dateFormatter.format(newDate.getTime());
                        Date dateFrom1 = dateFormatter.parse(lsFrom);
                        int result = dateFrom1.compareTo(dateTo);
                        if(result <= 0) {
                            txtDateFrom.setText(dateFormatter.format(newDate.getTime()));
                            long diff = dateTo.getTime() - dateFrom1.getTime();
                            long noOfDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
                            txtNoDays.setText(String.valueOf(noOfDays));
                        } else {
                            GToast.CreateMessage(getActivity(), "Invalid date selected.", GToast.ERROR).show();
                        }
                    } else {
                        txtDateFrom.setText(dateFormatter.format(newDate.getTime()));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            dateFrom.show();
        });

        txtDateTo.setOnClickListener(v -> {
            if(!Objects.requireNonNull(txtDateFrom.getText()).toString().isEmpty()) {
                final Calendar newCalendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
                @SuppressLint("SetTextI18n") final DatePickerDialog dateFrom = new DatePickerDialog(getActivity(), (viewx, year, month, dayOfMonth) -> {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, month, dayOfMonth);
                    try {
                        Date dateFrom1 = dateFormatter.parse(Objects.requireNonNull(txtDateFrom.getText()).toString());
                        Date dateTo = newDate.getTime();
                        assert dateFrom1 != null;
                        int result = dateTo.compareTo(dateFrom1);
                        if (result >= 0) {
                            txtDateTo.setText(dateFormatter.format(newDate.getTime()));
                            long diff = dateTo.getTime() - dateFrom1.getTime();
                            long noOfDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
                            txtNoDays.setText(""+ Integer.parseInt(String.valueOf(noOfDays)));
                        } else {
                            GToast.CreateMessage(getActivity(), "Invalid date selected.", GToast.ERROR).show();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                dateFrom.show();
            } else {
                GToast.CreateMessage(getActivity(), "Select start date first.", GToast.ERROR).show();
            }
        });

        btnSubmit.setOnClickListener(v -> {
            long time = SystemClock.elapsedRealtime() - mLastClickTime;
            if(time < 5000) {
                Toast.makeText(requireContext(), "Please wait...", Toast.LENGTH_LONG).show();
            } else {
                mLastClickTime = SystemClock.elapsedRealtime();
                poLeave.setDateFromx(Objects.requireNonNull(txtDateFrom.getText()).toString());
                poLeave.setDateThrux(Objects.requireNonNull(txtDateTo.getText()).toString());
                String lsNoDays = Objects.requireNonNull(txtNoDays.getText()).toString();
                int lnNoDays = 0;
                if (!lsNoDays.trim().isEmpty()) {
                    lnNoDays = Integer.parseInt(lsNoDays);
                }
                poLeave.setNoOfDaysx(lnNoDays);
                poLeave.setLeaveType(GetLeaveTypeIndex(spnType.getText().toString()));
                poLeave.setRemarksxx(Objects.requireNonNull(txtRemarks.getText()).toString());
                mViewModel.SaveApplication(poLeave, new VMLeaveApplication.LeaveApplicationCallback() {
                    @Override
                    public void OnSave(String Title, String message) {
                        poProgress.initDialog(Title, message, false);
                        poProgress.show();
                    }

                    @Override
                    public void OnSuccess(String message) {
                        poProgress.dismiss();
                        poMessage.initDialog();
                        poMessage.setIcon(R.drawable.baseline_message_24);
                        poMessage.setTitle("Leave Application");
                        poMessage.setMessage(message);
                        poMessage.setPositiveButton("Dismiss", (view, dialog) -> {
                            dialog.dismiss();
                            spnType.setSelection(0);
                            txtDateFrom.setText("");
                            txtDateTo.setText("");
                            txtNoDays.setText("");
                            txtRemarks.setText("");
                            poLeave = new LeaveApplication();
                        });
                        poMessage.show();
                    }

                    @Override
                    public void OnFailed(String message) {
                        poProgress.dismiss();
                        poMessage.initDialog();
                        poMessage.setIcon(R.drawable.baseline_error_24);
                        poMessage.setTitle("Leave Application");
                        poMessage.setMessage(message);
                        poMessage.setPositiveButton("Dismiss", (view, dialog) -> dialog.dismiss());
                        poMessage.show();
                    }
                });
            }
        });
        return view;
    }
}