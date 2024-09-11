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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.OtherRemCode;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_Transaction;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Etc.DCP_Constants;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.OnInitializeCameraCallback;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.VMIncompleteTransaction;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.ViewModelCallback;

import java.util.Objects;

public class Fragment_IncTransaction extends Fragment {
    private static final String TAG = Fragment_IncTransaction.class.getSimpleName();

    private VMIncompleteTransaction mViewModel;

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

    private ActivityResultLauncher<String[]> poRequest;

    private ActivityResultLauncher<Intent> poCamera;

    public static Fragment_IncTransaction newInstance() {
        return new Fragment_IncTransaction();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(VMIncompleteTransaction.class);
        poDialog = new LoadDialog(requireActivity());
        poMessage = new MessageBox(requireActivity());
        poRem = new OtherRemCode();

        View view = inflater.inflate(R.layout.fragment_inc_transaction, container, false);
        initWidgets(view);
        initActivityResultLaunchers();

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
            if(txtRemarks.getText().toString().trim().isEmpty()){
                Toast.makeText(requireActivity(), "Please enter remarks", Toast.LENGTH_SHORT).show();
                return;
            }

            poRem.setRemarksx(Objects.requireNonNull(txtRemarks.getText()).toString().trim());
            if(checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                poRequest.launch(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA});
            } else {
                InitializeCamera();
            }
        });

        return view;
    }

    private void initWidgets(View v){
        lblFullNme = v.findViewById(R.id.lbl_customerName);
        lblAccount = v.findViewById(R.id.lbl_AccountNo);
        lblTransact = v.findViewById(R.id.lbl_transaction);

        txtRemarks = v.findViewById(R.id.txt_dcpRemarks);

        btnPost = v.findViewById(R.id.btn_dcpConfirm);
    }

    private void initActivityResultLaunchers(){
        poRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> InitializeCamera());

        poCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK) {
                mViewModel.SaveTransaction(poRem, new ViewModelCallback() {
                    @Override
                    public void OnStartSaving() {
                        poDialog.initDialog("Selfie Log", "Saving promise to pay. Please wait...", false);
                        poDialog.show();
                    }

                    @Override
                    public void OnSuccessResult() {
                        poMessage.initDialog();
                        poMessage.setTitle("Daily Collection Plan");
                        poMessage.setMessage("Collection detail has been save.");
                        poMessage.setPositiveButton("Okay", (view, dialog) -> {
                            dialog.dismiss();
                            requireActivity().finish();
                        });
                        poMessage.show();
                    }

                    @Override
                    public void OnFailedResult(String message) {
                        poMessage.initDialog();
                        poMessage.setTitle("Daily Collection Plan");
                        poMessage.setMessage(message);
                        poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                        poMessage.show();
                    }
                });
            }
        });
    }

    private void InitializeCamera(){
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(requireActivity(), "Please enable your location service.", Toast.LENGTH_SHORT).show();
            return;
        }
        poMessage.initDialog();
        poMessage.setTitle("Daily Collection Plan");
        poMessage.setMessage("Please take a selfie with the customer or within the area of the customer.");
        poMessage.setPositiveButton("Okay", (view, dialog) -> {
            dialog.dismiss();
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
                    poMessage.initDialog();
                    poMessage.setTitle("Daily Collection Plan");
                    poMessage.setMessage(message + "\n Proceed taking selfie?");
                    poMessage.setPositiveButton("Continue", (view, dialog) -> {
                        dialog.dismiss();
                        poRem.setFilePath(args[0]);
                        poRem.setFileName(args[1]);
                        poRem.setLatitude(args[2]);
                        poRem.setLongtude(args[3]);
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