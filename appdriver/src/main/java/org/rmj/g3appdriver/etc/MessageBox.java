/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.g3appdriver.etc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.R;

import java.util.Objects;

public class  MessageBox {
    private AlertDialog poDialogx;
    private MaterialButton btnPositive;
    private MaterialButton btnNegative;
    private MaterialTextView lblTitle;
    private MaterialTextView lblMsgxx;
    private ShapeableImageView msg_icon;

    private final Context context;

    public MessageBox(Context context){
        this.context = Objects.requireNonNull(context);
    }
    public void initDialog(){

        AlertDialog.Builder poBuilder = new AlertDialog.Builder(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_message_box, null);

        poBuilder.setCancelable(false)
                .setView(view);

        poDialogx = poBuilder.create();
        poDialogx.setCancelable(false);

        msg_icon = view.findViewById(R.id.msg_icon);
        lblTitle = view.findViewById(R.id.lbl_dialogTitle);
        lblMsgxx = view.findViewById(R.id.lbl_dialogMessage);
        btnPositive = view.findViewById(R.id.btn_dialogPositive);
        btnPositive.setVisibility(View.GONE);
        btnNegative = view.findViewById(R.id.btn_dialogNegative);
        btnNegative.setVisibility(View.GONE);
    }

    public void setIcon(int psIconx) {
        try {
            msg_icon.setImageResource(psIconx);
        } catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    public void setMessage(String psMessage) {
        try {
            lblMsgxx.setText(Objects.requireNonNull(psMessage));
        } catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    public void setTitle(String psTitlexx) {
        try {
            lblTitle.setText(Objects.requireNonNull(psTitlexx));
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void setPositiveButton(String psBtnPost, final DialogButton listener) {
        btnPositive.setVisibility(View.VISIBLE);
        btnPositive.setText(psBtnPost);
        btnPositive.setOnClickListener(view -> {
            listener.OnButtonClick(view, poDialogx);
        });
    }

    public void setNegativeButton(String psBtnNegt, final DialogButton listener) {
        btnNegative.setVisibility(View.VISIBLE);
        btnNegative.setText(psBtnNegt);
        btnNegative.setOnClickListener(view -> {
            listener.OnButtonClick(view, poDialogx);
        });
    }

    public void show() {
        if(!poDialogx.isShowing()) {
            poDialogx.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            poDialogx.getWindow().getAttributes().windowAnimations = R.style.PopupAnimation;
            poDialogx.show();
        }
    }

    public interface DialogButton{
        void OnButtonClick(View view, AlertDialog dialog);
    }
}

