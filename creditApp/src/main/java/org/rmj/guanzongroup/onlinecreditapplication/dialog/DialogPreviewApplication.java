package org.rmj.guanzongroup.onlinecreditapplication.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;


import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCreditApplication;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.guanzongroup.onlinecreditapplication.R;

public class DialogPreviewApplication {
    private static final String TAG = DialogPreviewApplication.class.getSimpleName();

    private final Context mContext;

    private AlertDialog poDialogx;

    public interface OnDialogActionClickListener{
        void DocumentScan(DCreditApplication.ApplicationLog creditApp);
        void SendApplication(DCreditApplication.ApplicationLog creditApp);
    }

    public DialogPreviewApplication(Context context) {
        this.mContext = context;
    }

    public void initDialog(DCreditApplication.ApplicationLog args, OnDialogActionClickListener listener){

        AlertDialog.Builder poBuilder = new AlertDialog.Builder(mContext);

        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_preview_application, null);
        poBuilder.setCancelable(true)
                .setView(view);
        poDialogx = poBuilder.create();
        poDialogx.setCancelable(true);

        MaterialTextView lblTransNo = view.findViewById(R.id.lbl_TransNox),
                lblApplName = view.findViewById(R.id.lbl_applicantName),
                lblGOCasNo = view.findViewById(R.id.lbl_gocasNox),
                lblDateAppl = view.findViewById(R.id.lbl_dateCreated),
                lblDateSent = view.findViewById(R.id.lbl_dateSent),
                lblStatus = view.findViewById(R.id.lbl_applStatus),
                lblDateAppr = view.findViewById(R.id.lbl_dateApproved);

        MaterialButton btnScan = view.findViewById(R.id.btn_docScan),
                btn_docSend = view.findViewById(R.id.btn_docSend);

        lblTransNo.setText(args.sTransNox);
        lblApplName.setText(args.sClientNm);

        //initialize GOCAS number
        if(args.sGOCASNox.equalsIgnoreCase("null")){
            lblGOCasNo.setText("N/A");
        } else {
            lblGOCasNo.setText(args.sGOCASNox);
        }

        lblDateAppl.setText(FormatUIText.getParseDateTime(args.dCreatedx));
        lblDateSent.setText(FormatUIText.getParseDateTime(args.dReceived));

        //initialize status
        if(args.cTranStat.isEmpty() ||
            args.cTranStat.equalsIgnoreCase("0")){
            lblStatus.setText("Waiting for approval");
        } else if(args.cWithCIxx.equalsIgnoreCase("1")){
            lblStatus.setText("For C.I");
        } else {
            lblStatus.setText("N/A");
        }

        //initialize send button
        if (args.cSendStat == null || args.cSendStat.isEmpty() || args.cSendStat.equalsIgnoreCase("0")){
            btn_docSend.setEnabled(true);
        } else {
            btn_docSend.setEnabled(false);
        }

        lblDateAppr.setText(FormatUIText.getParseDateTime(args.dVerified));

        btnScan.setOnClickListener(v -> {
            poDialogx.dismiss();
            listener.DocumentScan(args);
        });

        btn_docSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poDialogx.dismiss();
                listener.SendApplication(args);
            }
        });
    }

    public void show(){
        poDialogx.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        poDialogx.getWindow().getAttributes().windowAnimations = org.rmj.g3appdriver.R.style.PopupAnimation;
        poDialogx.show();
    }
}
