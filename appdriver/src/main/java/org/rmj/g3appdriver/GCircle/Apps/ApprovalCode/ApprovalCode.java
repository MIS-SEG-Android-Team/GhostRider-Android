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
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DSCARqstEmp;
import org.rmj.g3appdriver.GCircle.room.Entities.ESCARqstEmp;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DApprovalCode;
import org.rmj.g3appdriver.GCircle.room.Entities.ECodeApproval;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ESCA_Request;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.Obj.LoanApproval;
import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.Obj.ManualLog;
import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.Obj.SystemCode;
import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.model.SCA;
import org.rmj.g3appdriver.utils.SQLUtil;

import java.util.List;

public class ApprovalCode {
    private static final String TAG = ApprovalCode.class.getSimpleName();

    private final DApprovalCode poDao;

    private final DSCARqstEmp poDaoRstEmp;;

    public final Application instance;

    private final GCircleApi poApi;

    private final HttpHeaders poHeaders;

    private String message;

    public ApprovalCode(Application instance) {
        this.instance = instance;
        this.poDao = GGC_GCircleDB.getInstance(instance).ApprovalDao();
        this.poDaoRstEmp = GGC_GCircleDB.getInstance(instance).scaRqstEmpDao();
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

        if (lsEmpLvID == 4 ||
                lsEmpLvID == 5 ||
                loUser.getEmployID().equalsIgnoreCase("M00112000440")){
            lsCondition  = "cAreaHead = '1'";
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

        int scarqstCount = poDaoRstEmp.CountSCARequest();

        if (scarqstCount > 0){
            String lsQuery = "SELECT a.* FROM xxxSCA_Request a LEFT JOIN SCA_Rqst_Emp b " +
                    "ON a.sSCACodex = b.sSCACodex AND b.sEmployIDx = '" + loUser.getEmployID() + "'";

            return poDao.getAuthorizedFeatures(new SimpleSQLiteQuery(lsQuery));
        }else {
            if (!lsCondition.isEmpty()){
                lsSqlQryxx = MiscUtil.addCondition(lsSqlQryxx, lsCondition);
            }
            return poDao.getAuthorizedFeatures(new SimpleSQLiteQuery(lsSqlQryxx));
        }
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
