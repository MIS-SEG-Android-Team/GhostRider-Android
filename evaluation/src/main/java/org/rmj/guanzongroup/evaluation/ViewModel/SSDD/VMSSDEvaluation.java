package org.rmj.guanzongroup.evaluation.ViewModel.SSDD;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.GCircle.Apps.ErrorList.ViewModel.VMErrorLogs;
import org.rmj.g3appdriver.GCircle.Apps.SSDD.SSDDEvaluation;
import org.rmj.g3appdriver.GCircle.room.Entities.EErrorLogs;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDCategories;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDImages;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDepartments;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDetail;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.ImageFileCreator;
import org.rmj.g3appdriver.etc.OnInitializeCameraCallback;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

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

    public VMSSDEvaluation(@NonNull Application application) {
        super(application);

        this.loContext = application;
        this.loEvaluation = new SSDDEvaluation(application);
        this.loConnect = new ConnectionUtil(application);
        this.poSession = EmployeeSession.getInstance(application);
    }

    public int CountDetails(String fsTransNox){
        return loEvaluation.CountDetails(fsTransNox);
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

    public ESSDDCategories GetCategory(String fsCategory){
        return loEvaluation.GetCategory(fsCategory);
    }

    public ESSDDepartments GetDepartment(String fsDeptIDxx){
        return loEvaluation.GetDepartment(fsDeptIDxx);
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

    public LiveData<List<ESSDDImages>> GetCategoryImages(String fsTransNox, String fsCategory){
        return loEvaluation.GetCategoryImages(fsTransNox, fsCategory);
    }

    public LiveData<ESSDDImages> GetImage(String fsTransNox, String fsCategory){
        return loEvaluation.GetImage(fsTransNox, fsCategory);
    }

    public String CreateEvaluation(String fsDeptID, List<ESSDDCategories> foCategories){
        return loEvaluation.CreateEvaluation(fsDeptID, foCategories);
    }

    public interface OnDownloadCallback{
        void OnLoad(String fsTitlexx, String fsMessage);
        void OnSuccess();
        void OnFailed(String fsMessage);
    }

    public interface OnSubmitCallback{
        void OnLoad(String fsTitlexx, String fsMessage);
        void OnSuccess(String fsTransNox);
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
                if (!loEvaluation.DownloadMaster(fsDeptIDxx, fsDfrom, fsDto)){
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

    public void SubmitEvaluation(ESSDDMaster foMaster, List<ESSDDetail> faDetails, OnSubmitCallback foCallback){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foCallback.OnLoad("SSDD Evaluation", "Uploading evaluations. Please wait . . ");
            }

            @Override
            public Object DoInBackground(Object args) {

                if (!loConnect.isDeviceConnected()){
                    fsMessage = loConnect.getMessage();
                    return false;
                }

                if (!loEvaluation.SubmitEvaluation(foMaster, faDetails)){
                    fsMessage = loEvaluation.GetMessage();
                    return false;
                }
                //new transaction number
                fsMessage = loEvaluation.GetMessage();
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                if ((Boolean) object){
                    foCallback.OnSuccess(fsMessage);
                }else {
                    foCallback.OnFailed(fsMessage);
                }
            }
        });
    }

    public void SaveImage(ESSDDImages foImage){
        loEvaluation.SaveImage(foImage);
    }

    public void InitCamera(OnInitializeCameraCallback foListener){

        ImageFileCreator loImage = new ImageFileCreator(loContext, AppConstants.SUB_FOLDER_SSDD_EVALUATION, poSession.getUserID());

        String[] lsResult = new String[4];
        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foListener.OnInit();
            }

            @Override
            public Object DoInBackground(Object args) {

                if(!loImage.IsFileCreated(true)){
                    fsMessage = loImage.getMessage();
                    return false;
                }

                lsResult[0]= loImage.getFilePath();
                lsResult[1]= loImage.getFileName();

                return true;
            }

            @Override
            public void OnPostExecute(Object object) {

                if (!(Boolean) object){
                    foListener.OnFailed(loImage.getMessage(), null, lsResult);
                }else {
                    foListener.OnSuccess(loImage.getCameraIntent(), lsResult);
                }
            }
        });
    }
}
