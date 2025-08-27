package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Policy_Menus", primaryKeys = {"sTransNoxx"})
public class EPolicyMenus{

    @ColumnInfo(name = "sTransNoxx")
    @NonNull
    public String sTransNoxx;
    @ColumnInfo(name = "sNamexx")
    public String sNamexx;

    @NonNull
    public String getsTransNoxx() {
        return sTransNoxx;
    }

    public void setsTransNoxx(@NonNull String sTransNoxx) {
        this.sTransNoxx = sTransNoxx;
    }

    public String getsNamexx() {
        return sNamexx;
    }

    public void setsNamexx(String sNamexx) {
        this.sNamexx = sNamexx;
    }
}
