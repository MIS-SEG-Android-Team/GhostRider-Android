package org.rmj.guanzongroup.evaluation.ViewModel.SSDD;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.GCircle.Apps.SSDD.SSDDEvaluation;
import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDCategories;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDepartments;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDetail;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.FileUtility;
import org.rmj.g3appdriver.etc.ImageFileCreator;
import org.rmj.g3appdriver.etc.OnInitializeCameraCallback;
import org.rmj.g3appdriver.lib.Location.GmsLocationRetriever;
import org.rmj.g3appdriver.lib.Location.HmsLocationRetriever;
import org.rmj.g3appdriver.lib.Location.LocationRetriever;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;
import org.rmj.guanzongroup.evaluation.Adapter.SSDD.Adapter_SSDDCategories;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VMSSDEvaluation extends AndroidViewModel {

    private String fsMessage;

    private final Context loContext;
    private final ConnectionUtil loConnect;
    private final SSDDEvaluation loEvaluation;
    private final EmployeeSession poSession;
    private final FileUtility loFile;

    public VMSSDEvaluation(@NonNull Application application) {
        super(application);

        this.loContext = application;
        this.loEvaluation = new SSDDEvaluation(application);
        this.loConnect = new ConnectionUtil(application);
        this.poSession = EmployeeSession.getInstance(application);
        this.loFile = new FileUtility(application);
    }

    public int CountDetails(String fsTransNox){
        return loEvaluation.CountDetails(fsTransNox);
    }

    public int CountDepartment(){
        return loEvaluation.CountDepartments();
    }

    public int CountMaster(String fsDeptIDxx){
        return loEvaluation.CountMasterByDepartment(fsDeptIDxx);
    }

    public Boolean IsFileExist(String fsPath){
        return loFile.IsFileExist(fsPath);
    }

    @SuppressLint("SimpleDateFormat")
    public String GetDateToday(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        }
    }

    @SuppressLint("SimpleDateFormat")
    public Boolean IsEvaluated(String fsdEvaluated) throws ParseException {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            LocalDate loDtoday = LocalDate.now();

            String lsdEvaluated = LocalDateTime.parse(fsdEvaluated, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate loDate = LocalDate.parse(lsdEvaluated, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            return loDtoday.isAfter(loDate);

        }else {

            Date loDtoday = Calendar.getInstance().getTime();

            String lsdEvaluated = new SimpleDateFormat("yyyy-MM-dd").format("yyyy-MM-dd HH:mm:ss");
            Date loDate = new SimpleDateFormat("yyyy-MM-dd").parse(lsdEvaluated);

            return loDtoday.after(loDate);

        }
    }

    public List<ESSDDCategories> GetCategoriesNonLive(){
        return loEvaluation.GetCategoriesNonLive();
    }

    public LiveData<List<ESSDDepartments>> GetDepartments(){
        return loEvaluation.GetDepartments();
    }

    public LiveData<List<ESSDDMaster>> GetMasterList(String dFrom, String dTo, String sDeptIDxx, String cTranStat){
        return loEvaluation.GetMasterList(dFrom, dTo, sDeptIDxx, cTranStat);
    }

    public LiveData<ESSDDMaster> GetMaster(String fsTransNox, String fsDeptIDxx) {
        return loEvaluation.GetMaster(fsTransNox, fsDeptIDxx);
    }

    public LiveData<List<ESSDDetail>> GetDetail(String fsTransNox) {
        return loEvaluation.GetDetail(fsTransNox);
    }

    public LiveData<ESSDDetail> GetCategoryDetail(String fsTransNox, String fsCategory){
        return loEvaluation.GetCategoryDetail(fsTransNox, fsCategory);
    }

    public LiveData<List<EImageInfo>> GetCategoryImages(String fsTransNox, String fsCategory){
        return loEvaluation.GetCategoryImages(fsTransNox, fsCategory);
    }

    public LiveData<List<EImageInfo>> GetTransactionImagesForUpload(String fsSourceNo){
        return loEvaluation.GetTransactionImagesForUpload(fsSourceNo);
    }

    public ESSDDCategories GetCategory(String fsCategory){
        return loEvaluation.GetCategory(fsCategory);
    }

    public ESSDDepartments GetDepartment(String fsDeptIDxx){
        return loEvaluation.GetDepartment(fsDeptIDxx);
    }

    public ESSDDMaster CreateEvaluation(String fsDeptID, List<ESSDDCategories> foCategories){
        return loEvaluation.CreateEvaluation(fsDeptID, foCategories);
    }

    public interface OnDownloadCallback{
        void OnLoad(String fsTitlexx, String fsMessage);
        void OnSuccess();
        void OnFailed(String fsMessage);
    }

    public interface OnSubmitCallback{
        void OnLoad(String fsTitlexx, String fsMessage);
        void OnSuccess(String fsTransnox);
        void OnFailed(String fsMessage);
    }
    
    public void SaveError(String fsSource, String fsMessage){
        loEvaluation.SaveError(fsSource, fsMessage);
    }

    public void Rate(String fsTransNox, String fsCatgrID, String fsRating, String fsRemarks, String fsDate){
        loEvaluation.Rate(fsTransNox, fsCatgrID, fsRating, fsRemarks, fsDate);
    }

    public void DownloadDepartments(OnDownloadCallback focallback){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                focallback.OnLoad("Downloading Departments", "Please wait");
            }

            @Override
            public Object DoInBackground(Object args) {

                if (!loConnect.isDeviceConnected()){
                    fsMessage = loConnect.getMessage();
                    return false;
                }

                if (!loEvaluation.DownloadDepartments()){
                    fsMessage = loEvaluation.GetMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {

                if ((Boolean) object){
                    focallback.OnSuccess();
                }else {
                    focallback.OnFailed(fsMessage);
                }
            }
        });
    }

    public void DownloadCategories(OnDownloadCallback focallback){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                focallback.OnLoad("Downloading Categories", "Please wait");
            }

            @Override
            public Object DoInBackground(Object args) {

                if (!loConnect.isDeviceConnected()){
                    fsMessage = loConnect.getMessage();
                    return false;
                }

                if (!loEvaluation.DownloadCategories()){
                    fsMessage = loEvaluation.GetMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {

                if ((Boolean) object){
                    focallback.OnSuccess();
                }else {
                    focallback.OnFailed(fsMessage);
                }
            }
        });
    }

    public void DownloadEvaluation(String fsDeptIDxx, String fsDfrom, String fsDto, OnDownloadCallback focallback){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                focallback.OnLoad("Downloading Evaluations", "Please wait");
            }

            @Override
            public Object DoInBackground(Object args) {

                if (!loConnect.isDeviceConnected()){
                    fsMessage = loConnect.getMessage();
                    return false;
                }

                //download previous transactions
                if (!loEvaluation.DownloadMasterList(fsDeptIDxx, fsDfrom, fsDto)){
                    fsMessage= loEvaluation.GetMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                if ((Boolean) object){
                    focallback.OnSuccess();
                }else {
                    focallback.OnFailed(fsMessage);
                }
            }
        });
    }

    public void DownloadEvaluationDetails(String fsTransnox, OnDownloadCallback focallback){

        TaskExecutor.Execute(fsTransnox, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                focallback.OnLoad("Downloading Details", "Please wait");
            }

            @Override
            public Object DoInBackground(Object args) {

                if (!loConnect.isDeviceConnected()){
                    fsMessage = loConnect.getMessage();
                    return false;
                }

                String lsPAram = (String) args;
                if (!loEvaluation.DownloadDetails(lsPAram)){
                    fsMessage = loEvaluation.GetMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                if ((Boolean) object){
                    focallback.OnSuccess();
                }else {
                    focallback.OnFailed(fsMessage);
                }
            }
        });
    }

    public void DownloadImageCategory(String fsTransNox, String fsCategrID, OnDownloadCallback callback){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnLoad("Downloading Images", "Please wait");
            }

            @Override
            public Object DoInBackground(Object args) {

                if (!loConnect.isDeviceConnected()){
                    fsMessage = loConnect.getMessage();
                    return false;
                }

                if (!loEvaluation.DownloadImageCategory(fsTransNox, fsCategrID)){
                    fsMessage = loEvaluation.GetMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                if ((Boolean) object){
                    callback.OnSuccess();
                }else {
                    callback.OnFailed(fsMessage);
                }
            }
        });
    }

    public void SubmitEvaluation(ESSDDMaster foMaster, List<ESSDDetail> faDetails, OnSubmitCallback foCallback){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foCallback.OnLoad("SSDD Evaluation", "Uploading evaluations. Please wait . . ");
            }

            @Override
            public Object DoInBackground(Object args) {

                Object[] result = new Object[2];

                if (!loConnect.isDeviceConnected()){
                    result[0] = false;
                    result[1] = loConnect.getMessage();
                    return result;
                }

                Object[] laRresult = loEvaluation.SubmitEvaluation(foMaster, faDetails);

                result[0] = laRresult[0];
                result[1] = laRresult[1];

                return result;
            }

            @Override
            public void OnPostExecute(Object object) {
                Object[] laResult = (Object[]) object;

                if (!(Boolean) laResult[0]){
                    foCallback.OnFailed((String) laResult[1]);
                }else {
                    foCallback.OnSuccess((String) laResult[1]);
                }
            }
        });
    }

    public void SaveImage(String fsTransNox, String fsFileName, String fsFilePath, String fsLongitude, String fsLatitude, String fsCategrID){
        loEvaluation.SaveSSDDImage(fsTransNox, fsFileName, fsFilePath, fsLongitude, fsLatitude, fsCategrID);
    }

    public void SubmitImage(EImageInfo foImages, OnSubmitCallback foCallback){

        TaskExecutor.Execute(foImages, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foCallback.OnLoad("SSDD Image", "Uploading image. Please wait . . ");
            }

            @Override
            public Object DoInBackground(Object args) {

                Object[] result = new Object[2];

                if (!loConnect.isDeviceConnected()){
                    result[0] = false;
                    result[1] = loConnect.getMessage();
                    return result;
                }

                EImageInfo loImage = (EImageInfo) args;
                Object[] laRresult = loEvaluation.SubmitImage(loImage);

                result[0] = laRresult[0];
                result[1] = laRresult[1];

                return result;
            }

            @Override
            public void OnPostExecute(Object object) {
                Object[] laResult = (Object[]) object;

                if (!(Boolean) laResult[0]){
                    foCallback.OnFailed((String) laResult[1]);
                }else {
                    foCallback.OnSuccess((String) laResult[1]);
                }
            }
        });
    }

    public void UpdateMasterStatus(String fsTranStat, String fsTransNox){
        loEvaluation.UpdateMasterStatus(fsTranStat, fsTransNox);
    }

    public void InitCamera(String fsRefernox, Adapter_SSDDCategories.SSDD_Evaluation_Categories foCategories, OnInitializeCameraCallback foListener){

        ImageFileCreator loImage = new ImageFileCreator(loContext, AppConstants.SUB_FOLDER_SSDD_EVALUATION  + "/" + fsRefernox +"/" + foCategories.sCategryID, poSession.getUserID() );

        String[] lsResult = new String[5];
        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foListener.OnInit();
            }

            @Override
            public Object DoInBackground(Object args) {

                //do not allow more than 2 images per category
                if (loEvaluation.CountImagePerCategory(fsRefernox, foCategories.sCategryID) >= 2){
                    fsMessage = "Only 2 images per category is allowed for evaluation";
                    return false;
                }

                //create image file
                if(!loImage.IsFileCreated(false)){
                    fsMessage = loImage.getMessage();
                    return false;
                }

                //return image result
                lsResult[0]= loImage.getFilePath(); //image path
                lsResult[1]= loImage.getFileName(); //image filename
                lsResult[2]= foCategories.sCategryID; //image filename

                String lsCompx = android.os.Build.MANUFACTURER.toLowerCase();
                LocationRetriever.iLocationRetriever location;

                if (!lsCompx.equalsIgnoreCase("huawei")) {
                    location = new GmsLocationRetriever();
                } else {
                    location = new HmsLocationRetriever();
                }

                location.GetLocation(loContext, new LocationRetriever.OnRetrieveLocationListener() {
                    @Override
                    public void OnRetrieve(String latitude, String longitude) {
                        lsResult[3] = latitude;
                        lsResult[4] = longitude;

                        Intent loIntent = loImage.getCameraIntent();
                        loIntent.putExtra("result", true);

                        foListener.OnSuccess(loIntent, lsResult);
                    }

                    @Override
                    public void OnFailed(String message, String latitude, String longitude) {
                        lsResult[3] = latitude;
                        lsResult[4] = longitude;

                        Intent loIntent = loImage.getCameraIntent();
                        loIntent.putExtra("result", false);

                        foListener.OnFailed(message, loIntent, lsResult);
                    }
                });

                return true;
            }

            @Override
            public void OnPostExecute(Object object) {

            }
        });
    }
}
