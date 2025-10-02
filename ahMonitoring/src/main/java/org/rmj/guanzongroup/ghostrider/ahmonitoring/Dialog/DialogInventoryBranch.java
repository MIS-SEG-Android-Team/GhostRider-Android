package org.rmj.guanzongroup.ghostrider.ahmonitoring.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.button.MaterialButton;


import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Adapter.AdapterInventoryBranch;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;

import java.util.List;

public class DialogInventoryBranch {
    private static final String TAG = DialogBranchSelection.class.getSimpleName();

    private final Context mContext;
    private final List<EBranchInfo> poList;
    private DialogBranchSelection.OnBranchSelectedCallback callback;
    private AdapterInventoryBranch loAdapter;
    private MessageBox poMessage;

    private AlertDialog poDialogx;


    public interface OnBranchSelectedCallback{
        void OnSelect(String BranchCode, AlertDialog dialog);
        void OnCancel();
    }

    public DialogInventoryBranch(Context context, List<EBranchInfo> list) {
        this.mContext = context;
        this.poList = list;
        this.poMessage = new MessageBox(mContext);
    }

    public void initDialog(DialogBranchSelection.OnBranchSelectedCallback foCallback){
        callback = foCallback;

        AlertDialog.Builder poBuilder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_cc_branch_selection, null);
        poBuilder.setCancelable(false)
                .setView(view);
        poDialogx = poBuilder.create();
        poDialogx.setCancelable(false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_branch);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        LinearLayoutManager loManager = new LinearLayoutManager(mContext);
        loManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(loManager);
        loAdapter = new AdapterInventoryBranch(poList, (BranchCode, BranchName) -> {
            try{
                poMessage.initDialog();
                poMessage.setIcon(R.drawable.baseline_contact_support_24);
                poMessage.setTitle("Branch Selected");
                poMessage.setMessage("You selected " + BranchName + ". continue cash count?");
                poMessage.setPositiveButton("Yes", (view12, dialog) -> {
                    callback.OnSelect(BranchCode, poDialogx);
                    dialog.dismiss();
                });
                poMessage.setNegativeButton("No", (view1, dialog) -> dialog.dismiss());
                poMessage.show();
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        recyclerView.setAdapter(loAdapter);

        btnCancel.setOnClickListener(v -> {
            poDialogx.dismiss();
            callback.OnCancel();
        });

        if(!poDialogx.isShowing()) {
            poDialogx.show();
            poDialogx.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            poDialogx.getWindow().getAttributes().windowAnimations = org.rmj.g3appdriver.R.style.PopupAnimation;
        }
    }
}
