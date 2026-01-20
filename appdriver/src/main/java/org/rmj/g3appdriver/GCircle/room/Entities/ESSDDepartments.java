package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SSDD_Department")
public class ESSDDepartments {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sDeptIDxx")
    public String sDeptIDxx;

    @ColumnInfo(name = "sDescript")
    public String sDescript;

    @NonNull
    public String getsDeptIDxx() {
        return sDeptIDxx;
    }

    public void setsDeptIDxx(@NonNull String sDeptIDxx) {
        this.sDeptIDxx = sDeptIDxx;
    }

    public String getsDescript() {
        return sDescript;
    }

    public void setsDescript(String sDescript) {
        this.sDescript = sDescript;
    }
}
