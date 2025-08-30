package org.rmj.g3appdriver.GCircle.Apps.User_Guide.Object;

import static org.rmj.g3appdriver.dev.Api.ApiResult.SERVER_NO_RESPONSE;
import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;
import static org.rmj.g3appdriver.lib.Firebase.CrashReportingUtil.reportException;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DArticle;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCompanyPolicy;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DGuides;
import org.rmj.g3appdriver.GCircle.room.Entities.EArticleDetails;
import org.rmj.g3appdriver.GCircle.room.Entities.EArticleHead;
import org.rmj.g3appdriver.GCircle.room.Entities.EGuides;
import org.rmj.g3appdriver.GCircle.room.Entities.EPolicyContents;
import org.rmj.g3appdriver.GCircle.room.Entities.EPolicyMenus;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.GCircle.room.Repositories.AppTokenManager;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.dev.Api.WebFileServer;
import org.rmj.g3appdriver.etc.AppConfigPreference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UserGuides {
    private final AppConfigPreference poConfig;
    private final EmployeeSession poSession;
    private final AppTokenManager poToken;
    private final DGuides dGuides;
    private DCompanyPolicy dCompanyPolicy;
    private DArticle dArticle;
    private final GCircleApi poApi;
    private final HttpHeaders poHeaders;
    private String message;

    public UserGuides(Application context){
        this.poConfig = AppConfigPreference.getInstance(context);
        this.dGuides = GGC_GCircleDB.getInstance(context).userguideDao();
        this.dCompanyPolicy = GGC_GCircleDB.getInstance(context).policyDao();
        this.dArticle = GGC_GCircleDB.getInstance(context).articleDao();
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

            int lnLocalID = dGuides.GetRowsCountForID() + 1;
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

                dGuides.DeleteGuides();

                //todo link of device configured server
                String lsFileDir = poConfig.getAppServer() + "usermanuals/";
                for(int i = 0; i < laGuides.length(); i++){

                    JSONObject loObj = laGuides.getJSONObject(i);
                    EGuides loGuide = new EGuides();

                    loGuide.setsTransNox(loObj.getString("transNox"));
                    loGuide.setType(loObj.getString("type"));
                    loGuide.setsTitlexx(loObj.getString("title"));
                    loGuide.setsURlxx(lsFileDir + loObj.getString("link"));

                    dGuides.InsertGuides(loGuide);
                }
            }

            return true;

        }catch (Exception e){
            message = e.getMessage();
            return false;
        }
    }

    public Boolean DownloadPolicyMenus(){

        try {

            String lsResponse = WebClient.sendRequest(poApi.getUrlDownloadPolicyMenus(), new JSONObject().toString(), poHeaders.getHeaders());
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

            Log.d("JSON POLICY", lsResponse);
            JSONArray laMenus = new JSONArray(loResponse.getString("detail"));
            for (int ctr = 0; ctr < laMenus.length(); ctr++){

                JSONObject loMenu = laMenus.getJSONObject(ctr);

                EPolicyMenus loPolicyMenu = new EPolicyMenus();
                loPolicyMenu.setsTransNoxx(loMenu.getString("sTransNoxx"));
                loPolicyMenu.setsNamexx(loMenu.getString("sNamexx"));

                dCompanyPolicy.savePolicyMenus(loPolicyMenu);

            }
            return true;

        }catch (Exception e){
            message = e.getMessage();
            return false;
        }
    }

    public Boolean DownloadPolicySummaryDisciplinary(){

        try {

            String lsResponse = WebClient.sendRequest(poApi.getUrlDownloadPolicySummaryDisciplinary(), new JSONObject().toString(), poHeaders.getHeaders());
            if (lsResponse == null || lsResponse.isEmpty()){
                message = "No response from server";
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");

            Log.d("JSON POLICY", lsResponse);

            if (lsResult.equalsIgnoreCase("error")) {
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;
            }

            JSONArray laPolicies = new JSONArray(loResponse.getString("detail"));
            for (int ctr = 0; ctr < laPolicies.length(); ctr++){

                JSONObject loObj = laPolicies.getJSONObject(ctr);

                EPolicyContents loPolicyContents = new EPolicyContents();
                loPolicyContents.setsTransNoxx(loObj.getString("sTransNoxx"));
                loPolicyContents.setsParentIDxx(loObj.getString("parentIDxx"));
                loPolicyContents.setsContentxx(loObj.getString("content"));

                dCompanyPolicy.savePolicyContents(loPolicyContents);

            }
            return true;

        }catch (Exception e){
            message = e.getMessage();
            return false;
        }
    }

    public Boolean DownloadArticles(){

        try {

            String lsResponse = WebClient.sendRequest(poApi.getUrlDownloadArticle(), new JSONObject().toString(), poHeaders.getHeaders());
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

            JSONArray laArticleHead = new JSONArray(loResponse.getString("head"));
            Log.d("JSON POLICY HEAD", loResponse.getString("head"));
            for (int ctr = 0; ctr < laArticleHead.length(); ctr++){

                JSONObject loArticle = laArticleHead.getJSONObject(ctr);

                String sCodexx = loArticle.getString("image");

                EArticleHead loArticleHead = new EArticleHead();
                loArticleHead.setsCodexx(sCodexx.substring(0, 2).toUpperCase() +"00"+ sCodexx.substring(sCodexx.length() - 1));
                loArticleHead.setsTitle(loArticle.getString("title"));
                loArticleHead.setsDescription(loArticle.getString("description"));
                loArticleHead.setsImage(loArticle.getString("image"));
                loArticleHead.setsSubtitle(loArticle.getString("subtitle"));

                dArticle.saveArticleHead(loArticleHead);

            }
            Thread.sleep(1000);

            JSONArray laArticleItems = new JSONArray(loResponse.getString("detail"));
            Log.d("JSON POLICY DETAIL", lsResponse);
            for (int ctr = 0; ctr < laArticleItems.length(); ctr++){

                JSONObject loArticleDetail = laArticleItems.getJSONObject(ctr);

                EArticleDetails loArticleDetails = new EArticleDetails();
                loArticleDetails.setsCodexx(loArticleDetail.getString("sCodexx"));
                loArticleDetails.setsContentxx(loArticleDetail.getString("sItemxx"));

                dArticle.saveArticleDetails(loArticleDetails);
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
                return false;
            }

            Log.d("FILE UPLOAD", loUpload.toString());

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

            dGuides.InsertGuides(loGuide);

            return true;

        }catch (Exception e){
            message = e.getMessage();
            return false;
        }

    }

    public LiveData<List<EGuides>> GetGuides(){
        return dGuides.GetGuides();
    }
}
