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

import static org.rmj.g3appdriver.etc.AppConstants.LEAVE_TYPE;
import static org.rmj.g3appdriver.etc.AppConstants.getLeaveStatus;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.etc.GToast;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.pojo.LeaveApprovalInfo;
import org.rmj.guanzongroup.petmanager.Activity.Activity_Application;
import org.rmj.guanzongroup.petmanager.R;
import org.rmj.guanzongroup.petmanager.ViewModel.VMLeaveApproval;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Fragment_LeaveApproval extends Fragment implements VMLeaveApproval.OnDownloadLeaveAppInfo {
    public static final String TAG = Fragment_LeaveApproval.class.getSimpleName();
    private VMLeaveApproval mViewModel;

    private LeaveApprovalInfo poModel;
    private LoadDialog poDialogx;
    private MessageBox poMessage;

    private int pnCredits, pnLeaveTp;

    private LinearLayout lnSearch, lnApprvl;
    private MaterialButton btnCancel, bntConfirm;
    private MaterialTextView
            lblHdBranch,
            lblHdAddrss,
            lblTransNox,
            lblEmployeNm,
            lblDeptName,
            lblBranchNm,
            lblLeaveCrd,
            lblDateAppl,
            lblDateFrom,
            lblDateThru,
            lblDateAppr,
            lblLeaveTpe,
            lblLeaveStx;
    private TextInputEditText txtSearch,
            tieDateFrom,
            tieDateThru,
            txtPurpse,
            tieWithPy,
            tieWOPay;
    private TextInputLayout tilRemarks;
    private MaterialButton btnQuickSearch;

    public static Fragment_LeaveApproval newInstance() {
        return new Fragment_LeaveApproval();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(VMLeaveApproval.class);
        View view = inflater.inflate(R.layout.fragment_leave_approval, container, false);
        poModel = new LeaveApprovalInfo();
        initWidgets(view);

        Typeface typeface = ResourcesCompat.getFont(requireActivity(), R.font.roboto_bold);
        tilRemarks.setTypeface(typeface);
        String TransNox = Activity_Application.getInstance().getTransNox();

        if(TransNox.isEmpty()){
            lnSearch.setVisibility(View.VISIBLE);
        } else {
            lnSearch.setVisibility(View.GONE);
        }
        boolean forViewing = requireActivity().getIntent().getBooleanExtra("type", false);
        if(forViewing){
            lnApprvl.setVisibility(View.GONE);
        } else {
            lnApprvl.setVisibility(View.VISIBLE);
        }

        mViewModel.setTransNox(TransNox);
        mViewModel.getTransNox().observe(getViewLifecycleOwner(), s -> {
            if(!s.isEmpty()){
                mViewModel.getEmployeeLeaveInfo(s).observe(requireActivity(), eEmployeeLeave -> {
                    try {
                        if(eEmployeeLeave == null){
                            mViewModel.downloadLeaveApplication(TransNox, Fragment_LeaveApproval.this);
                        } else {
                            poModel.setTransNox(s);
                            poModel.setAppldFrx(eEmployeeLeave.getDateFrom());
                            poModel.setAppldTox(eEmployeeLeave.getDateThru());
                            lblTransNox.setText("Transaction No. : " + eEmployeeLeave.getTransNox());
                            lblEmployeNm.setText(eEmployeeLeave.getEmployID());
                            lblDeptName.setText(eEmployeeLeave.getDeptName());
                            lblBranchNm.setText(eEmployeeLeave.getBranchNm());
                            pnLeaveTp = Integer.parseInt(eEmployeeLeave.getLeaveTyp());
                            lblLeaveCrd.setText("Leave Credits : " + eEmployeeLeave.getLveCredt());
                            lblLeaveStx.setText(getLeaveStatus(eEmployeeLeave.getTranStat()));
                            lblDateAppr.setText(FormatUIText.formatGOCasBirthdate(AppConstants.CURRENT_DATE()));
                            lblDateAppl.setText(FormatUIText.formatGOCasBirthdate(eEmployeeLeave.getTransact()));
                            lblDateFrom.setText(FormatUIText.formatGOCasBirthdate(eEmployeeLeave.getDateFrom()));
                            lblDateThru.setText(FormatUIText.formatGOCasBirthdate(eEmployeeLeave.getDateThru()));
                            lblLeaveTpe.setText(LEAVE_TYPE[Integer.parseInt(eEmployeeLeave.getLeaveTyp())]);
                            tieDateFrom.setText(FormatUIText.formatGOCasBirthdate(eEmployeeLeave.getDateFrom()));
                            tieDateThru.setText(FormatUIText.formatGOCasBirthdate(eEmployeeLeave.getDateThru()));
                            txtPurpse.setText(eEmployeeLeave.getPurposex());
                            pnCredits = eEmployeeLeave.getLveCredt();
                            pnLeaveTp = Integer.parseInt(eEmployeeLeave.getLeaveTyp());
                            String lsFromx = eEmployeeLeave.getDateFrom();
                            String lsDteTo = eEmployeeLeave.getDateThru();
                            mViewModel.setCredits(pnCredits);
                            mViewModel.calculateLeavePay(pnLeaveTp, lsFromx, lsDteTo);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                });
            }
        });

        mViewModel.getUserInfo().observe(requireActivity(), eEmployeeInfo -> {
            try{
                poModel.setApproved(AppConstants.CURRENT_DATE());
                poModel.setApprovex(eEmployeeInfo.sUserIDxx);
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.getWithPay().observe(getViewLifecycleOwner(), integer -> {
            try{
                tieWithPy.setText(String.valueOf(integer));
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.getWOPay().observe(getViewLifecycleOwner(), integer -> {
            try{
                tieWOPay.setText(String.valueOf(integer));
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.getUserBranchInfo().observe(requireActivity(), eBranchInfo -> {
            try{
                lblHdBranch.setText(eBranchInfo.getBranchNm());
                lblHdAddrss.setText(eBranchInfo.getAddressx());
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        btnQuickSearch.setOnClickListener(v -> mViewModel.downloadLeaveApplication(Objects.requireNonNull(txtSearch.getText()).toString(), this));

        bntConfirm.setOnClickListener(v -> {
            poMessage.initDialog();
            poMessage.setIcon(R.drawable.baseline_contact_support_24);
            poMessage.setTitle("Leave Approval");
            poMessage.setMessage("Approve " + lblEmployeNm.getText().toString() + "'s leave application?");
            poMessage.setPositiveButton("Approve", (view1, dialog) -> {
                dialog.dismiss();
                poModel.setTranStat("1");
                sendLeaveUpdate();
            });
            poMessage.setNegativeButton("Cancel", (view1, dialog) -> dialog.dismiss());
            poMessage.show();
        });

        btnCancel.setOnClickListener(v -> {
            poMessage.initDialog();
            poMessage.setIcon(R.drawable.baseline_contact_support_24);
            poMessage.setTitle("Leave Approval");
            poMessage.setMessage("Disapprove " + lblEmployeNm.getText().toString() + "'s leave application?");
            poMessage.setPositiveButton("Disapprove", (view1, dialog) -> {
                dialog.dismiss();
                poModel.setTranStat("3");
                sendLeaveUpdate();
            });
            poMessage.setNegativeButton("Cancel", (view1, dialog) -> dialog.dismiss());
            poMessage.show();
        });


        tieDateFrom.setOnClickListener(v -> {
            final Calendar newCalendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
            final DatePickerDialog dateFrom = new DatePickerDialog(getActivity(), (view1, year, month, dayOfMonth) -> {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                try {
                    Date dateTo = dataFormat.parse(Objects.requireNonNull(FormatUIText.formatTextToData(Objects.requireNonNull(tieDateThru.getText()).toString())));
                    Date dteFrm = newDate.getTime();
                    if(dteFrm.before(dateTo)) {
                        poModel.setAppldFrx(dataFormat.format(newDate.getTime()));
                        tieDateFrom.setText(dateFormatter.format(newDate.getTime()));
                        mViewModel.calculateLeavePay(pnLeaveTp, dataFormat.format(newDate.getTime()),
                                FormatUIText.formatTextToData(Objects.requireNonNull(tieDateThru.getText()).toString()));
                    } else {
                        GToast.CreateMessage(requireActivity(), "Invalid date selected.", GToast.ERROR).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(requireActivity(), "Error while selecting date. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            dateFrom.show();
        });

        tieDateThru.setOnClickListener(v -> {
            final Calendar newCalendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
            final DatePickerDialog dateFrom = new DatePickerDialog(getActivity(), (view1, year, month, dayOfMonth) -> {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                try {
                    Date dateFrmx = dataFormat.parse(Objects.requireNonNull(FormatUIText.formatTextToData(Objects.requireNonNull(tieDateFrom.getText()).toString())));
                    Date dateThru = newDate.getTime();
                    if (dateThru.after(dateFrmx)){
                        poModel.setAppldFrx(dataFormat.format(newDate.getTime()));
                        tieDateThru.setText(dateFormatter.format(newDate.getTime()));
                        mViewModel.calculateLeavePay(pnLeaveTp, FormatUIText.formatTextToData(Objects.requireNonNull(tieDateFrom.getText()).toString()),
                                dataFormat.format(newDate.getTime()));
                    } else {
                        GToast.CreateMessage(requireActivity(), "Invalid date selected.", GToast.ERROR).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(requireActivity(), "Error while selecting date. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            dateFrom.show();
        });

        tieWithPy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tieWithPy.removeTextChangedListener(this);
                try{
                    if(poModel.getTransNox() != null &&
                            tieWithPy.hasFocus()) {
                        int lnWthPay = Integer.parseInt(Objects.requireNonNull(s.toString()));
//                        mViewModel.setWithPay(lnWthPay);
                        mViewModel.calculateWithOPay(lnWthPay);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                tieWithPy.addTextChangedListener(this);
            }
        });

        tieWOPay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tieWOPay.removeTextChangedListener(this);
                try{
                    if(poModel.getTransNox() != null &&
                            tieWOPay.hasFocus()) {
                        int lnWOPay = Integer.parseInt(Objects.requireNonNull(s.toString()));
//                        mViewModel.setWOPay(lnWOPay);
                        mViewModel.calculateWithPay(lnWOPay);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                tieWOPay.addTextChangedListener(this);
            }
        });
        return view;
    }
    private void sendLeaveUpdate(){
        poModel.setWithPayx(Integer.parseInt(tieWithPy.getText().toString()));
        poModel.setWithOPay(Integer.parseInt(tieWOPay.getText().toString()));
        mViewModel.confirmLeaveApplication(poModel, new VMLeaveApproval.OnConfirmLeaveAppCallback() {
            @Override
            public void onConfirm() {
                poDialogx.initDialog("Leave Application", "Confirming leave application. Please wait...", false);
                poDialogx.show();
            }

            @Override
            public void onSuccess(String message) {
                poDialogx.dismiss();

                poMessage.initDialog();
                poMessage.setIcon(R.drawable.baseline_message_24);
                poMessage.setTitle("Leave Approval");
                poMessage.setMessage(message);
                poMessage.setPositiveButton("Okay", (view1, dialog) -> {
                    dialog.dismiss();
                });

                poMessage.show();
            }

            @Override
            public void onFailed(String message) {
                poDialogx.dismiss();
                initErrorDialog("PET Manager", message);
            }
        });
    }

    public void initWidgets(View view){
        poDialogx = new LoadDialog(getActivity());
        poMessage = new MessageBox(getActivity());
        lnSearch = view.findViewById(R.id.linear_search);
        lnApprvl = view.findViewById(R.id.linear_approval);
        txtSearch = view.findViewById(R.id.txt_leave_ob_search);
        btnQuickSearch = view.findViewById(R.id.btn_quick_search);
        lblHdBranch = view.findViewById(R.id.lbl_headerBranch);
        lblHdAddrss = view.findViewById(R.id.lbl_headerAddress);
        lblTransNox = view.findViewById(R.id.lbl_transNox);
        lblEmployeNm = view.findViewById(R.id.lbl_clientNm);
        tieDateFrom = view.findViewById(R.id.txt_dateFrom);
        lblDeptName = view.findViewById(R.id.lbl_deptNme);
        lblBranchNm = view.findViewById(R.id.lbl_branchNm);
        lblLeaveCrd = view.findViewById(R.id.lbl_leaveCrdt);
        lblDateAppl = view.findViewById(R.id.lbl_dateApplied);
        lblDateFrom = view.findViewById(R.id.lbl_dateFrom);
        lblDateThru = view.findViewById(R.id.lbl_dateThru);
        lblDateAppr = view.findViewById(R.id.lbl_dateApproved);
        lblLeaveTpe = view.findViewById(R.id.lbl_leaveType);
        lblLeaveStx = view.findViewById(R.id.lbl_leaveStatus);
        tieDateThru = view.findViewById(R.id.txt_dateTo);
        tilRemarks = view.findViewById(R.id.tilRemarks);
        txtPurpse = view.findViewById(R.id.txt_purpose);

        tieWithPy = view.findViewById(R.id.txt_withPay);
        tieWOPay = view.findViewById(R.id.txt_withoutPay);

        btnCancel = view.findViewById(R.id.btn_cancel);
        bntConfirm = view.findViewById(R.id.btn_confirm);
    }

    public void initErrorDialog(String title, String message){
        poMessage.initDialog();
        poMessage.setIcon(R.drawable.baseline_error_24);
        poMessage.setTitle(title);
        poMessage.setMessage(message);
        poMessage.setPositiveButton("Okay", (view, dialog) ->
                dialog.dismiss());
        poMessage.show();
    }

    @Override
    public void OnDownload() {
        txtSearch.setText("");
        poDialogx.initDialog("Leave Application", "Searching leave application. Please wait...", false);
        poDialogx.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void OnSuccessDownload(String TransNox) {
        poDialogx.dismiss();
        mViewModel.setTransNox(TransNox);
    }

    @Override
    public void OnFailedDownload(String message) {
        poDialogx.dismiss();
        initErrorDialog("Leave Application", message);
    }

}