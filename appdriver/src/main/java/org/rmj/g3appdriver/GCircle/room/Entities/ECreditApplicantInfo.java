/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.g3appdriver.GCircle.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Credit_Applicant_Info")
public class ECreditApplicantInfo {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sTransNox")
    private String TransNox;
    @ColumnInfo(name = "sClientNm")
    private String ClientNm;
    @ColumnInfo(name = "sDetlInfo")
    private String DetlInfo;
    @ColumnInfo(name = "sPurchase")
    private String Purchase;
    @ColumnInfo(name = "sApplInfo")
    private String ApplInfo;
    @ColumnInfo(name = "sResidnce")
    private String Residnce;
    @ColumnInfo(name = "cSameAddx")
    private String SameAddx;
    @ColumnInfo(name = "sAppMeans")
    private String AppMeans;
    @ColumnInfo(name = "sEmplymnt")
    private String Emplymnt;
    @ColumnInfo(name = "sBusnInfo")
    private String BusnInfo;
    @ColumnInfo(name = "sFinancex")
    private String Financex;
    @ColumnInfo(name = "sPensionx")
    private String Pensionx;
    @ColumnInfo(name = "sOtherInc")
    private String OtherInc;
    @ColumnInfo(name = "sSpousexx")
    private String Spousexx;
    @ColumnInfo(name = "sSpsResdx")
    private String SpsResdx;
    @ColumnInfo(name = "sSpsMEans")
    private String SpsMeans;
    @ColumnInfo(name = "sSpsEmplx")
    private String SpsEmplx;
    @ColumnInfo(name = "sSpsBusnx")
    private String SpsBusnx;
    @ColumnInfo(name = "sSpsPensn")
    private String SpsPensn;
    @ColumnInfo(name = "sSpOthInc")
    private String SpOthInc;
    @ColumnInfo(name = "sDisbrsmt")
    private String Disbrsmt;
    @ColumnInfo(name = "sDependnt")
    private String Dependnt;
    @ColumnInfo(name = "sProperty")
    private String Property;
    @ColumnInfo(name = "sOthrInfo")
    private String OthrInfo;
    @ColumnInfo(name = "sComakerx")
    private String Comakerx;
    @ColumnInfo(name = "sCmResidx")
    private String CmResidx;
    @ColumnInfo(name = "cIsSpouse")
    private String IsSpouse;
    @ColumnInfo(name = "cIsComakr")
    private String IsComakr;
    @ColumnInfo(name = "sBranchCd")
    private String BranchCd;
    @ColumnInfo(name = "cAppliedx")
    private String Appliedx;
    @ColumnInfo(name = "dTransact")
    private String Transact;
    @ColumnInfo(name = "dCreatedx")
    private String Createdx;
    @ColumnInfo(name = "nDownPaym")
    private double DownPaym;
    @ColumnInfo(name = "cTranStat")
    private String TranStat;
    @ColumnInfo(name = "sRemarksx")
    private String Remarksx;

    public ECreditApplicantInfo() {
    }

    @NonNull
    public String getTransNox() {
        return TransNox;
    }

    public void setTransNox(@NonNull String transNox) {
        TransNox = transNox;
    }

    public String getClientNm() {
        return ClientNm;
    }

    public void setClientNm(String clientNm) {
        ClientNm = clientNm;
    }

    public String getDetlInfo() {
        return DetlInfo;
    }

    public void setDetlInfo(String detlInfo) {
        DetlInfo = detlInfo;
    }

    public String getPurchase() {
        return Purchase;
    }

    public void setPurchase(String purchase) {
        Purchase = purchase;
    }

    public String getApplInfo() {
        return ApplInfo;
    }

    public void setApplInfo(String applInfo) {
        ApplInfo = applInfo;
    }

    public String getResidnce() {
        return Residnce;
    }

    public void setResidnce(String residnce) {
        Residnce = residnce;
    }

    public String getAppMeans() {
        return AppMeans;
    }

    public void setAppMeans(String appMeans) {
        AppMeans = appMeans;
    }

    public String getEmplymnt() {
        return Emplymnt;
    }

