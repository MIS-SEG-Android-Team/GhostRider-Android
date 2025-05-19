package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "SCA_Rqst_Emp", primaryKeys = {"sSCACodex","sEmployIDx"})
public class ESCARqstEmp {
    @NonNull
    @ColumnInfo
    public String sSCACodex;
    @NonNull
    @ColumnInfo
    public String sEmployIDx;
    @ColumnInfo
    public String sRecdStat;
    @ColumnInfo
    public String dTimeStmpx;

    @NonNull
    public String getsSCACodex() {
        return sSCACodex;
    }

    public void setsSCACodex(@NonNull String sSCACodex) {
        this.sSCACodex = sSCACodex;
    }

    @NonNull
    public String getsEmployIDx() {
        return sEmployIDx;
    }

    public void setsEmployIDx(@NonNull String sEmployIDx) {
        this.sEmployIDx = sEmployIDx;
    }

    public String getsRecdStat() {
        return sRecdStat;
    }

    public void setsRecdStat(String sRecdStat) {
        this.sRecdStat = sRecdStat;
    }

    public String getdTimeStmpx() {
        return dTimeStmpx;
    }

    public void setdTimeStmpx(String dTimeStmpx) {
        this.dTimeStmpx = dTimeStmpx;
    }
}
