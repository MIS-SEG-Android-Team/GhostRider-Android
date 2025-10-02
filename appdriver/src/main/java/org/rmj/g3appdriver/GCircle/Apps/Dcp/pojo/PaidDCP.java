package org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo;

public class PaidDCP {

    private String TransNo;
    private String EntryNo;
    private String PrNoxxx;
    private String Payment = "";
    private String Remarks = "";
    private double Amountx = 0.00;
    private double Dscount = 0.00;
    private double Othersx = 0.00;
    private double TotAmnt = 0.00;

    private String BankNme;
    private String CheckDt;
    private String CheckNo;
    private String AccntNo;

    private String TransNox, EntryNox, FileName, FilePath, latitude, longtude;

    private String message;

    public PaidDCP() {
    }

    public String getMessage(){
        return message;
    }

    public String getTransNo() {
        return TransNo;
    }

    public void setTransNo(String transNo) {
        TransNo = transNo;
    }

    public String getEntryNo() {
        return EntryNo;
    }

    public void setEntryNo(String entryNo) {
        EntryNo = entryNo;
    }

    public String getPrNoxxx() {
        return PrNoxxx;
    }

    public void setPrNoxxx(String prNoxxx) {
        PrNoxxx = prNoxxx;
    }

    public String getPayment() {
        return Payment;
    }

    public void setPayment(String payment) {
        Payment = payment;
    }

    public String getRemarks() {
        return Remarks.trim();
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public double getAmountx() {
        return Amountx;
    }

    public void setAmountx(double amountx) {
        Amountx = amountx;
    }

    public String getBankNme() {
        return BankNme;
    }

    public void setBankNme(String bankNme) {
        BankNme = bankNme;
    }

    public String getCheckDt() {
        return CheckDt;
    }

    public void setCheckDt(String checkDt) {
        CheckDt = checkDt;
    }

    public String getCheckNo() {
        return CheckNo;
    }

    public void setCheckNo(String checkNo) {
        CheckNo = checkNo;
    }

    public String getAccntNo() {
        return AccntNo;
    }

    public void setAccntNo(String accntNo) {
        AccntNo = accntNo;
    }

    public double getDscount() {
        return Dscount;
    }

    public void setDscount(double dscount) {
        Dscount = dscount;
    }

    public double getOthersx() {
        return Othersx;
    }

    public void setOthersx(double othersx) {
        Othersx = othersx;
    }

    public double getTotAmnt() {
        return TotAmnt;
    }

    public void setTotAmnt(double totAmnt) {
        TotAmnt = totAmnt;
    }

    public String getTransNox() {
        return TransNox;
    }

    public void setTransNox(String transNox) {
        TransNox = transNox;
    }

    public String getEntryNox() {
        return EntryNox;
    }

    public void setEntryNox(String entryNox) {
        EntryNox = entryNox;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtude() {
        return longtude;
    }

    public void setLongtude(String longtude) {
        this.longtude = longtude;
    }

    public boolean isDataValid(){
        return isPaymentTypeValid() && isPRNumberValid() && isAmountValid() && isRemarksValid();
    }

    private boolean isPaymentTypeValid(){
        if(Payment.trim().isEmpty()){
            message = "Please select payment type";
            return false;
        }
        return true;
    }

    private boolean isPRNumberValid(){
        if(PrNoxxx.trim().isEmpty()){
            message = "Please enter P.R number";
            return false;
        }
        return true;
    }

    private boolean isAmountValid(){
        if(Amountx == 0.00){
            message = "Please enter amount";
            return false;
        }
        return true;
    }
    //                        Added by Jonathan 07/27/2021
    private boolean isRemarksValid(){
        if(Remarks.trim().isEmpty()){
            message = "Please enter remarks";
            return false;
        }
        return true;
    }
}
