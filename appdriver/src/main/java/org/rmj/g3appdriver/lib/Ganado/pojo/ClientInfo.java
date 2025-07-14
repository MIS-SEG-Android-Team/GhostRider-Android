package org.rmj.g3appdriver.lib.Ganado.pojo;

public class ClientInfo {

    private String sTransNox = "";
    private String sLastName = "";
    private String sFrstName = "";
    private String sMiddName = "";
    private String sSuffixNm = "";
    private String sMaidenNm = "";
    private String cGenderCd = "";
    private String dBirthDte = "";
    private String sBirthIDx = "";
    private String sBirthPlc = "";
    private String sHouseNox = "";
    private String sAddressx = "";
    private String sTownIDxx = "";

    private String sMncplNme = "";
    private String sMobileNo = "";
    private String sEmailAdd = "";

    private String sReltionx = "";

    private String message;

    public ClientInfo() {
    }

    public String getMessage() {
        return message;
    }

    public String getsTransNox() {
        return sTransNox;
    }

    public void setsTransNox(String sTransNox) {
        this.sTransNox = sTransNox;
    }

    public String getLastName() {
        return sLastName;
    }

    public void setLastName(String sLastName) {
        this.sLastName = sLastName;
    }

    public String getFrstName() {
        return sFrstName;
    }

    public void setFrstName(String sFrstName) {
        this.sFrstName = sFrstName;
    }

    public String getMiddName() {
        return sMiddName;
    }

    public void setMiddName(String sMiddName) {
        this.sMiddName = sMiddName;
    }

    public String getSuffixNm() {
        return sSuffixNm;
    }

    public void setSuffixNm(String sSuffixNm) {
        this.sSuffixNm = sSuffixNm;
    }

    public String getMaidenNm() {
        return sMaidenNm;
    }

    public void setMaidenNm(String sMaidenNm) {
        this.sMaidenNm = sMaidenNm;
    }

    public String getGenderCd() {
        return cGenderCd;
    }

    public void setGenderCd(String cGenderCd) {
        this.cGenderCd = cGenderCd;
    }

    public String getBirthDte() {
        return dBirthDte;
    }

    public void setBirthDte(String dBirthDte) {
        this.dBirthDte = dBirthDte;
    }

    public String getBirthPlc() {
        return sBirthPlc;
    }

    public void setBirthPlc(String sBirthPlc) {
        this.sBirthPlc = sBirthPlc;
    }

    public String getHouseNox() {
        return sHouseNox;
    }

    public void setHouseNox(String sHouseNox) {
        this.sHouseNox = sHouseNox;
    }

    public String getAddressx() {
        return sAddressx;
    }

    public void setAddressx(String sAddressx) {
        this.sAddressx = sAddressx;
    }

    public String getTownIDxx() {
        return sTownIDxx;
    }

    public void setTownIDxx(String sTownIDxx) {
        this.sTownIDxx = sTownIDxx;
    }

    public String getMobileNo() {
        return sMobileNo;
    }

    public void setMobileNo(String sMobileNo) {
        this.sMobileNo = sMobileNo;
    }

    public String getEmailAdd() {
        return sEmailAdd;
    }

    public void setEmailAdd(String sEmailAdd) {
        this.sEmailAdd = sEmailAdd;
    }


    public String getsBirthIDx() {
        return sBirthIDx;
    }

    public void setsBirthIDx(String sBirthIDx) {
        this.sBirthIDx = sBirthIDx;
    }

    public String getsMncplNme() {
        return sMncplNme;
    }

    public void setsMncplNme(String sMncplNme) {
        this.sMncplNme = sMncplNme;
    }

    public String getsReltionx() {
        return sReltionx;
    }

    public void setsReltionx(String sReltionx) {
        this.sReltionx = sReltionx;
    }


    public boolean isDataValid(){
        if(sTransNox.isEmpty()){
            message = "Transaction No. is empty.";
            return false;
        }

        if(sLastName.isEmpty()){
            message = "Please enter last name.";
            return false;
        }

        if(sFrstName.isEmpty()){
            message = "Please enter first name.";
        }

        if(cGenderCd.isEmpty()){
            message = "Please select gender";
            return false;
        }

        if(sTownIDxx.isEmpty()){
            message = "Please select town or municipality.";
            return false;
        }

        if(sMobileNo.isEmpty()){
            message = "Please enter mobile number.";
            return false;
        }

        if(sReltionx.isEmpty()){
            message = "Please select relationship.";
            return false;
        }

        return true;
    }
}
