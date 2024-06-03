package org.rmj.guanzongroup.petmanager.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.GCircle.room.Entities.ELoanTypes;
import org.rmj.guanzongroup.petmanager.R;
import org.rmj.guanzongroup.petmanager.ViewModel.VMEmployeeLoanEntry;

import java.util.ArrayList;
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
    private MaterialButton btn_saveloanentry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_loan_entry);

        mViewModel = new ViewModelProvider(this).get(VMEmployeeLoanEntry.class);

        //set declared  object value by getting id object from xml file
        toolbar = findViewById(R.id.toolbar);
        spn_loantype = findViewById(R.id.spn_loantype);
        txt_loanamt = findViewById(R.id.txt_loanamt);
        spn_terms = findViewById(R.id.spn_terms);
        txt_firstpay = findViewById(R.id.txt_firstpay);
        txt_amort = findViewById(R.id.txt_monthlypay);
        txt_totalinterest = findViewById(R.id.txt_totalintrst);
        txt_loandt = findViewById(R.id.txt_loandt);
        btn_saveloanentry = findViewById(R.id.btn_saveloanentry);

        /*TOOL BAR*/
        setSupportActionBar(toolbar); //set object toolbar as default action bar for activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set back button to toolbar

        mViewModel.GetLoanTypes().observe(this, new Observer<List<ELoanTypes>>() {
            @Override
            public void onChanged(List<ELoanTypes> eLoanTypes) {
                if (eLoanTypes != null){

                    HashMap<String, String> mapVal = new HashMap<>(); //todo: this is for selected value reference
                    List<String> adaptString = new ArrayList<>(); //todo: this is for adapter display

                    for (int i = 0; i < eLoanTypes.size(); i++){
                        mapVal.put(eLoanTypes.get(i).getsLoanIDxx(), eLoanTypes.get(i).getsLoanNmxx());
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
        btn_saveloanentry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}