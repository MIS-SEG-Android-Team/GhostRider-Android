package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Branch_Visit_Checklist", primaryKeys = {"sCategrID"})
public class EBranchVisitChecklist {

    @ColumnInfo(name = "sCategrID")
    @NonNull
    public String sCategrID;

    @ColumnInfo(name = "sDescript")
    public String sDescript;

    @ColumnInfo(name = "cRecdStat")
    public String cRecdStat = "0";

    @NonNull
    public String getsCategrID() {
        return sCategrID;
    }

    public void setsCategrID(@NonNull String sCategrID) {
        this.sCategrID = sCategrID;
    }

    public String getsDescript() {
        return sDescript;
    }

    public void setsDescript(String sDescript) {
        this.sDescript = sDescript;
    }

    public String getcRecdStat() {
        return cRecdStat;
    }

    public void setcRecdStat(String cRecdStat) {
        this.cRecdStat = cRecdStat;
    }
}
