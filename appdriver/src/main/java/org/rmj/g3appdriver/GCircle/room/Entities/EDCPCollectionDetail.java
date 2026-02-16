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

@Entity(tableName = "LR_DCP_Collection_Detail", primaryKeys = {"sTransNox", "nEntryNox"})
public class EDCPCollectionDetail {

    @NonNull
    @ColumnInfo(name = "sTransNox")
    private String TransNox;

    @NonNull
    @ColumnInfo(name = "nEntryNox")
    private int EntryNox;
    @ColumnInfo(name = "sAcctNmbr")
    private String AcctNmbr = "";
    @ColumnInfo(name = "xFullName")
    private String FullName = "";
    @ColumnInfo(name = "sPRNoxxxx")
    private String PRNoxxxx = "";
    @ColumnInfo(name = "nTranAmtx")
    private Double TranAmtx = 0.00;
    @ColumnInfo(name = "nDiscount")
    private Double Discount = 0.00;
    @ColumnInfo(name = "nOthersxx")
    private Double Othersxx = 0.00;
    @ColumnInfo(name = "sRemarksx")
    private String Remarksx = "";
    @ColumnInfo(name = "sBankIDxx")
    private String BankIDxx = "";
    @ColumnInfo(name = "sCheckDte")
    private String CheckDte = "";
    @ColumnInfo(name = "sCheckNox")
    private String CheckNox = "";
    @ColumnInfo(name = "sCheckAct")
    private String CheckAct = "";
    @ColumnInfo(name = "dPromised")
    private String Promised = "";
    @ColumnInfo(name = "sRemCodex")
    private String RemCodex = "";
    @ColumnInfo(name = "cTranType")
    private String TranType = "";
    @ColumnInfo(name = "nTranTotl")
    private Double TranTotl = 0.00;
    @ColumnInfo(name = "sReferNox")
    private String ReferNox = "";
    @ColumnInfo(name = "cPaymForm")
    private String PaymForm = "0";
    @ColumnInfo(name = "sEWalletReference")
    public String sEWalletReference = "";
    @ColumnInfo(name = "cIsDCPxxx")
    private String IsDCPxxx = "";
    @ColumnInfo(name = "sMobileNo")
    private String MobileNo = "";
    @ColumnInfo(name = "sHouseNox")
    private String HouseNox = "";
    @ColumnInfo(name = "sAddressx")
    private String Addressx = "";
    @ColumnInfo(name = "sBrgyName")
    private String BrgyName = "";
    @ColumnInfo(name = "sTownName")
    private String TownName = "";
    @ColumnInfo(name = "dPurchase")
    private String Purchase = "";
    @ColumnInfo(name = "nMonAmort")
    private Double MonAmort = 0.00;
    @ColumnInfo(name = "nAmtDuexx")
    private Double AmtDuexx = 0.00;
    @ColumnInfo(name = "cApntUnit")
    private String ApntUnit = "";
    @ColumnInfo(name = "sBranchCd")
    private String BranchCd = "";
    @ColumnInfo(name = "dDueDatex")
    private String DueDatex = "";
    @ColumnInfo(name = "sImageNme")
    private String ImageNme = "";
    @ColumnInfo(name = "nLongitud")
    private Double Longitud = 0.00;
    @ColumnInfo(name = "nLatitude")
    private Double Latitude = 0.00;
    @ColumnInfo(name = "sClientID")
    private String ClientID = "";
    @ColumnInfo(name = "sSerialID")
    private String SerialID = "";
    @ColumnInfo(name = "sSerialNo")
    private String SerialNo = "";
    @ColumnInfo(name = "nLastPaym")
    private Double LastPaym = 0.00;
    @ColumnInfo(name = "dLastPaym")
    private String LastPaid = "0";
    @ColumnInfo(name = "nABalance")
    private String ABalance = "0";
    @ColumnInfo(name = "nDelayAvg")
    private Double DelayAvg = 0.00;
    @ColumnInfo(name = "cTranStat")
    private String TranStat = "0";
    @ColumnInfo(name = "cSendStat")
    private String SendStat = "0";
    @ColumnInfo(name = "dSendDate")
    private String SendDate;
    @ColumnInfo(name = "dModified")
    private String Modified;

    public EDCPCollectionDetail() {
    }

    @NonNull
    public String getTransNox() {
        return TransNox;
    }

    public void setTransNox(@NonNull String transNox) {
        TransNox = transNox;
    }

    public int getEntryNox() {
        return EntryNox;
    }

    public void setEntryNox(int entryNox) {
        EntryNox = entryNox;
    }

    public String getAcctNmbr() {
        return AcctNmbr;
    }

