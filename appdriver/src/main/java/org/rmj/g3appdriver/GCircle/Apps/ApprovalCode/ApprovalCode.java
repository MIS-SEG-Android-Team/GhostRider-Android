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

package org.rmj.g3appdriver.GCircle.Apps.ApprovalCode;


import static org.rmj.g3appdriver.dev.Api.ApiResult.SERVER_NO_RESPONSE;
import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;
import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.apprdiver.util.MiscUtil;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCASApprovalCode;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCASRequests;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DSCARqstEmp;
import org.rmj.g3appdriver.GCircle.room.Entities.ECASApprovalCode;
import org.rmj.g3appdriver.GCircle.room.Entities.ECASRequests;
import org.rmj.g3appdriver.GCircle.room.Entities.ESCARqstEmp;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DApprovalCode;
import org.rmj.g3appdriver.GCircle.room.Entities.ECodeApproval;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ESCA_Request;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.Obj.LoanApproval;
import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.Obj.ManualLog;
import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.Obj.SystemCode;
import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.model.SCA;
import org.rmj.g3appdriver.utils.SQLUtil;

import java.util.List;

public class ApprovalCode {
    private static final String TAG = ApprovalCode.class.getSimpleName();

    private final DApprovalCode poDao;
    private final DCASApprovalCode poCASDao;
    private final DCASRequests poCASReqDao;
    private final DSCARqstEmp poDaoRstEmp;


    public final EmployeeMaster poMaster;
    private final Application instance;
    private final GCircleApi poApi;
    private final HttpHeaders poHeaders;

    private String message;

    public ApprovalCode(Application instance) {
        this.instance = instance;
        this.poDao = GGC_GCircleDB.getInstance(instance).ApprovalDao();
        this.poCASDao = GGC_GCircleDB.getInstance(instance).casapprovalDao();
        this.poCASReqDao = GGC_GCircleDB.getInstance(instance).casrequestsDao();
        this.poDaoRstEmp = GGC_GCircleDB.getInstance(instance).scaRqstEmpDao();
        this.poMaster = new EmployeeMaster(instance);
        this.poApi = new GCircleApi(instance);
        this.poHeaders = HttpHeaders.getInstance(instance);
    }

    public String getMessage() {
        return message;
    }

    public SCA getInstance(eSCA args){
        switch (args){
            case MANUAL_LOG:
                return new ManualLog(instance);
            case SYSTEM_CODE:
                return new SystemCode(instance);
            case CREDIT_APP:
                return new LoanApproval(instance);
        }
        return null;
    }

