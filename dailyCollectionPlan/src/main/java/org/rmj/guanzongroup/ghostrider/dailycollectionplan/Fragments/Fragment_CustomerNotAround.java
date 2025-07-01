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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.AddressUpdate;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.CustomerNotAround;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.MobileUpdate;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_Transaction;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Adapter.AddressInfoAdapter;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Adapter.MobileInfoAdapter;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Dialog.DialogDisclosure;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Etc.DCP_Constants;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.OnInitializeCameraCallback;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.VMCustomerNotAround;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.ViewModelCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Fragment_CustomerNotAround extends Fragment {

    private VMCustomerNotAround mViewModel;
    private DialogDisclosure dialogDisclosure;
    private LoadDialog poDialog;
    private MessageBox poMessage;
    private CustomerNotAround poCna;
    private AddressUpdate poAddress;
    private MobileUpdate poMobile;
    private MobileInfoAdapter mobileAdapter;
    private AddressInfoAdapter addressAdapter;

    private MaterialCheckBox cbPrimeContact, cbPrimary;
    private MaterialTextView lblBranch, lblAddress, lblAccNo, lblClientNm, lblClientAddress;
    private RadioGroup rg_CNA_Input, rg_addressType;
    private MaterialRadioButton rb_permanent, rb_present;
    private TextInputEditText txtContact, txtHouseNox, txtAddress, txtRemarks;
    private MaterialAutoCompleteTextView txtTown, txtBrgy, spnRequestCode;
    private LinearLayout lnContactNox,
            lnAddress;
    private MaterialButton btnAdd, btnSubmit;
    private RecyclerView rvCNAOutputs;

    private boolean isMobileToggled = true;

    private String transNo;

    private final ActivityResultLauncher<Intent> poCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            try {

                if(result.getResultCode() == RESULT_OK) {

                    //TODO: 1. GET COORDINATES OF CAPTURED IMAGE'S PROPERTIES,
                    // NOTE: TO GET THIS PROPERLY. ENABLE MANUALLY THE TAG LOCATION SETTINGS ON CAMERA WITHIN THE APP
                    @SuppressLint({"NewApi", "LocalSuppress"}) ExifInterface exifInterface =
                            new ExifInterface(
                                    Objects.requireNonNull(requireContext().getContentResolver().openInputStream(
                                            MediaStore.setRequireOriginal(Uri.fromFile(new File(poCna.getFilePath())))
                                    ))
                            );

                    //TODO: 2. SET IMAGE COORDINATES, IF NOT EMPTY
                    if (exifInterface.getLatLong() != null){

                        Log.d("DCP Fragment", "Image Longitude is " + String.valueOf(Objects.requireNonNull(exifInterface.getLatLong())[1])
                                + " and Image Latitude is " + String.valueOf(exifInterface.getLatLong()[0]));

                        poCna.setLatitude(String.valueOf(exifInterface.getLatLong()[0]));
                        poCna.setLongtude(String.valueOf(exifInterface.getLatLong()[1]));
                    }

                    //TODO: 3. VALIDATE SAVED COORDINATES
                    if (poCna.getLongtude() == null || poCna.getLatitude() == null){
                        InitMessage(0, R.drawable.baseline_error_24, "Unable to get location coordinates. Please inform your superior for this matter.", "Okay", "", new OnDialogButtonCallback() {
                            @Override
                            public void OnPositive() {}

                            @Override
                            public void OnNegative() {}
                        });
                        return;
                    }

                    if (poCna.getLongtude().isEmpty() || poCna.getLatitude().isEmpty()){
                        InitMessage(0, R.drawable.baseline_error_24, "Location coordinates is empty. Please inform your superior for this matter.", "Okay", "", new OnDialogButtonCallback() {
                            @Override
                            public void OnPositive() {}

                            @Override
                            public void OnNegative() {}
                        });
                        return;
                    }

                    if (poCna.getLongtude().equalsIgnoreCase("0.00000000000") || poCna.getLatitude().equalsIgnoreCase("0.00000000000")) {
                        InitMessage(0, R.drawable.baseline_error_24, "Location coordinates is invalid. Please inform your superior for this matter.", "Okay", "", new OnDialogButtonCallback() {
                            @Override
                            public void OnPositive() {}

                            @Override
                            public void OnNegative() {}
                        });
                        return;
                    }

                    mViewModel.SaveTransaction(poCna, new ViewModelCallback() {
                        @Override
                        public void OnStartSaving() {
                            poDialog.initDialog("Selfie Log", "Saving selfie log. Please wait...", false);
                            poDialog.show();
                        }

                        @Override
                        public void OnSuccessResult() {
                            poDialog.dismiss();

                            poMessage.initDialog();
                            poMessage.setIcon(R.drawable.baseline_message_24);
                            poMessage.setTitle("Daily Collection Plan");
                            poMessage.setMessage("Customer not around has been save.");
                            poMessage.setPositiveButton("Okay", (view, dialog) -> {
                                dialog.dismiss();
                                requireActivity().finish();
                            });
                            poMessage.show();
                        }

                        @Override
                        public void OnFailedResult(String message) {
                            poDialog.dismiss();
                            poMessage.initDialog();
                            poMessage.setIcon(R.drawable.baseline_error_24);
                            poMessage.setTitle("Daily Collection Plan");
                            poMessage.setMessage(message);
                            poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                            poMessage.show();
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

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    });

    private final ActivityResultLauncher<Intent> poDialer = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

    });

    private class OnRadioButtonSelectListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(group.getId() == R.id.rg_CnaInput) {
                if(checkedId == R.id.rb_contactNox) {
                    isMobileToggled = true;
                    lnContactNox.setVisibility(View.VISIBLE);
                    lnAddress.setVisibility(View.GONE);
                    spnRequestCode.setSelection(0);
                    rb_permanent.setChecked(false);
                    rb_present.setChecked(false);
                    txtHouseNox.setText("");
                    txtAddress.setText("");
                    txtTown.setText("");
                    txtBrgy.setText("");
                    cbPrimary.setChecked(false);
                    txtRemarks.setText("");
                    InitMobileList();
                } else if(checkedId == R.id.rb_address){
                    isMobileToggled = false;
                    lnContactNox.setVisibility(View.GONE);
                    lnAddress.setVisibility(View.VISIBLE);
                    spnRequestCode.setSelection(0);
                    cbPrimeContact.setChecked(false);
                    txtContact.setText("");
                    txtRemarks.setText("");
                    InitAddressList();
                }
            }
            else if(group.getId() == R.id.rg_address_type) {
                if(checkedId == R.id.rb_permanent) {
                    poAddress.setcAddrssTp("0");
                }
                else if(checkedId == R.id.rb_present) {
                    poAddress.setcAddrssTp("1");
                }
            }
        }
    }

    private class OnCheckboxSetListener implements CheckBox.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(buttonView.isChecked()){
                poMobile.setPrimaryx("1");
                poAddress.setPrimaryStatus("1");
            } else{
                poMobile.setPrimaryx("0");
                poAddress.setPrimaryStatus("0");
            }
        }
    }

    private interface OnDialogButtonCallback{
        void OnPositive();
        void OnNegative();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_customer_not_around_fragment, container, false);
        String AccountN = Activity_Transaction.getInstance().getAccntNox();
        int EntryNox = Activity_Transaction.getInstance().getEntryNox();

        initWidgets(view);

        mViewModel = new ViewModelProvider(requireActivity()).get(VMCustomerNotAround.class);
        dialogDisclosure = new DialogDisclosure(requireActivity());
        poCna = new CustomerNotAround();
        poAddress = new AddressUpdate();
        poMobile = new MobileUpdate();
        poDialog = new LoadDialog(requireActivity());
        poMessage = new MessageBox(requireActivity());

        transNo = Activity_Transaction.getInstance().getTransNox();

        mViewModel.GetUserInfo().observe(getViewLifecycleOwner(), user -> {
            try{
                lblBranch.setText(user.sBranchNm);
                lblAddress.setText(user.sAddressx);
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.GetCollectionDetail(transNo, AccountN, String.valueOf(EntryNox)).observe(getViewLifecycleOwner(), detail -> {
            try{
                poCna.setTransNox(detail.getTransNox());
                poCna.setAccountNo(detail.getAcctNmbr());
                poCna.setEntryNox(String.valueOf(detail.getEntryNox()));

                poAddress.setsClientID(detail.getClientID());
                poMobile.setClientID(detail.getClientID());

                lblAccNo.setText(detail.getAcctNmbr());
                lblClientNm.setText(detail.getFullName());
                lblClientAddress.setText(detail.getAddressx());

                InitMobileList();

            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.getTownProvinceInfo().observe(getViewLifecycleOwner(), townProvinceInfos -> {
            try {
                List<String> loTownList = new ArrayList<>();
                for (int x = 0; x < townProvinceInfos.size(); x++) {
                    loTownList.add(townProvinceInfos.get(x).sTownName + ", " + townProvinceInfos.get(x).sProvName);
                }

                ArrayAdapter<String> loAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, loTownList.toArray(new String[0]));
                txtTown.setAdapter(loAdapter);

                txtTown.setOnItemClickListener((adapterView, v, i, l) -> {
                    String lsTown = txtTown.getText().toString();
                    for (int x = 0; x < townProvinceInfos.size(); x++) {
                        if (lsTown.equalsIgnoreCase(townProvinceInfos.get(x).sTownName + ", " + townProvinceInfos.get(x).sProvName)) {
                            poAddress.setsProvIDxx(townProvinceInfos.get(x).sProvIDxx);
                            poAddress.setTownID(townProvinceInfos.get(x).sTownIDxx);
                            break;
                        }
                    }

                    mViewModel.GetBarangayList(poAddress.getTownID()).observe(getViewLifecycleOwner(), barangays -> {
                        List<String> loBrgyList = new ArrayList<>();
                        for(int x = 0; x < barangays.size(); x++){
                            loBrgyList.add(barangays.get(x).getBrgyName());
                        }
                        ArrayAdapter<String> loBrgy = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, loBrgyList.toArray(new String[0]));
                        txtBrgy.setAdapter(loBrgy);

                        txtBrgy.setOnItemClickListener((adapterView1, v1, i1, l1) -> {
                            for (int x = 0; x < barangays.size(); x++) {
                                if (txtBrgy.getText().toString().equalsIgnoreCase(barangays.get(x).getBrgyName())) {
                                    poAddress.setBarangayID(barangays.get(x).getBrgyIDxx());
                                    break;
                                }
                            }
                        });
                    });
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        spnRequestCode.setAdapter(DCP_Constants.getAdapter(requireActivity(), DCP_Constants.REQUEST_CODE));

        btnAdd.setOnClickListener(view1 -> {
            if(isMobileToggled) {
                poMobile.setMobileNo(txtContact.getText().toString());
                poMobile.setRemarksx(txtRemarks.getText().toString());
                mViewModel.SaveNewMobile(poMobile, new ViewModelCallback() {
                    @Override
                    public void OnStartSaving() {
                        poDialog.initDialog("Customer Not Around", "Saving mobile update. Please wait...", false);
                        poDialog.show();
                    }

                    @Override
                    public void OnSuccessResult() {
                        poDialog.dismiss();
                        Toast.makeText(requireActivity(), "New record save!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnFailedResult(String message) {
                        poDialog.dismiss();
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                poAddress.setHouseNumber(txtHouseNox.getText().toString());
                poAddress.setAddress(txtAddress.getText().toString());
                poAddress.setsRemarksx(txtRemarks.getText().toString());
                mViewModel.SaveNewAddress(poAddress, new ViewModelCallback() {
                    @Override
                    public void OnStartSaving() {
                        poDialog.initDialog("Customer Not Around", "Saving address update. Please wait...", false);
                        poDialog.show();
                    }

                    @Override
                    public void OnSuccessResult() {
                        poDialog.dismiss();
                        Toast.makeText(requireActivity(), "New record save!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnFailedResult(String message) {
                        poDialog.dismiss();
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnSubmit.setOnClickListener(v -> {

            if(txtRemarks.getText().toString().trim().isEmpty()){
                Toast.makeText(requireActivity(), "Please enter remarks", Toast.LENGTH_SHORT).show();
                return;
            }

            poCna.setRemarksx(txtRemarks.getText().toString());

            if(checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                ShowDCPDisclosure();

            } else {
                InitializeCamera();
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
        lblBranch = v.findViewById(R.id.lbl_headerBranch);
        lblAddress = v.findViewById(R.id.lbl_headerAddress);
        lblAccNo = v.findViewById(R.id.lbl_dcpAccNo);
        lblClientAddress = v.findViewById(R.id.lbl_dcpClientAddress);
        lblClientNm = v.findViewById(R.id.lbl_dcpClientNm);

        cbPrimeContact = v.findViewById(R.id.cb_primaryContact);
        cbPrimary = v.findViewById(R.id.cb_primary);
        spnRequestCode = v.findViewById(R.id.spn_requestCode);
        rg_CNA_Input = v.findViewById(R.id.rg_CnaInput);
        rg_addressType = v.findViewById(R.id.rg_address_type);
        rb_permanent = v.findViewById(R.id.rb_permanent);
        rb_present = v.findViewById(R.id.rb_present);
        txtContact = v.findViewById(R.id.txt_dcpCNA_contactNox);
        txtHouseNox = v.findViewById(R.id.txt_houseNox);
        txtAddress = v.findViewById(R.id.txt_address);
        txtTown = v.findViewById(R.id.txt_dcpCNA_town);
        txtBrgy = v.findViewById(R.id.txt_dcpCNA_brgy);
        txtRemarks = v.findViewById(R.id.txt_dcpCNA_remarks);
        btnAdd = v.findViewById(R.id.btn_add);
        btnSubmit = v.findViewById(R.id.btn_submit);

        lnContactNox = v.findViewById(R.id.CNA_Contact);
        lnAddress = v.findViewById(R.id.CNA_Address);

        rvCNAOutputs = v.findViewById(R.id.rv_CNA_outputs);
        rvCNAOutputs.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCNAOutputs.setHasFixedSize(true);

        rg_CNA_Input.setOnCheckedChangeListener(new Fragment_CustomerNotAround.OnRadioButtonSelectListener());
        rg_addressType.setOnCheckedChangeListener(new Fragment_CustomerNotAround.OnRadioButtonSelectListener());
        spnRequestCode.setOnItemClickListener((adapterView, view, i, l) -> {
            poMobile.setReqstCde(String.valueOf(i));
            poAddress.setRequestCode(String.valueOf(i));
        });
        cbPrimary.setOnCheckedChangeListener(new OnCheckboxSetListener());
        cbPrimeContact.setOnCheckedChangeListener(new OnCheckboxSetListener());
    }

    private void InitializeCamera(){

        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(requireActivity(), "Please enable your location service.", Toast.LENGTH_SHORT).show();
            return;
        }

        InitMessage(0, R.drawable.baseline_message_24, "Please take a selfie within the area", "Okay", "", new OnDialogButtonCallback() {
            @Override
            public void OnPositive() {

                mViewModel.InitCameraLaunch(requireActivity(), transNo, new OnInitializeCameraCallback() {
                    @Override
                    public void OnInit() {
                        poDialog.initDialog("Daily Collection Plan", "Initializing camera. Please wait...", false);
                        poDialog.show();
                    }

                    @Override
                    public void OnSuccess(Intent intent, String[] args) {
                        poDialog.dismiss();

                        poCna.setFilePath(args[0]);
                        poCna.setFileName(args[1]);
                        poCna.setLatitude(args[2]);
                        poCna.setLongtude(args[3]);

                        poCamera.launch(intent);
                    }

                    @Override
                    public void OnFailed(String message, Intent intent, String[] args) {
                        poDialog.dismiss();
                        poMessage.initDialog();
                        poMessage.setIcon(R.drawable.baseline_contact_support_24);
                        poMessage.setTitle("Daily Collection Plan");
                        poMessage.setMessage(message + "\n Proceed taking selfie?");
                        poMessage.setPositiveButton("Continue", (view, dialog) -> {
                            dialog.dismiss();

                            poCna.setFilePath(args[0]);
                            poCna.setFileName(args[1]);
                            poCna.setLatitude(args[2]);
                            poCna.setLongtude(args[3]);

                            poCamera.launch(intent);
                        });
                        poMessage.setNegativeButton("Cancel", (view1, dialog) -> dialog.dismiss());
                        poMessage.show();
                    }
                });
            }

            @Override
            public void OnNegative() {}
        });
    }

    private void InitMobileList(){

        mViewModel.GetMobileUpdates(poMobile.getClientID()).observe(getViewLifecycleOwner(), mobileNox -> {
            try {
                mobileAdapter = new MobileInfoAdapter(new MobileInfoAdapter.OnItemInfoClickListener() {
                    @Override
                    public void OnDelete(int position) {

                        mViewModel.RemoveMobile(mobileNox.get(position).sTransNox, new VMCustomerNotAround.OnRemoveDetailCallback() {
                            @Override
                            public void OnSuccess() {
                                Toast.makeText(requireActivity(), "Mobile info remove.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void OnFailed(String message) {
                                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void OnMobileNoClick(String MobileNo) {
                        Intent mobileIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", MobileNo, null));
                        poDialer.launch(mobileIntent);
                    }
                });
                rvCNAOutputs.setAdapter(mobileAdapter);
                mobileAdapter.setMobileNox(mobileNox);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void InitAddressList(){
        mViewModel.GetAddressUpdates(poAddress.getClientID()).observe(getViewLifecycleOwner(), address -> {
            try {
                addressAdapter = new AddressInfoAdapter(position -> mViewModel.RemoveAddress(address.get(position).sTransNox, new VMCustomerNotAround.OnRemoveDetailCallback() {
                    @Override
                    public void OnSuccess() {
                        Toast.makeText(requireActivity(), "Mobile info remove.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnFailed(String message) {
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                }));
                rvCNAOutputs.setAdapter(addressAdapter);
                addressAdapter.setAddress(address);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }
}