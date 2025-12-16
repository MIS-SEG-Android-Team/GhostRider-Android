package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CAS_Approval_Code")
public class ECASApprovalCode{

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sSourceCD")
    public String sSourceCD;

    @ColumnInfo(name = "sDescript")
    public String sDescript;

    @NonNull
    public String getsSourceCD() {
        return sSourceCD;
    }

    public void setsSourceCD(@NonNull String sSourceCD) {
        this.sSourceCD = sSourceCD;
    }

    public String getsDescript() {
        return sDescript;
    }

    public void setsDescript(String sDescript) {
        this.sDescript = sDescript;
    }
}
