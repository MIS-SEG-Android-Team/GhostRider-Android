/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 8/16/21 8:21 AM
 * project file last modified : 8/16/21 8:21 AM
 */

package org.rmj.guanzongroup.petmanager.Fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.pojo.OBApprovalInfo;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.petmanager.Activity.Activity_Application;
import org.rmj.guanzongroup.petmanager.R;
import org.rmj.guanzongroup.petmanager.ViewModel.VMObApproval;

import java.util.Objects;

public class Fragment_BusinessTripApproval extends Fragment implements VMObApproval.OnDownloadBusinessTripCallback, VMObApproval.OnConfirmApplicationCallback {
    public static final String TAG = Fragment_BusinessTripApproval.class.getSimpleName();
    private VMObApproval mViewModel;

    private String TransNox;

    private LinearLayout lnSearch;
    private TextInputEditText
            txtSearch,
            txtPurpose,
            tieDateFrom,
            tieDateThru;
    private MaterialButton btnQuickSearch;
    private MaterialButton btnCancel, bntConfirm;
    private MaterialTextView
            lblBranchNm,
            lblBranchAd,
            lblTransNox,
            lblEmployeNm,
            lblDateAppd,
            lblDateFrom,
            lblDateThru,
            lblDateAppx;
    private MaterialCardView cvLeaveOb;

    private TextInputLayout tilRemarks;
    private LoadDialog poDialogx;
    private MessageBox poMessage;
    private OBApprovalInfo poModel;


