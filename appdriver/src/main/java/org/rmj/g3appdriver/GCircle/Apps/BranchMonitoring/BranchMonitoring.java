package org.rmj.g3appdriver.GCircle.Apps.BranchMonitoring;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitChecklist;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitDetail;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitMaster;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DErrorLogs;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitChecklist;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitDetail;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.EErrorLogs;
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
    private DBranchVisitChecklist poChecklist;
    private DBranchVisitMaster poMaster;
    private DBranchVisitDetail poDetail;

    public BranchMonitoring(Application foApplication){
        poSession = EmployeeSession.getInstance(foApplication);
        poApi = new GCircleApi(foApplication);
        poHeaders = HttpHeaders.getInstance(foApplication);
        poChecklist = GGC_GCircleDB.getInstance(foApplication).branchChecklistDao();
        poMaster = GGC_GCircleDB.getInstance(foApplication).branchVisitMasterDao();
        poDetail = GGC_GCircleDB.getInstance(foApplication).branchVisitDetailDao();
        poError = GGC_GCircleDB.getInstance(foApplication).errorLogsDao();
    }

    private String GetCurrentDate(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
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

    private void SaveError(String source, String message){

        EErrorLogs loError = new EErrorLogs();
        loError.setnErrorLogID(poError.GetErrorLogCount() + 1);
        loError.setdLogDate(GetCurrentDate());
        loError.setsSourceTransNo(source);
        loError.setsMessagex(message);
        loError.setcRead("0");

        poError.SaveErrorLogs(loError);
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

            JSONObject loResult = new JSONObject(lsResult);
            if (loResult.getString("result").equalsIgnoreCase("error")){
                lsMessage = loResult.getString("message");
                return false;
            }

            JSONArray laChecklist= loResult.getJSONArray("detail");
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

            JSONObject loResult = new JSONObject(lsResult);
            if (loResult.getString("result").equalsIgnoreCase("error")){
                lsMessage = loResult.getString("message");
                return false;
            }

            JSONArray laChecklist= loResult.getJSONArray("detail");
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

            JSONObject loResult = new JSONObject(lsResult);
            if (loResult.getString("result").equalsIgnoreCase("error")){
                lsMessage = loResult.getString("message");
                return false;
            }

            JSONArray laMaster= loResult.getJSONArray("detail");
            for (int i = 0; i < laMaster.length(); i++){
                JSONObject loMaster = laMaster.getJSONObject(i);

                EBranchVisitMaster loEntity = new EBranchVisitMaster();
                loEntity.setsTransNox(loMaster.getString("sTransNox"));
                loEntity.setdTransact(loMaster.getString("dTransact"));
                loEntity.setsBranchCd(loMaster.getString("sBranchCd"));
                loEntity.setsBranchCd(loMaster.getString("sUserIDxx"));
                loEntity.setsBranchCd(loMaster.getString("cTranStat"));
                loEntity.setcSendStat("1"); //donwloaded from server, mark as sent

                poMaster.Save(loEntity);
            }
            return true;
        }catch (Exception e){
            SaveError("Branch Monitoring Master" , e.getMessage());
            return false;
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

    public LiveData<List<DBranchVisitMaster.MasterHistory>> GetHistory(){
        return poMaster.GetHistory(poSession.getUserID());
    }

    public LiveData<List<DBranchVisitDetail.BranchVisitDetail>> GetDetails(String fsTransNox){
        return poDetail.GetDetails(fsTransNox);
    }
}
