package org.rmj.guanzongroup.ghostrider.approvalcode.Etc;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import org.rmj.guanzongroup.ghostrider.approvalcode.R;

public class Adapter_Transaction_History extends RecyclerView.Adapter<Adapter_Transaction_History.VH_Transaction_History>{

    @NonNull
    @Override
    public VH_Transaction_History onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Transaction_History holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class VH_Transaction_History extends RecyclerView.ViewHolder {

        private MaterialTextView mtv_sourceno;
        private MaterialTextView mtv_transactdt;
        private MaterialTextView mtv_remarks;

        public VH_Transaction_History(@NonNull View itemView) {
            super(itemView);

            mtv_sourceno = itemView.findViewById(R.id.mtv_sourceno);
            mtv_transactdt = itemView.findViewById(R.id.mtv_transactdt);
            mtv_remarks = itemView.findViewById(R.id.mtv_remarks);
        }
    }
}
