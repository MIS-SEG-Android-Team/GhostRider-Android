package org.rmj.g3appdriver.GCircle.Apps.User_Guide.Object;

import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;

import android.app.Application;
import android.util.Base64;

import androidx.lifecycle.LiveData;
import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DGuides;
import org.rmj.g3appdriver.GCircle.room.Entities.EGuides;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.etc.AppConfigPreference;

import java.util.List;

public class UserGuides {

    private AppConfigPreference poConfig;
    private DGuides poDao;
    private GCircleApi poApi;
    private HttpHeaders poHeaders;
    private String message;

    public UserGuides(Application context){
        this.poConfig = AppConfigPreference.getInstance(context);
        this.poDao = GGC_GCircleDB.getInstance(context).userguideDao();
        this.poApi = new GCircleApi(context);
        this.poHeaders = HttpHeaders.getInstance(context);
    }

    public String getMessage() {
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

    public Boolean UploadGuide(String filename, byte[] file){

        try {

            JSONObject params = new JSONObject();
            params.put("filename", filename);
            params.put("uploadedfile", Base64.encodeToString(file, Base64.DEFAULT));

            String lsResponse = WebClient.sendRequest(poApi.getUrlUploadGuides(), params.toString(), poHeaders.getHeaders());
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

            //todo link of device configured server
            String lsFileDir = poConfig.getAppServer() + "usermanuals/";

            EGuides loGuide = new EGuides();

            loGuide.setsTransNox(loResponse.getString("transNox"));
            loGuide.setType(loResponse.getString("type"));
            loGuide.setsTitlexx(loResponse.getString("title"));
            loGuide.setsURlxx(lsFileDir + loResponse.getString("link"));

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
