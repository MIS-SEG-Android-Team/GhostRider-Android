package org.rmj.g3appdriver.GCircle.Apps.User_Guide.Object;

import static org.rmj.g3appdriver.dev.Api.ApiResult.SERVER_NO_RESPONSE;
import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;
import static org.rmj.g3appdriver.lib.Firebase.CrashReportingUtil.reportException;

import android.app.Application;
import android.util.Base64;
import android.util.Log;

import androidx.lifecycle.LiveData;
import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DGuides;
import org.rmj.g3appdriver.GCircle.room.Entities.EGuides;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.GCircle.room.Repositories.AppTokenManager;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.dev.Api.WebFileServer;
import org.rmj.g3appdriver.etc.AppConfigPreference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserGuides {

    private final AppConfigPreference poConfig;
    private final EmployeeSession poSession;
    private final AppTokenManager poToken;
    private final DGuides poDao;
    private final GCircleApi poApi;
    private final HttpHeaders poHeaders;
    private String message;

    public UserGuides(Application context){
        this.poConfig = AppConfigPreference.getInstance(context);
        this.poDao = GGC_GCircleDB.getInstance(context).userguideDao();
        this.poApi = new GCircleApi(context);
        this.poHeaders = HttpHeaders.getInstance(context);
        this.poSession = EmployeeSession.getInstance(context);
        this.poToken = new AppTokenManager(context);
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
        }
        return lsUniqIDx;
    }

    public String GetMessage() {
        return message;
    }

    public Boolean DownloadGuides(){

        try {

            String lsResponse = WebClient.sendRequest(poApi.getUrlDownloadGuides(), new JSONObject().toString(), poHeaders.getHeaders());
            if (lsResponse == null || lsResponse.isEmpty()){
                message = "No response from server";
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");

            if (lsResult.equalsIgnoreCase("error")) {
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;
            }

            JSONArray laGuides = new JSONArray(loResponse.getString("detail"));
            if (laGuides.length() > 0){

                poDao.DeleteGuides();

                //todo link of device configured server
                String lsFileDir = poConfig.getAppServer() + "usermanuals/";
                for(int i = 0; i < laGuides.length(); i++){

                    JSONObject loObj = laGuides.getJSONObject(i);
                    EGuides loGuide = new EGuides();

                    loGuide.setsTransNox(loObj.getString("transNox"));
                    loGuide.setType(loObj.getString("type"));
                    loGuide.setsTitlexx(loObj.getString("title"));
                    loGuide.setsURlxx(lsFileDir + loObj.getString("link"));

                    poDao.InsertGuides(loGuide);
                }
            }

            return true;

        }catch (Exception e){
            message = e.getMessage();
            return false;
        }
    }

    public Boolean UploadGuide(String fileloc, String filename){

        try {

            String lsClient = poToken.GetClientToken();

            if(lsClient == null){
                message = "No generated client token to upload image.";
                return false;
            }

            String lsAccess = poToken.GetAccessToken(lsClient);

            if(lsAccess == null){
                message = "No generated access token to upload image.";
                return false;
            }

            org.json.simple.JSONObject loUpload = WebFileServer.UploadFile(
                    fileloc,
                    lsAccess,
                    "0031",
                    poSession.getUserID(),
                    filename,
                    poSession.getBranchCode(),
                    CreateUniqueID(),
                    "",
                    "");

            if (loUpload == null) {
                message = SERVER_NO_RESPONSE;
                reportException(poSession.getUserID(), message);
                return null;
            }

            JSONObject loResult = new JSONObject(loUpload.toJSONString());
            if(loResult.has("result")){
                String lsImgResult = (String) loUpload.get("result");
                if (lsImgResult.equalsIgnoreCase("error")) {
                    JSONObject loError = loResult.getJSONObject("error");
                    message = getErrorMessage(loError);
                    reportException(poSession.getUserID(), message);
                    return false;
                }
            } else {
                String lsImgResult = (String) loUpload.get("rhsult");
                if (lsImgResult.equalsIgnoreCase("error")) {
                    JSONObject loError = loResult.getJSONObject("error");
                    message = getErrorMessage(loError);
                    reportException(poSession.getUserID(), message);
                    return false;
                }
            }

            EGuides loGuide = new EGuides();

            loGuide.setsTransNox(loUpload.get("sTransNox").toString());
            loGuide.setType("0031");
            loGuide.setsTitlexx(filename);
            loGuide.setsURlxx(loUpload.get("url").toString());

            poDao.InsertGuides(loGuide);

            return true;

        }catch (Exception e){
            message = e.getMessage();
            return false;
        }

    }

    public LiveData<List<EGuides>> GetGuides(){
        return poDao.GetGuides();
    }
}
