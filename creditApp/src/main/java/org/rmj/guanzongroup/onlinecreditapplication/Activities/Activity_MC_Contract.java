package org.rmj.guanzongroup.onlinecreditapplication.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppConstants;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditOnlineApplication;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplication;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.onlinecreditapplication.R;
import org.rmj.guanzongroup.onlinecreditapplication.ViewModel.VMCreditApplications;

import java.util.ArrayList;
import java.util.List;

public class Activity_MC_Contract extends AppCompatActivity {

    private VMCreditApplications mViewModel;
    private LoadDialog poDialogx;
    private MessageBox poMessage;
    private List<String> laSerials = new ArrayList<>();

    private TextInputEditText tie_branch, tie_transaction, tie_client, tie_account, tie_downpay, tie_monthly, tie_remarks;
    private MaterialAutoCompleteTextView auto_serial, auto_term;
    private MaterialButton btn_download, btn_submit;

    public interface OnMessageButton {
        void OnPositive();
        void OnNegative();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mcontract);

        mViewModel = new ViewModelProvider(Activity_MC_Contract.this).get(VMCreditApplications.class);
        poDialogx = new LoadDialog(Activity_MC_Contract.this);
        poMessage = new MessageBox(Activity_MC_Contract.this);

        setContentView(R.layout.activity_mcontract);

        if (!getIntent().hasExtra("sTransNox")){
            InitMessage(0, R.drawable.baseline_error_24, "Invalid transactio number", "Okay", "", new Activity_CreditApplications.OnMessageButton() {
                @Override
                public void OnPositive() {
                    finish();
                }

                @Override
                public void OnNegative() {}
            });
            return;
        }

        InitWidgets();
        InitData();
    }

    private void InitWidgets(){
        tie_branch = findViewById(R.id.tie_branch);
        tie_transaction = findViewById(R.id.tie_transaction);
        tie_client = findViewById(R.id.tie_client);
        tie_account = findViewById(R.id.tie_account);
        tie_downpay = findViewById(R.id.tie_downpay);
        tie_monthly = findViewById(R.id.tie_monthly);
        tie_remarks = findViewById(R.id.tie_remarks);
        auto_serial = findViewById(R.id.auto_serial);
        auto_term = findViewById(R.id.auto_term);
        btn_submit = findViewById(R.id.btn_submit);
    }

    private void InitData(){

        try {

            ECreditApplication loApp = mViewModel.GetApplication(getIntent().getStringExtra("sTransNox"));
            if (loApp == null){
                return;
            }

            if (mViewModel.getBranchInfo(loApp.getBranchCd()) == null){
                tie_branch.setText(loApp.getBranchCd());
            }else {
                EBranchInfo loBranch = mViewModel.getBranchInfo(loApp.getBranchCd());
                Toast.makeText(Activity_MC_Contract.this, loBranch.getBranchNm(), Toast.LENGTH_LONG).show();
                tie_branch.setText(loBranch.getBranchNm());
            }
            auto_term.setAdapter(CreditAppConstants.getAdapter(Activity_MC_Contract.this, CreditAppConstants.INSTALLMENT_TERM));

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void InitListener(){

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (auto_serial.getText() == null || auto_serial.getText().toString().trim().isEmpty()){

                    InitMessage(0, R.drawable.ic_toast_warning, "Serial ID is required", "Okay", "", new Activity_CreditApplications.OnMessageButton() {
                        @Override
                        public void OnPositive() {}

                        @Override
                        public void OnNegative() {}
                    });
                    return;
                }

               mViewModel.GetSerials(auto_serial.getText().toString(), new VMCreditApplications.OnSearchLSerial() {
                   @Override
                   public void OnSuccess(List<CreditOnlineApplication.MCSerial> laResult) {

                       for (CreditOnlineApplication.MCSerial loSerial : laResult) {
                           laSerials.add(loSerial.lsSerialID);
                       }

                       auto_serial.setAdapter(new ArrayAdapter<>(Activity_MC_Contract.this,
                               android.R.layout.simple_list_item_1, CreditAppConstants.CO_MAKER_INCOME_SOURCE));
                   }

                   @Override
                   public void OnFailed(String message) {

                   }
               });

            }
        });
    }

    private void InitMessage(int messageType, int statusIcon, String message, String posText, String negText, Activity_CreditApplications.OnMessageButton callback){

        poMessage.initDialog();
        poMessage.setTitle("Credit Application");
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
}
