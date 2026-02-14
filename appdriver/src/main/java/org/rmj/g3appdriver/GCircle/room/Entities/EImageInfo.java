/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Image_Information")
public class EImageInfo implements Serializable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sTransNox")
    private String TransNox;
    @ColumnInfo(name = "sClientID")
    public String sClientID;
    @ColumnInfo(name = "sSourceCD")
    private String SourceCD;
    @ColumnInfo(name = "sSourceNo")
    private String SourceNo;
    @ColumnInfo(name = "sDtlSrcNo")
    private String DtlSrcNo;
    @ColumnInfo(name = "sFileCode")
    private String FileCode;
    @ColumnInfo(name = "sImageNme")
    private String ImageNme;
    @ColumnInfo(name = "sMD5Hashx")
    private String MD5Hashx;
    @ColumnInfo(name = "sFileLoct")
    private String FileLoct;
    @ColumnInfo(name = "nLatitude")
    private String Latitude;
    @ColumnInfo(name = "nLongitud")
    private String Longitud;
    @ColumnInfo(name = "dCaptured")
    private String Captured;
    @ColumnInfo(name = "cSendStat")
    private String SendStat = "0";
    @ColumnInfo(name = "dSendDate")
    private String SendDate;

    public EImageInfo() {
    }

    @NonNull
    public String getTransNox() {
        return TransNox;
    }

    public void setTransNox(@NonNull String transNox) {
        TransNox = transNox;
    }

    public String getsClientID() {
        return sClientID;
    }

    public void setsClientID(String sClientID) {
        this.sClientID = sClientID;
    }

    public String getSourceCD() {
        return SourceCD;
    }

    public void setSourceCD(String sourceCD) {
        SourceCD = sourceCD;
    }

    public String getSourceNo() {
        return SourceNo;
    }

    public void setSourceNo(String sourceNo) {
        SourceNo = sourceNo;
    }

    public String getDtlSrcNo() {
        return DtlSrcNo;
    }

    public void setDtlSrcNo(String dtlSrcNo) {
        DtlSrcNo = dtlSrcNo;
    }

    public String getFileCode() {
        return FileCode;
    }

    public void setFileCode(String fileCode) {
        FileCode = fileCode;
    }

    public String getImageNme() {
        return ImageNme;
    }

    public void setImageNme(String imageNme) {
        ImageNme = imageNme;
    }

    public String getMD5Hashx() {
        return MD5Hashx;
    }

    public void setMD5Hashx(String MD5Hashx) {
        this.MD5Hashx = MD5Hashx;
    }

    public String getFileLoct() {
        return FileLoct;
    }

    public void setFileLoct(String fileLoct) {
        FileLoct = fileLoct;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitud() {
        return Longitud;
    }

    public void setLongitud(String longitud) {
        Longitud = longitud;
    }

    public String getCaptured() {
        return Captured;
    }

    public void setCaptured(String captured) {
        Captured = captured;
    }

    public String getSendStat() {
        return this.SendStat;
    }

    public void setSendStat(String sendStat) {
        this.SendStat = sendStat;
    }

    public String getSendDate() {
        return SendDate;
    }

    public void setSendDate(String sendDate) {
        SendDate = sendDate;
    }
}
