package org.rmj.guanzongroup.ghostrider.ahmonitoring.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.Adapter.AdapterInventoryBranch;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;

import java.util.List;

public class DialogBranchSelection {
    private static final String TAG = DialogBranchSelection.class.getSimpleName();

    private final Context mContext;
    private final List<EBranchInfo> area, all;
    private OnBranchSelectedCallback callback;
    private AdapterInventoryBranch loAdapter;
    private MessageBox poMessage;

    private AlertDialog poDialogx;


    public interface OnBranchSelectedCallback{
        void OnSelect(String BranchCode, AlertDialog dialog);
        void OnCancel();
    }

    public DialogBranchSelection(Context context, List<EBranchInfo> area, List<EBranchInfo> all) {
        this.mContext = context;
        this.area = area;
        this.all = all;
        this.poMessage = new MessageBox(mContext);
    }

    public void initDialog(boolean isCancelable, OnBranchSelectedCallback foCallback){
        callback = foCallback;

        AlertDialog.Builder poBuilder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_inventory_branch_selection, null);
        poBuilder.setCancelable(false)
                .setView(view);
        poDialogx = poBuilder.create();
        poDialogx.setCancelable(false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_branch);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        RadioGroup rgBranch = view.findViewById(R.id.rg_branchlist);
        TextInputEditText searchView = view.findViewById(R.id.txt_searchBranch);
        LinearLayoutManager loManager = new LinearLayoutManager(mContext);
        loManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(loManager);
        loAdapter = new AdapterInventoryBranch(area, (BranchCode, BranchName) -> {
            try{
                poMessage.initDialog();
                poMessage.setIcon(R.drawable.baseline_contact_support_24);
                poMessage.setTitle("Branch Selected");
                poMessage.setMessage("You selected " + BranchName + ". Continue Selfie log?");
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
        rgBranch.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.rb_area){
                view.findViewById(R.id.til_searchBranch).setVisibility(View.GONE);
                loAdapter = new AdapterInventoryBranch(area, (BranchCode, BranchName) -> {
                    try{
                        poMessage.initDialog();
                        poMessage.setIcon(R.drawable.baseline_contact_support_24);
                        poMessage.setTitle("Branch Selected");
                        poMessage.setMessage("You selected " + BranchName + ". Continue Selfie log?");
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
            } else if(checkedId == R.id.rb_allbranch){
                view.findViewById(R.id.til_searchBranch).setVisibility(View.VISIBLE);
                loAdapter = new AdapterInventoryBranch(all, (BranchCode, BranchName) -> {
                    try{
                        poMessage.initDialog();
                        poMessage.setIcon(R.drawable.baseline_contact_support_24);
                        poMessage.setTitle("Branch Selected");
                        poMessage.setMessage("You selected " + BranchName + ". Continue Selfie log?");
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
            }
            loAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(loAdapter);
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    if(s != null) {
                        if (!s.toString().trim().isEmpty()) {
                            String query = s.toString();
                            loAdapter.getFilter().filter(query);
                            loAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(isCancelable){
            btnCancel.setVisibility(View.VISIBLE);
        } else {
            btnCancel.setVisibility(View.GONE);
        }

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
