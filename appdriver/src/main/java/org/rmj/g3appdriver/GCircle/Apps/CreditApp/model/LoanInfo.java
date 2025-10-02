package org.rmj.g3appdriver.GCircle.Apps.CreditApp.model;

public class LoanInfo {

    private String sAppTypex = ""; //must be index value
    private String sCustTypex = ""; //must be index value
    private String dTargetDte = "";
    private String sBranchCde = "";
    private String sBrandIDxx = "";
    private String sModelIDxx = "";
    private double sDownPaymt = 0;
    private int sAccTermxx = 0;
    private double sMonthlyAm = 0;
    private String sRemarks = "";

    private String message;

    public LoanInfo() {

    }

    public String getMessage() {
        return message;
    }

    public String getAppTypex() {
        return sAppTypex;
    }

    public void setAppTypex(String sAppTypex) {
        this.sAppTypex = sAppTypex;
    }

    public String getCustTypex() {
        return sCustTypex;
    }

    public void setCustTypex(String sCustTypex) {
        this.sCustTypex = sCustTypex;
    }

    public String getTargetDte() {
        return dTargetDte;
    }

    public void setTargetDte(String dTargetDte) {
        this.dTargetDte = dTargetDte;
    }

    public String getBranchCde() {
        return sBranchCde;
    }

    public void setBranchCde(String sBranchCde) {
        this.sBranchCde = sBranchCde;
    }

    public String getBrandIDxx() {
        return sBrandIDxx;
    }

    public void setBrandIDxx(String sBrandIDxx) {
        this.sBrandIDxx = sBrandIDxx;
    }

    public String getModelIDxx() {
        return sModelIDxx;
    }

    public void setModelIDxx(String sModelIDxx) {
        this.sModelIDxx = sModelIDxx;
    }

    public double getDownPaymt() {
        return sDownPaymt;
    }

    public void setDownPaymt(double sDownPaymt) {
        this.sDownPaymt = sDownPaymt;
    }

    public int getAccTermxx() {
        return sAccTermxx;
    }

    public void setAccTermxx(int args) {
        switch (args) {
            case 0:
                sAccTermxx = 36;
                break;
            case 1:
                sAccTermxx = 24;
                break;
            case 2:
                sAccTermxx = 18;
                break;
            case 3:
                sAccTermxx = 12;
                break;
            case 4:
                sAccTermxx = 6;
                break;
        }
    }

    public double getMonthlyAm() {
        return sMonthlyAm;
    }

    public void setMonthlyAm(double sMonthlyAm) {
        this.sMonthlyAm = sMonthlyAm;
    }

    public String getsRemarks(){
        return sRemarks;
    }

    public void setsRemarks(String sRemarks){
        this.sRemarks = sRemarks;
    }

    public boolean isDataValid(){
        if(sCustTypex.trim().isEmpty()){
            message = "Please select customer type";
            return false;
        }

        if(sAppTypex.trim().isEmpty()){
            message = "Please select loan unit";
            return false;
        }
        if(sBranchCde.trim().isEmpty()){
            message = "Please select preferred branch";
            return false;
        }
        if(sBrandIDxx.trim().isEmpty()){
            message = "Please select motorcycle brand";
            return false;
        }
        if(sModelIDxx.trim().isEmpty()){
            message = "Please select mc model";
            return false;
        }
        if(sDownPaymt == 0){
            message = "Please enter preferred downpayment";
            return false;
        }
        if(sAccTermxx == 0){
            message = "Please select installment term";
            return false;
        }
        return true;
    }
}
