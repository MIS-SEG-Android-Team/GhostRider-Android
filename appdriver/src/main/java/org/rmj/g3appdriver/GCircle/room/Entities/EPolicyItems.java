package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Policy_Items", primaryKeys = {"sTransNoxx"})
public class EPolicyItems {

    @ColumnInfo(name = "sTransNoxx")
    @NonNull
    public String sTransNoxx;
    @ColumnInfo(name = "sTitlexx")
    public String sTitlexx;
    @ColumnInfo(name = "sDescriptionxx")
    public String sDescriptionxx;
    @ColumnInfo(name = "sImagexx")
    public String sImagexx;
    @ColumnInfo(name = "sSubTitlexx")
    public String sSubTitlexx;
    @ColumnInfo(name = "sParentIDxx")
    public String sParentIDxx;

    @NonNull
    public String getsTransNoxx() {
        return sTransNoxx;
    }

    public void setsTransNoxx(@NonNull String sTransNoxx) {
        this.sTransNoxx = sTransNoxx;
    }

    public String getsTitlexx() {
        return sTitlexx;
    }

    public void setsTitlexx(String sTitlexx) {
        this.sTitlexx = sTitlexx;
    }

    public String getsDescriptionxx() {
        return sDescriptionxx;
    }

    public void setsDescriptionxx(String sDescriptionxx) {
        this.sDescriptionxx = sDescriptionxx;
    }

    public String getsImagexx() {
        return sImagexx;
    }

    public void setsImagexx(String sImagexx) {
        this.sImagexx = sImagexx;
    }

    public String getsSubTitlexx() {
        return sSubTitlexx;
    }

    public void setsSubTitlexx(String sSubTitlexx) {
        this.sSubTitlexx = sSubTitlexx;
    }

    public String getsParentIDxx() {
        return sParentIDxx;
    }

    public void setsParentIDxx(String sParentIDxx) {
        this.sParentIDxx = sParentIDxx;
    }
}
