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

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;


import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.pojo.CreditAppInfo;
import org.rmj.g3appdriver.utils.CopyToClipboard;
import org.rmj.guanzongroup.ghostrider.approvalcode.Activity.Activity_ApprovalCode;
import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.pojo.CreditApp;
import org.rmj.guanzongroup.ghostrider.approvalcode.Etc.ViewModelCallback;
import org.rmj.guanzongroup.ghostrider.approvalcode.R;
import org.rmj.guanzongroup.ghostrider.approvalcode.ViewModel.VMCreditAppApproval;

import java.util.Objects;

public class Fragment_CreditAppApproval extends Fragment {

    private VMCreditAppApproval mViewModel;

    private TextInputEditText txtTransNox, txtReqDate, txtMiscInfo, txtRemarks, txtAppNotes;
    private MaterialAutoCompleteTextView txtBranch, txtReBranch;
    private TextInputEditText txtAppCode;
    private MaterialButton btnCopy;
    private MaterialButton btnLoadApp, btnApprove, btnDApprove, btnAppNoCI;
    private LoadDialog poDialog;
    private MessageBox poMsgBox;

    private String sSystemCD;

    public static Fragment_CreditAppApproval newInstance() {
        return new Fragment_CreditAppApproval();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(VMCreditAppApproval.class);
        View layout = inflater.inflate(R.layout.fragment_credit_app_approval, container, false);
        sSystemCD = Activity_ApprovalCode.getInstance().getSystemCode();
        poDialog = new LoadDialog(requireActivity());
        poMsgBox = new MessageBox(requireActivity());
        initWidgets(layout);

        btnLoadApp.setOnClickListener(view -> {
            String lsTransNo = Objects.requireNonNull(txtTransNox.getText()).toString();
            String lsBrnchCd = txtBranch.getText().toString();
            mViewModel.LoadApplication(sSystemCD, lsTransNo, lsBrnchCd, new VMCreditAppApproval.OnLoadApplicationListener() {
                @Override
                public void OnLoad(String Title, String Message) {
                    poDialog.initDialog(Title, Message, false);
                    poDialog.show();
                }

                @Override
                public void OnLoadSuccess(CreditAppInfo args) {
                    poDialog.dismiss();
                    txtReBranch.setText(args.getRqstinfo());
                    txtReqDate.setText(args.getReqstdxx());
                    txtMiscInfo.setText(args.getMiscinfo());
                    txtRemarks.setText(args.getRemarks1());
                }

                @Override
                public void OnLoadFailed(String message) {
                    poDialog.dismiss();
                    poMsgBox.initDialog();
                    poMsgBox.setIcon(R.drawable.baseline_error_24);
                    poMsgBox.setTitle("Approval Code");
                    poMsgBox.setMessage(message);
                    poMsgBox.setPositiveButton("Dismiss", (view, dialog) -> dialog.dismiss());
                    poMsgBox.show();
                }
            });
        });

        btnApprove.setOnClickListener(view -> {
            String lsTransNo = Objects.requireNonNull(txtTransNox.getText()).toString();
            String lsReasonx = Objects.requireNonNull(txtAppNotes.getText()).toString();
            CreditApp.Application loApp = new CreditApp.Application(lsTransNo, lsReasonx, "yes");
            UploadApproval(loApp);
        });

        btnDApprove.setOnClickListener(view -> {
            String lsTransNo = Objects.requireNonNull(txtTransNox.getText()).toString();
            String lsReasonx = Objects.requireNonNull(txtAppNotes.getText()).toString();
            CreditApp.Application loApp = new CreditApp.Application(lsTransNo, lsReasonx, "no");
            UploadApproval(loApp);
        });

        btnAppNoCI.setOnClickListener(view -> {
            String lsTransNo = Objects.requireNonNull(txtTransNox.getText()).toString();
            String lsReasonx = Objects.requireNonNull(txtAppNotes.getText()).toString();
            CreditApp.Application loApp = new CreditApp.Application(lsTransNo, lsReasonx, "na");
            UploadApproval(loApp);
        });

        return layout;
    }

    private void initWidgets(View v){
        txtTransNox = v.findViewById(R.id.txt_appTransNox);
        txtReqDate = v.findViewById(R.id.txt_appRequestDate);
        txtMiscInfo = v.findViewById(R.id.txt_appMiscInfo);
        txtRemarks = v.findViewById(R.id.txt_appRemarks);
        txtAppNotes = v.findViewById(R.id.txt_appNotes);
        txtBranch = v.findViewById(R.id.txt_appBranch);
        txtReBranch = v.findViewById(R.id.txt_appReqstBranch);
        txtAppCode = v.findViewById(R.id.txt_approvalCode);
        btnCopy = v.findViewById(R.id.btn_CopyToClipboard);
        btnLoadApp = v.findViewById(R.id.btn_loadApplication);
        btnApprove = v.findViewById(R.id.btn_ApproveWithCI);
        btnDApprove = v.findViewById(R.id.btn_DisapproveApp);
        btnAppNoCI = v.findViewById(R.id.btn_ApproveAppNoCI);

        txtReBranch.setEnabled(false);
        txtReqDate.setEnabled(false);
        txtMiscInfo.setEnabled(false);
        txtRemarks.setEnabled(false);

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
    }

    private void UploadApproval(CreditApp.Application foVal){
        mViewModel.ApproveApplication(foVal, new ViewModelCallback() {
            @Override
            public void OnLoadData(String Title, String Message) {
                poDialog.initDialog(Title, Message, false);
                poDialog.show();
            }

            @Override
            public void OnSuccessResult(String args) {
                poDialog.dismiss();
                txtAppCode.setText(args);
            }

            @Override
            public void OnFailedResult(String message) {
                poDialog.dismiss();
                poMsgBox.initDialog();
                poMsgBox.setIcon(R.drawable.baseline_error_24);
                poMsgBox.setTitle("Approval Code");
                poMsgBox.setMessage(message);
                poMsgBox.setPositiveButton("Dismiss", (view, dialog) -> dialog.dismiss());
                poMsgBox.show();
            }
        });
    }
}