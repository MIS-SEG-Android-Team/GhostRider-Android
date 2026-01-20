package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CAS_Requests")
public class ECASRequests {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sTransNox")
    public String sTransNox;

    @ColumnInfo(name = "dTransact")
    public String dTransact;

    @ColumnInfo(name = "sSourceCD")
    public String sSourceCD;

    @ColumnInfo(name = "sSourceNo")
    public String sSourceNo;

    @ColumnInfo(name = "sAuthType")
    public String sAuthType;

    @ColumnInfo(name = "sDescript")
    public String sDescript;

    @ColumnInfo(name = "sCompnyNm")
    public String sCompnyNm;

    @ColumnInfo(name = "sRemarksx")
    public String sRemarksx;

    @ColumnInfo(name = "cTranStat")
    public String cTranStat;

    @ColumnInfo(name = "dApproved")
    public String dApproved;

    @ColumnInfo(name = "sAppSrcNo")
    public String sAppSrcNo;

    public void setsTransNox(@NonNull String sTransNox) {
        this.sTransNox = sTransNox;
    }

    public void setdTransact(String dTransact) {
        this.dTransact = dTransact;
    }

    public void setsSourceCD(String sSourceCD) {
        this.sSourceCD = sSourceCD;
    }

    public void setsSourceNo(String sSourceNo) {
        this.sSourceNo = sSourceNo;
    }

    public void setsAuthType(String sAuthType) {
        this.sAuthType = sAuthType;
    }

    public void setsDescript(String sDescript) {
        this.sDescript = sDescript;
    }

    public void setsCompnyNm(String sCompnyNm) {
        this.sCompnyNm = sCompnyNm;
    }

    public void setsRemarksx(String sRemarksx) {
        this.sRemarksx = sRemarksx;
    }

    public void setcTranStat(String cTranStat) {
        this.cTranStat = cTranStat;
    }

    public void setdApproved(String dApproved) {
        this.dApproved = dApproved;
    }

    public void setsAppSrcNo(String sAppSrcNo) {
        this.sAppSrcNo = sAppSrcNo;
    }

    @NonNull
    public String getsTransNox() {
        return sTransNox;
    }

    public String getdTransact() {
        return dTransact;
    }

    public String getsSourceCD() {
        return sSourceCD;
    }

    public String getsSourceNo() {
        return sSourceNo;
    }

    public String getsAuthType() {
        return sAuthType;
    }

    public String getsDescript() {
        return sDescript;
    }

    public String getsCompnyNm() {
        return sCompnyNm;
    }

    public String getsRemarksx() {
        return sRemarksx;
    }

    public String getcTranStat() {
        return cTranStat;
    }

    public String getdApproved() {
        return dApproved;
    }

    public String getsAppSrcNo() {
        return sAppSrcNo;
    }
}
