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

package org.rmj.g3appdriver.GCircle.room.Repositories;

import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;
import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchPerformance;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchPerformance;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.GCircle.Apps.BullsEye.PerformancePeriod;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.GCircle.Etc.DeptCode;

import java.util.ArrayList;
import java.util.List;

public class RBranchPerformance {
    private static final String TAG = RBranchPerformance.class.getSimpleName();

    private DBranchPerformance poDao;

    private final GCircleApi poApi;
    private final HttpHeaders poHeaders;

    private String message;

    public RBranchPerformance(Application instance) {
        this.poDao = GGC_GCircleDB.getInstance(instance).BranchPerformanceDao();
        this.poApi = new GCircleApi(instance);
        this.poHeaders = HttpHeaders.getInstance(instance);
    }

    public String getMessage() {
        return message;
    }

    public void insertBulkData(List<EBranchPerformance> list) {
        poDao.insertBulkData(list);
    }

    public LiveData<List<EBranchPerformance>> getAllBranchPerformanceInfoByBranch(String branchCD){
        return poDao.getAllBranchPerformanceInfoByBranch(branchCD);
    }

    public boolean ImportData(){
        try{
            int lsUserLvl = poDao.GetUserLevel();
            String lsDeptIDx = poDao.GetUserDepartment();

            if(!lsDeptIDx.equalsIgnoreCase(DeptCode.SALES) ||
            !lsDeptIDx.equalsIgnoreCase(DeptCode.CREDIT_SUPPORT_SERVICES)){
                message = "Your department code is not authorize to download branch performance info";
                return false;
            }

            if(lsUserLvl != DeptCode.LEVEL_AREA_MANAGER ||
                    lsUserLvl != DeptCode.LEVEL_BRANCH_HEAD){
                message = "Your user level is not authorize to download area/branch performance";
                return false;
            }

            String lsAreaCode = poDao.GetAreaCode();

            if(lsAreaCode == null){
                message = "Unable to retrieve area code. Please re-login account.";
                return false;
            }

            ArrayList<String> lsPeriod = PerformancePeriod.getList();
            for(int i = 0; i < lsPeriod.size(); i++){

                JSONObject params = new JSONObject();
                params.put("period", lsPeriod.get(i));
                params.put("areacd", lsAreaCode);

                String lsResponse = WebClient.sendRequest(
                        poApi.getImportBranchPerformance(),
                        params.toString(),
                        poHeaders.getHeaders());

                JSONObject loResponse = new JSONObject(lsResponse);
                String lsResult = loResponse.getString("result");
                if (lsResult.equalsIgnoreCase("error")) {
                    JSONObject loError = loResponse.getJSONObject("error");
                    message = getErrorMessage(loError);
                    return false;
                }

                JSONArray laJson = loResponse.getJSONArray("detail");
                List<EBranchPerformance> branchInfo = new ArrayList<>();
                for(int x = 0; x < laJson.length(); x++){
                    JSONObject loJson = laJson.getJSONObject(x);
                    EBranchPerformance loDetail = poDao.GetBranchPerformance(
                            loJson.getString("sPeriodxx"),
                            loJson.getString("sBranchCd"));

                    if(loDetail == null){

                        EBranchPerformance info = new EBranchPerformance();
                        info.setPeriodxx(loJson.getString("sPeriodxx"));
                        info.setBranchCd(loJson.getString("sBranchCd"));
                        info.setBranchNm(loJson.getString("sBranchNm"));
                        info.setMCGoalxx(Integer.parseInt(loJson.getString("nMCGoalxx")));
                        info.setSPGoalxx(Float.parseFloat(loJson.getString("nSPGoalxx")));
                        info.setJOGoalxx(Integer.parseInt(loJson.getString("nJOGoalxx")));
                        info.setLRGoalxx(Float.parseFloat(loJson.getString("nLRGoalxx")));
                        info.setMCActual(Integer.parseInt(loJson.getString("nMCActual")));
                        info.setSPActual(Float.parseFloat(loJson.getString("nSPActual")));
                        info.setLRActual(Float.parseFloat(loJson.getString("nLRActual")));
                        poDao.insert(info);
                        Log.d(TAG, "Branch performance for " + loJson.getString("sBranchCd") + " has been saved.");

                    } else {

                    }
                }

                Thread.sleep(1000);

            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }
}
