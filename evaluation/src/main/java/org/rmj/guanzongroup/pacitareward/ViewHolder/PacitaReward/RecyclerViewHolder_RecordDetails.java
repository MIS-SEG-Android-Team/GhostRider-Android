package org.rmj.guanzongroup.pacitareward.ViewHolder.PacitaReward;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.guanzongroup.pacitareward.R;

public class RecyclerViewHolder_RecordDetails extends RecyclerView.ViewHolder {
    public ShapeableImageView siv_rate;
    public MaterialTextView mtv_criteria;

    public RecyclerViewHolder_RecordDetails(@NonNull View itemView) {
        super(itemView);

        siv_rate = itemView.findViewById(R.id.siv_rate);
        mtv_criteria = itemView.findViewById(R.id.mtv_criteria);
    }
}
