package org.rmj.guanzongroup.pacitareward.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.guanzongroup.pacitareward.Activity.PacitaReward.Activity_BranchRecords;
import org.rmj.guanzongroup.pacitareward.Activity.PacitaReward.Activity_Branch_Rate;
import org.rmj.guanzongroup.pacitareward.R;

public class Dialog_SelectAction  {
    private MaterialButton btn_evaluate;
    private MaterialButton btn_vrec;
    private MaterialButton btn_cancel;
    private MaterialTextView mtv_branchname;
    private onDialogSelect mListener;

    public interface onDialogSelect{
        void onEvaluate(Class className, String branchCd, String branchNm, Dialog dialog);
        void onViewRecords(Class className, String branchCd, String branchNm, Dialog dialog);
        void onCancel(Dialog dialog);
    }

    public Dialog_SelectAction(onDialogSelect mListener){
        this.mListener = mListener;
    }

    public void initDialog(Context context, String sBranchcode, String sBranchname){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.activity_dialog_select_action, null);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = org.rmj.g3appdriver.R.style.PopupAnimation;

        btn_evaluate = view.findViewById(R.id.btn_evaluate);
        btn_vrec = view.findViewById(R.id.btn_vrec);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        mtv_branchname = view.findViewById(R.id.mtv_branchname);

        mtv_branchname.setText(sBranchname);

        btn_evaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEvaluate(Activity_Branch_Rate.class, sBranchcode, sBranchname, alertDialog);
            }
        });

        btn_vrec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEvaluate(Activity_BranchRecords.class, sBranchcode, sBranchname, alertDialog);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancel(alertDialog);
            }
        });
    }
}