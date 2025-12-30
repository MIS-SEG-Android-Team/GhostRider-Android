package org.rmj.g3appdriver.GCircle.room.Entities;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SSDD_Categories")
public class ESSDDCategories {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sCategrID")
    public String sCategrID;

    @ColumnInfo(name = "sDescript")
    public String sDescript;

    @ColumnInfo(name = "sMemoLink")
    public String sMemoLink;

    @ColumnInfo(name = "nPageNumber")
    public String nPageNumber;

    @NonNull
    public String getsCategrID() {
        return sCategrID;
    }

    public void setsCategrID(@NonNull String sCategrID) {
        this.sCategrID = sCategrID;
    }

    public String getsDescript() {
        return sDescript;
    }

    public void setsDescript(String sDescript) {
        this.sDescript = sDescript;
    }

    public String getsMemoLink() {
        return sMemoLink;
    }

    public void setsMemoLink(String sMemoLink) {
        this.sMemoLink = sMemoLink;
    }

    public String getnPageNumber() {
        return nPageNumber;
    }

    public void setnPageNumber(String nPageNumber) {
        this.nPageNumber = nPageNumber;
    }
}
