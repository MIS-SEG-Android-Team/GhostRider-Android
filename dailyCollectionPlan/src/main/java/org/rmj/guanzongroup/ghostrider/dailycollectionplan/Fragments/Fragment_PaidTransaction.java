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
import static org.rmj.guanzongroup.ghostrider.dailycollectionplan.Etc.DCP_Constants.GetPaymentTypeIndex;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import  com.google.android.material.checkbox.MaterialCheckBox;

import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.PaidDCP;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_Transaction;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Dialog.DialogCheckPayment;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Dialog.DialogDisclosure;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Etc.DCP_Constants;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.VMPaidTransaction;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel.ViewModelCallback;

import java.io.File;
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

    private MaterialCheckBox cbCheckPymnt;
    private MaterialTextView lblBranch, lblAddress, lblAccNo, lblClientNm, lblTransNo;
    private MaterialAutoCompleteTextView spnType;
    private TextInputEditText txtPrNoxx, txtRemarks, txtAmount, txtRebate, txtOthers, txtTotAmnt;
    private TextInputLayout tilDiscount, tilPenaly;
    private MaterialButton btnAmort, btnRBlnce, btnClear;
    private MaterialButton btnConfirm;

    private DialogDisclosure dialogDisclosure;
//    private final ActivityResultLauncher<Intent> poCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//
//        if(result.getResultCode() == RESULT_OK) {
//
//            try {
//
//                //TODO: 1. GET COORDINATES OF CAPTURED IMAGE'S PROPERTIES,
//                // NOTE: TO GET THIS PROPERLY. ENABLE MANUALLY THE TAG LOCATION SETTINGS ON CAMERA WITHIN THE APP
//                @SuppressLint({"NewApi", "LocalSuppress"}) ExifInterface exifInterface =
//                        new ExifInterface(
//                                Objects.requireNonNull(requireContext().getContentResolver().openInputStream(
//                                        MediaStore.setRequireOriginal(Uri.fromFile(new File(poRem.getFilePath())))
//                                ))
//                        );
//
//                //TODO: 2. SET IMAGE COORDINATES, IF NOT EMPTY
//                if (exifInterface.getLatLong() != null){
//
//                    Log.d("DCP Fragment", "Image Longitude is " + String.valueOf(Objects.requireNonNull(exifInterface.getLatLong())[1])
//                            + " and Image Latitude is " + String.valueOf(exifInterface.getLatLong()[0]));
//
//                    poRem.setLatitude(String.valueOf(exifInterface.getLatLong()[0]));
//                    poRem.setLongtude(String.valueOf(exifInterface.getLatLong()[1]));
//                }
//
//                //TODO: 3. VALIDATE SAVED COORDINATES
//                if (poRem.getLongtude() == null || poRem.getLatitude() == null){
//                    InitMessage(0, R.drawable.baseline_error_24, "Unable to get location coordinates. Please inform your superior for this matter.", "Okay", "", new Fragment_IncTransaction.OnDialogButtonCallback() {
//                        @Override
//                        public void OnPositive() {}
//
//                        @Override
//                        public void OnNegative() {}
//                    });
//                    return;
//                }
//
//                if (poRem.getLongtude().isEmpty() || poRem.getLatitude().isEmpty()){
//                    InitMessage(0, R.drawable.baseline_error_24, "Location coordinates is empty. Please inform your superior for this matter.", "Okay", "", new Fragment_IncTransaction.OnDialogButtonCallback() {
//                        @Override
//                        public void OnPositive() {}
//
//                        @Override
//                        public void OnNegative() {}
//                    });
//                    return;
//                }
//
//                if (poRem.getLongtude().equalsIgnoreCase("0.00000000000") || poRem.getLatitude().equalsIgnoreCase("0.00000000000")) {
//                    InitMessage(0, R.drawable.baseline_error_24, "Location coordinates is invalid. Please inform your superior for this matter.", "Okay", "", new Fragment_IncTransaction.OnDialogButtonCallback() {
//                        @Override
//                        public void OnPositive() {}
//
//                        @Override
//                        public void OnNegative() {}
//                    });
//                    return;
//                }
//
//                mViewModel.SaveTransaction(poRem, new ViewModelCallback() {
//                    @Override
//                    public void OnStartSaving() {
//                        poDialog.initDialog("Selfie Log", "Saving transaction. Please wait...", false);
//                        poDialog.show();
//                    }
//
//                    @Override
//                    public void OnSuccessResult() {
//                        InitMessage(0, R.drawable.baseline_message_24, "Collection detail has been save.", "Okay", "", new Fragment_IncTransaction.OnDialogButtonCallback() {
//                            @Override
//                            public void OnPositive() {
//                                requireActivity().finish();
//                            }
//                            @Override
//                            public void OnNegative() {}
//                        });
//                    }
//
//                    @Override
//                    public void OnFailedResult(String message) {
//                        InitMessage(0, R.drawable.baseline_error_24, message, "Okay", "", new Fragment_IncTransaction.OnDialogButtonCallback() {
//                            @Override
//                            public void OnPositive() {
//                                requireActivity().finish();
//                            }
//                            @Override
//                            public void OnNegative() {}
//                        });
//                    }
//                });
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }else {
//
//            InitMessage(0, R.drawable.baseline_error_24, "Error capturing image", "Okay", "", new Fragment_IncTransaction.OnDialogButtonCallback() {
//                @Override
//                public void OnPositive() {}
//                @Override
//                public void OnNegative() {}
//            });
//        }
//    });

    private interface OnDialogButtonCallback{
        void OnPositive();
        void OnNegative();
    }

    public static Fragment_PaidTransaction newInstance() {
        return new Fragment_PaidTransaction();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_paid_transaction, container, false);

        mViewModel = new ViewModelProvider(this).get(VMPaidTransaction.class);
        poPaid = new PaidDCP();
        poMessage = new MessageBox(requireActivity());
        poDialog = new LoadDialog(requireActivity());
        dialogDisclosure = new DialogDisclosure(requireActivity());

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

                btnAmort.setText("Amortization : " + FormatUIText.getCurrencyUIFormat(String.valueOf(detail.getMonAmort())));
                btnRBlnce.setText("Amount Due : " + FormatUIText.getCurrencyUIFormat(String.valueOf(detail.getAmtDuexx())));
                SimpleDateFormat loFormatter = new SimpleDateFormat("yyyy-MM-dd");

                String lsDayDuex = detail.getDueDatex().split("-")[2];
                //Check here if the due date is on the maximum days per month
                // if true check the maximum day of month and set it as the due date for this current month...
                if(lsDayDuex.equalsIgnoreCase("31")) {
                    LocalDate lastDayOfMonth = LocalDate.parse(AppConstants.CURRENT_DATE(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            .with(TemporalAdjusters.lastDayOfMonth());
                    lsDayDuex = String.valueOf(lastDayOfMonth.getDayOfMonth());
                }
                String lsCrtYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
                String lsCrtMnth = new SimpleDateFormat("MM", Locale.getDefault()).format(Calendar.getInstance().getTime());
                String lsDueDate = lsCrtYear + "-" + lsCrtMnth + "-" + lsDayDuex;
                Date ldDueDatex = new SimpleDateFormat("yyyy-MM-dd").parse(lsDueDate);
                Date loCrtDate = loFormatter.parse(AppConstants.CURRENT_DATE());
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
                    txtAmount.setText(String.valueOf(detail.getMonAmort()));
                });

                btnRBlnce.setOnClickListener(v -> {
                    mViewModel.setAmount(Double.valueOf(detail.getAmtDuexx()));
                    txtAmount.setText(String.valueOf(detail.getAmtDuexx()));
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

        spnType.setAdapter(new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, DCP_Constants.PAYMENT_TYPE));
        spnType.setSelection(0);

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
                    double lnInput = Double.valueOf(txtRebate.getText().toString().replace(",", ""));
                    if(!mViewModel.setRebate(lnInput)){
                        txtRebate.setText("0.0");
                    }
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
            poPaid.setPayment(GetPaymentTypeIndex(spnType.getText().toString()));
            poPaid.setPrNoxxx(Objects.requireNonNull(txtPrNoxx.getText()).toString());
            poPaid.setRemarks(Objects.requireNonNull(txtRemarks.getText()).toString());
            poPaid.setAmountx(FormatUIText.getParseDouble(Objects.requireNonNull(txtAmount.getText()).toString()));
            poPaid.setDscount(FormatUIText.getParseDouble(Objects.requireNonNull(txtRebate.getText()).toString()));
            poPaid.setOthersx(FormatUIText.getParseDouble(Objects.requireNonNull(txtOthers.getText()).toString()));
            poPaid.setTotAmnt(FormatUIText.getParseDouble(Objects.requireNonNull(txtTotAmnt.getText()).toString()));

            mViewModel.SavePaymentInfo(poPaid, Fragment_PaidTransaction.this);
        });

        return view;
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

    private static String getDecimalFormattedString(String value) {
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

    private void InitMessage(int messageType, int statusIcon, String message, String posText, String negText, Fragment_IncTransaction.OnDialogButtonCallback callback){

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

    @Override
    public void OnStartSaving() {
        poDialog.initDialog("Daily Collection Plan", "Posting transaction.Please wait...", false);
        poDialog.show();
    }

    @Override
    public void OnSuccessResult() {
        poDialog.dismiss();
        poMessage.initDialog();
        poMessage.setIcon(R.drawable.baseline_message_24);
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
        poMessage.setIcon(R.drawable.baseline_error_24);
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

}