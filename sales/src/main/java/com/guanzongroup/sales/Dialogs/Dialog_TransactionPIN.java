package com.guanzongroup.sales.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.guanzongroup.sales.R;

public class Dialog_TransactionPIN {
    private static final String TAG = Dialog_TransactionPIN.class.getSimpleName();

    private AlertDialog poDialogx;
    private final Context context;

    public Dialog_TransactionPIN(Context context) {
        this.context = context;
    }
    public void initDialog(String fsArgs, String message){
        AlertDialog.Builder loBuilder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirmation_pin, null, false);
        loBuilder.setCancelable(false)
                .setView(view);
        poDialogx = loBuilder.create();
        poDialogx.setCancelable(false);

        TextInputEditText tieOtp = view.findViewById(R.id.tie_otp);
        tieOtp.setText(fsArgs);
        TextView lbl_message = view.findViewById(R.id.lbl_message);
        lbl_message.setText(message);
        view.findViewById(R.id.btnCloseDialog).setOnClickListener(v -> poDialogx.dismiss());


        poDialogx.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        poDialogx.getWindow().getAttributes().windowAnimations = org.rmj.g3appdriver.R.style.PopupAnimation;
        poDialogx.show();
    }
}
