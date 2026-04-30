package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Branch_Visit_Master", primaryKeys = {"sTransNox"})
public class EBranchVisitMaster {

    @ColumnInfo(name = "sTransNox")
    @NonNull
    public String sTransNox;

    @ColumnInfo(name = "dTransact")
    public String dTransact;

    @ColumnInfo(name = "sBranchCd")
    public String sBranchCd;

    @ColumnInfo(name = "sUserIDxx")
    public String sUserIDxx;

    @ColumnInfo(name = "cTranStat")
    public String cTranStat;

    @ColumnInfo(name = "cSendStat")
    public String cSendStat;

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

    public String getsBranchCd() {
        return sBranchCd;
    }

    public void setsBranchCd(String sBranchCd) {
        this.sBranchCd = sBranchCd;
    }

    public String getsUserIDxx() {
        return sUserIDxx;
    }

    public void setsUserIDxx(String sUserIDxx) {
        this.sUserIDxx = sUserIDxx;
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
}
