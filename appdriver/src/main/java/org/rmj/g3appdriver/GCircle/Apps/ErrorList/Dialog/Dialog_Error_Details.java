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

package org.rmj.g3appdriver.GCircle.Apps.ErrorList.Dialog;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.EErrorLogs;
import org.rmj.g3appdriver.R;

public class Dialog_Error_Details {

    private final Context context;
    private AlertDialog poDialogx;

    public Dialog_Error_Details(Context context) {
        this.context = context;
    }

    public void initDialog(EErrorLogs foVal, boolean Cancellable){

        AlertDialog.Builder poBuilder = new AlertDialog.Builder(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_error_detail, null);

        poBuilder.setCancelable(Cancellable)
                .setView(view);

        poDialogx = poBuilder.create();
        poDialogx.setCancelable(Cancellable);

        MaterialTextView mtv_message = view.findViewById(R.id.mtv_message);
        MaterialTextView mtv_errId = view.findViewById(R.id.mtv_errId);
        MaterialTextView mtv_source = view.findViewById(R.id.mtv_sourcetrans);
        MaterialTextView mtv_date = view.findViewById(R.id.mtv_logdate);


        mtv_message.setText(foVal.getsMessagex());
        mtv_errId.setText(String.valueOf(foVal.getnErrorLogID()));
        mtv_source.setText(foVal.getsSourceTransNo());
        mtv_date.setText(foVal.getdLogDate());
    }

    public void show() {
        poDialogx.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        poDialogx.getWindow().getAttributes().windowAnimations = R.style.PopupAnimation;
        poDialogx.show();
    }

}

