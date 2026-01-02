package org.rmj.guanzongroup.evaluation.ViewHolder.PacitaReward;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import org.rmj.guanzongroup.evaluation.R;

public class RecyclerViewHolder_BranchRecord extends RecyclerView.ViewHolder {
    public MaterialTextView mtvrecord_date;
    public MaterialTextView mtvrecord_rate;
    public View view;
    public RecyclerViewHolder_BranchRecord(@NonNull View itemView) {
        super(itemView);
        mtvrecord_date = itemView.findViewById(R.id.record_date);
        mtvrecord_rate = itemView.findViewById(R.id.record_rate);
        view = itemView;
    }
}
