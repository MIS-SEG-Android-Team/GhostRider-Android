/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.dailyCollectionPlan
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.Fragments;

import static android.app.Activity.RESULT_OK;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.PromiseToPay;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_Transaction;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.OnInitializeCameraCallback;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.VMPromiseToPay;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.ViewModelCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class Fragment_PromiseToPay extends Fragment {

    private VMPromiseToPay mViewModel;

    private PromiseToPay poPtp;

    private LoadDialog poDialog;
    private MessageBox poMessage;

    private TextInputLayout tilBranchName, tilCollct;
    private TextInputEditText txtDate,
            txtCollct,
            txtRemarks;
    private MaterialAutoCompleteTextView txtBranch;
    private MaterialButton btnPtp;
    private RadioGroup rgPtpAppUnit;

    private String transNox;

    private MaterialTextView lblBranch, lblAddress, lblAccNo, lblClientNm, lblTransNo;

    ActivityResultLauncher<String[]> poRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> InitializeCamera());

    private final ActivityResultLauncher<Intent> poCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK) {
                mViewModel.SaveTransaction(poPtp, new ViewModelCallback() {
                    @Override
                    public void OnStartSaving() {
                        poDialog.initDialog("Selfie Log", "Saving promise to pay. Please wait...", false);
                        poDialog.show();
                    }

                    @Override
                    public void OnSuccessResult() {
                        poMessage.initDialog();
                        poMessage.setIcon(R.drawable.baseline_message_24);
                        poMessage.setTitle("Promise To Pay");
                        poMessage.setMessage("Promise to pay has been save.");
                        poMessage.setPositiveButton("Okay", (view, dialog) -> {
                            dialog.dismiss();
                            requireActivity().finish();
                        });
                        poMessage.show();
                    }

                    @Override
                    public void OnFailedResult(String message) {
                        poMessage.initDialog();
                        poMessage.setIcon(R.drawable.baseline_error_24);
                        poMessage.setTitle("Promise To Pay");
                        poMessage.setMessage(message);
                        poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                        poMessage.show();
                    }
                });
            }
        }
    });

    public static Fragment_PromiseToPay newInstance() {
        return new Fragment_PromiseToPay();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(VMPromiseToPay.class);
        poPtp = new PromiseToPay();
        View view = inflater.inflate(R.layout.fragment_promise_to_pay, container, false);
        poDialog = new LoadDialog(requireActivity());
        poMessage = new MessageBox(requireActivity());

        transNox = Activity_Transaction.getInstance().getTransNox();
        int entryNox = Activity_Transaction.getInstance().getEntryNox();
        String accntNox = Activity_Transaction.getInstance().getAccntNox();
        initWidgets(view);

        mViewModel.GetUserInfo().observe(getViewLifecycleOwner(), user -> {
            try{
                txtCollct.setText(user.sUserName);
                txtBranch.setText(user.sBranchNm);
                lblBranch.setText(user.sBranchNm);
                lblAddress.setText(user.sAddressx);
                poPtp.setBranchCd(user.sBranchCd);
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.GetCollectionDetail(transNox, entryNox, accntNox).observe(getViewLifecycleOwner(), collectionDetail -> {
            try {
                poPtp.setTransNox(transNox);
                poPtp.setEntryNox(String.valueOf(entryNox));
                poPtp.setAccntNox(accntNox);
                lblAccNo.setText(collectionDetail.getAcctNmbr());
                lblClientNm.setText(collectionDetail.getFullName());
                lblTransNo.setText(collectionDetail.getTransNox());
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.getAllBranchNames().observe(getViewLifecycleOwner(), strings -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, strings);
            txtBranch.setAdapter(adapter);
        });

        txtBranch.setOnItemClickListener((adapterView, v, i, l) -> mViewModel.getAllBranchInfo().observe(getViewLifecycleOwner(), branch -> {
            for(int x = 0; x < branch.size(); x++){
                if(txtBranch.getText().toString().equalsIgnoreCase(branch.get(x).getBranchNm())){
                    poPtp.setBranchCd(branch.get(x).getBranchCd());
                    break;
                }
            }
        }));

        txtDate.setOnClickListener(v ->  {
            final Calendar newCalendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
            final DatePickerDialog StartTime = new DatePickerDialog(getActivity(), (view131, year, monthOfYear, dayOfMonth) -> {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String lsDate = dateFormatter.format(newDate.getTime());
                txtDate.setText(lsDate);
                poPtp.setTransact(lsDate);
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            StartTime.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            StartTime.show();
        });

        rgPtpAppUnit.setOnCheckedChangeListener(new OnAppointmentUnitSelectionListener(rgPtpAppUnit));

        btnPtp.setOnClickListener( v -> {
            poPtp.setRemarks(Objects.requireNonNull(txtRemarks.getText()).toString().trim());
            if(!poPtp.isDataValid()){
                Toast.makeText(requireActivity(), poPtp.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                if (checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    poRequest.launch(new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CAMERA});
                } else {
                    InitializeCamera();
                }
            }
        });

        return view;
    }

    private void initWidgets(View v) {
        lblBranch = v.findViewById(R.id.lbl_headerBranch);
        lblAddress = v.findViewById(R.id.lbl_headerAddress);

        lblAccNo = v.findViewById(R.id.tvAccountNo);
        lblClientNm = v.findViewById(R.id.tvClientname);
        lblTransNo = v.findViewById(R.id.lbl_dcpTransNo);

        tilBranchName = v.findViewById(R.id.til_ptp_branchName);
        tilCollct = v.findViewById(R.id.til_CollectrName);
        txtDate = v.findViewById(R.id.pToPayDate);
        txtBranch = v.findViewById(R.id.txt_ptp_branchName);
        rgPtpAppUnit = v.findViewById(R.id.rb_ap_ptpBranch);
        txtRemarks = v.findViewById(R.id.tie_ptp_Remarks);
        txtCollct = v.findViewById(R.id.txt_ptp_collectorName);
        btnPtp = v.findViewById(R.id.btn_ptp_submit);
    }

    class OnAppointmentUnitSelectionListener implements RadioGroup.OnCheckedChangeListener{

        private final View rbView;

        OnAppointmentUnitSelectionListener(View view){
            this.rbView = view;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(rbView.getId() == R.id.rb_ap_ptpBranch){
                String appointment;
                if(checkedId == R.id.rb_ptpBranch) {
                    appointment = "1";
                    tilBranchName.setVisibility(View.VISIBLE);
                    tilCollct.setVisibility(View.GONE);
                } else {
                    tilBranchName.setVisibility(View.GONE);
                    tilCollct.setVisibility(View.VISIBLE);
                    appointment = "0";
                }
                poPtp.setPaymntxx(appointment);
            }
        }
    }

    private void InitializeCamera(){
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(requireActivity(), "Please enable your location service.", Toast.LENGTH_SHORT).show();
            return;
        }
        poMessage.initDialog();
        poMessage.setIcon(R.drawable.baseline_message_24);
        poMessage.setTitle("Promise To Pay");
        poMessage.setMessage("Please take a selfie with the customer or within the area of the customer.");
        poMessage.setPositiveButton("Okay", (view, dialog) -> {
            dialog.dismiss();
            mViewModel.InitCameraLaunch(requireActivity(), transNox, new OnInitializeCameraCallback() {
                @Override
                public void OnInit() {
                    poDialog.initDialog("Promise To Pay", "Initializing camera. Please wait...", false);
                    poDialog.show();
                }

                @Override
                public void OnSuccess(Intent intent, String[] args) {
                    poDialog.dismiss();
                    poPtp.setFilePath(args[0]);
                    poPtp.setFileName(args[1]);
                    poPtp.setLatitude(Double.parseDouble(args[2]));
                    poPtp.setLongtude(Double.parseDouble(args[3]));
                    poCamera.launch(intent);
                }

                @Override
                public void OnFailed(String message, Intent intent, String[] args) {
                    poDialog.dismiss();
                    poMessage.initDialog();
                    poMessage.setIcon(R.drawable.baseline_contact_support_24);
                    poMessage.setTitle("Promise To Pay");
                    poMessage.setMessage(message + "\n Proceed taking selfie?");
                    poMessage.setPositiveButton("Continue", (view, dialog) -> {
                        dialog.dismiss();
                        poPtp.setFilePath(args[0]);
                        poPtp.setFileName(args[1]);
                        poPtp.setLatitude(Double.parseDouble(args[2]));
                        poPtp.setLongtude(Double.parseDouble(args[3]));
                        poCamera.launch(intent);
                    });
                    poMessage.setNegativeButton("Cancel", (view1, dialog) -> dialog.dismiss());
                    poMessage.show();
                }
            });
        });
        poMessage.setNegativeButton("Cancel", (view, dialog) -> dialog.dismiss());
        poMessage.show();
    }
}