    public boolean ImportSCARequest(){
        try{
            JSONObject params = new JSONObject();
            params.put("descript", "all");
            params.put("timestamp", "");
            //params.put("bsearch", true);

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlScaRequest(),
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
                message = getErrorMessage(loError);;
                return false;
            }

            JSONArray laJson = loResponse.getJSONArray("detail");

            poDao.clear();
            for (int x = 0; x < laJson.length(); x++) {

                JSONObject loJson = laJson.getJSONObject(x);
                ESCA_Request loDetail = poDao.GetApprovalCode(loJson.getString("sSCACodex"));

                if(loDetail == null){
                    if(loJson.getString("cRecdStat").equalsIgnoreCase("1")) {

                        ESCA_Request info = new ESCA_Request();

                        info.setSCACodex(loJson.getString("sSCACodex"));
                        info.setSCATitle(loJson.getString("sSCATitle"));
                        info.setSCADescx(loJson.getString("sSCADescx"));
                        info.setSCATypex(loJson.getString("cSCATypex"));
                        info.setAreaHead(loJson.getString("cAreaHead"));
                        info.setHCMDeptx(loJson.getString("cHCMDeptx"));
                        info.setCSSDeptx(loJson.getString("cCSSDeptx"));
                        info.setComplnce(loJson.getString("cComplnce"));
                        info.setMktgDept(loJson.getString("cMktgDept"));
                        info.setASMDeptx(loJson.getString("cASMDeptx"));
                        info.setTLMDeptx(loJson.getString("cTLMDeptx"));
                        info.setSCMDeptx(loJson.getString("cSCMDeptx"));
                        info.setRecdStat(loJson.getString("cRecdStat"));
                        info.setTimeStmp(loJson.getString("dTimeStmp"));

                        poDao.SaveSCARequest(info);
                        Log.d(TAG, "SCA Request has been saved.");
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

    public boolean ImportSCARequestEmp(String timestamp){
        try {
            JSONObject params = new JSONObject();
            params.put("timestamp", timestamp);

            String lsResponse = WebClient.sendRequest(poApi.getDownloadSCARqstEmp(), params.toString(), poHeaders.getHeaders());
            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");

            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);;
                return false;
            }else {

                JSONArray laJson = loResponse.getJSONArray("detail");

                poDaoRstEmp.clear();
                for (int x = 0; x < laJson.length(); x++) {
                    JSONObject loObject = laJson.getJSONObject(x);
                    ESCARqstEmp loVal = new ESCARqstEmp();

                    loVal.setsSCACodex(loObject.get("sSCACodex").toString());
                    loVal.setsEmployIDx(loObject.get("sEmployID").toString());
                    loVal.setsRecdStat(loObject.get("cRecdStat").toString());
                    loVal.setdTimeStmpx(loObject.get("dTimeStmp").toString());

                    poDaoRstEmp.SaveRqstEmp(loVal);
                }
            }

            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean UploadApprovalCode(){
        try{
            List<ECodeApproval> loApp = poDao.GetSystemApprovalForUploading();
            if(loApp == null){
                message = "No approval code record found.";
                return false;
            }

            if(loApp.size() == 0){
                message = "No approval code record found.";
                return false;
            }

            for(int x = 0; x < loApp.size(); x++){
                ECodeApproval loCode = loApp.get(x);

                JSONObject param = new JSONObject();
                param.put("sTransNox", loCode.getTransNox());
                param.put("dTransact", loCode.getTransact());
                param.put("sSystemCD", loCode.getSystemCD());
                param.put("sReqstdBy", loCode.getReqstdBy());
                param.put("dReqstdxx", loCode.getReqstdxx());
                param.put("cIssuedBy", loCode.getIssuedBy());
                param.put("sMiscInfo", loCode.getMiscInfo());
                param.put("sRemarks1", loCode.getRemarks1());
                param.put("sRemarks2", loCode.getApprCode() == null ? "" : loCode.getRemarks2());
                param.put("sApprCode", loCode.getApprCode());
                param.put("sEntryByx", loCode.getEntryByx());
                param.put("sApprvByx", loCode.getApprvByx());
                param.put("sReasonxx", loCode.getReasonxx() == null ? "" : loCode.getReasonxx());
                param.put("sReqstdTo", loCode.getReqstdTo() == null ? "" : loCode.getReqstdTo());
                param.put("cTranStat", loCode.getTranStat());

                String lsResponse = WebClient.sendRequest(
                        poApi.getUrlSaveApproval(),
                        param.toString(),
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
                    message = getErrorMessage(loError);;
                    Log.e(TAG, message);
                    Thread.sleep(1000);
                    continue;
                }

                String lsTransNo = loResponse.getString("sTransNox");
                poDao.UpdateUploaded(loCode.getTransNox(), lsTransNo);
                Log.d(TAG, "System code approval has been save to server.");
                Thread.sleep(1000);
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean ImportCASTransactions(){
        try{
            JSONObject params = new JSONObject();

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlCasTransactions(),
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
                message = getErrorMessage(loError);;
                return false;
            }

            JSONArray laDetail = loResponse.getJSONArray("detail");
            for (int i = 0; i < laDetail.length(); i++) {

                JSONObject loResult = laDetail.getJSONObject(i);

                ECASApprovalCode loCASCode = new ECASApprovalCode();
                loCASCode.setsSourceCD(loResult.getString("sSourceCD"));
                loCASCode.setsDescript(loResult.getString("sDescript"));

                poCASDao.SaveCASApprovalCode(loCASCode);
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean ImportCASRequests(String fsEmployID, String fsSrcCd){
        try{
            JSONObject params = new JSONObject();
            params.put("sEmployID", fsEmployID);
            params.put("sSourceCD", fsSrcCd);

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlCasRequests(),
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
                message = getErrorMessage(loError);;
                return false;
            }

            JSONArray laDetail = loResponse.getJSONArray("detail");
            for (int i = 0; i < laDetail.length(); i++) {

                JSONObject loResult = laDetail.getJSONObject(i);

                ECASRequests loRequests = new ECASRequests();
                loRequests.setsTransNox(loResult.getString("sTransNox"));
                loRequests.setdTransact(loResult.getString("dTransact"));
                loRequests.setsSourceCD(loResult.getString("sSourceCD"));
                loRequests.setsSourceNo(loResult.getString("sSourceNo"));
                loRequests.setsAuthType(loResult.getString("sAuthType"));
                loRequests.setsDescript(loResult.getString("sDescript"));
                loRequests.setsCompnyNm(loResult.getString("sCompnyNm"));
                loRequests.setsRemarksx(loResult.getString("sRemarksx"));
                loRequests.setcTranStat(loResult.getString("cTranStat"));
                loRequests.setdApproved(loResult.getString("dApproved"));
                loRequests.setsAppSrcNo(loResult.getString("sAppSrcNo"));

                poCASReqDao.SaveCASRequest(loRequests);
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean VerifyUserMatrix(String fsAuthType, String fsEmployID){
        try{
            JSONObject params = new JSONObject();
            params.put("sEmployID", fsEmployID);
            params.put("sAuthType", fsAuthType);

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlCasMatrix(),
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
                message = getErrorMessage(loError);;
                return false;
            }
            message = "User verified successfully";

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean UpdateCASRequest(String fsTransNox, String fsEmployID, String fscTranStat){
        try{
            JSONObject params = new JSONObject();
            params.put("sEmployID", fsEmployID);
            params.put("sTransNox", fsTransNox);
            params.put("cTranStat", fscTranStat);

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlCasApproval(),
                    params.toString(),
                    poHeaders.getHeaders());

            System.out.println(lsResponse);
            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");

            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);;
                return false;
            }

            poCASReqDao.UpdateRequest(loResponse.getString("sTransNox"), loResponse.getString("cTranStat"), loResponse.getString("dApproved"), loResponse.getString("sEmployID"));

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public LiveData<List<ESCA_Request>> getAuthorizedFeatures(String fsVal){

        EEmployeeInfo loUser = poDao.GetUserInfo();

        String lsSqlQryxx = "SELECT *" +
                " FROM xxxSCA_Request" +
                " WHERE cSCATypex = " + SQLUtil.toSQL(fsVal)  +
                " AND cRecdStat = '1'" +
                " ORDER BY sSCATitle";

        String lsCondition = "";
        int lsEmpLvID = loUser.getEmpLevID();
        String lsDeptIDx = loUser.getDeptIDxx();
        String lsPostion = loUser.getPositnID();

        /*if (lsEmpLvID == 4 ||
                lsEmpLvID == 5 ||
                loUser.getEmployID().equalsIgnoreCase("M00112000440")){
            lsCondition  = "cAreaHead = '1'";*/

        //todo: replaced the old validation above as requested
        if (lsEmpLvID == 4 || loUser.getEmployID().equalsIgnoreCase("M00105000084")) {
            lsCondition  = "cAreaHead = '1'"; //AREA HEAD, LEXTER OCAMPO
        } else if (lsEmpLvID == 5 || loUser.getEmployID().equalsIgnoreCase("H00220000001")
                || loUser.getEmployID().equalsIgnoreCase("M00111005387")) {
            lsCondition  = "cAreaHead = '1'"; //GENERAL MANAGER, Tania Vanessa Cañete, MICHAEL CUISON
        } else{
            switch (lsDeptIDx){
                case "021": //hcm
                    lsCondition = "cHCMDeptx = '1'"; break;
                case "022": //css
                    lsCondition = "cCSSDeptx = '1'"; break;
                case "034": //cm
                    lsCondition = "cComplnce = '1'"; break;
                case "025": //m&p
                    lsCondition = "cMktgDept = '1'"; break;
                case "027": //asm
                    lsCondition = "cASMDeptx = '1'"; break;
                case "035": //tele
                    lsCondition = "cTLMDeptx = '1'"; break;
                case "024": //scm
                    lsCondition = "cSCMDeptx = '1'"; break;
                case "026": //mis

                    break;
                case "015": //sales
                    if (lsPostion.equals("091") ||
                            lsPostion.equals("056") ||
                            lsPostion.equals("299") ||
                            lsPostion.equals("298")){
                        //field specialist
                        lsCondition = "sSCACodex = 'CA'";
                    } else {
                        lsCondition = "0=1";
                    }
                    break;
                default: lsCondition = "0=1";
            }
        }

        if (!lsCondition.isEmpty()){
            lsSqlQryxx = MiscUtil.addCondition(lsSqlQryxx, lsCondition);
        }

        Log.d(TAG, lsSqlQryxx);

        return poDao.getAuthorizedFeatures(new SimpleSQLiteQuery(lsSqlQryxx));
    }

    public LiveData<List<ECASApprovalCode>> GetCASApprovalCodes(){
        return poCASDao.getCASApprovalCodes();
    }
    public LiveData<List<ECASRequests>> GetCASRequests(String fsSource, String dFrom, String dTo){
        return poCASReqDao.GetRequests(fsSource, dFrom, dTo);
    }
    public LiveData<List<ECASRequests>> GetCASHistory(String fsSource, String dFrom, String dTo){
        return poCASReqDao.GetHistory(fsSource, dFrom, dTo);
    }
    public LiveData<ECASRequests> GetRequestDetail(String sTransNox){
        return poCASReqDao.GetRequestDetail(sTransNox);
    }

    public String GetDescription(String code){
        return poCASDao.getDescription(code);
    }
    public String getLatestStamp(){
        return poDaoRstEmp.GetLatestStamp();
    }
    public enum eSCA{
        MANUAL_LOG,
        SYSTEM_CODE,
        CREDIT_APP
    }
}
