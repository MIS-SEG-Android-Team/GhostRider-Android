package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Branch_Checklist", primaryKeys = {"sCategrID"})
public class EBranchChecklist {

    @ColumnInfo(name = "sCategrID")
    @NonNull
    public String sCategrID;

    @ColumnInfo(name = "sDescript")
    public String sDescript;

    @ColumnInfo(name = "cRecdStat")
    public String cRecdStat = "0";
}
