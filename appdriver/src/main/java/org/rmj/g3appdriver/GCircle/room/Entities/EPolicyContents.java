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
    @ColumnInfo(name = "sNamexx")
    public String sNamexx;
    @ColumnInfo(name = "sOffenses")
    public String sOffenses;

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

    public String getsNamexx() {
        return sNamexx;
    }

    public void setsNamexx(String sNamexx) {
        this.sNamexx = sNamexx;
    }

    public String getsOffenses() {
        return sOffenses;
    }

    public void setsOffenses(String sOffenses) {
        this.sOffenses = sOffenses;
    }
}
