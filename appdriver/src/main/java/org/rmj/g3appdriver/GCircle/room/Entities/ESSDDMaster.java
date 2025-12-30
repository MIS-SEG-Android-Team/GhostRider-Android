package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SSDD_Master")
public class ESSDDMaster {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sTransNox")
    public String sTransNox;

    @ColumnInfo(name = "dTransact")
    public String dTransact;

    @ColumnInfo(name = "cTranStat")
    public String cTranStat;

    @ColumnInfo(name = "sDeptIDxx")
    public String sDeptIDxx;

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

    public String getcTranStat() {
        return cTranStat;
    }

    public void setcTranStat(String cTranStat) {
        this.cTranStat = cTranStat;
    }

    public String getsDeptIDxx() {
        return sDeptIDxx;
    }

    public void setsDeptIDxx(String sDeptIDxx) {
        this.sDeptIDxx = sDeptIDxx;
    }
}
