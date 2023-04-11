package org.rmj.guanzongroup.pacitareward;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

public class RecyclerViewHolder_BranchList extends RecyclerView.ViewHolder {
    public MaterialTextView item_branch;
    public MaterialTextView item_branchloc;

    public RecyclerViewHolder_BranchList(@NonNull View itemView) {
        super(itemView);

        item_branch = itemView.findViewById(R.id.item_branch);
        item_branchloc = itemView.findViewById(R.id.item_branchloc);
    }
}
