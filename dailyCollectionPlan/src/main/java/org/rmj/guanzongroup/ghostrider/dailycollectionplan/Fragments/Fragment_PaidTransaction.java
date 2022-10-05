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

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.rmj.g3appdriver.GRider.Constants.AppConstants;
import org.rmj.g3appdriver.GRider.Etc.FormatUIText;
import org.rmj.g3appdriver.GRider.Etc.LoadDialog;
import org.rmj.g3appdriver.GRider.Etc.MessageBox;
import org.rmj.g3appdriver.lib.integsys.Dcp.PaidDCP;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_Transaction;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Dialog.DialogCheckPayment;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.VMPaidTransaction;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.ViewModelCallback;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;

public class Fragment_PaidTransaction extends Fragment implements ViewModelCallback {
    private static final String TAG = Fragment_PaidTransaction.class.getSimpleName();

    private VMPaidTransaction mViewModel;
    private PaidDCP poPaid;
    private MessageBox poMessage;
    private LoadDialog poDialog;

    private final DecimalFormat formatter = new DecimalFormat("###,###,##0.00");

    private CheckBox cbCheckPymnt;
    private TextView lblBranch, lblAddress, lblAccNo, lblClientNm, lblTransNo;
    private Spinner spnType;
    private TextInputEditText txtPrNoxx, txtRemarks, txtAmount, txtRebate, txtOthers, txtTotAmnt;
    private TextInputLayout tilDiscount, tilPenaly;
    private Button btnAmort, btnRBlnce, btnClear;
    private MaterialButton btnConfirm;

