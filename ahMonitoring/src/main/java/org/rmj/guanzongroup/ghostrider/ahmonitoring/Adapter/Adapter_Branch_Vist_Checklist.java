package org.rmj.guanzongroup.ghostrider.ahmonitoring.Adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitDetail;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitChecklist;
import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;

import java.util.List;

public class Adapter_Branch_Vist_Checklist extends RecyclerView.Adapter<Adapter_Branch_Vist_Checklist.VHBranchChecklist> {

    private List<DBranchVisitDetail.BranchVisitDetail> laChecklist;
    private OnItemListener loListener;

    public Adapter_Branch_Vist_Checklist(List<DBranchVisitDetail.BranchVisitDetail> faChecklist, OnItemListener foListener){
        laChecklist = faChecklist;
        loListener = foListener;
    }

    public interface OnItemListener{
        void OnCamera(String fsCategrID);
        void OnViewDetails(String fsCategrID);
        void OnRemarks(String fsCategrID, String sRemarks);
    }

    @NonNull
    @Override
    public VHBranchChecklist onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VHBranchChecklist(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.adapter_branch_visit_checklist, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VHBranchChecklist holder, int position) {

        holder.mtv_checklist.setText(laChecklist.get(position).sDescript);
        holder.txt_remarks.setText(laChecklist.get(position).sRemarksx);

        //add listener for camera button
        holder.btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loListener.OnCamera(laChecklist.get(position).sCategrID);
            }
        });

        //add listener for info button
        holder.ib_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loListener.OnViewDetails(laChecklist.get(position).sCategrID);
            }
        });

        //hide or display confirm remarks button, based on remarks entry
        holder.txt_remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() <= 0){
                    holder.mtv_confirm.setVisibility(GONE);
                }else {
                    holder.mtv_confirm.setVisibility(VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        //add event for mtv confirm
        holder.mtv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loListener.OnRemarks(laChecklist.get(position).sCategrID, holder.txt_remarks.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return laChecklist.size();
    }

    public static class VHBranchChecklist extends RecyclerView.ViewHolder{

        public MaterialTextView mtv_checklist, mtv_confirm;
        public ImageButton btn_camera, ib_info;
        public TextInputEditText txt_remarks;

        public VHBranchChecklist(@NonNull View itemView) {
            super(itemView);

            mtv_checklist = itemView.findViewById(R.id.mtv_checklist);
            btn_camera = itemView.findViewById(R.id.btn_camera);
            ib_info = itemView.findViewById(R.id.ib_info);
            txt_remarks = itemView.findViewById(R.id.txt_remarks);
            mtv_confirm = itemView.findViewById(R.id.mtv_confirm);
        }

    }
}
