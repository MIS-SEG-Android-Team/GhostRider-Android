package org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo;

public class Remittance {
    private String sTransNox;
    private String cPaymForm = "";
    private String cRemitTyp = "";
    private String sCompnyNm = "";
    private String sBankAcct = "";
    private String sReferNox = "";
    private double nCollectd = 0.00;
    private double nAmountxx = 0.00;

    private String message;

    public Remittance() {
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

    public String getcPaymForm() {
        return cPaymForm;
    }

    public void setcPaymForm(String cPaymForm) {
        this.cPaymForm = cPaymForm;
    }

    public String getcRemitTyp() {
        return cRemitTyp;
    }

    public void setcRemitTyp(String cRemitTyp) {
        this.cRemitTyp = cRemitTyp;
    }

    public String getsCompnyNm() {
        return sCompnyNm;
    }

    public void setsCompnyNm(String sCompnyNm) {
        this.sCompnyNm = sCompnyNm;
    }

    public String getsBankAcct() {
        return sBankAcct;
    }

    public void setsBankAcct(String sBankAcct) {
        this.sBankAcct = sBankAcct;
    }

    public String getsReferNox() {
        return sReferNox;
    }

    public void setsReferNox(String sReferNox) {
        this.sReferNox = sReferNox;
    }

    public double getnAmountxx() {
        return nAmountxx;
    }

    public void setnAmountxx(double nAmountxx) {
        this.nAmountxx = nAmountxx;
    }

    public double getnCollectd() {
        return nCollectd;
    }

    public void setnCollectd(double nCollectd) {
        this.nCollectd = nCollectd;
    }

    public boolean isDataValid(){
        if(nAmountxx < 0){
            message = "Invalid remittance amount.";
            return false;
        }

        if(nAmountxx > nCollectd){
            message = "Unable to remit amount more than the collected amount";
            return false;
        }

        switch (cRemitTyp){
            case "0":
                if(sCompnyNm.trim().isEmpty()){
                    message = "Please select branch.";
                    return false;
                }
                break;
            case "1":
            case "2":
                if(sCompnyNm.trim().isEmpty()){
                    message = "Please select branch.";
                    return false;
                } else if(sBankAcct.trim().isEmpty()){
                    message = "Please select bank or payment partners for remittance.";
                    return false;
                } else if(sReferNox.trim().isEmpty()){
                    message = "Please enter reference no.";
                    return false;
                }
            break;
        }
        return true;
    }
}
