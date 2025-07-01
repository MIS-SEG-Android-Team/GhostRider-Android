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
import android.app.AlertDialog;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.Apps.Dcp.model.LRDcp;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.obj.OTH;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DLRDcp;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.OtherRemCode;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_Transaction;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Dialog.DialogDisclosure;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Etc.DCP_Constants;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.OnInitializeCameraCallback;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.VMIncompleteTransaction;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.ViewModelCallback;

import java.io.File;
import java.util.Objects;

public class Fragment_IncTransaction extends Fragment {
    private static final String TAG = Fragment_IncTransaction.class.getSimpleName();

    private VMIncompleteTransaction mViewModel;
    private DialogDisclosure dialogDisclosure;

    private OtherRemCode poRem;
    private LoadDialog poDialog;
    private MessageBox poMessage;

    private MaterialTextView lblFullNme, lblAccount, lblTransact;
    private TextInputEditText txtRemarks;
    private MaterialButton btnPost;

    private String TransNox;
    private int EntryNox;
    private String AccntNox;
    private String Remarksx;

    private final ActivityResultLauncher<Intent> poCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if(result.getResultCode() == RESULT_OK) {

            try {

                //TODO: 1. GET COORDINATES OF CAPTURED IMAGE'S PROPERTIES,
                // NOTE: TO GET THIS PROPERLY. ENABLE MANUALLY THE TAG LOCATION SETTINGS ON CAMERA WITHIN THE APP
                @SuppressLint({"NewApi", "LocalSuppress"}) ExifInterface exifInterface =
                        new ExifInterface(
                                Objects.requireNonNull(requireContext().getContentResolver().openInputStream(
                                        MediaStore.setRequireOriginal(Uri.fromFile(new File(poRem.getFilePath())))
                                ))
                        );

                //TODO: 2. SET IMAGE COORDINATES, IF NOT EMPTY
                if (exifInterface.getLatLong() != null){

                    Log.d("DCP Fragment", "Image Longitude is " + String.valueOf(Objects.requireNonNull(exifInterface.getLatLong())[1])
                            + " and Image Latitude is " + String.valueOf(exifInterface.getLatLong()[0]));

                    poRem.setLatitude(String.valueOf(exifInterface.getLatLong()[0]));
                    poRem.setLongtude(String.valueOf(exifInterface.getLatLong()[1]));
                }

                //TODO: 3. VALIDATE SAVED COORDINATES
                if (poRem.getLongtude() == null || poRem.getLatitude() == null){
                    InitMessage(0, R.drawable.baseline_error_24, "Unable to get location coordinates. Please inform your superior for this matter.", "Okay", "", new OnDialogButtonCallback() {
                        @Override
                        public void OnPositive() {}

                        @Override
                        public void OnNegative() {}
                    });
                    return;
                }

                if (poRem.getLongtude().isEmpty() || poRem.getLatitude().isEmpty()){
                    InitMessage(0, R.drawable.baseline_error_24, "Location coordinates is empty. Please inform your superior for this matter.", "Okay", "", new OnDialogButtonCallback() {
                        @Override
                        public void OnPositive() {}

                        @Override
                        public void OnNegative() {}
                    });
                    return;
                }

                if (poRem.getLongtude().equalsIgnoreCase("0.00000000000") || poRem.getLatitude().equalsIgnoreCase("0.00000000000")) {
                    InitMessage(0, R.drawable.baseline_error_24, "Location coordinates is invalid. Please inform your superior for this matter.", "Okay", "", new OnDialogButtonCallback() {
                        @Override
                        public void OnPositive() {}

                        @Override
                        public void OnNegative() {}
                    });
                    return;
                }

                mViewModel.SaveTransaction(poRem, new ViewModelCallback() {
                    @Override
                    public void OnStartSaving() {
                        poDialog.initDialog("Selfie Log", "Saving transaction. Please wait...", false);
                        poDialog.show();
                    }

                    @Override
                    public void OnSuccessResult() {
                        InitMessage(0, R.drawable.baseline_message_24, "Collection detail has been save.", "Okay", "", new OnDialogButtonCallback() {
                            @Override
                            public void OnPositive() {
                                requireActivity().finish();
                            }
                            @Override
                            public void OnNegative() {}
                        });
                    }

                    @Override
                    public void OnFailedResult(String message) {
                        InitMessage(0, R.drawable.baseline_error_24, message, "Okay", "", new OnDialogButtonCallback() {
                            @Override
                            public void OnPositive() {
                                requireActivity().finish();
                            }
                            @Override
                            public void OnNegative() {}
                        });
                    }
                });

            }catch (Exception e){
                e.printStackTrace();
            }

        }else {

            InitMessage(0, R.drawable.baseline_error_24, "Error capturing image", "Okay", "", new OnDialogButtonCallback() {
                @Override
                public void OnPositive() {}
                @Override
                public void OnNegative() {}
            });
        }
    });

    private interface OnDialogButtonCallback{
        void OnPositive();
        void OnNegative();
    }

    public static Fragment_IncTransaction newInstance() {
        return new Fragment_IncTransaction();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inc_transaction, container, false);

        mViewModel = new ViewModelProvider(this).get(VMIncompleteTransaction.class);
        dialogDisclosure = new DialogDisclosure(requireActivity());
        poDialog = new LoadDialog(requireActivity());
        poMessage = new MessageBox(requireActivity());
        poRem = new OtherRemCode();

        initWidgets(view);

        TransNox = Activity_Transaction.getInstance().getTransNox();
        EntryNox = Activity_Transaction.getInstance().getEntryNox();
        AccntNox = Activity_Transaction.getInstance().getAccntNox();
        Remarksx = Activity_Transaction.getInstance().getRemarksCode();

        mViewModel.GetCollectionDetail(TransNox, AccntNox, String.valueOf(EntryNox)).observe(getViewLifecycleOwner(), detail -> {
            try{

                poRem.setTransNox(TransNox);
                poRem.setAccountNo(AccntNox);
                poRem.setEntryNox(String.valueOf(EntryNox));
                poRem.setRemCodex(DCP_Constants.getRemarksCode(Remarksx));

                lblFullNme.setText(detail.getFullName());
                lblAccount.setText(detail.getAcctNmbr());
                lblTransact.setText(Remarksx);

            } catch (Exception e){
                e.printStackTrace();
            }
        });

        btnPost.setOnClickListener(v -> {

            //todo should not be null to identify transaction
            if (getArguments() != null){

                if (getArguments().getString("transaction").isEmpty()){
                    InitMessage(0, R.drawable.baseline_error_24, "Invalid type. Please select transaction again.", "Okay", "", new OnDialogButtonCallback() {
                        @Override
                        public void OnPositive() {}
                        @Override
                        public void OnNegative() {}
                    });
                    return;
                }

                //todo validate and set remarks, and initialize camera with transaction parameter
                if(txtRemarks.getText().toString().trim().isEmpty()){
                    Toast.makeText(requireActivity(), "Please enter remarks", Toast.LENGTH_SHORT).show();
                    return;
                }
                poRem.setRemarksx(Objects.requireNonNull(txtRemarks.getText()).toString().trim());

                //todo require location and camera permission for selfie log, except for 'Not Visited'
                if (!getArguments().getString("transaction").equalsIgnoreCase("Not Visited")){

                    if(checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                        ShowDCPDisclosure();
                        return;
                    }

                    //todo initialize camera
                    InitializeCamera();
                }else {

                    //todo update collection detail only, no selfie log required
                    EDCPCollectionDetail loDetail = mViewModel.GetCollectionForTransaction(TransNox, AccntNox, String.valueOf(EntryNox));
                    loDetail.setTranStat("1");
                    loDetail.setRemCodex(poRem.getRemCodex());
                    loDetail.setRemarksx(poRem.getRemarksx());
                    loDetail.setModified(AppConstants.DATE_MODIFIED());

                    mViewModel.UpdateCollection(loDetail);

                }

            }else {
                InitMessage(0, R.drawable.baseline_error_24, "Invalid type. Please select transaction again.", "Okay", "", new OnDialogButtonCallback() {
                    @Override
                    public void OnPositive() {}
                    @Override
                    public void OnNegative() {}
                });
            }
        });

        return view;
    }

    private void InitMessage(int messageType, int statusIcon, String message, String posText, String negText, OnDialogButtonCallback callback){

        poMessage.initDialog();
        poMessage.setTitle("Daily Collection Plan");
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

    private void ShowDCPDisclosure(){

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

    private void initWidgets(View v){
        lblFullNme = v.findViewById(R.id.lbl_customerName);
        lblAccount = v.findViewById(R.id.lbl_AccountNo);
        lblTransact = v.findViewById(R.id.lbl_transaction);

        txtRemarks = v.findViewById(R.id.txt_dcpRemarks);

        btnPost = v.findViewById(R.id.btn_dcpConfirm);
    }

    private void InitializeCamera(){

        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(requireActivity(), "Please enable your location service.", Toast.LENGTH_SHORT).show();
            return;
        }

        InitMessage(0, R.drawable.baseline_message_24, "Please take a selfie with the customer or within the area of the customer.", "Okay", "", new OnDialogButtonCallback() {
            @Override
            public void OnPositive() {

                mViewModel.InitCameraLaunch(requireActivity(), TransNox, new OnInitializeCameraCallback() {
                    @Override
                    public void OnInit() {
                        poDialog.initDialog("Daily Collection Plan", "Initializing camera. Please wait...", false);
                        poDialog.show();
                    }

                    @Override
                    public void OnSuccess(Intent intent, String[] args) {
                        poDialog.dismiss();

                        poRem.setFilePath(args[0]);
                        poRem.setFileName(args[1]);
                        poRem.setLatitude(args[2]);
                        poRem.setLongtude(args[3]);

                        poCamera.launch(intent);
                    }

                    @Override
                    public void OnFailed(String message, Intent intent, String[] args) {
                        poDialog.dismiss();

                        InitMessage(1, R.drawable.baseline_contact_support_24, message + "\n Proceed taking selfie?", "Continue", "Cancel", new OnDialogButtonCallback() {
                            @Override
                            public void OnPositive() {

                                poRem.setFilePath(args[0]);
                                poRem.setFileName(args[1]);
                                poRem.setLatitude(args[2]);
                                poRem.setLongtude(args[3]);

                                poCamera.launch(intent);
                            }

                            @Override
                            public void OnNegative() {}
                        });
                    }
                });

            }

            @Override
            public void OnNegative() {}
        });
    }


}