package org.rmj.g3appdriver.GCircle.Apps.SSDD;

import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DErrorLogs;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DImageInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DSSDDCategories;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DSSDDMaster;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DSSDDepartment;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DSSDDetail;
import org.rmj.g3appdriver.GCircle.room.Entities.EErrorLogs;
import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDCategories;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDepartments;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDetail;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.GCircle.room.Repositories.RImageInfo;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.etc.AppConstants;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SSDDEvaluation {

    private String lsMessage;

    private final EmployeeSession poSession;
    private final EmployeeMaster poEmployee;
    private final GCircleApi poApi;
    private final HttpHeaders poHeaders;
    private final RImageInfo poImage;

    private final DSSDDepartment poDeptDao;
    private final DSSDDCategories poCatgDao;
    private final DSSDDMaster poMasterDao;
    private final DSSDDetail poDetailDao;
    private final DImageInfo poImageDao;
    private final DErrorLogs poErrorDao;

    public SSDDEvaluation(Application poApp){

        this.poSession = EmployeeSession.getInstance(poApp);
        this.poEmployee = new EmployeeMaster(poApp);
        this.poApi = new GCircleApi(poApp);
        this.poHeaders = HttpHeaders.getInstance(poApp);

        this.poImage = new RImageInfo(poApp);

        this.poDeptDao = GGC_GCircleDB.getInstance(poApp).ssdDepartmentDao();
        this.poCatgDao = GGC_GCircleDB.getInstance(poApp).ssdCategoriesDao();
        this.poMasterDao = GGC_GCircleDB.getInstance(poApp).ssdMasterDao();
        this.poDetailDao = GGC_GCircleDB.getInstance(poApp).ssdDetailDao();
        this.poImageDao = GGC_GCircleDB.getInstance(poApp).ImageInfoDao();
        this.poErrorDao = GGC_GCircleDB.getInstance(poApp).errorLogsDao();
    }

    public void SaveError(String fsSource, String fsMessage){

        EErrorLogs logs = new EErrorLogs();
        logs.setnErrorLogID(poErrorDao.GetErrorLogCount() + 1);
        logs.setsMessagex(fsMessage);
        logs.setdLogDate(GetCurrentDateTime());
        logs.setsSourceTransNo(fsSource);
        logs.setcRead("0");

        poErrorDao.SaveErrorLogs(logs);
    }

    public void Rate(String fsTransNox, String fsCatgrID, String fsRating, String fsRemarks, String fsDate){
        poDetailDao.Rate(fsTransNox, fsCatgrID, fsRating, fsRemarks, fsDate);
    }

    public void SaveSSDDImage(String fsTransNox, String fsFileName, String fsFilePath, String fsLongitude, String fsLatitude, String fsCategrID){
        poImage.SaveSSDDImage(fsTransNox, fsFileName, fsFilePath, fsLongitude, fsLatitude, fsCategrID);
    }

    public void UpdateMasterStatus(String fsTranStat, String fsTransNox){
        poMasterDao.UpdateMasterStatus(fsTranStat, fsTransNox);
    }

    public int CountDetails(String fsTransNox){
        return poDetailDao.CountDetails(fsTransNox);
    }

    public int CountDepartments(){
        return poDeptDao.GetCount();
    }

    public int CountMasterByDepartment(String fsTransNox){
        return poMasterDao.GetCountByDepartment(fsTransNox);
    }

    public int CountImagePerCategory(String fsRefernox, String fsCategrID){
        return poImageDao.CountImagePerCategory(fsRefernox, fsCategrID);
    }

    private String GenerateTransNox(){

        String lsTransNox = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lsTransNox = "MX01" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                    poMasterDao.GetCount() + 1;
        }else {
            lsTransNox = "MX01" +
                    new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Calendar.getInstance().getTime()) +
                    poMasterDao.GetCount() + 1;
        }

        return lsTransNox;
    }

    private String GetCurrentDateTime(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        }
    }

    private String GetCurrentDate(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }else {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        }
    }

    public ESSDDMaster CreateEvaluation(String fsDeptID, List<ESSDDCategories> foCategories){

        for (ESSDDCategories loCategory : foCategories){

            ESSDDetail loDetail = new ESSDDetail();
            loDetail.setsTransNox(GenerateTransNox());
            loDetail.setsCategrID(loCategory.getsCategrID());
            loDetail.setnRatingxx("0.0");
            loDetail.setdEvaluate("");
            loDetail.setsRemarksx("");

            poDetailDao.Save(loDetail);
        }

        String lsTransNox = GenerateTransNox();

        ESSDDMaster loMaster = new ESSDDMaster();
        loMaster.setsTransNox(lsTransNox);
        loMaster.setdTransact(GetCurrentDate());
        loMaster.setsDeptIDxx(fsDeptID);
        loMaster.setcTranStat("0");
        loMaster.setcSendStat("0");

        poMasterDao.Save(loMaster);

        return loMaster;
    }

    public String GetMessage(){
        return lsMessage;
    }

    public List<ESSDDCategories> GetCategoriesNonLive(){
        return poCatgDao.GetCategoriesNonLive();
    }

    public LiveData<List<ESSDDepartments>> GetDepartments(){
        return poDeptDao.GetDepartmentList();
    }

    public LiveData<List<ESSDDMaster>> GetMasterList(String dFrom, String dTo, String sDeptIDxx, String cTranStat){

        SimpleSQLiteQuery lsQuery = new SimpleSQLiteQuery(
                "SELECT * FROM SSDD_Master WHERE dTransact BETWEEN '" + dFrom + "' AND '" + dTo + "' AND " +
                "sDeptIDxx = '" + sDeptIDxx + "' AND " + cTranStat);
        return poMasterDao.GetMasterList(lsQuery);
    }

    public LiveData<ESSDDMaster> GetMaster(String fsTransNox, String fsDeptIDxx) {
        return poMasterDao.GetMaster(fsTransNox, fsDeptIDxx);
    }

    public LiveData<List<ESSDDetail>> GetDetail(String fsTransNox) {
        return poDetailDao.GetDetails(fsTransNox);
    }

    public LiveData<ESSDDetail> GetCategoryDetail(String fsTransNox, String fsCategory){
        return poDetailDao.GetCategoryDetail(fsTransNox, fsCategory);
    }

    public LiveData<List<EImageInfo>> GetCategoryImages(String fsTransNox, String fsCategory){
        return poImageDao.GetSSDDImagesPerCategory(fsTransNox, fsCategory);
    }

    public LiveData<List<EImageInfo>> GetTransactionImagesForUpload(String fsSourceNo){
        return poImageDao.GetTransactionImagesForUpload(fsSourceNo);
    }

    public ESSDDCategories GetCategory(String fsCategory){
        return poCatgDao.GetCategory(fsCategory);
    }

    public ESSDDepartments GetDepartment(String fsDeptIDxx){
        return poDeptDao.GetDepartment(fsDeptIDxx);
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
            lsMessage= e.getMessage();
            return false;
        }
    }

    public Boolean DownloadCategories(){

        try {

            String lsResponse = WebClient.sendRequest(poApi.getUrlSSDCategories(), new JSONObject().toString(), poHeaders.getHeaders());
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
            lsMessage= e.getMessage();
            return false;
        }
    }

    public Boolean DownloadMasterList(String fsDeptIDxx, String fsDfrom, String fsDto){

        try {

            JSONObject loParams = new JSONObject();
            loParams.put("sUserIDxx", poEmployee.getUserNonLiveData().getUserIDxx());
            loParams.put("sDeptIDxx", fsDeptIDxx);
            loParams.put("dFrom", fsDfrom);
            loParams.put("dTo", fsDto);

            String lsResponse = WebClient.sendRequest(poApi.getUrlSSDMaster(), loParams.toString(), poHeaders.getHeaders());
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

                ESSDDMaster loMaster = new ESSDDMaster();
                loMaster.setsTransNox(loResult.getString("sTransNox"));
                loMaster.setdTransact(loResult.getString("dTransact"));
                loMaster.setcTranStat(loResult.getString("cTranStat"));
                loMaster.setcSendStat("1");
                loMaster.setsDeptIDxx(loResult.getString("sDeptIDxx"));

                poMasterDao.Save(loMaster);
            }

            return true;
        }catch (Exception e){
            lsMessage= e.getMessage();
            return false;
        }
    }

    public Boolean DownloadDetails(String fsTransnox){

        try {

            JSONObject loParams = new JSONObject();
            loParams.put("sTransNox", fsTransnox);

            String lsResponse = WebClient.sendRequest(poApi.getUrlSSDetails(),loParams.toString(), poHeaders.getHeaders());
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

                ESSDDetail loDetail = new ESSDDetail();
                loDetail.setsTransNox(fsTransnox);
                loDetail.setsCategrID(loResult.getString("sCategrID"));
                loDetail.setnRatingxx(loResult.getString("nRatingxx"));
                loDetail.setsRemarksx(loResult.getString("sRemarksx"));
                loDetail.setdEvaluate(loResult.getString("dEvaluate"));

                poDetailDao.Save(loDetail);
            }

            return true;
        }catch (Exception e){
            lsMessage = e.getMessage();
            return false;
        }
    }

    public Boolean DownloadImageCategory(String fsTransNox, String fsCategrID){

        try {

            JSONObject loParams = new JSONObject();
            loParams.put("sTransNox", fsTransNox);
            loParams.put("sCategrID", fsCategrID);

            String lsResponse = WebClient.sendRequest(poApi.getUrlSSDImageCategory(),loParams.toString(), poHeaders.getHeaders());
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

                EImageInfo loDetail = new EImageInfo();
                loDetail.setTransNox(loResult.getString("sTransNox"));
                loDetail.setsClientID(loResult.getString("sClientID"));
                loDetail.setDtlSrcNo(loResult.getString("sUserIDxx"));
                loDetail.setFileCode(fsCategrID);
                loDetail.setImageNme(loResult.getString("sImageNme"));
                loDetail.setFileLoct(loResult.getString("sImagePth"));
                loDetail.setSourceNo(fsTransNox);
                loDetail.setSourceCD("SSDD");
                loDetail.setSendStat("1");
                loDetail.setCaptured(loResult.getString("dImgeDate"));
                loDetail.setMD5Hashx(loResult.getString("sMD5Hashx"));
                loDetail.setSendDate(loResult.getString("dTimeStmp"));
                loDetail.setLongitud("0.0");
                loDetail.setLatitude("0.0");

                poImageDao.SaveImageInfo(loDetail);
            }

            return true;
        }catch (Exception e){
            lsMessage = e.getMessage();
            return false;
        }
    }

    public Boolean DownloadImageFile(String fsTransNox){

        if (!poImage.DownloadImageFile(fsTransNox)){
            lsMessage = poImage.getMessage();
            return false;
        }
        return true;
    }

    public Boolean SubmitSSDDImagesForUpload(){

        try {

            if (poImageDao.GetSSDDImagesForUpload() == null || poImageDao.GetSSDDImagesForUpload().size() < 1){
                lsMessage = "No SSDD image to upload";
                return false;
            }

            boolean isSuccess = true;

            List<EImageInfo> laImages =  poImageDao.GetSSDDImagesForUpload();
            for (EImageInfo loImage : laImages){

                String lsOldTransnox = loImage.getTransNox();

                //if uploading successful,
                String lsImageID = poImage.UploadImage(loImage.getTransNox());
                if (lsImageID != null && !lsImageID.isEmpty()){

                    Thread.sleep(1000);

                    JSONObject loParams = new JSONObject();
                    loParams.put("sTransNox", lsImageID);
                    loParams.put("sSourceNo", lsOldTransnox);

                    String lsResponse = WebClient.sendRequest(poApi.getUrlUpdateSSDDTransaction(), loParams.toString(), poHeaders.getHeaders());
                    if (lsResponse == null){
                        lsMessage = "Server no response";
                        isSuccess = false;
                        break;
                    }
                    Log.d("SSDDEvaluation", lsResponse);

                    JSONObject loResponse = new JSONObject(lsResponse);
                    String lsResult = loResponse.getString("result");

                    if(lsResult.equalsIgnoreCase("error")){
                        JSONObject loError = loResponse.getJSONObject("error");
                        lsMessage = getErrorMessage(loError);;
                        isSuccess = false;
                        break;
                    }
                    if (loResponse.getString("sTransNox") == null || loResponse.getString("sTransNox").isEmpty()){
                        isSuccess = false;
                        break;
                    }

                    //update send status, after update of transaction number
                    loImage.setSendStat("1");
                    loImage.setSendDate(AppConstants.DATE_MODIFIED());

                    poImageDao.update(loImage);

                    //update transaction number
                    poImageDao.UpdateTransNox(lsImageID, lsOldTransnox);

                    Thread.sleep(1000);
                }
            }
            return isSuccess;

        }catch (Exception e){
            lsMessage = e.getMessage();
            return false;
        }
    }

    public Object[] SubmitEvaluation(ESSDDMaster foMaster, List<ESSDDetail> faDetails){

        Object[] result = new Object[2];
        try {

            JSONObject loParams = new JSONObject();
            loParams.put("sTransNox", foMaster.getsTransNox());
            loParams.put("dTransact", foMaster.getdTransact());
            loParams.put("sDeptIDxx", foMaster.getsDeptIDxx());
            loParams.put("cTranStat", foMaster.getcTranStat());

            JSONArray laDetails = new JSONArray();
            for (ESSDDetail loDetail : faDetails){

                JSONObject loParam = new JSONObject();
                loParam.put("sCategrID", loDetail.getsCategrID());
                loParam.put("sDescript", GetCategory(loDetail.getsCategrID()).getsDescript());
                loParam.put("nRatingxx", loDetail.getnRatingxx());
                loParam.put("sRemarksx", loDetail.getsRemarksx());
                loParam.put("dEvaluate", loDetail.getdEvaluate());
                loParam.put("dEvaluate", loDetail.getdEvaluate());

                laDetails.put(loParam);
            }
            loParams.put("sPayloadxx", laDetails);

            String lsResponse = WebClient.sendRequest(poApi.getUrlSubmitEvaluation(), loParams.toString(), poHeaders.getHeaders());
            if (lsResponse == null){
                lsMessage = "Server no response";
                result[0] = false;
                result[1] = lsMessage;
                return result;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");

            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                lsMessage = getErrorMessage(loError);;
                result[0] = false;
                result[1] = lsMessage;
                return result;
            }

            String lsTransNox = loResponse.getString("sTransNox");

            //update master transaction no
            poMasterDao.Submit(lsTransNox, foMaster.getsTransNox());

            //update detail transaction no
            poDetailDao.Submit(lsTransNox, foMaster.getsTransNox());

            //update image reference no
            if (poImageDao.GetCountPerTransaction(foMaster.getsTransNox()) > 0){
                poImageDao.UpdateSourceNo(lsTransNox, foMaster.getsTransNox());
            }

            result[0] = true;
            result[1] = lsTransNox;
            return result;
        }catch (Exception e){
            lsMessage = e.getMessage();
            result[0] = false;
            result[1] = lsMessage;
            return result;
        }
    }

    public Object[] SubmitImage(EImageInfo foImages){

        Object[] result= new Object[2];
        try {

            JSONObject loParams = new JSONObject();

            //if uploading successful, set transaction number as parameter for uploading image
            String lsImageID = poImage.UploadImage(foImages.getTransNox());
            if (lsImageID != null && !lsImageID.isEmpty()){
                loParams.put("sTransNox", lsImageID);
            }

            //initialize other parameters
            loParams.put("sClientID", poSession.getClientId());
            loParams.put("sReferNox", foImages.getSourceNo());
            loParams.put("sCategrID", foImages.getFileCode());
            loParams.put("sScanndID", "");
            loParams.put("sImageNme", foImages.getImageNme());
            loParams.put("sMD5Hashx", foImages.getMD5Hashx());
            loParams.put("sImagePth", foImages.getFileLoct());
            loParams.put("dImgeDate", foImages.getCaptured());
            loParams.put("dImgeDate", foImages.getCaptured());

            //upload image details
            String lsResponse = WebClient.sendRequest(poApi.getUrlSubmitSSDDImage(), loParams.toString(), poHeaders.getHeaders());
            if (lsResponse == null){
                lsMessage = "Server no response";

                result[0] = false;
                result[1] = lsMessage;
                return result;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");

            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                lsMessage = getErrorMessage(loError);;

                result[0] = false;
                result[1] = lsMessage;
                return result;
            }

            String lsTransNox = loResponse.getString("sTransNox");
            poImageDao.UpdateTransNox(lsTransNox, foImages.getTransNox());

            result[0] = true;
            result[1] = lsTransNox;
            return result;
        }catch (Exception e){
            lsMessage = e.getMessage();

            result[0] = false;
            result[1] = lsMessage;
            return result;
        }
    }
}
