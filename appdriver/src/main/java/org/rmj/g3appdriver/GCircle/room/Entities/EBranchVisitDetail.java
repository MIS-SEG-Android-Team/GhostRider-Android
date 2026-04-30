package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Branch_Visit_Detail", primaryKeys = {"sTransNox", "sCategrID"})
public class EBranchVisitDetail {

    @ColumnInfo(name = "sTransNox")
    @NonNull
    public String sTransNox;

    @ColumnInfo(name = "sCategrID")
    @NonNull
    public String sCategrID;

    @ColumnInfo(name = "sRemarksx")
    public String sRemarksx;

    @NonNull
    public String getsTransNox() {
        return sTransNox;
    }

    public void setsTransNox(@NonNull String sTransNox) {
        this.sTransNox = sTransNox;
    }

    @NonNull
    public String getsCategrID() {
        return sCategrID;
    }

    public void setsCategrID(@NonNull String sCategrID) {
        this.sCategrID = sCategrID;
    }

    public String getsRemarksx() {
        return sRemarksx;
    }

    public void setsRemarksx(String sRemarksx) {
        this.sRemarksx = sRemarksx;
    }
}
