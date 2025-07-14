/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 4/28/21 8:56 AM
 * project file last modified : 4/28/21 8:56 AM
 */
package org.rmj.g3appdriver.GCircle.room.Repositories;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DLocatorSysLog;
import org.rmj.g3appdriver.GCircle.room.Entities.EGLocatorSysLog;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.GConnect.Api.GConnectApi;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.dev.Device.Telephony;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.etc.AppConfigPreference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DeviceLocationRecords {
    private static final String TAG = DeviceLocationRecords.class.getSimpleName();

    private final DLocatorSysLog poDao;
    private final HttpHeaders poHeaders;
    private final GCircleApi poApi;
    private final AppConfigPreference poConfig;
    private final Telephony poDevID;

    private String message;

    public DeviceLocationRecords(Application instance) {
        this.poDao = GGC_GCircleDB.getInstance(instance).locatorSysLogDao();
        this.poHeaders = HttpHeaders.getInstance(instance);
        this.poConfig = AppConfigPreference.getInstance(instance);
        this.poApi = new GCircleApi(instance);
        this.poDevID = new Telephony(instance);
    }

    public String getMessage() {
        return message;
    }

    public boolean saveCurrentLocation(double nLatitude, double nLongtude, String cServicex, String sRemarksx){
        try {
            String lsUserID = poDao.getUserID();

            if (lsUserID == null) {
                Log.d(TAG, "Unable to save location no user detected.");
                return false;
            }

            EGLocatorSysLog loDetail = new EGLocatorSysLog();
            String lsLoctID = CreateUniqueID();

            loDetail.setLoctnIDx(lsLoctID);
            loDetail.setUserIDxx(lsUserID);
            loDetail.setDeviceID(poDevID.getDeviceID());
            loDetail.setTransact(AppConstants.DATE_MODIFIED());
            loDetail.setLatitude(nLatitude);
            loDetail.setLongitud(nLongtude);
            loDetail.setGpsEnbld(cServicex);
            loDetail.setRemarksx(sRemarksx);
            loDetail.setTimeStmp(AppConstants.DATE_MODIFIED());

            poDao.insertLocation(loDetail);

            //todo: upload to server
            JSONObject loParam = new JSONObject();
            loParam.put("dTransact", AppConstants.DATE_MODIFIED());
            loParam.put("nLatitude", nLatitude);
            loParam.put("nLongitud", nLongtude);
            loParam.put("cGPSEnbld", cServicex);

            JSONArray laDetail = new JSONArray();
            laDetail.put(loParam);

            JSONObject loJson = new JSONObject();
            loJson.put("detail", laDetail);

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlSubmitLocationTrack(),
                    loJson.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = "No server response. Tracked location will be uploaded soon.";

                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");

            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = loError.getString("message") + ". Tracked location will be uploaded soon.";

                return false;
            }else {
                poDao.updateSysLogStatus(AppConstants.DATE_MODIFIED(), lsLoctID);
                message = "Tracked location sent to server.";

                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
            message = e.getMessage();

            return false;
        }
    }

    public boolean uploadUnsentLocationTracks(){
        try{
            List<EGLocatorSysLog> loLocations = poDao.GetTrackingLocations();

            if(loLocations == null){
                message = "No records for location to upload";
                return false;
            }

            if(loLocations.size() == 0){
                message = "No records for location to upload";
                return false;
            }

            JSONObject loJson = new JSONObject();
            JSONArray laDetail = new JSONArray();
            for(int x= 0; x < loLocations.size(); x++){
                JSONObject loDetail = new JSONObject();
                loDetail.put("dTransact", loLocations.get(x).getTransact());
                loDetail.put("nLatitude", loLocations.get(x).getLatitude());
                loDetail.put("nLongitud", loLocations.get(x).getLongitud());
                loDetail.put("cGPSEnbld", loLocations.get(x).getGpsEnbld());

                laDetail.put(loDetail);
            }

            loJson.put("detail", laDetail);

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlSubmitLocationTrack(),
                    loJson.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = "No server response.";
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                String lsMessage = loError.getString("message");
                message = lsMessage;
                return false;
            }

            for(int x = 0; x < loLocations.size(); x++){
                poDao.updateSysLogStatus(AppConstants.DATE_MODIFIED(), loLocations.get(x).getLoctnIDx());
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = e.getMessage();
            return false;
        }
    }

    private String CreateUniqueID() {
        String lsUniqIDx = "";
        try {
            String lsBranchCd = "MX01";
            String lsCrrYear = new SimpleDateFormat("yy", Locale.getDefault()).format(new Date());
            StringBuilder loBuilder = new StringBuilder(lsBranchCd);
            loBuilder.append(lsCrrYear);

            int lnLocalID = poDao.GetRowsCountForID() + 1;
            String lsPadNumx = String.format("%05d", lnLocalID);
            loBuilder.append(lsPadNumx);
            lsUniqIDx = loBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        Log.d(TAG, lsUniqIDx);
        return lsUniqIDx;
    }
}
