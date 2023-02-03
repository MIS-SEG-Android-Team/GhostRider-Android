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

package org.rmj.guanzongroup.ghostrider.ahmonitoring.Fragment;

import static android.app.Activity.RESULT_OK;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.dev.Database.Entities.EBranchInfo;
import org.rmj.g3appdriver.dev.DeptCode;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.GToast;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.lib.SelfieLog.SelfieLog;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity.Activity_CashCounter;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Adapter.TimeLogAdapter;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Dialog.DialogBranchSelection;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Dialog.DialogSelfieLogRemarks;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel.OnInitializeCameraCallback;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel.VMSelfieLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Fragment_SelfieLog extends Fragment {
    private static final String TAG = Fragment_SelfieLog.class.getSimpleName();

    private VMSelfieLog mViewModel;

    private TextView lblUsername, lblPosition, lblBranch;
    private TextInputEditText txtDate;
    private MaterialButton btnCamera, btnBranch;
    private RecyclerView recyclerView;

    private final SelfieLog.SelfieLogDetail poSelfie = new SelfieLog.SelfieLogDetail();

    private LoadDialog poLoad;
    private MessageBox poMessage;

    private ActivityResultLauncher<Intent> poCamera;

    private ActivityResultLauncher<String[]> poRequest;

    private final ActivityResultLauncher<Intent> poSettings = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){

            }
        }
    });

    public static Fragment_SelfieLog newInstance() {
        return new Fragment_SelfieLog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(VMSelfieLog.class);
        View view =  inflater.inflate(R.layout.fragment_selfie_log, container, false);
        InitActivityResultLaunchers();
        initWidgets(view);

        btnBranch.setVisibility(View.VISIBLE);

        mViewModel.GetUserInfo().observe(getViewLifecycleOwner(), eEmployeeInfo -> {
            try {
                lblUsername.setText(eEmployeeInfo.sUserName);
                lblPosition.setText(DeptCode.getDepartmentName(eEmployeeInfo.sDeptIDxx));
                lblBranch.setText(eEmployeeInfo.sBranchNm);

            } catch (NullPointerException e){
                e.printStackTrace();
            }
        });

        txtDate.setText(new AppConstants().CURRENT_DATE_WORD);
        txtDate.setOnClickListener(v -> {
            final Calendar newCalendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
            final DatePickerDialog StartTime = new DatePickerDialog(getActivity(), (view131, year, monthOfYear, dayOfMonth) -> {
                try {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    String lsDate = dateFormatter.format(newDate.getTime());
                    txtDate.setText(lsDate);
                    Date loDate = new SimpleDateFormat("MMMM dd, yyyy").parse(lsDate);
                    lsDate = new SimpleDateFormat("yyyy-MM-dd").format(loDate);
                    mViewModel.SetSelectedDate(lsDate);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            StartTime.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            StartTime.show();
        });

        mViewModel.GetSelectedDate().observe(getViewLifecycleOwner(), date -> {
            try{
                mViewModel.getAllEmployeeTimeLog(date).observe(getViewLifecycleOwner(), eLog_selfies -> {
                    TimeLogAdapter logAdapter = new TimeLogAdapter(eLog_selfies, sTransNox -> GToast.CreateMessage(requireActivity(), "Feature not yet implemented", GToast.INFORMATION).show());
                    LinearLayoutManager loManager = new LinearLayoutManager(requireActivity());
                    loManager.setOrientation(RecyclerView.VERTICAL);
                    recyclerView.setLayoutManager(loManager);
                    recyclerView.setAdapter(logAdapter);
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        btnBranch.setOnClickListener(v -> mViewModel.CheckBranchList(new VMSelfieLog.OnBranchCheckListener() {
            @Override
            public void OnCheck() {
                poLoad.initDialog("Selfie Log", "Initializing branch list. Please wait...", false);
                poLoad.show();
            }

            @Override
            public void OnCheck(List<EBranchInfo> area, List<EBranchInfo> all) {
                poLoad.dismiss();
//                Prompt a dialog which will display list of branch per area or all branch
                new DialogBranchSelection(requireActivity(), area, all).initDialog(true, new DialogBranchSelection.OnBranchSelectedCallback() {
                    @Override
                    public void OnSelect(String BranchCode, AlertDialog dialog) {

//                        Upon selection of branch code validate or check if the selected branch code has already exist
                        mViewModel.checkIfAlreadyLog(BranchCode, new VMSelfieLog.OnBranchSelectedCallback() {
                            @Override
                            public void OnLoad() {
                                poLoad.initDialog("Selfie Log", "Validating branch. Please wait...", false);
                                poLoad.show();
                            }

                            @Override
                            public void OnSuccess() {
                                poLoad.dismiss();
                                poSelfie.setBranchCode(BranchCode);
                                mViewModel.getBranchInfo(BranchCode).observe(getViewLifecycleOwner(), eBranchInfo -> {
                                    try{
                                        lblBranch.setText(eBranchInfo.getBranchNm());
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                });
                            }

                            @Override
                            public void OnFailed(String message) {
                                poLoad.dismiss();
                                poMessage.initDialog();
                                poMessage.setTitle("Selfie Login");
                                poMessage.setMessage(message);
                                poMessage.setPositiveButton("Okay", (view1, dialog) -> dialog.dismiss());
                                poMessage.show();
                            }
                        });
                        dialog.dismiss();
                    }

                    @Override
                    public void OnCancel() {

                    }
                });
            }

            @Override
            public void OnFailed(String message) {
                poLoad.dismiss();
            }
        }));

        btnCamera.setOnClickListener(v -> {
            if(checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                poRequest.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA
                });
            } else {
                validateSelfieLog();
            }
        });
        return view;
    }

    private void initWidgets(View view){
        lblUsername = view.findViewById(R.id.lbl_username);
        lblPosition = view.findViewById(R.id.lbl_userPosition);
        lblBranch = view.findViewById(R.id.lbl_userBranch);
        btnCamera = view.findViewById(R.id.btn_takeSelfie);
        btnBranch = view.findViewById(R.id.btn_selectBranch);
        recyclerView = view.findViewById(R.id.recyclerview_timeLog);
        txtDate = view.findViewById(R.id.txt_selfieDate);

        poLoad = new LoadDialog(requireActivity());
        poMessage = new MessageBox(requireActivity());
    }

    @SuppressLint("MissingPermission")
    private void validateSelfieLog(){
        try {
            if(checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                poRequest.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
            } else {
                mViewModel.ValidateSelfieBranch(poSelfie.getBranchCode(), new VMSelfieLog.OnValidateSelfieBranch() {
                    @Override
                    public void OnValidate() {
                        poLoad.initDialog("Selfie Log", "Validating entry. Please wait...", false);
                        poLoad.show();
                    }

                    @Override
                    public void OnSuccess() {
                        poLoad.dismiss();
                        InitCamera("");
                    }

                    @Override
                    public void OnRequireRemarks() {
                        poLoad.dismiss();
                        new DialogSelfieLogRemarks(requireActivity()).initDialog(new DialogSelfieLogRemarks.OnDialogRemarksEntry() {
                            @Override
                            public void OnConfirm(String args) {
                                InitCamera(args);
                            }

                            @Override
                            public void OnCancel() {

                            }
                        });
                    }

                    @Override
                    public void OnFailed(String message) {
                        poLoad.dismiss();
                        poMessage.initDialog();
                        poMessage.setTitle("Selfie Login");
                        poMessage.setMessage(message);
                        poMessage.setPositiveButton("Okay", (view, dialog) -> {
                            dialog.dismiss();
                            requireActivity().finish();
                        });
                        poMessage.show();
                    }
                });
            }
        } catch(RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void InitCamera(String args){
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(requireActivity(), "Please enable your location service.", Toast.LENGTH_SHORT).show();
            return;
        }
        poSelfie.setRemarksx(args);
        mViewModel.InitCameraLaunch(requireActivity(), new OnInitializeCameraCallback() {
            @Override
            public void OnInit() {
                poLoad.initDialog("Selfie Log", "Initializing camera. Please wait...", false);
                poLoad.show();
            }

            @Override
            public void OnSuccess(Intent intent, String[] args) {
                poLoad.dismiss();
                poSelfie.setLocation(args[0]);
                poSelfie.setFileName(args[1]);
                poSelfie.setLatitude(args[2]);
                poSelfie.setLongitude(args[3]);
                poCamera.launch(intent);
            }

            @Override
            public void OnFailed(String message, Intent intent, String[] args) {
                poLoad.dismiss();
                poMessage.initDialog();
                poMessage.setTitle("Selfie Login");
                poMessage.setMessage(message + "\n Proceed taking selfie log?");
                poMessage.setPositiveButton("Continue", (view, dialog) -> {
                    dialog.dismiss();
                    poSelfie.setLocation(args[0]);
                    poSelfie.setFileName(args[1]);
                    poSelfie.setLatitude(args[2]);
                    poSelfie.setLongitude(args[3]);
                    poCamera.launch(intent);
                });
                poMessage.setNegativeButton("Cancel", (view1, dialog) -> dialog.dismiss());
                poMessage.show();
            }
        });
    }

    public void ValidateCashCount(){
        mViewModel.ValidateCashCount(poSelfie.getBranchCode(), new VMSelfieLog.OnValidateCashCount() {
            @Override
            public void OnValidate() {
                poLoad.initDialog("Selfie Log", "Checking cash count entries. Please wait...", false);
                poLoad.show();
            }

            @Override
            public void OnProceed(String args) {
                poLoad.dismiss();
                Intent loIntent = new Intent(requireActivity(), Activity_CashCounter.class);
                loIntent.putExtra("BranchCd", poSelfie.getBranchCode());
                requireActivity().startActivity(loIntent);
                requireActivity().finish();
            }

            @Override
            public void OnWarning(String message) {
                poLoad.dismiss();
                poMessage.initDialog();
                poMessage.setTitle("Selfie Login");
                poMessage.setMessage("A Cash count entry for current branch already exist on local device. Create another entry?");
                poMessage.setPositiveButton("Create", (view, dialog) -> {
                    Intent loIntent = new Intent(requireActivity(), Activity_CashCounter.class);
                    loIntent.putExtra("BranchCd", poSelfie.getBranchCode());
                    requireActivity().startActivity(loIntent);
                    requireActivity().finish();
                });
                poMessage.setNegativeButton("Exit", (view, dialog) -> requireActivity().finish());
                poMessage.show();
            }

            @Override
            public void OnFailed(String message) {
                poLoad.dismiss();
                poMessage.initDialog();
                poMessage.setTitle("Selfie Login");
                poMessage.setMessage(message);
                poMessage.setPositiveButton("Okay", (view, dialog) -> {
                    dialog.dismiss();
                    requireActivity().finish();
                });
                poMessage.show();
            }

            @Override
            public void OnUnauthorize(String message) {
                poLoad.dismiss();
                poMessage.initDialog();
                poMessage.setTitle("Selfie Log");
                poMessage.setMessage("Selfie log save.");
                poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                poMessage.show();
            }
        });
    }

    private void InitActivityResultLaunchers(){
        poCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                try{
                    int resultCode = result.getResultCode();
                    if(resultCode == RESULT_OK){
                        mViewModel.TimeIn(poSelfie, new VMSelfieLog.OnLoginTimekeeperListener() {
                            @Override
                            public void OnLogin() {
                                poLoad.initDialog("Selfie Log", "Sending your time in. Please wait...", false);
                                poLoad.show();
                            }

                            @Override
                            public void OnSuccess(String args) {
                                poLoad.dismiss();
                                ValidateCashCount();
                            }

                            @Override
                            public void SaveOffline(String args) {
                                poLoad.dismiss();
                                ValidateCashCount();
                            }

                            @Override
                            public void OnFailed(String message) {
                                poLoad.dismiss();
                                poMessage.initDialog();
                                poMessage.setTitle("Selfie Log");
                                poMessage.setMessage(message);
                                poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                                poMessage.show();
                            }
                        });
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        poRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    Boolean fineLoct = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseL = result.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    Boolean camerax = result.getOrDefault(
                            Manifest.permission.CAMERA, false);
                    if(Boolean.FALSE.equals(camerax)){
                        Toast.makeText(requireActivity(), "Please allow camera permission to proceed.", Toast.LENGTH_SHORT).show();
                    } else if(Boolean.FALSE.equals(fineLoct)){
                        Toast.makeText(requireActivity(), "Please allow permission to get device location.", Toast.LENGTH_SHORT).show();
                    } else if(Boolean.FALSE.equals(coarseL)){
                        Toast.makeText(requireActivity(), "Please allow permission to get device location.", Toast.LENGTH_SHORT).show();
                    } else {
                        validateSelfieLog();
                    }
                } else {
                    if(checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(requireActivity(), "Please allow camera permission to proceed.", Toast.LENGTH_SHORT).show();
                    } else if(checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(requireActivity(), "Please allow permission to get device location.", Toast.LENGTH_SHORT).show();
                    } else if(checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(requireActivity(), "Please allow permission to get device location.", Toast.LENGTH_SHORT).show();
                    } else {
                        validateSelfieLog();
                    }
                }
            }
        });
    }
}