    public void setEmplymnt(String emplymnt) {
        Emplymnt = emplymnt;
    }

    public String getBusnInfo() {
        return BusnInfo;
    }

    public void setBusnInfo(String busnInfo) {
        BusnInfo = busnInfo;
    }

    public String getFinancex() {
        return Financex;
    }

    public void setFinancex(String financex) {
        Financex = financex;
    }

    public String getPensionx() {
        return Pensionx;
    }

    public void setPensionx(String pensionx) {
        Pensionx = pensionx;
    }

    public String getOtherInc() {
        return OtherInc;
    }

    public void setOtherInc(String otherInc) {
        OtherInc = otherInc;
    }

    public String getSpousexx() {
        return Spousexx;
    }

    public void setSpousexx(String spousexx) {
        Spousexx = spousexx;
    }

    public String getSpsResdx() {
        return SpsResdx;
    }

    public void setSpsResdx(String spsResdx) {
        SpsResdx = spsResdx;
    }

    public String getSpsMeans() {
        return SpsMeans;
    }

    public void setSpsMeans(String spsMeans) {
        SpsMeans = spsMeans;
    }

    public String getSpsEmplx() {
        return SpsEmplx;
    }

    public void setSpsEmplx(String spsEmplx) {
        SpsEmplx = spsEmplx;
    }

    public String getSpsBusnx() {
        return SpsBusnx;
    }

    public void setSpsBusnx(String spsBusnx) {
        SpsBusnx = spsBusnx;
    }

    public String getSpsPensn() {
        return SpsPensn;
    }

    public void setSpsPensn(String spsPensn) {
        SpsPensn = spsPensn;
    }

    public String getSpOthInc() {
        return SpOthInc;
    }

    public void setSpOthInc(String spOthInc) {
        SpOthInc = spOthInc;
    }

    public String getDisbrsmt() {
        return Disbrsmt;
    }

    public void setDisbrsmt(String disbrsmt) {
        Disbrsmt = disbrsmt;
    }

    public String getDependnt() {
        return Dependnt;
    }

    public void setDependnt(String dependnt) {
        Dependnt = dependnt;
    }

    public String getProperty() {
        return Property;
    }

    public void setProperty(String property) {
        Property = property;
    }

    public String getOthrInfo() {
        return OthrInfo;
    }

    public void setOthrInfo(String othrInfo) {
        OthrInfo = othrInfo;
    }

    public String getComakerx() {
        return Comakerx;
    }

    public void setComakerx(String comakerx) {
        Comakerx = comakerx;
    }

    public String getCmResidx() {
        return CmResidx;
    }

    public void setCmResidx(String cmResidx) {
        CmResidx = cmResidx;
    }

    public String getIsSpouse() {
        return IsSpouse;
    }

    public void setIsSpouse(String isSpouse) {
        IsSpouse = isSpouse;
    }

    public String getIsComakr() {
        return IsComakr;
    }

    public void setIsComakr(String isComakr) {
        IsComakr = isComakr;
    }

    public String getBranchCd() {
        return BranchCd;
    }

    public void setBranchCd(String branchCd) {
        BranchCd = branchCd;
    }

    public String getAppliedx() {
        return Appliedx;
    }

    public void setAppliedx(String appliedx) {
        Appliedx = appliedx;
    }

    public String getTransact() {
        return Transact;
    }

    public void setTransact(String transact) {
        Transact = transact;
    }

    public String getCreatedx() {
        return Createdx;
    }

    public void setCreatedx(String createdx) {
        Createdx = createdx;
    }

    public double getDownPaym() {
        return DownPaym;
    }

    public void setDownPaym(double downPaym) {
        DownPaym = downPaym;
    }

    public String getTranStat() {
        return TranStat;
    }

    public void setTranStat(String tranStat) {
        TranStat = tranStat;
    }

    public String getSameAddx() {
        return SameAddx;
    }

    public void setSameAddx(String sameAddx) {
        SameAddx = sameAddx;
    }

    public String getRemarksx() {
        return Remarksx;
    }

    public void setRemarksx(String remarksx) {
        Remarksx = remarksx;
    }
}
