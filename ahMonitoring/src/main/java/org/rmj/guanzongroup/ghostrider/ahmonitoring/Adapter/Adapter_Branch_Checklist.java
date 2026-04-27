package org.rmj.guanzongroup.ghostrider.ahmonitoring.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.guanzongroup.ghostrider.ahmonitoring.R;

public class Adapter_Branch_Checklist extends RecyclerView.Adapter<Adapter_Branch_Checklist.VHBranchChecklist> {


    @NonNull
    @Override
    public VHBranchChecklist onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull VHBranchChecklist holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class VHBranchChecklist extends RecyclerView.ViewHolder{

        public MaterialTextView mtv_checklist;
        public ImageButton btn_camera;
        public TextInputEditText txt_remarks;

        public VHBranchChecklist(@NonNull View itemView) {
            super(itemView);

            mtv_checklist = itemView.findViewById(R.id.mtv_checklist);
            btn_camera = itemView.findViewById(R.id.btn_camera);
            txt_remarks = itemView.findViewById(R.id.txt_remarks);
        }

    }
}
