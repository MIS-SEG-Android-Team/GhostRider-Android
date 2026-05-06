package org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.GCircle.Apps.BranchMonitoring.BranchMonitoring;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitDetail;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitChecklist;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.ImageFileCreator;
import org.rmj.g3appdriver.lib.Location.GmsLocationRetriever;
import org.rmj.g3appdriver.lib.Location.HmsLocationRetriever;
import org.rmj.g3appdriver.lib.Location.LocationRetriever;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMBranchVisit extends AndroidViewModel {

    private String lsMessage;

    @SuppressLint("StaticFieldLeak")
    private final Context loInstance;
    private final EmployeeSession poSession;
    private final ConnectionUtil poConnection;
    private final BranchMonitoring poSys;

    public interface OnImport {
        void OnLoad();
        void OnFinished(String fsMessage);
    }

    public interface OnSubmit{
        void OnLoad();
        void OnSuccess();
        void OnFailed(String fsMessage);
    }

    public interface OnSubmitImageCallback{
        void OnLoad(String fsTitlexx, String fsMessage);
        void OnSuccess(String fsTransnox);
        void OnFailed(String fsMessage);
    }

    public interface OnInitializeCameraListner {
        void OnInit();
        void OnSuccess(Intent intent, String[] args);
        void OnFailed(String message, Intent intent, String[] args);
        void OnError(String fsMessage);
    }

    public VMBranchVisit(@NonNull Application application) {
        super(application);

        loInstance = application;
        poSession = EmployeeSession.getInstance(application);
        poConnection = new ConnectionUtil(application);
        poSys = new BranchMonitoring(application);
    }

    public void SaveError(String fsSource, String fsMessage){
        poSys.SaveError(fsSource, fsMessage);
    }

    public void SaveNewDetail(String fsTransNox, String fsCategrID, String fsRemarks){
        poSys.SaveNewDetail(fsTransNox, fsCategrID, fsRemarks);
    }

    public void SubmitBranchVisit(EBranchVisitMaster foMaster, List<DBranchVisitDetail.BranchVisitDetail> faDetails, OnSubmit foListener){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foListener.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {
                Object[] result = new Object[2];

                if (!poConnection.isDeviceConnected()){
                    result[0] = false;
                    result[1] = poConnection.getMessage();
                    return result;
                }

                Object[] laRresult = poSys.SubmitBranchVisit(foMaster, faDetails);

                result[0] = laRresult[0];
                result[1] = laRresult[1];

                return result;
            }

            @Override
            public void OnPostExecute(Object object) {
                Object[] laResult = (Object[]) object;

                if (!(Boolean) laResult[0]){
                    foListener.OnFailed((String) laResult[1]);
                }else {
                    foListener.OnSuccess();
                }
            }
        });
    }

    public void UpdateRemarks(String fsRemarksx, String fsTransNox, String fsCategrID){
        poSys.UpdateRemarks(fsRemarksx, fsTransNox, fsCategrID);
    }

    public void ImportChecklist(OnImport foListener){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foListener.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {

                if (!poConnection.isDeviceConnected()){
                    return poConnection.getMessage();
                }

                if (!poSys.ImportChecklist()){
                    return poSys.GetMessage();
                }
                return "Checklist downloaded successfully";
            }

            @Override
            public void OnPostExecute(Object object) {

                String lsMessage = (String) object;
                foListener.OnFinished(lsMessage);
            }
        });
    }

    public void ImportMaster(String fsDfrom, String fsDto, OnImport foListener){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foListener.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {

                if (!poConnection.isDeviceConnected()){
                    return poConnection.getMessage();
                }

                if (!poSys.ImportBranchVisitMaster(fsDfrom, fsDto)){
                    return poSys.GetMessage();
                }

                return "Master downloaded successfully";
            }

            @Override
            public void OnPostExecute(Object object) {
                String lsMessage = (String) object;
                foListener.OnFinished(lsMessage);
            }
        });
    }

    public void ImportDetails(String fsTransNox, OnImport foListener){

        TaskExecutor.Execute(fsTransNox, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foListener.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {

                String lsTransNox = (String) args;

                if (!poConnection.isDeviceConnected()){
                    return poConnection.getMessage();
                }

                if (!poSys.ImportBranchVisitDetails(lsTransNox)){
                    return poSys.GetMessage();
                }
                return "Details downloaded successfully";
            }

            @Override
            public void OnPostExecute(Object object) {
                String lsMessage = (String) object;
                foListener.OnFinished(lsMessage);
            }
        });
    }

    public void ImportBranchVisitSelfie(String fsTransNox, OnImport foListener){

        TaskExecutor.Execute(fsTransNox, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foListener.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {

                String lsTransNox = (String) args;

                if (!poConnection.isDeviceConnected()){
                    return poConnection.getMessage();
                }

                if (!poSys.ImportBranchVisitSelfie(lsTransNox)){
                    return poSys.GetMessage();
                }
                return "Branch Visit Images downloaded successfully";
            }

            @Override
            public void OnPostExecute(Object object) {
                String lsMessage = (String) object;
                foListener.OnFinished(lsMessage);
            }
        });
    }

    public void InitCamera(String fsTransNox, String fsCategrID, OnInitializeCameraListner foListener){

        ImageFileCreator loImage = new ImageFileCreator(
                loInstance,
                AppConstants.SUB_FOLDER_BRANCH_VISIT  + "/" + fsCategrID +"/" + fsTransNox,
                poSession.getUserID()
        );

        String[] lsResult = new String[5];
        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foListener.OnInit();
            }

            @Override
            public Object DoInBackground(Object args) {

                //do not allow more than 2 images per category
                if (poSys.CountImagePerCategory(fsTransNox, fsCategrID, "BVS") >= 1){
                    lsMessage = "Only 1 image per category is allowed!";
                    return false;
                }

                //create image file
                if(!loImage.IsFileCreated(false)){
                    lsMessage = loImage.getMessage();
                    return false;
                }

                //return image result
                lsResult[0]= loImage.getFilePath(); //image path
                lsResult[1]= loImage.getFileName(); //image filename
                lsResult[2]= fsCategrID; //file code

                String lsCompx = android.os.Build.MANUFACTURER.toLowerCase();
                LocationRetriever.iLocationRetriever location;

                if (!lsCompx.equalsIgnoreCase("huawei")) {
                    location = new GmsLocationRetriever();
                } else {
                    location = new HmsLocationRetriever();
                }

                location.GetLocation(loInstance, new LocationRetriever.OnRetrieveLocationListener() {
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

                if (!(Boolean) object){
                    foListener.OnError(lsMessage);
                }
            }
        });

    }

    public void SaveImage(String fsTransNox, String fsFileName, String fsFilePath, String fsLongitude, String fsLatitude, String fsCategrID){
        poSys.SaveBranchVisitImage(fsTransNox, fsFileName, fsFilePath, fsLongitude, fsLatitude, fsCategrID);
    }

    public void SubmitImage(EImageInfo foImages, OnSubmitImageCallback foCallback){

        TaskExecutor.Execute(foImages, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foCallback.OnLoad("Branch Visit Image", "Uploading image. Please wait . . ");
            }

            @Override
            public Object DoInBackground(Object args) {

                Object[] result = new Object[2];

                if (!poConnection.isDeviceConnected()){
                    result[0] = false;
                    result[1] = poConnection.getMessage();
                    return result;
                }

                EImageInfo loImage = (EImageInfo) args;
                Object[] laRresult = poSys.SubmitImage(loImage);

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

    public EBranchInfo GetBranchName(String fsBranchCd){
        return poSys.GetBranch(fsBranchCd);
    }

    public String SaveNewMaster(String fsBranchCd){
        return poSys.SaveNewMaster(fsBranchCd);
    }

    public String GetCurrentDate(){
        return poSys.GetCurrentDate();
    }

    public LiveData<List<EBranchVisitChecklist>> GetChecklist(){
        return poSys.GetChecklist();
    }

    public EBranchVisitMaster GetEntryToday(){
        return poSys.GetEntryToday();
    }

    public LiveData<EBranchVisitMaster> GetMasterTransaction(String fsTransNox){
        return poSys.GetMasterTransaction(fsTransNox);
    }

    public LiveData<List<DBranchVisitMaster.MasterHistory>> GetHistory(String fsTransTat){
        return poSys.GetHistory(fsTransTat);
    }

    public LiveData<List<DBranchVisitDetail.BranchVisitDetail>> GetDetails(String fsTransNox){
        return poSys.GetDetails(fsTransNox);
    }

    public LiveData<List<EImageInfo>> GetTransactionImagesForUpload(String fsSourceNo, String fsSourceCD){
        return poSys.GetTransactionImagesForUpload(fsSourceNo, fsSourceCD);
    }
}
