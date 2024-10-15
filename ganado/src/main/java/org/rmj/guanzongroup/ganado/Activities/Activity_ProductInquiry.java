package org.rmj.guanzongroup.ganado.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DGanadoOnline;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.g3appdriver.lib.Ganado.Obj.Ganado;
import org.rmj.g3appdriver.lib.Ganado.model.GConstants;
import org.rmj.g3appdriver.lib.Ganado.pojo.InstallmentInfo;
import org.rmj.g3appdriver.utils.ImageFileManager;
import org.rmj.guanzongroup.ganado.R;
import org.rmj.guanzongroup.ganado.ViewModel.OnSaveInfoListener;
import org.rmj.guanzongroup.ganado.ViewModel.VMProductInquiry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Activity_ProductInquiry extends AppCompatActivity {
    private VMProductInquiry mViewModel;
    private MessageBox poMessage;
    private MaterialTextView txtBrandNm, txtModelNm, txtModelCd, txtyourmnmdp;
    private TextInputEditText txtCashPrce, txtDownPymnt, txtAmort, txtDTarget;
    private MaterialAutoCompleteTextView spn_color, spnPayment, spnAcctTerm;
    private MaterialButton btnContinue,btnCalculate;
    private ShapeableImageView imgMC;
    private String lsModelID, lsBrandID, lsImgLink, lsBrandNm;
    private Double nMinDownPay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_inquiry);

        initWidgets();

        spnPayment.setAdapter(GConstants.getAdapter(Activity_ProductInquiry.this, GConstants.PAYMENT_FORM));
        spnAcctTerm.setText(GConstants.INSTALLMENT_TERM[0]);
        spnAcctTerm.setAdapter(GConstants.getAdapter(Activity_ProductInquiry.this, GConstants.INSTALLMENT_TERM));
        mViewModel.setBrandID(getIntent().getStringExtra("lsBrandID"));
        mViewModel.setModelID(getIntent().getStringExtra("lsModelID"));

        lsBrandID = getIntent().getStringExtra("lsBrandID");
        lsModelID = getIntent().getStringExtra("lsModelID");
        lsImgLink = getIntent().getStringExtra("lsImgLink");
        lsBrandNm = getIntent().getStringExtra("lsBrandNm");

        mViewModel.getModel().setBrandIDx(lsBrandID);
        mViewModel.getModel().setModelIDx(lsModelID);
        mViewModel.getModel().setTermIDxx("36");

        mViewModel.GetModelBrand(lsBrandID, lsModelID).observe(Activity_ProductInquiry.this, eMcModel -> {
            try {
                txtModelCd.setText(eMcModel.getModelCde());
                txtModelNm.setText(eMcModel.getModelNme());
                txtBrandNm.setText(getIntent().getStringExtra("lsBrandNm"));
                ImageFileManager.LoadImageToView(lsImgLink, imgMC);
            }catch (NullPointerException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        mViewModel.GetModelColor(lsModelID).observe(Activity_ProductInquiry.this, colorList->{
            try {
                ArrayList<String> string = new ArrayList<>();
                for (int x = 0; x < colorList.size(); x++) {
                    String lsColor = colorList.get(x).getColorNme();
                    //                        String lsTown =  loList.get(x).sProvName ;
                    string.add(lsColor);
                }

                ArrayAdapter<String> adapters = new ArrayAdapter<>(Activity_ProductInquiry.this, android.R.layout.simple_spinner_dropdown_item, string.toArray(new String[0]));
                spn_color.setAdapter(adapters);
            } catch (NullPointerException e){
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        spn_color.setOnItemClickListener(new OnItemClickListener(spn_color));
        spnAcctTerm.setOnItemClickListener(new OnItemClickListener(spnAcctTerm));
        txtDownPymnt.addTextChangedListener(new FormatUIText.CurrencyFormat(txtDownPymnt));

        mViewModel.GetModelID().observe(Activity_ProductInquiry.this, modelID -> {
            try{
                mViewModel.GetCashPrice(modelID).observe(Activity_ProductInquiry.this, cashPrice -> {
                    try{
                        txtCashPrce.setText(FormatUIText.getCurrencyUIFormat(String.valueOf(cashPrice.CashPrce)));
                        mViewModel.getModel().setCashPrce(cashPrice.CashPrce);
                        mViewModel.getModel().setPricexxx(cashPrice.Pricedxx);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                });

                mViewModel.GetMinimumDownpayment(modelID, new VMProductInquiry.OnRetrieveInstallmentInfo() {
                    @Override
                    public void OnRetrieve(InstallmentInfo loResult) {
                        nMinDownPay = loResult.getMinimumDownpayment();

                        mViewModel.getModel().setDownPaym(String.valueOf(loResult.getMinimumDownpayment()));
                        txtDownPymnt.setText(String.valueOf(loResult.getMinimumDownpayment()));
                        txtyourmnmdp.setText("The required Minimum Down Payment is at least " + String.valueOf(loResult.getMinimumDownpayment()));

                        mViewModel.getModel().setMonthAmr(String.valueOf(loResult.getMonthlyAmortization()));
                        txtAmort.setText(FormatUIText.getCurrencyUIFormat(String.valueOf(loResult.getMonthlyAmortization())));
                    }

                    @Override
                    public void OnFailed(String message) {

                    }
                });
            } catch (NullPointerException e){
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        txtDTarget.setOnClickListener(v -> {
            final Calendar newCalendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");

            // Set the maximum date to one month from the current date
            newCalendar.add(Calendar.MONTH, 1);
            long maxDateInMillis = newCalendar.getTimeInMillis();

            final DatePickerDialog StartTime = new DatePickerDialog(Activity_ProductInquiry.this,
                    android.R.style.Theme_Holo_Dialog, (view131, year, monthOfYear, dayOfMonth) -> {
                try {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);

                    // Check if the selected date is within one month from the current date
                    if (newDate.getTimeInMillis() <= maxDateInMillis) {
                        String lsDate = dateFormatter.format(newDate.getTime());
                        txtDTarget.setText(lsDate);
                        Date loDate = new SimpleDateFormat("MMMM dd, yyyy").parse(lsDate);
                        lsDate = new SimpleDateFormat("yyyy-MM-dd").format(loDate);
                        mViewModel.getModel().setTargetxx(lsDate);
                    } else {
                        // Show an error message or handle the case when the selected date is outside the allowed range
                        Toast.makeText(Activity_ProductInquiry.this, "Please select a date within one month from the current date", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

            StartTime.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            StartTime.getDatePicker().setMaxDate(maxDateInMillis); // Set the maximum date

            StartTime.show();
        });

        spnPayment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.getModel().setPaymForm(String.valueOf(position));
                if(position == 0){
                    findViewById(R.id.til_cashPrice).setVisibility(View.VISIBLE);
                    findViewById(R.id.linearInstallment).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.til_cashPrice).setVisibility(View.GONE);
                    findViewById(R.id.linearInstallment).setVisibility(View.VISIBLE);
                }
            }
        });

        btnCalculate.setOnClickListener(view -> {
            String lsInput = txtDownPymnt.getText().toString().trim();
            Double lnInput = FormatUIText.getParseDouble(lsInput);

            if (nMinDownPay > lnInput){
                poMessage.initDialog();
                poMessage.setTitle("Product Inquiry");
                poMessage.setMessage("The downpayment should not be less than the minimum required amount");
                poMessage.setPositiveButton("Okay", (view1, dialog) -> dialog.dismiss());
                poMessage.show();

                return;
            }

            mViewModel.getModel().setDownPaym(String.valueOf(lnInput));

            mViewModel.CalculateNewDownpayment(lsModelID, Integer.parseInt(mViewModel.getModel().getTermIDxx()), lnInput, new VMProductInquiry.OnCalculateNewDownpayment() {
                @Override
                public void OnCalculate(double lnResult) {
                    txtAmort.setText(FormatUIText.getCurrencyUIFormat(String.valueOf(lnResult)));
                    mViewModel.getModel().setMonthAmr(String.valueOf(lnResult));
                }

                @Override
                public void OnFailed(String message) {
                    poMessage.initDialog();
                    poMessage.setTitle("Product Inquiry");
                    poMessage.setMessage(message);
                    poMessage.setPositiveButton("Okay", (view1, dialog) -> dialog.dismiss());
                    poMessage.show();
                    txtAmort.setText("0");
                    mViewModel.getModel().setMonthAmr("0");
                }
            });
        });

        btnContinue.setOnClickListener(view ->{
            String lsInput = txtDownPymnt.getText().toString().trim();
            Double lnInput = FormatUIText.getParseDouble(lsInput);

            if (nMinDownPay > lnInput){
                poMessage.initDialog();
                poMessage.setTitle("Product Inquiry");
                poMessage.setMessage("The downpayment should not be less than the minimum required amount");
                poMessage.setPositiveButton("Okay", (view1, dialog) -> dialog.dismiss());
                poMessage.show();

                return;
            }

            mViewModel.getModel().setDownPaym(String.valueOf(lnInput));

            mViewModel.CalculateNewDownpayment(lsModelID, Integer.parseInt(mViewModel.getModel().getTermIDxx()), lnInput, new VMProductInquiry.OnCalculateNewDownpayment() {
                @Override
                public void OnCalculate(double lnResult) {
                    txtAmort.setText(FormatUIText.getCurrencyUIFormat(String.valueOf(lnResult)));
                    mViewModel.getModel().setMonthAmr(String.valueOf(lnResult));

                    mViewModel.SaveData(new OnSaveInfoListener() {
                        @Override
                        public void OnSave(String args) {
                            Intent loIntent = new Intent(Activity_ProductInquiry.this, Activity_ClientInfo.class);
                            loIntent.putExtra("sTransNox", args);
                            startActivity(loIntent);
                            overridePendingTransition(R.anim.anim_intent_slide_in_right, R.anim.anim_intent_slide_out_left);
                        }

                        @Override
                        public void OnFailed(String message) {
                            poMessage.initDialog();
                            poMessage.setTitle("Product Inquiry");
                            poMessage.setMessage(message);
                            poMessage.setPositiveButton("Okay", (view1, dialog) -> dialog.dismiss());
                            poMessage.show();
                        }
                    });

                }

                @Override
                public void OnFailed(String message) {
                    poMessage.initDialog();
                    poMessage.setTitle("Product Inquiry");
                    poMessage.setMessage(message);
                    poMessage.setPositiveButton("Okay", (view1, dialog) -> dialog.dismiss());
                    poMessage.show();
                    txtAmort.setText("0");
                    mViewModel.getModel().setMonthAmr("0");
                }
            });
        });
    }
    private void initWidgets(){
        mViewModel = new ViewModelProvider(Activity_ProductInquiry.this).get(VMProductInquiry.class);
        poMessage = new MessageBox(Activity_ProductInquiry.this);
        MaterialToolbar toolbar = findViewById(R.id.toolbar_Inquiry);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        txtBrandNm = findViewById(R.id.lblBrand);
        txtModelCd = findViewById(R.id.lblModelCde);
        txtModelNm = findViewById(R.id.lblModelNme);
        txtCashPrce = findViewById(R.id.txt_cashPrice);
        txtDownPymnt = findViewById(R.id.txt_downpayment);
        txtAmort = findViewById(R.id.txt_monthlyAmort);
        txtDTarget = findViewById(R.id.txt_targetDate);
        spnPayment = findViewById(R.id.spn_paymentMethod);
        spnAcctTerm = findViewById(R.id.spn_installmentTerm);
        spn_color = findViewById(R.id.spn_color);
        imgMC = findViewById(R.id.imgMC);
        txtyourmnmdp = findViewById(R.id.txtyourmnmdp);

        btnContinue = findViewById(R.id.btnContinue);
        btnCalculate = findViewById(R.id.btnCalculate);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed () {
        finish();
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }
    @Override
    protected void onDestroy () {
        getViewModelStore().clear();
        super.onDestroy();
    }
    private class OnItemClickListener implements AdapterView.OnItemClickListener {

        private final View loView;

        public OnItemClickListener(View loView) {
            this.loView = loView;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(loView == spn_color){
                mViewModel.GetModelColor(lsModelID).observe(Activity_ProductInquiry.this, colorList->{
                    mViewModel.getModel().setColorIDx(colorList.get(i).getColorIDx());

                });
            } else if(loView == spnAcctTerm){
                if(i==0){
                    mViewModel.getModel().setTermIDxx("36");
                }else if(i==1){
                    mViewModel.getModel().setTermIDxx("24");
                }else if(i==2){
                    mViewModel.getModel().setTermIDxx("18");
                }else if(i==3){
                    mViewModel.getModel().setTermIDxx("12");
                }else if(i==4){
                    mViewModel.getModel().setTermIDxx("6");
                }

                String lsInput = txtDownPymnt.getText().toString().trim();
                Double lnInput = FormatUIText.getParseDouble(lsInput);

                if (nMinDownPay > lnInput){
                    poMessage.initDialog();
                    poMessage.setTitle("Product Inquiry");
                    poMessage.setMessage("The downpayment should not be less than the minimum required amount");
                    poMessage.setPositiveButton("Okay", (view1, dialog) -> dialog.dismiss());
                    poMessage.show();

                    return;
                }

                mViewModel.getModel().setDownPaym(String.valueOf(lnInput));

                double lnMonthly = mViewModel.GetMonthlyAmortization(Integer.parseInt(mViewModel.getModel().getTermIDxx()));
                txtAmort.setText(FormatUIText.getCurrencyUIFormat(String.valueOf(lnMonthly)));

                mViewModel.CalculateNewDownpayment(lsModelID, Integer.parseInt(mViewModel.getModel().getTermIDxx()), lnInput, new VMProductInquiry.OnCalculateNewDownpayment() {
                    @Override
                    public void OnCalculate(double lnResult) {
                        txtAmort.setText(FormatUIText.getCurrencyUIFormat(String.valueOf(lnResult)));
                        mViewModel.getModel().setMonthAmr(String.valueOf(lnResult));
                    }

                    @Override
                    public void OnFailed(String message) {
                        txtAmort.setText("0");

                        mViewModel.getModel().setMonthAmr("0");
                    }
                });
            }
        }
    }
}
