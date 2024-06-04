package org.rmj.guanzongroup.petmanager.ViewHolder;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.guanzongroup.petmanager.R;

public class VH_LoanItems extends RecyclerView.ViewHolder {
    public ConstraintLayout layout_empinfo;
    public MaterialTextView empname;
    public MaterialTextView emppos;
    public MaterialTextView loantype;
    public MaterialTextView loan_status;
    public MaterialTextView loandate;
    public MaterialTextView loanamt;
    public MaterialButton btn_view;
    public MaterialButton btn_approve;
    public VH_LoanItems(@NonNull View itemView) {
        super(itemView);

        layout_empinfo = itemView.findViewById(R.id.layout_empinfo);
        empname = itemView.findViewById(R.id.empname);
        emppos = itemView.findViewById(R.id.emppos);
        loantype = itemView.findViewById(R.id.loantype);
        loan_status = itemView.findViewById(R.id.loan_status);
        loandate = itemView.findViewById(R.id.loandate);
        loanamt = itemView.findViewById(R.id.loanamt);
        btn_view = itemView.findViewById(R.id.btn_view);
        btn_approve = itemView.findViewById(R.id.btn_approve);
    }
}
