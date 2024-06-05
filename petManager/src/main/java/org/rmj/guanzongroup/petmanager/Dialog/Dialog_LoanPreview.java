package org.rmj.guanzongroup.petmanager.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textview.MaterialTextView;

import org.rmj.guanzongroup.petmanager.R;

public class Dialog_LoanPreview {
    private Context context;
    private AlertDialog poDialogx;
    private DialogVal loVal;
    private MaterialTextView empname;
    private MaterialTextView emppos;
    private MaterialTextView loanname;
    private MaterialTextView loandate;
    private MaterialTextView loanamt;
    private MaterialTextView loanterms;
    private MaterialTextView loanstat;
    private ConstraintLayout layout_installment;
    private MaterialTextView txt_firstpay;
    private MaterialTextView txt_mnthlypay;
    private MaterialTextView txt_mnthlintrst;
    public Dialog_LoanPreview(Context context, DialogVal loVal){
        this.context = context;
        this.loVal = loVal;
    }
    public void initDialog(){
        AlertDialog.Builder poBuilder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loanpreview, null);
        poBuilder.setCancelable(false)
                .setView(view);
        poDialogx = poBuilder.create();
        poDialogx.setCancelable(true);

        empname = view.findViewById(R.id.empname);
        emppos = view.findViewById(R.id.emppos);
        loanname = view.findViewById(R.id.loanname);
        loandate = view.findViewById(R.id.loandate);
        loanamt = view.findViewById(R.id.loanamt);
        loanterms = view.findViewById(R.id.loanterms);
        loanstat = view.findViewById(R.id.loanstat);
        layout_installment = view.findViewById(R.id.layout_installment);
        txt_firstpay = view.findViewById(R.id.txt_firstpay);
        txt_mnthlypay = view.findViewById(R.id.txt_mnthlypay);
        txt_mnthlintrst = view.findViewById(R.id.txt_mnthlintrst);

        empname.setText(loVal.getsEmpNm());
        emppos.setText(loVal.getsEmpPos());
        loanname.setText(loVal.getsLoanType());
        loandate.setText(loVal.getsLoanDate());
        loanamt.setText(loVal.getsLoanAmt());
        loanterms.setText(loVal.getsLoanTerms() + " Month/s");

        if (!loVal.getsLoanStat().isEmpty()){
            if (Integer.parseInt(loVal.getsLoanStat()) > 0){
                loanstat.setText("Approved");
                loanstat.setTextColor(Color.GREEN);

                //todo: show computed details for installment, if loan approved
                layout_installment.setVisibility(View.VISIBLE);
                
                txt_firstpay.setText(loVal.getsFirstPay());
                txt_mnthlypay.setText(loVal.getsMnthlyPay());
                txt_mnthlintrst.setText(loVal.getsMnthlyIntrst());
            }else {
                loanstat.setText("Pending");
                loanstat.setTextColor(Color.RED);
            }
        }else {
            loanstat.setText("Pending");
            loanstat.setTextColor(Color.RED);
        }
    }
    public void show(){
        poDialogx.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        poDialogx.getWindow().getAttributes().windowAnimations = org.rmj.g3appdriver.R.style.PopupAnimation;
        poDialogx.show();
    }
    public static class DialogVal{
        String sEmpNm;
        String sEmpPos;
        String sLoanType;
        String sLoanDate;
        String sLoanAmt;
        String sLoanTerms;
        String sLoanStat;
        String sFirstPay;
        String sMnthlyPay;
        String sMnthlyIntrst;

        public String getsEmpNm() {
            return sEmpNm;
        }

        public void setsEmpNm(String sEmpNm) {
            this.sEmpNm = sEmpNm;
        }

        public String getsEmpPos() {
            return sEmpPos;
        }

        public void setsEmpPos(String sEmpPos) {
            this.sEmpPos = sEmpPos;
        }

        public String getsLoanType() {
            return sLoanType;
        }

        public void setsLoanType(String sLoanType) {
            this.sLoanType = sLoanType;
        }

        public String getsLoanDate() {
            return sLoanDate;
        }

        public void setsLoanDate(String sLoanDate) {
            this.sLoanDate = sLoanDate;
        }

        public String getsLoanAmt() {
            return sLoanAmt;
        }

        public void setsLoanAmt(String sLoanAmt) {
            this.sLoanAmt = sLoanAmt;
        }

        public String getsLoanTerms() {
            return sLoanTerms;
        }

        public void setsLoanTerms(String sLoanTerms) {
            this.sLoanTerms = sLoanTerms;
        }

        public String getsLoanStat() {
            return sLoanStat;
        }

        public void setsLoanStat(String sLoanStat) {
            this.sLoanStat = sLoanStat;
        }

        public String getsFirstPay() {
            return sFirstPay;
        }

        public void setsFirstPay(String sFirstPay) {
            this.sFirstPay = sFirstPay;
        }

        public String getsMnthlyPay() {
            return sMnthlyPay;
        }

        public void setsMnthlyPay(String sMnthlyPay) {
            this.sMnthlyPay = sMnthlyPay;
        }

        public String getsMnthlyIntrst() {
            return sMnthlyIntrst;
        }

        public void setsMnthlyIntrst(String sMnthlyIntrst) {
            this.sMnthlyIntrst = sMnthlyIntrst;
        }
    }
}
