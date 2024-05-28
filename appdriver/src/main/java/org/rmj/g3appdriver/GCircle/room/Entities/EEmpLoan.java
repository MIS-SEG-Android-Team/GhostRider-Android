package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import javax.persistence.Table;

@Table(name = "Employee_Loan")
public class EEmpLoan{

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sTransNox")
    private String sTransNox;

    @ColumnInfo(name = "dTransact")
    private String dTransact;

    @ColumnInfo(name = "sEmployID")
    private String sEmployID;

    @ColumnInfo(name = "sLoanIDxx")
    private String sLoanIDxx;

    @ColumnInfo(name = "nLoanAmtxx")
    private double nLoanAmtxx;

    @ColumnInfo(name = "nNetAmtxx")
    private double nNetAmtxx;

    @ColumnInfo(name = "nActAmtxx")
    private double nActAmtxx;

    @ColumnInfo(name = "sPurposed")
    private String sPurposed;

    @ColumnInfo(name = "sRemarks")
    private String sRemarks;

    @ColumnInfo(name = "nInterest")
    private double nInterest;

    @ColumnInfo(name = "nPaymTerm")
    private double nPaymTerm;

    @ColumnInfo(name = "dLoanDate")
    private String dLoanDate;

    @ColumnInfo(name = "dFirstPay")
    private String dFirstPay;

    @ColumnInfo(name = "nAmort")
    private double nAmort;

    @ColumnInfo(name = "nBalance")
    private double nBalance;

    @ColumnInfo(name = "cHoldDed")
    private String cHoldDed;

    @ColumnInfo(name = "cTranStat")
    private String cTranStat;

    @ColumnInfo(name = "cSendStat")
    private String cSendStat;

    @ColumnInfo(name = "dTimeStmp")
    private String dTimeStmp;

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
