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
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import  com.google.android.material.checkbox.MaterialCheckBox;


import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.lib.integsys.Dcp.model.AddressUpdate;
import org.rmj.g3appdriver.lib.integsys.Dcp.model.CustomerNotAround;
import org.rmj.g3appdriver.lib.integsys.Dcp.model.MobileUpdate;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_Transaction;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Adapter.AddressInfoAdapter;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Adapter.MobileInfoAdapter;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.OnInitializeCameraCallback;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.VMCustomerNotAround;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.ViewModelCallback;

import java.util.ArrayList;
import java.util.List;

public class Fragment_CustomerNotAround extends Fragment {

    private VMCustomerNotAround mViewModel;
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

    ActivityResultLauncher<String[]> poRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> InitializeCamera());

    private final ActivityResultLauncher<Intent> poCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK) {
                mViewModel.SaveTransaction(poCna, new ViewModelCallback() {
                    @Override
                    public void OnStartSaving() {
                        poDialog.initDialog("Selfie Log", "Saving promise to pay. Please wait...", false);
                        poDialog.show();
                    }

                    @Override
                    public void OnSuccessResult() {
                        poDialog.dismiss();
                        poMessage.initDialog();
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
                        poMessage.setTitle("Daily Collection Plan");
                        poMessage.setMessage(message);
                        poMessage.setPositiveButton("Okay", (view, dialog) -> dialog.dismiss());
                        poMessage.show();
                    }
                });
            }
        }
    });

    private final ActivityResultLauncher<Intent> poDialer = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK){

        } else {

        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(VMCustomerNotAround.class);
        View view = inflater.inflate(R.layout.fragment_customer_not_around_fragment, container, false);
        poCna = new CustomerNotAround();
        poAddress = new AddressUpdate();
        poMobile = new MobileUpdate();
        poDialog = new LoadDialog(requireActivity());
        poMessage = new MessageBox(requireActivity());

        transNo = Activity_Transaction.getInstance().getTransNox();
        String AccountN = Activity_Transaction.getInstance().getAccntNox();
        int EntryNox = Activity_Transaction.getInstance().getEntryNox();
        initWidgets(view);

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

        mViewModel.getRequestCodeOptions().observe(getViewLifecycleOwner(), stringArrayAdapter -> {
            spnRequestCode.setAdapter(stringArrayAdapter);
            spnRequestCode.setDropDownBackgroundResource(R.drawable.bg_gradient_light);
        });

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
        });
        poMessage.setNegativeButton("Cancel", (view, dialog) -> dialog.dismiss());
        poMessage.show();
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