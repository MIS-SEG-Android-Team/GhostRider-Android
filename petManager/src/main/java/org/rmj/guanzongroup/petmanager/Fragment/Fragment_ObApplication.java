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

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.etc.GToast;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.pojo.OBApplication;
import org.rmj.guanzongroup.petmanager.R;
import org.rmj.guanzongroup.petmanager.ViewModel.VMObApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Fragment_ObApplication extends Fragment {

    public static final String TAG = Fragment_ObApplication.class.getSimpleName();
    private VMObApplication mViewModel;

    private MaterialTextView lblUsername, lblPosition, lblBranch;
    private LinearLayout lnWithoutLog;
    private Button btnSubmit;
    private MaterialAutoCompleteTextView txtBranchDestination;
    private TextInputEditText txtDateFrom,
                                txtDateTo,
                                txtNoDays,
                                txtRemarks;
    private OBApplication infoModel;
    private LoadDialog poProgress;
    private MessageBox loMessage;

    private long mLastClickTime = 0;

    public static Fragment_ObApplication newInstance() {
        return new Fragment_ObApplication();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ob_application, container, false);
        infoModel = new OBApplication();
        lblUsername = view.findViewById(R.id.lbl_username);
        lblPosition = view.findViewById(R.id.lbl_userPosition);
        lblBranch = view.findViewById(R.id.lbl_userBranch);

        lnWithoutLog = view.findViewById(R.id.linear_ObWithoutLog);
        txtDateFrom = view.findViewById(R.id.txt_dateFrom);
        txtDateTo = view.findViewById(R.id.txt_dateTo);
        txtNoDays = view.findViewById(R.id.txt_noOfDays);
        btnSubmit = view.findViewById(R.id.btn_submitApplication);
        txtBranchDestination = view.findViewById(R.id.txt_brnDestination);
        txtRemarks = view.findViewById(R.id.txt_remarks);

        poProgress = new LoadDialog(getActivity());
        loMessage = new MessageBox(getActivity());

        mViewModel = new ViewModelProvider(this).get(VMObApplication.class);

        mViewModel.getUserInfo().observe(getViewLifecycleOwner(), eEmployeeInfo -> {
            try{
                lblUsername.setText(eEmployeeInfo.sUserName);
                lblPosition.setText(DeptCode.getDepartmentName(eEmployeeInfo.sDeptIDxx));
                infoModel.setEmployID(eEmployeeInfo.sEmployID);
                infoModel.setTransact(AppConstants.CURRENT_DATE());
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.getUserBranchInfo().observe(getViewLifecycleOwner(), eBranchInfo -> {
            try{
                lblBranch.setText(eBranchInfo.getBranchNm());

            } catch (Exception e){
                e.printStackTrace();
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

        mViewModel.getAllBranchNames().observe(getViewLifecycleOwner(), strings -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, strings);
            txtBranchDestination.setAdapter(adapter);
        });

        txtBranchDestination.setOnItemClickListener((adapterView, viewx, i, l) -> mViewModel.getAllBranchInfo().observe(getViewLifecycleOwner(), eBranchInfos -> {
            for(int x = 0; x < eBranchInfos.size(); x++){
                if(txtBranchDestination.getText().toString().equalsIgnoreCase(eBranchInfos.get(x).getBranchNm())){
                    infoModel.setDestinat(eBranchInfos.get(x).getBranchCd());
                    break;
                }
            }
        }));

        btnSubmit.setOnClickListener(v->{
            long time = SystemClock.elapsedRealtime() - mLastClickTime;
            if(time < 5000){
                Toast.makeText(requireContext(), "Please wait...", Toast.LENGTH_LONG).show();
            } else {
                mLastClickTime = SystemClock.elapsedRealtime();
                infoModel.setDateFrom(FormatUIText.formatTextToData(Objects.requireNonNull(txtDateFrom.getText()).toString()));
                infoModel.setDateThru(FormatUIText.formatTextToData(Objects.requireNonNull(txtDateTo.getText()).toString()));
                infoModel.setRemarksx(Objects.requireNonNull(txtRemarks.getText()).toString());
                mViewModel.saveObLeave(infoModel, new VMObApplication.OnSubmitOBLeaveListener() {
                    @Override
                    public void onSuccess() {
                        poProgress.dismiss();
                        loMessage.initDialog();
                        loMessage.setIcon(R.drawable.baseline_message_24);
                        loMessage.setPositiveButton("Okay", (v, dialog) -> {
                            dialog.dismiss();
                            txtBranchDestination.setText("");
                            txtDateFrom.setText("");
                            txtDateTo.setText("");
                            txtNoDays.setText("");
                            txtRemarks.setText("");
                            requireActivity().finish();
                        });
                        loMessage.setTitle("PET Manager");
                        loMessage.setMessage("Your business trip application has been submitted.");
                        loMessage.show();
                    }

                    @Override
                    public void onFailed(String message) {
                        poProgress.dismiss();
                        loMessage.initDialog();
                        loMessage.setIcon(R.drawable.baseline_error_24);
                        loMessage.setPositiveButton("Okay", (v, dialog) -> dialog.dismiss());
                        loMessage.setTitle("PET Manager");
                        loMessage.setMessage(message);
                        loMessage.show();
                    }
                });
            }
        });
        return view;
    }
}