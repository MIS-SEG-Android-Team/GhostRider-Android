package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Article_Details", primaryKeys = {"sCodexx"})
public class EArticleDetails {

    @NonNull
    @ColumnInfo(name = "sCodexx")
    public String sCodexx;

    @ColumnInfo(name = "sContentxx")
    public String sContentxx;

    @NonNull
    public String getsCodexx() {
        return sCodexx;
    }

    public void setsCodexx(@NonNull String sCodexx) {
        this.sCodexx = sCodexx;
    }

    public String getsContentxx() {
        return sContentxx;
    }

    public void setsContentxx(String sContentxx) {
        this.sContentxx = sContentxx;
    }
}
