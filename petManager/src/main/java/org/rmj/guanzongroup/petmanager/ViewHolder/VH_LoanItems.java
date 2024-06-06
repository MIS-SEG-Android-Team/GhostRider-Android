package org.rmj.guanzongroup.petmanager.ViewHolder;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.guanzongroup.petmanager.R;

public class VH_LoanItems extends RecyclerView.ViewHolder {
    public View view;
    public MaterialTextView loantype;
    public MaterialTextView loan_status;
    public MaterialTextView loandate;
    public MaterialTextView loanamt;
    public MaterialButton btn_approve;
    public MaterialButton btn_disapprove;
    public VH_LoanItems(@NonNull View itemView) {
        super(itemView);

        view = itemView;
        loantype = itemView.findViewById(R.id.loantype);
        loan_status = itemView.findViewById(R.id.loan_status);
        loandate = itemView.findViewById(R.id.loandate);
        loanamt = itemView.findViewById(R.id.loanamt);
        btn_approve = itemView.findViewById(R.id.btn_approve);
        btn_disapprove = itemView.findViewById(R.id.btn_disapprove);
    }
}
