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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.etc.OnInitializeCameraCallback;
import org.rmj.g3appdriver.GCircle.Apps.SelfieLog.SelfieLog;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Activity.Activity_CashCounter;
import org.rmj.guanzongroup.petmanager.Adapter.TimeLogAdapter;
import org.rmj.guanzongroup.petmanager.Dialog.DialogBranchSelection;
import org.rmj.guanzongroup.petmanager.Dialog.DialogDisclosure;
import org.rmj.guanzongroup.petmanager.Dialog.DialogSelfieLogRemarks;
import org.rmj.guanzongroup.petmanager.R;
import org.rmj.guanzongroup.petmanager.ViewModel.VMSelfieLog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Fragment_SelfieLog extends Fragment {
    private static final String TAG = Fragment_SelfieLog.class.getSimpleName();

    private VMSelfieLog mViewModel;

    private MaterialTextView lblBranch;
    private TextInputEditText txtDate;
    private MaterialButton btnCamera, btnBranch;
    private RecyclerView recyclerView;

    private final SelfieLog.SelfieLogDetail poSelfie = new SelfieLog.SelfieLogDetail();

    private LoadDialog poLoad;
    private MessageBox poMessage;

    private DialogDisclosure dialogDisclosure;

    private ActivityResultLauncher<Intent> poCamera;

    private interface OnDialogButtonCallback{
        void OnPositive();
        void OnNegative();
    }

    public static Fragment_SelfieLog newInstance() {
        return new Fragment_SelfieLog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_selfie_log, container, false);

        mViewModel = new ViewModelProvider(this).get(VMSelfieLog.class);

        dialogDisclosure = new DialogDisclosure(requireActivity());

        InitActivityResultLaunchers();
        initWidgets(view);

        btnBranch.setVisibility(View.VISIBLE);

        mViewModel.GetUserInfo().observe(getViewLifecycleOwner(), eEmployeeInfo -> {
            try {
                //todo set user branch by default
                lblBranch.setText(eEmployeeInfo.sBranchNm);
                poSelfie.setBranchCode(eEmployeeInfo.sBranchCd);

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

                                InitMessage(0, R.drawable.baseline_error_24, message, "Okay", "", new OnDialogButtonCallback() {
                                    @Override
                                    public void OnPositive() {}
                                    @Override
                                    public void OnNegative() {}
                                });
                            }
                        });
                        dialog.dismiss();
                    }

                    @Override
                    public void OnCancel() {}
                });
            }

            @Override
            public void OnFailed(String message) {
                poLoad.dismiss();
            }
        }));

        btnCamera.setOnClickListener(v -> {

            if(checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                showDCPDisclosure();

            } else {
                validateSelfieLog();
            }
        });

        mViewModel.GetSelectedDate().observe(getViewLifecycleOwner(), date -> {
            try{
                mViewModel.GetTimeLogForTheDay(date).observe(getViewLifecycleOwner(), eLog_selfies -> {
                    TimeLogAdapter logAdapter = new TimeLogAdapter(eLog_selfies, new TimeLogAdapter.OnTimeLogActionListener() {
                        @Override
                        public void OnImagePreview(String sTransNox) {

                        }

                        @Override
                        public void OnClickResend(String TransNox) {
                            mViewModel.ResendTimeIn(TransNox, new VMSelfieLog.OnLoginTimekeeperListener() {
                                @Override
                                public void OnLogin() {
                                    poLoad.initDialog("Selfie Log", "Sending your selfie log. Please wait...", false);
                                    poLoad.show();
                                }

                                @Override
                                public void OnSuccess(String args) {
                                    poLoad.dismiss();
                                }

                                @Override
                                public void SaveOffline(String args) {}

                                @Override
                                public void OnFailed(String message) {
                                    poLoad.dismiss();

                                    InitMessage(0, R.drawable.baseline_error_24, message, "Okay", "", new OnDialogButtonCallback() {
                                        @Override
                                        public void OnPositive() {}
                                        @Override
                                        public void OnNegative() {}
                                    });
                                }
                            });
                        }
                    });
                    LinearLayoutManager loManager = new LinearLayoutManager(requireActivity());
                    loManager.setOrientation(RecyclerView.VERTICAL);

                    recyclerView.setLayoutManager(loManager);
                    recyclerView.setAdapter(logAdapter);
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        return view;
    }

    private void InitMessage(int messageType, int statusIcon, String message, String posText, String negText, OnDialogButtonCallback callback){

        poMessage.initDialog();
        poMessage.setTitle("Selfie Login");
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

    private void showDCPDisclosure(){

        dialogDisclosure.initDialog(new DialogDisclosure.onDisclosure() {
            @Override
            public void onAccept() {
                dialogDisclosure.dismiss();

                if (checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                    Intent appSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    appSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    appSettings.setData(Uri.parse("package:" + requireActivity().getPackageName()));
                    startActivity(appSettings);

                }
            }

            @Override
            public void onDecline() {
                dialogDisclosure.dismiss();

                MessageBox loMessage = new MessageBox(requireActivity());
                loMessage.setIcon(R.drawable.baseline_error_24);
                loMessage.initDialog();
                loMessage.setTitle("Disclosure");
                loMessage.setMessage("Disclosure denied. Selfie log cancelled.");
                loMessage.setPositiveButton("Okay", new MessageBox.DialogButton() {
                    @Override
                    public void OnButtonClick(View view, AlertDialog dialog) {
                        dialog.dismiss();
                    }
                });

                loMessage.show();
            }
        });

        dialogDisclosure.setMessage("Guanzon Circle requires location and camera permission to take selfie log when the app is in use.");
        dialogDisclosure.show();
    }

    private void initWidgets(View view){
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
                        public void OnCancel() {}
                    });
                }

                @Override
                public void OnFailed(String message) {
                    poLoad.dismiss();

                    InitMessage(0, R.drawable.baseline_error_24, message, "Okay", "", new OnDialogButtonCallback() {
                        @Override
                        public void OnPositive() {}
                        @Override
                        public void OnNegative() {}
                    });
                }
            });
        } catch(RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void InitCamera(String args){

        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(requireActivity(), "Please enable your location service.", Toast.LENGTH_LONG).show();
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
                poSelfie.setLongitude(args[2]);
                poSelfie.setLatitude(args[3]);

                Log.d(TAG, "Device Longitude is " + String.valueOf(args[2])
                        + " and Device Latitude is " + String.valueOf(args[3]));

                poCamera.launch(intent);
            }

            @Override
            public void OnFailed(String message, Intent intent, String[] args) {
                poLoad.dismiss();

                InitMessage(1, R.drawable.baseline_error_24, message + "\n Proceed taking selfie log?", "Continue", "Cancel", new OnDialogButtonCallback() {
                    @Override
                    public void OnPositive() {
                        poSelfie.setLocation(args[0]);
                        poSelfie.setFileName(args[1]);
                        poSelfie.setLongitude(args[2]);
                        poSelfie.setLatitude(args[3]);

                        poCamera.launch(intent);
                    }

                    @Override
                    public void OnNegative() {}
                });
            }
        });
    }

    private void ValidateCashCount(){

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

                if(!requireActivity().getClass().getSimpleName().equalsIgnoreCase("Activity_Main")) {
                    requireActivity().finish();
                }
            }

            @Override
            public void OnWarning(String message) {
                poLoad.dismiss();

                InitMessage(1, R.drawable.baseline_contact_support_24, "A Cash count entry for current branch already exist on local device. Create another entry?",
                        "Create", "Exit", new OnDialogButtonCallback() {
                            @Override
                            public void OnPositive() {
                                Intent loIntent = new Intent(requireActivity(), Activity_CashCounter.class);

                                loIntent.putExtra("BranchCd", poSelfie.getBranchCode());
                                requireActivity().startActivity(loIntent);

                                if(!requireActivity().getClass().getSimpleName().equalsIgnoreCase("Activity_Main")) {
                                    requireActivity().finish();
                                }
                            }

                            @Override
                            public void OnNegative() {
                                if(!requireActivity().getClass().getSimpleName().equalsIgnoreCase("Activity_Main")) {
                                    requireActivity().finish();
                                }
                            }
                        });
            }

            @Override
            public void OnFailed(String message) {
                poLoad.dismiss();

                InitMessage(0, R.drawable.baseline_error_24, message, "Okay", "", new OnDialogButtonCallback() {
                    @Override
                    public void OnPositive() {
                        if(!requireActivity().getClass().getSimpleName().equalsIgnoreCase("Activity_Main")) {
                            requireActivity().finish();
                        }
                    }

                    @Override
                    public void OnNegative() {}
                });
            }

            @Override
            public void OnUnauthorize(String message) {
                poLoad.dismiss();

                InitMessage(0, R.drawable.baseline_message_24, "Selfie log save.", "Okay", "", new OnDialogButtonCallback() {
                    @Override
                    public void OnPositive() {}
                    @Override
                    public void OnNegative() {}
                });
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

                        //TODO: 1. GET COORDINATES OF CAPTURED IMAGE'S PROPERTIES,
                        // NOTE: TO GET THIS PROPERLY. ENABLE MANUALLY THE TAG LOCATION SETTINGS ON CAMERA WITHIN THE APP
                        @SuppressLint({"NewApi", "LocalSuppress"}) ExifInterface exifInterface =
                                new ExifInterface(
                                        Objects.requireNonNull(requireContext().getContentResolver().openInputStream(
                                                MediaStore.setRequireOriginal(Uri.fromFile(new File(poSelfie.getFileLocation())))
                                        ))
                                );

                        //TODO: 2. SET IMAGE COORDINATES, IF NOT EMPTY
                        if (exifInterface.getLatLong() != null){

                            Log.d(TAG, "Image Longitude is " + String.valueOf(Objects.requireNonNull(exifInterface.getLatLong())[1])
                                    + " and Image Latitude is " + String.valueOf(exifInterface.getLatLong()[0]));

                            poSelfie.setLatitude(String.valueOf(exifInterface.getLatLong()[0]));
                            poSelfie.setLongitude(String.valueOf(exifInterface.getLatLong()[1]));
                        }

                        //TODO: 3. VALIDATE SAVED COORDINATES
                        if (poSelfie.getLongitude() == null || poSelfie.getLatitude() == null){
                            InitMessage(0, R.drawable.baseline_error_24, "Unable to get location coordinates. Please inform your superior for this matter.", "Okay", "", new OnDialogButtonCallback() {
                                @Override
                                public void OnPositive() {}

                                @Override
                                public void OnNegative() {}
                            });
                            return;
                        }

                        if (poSelfie.getLongitude().isEmpty() || poSelfie.getLatitude().isEmpty()){
                            InitMessage(0, R.drawable.baseline_error_24, "Location coordinates is empty. Please inform your superior for this matter.", "Okay", "", new OnDialogButtonCallback() {
                                @Override
                                public void OnPositive() {}

                                @Override
                                public void OnNegative() {}
                            });
                            return;
                        }

                        if (poSelfie.getLongitude().equalsIgnoreCase("0.00000000000") || poSelfie.getLatitude().equalsIgnoreCase("0.00000000000")) {
                            InitMessage(0, R.drawable.baseline_error_24, "Location coordinates is invalid. Please inform your superior for this matter.", "Okay", "", new OnDialogButtonCallback() {
                                @Override
                                public void OnPositive() {}

                                @Override
                                public void OnNegative() {}
                            });
                            return;
                        }

                        //TODO: 4. PROCEED TO TIME IN
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

                                InitMessage(0, R.drawable.baseline_error_24, message, "Okay", "", new OnDialogButtonCallback() {
                                    @Override
                                    public void OnPositive() {}

                                    @Override
                                    public void OnNegative() {}
                                });
                            }
                        });
                    }else {
                        InitMessage(0, R.drawable.baseline_error_24, "Error capturing image", "Okay", "", new OnDialogButtonCallback() {
                            @Override
                            public void OnPositive() {}
                            @Override
                            public void OnNegative() {}
                        });
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}