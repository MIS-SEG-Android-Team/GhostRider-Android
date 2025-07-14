package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "User_Guides", primaryKeys = {"sTransNox", "type"})
public class EGuides {

    @ColumnInfo
    @NonNull
    public String sTransNox;

    @ColumnInfo
    @NonNull
    public String type;

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

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
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
