package org.rmj.guanzongroup.petmanager.Adapter;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj.EmployeeLoan;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmpLoan;
import org.rmj.guanzongroup.petmanager.Dialog.Dialog_LoanPreview;
import org.rmj.guanzongroup.petmanager.R;
import org.rmj.guanzongroup.petmanager.ViewHolder.VH_LoanItems;

import java.util.HashMap;
import java.util.List;

public class Adapter_LoanItems extends RecyclerView.Adapter<VH_LoanItems> {
    private Context context;
    private EmployeeLoan poEmpLoan;
    private List<EEmpLoan> poLoans;

    public Adapter_LoanItems(Context context, EmployeeLoan poEmpLoan, List<EEmpLoan> poLoans){
        this.context = context;
        this.poEmpLoan = poEmpLoan;
        this.poLoans = poLoans;
    }

    @NonNull
    @Override
    public VH_LoanItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_loan_items, parent, false);
        return new VH_LoanItems(view);
    }
    @Override
    public void onBindViewHolder(@NonNull VH_LoanItems holder, int position) {
        String loanNm = poEmpLoan.GetLoanName(poLoans.get(position).getsLoanIDxx());
        String cTranStat = poLoans.get(position).getcTranStat();
        String loanDt = poLoans.get(position).getdLoanDate();
        String loanAmt = String.valueOf(poLoans.get(position).getnLoanAmtxx());

        holder.loantype.setText(loanNm);
        holder.loandate.setText(loanDt);
        holder.loanamt.setText(loanAmt);

        if (!cTranStat.isEmpty()){
            if (Integer.parseInt(cTranStat) > 0){
                holder.loan_status.setText("Approved");
            }else {
                holder.loan_status.setText("Pending");
            }
        }else {
            holder.loan_status.setText("No Status");
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_LoanPreview.DialogVal params = new Dialog_LoanPreview.DialogVal();

                params.setsEmpNm(poEmpLoan.GetEmpName());
                params.setsEmpPos(poEmpLoan.GetEmpDepartment());
                params.setsLoanType(poEmpLoan.GetLoanName(poLoans.get(position).getsLoanIDxx()));
                params.setsLoanDate(poLoans.get(position).getdLoanDate());
                params.setsLoanAmt(String.valueOf(poLoans.get(position).getnLoanAmtxx()));
                params.setsLoanTerms(String.valueOf(poLoans.get(position).getnPaymTerm()));
                params.setsLoanStat(cTranStat);

                if (!cTranStat.isEmpty()){
                    if (Integer.parseInt(cTranStat) > 0){
                        params.setsFirstPay(poLoans.get(position).getdFirstPay());
                        params.setsMnthlyPay(String.valueOf(poLoans.get(position).getnNetAmtxx()));
                        params.setsMnthlyIntrst(String.valueOf(poLoans.get(position).getnInterest()));
                    }
                }

                Dialog_LoanPreview prevDialog = new Dialog_LoanPreview(context, params);
                prevDialog.initDialog();
                prevDialog.show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return poLoans.size();
    }
}
