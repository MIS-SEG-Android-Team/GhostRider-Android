package org.rmj.guanzongroup.ghostrider.approvalcode.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.guanzongroup.ghostrider.approvalcode.R;

public class Fragment_TransactionApproval_Details extends Fragment {

    private String lsArgs;
    private MaterialTextView mtv_transactno, mtv_dtransact, mtv_status, mtv_sourceno, mtv_recipient, mtv_industry, mtv_source, mtv_remarks;
    private MaterialButton btn_disapprove, btn_approve;

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);

        if (args != null){
            if (!args.containsKey("source")){
                return;
            }
            lsArgs = args.getString("source");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_transactionapproval_details, container, false);

        mtv_transactno = v.findViewById(R.id.mtv_transactno);
        mtv_dtransact = v.findViewById(R.id.mtv_dtransact);
        mtv_status = v.findViewById(R.id.mtv_status);
        mtv_sourceno = v.findViewById(R.id.mtv_sourceno);
        mtv_recipient = v.findViewById(R.id.mtv_recipient);
        mtv_industry = v.findViewById(R.id.mtv_industry);
        mtv_source = v.findViewById(R.id.mtv_source);
        mtv_remarks = v.findViewById(R.id.mtv_remarks);

        btn_disapprove = v.findViewById(R.id.btn_disapprove);
        btn_approve = v.findViewById(R.id.btn_approve);

        return v;
    }
}
