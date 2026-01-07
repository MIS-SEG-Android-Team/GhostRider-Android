package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SSDD_Images")
public class ESSDDImages {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sTransNox")
    public String sTransNox;

    @ColumnInfo(name = "sReferNox")
    public String sReferNox;

    @ColumnInfo(name = "sCategrID")
    public String sCategrID;

    @ColumnInfo(name = "nEntryNox")
    public String nEntryNox;

    @ColumnInfo(name = "sScanndID")
    public String sScanndID;

    @ColumnInfo(name = "sImageNme")
    public String sImageNme;

    @ColumnInfo(name = "sMD5Hashx")
    public String sMD5Hashx;

    @ColumnInfo(name = "sImagePth")
    public String sImagePth;

    @ColumnInfo(name = "dImgeDate")
    public String dImgeDate;

    @ColumnInfo(name = "cImgeStat")
    public String cImgeStat;

    @NonNull
    public String getsTransNox() {
        return sTransNox;
    }

    public void setsTransNox(@NonNull String sTransNox) {
        this.sTransNox = sTransNox;
    }

    public String getsReferNox() {
        return sReferNox;
    }

    public void setsReferNox(String sReferNox) {
        this.sReferNox = sReferNox;
    }

    public String getsCategrID() {
        return sCategrID;
    }

    public void setsCategrID(String sCategrID) {
        this.sCategrID = sCategrID;
    }

    public String getnEntryNox() {
        return nEntryNox;
    }

    public void setnEntryNox(String nEntryNox) {
        this.nEntryNox = nEntryNox;
    }

    public String getsScanndID() {
        return sScanndID;
    }

    public void setsScanndID(String sScanndID) {
        this.sScanndID = sScanndID;
    }

    public String getsImageNme() {
        return sImageNme;
    }

    public void setsImageNme(String sImageNme) {
        this.sImageNme = sImageNme;
    }

    public String getsMD5Hashx() {
        return sMD5Hashx;
    }

    public void setsMD5Hashx(String sMD5Hashx) {
        this.sMD5Hashx = sMD5Hashx;
    }

    public String getsImagePth() {
        return sImagePth;
    }

    public void setsImagePth(String sImagePth) {
        this.sImagePth = sImagePth;
    }

    public String getdImgeDate() {
        return dImgeDate;
    }

    public void setdImgeDate(String dImgeDate) {
        this.dImgeDate = dImgeDate;
    }

    public String getcImgeStat() {
        return cImgeStat;
    }

    public void setcImgeStat(String cImgeStat) {
        this.cImgeStat = cImgeStat;
    }
}
