package org.rmj.guanzongroup.ghostrider.approvalcode.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import org.rmj.g3appdriver.GCircle.room.Entities.ECASRequests;
import org.rmj.g3appdriver.etc.FileViewer.FileViewer;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.approvalcode.R;
import org.rmj.guanzongroup.ghostrider.approvalcode.ViewModel.VMApprovalSelection;

public class Activity_TransactionApproval_Details extends AppCompatActivity {

    private String lsTransNox;
    private String lsMode;

    private LoadDialog loDialog;
    private MessageBox loMessage;
    private VMApprovalSelection mViewModel;

    private MaterialTextView mtv_transactno, mtv_dtransact, mtv_status, mtv_sourceno, mtv_recipient, mtv_industry, mtv_source, mtv_remarks, btn_view_attachment;
    private MaterialToolbar toolbar;
    private MaterialButton btn_disapprove, btn_approve;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_transactionapproval_details);

        loDialog = new LoadDialog(this);
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

        toolbar = findViewById(R.id.toolbar);
        mtv_transactno = findViewById(R.id.mtv_transactno);
        mtv_dtransact = findViewById(R.id.mtv_dtransact);
        mtv_status = findViewById(R.id.mtv_status);
        mtv_sourceno = findViewById(R.id.mtv_sourceno);
        mtv_recipient = findViewById(R.id.mtv_recipient);
        mtv_industry = findViewById(R.id.mtv_industry);
        mtv_source = findViewById(R.id.mtv_source);
        mtv_remarks = findViewById(R.id.mtv_remarks);
        btn_view_attachment = findViewById(R.id.btn_view_attachment);

        btn_disapprove = findViewById(R.id.btn_disapprove);
        btn_approve = findViewById(R.id.btn_approve);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Guanzon Circle");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (lsMode.equals("4")){
            btn_disapprove.setVisibility(View.GONE);
            btn_approve.setVisibility(View.GONE);
        }else {
            btn_disapprove.setVisibility(View.VISIBLE);
            btn_approve.setVisibility(View.VISIBLE);
        }

        InitDetails();

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_intent_slide_in_left, R.anim.anim_intent_slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void InitDetails(){

        mViewModel.GetRequestDetail(lsTransNox).observe(Activity_TransactionApproval_Details.this, new Observer<ECASRequests>() {
            @Override
            public void onChanged(ECASRequests ecasRequests) {

                if (ecasRequests == null){

                    InitMessage(2, "Transaction details not found", new onMessageButton() {
                        @Override
                        public void onPositive() {
                            finish();
                        }

                        @Override
                        public void onNegative() {

                        }
                    });

                    return;
                }

                //display transaction details
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

                //display status description
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

                //Initialize object listeners
                btn_approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ApproveRequest(ecasRequests.getsAuthType(), ecasRequests.getsTransNox(), "1");
                    }
                });

                btn_disapprove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ApproveRequest(ecasRequests.getsAuthType(), ecasRequests.getsTransNox(), "3");
                    }
                });

                btn_view_attachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent loIntent = new Intent(Activity_TransactionApproval_Details.this, FileViewer.class);
                        loIntent.putExtra("sSourceCd", ecasRequests.getsSourceCD());
                        loIntent.putExtra("sSourceNo", ecasRequests.getsSourceNo());

                        startActivity(loIntent);

                    }
                });

            }
        });
    }

    private void ApproveRequest(String fsAuthType, String fsTransNox, String fscTranStat){

        String lsMessage = "Are you sure you want to approve this request?";
        if (fscTranStat.equalsIgnoreCase("3")){
            lsMessage = "Are you sure you want to disapprove this request?";
        }

        InitMessage(3, lsMessage, new onMessageButton() {
            @Override
            public void onPositive() {

                mViewModel.updateCASRequest(fsAuthType, fsTransNox, fscTranStat, new VMApprovalSelection.OnTransaction() {
                    @Override
                    public void OnLoad(String fsTitle, String fsMessage) {
                        loDialog.initDialog(fsTitle, fsMessage, false);
                        loDialog.show();
                    }

                    @Override
                    public void OnSuccess() {
                        loDialog.dismiss();

                        String lsMessage = "Request has been approved!";
                        if (fscTranStat.equals("3")){
                            lsMessage = "Request has been disapproved!";
                        }

                        InitMessage(1, lsMessage, new onMessageButton() {
                            @Override
                            public void onPositive() {
                                finish();
                            }

                            @Override
                            public void onNegative() {

                            }
                        });
                    }

                    @Override
                    public void OnFailed(String fsMessage) {
                        loDialog.dismiss();

                        InitMessage(2, fsMessage, new onMessageButton() {
                            @Override
                            public void onPositive() {

                            }

                            @Override
                            public void onNegative() {

                            }
                        });
                    }
                });
            }

            @Override
            public void onNegative() {}
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
