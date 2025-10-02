package org.rmj.guanzongroup.petmanager.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj.EmployeeLoan;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmpLoan;
import org.rmj.guanzongroup.petmanager.Dialog.Dialog_LoanPreview;
import org.rmj.guanzongroup.petmanager.R;
import org.rmj.guanzongroup.petmanager.ViewHolder.VH_LoanItems;
import java.util.List;

public class Adapter_LoanItems extends RecyclerView.Adapter<VH_LoanItems> {
    private Context context;
    private EmployeeLoan poEmpLoan;
    private List<EEmpLoan> poLoans;
    private onSelectDetails callBack;
    public interface onSelectDetails{
        void onSelectData(Dialog_LoanPreview.DialogVal loVal);
    }

    public Adapter_LoanItems(Context context, EmployeeLoan poEmpLoan, List<EEmpLoan> poLoans, onSelectDetails callBack){
        this.context = context;
        this.poEmpLoan = poEmpLoan;
        this.poLoans = poLoans;
        this.callBack = callBack;
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
        String cSendStat = poLoans.get(position).getcSendStat();
        String cTranStat = poLoans.get(position).getcTranStat();
        String loanStat = poEmpLoan.GetStatus(cSendStat, cTranStat);
        String loanDt = poLoans.get(position).getdLoanDate();
        String loanAmt = String.valueOf(poLoans.get(position).getnLoanAmtxx());

        holder.loantype.setText(loanNm);
        holder.loandate.setText(loanDt);
        holder.loanamt.setText(loanAmt);

        holder.loan_status.setText(loanStat);
        if (cSendStat.isEmpty()){
            holder.loan_status.setTextColor(Color.RED);
        }else {
            //todo: if uploaded, set loan approval status else set send status
            if (Integer.parseInt(cSendStat) > 0){
                if (!cTranStat.isEmpty()){
                    if (Integer.parseInt(cTranStat) > 0){ //APPROVED
                        holder.loan_status.setTextColor(Color.GREEN);
                    }else { //PENDING
                        holder.loan_status.setTextColor(Color.RED);
                    }
                }else { //UPLOADED
                    holder.loan_status.setTextColor(Color.GREEN);
                }
            }else {
                holder.loan_status.setTextColor(Color.RED);
            }
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_LoanPreview.DialogVal params = new Dialog_LoanPreview.DialogVal();

                params.setsLoanType(poEmpLoan.GetLoanName(poLoans.get(position).getsLoanIDxx()));
                params.setsLoanDate(poLoans.get(position).getdLoanDate());
                params.setsLoanAmt(String.valueOf(poLoans.get(position).getnLoanAmtxx()));
                params.setsLoanTerms(String.valueOf(poLoans.get(position).getnPaymTerm()));
                params.setcSendStat(cSendStat);
                params.setsLoanStat(cTranStat);

                if (!cTranStat.isEmpty()){
                    if (Integer.parseInt(cTranStat) > 0){
                        params.setsFirstPay(poLoans.get(position).getdFirstPay());
                        params.setsMnthlyPay(String.valueOf(poLoans.get(position).getnNetAmtxx()));
                        params.setsMnthlyIntrst(String.valueOf(poLoans.get(position).getnInterest()));
                    }
                }

                //TODO: PASS DETAILS ON LISTENER TO DISPLAY ON DIALOG
                callBack.onSelectData(params);
            }
        });
    }
    @Override
    public int getItemCount() {
        return poLoans.size();
    }
}
