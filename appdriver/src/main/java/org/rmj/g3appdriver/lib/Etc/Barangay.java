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
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBarangayInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBarangayInfo;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;

import java.util.Date;
import java.util.List;

public class Barangay {
    private static final String TAG = Barangay.class.getSimpleName();

    private final DBarangayInfo poDao;

    private final GCircleApi poApi;
    private final HttpHeaders poHeaders;

    private String message;

    public Barangay(Application instance){
        this.poDao = GGC_GCircleDB.getInstance(instance).BarangayDao();
        this.poApi = new GCircleApi(instance);
        this.poHeaders = HttpHeaders.getInstance(instance);
    }

    public String getMessage() {
        return message;
    }

    public void insertBulkData(List<EBarangayInfo> barangayInfos){
        poDao.insertBulkBarangayData(barangayInfos);
    }

    public LiveData<List<EBarangayInfo>> getAllBarangayFromTown(String TownID){
        return poDao.getAllBarangayInfoFromTown(TownID);
    }

    public LiveData<String[]> getBarangayNamesFromTown(String TownID){
        return poDao.getAllBarangayNameFromTown(TownID);
    }

    public DBarangayInfo.BrgyTownProvNames getBrgyTownProvName(String BrgyID){
        return poDao.getBrgyTownProvName(BrgyID);
    }

    public String getLatestDataTime(){
        return poDao.getLatestDataTime();
    }

    public boolean ImportBarangay(){
        try{
            JSONObject params = new JSONObject();
            params.put("bsearch", true);
            params.put("descript", "All");

            Log.d(TAG, params.toString());

            EBarangayInfo loObj = poDao.GetLatestBarangayInfo();
            if(loObj != null) {
                params.put("timestamp", loObj.getTimeStmp());
            }

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlImportBarangay(),
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
            for(int x = 0; x < laJson.length(); x++){
                JSONObject loJson = laJson.getJSONObject(x);
                EBarangayInfo loDetail = poDao.GetBarangayInfo(loJson.getString("sBrgyIDxx"));

                if(loDetail == null) {

                    if(loJson.getString("cRecdStat").equalsIgnoreCase("1")) {
                        EBarangayInfo info = new EBarangayInfo();
                        info.setBrgyIDxx(loJson.getString("sBrgyIDxx"));
                        info.setBrgyName(loJson.getString("sBrgyName"));
                        info.setTownIDxx(loJson.getString("sTownIDxx"));
                        info.setHasRoute(loJson.getString("cHasRoute"));
                        info.setBlackLst(loJson.getString("cBlackLst"));
                        info.setRecdStat(loJson.getString("cRecdStat"));
                        info.setTimeStmp(loJson.getString("dTimeStmp"));
                        poDao.insert(info);
                        Log.d(TAG, "Barangay info has been saved.");
                    }

                } else {
                    Date ldDate1 = SQLUtil.toDate(loDetail.getTimeStmp(), SQLUtil.FORMAT_TIMESTAMP);
                    Date ldDate2 = SQLUtil.toDate((String) loJson.get("dTimeStmp"), SQLUtil.FORMAT_TIMESTAMP);
                    if (!ldDate1.equals(ldDate2)) {
                        loDetail.setBrgyIDxx(loJson.getString("sBrgyIDxx"));
                        loDetail.setBrgyName(loJson.getString("sBrgyName"));
                        loDetail.setTownIDxx(loJson.getString("sTownIDxx"));
                        loDetail.setHasRoute(loJson.getString("cHasRoute"));
                        loDetail.setBlackLst(loJson.getString("cBlackLst"));
                        loDetail.setRecdStat(loJson.getString("cRecdStat"));
                        loDetail.setTimeStmp(loJson.getString("dTimeStmp"));
                        poDao.update(loDetail);
                        Log.d(TAG, "Barangay info has been updated.");
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
