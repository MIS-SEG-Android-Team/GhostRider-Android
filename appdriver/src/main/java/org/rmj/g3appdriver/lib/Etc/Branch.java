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

package org.rmj.g3appdriver.lib.Etc;

import static org.rmj.g3appdriver.dev.Api.ApiResult.SERVER_NO_RESPONSE;
import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;
import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.apprdiver.util.SQLUtil;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;

import java.util.Date;
import java.util.List;

public class Branch {
    private static final String TAG = Branch.class.getSimpleName();

    private final DBranchInfo poDao;
    private final GCircleApi poApi;
    private final HttpHeaders poHeaders;

    private String message;

    public Branch(Application instance){
        this.poDao = GGC_GCircleDB.getInstance(instance).BranchDao();
        this.poApi = new GCircleApi(instance);
        this.poHeaders = HttpHeaders.getInstance(instance);
    }

    public String getMessage() {
        return message;
    }

    public String getLatestDataTime(){
        return poDao.getLatestDataTime();
    }

    public List<EBranchInfo> getBranchList(){
        return poDao.getBranchList();
    }
    public List<EBranchInfo> getAreaBranchesList(){
        return poDao.getAreaBranchesList();
    }

    public LiveData<List<EBranchInfo>> getAllMcBranchInfo() {
        return poDao.getAllMcBranchInfo();
    }

    public LiveData<String[]> getAllMcBranchNames(){
        return poDao.getMCBranchNames();
    }

    public LiveData<List<EBranchInfo>> getAllBranchInfo(){
        return poDao.getAllBranchInfo();
    }

    public LiveData<EBranchInfo> getUserBranchInfo(){
        return poDao.getUserBranchInfo();
    }

    public LiveData<String> getBranchName(String BranchCde) {
        return poDao.getBranchName(BranchCde);
    }

    public LiveData<EBranchInfo> getSelfieLogBranchInfo(){
        return poDao.getSelfieLogBranchInfo();
    }

    public LiveData<EBranchInfo> getBranchInfo(String BranchCD){
        return poDao.getBranchInfo(BranchCD);
    }

    public EBranchInfo getBranchInfoNonLive(String BranchCD){
        return poDao.GetBranchInfo(BranchCD);
    }

    public boolean ImportBranches(){
        try{
            JSONObject params = new JSONObject();
            params.put("bsearch", true);
            params.put("descript", "All");

            EBranchInfo loObj = poDao.GetLatestBranchInfo();
            if(loObj != null){
                params.put("timestamp", loObj.getTimeStmp());
            }

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlImportBranches(),
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

            JSONArray laJson = loResponse.getJSONArray("detail");
            for(int x = 0; x < laJson.length(); x++){
                JSONObject loJson = laJson.getJSONObject(x);
                EBranchInfo loDetail = poDao.GetBranchInfo(loJson.getString("sBranchCd"));

                if(loDetail == null){

                    if(loJson.getString("cRecdStat").equalsIgnoreCase("1")){
                        EBranchInfo loBranch = new EBranchInfo();
                        loBranch.setBranchCd(loJson.getString("sBranchCd"));
                        loBranch.setBranchNm(loJson.getString("sBranchNm"));
                        loBranch.setDescript(loJson.getString("sDescript"));
                        loBranch.setAddressx(loJson.getString("sAddressx"));
                        loBranch.setTownIDxx(loJson.getString("sTownIDxx"));
                        loBranch.setAreaCode(loJson.getString("sAreaCode"));
                        loBranch.setDivision(loJson.getString("cDivision"));
                        loBranch.setPromoDiv(loJson.getString("cPromoDiv"));
                        loBranch.setRecdStat(loJson.getString("cRecdStat"));
                        loBranch.setTimeStmp(loJson.getString("dTimeStmp"));
                        poDao.SaveBranchInfo(loBranch);
                        Log.d(TAG, "Branch info has been saved.");
                    }

                } else {
                    Date ldDate1 = SQLUtil.toDate(loDetail.getTimeStmp(), SQLUtil.FORMAT_TIMESTAMP);
                    Date ldDate2 = SQLUtil.toDate((String) loJson.getString("dTimeStmp"), SQLUtil.FORMAT_TIMESTAMP);
                    if (!ldDate1.equals(ldDate2)) {
                        loDetail.setBranchCd(loJson.getString("sBranchCd"));
                        loDetail.setBranchNm(loJson.getString("sBranchNm"));
                        loDetail.setDescript(loJson.getString("sDescript"));
                        loDetail.setAddressx(loJson.getString("sAddressx"));
                        loDetail.setTownIDxx(loJson.getString("sTownIDxx"));
                        loDetail.setAreaCode(loJson.getString("sAreaCode"));
                        loDetail.setDivision(loJson.getString("cDivision"));
                        loDetail.setPromoDiv(loJson.getString("cPromoDiv"));
                        loDetail.setRecdStat(loJson.getString("cRecdStat"));
                        loDetail.setTimeStmp(loJson.getString("dTimeStmp"));
                        poDao.UpdateBranchInfo(loDetail);
                        Log.d(TAG, "Branch info has been updated.");
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
}
