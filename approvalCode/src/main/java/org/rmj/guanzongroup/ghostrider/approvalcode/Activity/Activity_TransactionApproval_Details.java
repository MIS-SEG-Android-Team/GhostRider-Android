package org.rmj.guanzongroup.ghostrider.approvalcode.Activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import org.rmj.g3appdriver.GCircle.room.Entities.ECASRequests;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.approvalcode.R;
import org.rmj.guanzongroup.ghostrider.approvalcode.ViewModel.VMApprovalSelection;

public class Activity_TransactionApproval_Details extends AppCompatActivity {

    private String lsTransNox;
    private String lsMode;

    private MessageBox loMessage;
    private VMApprovalSelection mViewModel;

    private MaterialTextView mtv_transactno, mtv_dtransact, mtv_status, mtv_sourceno, mtv_recipient, mtv_industry, mtv_source, mtv_remarks;
    private MaterialButton btn_disapprove, btn_approve;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_transactionapproval_details);

        loMessage = new MessageBox(this);

        if (!getIntent().hasExtra("transnox")){
            InitMessage(2, "Transaction number not found", new onMessageButton() {
                @Override
                public void onPositive() {
                    finish();
                }

                @Override
                public void onNegative() {

                }
            });
        } else if (!getIntent().hasExtra("mode")){
            InitMessage(2, "Could process transaction details", new onMessageButton() {
                @Override
                public void onPositive() {
                    finish();
                }

                @Override
                public void onNegative() {

                }
            });
        }

        mViewModel = new ViewModelProvider(this).get(VMApprovalSelection.class);

        lsTransNox = getIntent().getStringExtra("transnox");
        lsMode = getIntent().getStringExtra("mode");

        mtv_transactno = findViewById(R.id.mtv_transactno);
        mtv_dtransact = findViewById(R.id.mtv_dtransact);
        mtv_status = findViewById(R.id.mtv_status);
        mtv_sourceno = findViewById(R.id.mtv_sourceno);
        mtv_recipient = findViewById(R.id.mtv_recipient);
        mtv_industry = findViewById(R.id.mtv_industry);
        mtv_source = findViewById(R.id.mtv_source);
        mtv_remarks = findViewById(R.id.mtv_remarks);

        btn_disapprove = findViewById(R.id.btn_disapprove);
        btn_approve = findViewById(R.id.btn_approve);

        if (lsMode.equals("4")){
            btn_disapprove.setVisibility(View.GONE);
            btn_approve.setVisibility(View.GONE);
        }else {
            btn_disapprove.setVisibility(View.VISIBLE);
            btn_approve.setVisibility(View.VISIBLE);
        }

        InitDetails();

    }

    private void InitDetails(){
        mViewModel.GetRequestDetail(lsTransNox).observe(Activity_TransactionApproval_Details.this, new Observer<ECASRequests>() {
            @Override
            public void onChanged(ECASRequests ecasRequests) {

                mtv_transactno.setText(ecasRequests.getsTransNox());
                mtv_dtransact.setText(ecasRequests.getdTransact());
                mtv_sourceno.setText(ecasRequests.getsSourceNo());
                mtv_recipient.setText(ecasRequests.getsCompnyNm());
                mtv_industry.setText(ecasRequests.getsDescript());
                mtv_remarks.setText(ecasRequests.getsRemarksx());

                if (!getIntent().hasExtra("source")){
                    mtv_source.setText(mViewModel.getDescription(ecasRequests.getsSourceCD()));
                } else {
                    mtv_source.setText(getIntent().getStringExtra("source"));
                }

                switch (ecasRequests.getcTranStat()){
                    case "0":
                        mtv_status.setText("Pending");
                        break;
                    case "1":
                        mtv_status.setText("Approved");
                        mtv_status.setTextColor(Color.GREEN);
                        break;
                    case "3":
                        mtv_status.setText("Disapproved");
                        mtv_status.setTextColor(Color.RED);
                        break;
                }
            }
        });
    }

    private void InitMessage(int mode, String message, onMessageButton callback){

        loMessage.initDialog();
        loMessage.setTitle("Guanzon Circle");
        loMessage.setMessage(message);

        switch (mode){

            case 1, 2:
                if (mode == 1){
                    loMessage.setIcon(R.drawable.baseline_message_24);
                }

                if (mode == 2){
                    loMessage.setIcon(R.drawable.baseline_error_24);
                }

                loMessage.setPositiveButton("Okay", new MessageBox.DialogButton() {
                    @Override
                    public void OnButtonClick(View view, AlertDialog dialog) {
                        dialog.dismiss();
                        callback.onPositive();
                    }
                });

                break;

            case 3:
                loMessage.setIcon(R.drawable.baseline_contact_support_24);
                loMessage.setPositiveButton("Yes", new MessageBox.DialogButton() {
                    @Override
                    public void OnButtonClick(View view, AlertDialog dialog) {
                        dialog.dismiss();
                        callback.onPositive();
                    }
                });
                loMessage.setNegativeButton("No", new MessageBox.DialogButton() {
                    @Override
                    public void OnButtonClick(View view, AlertDialog dialog) {
                        dialog.dismiss();
                        callback.onNegative();
                    }
                });

                break;
        }

        loMessage.show();
    }

    private interface onMessageButton{
        void onPositive();
        void onNegative();
    }
}
