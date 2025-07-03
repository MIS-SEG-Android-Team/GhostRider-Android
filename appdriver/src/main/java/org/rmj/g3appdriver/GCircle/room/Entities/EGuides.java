package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "User_Guides", primaryKeys = {"sTransNox"})
public class EGuides {

    @ColumnInfo
    @NonNull
    public String sTransNox;

    @ColumnInfo
    public String sTitlexx;

    @ColumnInfo
    public String sURlxx;

    @NonNull
    public String getsTransNox() {
        return sTransNox;
    }

    public void setsTransNox(@NonNull String sTransNox) {
        this.sTransNox = sTransNox;
    }

    public String getsTitlexx() {
        return sTitlexx;
    }

    public void setsTitlexx(String sTitlexx) {
        this.sTitlexx = sTitlexx;
    }

    public String getsURlxx() {
        return sURlxx;
    }

    public void setsURlxx(String sURlxx) {
        this.sURlxx = sURlxx;
    }
}