    public static Fragment_BusinessTripApproval newInstance() {return new Fragment_BusinessTripApproval();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(VMObApproval.class);
        View view = inflater.inflate(R.layout.fragment_business_trip_approval, container, false);
        poModel = new OBApprovalInfo();
        initWidgets(view);

        Typeface typeface = ResourcesCompat.getFont(requireActivity(), R.font.roboto_bold);
        tilRemarks.setTypeface(typeface);
        TransNox = Activity_Application.getInstance().getTransNox();

        if(TransNox.isEmpty()){
            lnSearch.setVisibility(View.VISIBLE);
        } else {
            lnSearch.setVisibility(View.GONE);
        }

        boolean forViewing = requireActivity().getIntent().getBooleanExtra("type", false);
        if(forViewing){
            cvLeaveOb.setVisibility(View.GONE);
        } else {
            cvLeaveOb.setVisibility(View.VISIBLE);
        }

        mViewModel.setTransNox(TransNox);
        mViewModel.getUserBranchInfo().observe(getViewLifecycleOwner(), eBranchInfo -> {
            try{
                lblBranchNm.setText(eBranchInfo.getBranchNm());
                lblBranchAd.setText(eBranchInfo.getAddressx());
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.getTransNox().observe(getViewLifecycleOwner(), s -> {
            if(!s.isEmpty()) {
                mViewModel.getBusinessTripInfo(s).observe(getViewLifecycleOwner(), eEmployeeBusinessTrip -> {
                    try {
                        if (eEmployeeBusinessTrip == null) {
                            mViewModel.downloadBusinessTrip(s, Fragment_BusinessTripApproval.this);
                        } else {
                            lblTransNox.setText(eEmployeeBusinessTrip.getTransNox());
                            lblDateAppd.setText(FormatUIText.formatGOCasBirthdate(eEmployeeBusinessTrip.getTransact()));
                            lblDateAppx.setText(FormatUIText.formatGOCasBirthdate(AppConstants.CURRENT_DATE()));
                            lblEmployeNm.setText(eEmployeeBusinessTrip.getFullName());
                            lblDateFrom.setText(FormatUIText.formatGOCasBirthdate(eEmployeeBusinessTrip.getDateFrom()));
                            lblDateThru.setText(FormatUIText.formatGOCasBirthdate(eEmployeeBusinessTrip.getDateThru()));
                            tieDateFrom.setText(FormatUIText.formatGOCasBirthdate(eEmployeeBusinessTrip.getDateFrom()));
                            tieDateThru.setText(FormatUIText.formatGOCasBirthdate(eEmployeeBusinessTrip.getDateThru()));
                            txtPurpose.setText(eEmployeeBusinessTrip.getRemarksx());

                            poModel.setTransNox(s);
                            poModel.setAppldFrx(eEmployeeBusinessTrip.getDateFrom());
                            poModel.setAppldTox(eEmployeeBusinessTrip.getDateThru());
                            poModel.setDateAppv(eEmployeeBusinessTrip.getDapprove());
                            poModel.setApproved(EmployeeSession.getInstance(requireActivity()).getEmployeeID());
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                });
            }
        });

        btnQuickSearch.setOnClickListener(v ->  {
            mViewModel.downloadBusinessTrip(Objects.requireNonNull(txtSearch.getText()).toString(), Fragment_BusinessTripApproval.this);
        });

        bntConfirm.setOnClickListener(v -> {
            poMessage.initDialog();
            poMessage.setIcon(R.drawable.baseline_contact_support_24);
            poMessage.setTitle("OB Approval");
            poMessage.setMessage("Approve " + lblEmployeNm.getText().toString() + "'s business trip application?");
            poMessage.setPositiveButton("Approve", (view1, dialog) -> {
                dialog.dismiss();
                poModel.setTranStat("1");
                mViewModel.confirmOBApplication(poModel, Fragment_BusinessTripApproval.this);
            });
            poMessage.setNegativeButton("Cancel", (view1, dialog) -> dialog.dismiss());
            poMessage.show();
        });

        btnCancel.setOnClickListener(v -> {
            poMessage.initDialog();
            poMessage.setIcon(R.drawable.baseline_contact_support_24);
            poMessage.setTitle("OB Approval");
            poMessage.setMessage("Disapprove " + lblEmployeNm.getText().toString() + "'s business trip application?");
            poMessage.setPositiveButton("Disapprove", (view1, dialog) -> {
                dialog.dismiss();
                poModel.setTranStat("3");
                mViewModel.confirmOBApplication(poModel, Fragment_BusinessTripApproval.this);
            });
            poMessage.setNegativeButton("Cancel", (view1, dialog) -> dialog.dismiss());
            poMessage.show();
        });
        return view;
    }

    public void initWidgets(View view){
        poDialogx = new LoadDialog(getActivity());
        poMessage = new MessageBox(getActivity());
        poModel = new OBApprovalInfo();
        lnSearch = view.findViewById(R.id.linear_search);
        lblBranchNm = view.findViewById(R.id.lbl_headerBranch);
        lblBranchAd = view.findViewById(R.id.lbl_headerAddress);
        txtSearch = view.findViewById(R.id.txt_leave_ob_search);
        btnQuickSearch = view.findViewById(R.id.btn_quick_search);
        cvLeaveOb = view.findViewById(R.id.cv_leave_ob);
        lblTransNox = view.findViewById(R.id.lbl_transNox);
        lblEmployeNm = view.findViewById(R.id.lbl_clientNm);
        tieDateFrom = view.findViewById(R.id.txt_dateFrom);
        tieDateThru = view.findViewById(R.id.txt_dateTo);
        tilRemarks = view.findViewById(R.id.tilRemarks);
        txtPurpose = view.findViewById(R.id.txt_purpose);
        tieDateFrom = view.findViewById(R.id.txt_dateFrom);
        tieDateThru = view.findViewById(R.id.txt_dateTo);
        lblDateAppd = view.findViewById(R.id.lbl_dateApplied);
        lblDateFrom = view.findViewById(R.id.lbl_dateFrom);
        lblDateThru = view.findViewById(R.id.lbl_dateThru);
        lblDateAppx = view.findViewById(R.id.lbl_dateApproved);
        tieDateFrom = view.findViewById(R.id.txt_dateFrom);
        tieDateThru = view.findViewById(R.id.txt_dateTo);
        btnCancel = view.findViewById(R.id.btn_cancel);
        bntConfirm = view.findViewById(R.id.btn_confirm);
    }

    public void initErrorDialog(String title, String message){
        poMessage.initDialog();
        poMessage.setIcon(R.drawable.baseline_error_24);
        poMessage.setTitle(title);
        poMessage.setMessage(message);
        poMessage.setPositiveButton("Dismiss", (view, dialog) -> dialog.dismiss());
        poMessage.show();
    }

    @Override
    public void OnDownload(String title, String message) {
        poDialogx.initDialog(title, message, false);
        poDialogx.show();
    }

    @Override
    public void OnSuccessDownload(String transNox) {
        poDialogx.dismiss();
        mViewModel.setTransNox(transNox);
    }

    @Override
    public void OnFailedDownload(String message) {
        poDialogx.dismiss();
        initErrorDialog("PET Manager", message);
    }

    @Override
    public void OnConfirm(String title, String message) {
        poDialogx.initDialog(title, message, false);
        poDialogx.show();
    }

    @Override
    public void OnSuccess(String message) {
        poDialogx.dismiss();
        initErrorDialog("PET Manager", message);
    }

    @Override
    public void OnFailed(String message) {
        poDialogx.dismiss();
        initErrorDialog("PET Manager", message);
    }
}