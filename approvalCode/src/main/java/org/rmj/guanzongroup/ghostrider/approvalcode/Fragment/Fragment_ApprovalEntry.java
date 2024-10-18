/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.approvalCode
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.approvalcode.Fragment;

import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;


import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.utils.CopyToClipboard;
import org.rmj.guanzongroup.ghostrider.approvalcode.Activity.Activity_ApprovalCode;
import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.pojo.AppCodeParams;
import org.rmj.guanzongroup.ghostrider.approvalcode.R;
import org.rmj.guanzongroup.ghostrider.approvalcode.ViewModel.VMApprovalEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class Fragment_ApprovalEntry extends Fragment {

    private VMApprovalEntry mViewModel;

    private MessageBox poMesgBox;
    private LoadDialog poLoad;

    private AppCodeParams poEntryxx;

    private String psSysType, psSysCode, psSCAType;

    private AutoCompleteTextView txtBranch;
    private TextInputLayout tilReferNo;
    private TextInputEditText txtDate, txtReferNo, txtLastNm, txtFrstNm, txtMiddNm, txtSuffix, txtRemarks,txtAppCode;
    private MaterialTextView lblAppv;
    private LinearLayout lnFullNm;
    private MaterialButton btnCreate, btnCopy;

    private boolean createNew = true;

    public static Fragment_ApprovalEntry newInstance() {
        return new Fragment_ApprovalEntry();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(VMApprovalEntry.class);
        View v = inflater.inflate(R.layout.fragment_approval_entry, container, false);
        poEntryxx = new AppCodeParams();
        poMesgBox = new MessageBox(requireActivity());
        poLoad = new LoadDialog(requireActivity());
        psSysType = Activity_ApprovalCode.getInstance().getSystemType();
        psSysCode = Activity_ApprovalCode.getInstance().getSystemCode();
        psSCAType = Activity_ApprovalCode.getInstance().getPsSCAType();
        poEntryxx.setSysTypex(psSysType);
        poEntryxx.setSystemCd(psSysCode);
        poEntryxx.setSCAType(psSCAType);
        initWidgets(v);

        txtDate.setInputType(InputType.TYPE_NULL);
        txtDate.setOnFocusChangeListener((view, b) -> {
            if(b){
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                DatePickerDialog picker = new DatePickerDialog(getActivity(),
                        (view1, year1, monthOfYear, dayOfMonth) -> {
                            String lsMonth = String.valueOf(monthOfYear + 1);
                            String lsDay = String.valueOf(dayOfMonth);

                            if ((monthOfYear + 1) < 10) lsMonth = "0" + (monthOfYear + 1);
                            if (dayOfMonth < 10) lsDay = "0" + dayOfMonth;

                            txtDate.setText(year1 + "-"+ lsMonth + "-" + lsDay);
                        }, year, month, day);
                picker.show();
            }
        });

        switch (psSysType){
            case "1":
                lnFullNm.setVisibility(View.GONE);
                break;
            case "2":
                tilReferNo.setVisibility(View.GONE);
                break;
        }

        btnCopy.setOnClickListener(view -> {
            String KnoxPin = txtAppCode.getText().toString();
            String message;
            if (!KnoxPin.isEmpty()) {
                new CopyToClipboard(getActivity()).CopyTextClip("Knox_Pin", KnoxPin);
                message = "Approval code copied to clipboard.";
            } else {
                message = "Unable to copy empty content.";
            }
            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
        });

        mViewModel.GetApprovalCodeDescription(psSysCode).observe(getViewLifecycleOwner(), s -> {
            try {
                lblAppv.setText(s);
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.GetAllBranchInfo().observe(getViewLifecycleOwner(), eBranchInfos -> {
            try{
                ArrayList<String> loNames = new ArrayList<>();
                for(int x = 0; x < eBranchInfos.size(); x++){
                    loNames.add(eBranchInfos.get(x).getBranchNm());
                }

                ArrayAdapter<String> loAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, loNames.toArray(new String[0]));
                txtBranch.setAdapter(loAdapter);

                txtBranch.setOnItemClickListener((parent, view, position, id) -> {
                    for(int x = 0; x < eBranchInfos.size(); x++){
                        loNames.add(eBranchInfos.get(x).getBranchNm());
                        if(txtBranch.getText().toString().equalsIgnoreCase(eBranchInfos.get(x).getBranchNm())){
                            poEntryxx.setBranchCd(eBranchInfos.get(x).getBranchCd());
                            break;
                        }
                    }
                });

            } catch (Exception e){
                e.printStackTrace();
            }
        });

        btnCreate.setOnClickListener(view -> {
            if(!createNew){
                poMesgBox.initDialog();
                poMesgBox.setIcon(R.drawable.baseline_contact_support_24);
                poMesgBox.setTitle("Approval Code");
                poMesgBox.setMessage("Create new approval code?");
                poMesgBox.setPositiveButton("Yes", (view13, dialog) -> {
                    dialog.dismiss();
                    txtBranch.setEnabled(true);
                    txtReferNo.setEnabled(true);
                    txtLastNm.setEnabled(true);
                    txtFrstNm.setEnabled(true);
                    txtMiddNm.setEnabled(true);
                    txtSuffix.setEnabled(true);
                    txtRemarks.setEnabled(true);
                    createNew = true;

                    btnCreate.setText("Generate Approval Code");
                });
                poMesgBox.setNegativeButton("No", (view12, dialog) -> dialog.dismiss());
                poMesgBox.show();
            } else {
                poEntryxx.setReqDatex(Objects.requireNonNull(txtDate.getText()).toString());
                poEntryxx.setReferNox(Objects.requireNonNull(txtReferNo.getText()).toString());
                poEntryxx.setLastName(Objects.requireNonNull(txtLastNm.getText()).toString());
                poEntryxx.setFrstName(Objects.requireNonNull(txtFrstNm.getText()).toString());
                poEntryxx.setSuffix(Objects.requireNonNull(txtSuffix.getText()).toString());
                poEntryxx.setMiddName(Objects.requireNonNull(txtMiddNm.getText()).toString());
                poEntryxx.setRemarks(Objects.requireNonNull(txtRemarks.getText()).toString());
                mViewModel.GenerateCode(poEntryxx, new VMApprovalEntry.OnGenerateApprovalCodeListener() {
                    @Override
                    public void OnGenerate(String title, String message) {
                        poLoad.initDialog(title, message, false);
                        poLoad.show();
                    }

                    @Override
                    public void OnSuccess(String args) {
                        poLoad.dismiss();
                        txtAppCode.setText(args);
                        txtBranch.setEnabled(false);
                        txtReferNo.setEnabled(false);
                        txtLastNm.setEnabled(false);
                        txtFrstNm.setEnabled(false);
                        txtMiddNm.setEnabled(false);
                        txtSuffix.setEnabled(false);
                        txtRemarks.setEnabled(false);
                        createNew = false;

                        btnCreate.setText("Generate New");
                    }

                    @Override
                    public void OnFailed(String message) {
                        poLoad.dismiss();
                        poMesgBox.initDialog();
                        poMesgBox.setIcon(R.drawable.baseline_error_24);
                        poMesgBox.setPositiveButton("Dismiss", (view1, dialog) -> dialog.dismiss());
                        poMesgBox.setTitle("Approval Code");
                        poMesgBox.setMessage(message);
                        poMesgBox.show();
                    }
                });
            }
        });
        return v;
    }

    private void initWidgets(View v){
        txtBranch = v.findViewById(R.id.txt_appBranch);
        tilReferNo = v.findViewById(R.id.til_appReferNox);
        txtReferNo = v.findViewById(R.id.txt_appReferNox);
        txtDate = v.findViewById(R.id.txt_appRequestDate);
        lnFullNm = v.findViewById(R.id.linear_appFullname);
        txtLastNm = v.findViewById(R.id.txt_appLastName);
        txtFrstNm = v.findViewById(R.id.txt_appFirstName);
        txtMiddNm = v.findViewById(R.id.txt_appMiddName);
        txtSuffix = v.findViewById(R.id.txt_appSuffix);
        txtRemarks = v.findViewById(R.id.txt_appRemarks);
        txtAppCode = v.findViewById(R.id.txt_approvalCode);
        lblAppv = v.findViewById(R.id.lbl_approval);
        btnCreate = v.findViewById(R.id.btn_requestAppCode);
        btnCopy = v.findViewById(R.id.btn_CopyToClipboard);
    }
}