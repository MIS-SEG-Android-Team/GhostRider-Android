package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SSDD_Detail", primaryKeys = {"sTransNox", "sCategrID"})
public class ESSDDetail {
    
    @ColumnInfo(name = "sTransNox")
    public String sTransNox;

    @ColumnInfo(name = "sCategrID")
    public String sCategrID;

    @ColumnInfo(name = "nRatingxx")
    public String nRatingxx;

    @ColumnInfo(name = "sRemarksx")
    public String sRemarksx;

    @ColumnInfo(name = "dEvaluate")
    public String dEvaluate;

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

    public String getnRatingxx() {
        return nRatingxx;
    }

    public void setnRatingxx(String nRatingxx) {
        this.nRatingxx = nRatingxx;
    }

    public String getsRemarksx() {
        return sRemarksx;
    }

    public void setsRemarksx(String sRemarksx) {
        this.sRemarksx = sRemarksx;
    }

    public String getdEvaluate() {
        return dEvaluate;
    }

    public void setdEvaluate(String dEvaluate) {
        this.dEvaluate = dEvaluate;
    }
}
