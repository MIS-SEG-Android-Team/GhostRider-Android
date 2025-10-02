package org.rmj.guanzongroup.petmanager.ViewHolder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import org.rmj.guanzongroup.petmanager.R;

public class VH_LoanItemParents extends RecyclerView.ViewHolder {
    public ConstraintLayout layout_empinfo;
    public MaterialTextView empname;
    public MaterialTextView emppos;
    public RecyclerView rec_parentitems;
    public VH_LoanItemParents(@NonNull View itemView) {
        super(itemView);

        layout_empinfo = itemView.findViewById(R.id.layout_empinfo);
        empname = itemView.findViewById(R.id.empname);
        emppos = itemView.findViewById(R.id.emppos);
        rec_parentitems = itemView.findViewById(R.id.rec_parentitems);
    }
}
