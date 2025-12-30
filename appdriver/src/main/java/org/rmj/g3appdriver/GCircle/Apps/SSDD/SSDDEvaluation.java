package org.rmj.g3appdriver.GCircle.Apps.SSDD;

import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DSSDDCategories;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DSSDDepartment;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDCategories;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDepartments;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Api.WebClient;

import java.util.List;

public class SSDDEvaluation {

    private String lsMessage;

    private final GCircleApi poApi;
    private final HttpHeaders poHeaders;
    private final DSSDDepartment poDeptDao;
    private final DSSDDCategories poCatgDao;

    public SSDDEvaluation(Application poApp){
        this.poApi = new GCircleApi(poApp);
        this.poHeaders = HttpHeaders.getInstance(poApp);
        this.poDeptDao = GGC_GCircleDB.getInstance(poApp).ssdDepartmentDao();
        this.poCatgDao = GGC_GCircleDB.getInstance(poApp).ssdCategoriesDao();
    }

    public String GetMessage(){
        return lsMessage;
    }

    public LiveData<List<ESSDDepartments>> GetDepartments(){
        return poDeptDao.GetDepartmentList();
    }

    public LiveData<List<ESSDDCategories>> GetCategories(){
        return poCatgDao.GetCategories();
    }

    public Boolean DownloadDepartments(){

        try {

            String lsResponse = WebClient.sendRequest(poApi.getUrlSSDDepartments(), new JSONObject().toString(), poHeaders.getHeaders());
            if (lsResponse == null){
                lsMessage = "Server no response";
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");

            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                lsMessage = getErrorMessage(loError);;
                return false;
            }

            JSONArray laDetail = loResponse.getJSONArray("detail");
            for (int i = 0; i < laDetail.length(); i++) {

                JSONObject loResult = laDetail.getJSONObject(i);

                ESSDDepartments loDept = new ESSDDepartments();
                loDept.setsDeptIDxx(loResult.getString("sDeptIDxx"));
                loDept.setsDescript(loResult.getString("sDescript"));

                poDeptDao.Save(loDept);
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Boolean DownloadCategories(){

        try {

            String lsResponse = WebClient.sendRequest(poApi.getUrlSSDDepartments(), new JSONObject().toString(), poHeaders.getHeaders());
            if (lsResponse == null){
                lsMessage = "Server no response";
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");

            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                lsMessage = getErrorMessage(loError);;
                return false;
            }

            JSONArray laDetail = loResponse.getJSONArray("detail");
            for (int i = 0; i < laDetail.length(); i++) {

                JSONObject loResult = laDetail.getJSONObject(i);

                ESSDDCategories loCatg = new ESSDDCategories();
                loCatg.setsCategrID(loResult.getString("sCategrID"));
                loCatg.setsDescript(loResult.getString("sDescript"));
                loCatg.setsMemoLink(loResult.getString("sMemoLink"));
                loCatg.setnPageNumber(loResult.getString("nPageNumber"));

                poCatgDao.Save(loCatg);
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
