package org.rmj.g3appdriver.lib.Version;

import static org.rmj.g3appdriver.dev.Api.ApiResult.SERVER_NO_RESPONSE;
import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;
import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Device.Telephony;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;

import java.util.ArrayList;
import java.util.List;

public class AppVersion {
    private static final String TAG = AppVersion.class.getSimpleName();

    private final AppConfigPreference poConfig;
    private final Telephony poTlphony;
    private final GCircleApi poApi;
    private final HttpHeaders poHeaders;
    private final EmployeeMaster poUser;

    private String message;

    public AppVersion(Application instance) {
        this.poConfig = AppConfigPreference.getInstance(instance);
        this.poTlphony = new Telephony(instance);
        this.poApi = new GCircleApi(instance);
        this.poHeaders = HttpHeaders.getInstance(instance);
        this.poUser = new EmployeeMaster(instance);
    }

    public String getMessage() {
        return message;
    }

    public boolean SubmitUserAppVersion(){
        try{
            EEmployeeInfo loUser = poUser.getUserNonLiveData();
            JSONObject params = new JSONObject();
            params.put("sUserIDxx", loUser.getUserIDxx());
            params.put("sProdctID", poConfig.ProducID());
            params.put("sIMEINoxx", poTlphony.getDeviceID());
            params.put("sAppVersn", poConfig.getVersionCode());

            String lsAddress = poApi.getUrlSubmitAppVersion();

            String lsResponse = WebClient.sendRequest(
                    lsAddress,
                    params.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                Log.e(TAG, message);
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                Log.e(TAG, loError.toString());
                return false;
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public List<VersionInfo> GetVersionInfo(){
        try{
            List<VersionInfo> loVersion = new ArrayList<>();
            String lsAddress = poApi.getUrlVersionLog();

            String lsResponse = WebClient.sendRequest(
                    lsAddress,
                    new JSONObject().toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                return null;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                Log.e(TAG, loError.toString());
                return null;
            }

            JSONArray laDetail = loResponse.getJSONArray("detail");
            for(int x = 0; x < laDetail.length(); x++){
                JSONObject loJson = laDetail.getJSONObject(x);
                int lsVersnCde = loJson.getInt("sVersnCde");
                String lsVersnNme = loJson.getString("sVersnNme");
                String lsAppNotes = loJson.getString("sAppNotes");

                VersionInfo loInfo = new VersionInfo();
                loInfo.setsVrsionCd(lsVersnCde);
                loInfo.setsVrsionNm(lsVersnNme);
                loInfo.setsVrsnNote(lsAppNotes);
                if(x == 0){
                    int lsVernCd = poConfig.getVersionCode();
                    if(lsVersnCde > lsVernCd){
                        loInfo.setcNewUpdte("1");
                    }
                }
                loVersion.add(loInfo);
            }
            return loVersion;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    public VersionInfo CheckUpdate(){
        try{
            VersionInfo loVersion = new VersionInfo();
            String lsAddress = poApi.getUrlCheckUpdate();

            JSONObject params = new JSONObject();
            params.put("sVersnCde", poConfig.getVersionCode());

            String lsResponse = WebClient.sendRequest(
                    lsAddress,
                    params.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                return null;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                Log.e(TAG, loError.toString());
                return null;
            }

            JSONArray laDetail = loResponse.getJSONArray("detail");
            for(int x = 0; x < laDetail.length(); x++){
                JSONObject loJson = laDetail.getJSONObject(x);
                int lsVersnCde = loJson.getInt("sVersnCde");
                String lsVersnNme = loJson.getString("sVersnNme");
                String lsAppNotes = loJson.getString("sAppNotes");

                VersionInfo loInfo = new VersionInfo();
                loInfo.setsVrsionCd(lsVersnCde);
                loInfo.setsVrsionNm(lsVersnNme);
                loInfo.setsVrsnNote(lsAppNotes);
                if(x == 0){
                    int lsVernCd = poConfig.getVersionCode();
                    if(lsVersnCde > lsVernCd){
                        loInfo.setcNewUpdte("1");
                    }
                }
                loVersion = loInfo;
            }
            return loVersion;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }
}
