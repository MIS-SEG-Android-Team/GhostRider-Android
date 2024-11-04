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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textview.MaterialTextView;


import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.etc.OnDateSetListener;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.LoanUnit;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_Transaction;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.OnInitializeCameraCallback;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.VMLoanUnit;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.ViewModelCallback;

import java.text.SimpleDateFormat;

public class Fragment_LoanUnit extends Fragment {

    private VMLoanUnit mViewModel;

    private LoanUnit poLUn;

    private LoadDialog poDialog;
    private MessageBox poMessage;

    private MaterialTextView lblBranch,
            lblAddress,
            lblAccNo,
            lblClientNm,
            lblTransNo,
            lblRemCode;
    private TextInputEditText tieLName, tieFName, tieMName, tieSuffix;
    private TextInputEditText tieHouseNo, tieStreet;
    private MaterialAutoCompleteTextView tieTown, tieBrgy,tieBPlace;
    private RadioGroup rgGender;
    private TextInputEditText tieBDate, tiePhone, tieMobileNo, tieEmailAdd,tieRemarks;
    private MaterialButton btnSave;
    private MaterialAutoCompleteTextView spnCivilStats;

    private String TransNox, Remarksx, AccntNox;
    int EntryNox;

    ActivityResultLauncher<String[]> poRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> InitializeCamera());

    private final ActivityResultLauncher<Intent> poCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK) {
            SaveLoanUnit();
        }
    });

    public static Fragment_LoanUnit newInstance() {
        return new Fragment_LoanUnit();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(VMLoanUnit.class);
        poLUn = new LoanUnit();
        poDialog = new LoadDialog(requireActivity());
        poMessage = new MessageBox(requireActivity());
        View view = inflater.inflate(R.layout.fragment_loan_unit, container, false);
        Remarksx = Activity_Transaction.getInstance().getRemarksCode();
        TransNox = Activity_Transaction.getInstance().getTransNox();
        EntryNox = Activity_Transaction.getInstance().getEntryNox();
        AccntNox = Activity_Transaction.getInstance().getAccntNox();
        initWidgets(view);

        mViewModel.GetUserInfo().observe(getViewLifecycleOwner(), user -> {
            try{
                lblBranch.setText(user.sBranchNm);
                lblAddress.setText(user.sAddressx);
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.GetCollectionDetail(TransNox, AccntNox, String.valueOf(EntryNox)).observe(getViewLifecycleOwner(), detail -> {
            try{
                poLUn.setTransNox(TransNox);
                poLUn.setEntryNox(String.valueOf(EntryNox));
                poLUn.setAccntNox(AccntNox);

                lblAccNo.setText(detail.getAcctNmbr());
                lblClientNm.setText(detail.getFullName());
                lblTransNo.setText(detail.getTransNox());
                tieRemarks.setText(detail.getRemarksx());
                lblRemCode.setText(Remarksx);
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.getSpnCivilStats().observe(getViewLifecycleOwner(), stringArrayAdapter ->{
            spnCivilStats.setAdapter(stringArrayAdapter);
        });

        mViewModel.getTownProvinceInfo().observe(getViewLifecycleOwner(), townProvinceInfos -> {
            String[] townProvince = new String[townProvinceInfos.size()];
            for(int x = 0; x < townProvinceInfos.size(); x++){
                townProvince[x] = townProvinceInfos.get(x).sTownName + ", " + townProvinceInfos.get(x).sProvName;
            }
            ArrayAdapter<String> loAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, townProvince);
            tieTown.setAdapter(loAdapter);
            tieBPlace.setAdapter(loAdapter);
        });

        tieTown.setOnItemClickListener((adapterView, v, i, l) -> {
            String lsTown = tieTown.getText().toString();
            String[] town = lsTown.split(", ");
            mViewModel.getTownProvinceInfo().observe(getViewLifecycleOwner(), townProvinceInfos -> {
                for(int x = 0; x < townProvinceInfos.size(); x++){
                    if(town[0].equalsIgnoreCase(townProvinceInfos.get(x).sTownName)){
                        poLUn.setTownIDxx(townProvinceInfos.get(x).sTownIDxx);
                        break;
                    }
                }

                mViewModel.getBarangayNameList(poLUn.getTownIDxx()).observe(getViewLifecycleOwner(), strings -> {
                    ArrayAdapter<String> loAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, strings);
                    tieBrgy.setAdapter(loAdapter);
                });
            });
        });

        tieBrgy.setOnItemClickListener((adapterView, v, i, l) -> mViewModel.getBarangayInfoList(poLUn.getTownIDxx()).observe(getViewLifecycleOwner(), eBarangayInfos -> {
            for(int x = 0; x < eBarangayInfos.size(); x++){
                if(tieBrgy.getText().toString().equalsIgnoreCase(eBarangayInfos.get(x).getBrgyName())){
                    poLUn.setBrgyIDxx(eBarangayInfos.get(x).getBrgyIDxx());
                    break;
                }
            }
        }));


        tieBPlace.setOnItemClickListener((adapterView, v, i, l) -> {
            String lsTown = tieTown.getText().toString();
            String[] town = lsTown.split(", ");
            mViewModel.getTownProvinceInfo().observe(getViewLifecycleOwner(), townProvinceInfos -> {
                for(int x = 0; x < townProvinceInfos.size(); x++){
                    if(town[0].equalsIgnoreCase(townProvinceInfos.get(x).sTownName)){
                        poLUn.setBirthPlc(townProvinceInfos.get(x).sTownIDxx);
                        break;
                    }
                }

                mViewModel.getBarangayNameList(poLUn.getTownIDxx()).observe(getViewLifecycleOwner(), strings -> {
                    ArrayAdapter<String> loAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, strings);
                    tieBrgy.setAdapter(loAdapter);
                });
            });
        });

        spnCivilStats.setOnItemClickListener(new Fragment_LoanUnit.OnItemClickListener(spnCivilStats));

        tieBDate.addTextChangedListener(new OnDateSetListener(tieBDate));

        rgGender.setOnCheckedChangeListener((radioGroup, i) -> {
            if(i == R.id.rb_male){
                poLUn.setGenderxx("0");
            }
            if(i == R.id.rb_female){
                poLUn.setGenderxx("1");
            }
            if(i == R.id.rb_lgbt){
                poLUn.setGenderxx("2");
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
        lblRemCode = v.findViewById(R.id.lblRemCode);
//        Full Name
        tieLName = v.findViewById(R.id.tie_lun_lName);
        tieFName = v.findViewById(R.id.tie_lun_fName);
        tieMName = v.findViewById(R.id.tie_lun_middName);
        tieSuffix = v.findViewById(R.id.tie_lun_suffix);
        tieHouseNo = v.findViewById(R.id.tie_lun_houseNo);
        tieStreet = v.findViewById(R.id.tie_lun_street);
        tieTown = v.findViewById(R.id.tie_lun_town);
        tieBrgy = v.findViewById(R.id.tie_lun_brgy);
        rgGender = v.findViewById(R.id.rg_dcp_gender);
        spnCivilStats = v.findViewById(R.id.spn_lun_cstatus);

        tieBDate = v.findViewById(R.id.tie_lun_bdate);
        tieBPlace = v.findViewById(R.id.tie_lun_bplace);
        tiePhone = v.findViewById(R.id.tie_lun_phone);
        tieMobileNo = v.findViewById(R.id.tie_lun_mobileNp);
        tieEmailAdd = v.findViewById(R.id.tie_lun_email);
        tieRemarks = v.findViewById(R.id.tie_lun_Remarks);
        btnSave = v.findViewById(R.id.btn_dcpSubmit);

        btnSave.setOnClickListener(view -> {
            if(tieRemarks.getText().toString().trim().isEmpty()){
                Toast.makeText(requireActivity(), "Please enter remarks", Toast.LENGTH_SHORT).show();
                return;
            } else if(tieLName.getText().toString().trim().isEmpty()){
                Toast.makeText(requireActivity(), "Please enter last name", Toast.LENGTH_SHORT).show();
                return;
            } else if(tieFName.getText().toString().trim().isEmpty()){
                Toast.makeText(requireActivity(), "Please enter first name", Toast.LENGTH_SHORT).show();
                return;
            } else if(tieMobileNo.getText().toString().trim().isEmpty()){
                Toast.makeText(requireActivity(), "Please enter mobile no.", Toast.LENGTH_SHORT).show();
                return;
            }

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
    }

    class OnItemClickListener implements AdapterView.OnItemClickListener {
        AutoCompleteTextView poView;
        public OnItemClickListener(AutoCompleteTextView view) {
            this.poView = view;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (spnCivilStats.equals(poView)) {
                poLUn.setCvilStat(String.valueOf(i));
            }
        }
    }

    private void SaveLoanUnit() {
        poLUn.setRemarksx(tieRemarks.getText().toString());
        poLUn.setLastName(tieLName.getText().toString());
        poLUn.setFrstName(tieFName.getText().toString());
        poLUn.setMiddName(tieMName.getText().toString());
        poLUn.setSuffixxx(tieSuffix.getText().toString());
        poLUn.setBirthDte(new SimpleDateFormat("MM/dd/yyyy").format(tieBDate.getText().toString()));
        poLUn.setHouseNox(tieHouseNo.getText().toString());
        poLUn.setStreetxx(tieStreet.getText().toString());
        poLUn.setPhoneNox(tiePhone.getText().toString());
        poLUn.setMobileNo(tieMobileNo.getText().toString());
        poLUn.setEmailAdd(tieEmailAdd.getText().toString());

        mViewModel.SaveTransaction(poLUn, new ViewModelCallback() {
            @Override
            public void OnStartSaving() {
                poDialog.initDialog("Selfie Log", "Saving promise to pay. Please wait...", false);
                poDialog.show();
            }

            @Override
            public void OnSuccessResult() {
                poMessage.initDialog();
                poMessage.setIcon(R.drawable.baseline_message_24);
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
                poMessage.setIcon(R.drawable.baseline_error_24);
                poMessage.setTitle("Daily Collection Plan");
                poMessage.setMessage(message);
                poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                poMessage.show();
            }
        });
    }

    private void InitializeCamera(){
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(requireActivity(), "Please enable your location service.", Toast.LENGTH_SHORT).show();
            return;
        }
        mViewModel.InitCameraLaunch(requireActivity(), TransNox, new OnInitializeCameraCallback() {
            @Override
            public void OnInit() {
                poDialog.initDialog("Selfie Log", "Initializing camera. Please wait...", false);
                poDialog.show();
            }

            @Override
            public void OnSuccess(Intent intent, String[] args) {
                poDialog.dismiss();
                poLUn.setFilePath(args[0]);
                poLUn.setFileName(args[1]);
                poLUn.setLatitude(args[2]);
                poLUn.setLongtude(args[3]);
                poCamera.launch(intent);
            }

            @Override
            public void OnFailed(String message, Intent intent, String[] args) {
                poDialog.dismiss();
                poMessage.initDialog();
                poMessage.setIcon(R.drawable.baseline_contact_support_24);
                poMessage.setTitle("Daily Collection Plan");
                poMessage.setMessage(message + "\n Proceed taking selfie log?");
                poMessage.setPositiveButton("Continue", (view, dialog) -> {
                    dialog.dismiss();
                    poLUn.setFilePath(args[0]);
                    poLUn.setFileName(args[1]);
                    poLUn.setLatitude(args[2]);
                    poLUn.setLongtude(args[3]);
                    poCamera.launch(intent);
                });
                poMessage.setNegativeButton("Cancel", (view1, dialog) -> dialog.dismiss());
                poMessage.show();
            }
        });
    }
}