package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Policy_Contents", primaryKeys = {"sTransNoxx"})
public class EPolicyContents {

    @ColumnInfo(name = "sTransNoxx")
    @NonNull
    public String sTransNoxx;
    @ColumnInfo(name = "sParentIDxx")
    public String sParentIDxx;
    @ColumnInfo(name = "sContentxx")
    public String sContentxx;

    @NonNull
    public String getsTransNoxx() {
        return sTransNoxx;
    }

    public void setsTransNoxx(@NonNull String sTransNoxx) {
        this.sTransNoxx = sTransNoxx;
    }

    public String getsParentIDxx() {
        return sParentIDxx;
    }

    public void setsParentIDxx(String sParentIDxx) {
        this.sParentIDxx = sParentIDxx;
    }

    public String getsContentxx() {
        return sContentxx;
    }

    public void setsContentxx(String sContentxx) {
        this.sContentxx = sContentxx;
    }
}
