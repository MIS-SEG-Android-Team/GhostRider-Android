package org.guanzongroup.com.creditevaluation.ViewModel;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditOnlineApplicationCI;
import org.rmj.g3appdriver.GCircle.room.Entities.EOccupationInfo;
import org.rmj.g3appdriver.GCircle.room.Repositories.ROccupation;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.lib.Location.LocationRetriever;
import org.rmj.g3appdriver.GCircle.Apps.CreditInvestigator.pojo.BarangayRecord;
import org.rmj.g3appdriver.GCircle.Apps.CreditInvestigator.pojo.CIImage;
import org.rmj.g3appdriver.GCircle.Apps.CreditInvestigator.Obj.CITagging;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.etc.ImageFileCreator;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMEvaluation extends AndroidViewModel {
    private static final String TAG = VMEvaluation.class.getSimpleName();

    private final Application instance;

    private final CITagging poSys;
    private final ROccupation poJob;

    private final ConnectionUtil poConn;

    private String message;

    public interface OnValidateTaggingResult{
        void OnValid();
        void OnInvalid(String message);
    }

    public interface OnSaveCIResultListener {
        void OnSuccess();
        void OnError(String message);
    }

    public interface OnSaveAddressResult {
        void OnSuccess(String args, String args1, String args2);
        void OnError(String message);
    }

    public interface OnUpdateOtherDetail{
        void OnSuccess();
        void OnError(String message);
    }

    public interface OnUploadResultListener{
        void OnUpload();
        void OnSuccess();
        void OnFailed(String message);
    }

    public interface OnPostRecommendationCallback{
        void OnPost();
        void OnSuccess();
        void OnFailed(String message);
    }

    public VMEvaluation(@NonNull Application application) {
        super(application);
        this.instance = application;
        this.poSys = new CITagging(application);
        this.poJob = new ROccupation(application);
        this.poConn = new ConnectionUtil(application);
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> GetUserInfo(){
        return poSys.GetUserInfo();
    }

    public LiveData<ECreditOnlineApplicationCI> GetApplicationDetail(String fsVal){
        return poSys.GetApplicationDetail(fsVal);
    }

    public LiveData<List<EOccupationInfo>> GetOccupationList(){
        return poJob.getAllOccupationInfo();
    }

    public void ValidateTagging(String TransNox, String fsKeyxx, List<String> foList, OnValidateTaggingResult listener){
        TaskExecutor.Execute(foList, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                if(!poSys.ValidateTagging(TransNox, fsKeyxx, (List<String>) args)){
                    message = poSys.getMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(!isSuccess){
                    listener.OnInvalid(message);
                } else {
                    listener.OnValid();
                }
            }
        });
    }

    public void SaveCIResult(String args, String fsPar, String fsKey, String fsRes, List<String> foList, OnSaveCIResultListener listener){
        String[] params = {args, fsPar, fsKey, fsRes};
        TaskExecutor.Execute(params, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                try{
                    String[] array = (String[]) args;
                    String TransNox = array[0];
                    String Parentxx = array[1];
                    String KeyNamex = array[2];
                    String Resultxx = array[3];


                    if (!poSys.SaveCIResult(TransNox, Parentxx, KeyNamex, Resultxx)) {
                        message = poSys.getMessage();
                        return false;
                    }

                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    message = e.getMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(!isSuccess){
                    listener.OnError(message);
                } else {
                    listener.OnSuccess();
                }
            }
        });
    }

    public void SaveBarangayRecord(BarangayRecord foVal, OnUpdateOtherDetail callback){
        TaskExecutor.Execute(foVal, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                try{
                    if(!poSys.UpdateBarangayRecord((BarangayRecord) args)){
                        message = poSys.getMessage();
                        return false;
                    }

                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    message = e.getMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(!isSuccess){
                    callback.OnError(message);
                } else {
                    callback.OnSuccess();
                }
            }
        });
    }

    public void UpdateOtherDetail(String args, String args1, String args2, OnUpdateOtherDetail callback){
        String[] array = {args, args1, args2};
        TaskExecutor.Execute(array, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                try {
                    String[] vals = (String[]) args;

                    String lsTransN = vals[0];
                    String lsUpdate = vals[1];
                    String lsValuex = vals[2];

                    switch (lsUpdate) {
                        case "n1":
                            poSys.UpdateNeighbor1(lsTransN, lsValuex);
                            break;
                        case "n2":
                            poSys.UpdateNeighbor2(lsTransN, lsValuex);
                            break;
                        case "n3":
                            poSys.UpdateNeighbor3(lsTransN, lsValuex);
                            break;
                    }
                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    message = e.getMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(!isSuccess){
                    callback.OnError(message);
                } else {
                    callback.OnSuccess();
                }
            }
        });
    }

    public void UploadResult(String args, OnUploadResultListener listener){
        TaskExecutor.Execute(args, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                listener.OnUpload();
            }

            @Override
            public Object DoInBackground(Object args) {
                try{
                    if(!poConn.isDeviceConnected()){
                        message = poConn.getMessage();
                        return false;
                    }

                    if(!poSys.UploadEvaluationResult((String) args)){
                        message = poSys.getMessage();
                        return false;
                    }

                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    message = e.getMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(!isSuccess){
                    listener.OnFailed(message);
                } else {
                    listener.OnSuccess();
                }
            }
        });
    }

    public void SaveRecommendation(String TransNox, String cApprove, String sRemarks, OnPostRecommendationCallback callback){
        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnPost();
            }

            @Override
            public Object DoInBackground(Object args) {
                try{
                    if(!poSys.SaveCIApproval(TransNox, cApprove, sRemarks)){
                        message = poSys.getMessage();
                        return false;
                    }

                    if(!poConn.isDeviceConnected()){
                        message = poConn.getMessage();
                        return false;
                    }

                    if(!poSys.PostCIApproval(TransNox)){
                        message = poSys.getMessage();
                        return false;
                    }
                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    message = e.getMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(!isSuccess){
                    callback.OnFailed(message);
                } else {
                    callback.OnSuccess();
                }
            }
        });
    }

    public void InitCameraLaunch(Activity activity, String TransNox, OnInitializeCameraCallback callback){
        ImageFileCreator loImage = new ImageFileCreator(instance, AppConstants.SUB_FOLDER_CI_ADDRESS, TransNox);
        String argsList[] = new String[4];
        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnInit();
            }

            @Override
            public Object DoInBackground(Object args) {
                if(!loImage.IsFileCreated(false)){
                    message = loImage.getMessage();
                    return false;
                } else {
                    LocationRetriever loLrt = new LocationRetriever(instance, activity);
                    if(loLrt.HasLocation()){
                        argsList[0] = loImage.getFilePath();
                        argsList[1] = loImage.getFileName();
                        argsList[2] = loLrt.getLatitude();
                        argsList[3] = loLrt.getLongitude();
                        Intent loIntent = loImage.getCameraIntent();
                        loIntent.putExtra("result", true);
                        return loIntent;
                    } else {
                        argsList[0] = loImage.getFilePath();
                        argsList[1] = loImage.getFileName();
                        argsList[2] = loLrt.getLatitude();
                        argsList[3] = loLrt.getLongitude();
                        message = loLrt.getMessage();
                        Intent loIntent = loImage.getCameraIntent();
                        loIntent.putExtra("result", false);
                        return loIntent;
                    }
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Intent loResult = (Intent) object;
                if(loResult.getBooleanExtra("result", false)){
                    callback.OnFailed(message, loResult, argsList);
                    return;
                }
                callback.OnSuccess(loResult, argsList);
            }
        });
    }

    public void SaveAddressImage(CIImage foVal, OnSaveAddressResult listener){
        final String[] argsVal = new String[1];
        final String[] args1 = new String[1];
        final String[] args2 = new String[1];
        TaskExecutor.Execute(foVal, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                CIImage foval = (CIImage) args;
                try{
                    if(!poSys.SaveImageInfo((foval))){
                        message = poSys.getMessage();
                        return false;
                    }
                    argsVal[0] = foval.getsParentxx();
                    args1[0] = foval.getsKeyNamex();
                    args2[0] = foval.getcResultxx();
                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    message = e.getMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(!isSuccess){
                    listener.OnError(message);
                } else {
                    listener.OnSuccess(argsVal[0], args1[0], args2[0]);
                }
            }
        });
    }
}
