package org.rmj.g3appdriver.GCircle.Apps.CreditApp;

import static org.rmj.g3appdriver.dev.Api.ApiResult.SERVER_NO_RESPONSE;
import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;
import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.apprdiver.util.SQLUtil;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.ApplicationInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.BusinessInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.DependentsInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.FinancierInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.MeansSelectionInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.PensionInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.PersonalInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.PropertiesInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.ResidenceInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.ReviewLoanInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.SpousePensionInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.LoanInfo;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DErrorLogs;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DMC_Contract;
import org.rmj.g3appdriver.GCircle.room.Entities.EErrorLogs;
import org.rmj.g3appdriver.GCircle.room.Entities.EMCContractInfo;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCreditApplication;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DMcModel;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchLoanApplication;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicantInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplication;
import org.rmj.g3appdriver.GCircle.room.Entities.ELoanTerm;
import org.rmj.g3appdriver.GCircle.room.Entities.EMcBrand;
import org.rmj.g3appdriver.GCircle.room.Entities.EMcModel;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.lib.Etc.Branch;
import org.rmj.g3appdriver.GCircle.room.Repositories.RMcBrand;
import org.rmj.g3appdriver.GCircle.room.Repositories.RMcModel;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.CoMakerInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.CoMakerResidenceInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.DisbursementInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.EmploymentInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.OtherInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.SpouseBusinessInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.SpouseEmploymentInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.SpouseInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj.SpouseResidenceInfo;
import org.rmj.gocas.base.GOCASApplication;
import org.rmj.gocas.pricelist.PriceFactory;
import org.rmj.gocas.pricelist.Pricelist;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreditOnlineApplication {
    private static final String TAG = CreditOnlineApplication.class.getSimpleName();

    private final Application instance;

    private final DCreditApplication poDao;
    private final DMC_Contract poMContract;
    private final DErrorLogs poError;

    private final EmployeeMaster poUser;

    private final Branch poBranch;
    private final RMcBrand poBrand;
    private final RMcModel poModel;
    private final Pricelist poPrice;

    private final EmployeeSession poSession;
    private final GCircleApi poApi;
    private final HttpHeaders poHeaders;

    private String message;

    public CreditOnlineApplication(Application instance) {
        this.instance = instance;
        this.poDao = GGC_GCircleDB.getInstance(instance).CreditApplicationDao();
        this.poMContract = GGC_GCircleDB.getInstance(instance).mcontractDao();
        this.poError = GGC_GCircleDB.getInstance(instance).errorLogsDao();
        this.poUser = new EmployeeMaster(instance);
        this.poSession = EmployeeSession.getInstance(instance);
        this.poApi = new GCircleApi(instance);
        this.poHeaders = HttpHeaders.getInstance(instance);
        this.poBranch = new Branch(instance);
        this.poBrand = new RMcBrand(instance);
        this.poModel = new RMcModel(instance);
        this.poPrice = PriceFactory.make(PriceFactory.ProductType.MOTORCYCLE);
    }

    @SuppressLint("SimpleDateFormat")
    private String GetDateToday(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        }
    }

    private String CreateUniqueIDForApplicant(){
        String lsUniqIDx = "";
        try{
            String lsBranchCd = "MX01";
            String lsCrrYear = new SimpleDateFormat("yy", Locale.getDefault()).format(new Date());
            StringBuilder loBuilder = new StringBuilder(lsBranchCd);
            loBuilder.append(lsCrrYear);

            int lnLocalID = poDao.GetRowsCountForUniqueID() + 1;
            String lsPadNumx = String.format("%05d", lnLocalID);
            loBuilder.append(lsPadNumx);
            lsUniqIDx = loBuilder.toString();
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        Log.d(TAG, lsUniqIDx);
        return lsUniqIDx;
    }

    public String getMessage() {
        return message;
    }

    public String CreateContractID(){

        String lsTransNox = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lsTransNox = "MX01" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                    poMContract.CountRecord() + 1;
        }else {
            lsTransNox = "MX01" +
                    new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Calendar.getInstance().getTime()) +
                    poMContract.CountRecord() + 1;
        }

        return lsTransNox;
    }

    public String CreateIDForClient(){

        String lsTransNox = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lsTransNox = "CL" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                    poMContract.CountRecord() + 1;
        }else {
            lsTransNox = "CL" +
                    new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Calendar.getInstance().getTime()) +
                    poMContract.CountRecord() + 1;
        }

        return lsTransNox;
    }

    public String CreateIDForAccountNumber(){

        String lsTransNox = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lsTransNox = "AN" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                    poMContract.CountRecord() + 1;
        }else {
            lsTransNox = "AN" +
                    new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Calendar.getInstance().getTime()) +
                    poMContract.CountRecord() + 1;
        }

        return lsTransNox;
    }

    public String CreateApplication(LoanInfo foVal){
        try{
            if(!foVal.isDataValid()){
                message = foVal.getMessage();
                return null;
            }

            String lsTransNo = CreateUniqueIDForApplicant();

            if(lsTransNo.isEmpty()){
                message = "Failed to generate unique id please restart the app and try again.";
                return null;
            }

            GOCASApplication loGocas = new GOCASApplication();
            loGocas.PurchaseInfo().setAppliedFor(String.valueOf(foVal.getAppTypex()));
            loGocas.PurchaseInfo().setTargetPurchase(foVal.getTargetDte());
            loGocas.PurchaseInfo().setCustomerType(String.valueOf(foVal.getCustTypex()));
            loGocas.PurchaseInfo().setPreferedBranch(foVal.getBranchCde());
            loGocas.PurchaseInfo().setBrandName(foVal.getBrandIDxx());
            loGocas.PurchaseInfo().setModelID(foVal.getModelIDxx());
            loGocas.PurchaseInfo().setDownPayment(foVal.getDownPaymt());
            loGocas.PurchaseInfo().setAccountTerm(foVal.getAccTermxx());
            loGocas.PurchaseInfo().setDateApplied(AppConstants.DATE_MODIFIED());
            loGocas.PurchaseInfo().setMonthlyAmortization(foVal.getMonthlyAm());
            loGocas.PurchaseInfo().setRemarks(foVal.getsRemarks());

            lsTransNo = CreateUniqueIDForApplicant();

            ECreditApplicantInfo loApp = new ECreditApplicantInfo();
            loApp.setTransNox(lsTransNo);
            loApp.setPurchase(loGocas.PurchaseInfo().toJSONString());
            loApp.setBranchCd(foVal.getBranchCde());
            loApp.setAppliedx(loGocas.PurchaseInfo().getAppliedFor());
            loApp.setDownPaym(loGocas.PurchaseInfo().getDownPayment());
            loApp.setCreatedx(AppConstants.DATE_MODIFIED());
            loApp.setTransact(AppConstants.CURRENT_DATE());
            loApp.setTranStat("0");
            loApp.setRemarksx(loGocas.PurchaseInfo().getRemarks());

            poDao.Save(loApp);

            Log.d(TAG, "New credit online application has been created.");
            return lsTransNo;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> GetUserInfo(){
        return poUser.GetEmployeeBranch();
    }

    public LiveData<List<ELoanTerm>> GetLoanTerms(){
        return poDao.GetLoanTerms();
    }

    public LiveData<List<DCreditApplication.ApplicationLog>> GetCreditApplications(){
        return poDao.getApplicationHistory();
    }

    public LiveData<List<EBranchLoanApplication>> GetBranchApplications(){
        return poDao.GetBranchApplications();
    }

    public LiveData<List<EBranchInfo>> getAllBranchInfo(){
        return poBranch.getAllMcBranchInfo();
    }

    public LiveData<List<EMcBrand>> getAllMcBrand(){
        return poBrand.getAllBrandInfo();
    }

    public LiveData<List<EMcModel>> getAllBrandModelInfo(String args){
        return poModel.getMcModelFromBrand(args);
    }

    public List<MCSerial> DownloadSerials(String fsSearch){

        try {
            JSONObject params = new JSONObject();
            params.put("sSerial", fsSearch);

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlImportMcSerials(),
                    params.toString(),
                    poHeaders.getHeaders());

            if (lsResponse == null) {
                message = SERVER_NO_RESPONSE;
                return null;
            }
            Log.d(TAG, lsResponse);

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if (lsResult.equalsIgnoreCase("error")) {
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return null;
            }

            List<MCSerial> laSerials = new ArrayList<>();
            JSONArray laJson = loResponse.getJSONArray("detail");
            for (int x = 0; x < laJson.length(); x++) {

                JSONObject loResult = laJson.getJSONObject(x);

                MCSerial loSerial = new MCSerial(
                        loResult.getString("sSerialID"),
                        loResult.getString("sEngineNo"),
                        loResult.getString("sFrameNox"),
                        loResult.getString("sModelIDx"),
                        loResult.getString("sDesc")
                );
                laSerials.add(loSerial);
            }
            return laSerials;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public EMCContractInfo GetCreditContract(String fsTransNox){
        return poMContract.GetCreditContract(fsTransNox);
    }

    public CreditApp getInstance(CreditAppInstance app){
        switch (app){
            case Client_Info:
                return new PersonalInfo(instance);
            case Residence_Info:
                return new ResidenceInfo(instance);
            case Employed_Info:
                return new EmploymentInfo(instance);
            case Self_Employed_Info:
                return new BusinessInfo(instance);
            case Financier_Info:
                return new FinancierInfo(instance);
            case Pension_Info:
                return new PensionInfo(instance);
            case Spouse_Info:
                return new SpouseInfo(instance);
            case Spouse_Residence_Info:
                return new SpouseResidenceInfo(instance);
            case Spouse_Employment_Info:
                return new SpouseEmploymentInfo(instance);
            case Spouse_Self_Employed_Info:
                return new SpouseBusinessInfo(instance);
            case Spouse_Pension_Info:
                return new SpousePensionInfo(instance);
            case Disbursement_Info:
                return new DisbursementInfo(instance);
            case Dependent_Info:
                return new DependentsInfo(instance);
            case Properties_Info:
                return new PropertiesInfo(instance);
            case Other_Info:
                return new OtherInfo(instance);
            case CoMaker_Info:
                return new CoMakerInfo(instance);
            case CoMaker_Residence_Info:
                return new CoMakerResidenceInfo(instance);
            case Application_Info:
                return new ApplicationInfo(instance);
            case Means_Info:
                return new MeansSelectionInfo(instance);
            case ReviewLoanInfo:
                return new ReviewLoanInfo(instance);
            default:
                return null;
        }
    }

    public ECreditApplication GetApplication(String fsTransNox){
        return poDao.GetCreditOnlineApplication(fsTransNox);
    }

    public EBranchInfo getBranchInfoNonLive(String fsBranchCd){
        return poBranch.getBranchInfoNonLive(fsBranchCd);
    }

    public void SaveError(String source, String message){

        EErrorLogs logs = new EErrorLogs();
        logs.setnErrorLogID(poError.GetErrorLogCount() + 1);
        logs.setsMessagex(message);
        logs.setdLogDate(GetDateToday());
        logs.setsSourceTransNo(source);
        logs.setcRead("0");

        poError.SaveErrorLogs(logs);
    }

    public boolean DownloadApplications(){
        try{
            JSONObject params = new JSONObject();
            params.put("bycode", true);
            params.put("sUserIDxx", poSession.getUserID());

            ECreditApplication loApp = poDao.GetLatestRecord();

            if(loApp != null){
                params.put("sUserIDxx", loApp.getTimeStmp());
            }

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlImportOnlineApplications(),
                    params.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                return false;
            }
            Log.d(TAG, lsResponse);

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;
            }

            JSONArray laJson = loResponse.getJSONArray("detail");
            for(int x = 0; x < laJson.length(); x++){
                JSONObject loJson = laJson.getJSONObject(x);

                ECreditApplication loDetail = poDao.GetCreditOnlineApplication(loJson.getString("sTransNox"));

                if(loDetail == null) {
                    ECreditApplication info = new ECreditApplication();
                    info.setTransNox(loJson.getString("sTransNox"));
                    info.setBranchCd(loJson.getString("sBranchCd"));
                    info.setTransact(loJson.getString("dTransact"));
                    info.setClientNm(loJson.getString("sClientNm"));
                    info.setGOCASNox(loJson.getString("sGOCASNox"));
                    //TODO : Unit Applied is default to 0 cuz empty String is return from Server upon request
                    // this must be update when credit is available in for mobile phone
                    info.setUnitAppl("0");
                    info.setSourceCD(loJson.getString("sSourceCD"));
                    info.setDetlInfo(loJson.getString("sDetlInfo"));
                    info.setQMatchNo(loJson.getString("sQMatchNo"));
                    info.setWithCIxx(loJson.getString("cWithCIxx"));
                    info.setDownPaym(Double.parseDouble(loJson.getString("nDownPaym")));
                    info.setRemarksx(loJson.getString("sRemarksx"));
                    info.setCreatedx(loJson.getString("sCreatedx"));
                    info.setDateCreatedx(loJson.getString("dCreatedx"));
                    info.setSendStat("1");
                    info.setVerified(loJson.getString("sVerified"));
                    info.setDateVerified(loJson.getString("dVerified"));
                    info.setTranStat(loJson.getString("cTranStat"));
                    info.setDivision(loJson.getString("cDivision"));
                    info.setReceived(loJson.getString("dReceived"));
                    info.setTimeStmp(loJson.getString("dTimeStmp"));
                    poDao.Save(info);
                } else {
                    Date ldDate1 = SQLUtil.toDate(loDetail.getTimeStmp(), SQLUtil.FORMAT_TIMESTAMP);
                    Date ldDate2 = SQLUtil.toDate((String) loJson.get("dTimeStmp"), SQLUtil.FORMAT_TIMESTAMP);

                    if(!ldDate1.equals(ldDate2)){
                        loDetail.setTransNox(loJson.getString("sTransNox"));
                        loDetail.setBranchCd(loJson.getString("sBranchCd"));
                        loDetail.setTransact(loJson.getString("dTransact"));
                        loDetail.setClientNm(loJson.getString("sClientNm"));
                        loDetail.setGOCASNox(loJson.getString("sGOCASNox"));
                        //TODO : Unit Applied is default to 0 cuz empty String is return from Server upon request
                        // this must be update when credit is available in for mobile phone
                        loDetail.setUnitAppl("0");
                        loDetail.setSourceCD(loJson.getString("sSourceCD"));
                        loDetail.setDetlInfo(loJson.getString("sDetlInfo"));
                        loDetail.setQMatchNo(loJson.getString("sQMatchNo"));
                        loDetail.setWithCIxx(loJson.getString("cWithCIxx"));
                        loDetail.setDownPaym(Double.parseDouble(loJson.getString("nDownPaym")));
                        loDetail.setRemarksx(loJson.getString("sRemarksx"));
                        loDetail.setCreatedx(loJson.getString("sCreatedx"));
                        loDetail.setDateCreatedx(loJson.getString("dCreatedx"));
                        loDetail.setSendStat("1");
                        loDetail.setVerified(loJson.getString("sVerified"));
                        loDetail.setDateVerified(loJson.getString("dVerified"));
                        loDetail.setTranStat(loJson.getString("cTranStat"));
                        loDetail.setDivision(loJson.getString("cDivision"));
                        loDetail.setReceived(loJson.getString("dReceived"));
                        loDetail.setTimeStmp(loJson.getString("dTimeStmp"));
                    }
                }
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean ImportInstallmentTerms(){
        try{
            String lsResponse = WebClient.sendRequest(
                    poApi.getGetInstallmentTerms(),
                    new JSONObject().toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                return false;
            }

            Log.d(TAG, lsResponse);
            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;
            }

            JSONArray laJson = loResponse.getJSONArray("detail");
            for(int x = 0; x < laJson.length(); x++){
                JSONObject loJson = laJson.getJSONObject(x);

                int lnTermCde = loJson.getInt("nTermCode");
                ELoanTerm loDetail = poDao.GetLoanTerm(lnTermCde);

                if(loDetail == null){
                    ELoanTerm loTerm = new ELoanTerm();
                    loTerm.setTermCode(lnTermCde);
                    loTerm.setTermDesc(loJson.getString("sTermDesc"));
                    loTerm.setRecdStat(loJson.getString("cRecdStat"));
                    loTerm.setTimeStmp(loJson.getString("dTimeStmp"));
                    poDao.Save(loTerm);
                } else {
                    Date ldDate1 = SQLUtil.toDate(loDetail.getTimeStmp(), SQLUtil.FORMAT_TIMESTAMP);
                    Date ldDate2 = SQLUtil.toDate((String) loJson.getString("dTimeStmp"), SQLUtil.FORMAT_TIMESTAMP);
                    if (!ldDate1.equals(ldDate2)) {
                        loDetail.setTermCode(lnTermCde);
                        loDetail.setTermDesc(loJson.getString("sTermDesc"));
                        loDetail.setRecdStat(loJson.getString("cRecdStat"));
                        loDetail.setTimeStmp(loJson.getString("dTimeStmp"));
                        poDao.Update(loDetail);
                    }
                }
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean DownloadBranchApplications(){
        try{
            JSONObject params = new JSONObject();
            params.put("bycode", true);
            params.put("value", poSession.getBranchCode());

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlBranchLoanApp(),
                    params.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if (lsResult.equalsIgnoreCase("error")) {
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;
            }

            JSONArray laJson = loResponse.getJSONArray("detail");

            for(int x= 0; x < laJson.length(); x++){
                JSONObject loJson = laJson.getJSONObject(x);

                EBranchLoanApplication loDetail = poDao.GetBranchApplication(loJson.getString("sTransNox"));

                if(loDetail == null){
                    EBranchLoanApplication loanInfo = new EBranchLoanApplication();
                    loanInfo.setTransNox(loJson.getString("sTransNox"));
                    loanInfo.setBranchCD(loJson.getString("sBranchCd"));
                    loanInfo.setTransact(loJson.getString("dTransact"));
                    loanInfo.setCredInvx(loJson.getString("sCredInvx"));
                    loanInfo.setCompnyNm(loJson.getString("sCompnyNm"));
                    loanInfo.setSpouseNm(loJson.getString("sSpouseNm"));
                    loanInfo.setAddressx(loJson.getString("sAddressx"));
                    loanInfo.setMobileNo(loJson.getString("sMobileNo"));
                    loanInfo.setQMAppCde(loJson.getString("sQMAppCde"));
                    loanInfo.setModelNme(loJson.getString("sModelNme"));
                    loanInfo.setDownPaym(loJson.getString("nDownPaym"));
                    loanInfo.setAcctTerm(loJson.getString("nAcctTerm"));
                    loanInfo.setCreatedX(loJson.getString("sCreatedx"));
                    loanInfo.setTranStat(loJson.getString("cTranStat"));
                    loanInfo.setTimeStmp(loJson.getString("dTimeStmp"));
                    poDao.Save(loanInfo);
                    Log.d(TAG, "Branch loan application has been saved.");
                } else {
                    loDetail.setTransNox(loJson.getString("sTransNox"));
                    loDetail.setBranchCD(loJson.getString("sBranchCd"));
                    loDetail.setTransact(loJson.getString("dTransact"));
                    loDetail.setCredInvx(loJson.getString("sCredInvx"));
                    loDetail.setCompnyNm(loJson.getString("sCompnyNm"));
                    loDetail.setSpouseNm(loJson.getString("sSpouseNm"));
                    loDetail.setAddressx(loJson.getString("sAddressx"));
                    loDetail.setMobileNo(loJson.getString("sMobileNo"));
                    loDetail.setQMAppCde(loJson.getString("sQMAppCde"));
                    loDetail.setModelNme(loJson.getString("sModelNme"));
                    loDetail.setDownPaym(loJson.getString("nDownPaym"));
                    loDetail.setAcctTerm(loJson.getString("nAcctTerm"));
                    loDetail.setCreatedX(loJson.getString("sCreatedx"));
                    loDetail.setTranStat(loJson.getString("cTranStat"));
                    loDetail.setTimeStmp(loJson.getString("dTimeStmp"));
                    poDao.Update(loDetail);
                    Log.d(TAG, "Branch loan application has been updated.");
                }
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean DownloadMContract(String fsReferNoxx){

        try {

            //initialize parameter
            JSONObject params = new JSONObject();
            params.put("sReferNoxx", fsReferNoxx);

            //upload to database
            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlImportMContract(),
                    params.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;
            }
            poMContract.DeleteMContractInfo(fsReferNoxx);

            JSONArray laJson = loResponse.getJSONArray("detail");

            for(int x= 0; x < laJson.length(); x++){

                JSONObject loJson = laJson.getJSONObject(x);

                EMCContractInfo loDetail = new EMCContractInfo();
                loDetail.setsTransNox(loJson.getString("sTransNox"));
                loDetail.setsBranchCd(loJson.getString("sBranchCD"));
                loDetail.setsClientID(loJson.getString("sClientID"));
                loDetail.setsReferNox(loJson.getString("sReferNox"));
                loDetail.setsAcctNmbr(loJson.getString("sAcctNmbr"));
                loDetail.setsSerialID(loJson.getString("sSerialID"));
                loDetail.setdTransact(loJson.getString("dTransact"));
                loDetail.setnAcctTerm(Integer.parseInt(loJson.getString("nAcctTerm")));
                loDetail.setnDownPaym(Double.parseDouble(loJson.getString("nDownPayM")));
                loDetail.setnMonAmort(Double.parseDouble(loJson.getString("nMonAmort")));
                loDetail.setnRebatesx(Double.parseDouble(loJson.getString("nRebatesx")));
                loDetail.setnPenaltyx(Double.parseDouble(loJson.getString("nPenaltyX")));
                loDetail.setdFirstPay(loJson.getString("dFirstPay"));
                loDetail.setsRemarksx(loJson.getString("sRemarksX"));
                loDetail.setcTranStat(loJson.getString("cTranStat"));
                loDetail.setsSendStat("1");

                poMContract.Save(loDetail);
            }

            return true;
        } catch (Exception e){
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean UploadApplication(String args){
        try {
            ECreditApplication loApp = poDao.GetCreditOnlineApplication(args);

            if(loApp == null){
                message = "Unable to find application to upload.";
                return false;
            }

            JSONObject params = new JSONObject(loApp.getDetlInfo());
            params.put("dCreatedx", loApp.getDateCreatedx());

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlSubmitOnlineApplication(),
                    params.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;
            }

            String lsTransNox = loResponse.getString("sTransNox");
            poDao.updateSentLoanAppl(loApp.getTransNox(), lsTransNox, new AppConstants().DATE_MODIFIED);

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean UploadApplications(){
        try{
            List<ECreditApplication> loApps = poDao.GetApplicationsForUpload();

            if(loApps == null){
                message = "No credit online application to upload.";
                return false;
            }

            if(loApps.size() == 0){
                message = "No credit online application to upload.";
                return false;
            }

            for(int x = 0; x < loApps.size(); x++){
                ECreditApplication loApp = loApps.get(x);

                JSONObject params = new JSONObject(loApp.getDetlInfo());
                params.put("dCreatedx", loApp.getCreatedx());

                String lsResponse = WebClient.sendRequest(
                        poApi.getUrlSubmitOnlineApplication(),
                        params.toString(),
                        poHeaders.getHeaders());

                if(lsResponse == null){
                    message = SERVER_NO_RESPONSE;
                    Log.e(TAG, message);
                    Thread.sleep(1000);
                    continue;
                }

                JSONObject loResponse = new JSONObject(lsResponse);
                String lsResult = loResponse.getString("result");
                if(lsResult.equalsIgnoreCase("error")){
                    JSONObject loError = loResponse.getJSONObject("error");
                    message = getErrorMessage(loError);
                    Log.e(TAG, message);
                    Thread.sleep(1000);
                    continue;
                }

                String lsTransNox = loResponse.getString("sTransNox");
                poDao.updateSentLoanAppl(loApp.getTransNox(), lsTransNox, new AppConstants().DATE_MODIFIED);
                Log.d(TAG, "Credit online application uploaded successfully.");
                Thread.sleep(1000);
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean UploadMContract(EMCContractInfo foVal){

        try {

            //initialize date transact (considered new record) if not exist
            if (poMContract.GetMContractInfo(foVal.getsTransNox()) == null){
                foVal.setdTransact(GetDateToday());
            }

            //save or update first to local
            poMContract.Save(foVal);

            //initialize parameter
            JSONObject params = new JSONObject();
            params.put("sBranchCd", foVal.getsBranchCd());
            params.put("dTransact", GetDateToday());
            params.put("sClientID", foVal.getsClientID());
            params.put("sReferNox", foVal.getsReferNox());
            params.put("sAcctNmbr", foVal.getsAcctNmbr());
            params.put("sSerialID", foVal.getsSerialID());
            params.put("nDownPaym", foVal.getnDownPaym());
            params.put("nAcctTerm", foVal.getnAcctTerm());
            params.put("nMonAmort", foVal.getnMonAmort());
            params.put("sRemarksx", foVal.getsRemarksx());
            params.put("cTranStat", foVal.getcTranStat());

            //upload to database
            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlSubmitMContract(),
                    params.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;
            }

            poMContract.DeleteMContractInfo(foVal.getsReferNox());

            return true;
        } catch (Exception e){
            message = getLocalMessage(e);
            return false;
        }
    }

    public static class MCSerial{

        public String lsSerialID;
        public String lsEngine;
        public String lsFrame;
        public String lsModelIDx;
        public String lsDescr;

        public MCSerial(String lsSerialID, String lsEngine, String lsFrame, String lsModelIDx, String lsDescr){
            this.lsSerialID = lsSerialID;
            this.lsEngine = lsEngine;
            this.lsFrame = lsFrame;
            this.lsModelIDx = lsModelIDx;
            this.lsDescr = lsDescr;
        }
    }
}