    public void setAcctNmbr(String acctNmbr) {
        AcctNmbr = acctNmbr;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getPRNoxxxx() {
        return PRNoxxxx;
    }

    public void setPRNoxxxx(String PRNoxxxx) {
        this.PRNoxxxx = PRNoxxxx;
    }

    public Double getTranAmtx() {
        return TranAmtx;
    }

    public void setTranAmtx(Double tranAmtx) {
        TranAmtx = tranAmtx;
    }

    public Double getDiscount() {
        return Discount;
    }

    public void setDiscount(Double discount) {
        Discount = discount;
    }

    public Double getOthersxx() {
        return Othersxx;
    }

    public void setOthersxx(Double othersxx) {
        Othersxx = othersxx;
    }

    public String getRemarksx() {
        return Remarksx;
    }

    public void setRemarksx(String remarksx) {
        Remarksx = remarksx;
    }

    public String getBankIDxx() {
        return BankIDxx;
    }

    public void setBankIDxx(String bankIDxx) {
        BankIDxx = bankIDxx;
    }

    public String getCheckDte() {
        return CheckDte;
    }

    public void setCheckDte(String checkDte) {
        CheckDte = checkDte;
    }

    public String getCheckNox() {
        return CheckNox;
    }

    public void setCheckNox(String checkNox) {
        CheckNox = checkNox;
    }

    public String getCheckAct() {
        return CheckAct;
    }

    public void setCheckAct(String checkAct) {
        CheckAct = checkAct;
    }

    public String getPromised() {
        return Promised;
    }

    public void setPromised(String promised) {
        Promised = promised;
    }

    public String getRemCodex() {
        return RemCodex;
    }

    public void setRemCodex(String remCodex) {
        RemCodex = remCodex;
    }

    public String getTranType() {
        return TranType;
    }

    public void setTranType(String tranType) {
        TranType = tranType;
    }

    public Double getTranTotl() {
        return TranTotl;
    }

    public void setTranTotl(Double tranTotl) {
        TranTotl = tranTotl;
    }

    public String getReferNox() {
        return ReferNox;
    }

    public void setReferNox(String referNox) {
        ReferNox = referNox;
    }

    public String getPaymForm() {
        return PaymForm;
    }

    public void setPaymForm(String paymForm) {
        PaymForm = paymForm;
    }

    public String getsEWalletReference() {
        return sEWalletReference;
    }

    public void setsEWalletReference(String sEWalletReference) {
        this.sEWalletReference = sEWalletReference;
    }

    public String getIsDCPxxx() {
        return IsDCPxxx;
    }

    public void setIsDCPxxx(String isDCPxxx) {
        IsDCPxxx = isDCPxxx;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getHouseNox() {
        return HouseNox;
    }

    public void setHouseNox(String houseNox) {
        HouseNox = houseNox;
    }

    public String getAddressx() {
        return Addressx;
    }

    public void setAddressx(String addressx) {
        Addressx = addressx;
    }

    public String getBrgyName() {
        return BrgyName;
    }

    public void setBrgyName(String brgyName) {
        BrgyName = brgyName;
    }

    public String getTownName() {
        return TownName;
    }

    public void setTownName(String townName) {
        TownName = townName;
    }

    public String getPurchase() {
        return Purchase;
    }

    public void setPurchase(String purchase) {
        Purchase = purchase;
    }

    public Double getMonAmort() {
        return MonAmort;
    }

    public void setMonAmort(Double monAmort) {
        MonAmort = monAmort;
    }

    public Double getAmtDuexx() {
        return AmtDuexx;
    }

    public void setAmtDuexx(Double amtDuexx) {
        AmtDuexx = amtDuexx;
    }

    public String getApntUnit() {
        return ApntUnit;
    }

    public void setApntUnit(String apntUnit) {
        ApntUnit = apntUnit;
    }

    public String getImageNme() {
        return ImageNme;
    }

    public String getBranchCd() {
        return BranchCd;
    }

    public void setBranchCd(String branchCd) {
        BranchCd = branchCd;
    }

    public void setImageNme(String imageNme) {
        ImageNme = imageNme;
    }

    public String getDueDatex() {
        return DueDatex;
    }

    public void setDueDatex(String dueDatex) {
        DueDatex = dueDatex;
    }

    public Double getLongitud() {
        return Longitud;
    }

    public void setLongitud(Double longitud) {
        Longitud = longitud;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public String getClientID() {
        return ClientID;
    }

    public void setClientID(String clientID) {
        ClientID = clientID;
    }

    public String getSerialID() {
        return SerialID;
    }

    public void setSerialID(String serialID) {
        SerialID = serialID;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public Double getLastPaym() {
        return LastPaym;
    }

    public void setLastPaym(Double lastPaym) {
        LastPaym = lastPaym;
    }

    public String getLastPaid() {
        return LastPaid;
    }

    public void setLastPaid(String lastPaid) {
        LastPaid = lastPaid;
    }

    public String getABalance() {
        return ABalance;
    }

    public void setABalance(String ABalance) {
        this.ABalance = ABalance;
    }

    public Double getDelayAvg() {
        return DelayAvg;
    }

    public void setDelayAvg(Double delayAvg) {
        DelayAvg = delayAvg;
    }

    public String getTranStat() {
        return TranStat;
    }

    public void setTranStat(String tranStat) {
        TranStat = tranStat;
    }

    public String getSendStat() {
        return SendStat;
    }

    public void setSendStat(String sendStat) {
        SendStat = sendStat;
    }

    public String getSendDate() {
        return SendDate;
    }

    public void setSendDate(String sendDate) {
        SendDate = sendDate;
    }

    public String getModified() {
        return Modified;
    }

    public void setModified(String modified) {
        Modified = modified;
    }

}
