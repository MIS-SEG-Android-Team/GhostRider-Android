package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MC_Contract_Info")
public class EMCContractInfo {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sTransNox")
    public String sTransNox;

    @ColumnInfo(name = "sBranchCd")
    public String sBranchCd;

    @ColumnInfo(name = "dTransact")
    public String dTransact;

    @ColumnInfo(name = "sClientID")
    public String sClientID;

    @ColumnInfo(name = "sReferNox")
    public String sReferNox;

    @ColumnInfo(name = "sAcctNmbr")
    public String sAcctNmbr;

    @ColumnInfo(name = "dPurchase")
    public String dPurchase;

    @ColumnInfo(name = "drNo")
    public String drNo;

    @ColumnInfo(name = "sSerialID")
    public String sSerialID;

    @ColumnInfo(name = "nAcctTerm")
    public int nAcctTerm;

    @ColumnInfo(name = "nDownPaym")
    public double nDownPaym;

    @ColumnInfo(name = "nMonAmort")
    public double nMonAmort;

    @ColumnInfo(name = "nRebatesx")
    public double nRebatesx;

    @ColumnInfo(name = "nPenaltyx")
    public double nPenaltyx;

    @ColumnInfo(name = "dFirstPay")
    public String dFirstPay;

    @ColumnInfo(name = "sRemarksx")
    public String sRemarksx;

    @ColumnInfo(name = "cTranStat")
    public String cTranStat;

    @ColumnInfo(name = "sSendStat")
    public String sSendStat;

    @NonNull
    public String getsTransNox() {
        return sTransNox;
    }

    public void setsTransNox(@NonNull String sTransNox) {
        this.sTransNox = sTransNox;
    }

    public String getsBranchCd() {
        return sBranchCd;
    }

    public void setsBranchCd(String sBranchCd) {
        this.sBranchCd = sBranchCd;
    }

    public String getdTransact() {
        return dTransact;
    }

    public void setdTransact(String dTransact) {
        this.dTransact = dTransact;
    }

    public String getsClientID() {
        return sClientID;
    }

    public void setsClientID(String sClientID) {
        this.sClientID = sClientID;
    }

    public String getsReferNox() {
        return sReferNox;
    }

    public void setsReferNox(String sReferNox) {
        this.sReferNox = sReferNox;
    }

    public String getsAcctNmbr() {
        return sAcctNmbr;
    }

    public void setsAcctNmbr(String sAcctNmbr) {
        this.sAcctNmbr = sAcctNmbr;
    }

    public String getdPurchase() {
        return dPurchase;
    }

    public void setdPurchase(String dPurchase) {
        this.dPurchase = dPurchase;
    }

    public String getDrNo() {
        return drNo;
    }

    public void setDrNo(String drNo) {
        this.drNo = drNo;
    }

    public String getsSerialID() {
        return sSerialID;
    }

    public void setsSerialID(String sSerialID) {
        this.sSerialID = sSerialID;
    }

    public int getnAcctTerm() {
        return nAcctTerm;
    }

    public void setnAcctTerm(int nAcctTerm) {
        this.nAcctTerm = nAcctTerm;
    }

    public double getnDownPaym() {
        return nDownPaym;
    }

    public void setnDownPaym(double nDownPaym) {
        this.nDownPaym = nDownPaym;
    }

    public double getnMonAmort() {
        return nMonAmort;
    }

    public void setnMonAmort(double nMonAmort) {
        this.nMonAmort = nMonAmort;
    }

    public double getnRebatesx() {
        return nRebatesx;
    }

    public void setnRebatesx(double nRebatesx) {
        this.nRebatesx = nRebatesx;
    }

    public double getnPenaltyx() {
        return nPenaltyx;
    }

    public void setnPenaltyx(double nPenaltyx) {
        this.nPenaltyx = nPenaltyx;
    }

    public String getdFirstPay() {
        return dFirstPay;
    }

    public void setdFirstPay(String dFirstPay) {
        this.dFirstPay = dFirstPay;
    }

    public String getsRemarksx() {
        return sRemarksx;
    }

    public void setsRemarksx(String sRemarksx) {
        this.sRemarksx = sRemarksx;
    }

    public String getcTranStat() {
        return cTranStat;
    }

    public void setcTranStat(String cTranStat) {
        this.cTranStat = cTranStat;
    }

    public String getsSendStat() {
        return sSendStat;
    }

    public void setsSendStat(String sSendStat) {
        this.sSendStat = sSendStat;
    }
}
