package org.rmj.g3appdriver.GCircle.Apps.BranchMonitoring;

import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;

import android.app.Application;
import android.os.Build;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitChecklist;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitDetail;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitMaster;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DErrorLogs;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitChecklist;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitDetail;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.EErrorLogs;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDetail;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Api.WebClient;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BranchMonitoring {

    private String lsMessage;

    private EmployeeSession poSession;
    private GCircleApi poApi;
    private HttpHeaders poHeaders;

    private DErrorLogs poError;
    private DBranchInfo poBranch;
    private DBranchVisitChecklist poChecklist;
    private DBranchVisitMaster poMaster;
    private DBranchVisitDetail poDetail;

    public BranchMonitoring(Application foApplication){
        poSession = EmployeeSession.getInstance(foApplication);
        poApi = new GCircleApi(foApplication);
        poHeaders = HttpHeaders.getInstance(foApplication);
        poBranch = GGC_GCircleDB.getInstance(foApplication).BranchDao();
        poChecklist = GGC_GCircleDB.getInstance(foApplication).branchChecklistDao();
        poMaster = GGC_GCircleDB.getInstance(foApplication).branchVisitMasterDao();
        poDetail = GGC_GCircleDB.getInstance(foApplication).branchVisitDetailDao();
        poError = GGC_GCircleDB.getInstance(foApplication).errorLogsDao();
    }

    private String GetCurrentDateTime(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        }
    }

    public String GetCurrentDate(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }else {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        }
    }

    private String GenerateTransNox(){

        String lsTransNox = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lsTransNox = poSession.getBranchCode() +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                    poMaster.CountMaster() + 1;
        }else {
            lsTransNox = poSession.getBranchCode() +
                    new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Calendar.getInstance().getTime()) +
                    poMaster.CountMaster() + 1;
        }

        return lsTransNox;
    }

    public void SaveError(String source, String message){

        EErrorLogs loError = new EErrorLogs();
        loError.setnErrorLogID(poError.GetErrorLogCount() + 1);
        loError.setdLogDate(GetCurrentDateTime());
        loError.setsSourceTransNo(source);
        loError.setsMessagex(message);
        loError.setcRead("0");

        poError.SaveErrorLogs(loError);
    }

    public EBranchInfo GetBranch(String fsBranchCd){
        return poBranch.GetBranchInfo(fsBranchCd);
    }

    public String SaveNewMaster(String fsBranchCd){

        EBranchVisitMaster loMaster = new EBranchVisitMaster();
        loMaster.setsTransNox(GenerateTransNox());
        loMaster.setsBranchCd(fsBranchCd);
        loMaster.setdTransact(GetCurrentDate());
        loMaster.setsUserIDxx(poSession.getUserID());
        loMaster.setcTranStat("0");
        loMaster.setcSendStat("0");

        poMaster.Save(loMaster);
        return loMaster.getsTransNox();
    }

    public void SaveNewDetail(String fsTransNox, String fsCategrID, String fsRemarks){
        EBranchVisitDetail loDetail = new EBranchVisitDetail();
        loDetail.setsTransNox(fsTransNox);
        loDetail.setsCategrID(fsCategrID);
        loDetail.setsRemarksx(fsRemarks);

        poDetail.Save(loDetail);
    }

    public void UpdateRemarks(String fsRemarksx, String fsTransNox, String fsCategrID){
        poDetail.UpdateRemarks(fsRemarksx, fsTransNox, fsCategrID);
    }

    public String GetMessage(){
        return lsMessage;
    }

    public Boolean ImportChecklist(){

        try{

            String lsResult = WebClient.sendRequest(poApi.getUrlBranchChecklist(), new JSONObject().toString(), poHeaders.getHeaders());
            if (lsResult == null || lsResult.isEmpty()){
                lsMessage = "No response from server";
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResult);
            String lsResponse = loResponse.getString("result");

            if(lsResponse.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                lsMessage = getErrorMessage(loError);;
                return false;
            }

            JSONArray laChecklist= loResponse.getJSONArray("detail");
            for (int i = 0; i < laChecklist.length(); i++){
                JSONObject loChecklist = laChecklist.getJSONObject(i);

                loChecklist.getString("sCategrID");
                loChecklist.getString("sDescript");
                loChecklist.getString("cRecdStat");

                EBranchVisitChecklist loEntity = new EBranchVisitChecklist();
                loEntity.setsCategrID(loChecklist.getString("sCategrID"));
                loEntity.setsDescript(loChecklist.getString("sDescript"));
                loEntity.setcRecdStat(loChecklist.getString("cRecdStat"));

                poChecklist.Save(loEntity);
            }
            return true;
        }catch (Exception e){
            lsMessage = e.getMessage();
            SaveError("Branch Monitoring Checklist" , e.getMessage());
            return false;
        }
    }

    public Boolean ImportBranchVisitDetails(String fsTransNox){

        try {

            JSONObject loParams = new JSONObject();
            loParams.put("sTransNox", fsTransNox);

            String lsResult = WebClient.sendRequest(poApi.getUrlBranchVisitDetail(), loParams.toString(), poHeaders.getHeaders());
            if (lsResult == null || lsResult.isEmpty()){
                lsMessage = "No response from server";
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResult);
            String lsResponse = loResponse.getString("result");

            if(lsResponse.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                lsMessage = getErrorMessage(loError);;
                return false;
            }

            JSONArray laChecklist= loResponse.getJSONArray("detail");
            for (int i = 0; i < laChecklist.length(); i++){
                JSONObject loChecklist = laChecklist.getJSONObject(i);

                loChecklist.getString("sTransNox");
                loChecklist.getString("sCategrID");
                loChecklist.getString("sRemarksx");

                EBranchVisitDetail loEntity = new EBranchVisitDetail();
                loEntity.setsTransNox(loChecklist.getString("sTransNox"));
                loEntity.setsCategrID(loChecklist.getString("sCategrID"));
                loEntity.setsRemarksx(loChecklist.getString("sRemarksx"));

                poDetail.Save(loEntity);
            }
            return true;

        }catch (Exception e){
            lsMessage = e.getMessage();
            SaveError("Branch Monitoring Details" , e.getMessage());
            return false;
        }
    }

    public Boolean ImportBranchVisitMaster(String fsDfrom, String fsDto){

        try {

            JSONObject loParams = new JSONObject();
            loParams.put("dFrom", fsDfrom);
            loParams.put("dTo", fsDto);
            loParams.put("sUserIDxx", poSession.getUserID());

            String lsResult = WebClient.sendRequest(poApi.getUrlBranchVisitMaster() , loParams.toString(), poHeaders.getHeaders());
            if (lsResult == null || lsResult.isEmpty()){
                lsMessage = "No response from server";
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResult);
            String lsResponse = loResponse.getString("result");

            if(lsResponse.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                lsMessage = getErrorMessage(loError);;
                return false;
            }

            JSONArray laMaster= loResponse.getJSONArray("detail");
            for (int i = 0; i < laMaster.length(); i++){
                JSONObject loMaster = laMaster.getJSONObject(i);

                EBranchVisitMaster loEntity = new EBranchVisitMaster();
                loEntity.setsTransNox(loMaster.getString("sTransNox"));
                loEntity.setdTransact(loMaster.getString("dTransact"));
                loEntity.setsBranchCd(loMaster.getString("sBranchCd"));
                loEntity.setsUserIDxx(loMaster.getString("sUserIDxx"));
                loEntity.setcTranStat(loMaster.getString("cTranStat"));
                loEntity.setcSendStat("1"); //donwloaded from server, mark as sent

                poMaster.Save(loEntity);
            }
            return true;
        }catch (Exception e){
            lsMessage = e.getMessage();
            SaveError("Branch Monitoring Master" , e.getMessage());
            return false;
        }
    }

    public Object[] SubmitBranchVisit(EBranchVisitMaster foMaster, List<DBranchVisitDetail.BranchVisitDetail> faDetails){

        Object[] result = new Object[2];
        try {

            //update status first, submit will CLOSE(1) the transaction to avoid modifications
            poMaster.UpdateStatus(foMaster.getsTransNox(), "1");

            JSONObject loParams = new JSONObject();
            loParams.put("sTransNox", foMaster.getsTransNox());
            loParams.put("sBranchCd", foMaster.getsBranchCd());
            loParams.put("cTranStat", "1");

            JSONArray laDetails = new JSONArray();
            for (DBranchVisitDetail.BranchVisitDetail loDetail : faDetails){

                JSONObject loParam = new JSONObject();
                loParam.put("sCategrID", loDetail.sCategrID);
                loParam.put("sRemarksx", loDetail.sRemarksx);

                laDetails.put(loParam);
            }
            loParams.put("sPayloadxx", laDetails);

            String lsResponse = WebClient.sendRequest(poApi.getUrlSubmitBranchVisit(), loParams.toString(), poHeaders.getHeaders());
            if (lsResponse == null){
                lsMessage = "Server no response";
                result[0] = false;
                result[1] = lsMessage;
                return result;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");

            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                lsMessage = getErrorMessage(loError);;
                result[0] = false;
                result[1] = lsMessage;
                return result;
            }

            String lsTransNox = loResponse.getString("sTransNox");

            //update master transaction no
            poMaster.UpdateTransactionNumber(lsTransNox, foMaster.getsTransNox());

            //update detail transaction no
            poDetail.UpdateTransactionNumber(lsTransNox, foMaster.getsTransNox());

            result[0] = true;
            result[1] = lsTransNox;
            return result;
        }catch (Exception e){
            lsMessage = e.getMessage();
            result[0] = false;
            result[1] = lsMessage;
            return result;
        }
    }

    public LiveData<List<EBranchVisitChecklist>> GetChecklist(){
        return poChecklist.GetChecklist();
    }

    public EBranchVisitMaster GetEntryToday(){
        return poMaster.GetEntryToday(poSession.getUserID(), GetCurrentDate());
    }

    public LiveData<EBranchVisitMaster> GetMasterTransaction(String fsTransNox){
        return poMaster.GetMasterTransaction(fsTransNox);
    }

    public LiveData<List<DBranchVisitMaster.MasterHistory>> GetHistory(String fsTransTat){
        return poMaster.GetHistory(
                new SimpleSQLiteQuery(
                        "SELECT " +
                                "IFNULL((SELECT b.sBranchNm FROM Branch_Info b WHERE b.sBranchCd = a.sBranchCd), " +
                                "a.sBranchCd) sBranchNm, " +
                                "a.sTransNox, " +
                                "a.sBranchCd, " +
                                "a.dTransact, " +
                                "a.cSendStat, " +
                                "a.cTranStat " +
                        "FROM " +
                                "Branch_Visit_Master a " +
                        "WHERE " +
                                "a.sUserIDxx = '" + poSession.getUserID() + "' " +
                        "AND " +
                                fsTransTat
                )
        );
    }

    public LiveData<List<DBranchVisitDetail.BranchVisitDetail>> GetDetails(String fsTransNox){
        return poDetail.GetDetails(fsTransNox);
    }
}
