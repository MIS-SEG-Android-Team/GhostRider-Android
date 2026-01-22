package org.rmj.guanzongroup.onlinecreditapplication.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import org.rmj.guanzongroup.onlinecreditapplication.R;

public class Dialog_MContract {
    private static final String TAG = Dialog_MContract.class.getSimpleName();

    private final Context mContext;

    private AlertDialog poDialogx;

    public interface OnDialogActionClickListener{
        void SendApplication();
    }

    public Dialog_MContract(Context context) {
        this.mContext = context;
    }

    public void initDialog(OnDialogActionClickListener listener){

        AlertDialog.Builder poBuilder = new AlertDialog.Builder(mContext);

        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_mcontract, null);
        poBuilder.setCancelable(true)
                .setView(view);
        poDialogx = poBuilder.create();
        poDialogx.setCancelable(true);

//        MaterialTextView lblTransNo = view.findViewById(R.id.lbl_TransNox),
//                lblApplName = view.findViewById(R.id.lbl_applicantName),
//                lblGOCasNo = view.findViewById(R.id.lbl_gocasNox),
//                lblDateAppl = view.findViewById(R.id.lbl_dateCreated),
//                lblDateSent = view.findViewById(R.id.lbl_dateSent),
//                lblStatus = view.findViewById(R.id.lbl_applStatus),
//                lblDateAppr = view.findViewById(R.id.lbl_dateApproved);

    }

    public void show(){
        poDialogx.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        poDialogx.getWindow().getAttributes().windowAnimations = org.rmj.g3appdriver.R.style.PopupAnimation;
        poDialogx.show();
    }
}
