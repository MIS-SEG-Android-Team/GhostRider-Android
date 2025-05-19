package org.rmj.guanzongroup.petmanager.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.guanzongroup.petmanager.R;

public class DialogDisclosure {

    private AlertDialog poDialogx;
    private Context context;
    private MaterialTextView mtv_message;

    public DialogDisclosure(Context context){
        this.context = context;
    }

    public void initDialog(onDisclosure callback){
        AlertDialog.Builder loBuilder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_dcp_permission, null, false);
        loBuilder.setCancelable(false)
                .setView(view);
        poDialogx = loBuilder.create();

        MaterialButton btnAccept = view.findViewById(R.id.btn_accept);
        MaterialButton btnDecline = view.findViewById(R.id.btn_decline);

        mtv_message = view.findViewById(R.id.mtv_message);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onAccept();
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onDecline();
            }
        });

    }

    public void setMessage(String message){
        mtv_message.setText(message);
    }

    public void show(){
        if(!poDialogx.isShowing()){
            poDialogx.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            poDialogx.getWindow().getAttributes().windowAnimations = org.rmj.g3appdriver.R.style.PopupAnimation;
            poDialogx.show();
        }
    }

    public void dismiss(){
        if(poDialogx.isShowing()){
            poDialogx.dismiss();
        }
    }

    public interface onDisclosure{
        void onAccept();
        void onDecline();
    }
}
