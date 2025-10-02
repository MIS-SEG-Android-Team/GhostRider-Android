package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Article_Head", primaryKeys = {"sCodexx"})
public class EArticleHead {

    @NonNull
    @ColumnInfo(name = "sCodexx")
    public String sCodexx;

    @ColumnInfo(name = "sTitle")
    public String sTitle;

    @ColumnInfo(name = "sImage")
    public String sImage;

    @ColumnInfo(name = "sDescription")
    public String sDescription;

    @ColumnInfo(name = "sSubtitle")
    public String sSubtitle;

    @NonNull
    public String getsCodexx() {
        return sCodexx;
    }

    public void setsCodexx(@NonNull String sCodexx) {
        this.sCodexx = sCodexx;
    }

    public String getsTitle() {
        return sTitle;
    }

    public void setsTitle(String sTitle) {
        this.sTitle = sTitle;
    }

    public String getsImage() {
        return sImage;
    }

    public void setsImage(String sImage) {
        this.sImage = sImage;
    }

    public String getsDescription() {
        return sDescription;
    }

    public void setsDescription(String sDescription) {
        this.sDescription = sDescription;
    }

    public String getsSubtitle() {
        return sSubtitle;
    }

    public void setsSubtitle(String sSubtitle) {
        this.sSubtitle = sSubtitle;
    }
}
