package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Employee_Loan")
public class EEmpLoan{

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sTransNox")
    public String sTransNox;

    @ColumnInfo(name = "dTransact")
    public String dTransact;

    @ColumnInfo(name = "sEmployID")
    public String sEmployID;

    @ColumnInfo(name = "sLoanIDxx")
    public String sLoanIDxx;

    @ColumnInfo(name = "nLoanAmtxx")
    public double nLoanAmtxx = 0.00;

    @ColumnInfo(name = "nNetAmtxx")
    public double nNetAmtxx = 0.00;

    @ColumnInfo(name = "nActAmtxx")
    public double nActAmtxx = 0.00;

    @ColumnInfo(name = "sPurposed")
    public String sPurposed;

    @ColumnInfo(name = "sRemarks")
    public String sRemarks;

    @ColumnInfo(name = "nInterest")
    public double nInterest = 0.00;

    @ColumnInfo(name = "nPaymTerm")
    public double nPaymTerm = 0.00;

    @ColumnInfo(name = "dLoanDate")
    public String dLoanDate;

    @ColumnInfo(name = "dFirstPay")
    public String dFirstPay;

    @ColumnInfo(name = "nAmort")
    public double nAmort = 0.00;

    @ColumnInfo(name = "nBalance")
    public double nBalance = 0.00;

    @ColumnInfo(name = "cHoldDed")
    public String cHoldDed;

    @ColumnInfo(name = "cTranStat")
    public String cTranStat = "0";

    @ColumnInfo(name = "cSendStat")
    public String cSendStat = "0";

    @ColumnInfo(name = "dTimeStmp")
    public String dTimeStmp;

    @NonNull
    public String getsTransNox() {
        return sTransNox;
    }

    public void setsTransNox(@NonNull String sTransNox) {
        this.sTransNox = sTransNox;
    }

    public String getdTransact() {
        return dTransact;
    }

    public void setdTransact(String dTransact) {
        this.dTransact = dTransact;
    }

    public String getsEmployID() {
        return sEmployID;
    }

    public void setsEmployID(String sEmployID) {
        this.sEmployID = sEmployID;
    }

    public String getsLoanIDxx() {
        return sLoanIDxx;
    }

    public void setsLoanIDxx(String sLoanIDxx) {
        this.sLoanIDxx = sLoanIDxx;
    }

    public double getnLoanAmtxx() {
        return nLoanAmtxx;
    }

    public void setnLoanAmtxx(double nLoanAmtxx) {
        this.nLoanAmtxx = nLoanAmtxx;
    }

    public double getnNetAmtxx() {
        return nNetAmtxx;
    }

    public void setnNetAmtxx(double nNetAmtxx) {
        this.nNetAmtxx = nNetAmtxx;
    }

    public double getnActAmtxx() {
        return nActAmtxx;
    }

    public void setnActAmtxx(double nActAmtxx) {
        this.nActAmtxx = nActAmtxx;
    }

    public String getsPurposed() {
        return sPurposed;
    }

    public void setsPurposed(String sPurposed) {
        this.sPurposed = sPurposed;
    }

    public String getsRemarks() {
        return sRemarks;
    }

    public void setsRemarks(String sRemarks) {
        this.sRemarks = sRemarks;
    }

    public double getnInterest() {
        return nInterest;
    }

    public void setnInterest(double nInterest) {
        this.nInterest = nInterest;
    }

    public double getnPaymTerm() {
        return nPaymTerm;
    }

    public void setnPaymTerm(double nPaymTerm) {
        this.nPaymTerm = nPaymTerm;
    }

    public String getdLoanDate() {
        return dLoanDate;
    }

    public void setdLoanDate(String dLoanDate) {
        this.dLoanDate = dLoanDate;
    }

    public String getdFirstPay() {
        return dFirstPay;
    }

    public void setdFirstPay(String dFirstPay) {
        this.dFirstPay = dFirstPay;
    }

    public double getnAmort() {
        return nAmort;
    }

    public void setnAmort(double nAmort) {
        this.nAmort = nAmort;
    }

    public double getnBalance() {
        return nBalance;
    }

    public void setnBalance(double nBalance) {
        this.nBalance = nBalance;
    }

    public String getcHoldDed() {
        return cHoldDed;
    }

    public void setcHoldDed(String cHoldDed) {
        this.cHoldDed = cHoldDed;
    }

    public String getcTranStat() {
        return cTranStat;
    }

    public void setcTranStat(String cTranStat) {
        this.cTranStat = cTranStat;
    }

    public String getcSendStat() {
        return cSendStat;
    }

    public void setcSendStat(String cSendStat) {
        this.cSendStat = cSendStat;
    }

    public String getdTimeStmp() {
        return dTimeStmp;
    }

    public void setdTimeStmp(String dTimeStmp) {
        this.dTimeStmp = dTimeStmp;
    }
}