    public static Fragment_PaidTransaction newInstance() {
        return new Fragment_PaidTransaction();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(VMPaidTransaction.class);
        View view = inflater.inflate(R.layout.fragment_paid_transaction, container, false);
        poPaid = new PaidDCP();
        poMessage = new MessageBox(requireActivity());
        poDialog = new LoadDialog(requireActivity());
        initWidgets(view);

        String Remarksx = Activity_Transaction.getInstance().getRemarksCode();
        String TransNox = Activity_Transaction.getInstance().getTransNox();
        String AccntNox = Activity_Transaction.getInstance().getAccntNox();
        int EntryNox = Activity_Transaction.getInstance().getEntryNox();

        mViewModel.GetUserInfo().observe(getViewLifecycleOwner(), user -> {
            try {
                lblBranch.setText(user.sBranchNm);
                lblAddress.setText(user.sAddressx);
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.GetCollectionDetail(TransNox, EntryNox, AccntNox).observe(getViewLifecycleOwner(), detail -> {
            try {
                poPaid.setAccntNo(detail.getAcctNmbr());
                poPaid.setTransNo(detail.getTransNox());
                poPaid.setEntryNo(String.valueOf(detail.getEntryNox()));

                lblAccNo.setText(detail.getAcctNmbr());
                lblClientNm.setText(detail.getFullName());
                lblTransNo.setText(detail.getTransNox());

                mViewModel.InitPurchaseInfo(detail);

                btnAmort.setText("Amortization : " + FormatUIText.getCurrencyUIFormat(detail.getMonAmort()));
                btnRBlnce.setText("Amount Due : " + FormatUIText.getCurrencyUIFormat(detail.getAmtDuexx()));
                SimpleDateFormat loFormatter = new SimpleDateFormat("yyyy-MM-dd");

                String lsDayDuex = detail.getDueDatex().split("-")[2];
                //Check here if the due date is on the maximum days per month
                // if true check the maximum day of month and set it as the due date for this current month...
                if(lsDayDuex.equalsIgnoreCase("31")) {
                    LocalDate lastDayOfMonth = LocalDate.parse(AppConstants.CURRENT_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            .with(TemporalAdjusters.lastDayOfMonth());
                    lsDayDuex = String.valueOf(lastDayOfMonth.getDayOfMonth());
                }
                String lsCrtYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
                String lsCrtMnth = new SimpleDateFormat("MM", Locale.getDefault()).format(Calendar.getInstance().getTime());
                String lsDueDate = lsCrtYear + "-" + lsCrtMnth + "-" + lsDayDuex;
                Date ldDueDatex = new SimpleDateFormat("yyyy-MM-dd").parse(lsDueDate);
                Date loCrtDate = loFormatter.parse(AppConstants.CURRENT_DATE);
                int lnResult = loCrtDate.compareTo(ldDueDatex);

                // If result is less than 0 current date is before the due date
                // If result is equal to 0 current date is equal to due date
                // if result is more than 0 current date is after the due date
                if (lnResult > 0) {
                    tilDiscount.setErrorEnabled(true);
                    tilDiscount.setError("Due date has passed.");
                    mViewModel.setIsDuePass(true);
                    txtRebate.setEnabled(false);

                    tilPenaly.setErrorEnabled(true);
                    tilPenaly.setError("Please enter correct penalty if calculated penalty is incorrect");
                } else {
                    tilDiscount.setErrorEnabled(false);
                    mViewModel.setIsDuePass(false);
                    txtRebate.setEnabled(true);

                    tilPenaly.setErrorEnabled(false);
                }

                btnAmort.setOnClickListener(v -> {
                    mViewModel.setAmount(Double.valueOf(detail.getMonAmort()));
                    txtAmount.setText(detail.getMonAmort());
                });

                btnRBlnce.setOnClickListener(v -> {
                    mViewModel.setAmount(Double.valueOf(detail.getAmtDuexx()));
                    txtAmount.setText(detail.getAmtDuexx());
                });

                btnClear.setOnClickListener(v -> {
                    txtAmount.setText("");
                    txtRebate.setText("0.0");
                    txtOthers.setText("0.0");
                    txtTotAmnt.setText("");
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.GetPaymentType().observe(getViewLifecycleOwner(), stringArrayAdapter -> {
            spnType.setAdapter(stringArrayAdapter);
            spnType.setSelection(1);
        });

        mViewModel.GetPrNumber().observe(getViewLifecycleOwner(), s -> {
            if(s != null){
                txtPrNoxx.setText(s);
            }
        });

        mViewModel.getRebate().observe(getViewLifecycleOwner(), aDouble -> {
            try{
                txtRebate.setText(String.valueOf(aDouble));
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.getPenalty().observe(getViewLifecycleOwner(), aDouble -> {
            try{
                txtOthers.setText(String.valueOf(aDouble));
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.getRebateNotice().observe(getViewLifecycleOwner(), s -> {
            if(!s.isEmpty()){
                tilDiscount.setErrorEnabled(true);
                tilDiscount.setError(s);
            } else {
                tilDiscount.setErrorEnabled(false);
            }
        });

        txtRebate.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                if (!Objects.requireNonNull(txtRebate.getText()).toString().isEmpty()) {
                    mViewModel.setRebate(Double.valueOf(txtRebate.getText().toString().replace(",", "")));
                } else {
                    mViewModel.setRebate(0.00);
                }
            }
        });

        mViewModel.getTotalAmount().observe(getViewLifecycleOwner(), aFloat ->{
            try {
                txtTotAmnt.setText(formatter.format(aFloat));
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        cbCheckPymnt.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                DialogCheckPayment loPayment = new DialogCheckPayment(getActivity());
                mViewModel.getBankNameList().observe(getViewLifecycleOwner(), strings -> {
                    loPayment.initDialog(strings, new DialogCheckPayment.OnCheckPaymentDialogListener() {
                        @Override
                        public void OnConfirm(AlertDialog dialog, String bank, String date, String checkNo, String AcctNo) {
                            poPaid.setCheckDt(date);
                            poPaid.setCheckNo(checkNo);
                            poPaid.setAccntNo(AcctNo);
                            mViewModel.getBankInfoList().observe(getViewLifecycleOwner(), eBankInfos -> {
                                for(int x = 0; x < eBankInfos.size(); x++){
                                    if(bank.equalsIgnoreCase(eBankInfos.get(x).getBankName())){
                                        poPaid.setBankNme(eBankInfos.get(x).getBankIDxx());
                                        break;
                                    }
                                }
                            });
                            dialog.dismiss();
                        }

                        @Override
                        public void OnCancel(AlertDialog dialog) {
                            cbCheckPymnt.setChecked(false);
                            dialog.dismiss();
                        }
                    });
                    loPayment.show();
                });
            }
        });

        btnConfirm.setOnClickListener(v -> {
            poPaid.setRemarks(Remarksx);
            poPaid.setPayment(String.valueOf(spnType.getSelectedItemPosition()));
            poPaid.setPrNoxxx(Objects.requireNonNull(txtPrNoxx.getText()).toString());
            poPaid.setRemarks(Objects.requireNonNull(txtRemarks.getText()).toString());
            poPaid.setAmountx(Objects.requireNonNull(txtAmount.getText()).toString());
            poPaid.setDscount(Objects.requireNonNull(txtRebate.getText()).toString());
            poPaid.setOthersx(Objects.requireNonNull(txtOthers.getText()).toString());
            poPaid.setTotAmnt(Objects.requireNonNull(txtTotAmnt.getText()).toString());
            mViewModel.SavePaymentInfo(poPaid, Fragment_PaidTransaction.this);
        });

        return view;
    }

    private void initWidgets(View v){
        lblBranch = v.findViewById(R.id.lbl_headerBranch);
        lblAddress = v.findViewById(R.id.lbl_headerAddress);
        lblAccNo = v.findViewById(R.id.lbl_dcpAccNo);
        lblClientNm = v.findViewById(R.id.lbl_dcpClientNm);
        lblTransNo = v.findViewById(R.id.lbl_dcpTransNo);
        spnType = v.findViewById(R.id.spn_paymentType);
        cbCheckPymnt = v.findViewById(R.id.cb_dcpCheckPayment);
        txtPrNoxx = v.findViewById(R.id.txt_dcpPRNumber);
        txtRemarks = v.findViewById(R.id.txt_dcpRemarks);
        txtAmount = v.findViewById(R.id.txt_dcpAmount);
        tilDiscount = v.findViewById(R.id.til_dcpDiscount);
        tilPenaly = v.findViewById(R.id.til_dcpOthers);
        txtRebate = v.findViewById(R.id.txt_dcpDiscount);
        txtOthers = v.findViewById(R.id.txt_dcpOthers);
        txtTotAmnt = v.findViewById(R.id.txt_dcpTotAmount);
        btnConfirm = v.findViewById(R.id.btn_confirm);
        btnAmort = v.findViewById(R.id.btn_amortization);
        btnRBlnce = v.findViewById(R.id.btn_remainbalance);
        btnClear = v.findViewById(R.id.btn_clearText);

        txtAmount.addTextChangedListener(new OnAmountEnterTextWatcher(txtAmount));
        txtRebate.addTextChangedListener(new OnAmountEnterTextWatcher(txtRebate));
        txtOthers.addTextChangedListener(new OnAmountEnterTextWatcher(txtOthers));
    }

    @Override
    public void OnStartSaving() {
        poDialog.initDialog("Daily Collection Plan", "Posting transaction.Please wait...", false);
        poDialog.show();
    }

    @Override
    public void OnSuccessResult() {
        poDialog.dismiss();
        poMessage.initDialog();
        poMessage.setTitle("Transaction Success");
        poMessage.setMessage("Collection save successfully");
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
        poMessage.setPositiveButton("Okay", (view, dialog) -> {
            dialog.dismiss();
            if(message.equalsIgnoreCase("Collection info has been save.")){
                requireActivity().finish();
            }
        });
        poMessage.show();
    }

    private class OnAmountEnterTextWatcher implements TextWatcher{
        private final TextInputEditText inputEditText;

        OnAmountEnterTextWatcher(TextInputEditText inputEditText){
            this.inputEditText = inputEditText;
        }


        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            try {
                inputEditText.removeTextChangedListener(this);
                if (inputEditText.getId() == R.id.txt_dcpAmount) {
                    if(!Objects.requireNonNull(inputEditText.getText()).toString().isEmpty()) {
                        mViewModel.setAmount(Double.valueOf(inputEditText.getText().toString().replace(",", "")));
                    } else {
                        mViewModel.setAmount((double) 0);
                    }
                } else if (inputEditText.getId() == R.id.txt_dcpOthers) {
                    if(!Objects.requireNonNull(inputEditText.getText()).toString().isEmpty()) {
                        mViewModel.setPenalty(Double.valueOf(inputEditText.getText().toString().replace(",", "")));
                    } else {
                        mViewModel.setPenalty((double) 0);
                    }
                }
                inputEditText.addTextChangedListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            FormatCurrency(inputEditText);
        }

        private void FormatCurrency(TextInputEditText txt){
            try
            {
                txt.removeTextChangedListener(this);
                String value = Objects.requireNonNull(txt.getText()).toString();

                if (!value.equals(""))
                {

                    if(value.startsWith(".")){
                        txt.setText("0.");
                    }
                    if(value.startsWith("0") && !value.startsWith("0.")){
                        txt.setText("");

                    }

                    String str = txt.getText().toString().replaceAll(",", "");
                    txt.setText(getDecimalFormattedString(str));
                    txt.setSelection(txt.getText().toString().length());
                }
                txt.addTextChangedListener(this);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                txt.addTextChangedListener(this);
            }

        }
    }

    private static String getDecimalFormattedString(String value)
    {
        StringTokenizer lst = new StringTokenizer(value, ".");
        String str1 = value;
        String str2 = "";
        if (lst.countTokens() > 1)
        {
            str1 = lst.nextToken();
            str2 = lst.nextToken();
        }
        StringBuilder str3 = new StringBuilder();
        int i = 0;
        int j = -1 + str1.length();
        if (str1.charAt( -1 + str1.length()) == '.')
        {
            j--;
            str3 = new StringBuilder(".");
        }
        for (int k = j;; k--)
        {
            if (k < 0)
            {
                if (str2.length() > 0)
                    str3.append(".").append(str2);
                return str3.toString();
            }
            if (i == 3)
            {
                str3.insert(0, ",");
                i = 0;
            }
            str3.insert(0, str1.charAt(k));
            i++;
        }

    }
}