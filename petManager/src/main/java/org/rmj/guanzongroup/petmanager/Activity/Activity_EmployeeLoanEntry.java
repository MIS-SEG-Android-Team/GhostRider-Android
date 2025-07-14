package org.rmj.guanzongroup.petmanager.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.GCircle.room.Entities.EEmpLoan;
import org.rmj.g3appdriver.GCircle.room.Entities.ELoanTypes;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.petmanager.R;
import org.rmj.guanzongroup.petmanager.ViewModel.VMEmployeeLoanEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Activity_EmployeeLoanEntry extends AppCompatActivity {
    private static final String TAG = Activity_EmployeeLoanEntry.class.getSimpleName();
    private VMEmployeeLoanEntry mViewModel;
    private MaterialToolbar toolbar;
    private MaterialAutoCompleteTextView spn_loantype;
    private TextInputEditText txt_loanamt;
    private MaterialAutoCompleteTextView spn_terms;
    private TextInputEditText txt_firstpay;
    private TextInputEditText txt_amort;
    private TextInputEditText txt_totalinterest;
    private TextInputEditText txt_loandt;
    private TextInputEditText txt_purpose;
    private ConstraintLayout installment_layout;
    private MaterialButton btn_saveloanentry;
    private LoadDialog poDialog;
    private MessageBox poMessage;
    private HashMap<String, String> poLoanType; //todo: for id reference of loan type
    private Boolean isApproved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_loan_entry);

        mViewModel = new ViewModelProvider(this).get(VMEmployeeLoanEntry.class);

        //set declared  object value by getting id object from xml file
        toolbar = findViewById(R.id.toolbar);
        spn_loantype = findViewById(R.id.spn_loantype);
        txt_loandt = findViewById(R.id.txt_loandt);
        txt_loanamt = findViewById(R.id.txt_loanamt);
        spn_terms = findViewById(R.id.spn_terms);
        txt_purpose = findViewById(R.id.txt_purpose);
        txt_firstpay = findViewById(R.id.txt_firstpay);
        txt_amort = findViewById(R.id.txt_monthlypay);
        txt_totalinterest = findViewById(R.id.txt_totalintrst);
        installment_layout = findViewById(R.id.installment_layout);
        btn_saveloanentry = findViewById(R.id.btn_saveloanentry);

        poDialog = new LoadDialog(this);
        poMessage = new MessageBox(this);

        poLoanType = new HashMap<String, String>();

        poMessage.initDialog();
        poMessage.setTitle("Employee Loan");
        poMessage.setPositiveButton("Okay", new MessageBox.DialogButton() {
            @Override
            public void OnButtonClick(View view, AlertDialog dialog) {
                dialog.dismiss();
            }
        });

        /*TOOL BAR*/
        setSupportActionBar(toolbar); //set object toolbar as default action bar for activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set back button to toolbar

        mViewModel.GetLoanTypes().observe(this, new Observer<List<ELoanTypes>>() {
            @Override
            public void onChanged(List<ELoanTypes> eLoanTypes) {
                if (eLoanTypes != null){

                    List<String> adaptString = new ArrayList<>(); //todo: this is for adapter display

                    for (int i = 0; i < eLoanTypes.size(); i++){
                        poLoanType.put(eLoanTypes.get(i).getsLoanNmxx(), eLoanTypes.get(i).getsLoanIDxx());
                        adaptString.add(eLoanTypes.get(i).getsLoanNmxx());
                    }

                    spn_loantype.setAdapter(new ArrayAdapter<String>(Activity_EmployeeLoanEntry.this, android.R.layout.simple_spinner_dropdown_item, adaptString));
                }
            }
        });

        spn_terms.setAdapter(mViewModel.GetTerms());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txt_loandt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateTimePicker(txt_loandt);
            }
        });
        btn_saveloanentry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EEmpLoan foLoan = new EEmpLoan();
                    foLoan.setsTransNox(mViewModel.GenerateID());
                    foLoan.setsEmployID(mViewModel.GetEmpID());
                    foLoan.setsLoanIDxx(poLoanType.get(spn_loantype.getText().toString()));
                    foLoan.setdTransact(mViewModel.CurrentDate());

                    SimpleDateFormat sSavedFormat = new SimpleDateFormat("MMMM dd, yyyy");
                    SimpleDateFormat sNewFormat = new SimpleDateFormat("yyyy-MM-dd");

                    Date dLoanDt = sSavedFormat.parse(txt_loandt.getText().toString());
                    foLoan.setdLoanDate(sNewFormat.format(dLoanDt));

                    foLoan.setnLoanAmtxx(Integer.parseInt(txt_loanamt.getText().toString()));
                    foLoan.setnPaymTerm(Integer.parseInt(spn_terms.getText().toString()));
                    foLoan.setsPurposed(txt_purpose.getText().toString());

                    if (isApproved){
                        Date dFirstPayDt = sSavedFormat.parse(txt_firstpay.getText().toString());
                        foLoan.setdFirstPay(sNewFormat.format(dFirstPayDt));
                    }

                    mViewModel.SaveLoanEntry(foLoan, new VMEmployeeLoanEntry.OnSaveEntry() {
                        @Override
                        public void onLoad(String title, String message) {
                            poDialog.initDialog(title, message, false);
                            poDialog.show();
                        }
                        @Override
                        public void onSuccess(String message) {
                            poDialog.dismiss();
                            clearForms();

                            poMessage.setMessage("Loan saved");
                            poMessage.show();
                        }
                        @Override
                        public void onFailed(String message) {
                            poDialog.dismiss();

                            poMessage.setMessage(message);
                            poMessage.show();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    public void clearForms(){
        spn_loantype.setText("");
        txt_loandt.setText("");
        txt_loanamt.setText("");
        spn_terms.setText("");
        txt_purpose.setText("");
    }
    public void setDateTimePicker(TextInputEditText dtHolder){
        final Calendar newCalendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");

        final DatePickerDialog DialogTime = new DatePickerDialog(Activity_EmployeeLoanEntry.this,
                android.R.style.Theme_Holo_Dialog, (view131, year, monthOfYear, dayOfMonth) -> {

            Calendar selectDate = Calendar.getInstance();
            selectDate.set(year, monthOfYear, dayOfMonth);

            String lsDate = dateFormatter.format(selectDate.getTime());
            dtHolder.setText(lsDate);

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        DialogTime.show();
    }
}