/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.petmanager.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DSelfieLog;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ESelfieLog;
import org.rmj.g3appdriver.lib.Etc.Branch;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.ImageFileCreator;
import org.rmj.g3appdriver.etc.OnInitializeCameraCallback;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.GCircle.Apps.SelfieLog.SelfieLog;
import org.rmj.g3appdriver.GCircle.Apps.CashCount.CashCount;
import org.rmj.g3appdriver.lib.Location.LocationRetriever;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;
import java.util.List;

public class VMSelfieLog extends AndroidViewModel {
    private static final String TAG = VMSelfieLog.class.getSimpleName();

    private final Application instance;
    private final SelfieLog poSys;
    private final CashCount poCash;
    private final Branch pobranch;
    private final ConnectionUtil poConn;
    private final EmployeeMaster poUser;
    private final EmployeeSession poSession;
    private final MutableLiveData<String> pdTransact = new MutableLiveData<>();

    private String message;

    private List<EBranchInfo> area, all;

    public interface OnLoginTimekeeperListener{
        void OnLogin();
        void OnSuccess(String args);
        void SaveOffline(String args);
        void OnFailed(String message);
    }

    public interface OnValidateCashCount{
        void OnValidate();
        void OnProceed(String args);
        void OnWarning(String message);
        void OnFailed(String message);
        void OnUnauthorize(String message);
    }

    public interface OnBranchSelectedCallback {
        void OnLoad();
        void OnSuccess();
        void OnFailed(String message);
    }

    public interface OnValidateSelfieBranch{
        void OnValidate();
        void OnSuccess();
        void OnRequireRemarks();
        void OnFailed(String message);
    }

    public VMSelfieLog(@NonNull Application application) {
        super(application);
        this.instance = application;
        this.poUser = new EmployeeMaster(instance);
        this.poSys = new SelfieLog(instance);
        this.pobranch = new Branch(instance);
        this.poCash = new CashCount(instance);
        this.poConn = new ConnectionUtil(instance);
        this.poSession = EmployeeSession.getInstance(instance);
        this.pdTransact.setValue(AppConstants.CURRENT_DATE());
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> GetUserInfo(){
        return poUser.GetEmployeeBranch();
    }

    public LiveData<EBranchInfo> getUserBranchInfo(){
        return pobranch.getUserBranchInfo();
    }

    public LiveData<EBranchInfo> getBranchInfo(String BranchCD){
        return pobranch.getBranchInfo(BranchCD);
    }

    public LiveData<String> GetSelectedDate(){
        return pdTransact;
    }

    public void SetSelectedDate(String fsVal){
        this.pdTransact.setValue(fsVal);
    }

    public LiveData<List<ESelfieLog>> getAllEmployeeTimeLog(String fsVal){
        return poSys.GetAllEmployeeTimeLog(fsVal);
    }

    public LiveData<List<DSelfieLog.LogTime>> GetTimeLogForTheDay(String date){
        return poSys.GetAllTimeLog(date);
    }

    public interface OnBranchCheckListener{
        void OnCheck();
        void OnCheck(List<EBranchInfo> area, List<EBranchInfo> all);
        void OnFailed(String message);
    }

    public void CheckBranchList(OnBranchCheckListener listener){
        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                listener.OnCheck();
            }

            @Override
            public Object DoInBackground(Object args) {
                try{
                    area = pobranch.getAreaBranchesList();
                    all = pobranch.getBranchList();
                    if(all.size() > 0){
                        return true;
                    } else {
                        message = "No branch list retrieve from local. Please sync device records";
                        return false;
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                boolean isSuccess = (boolean) object;
                if(isSuccess){
                    listener.OnCheck(area, all);
                } else {
                    listener.OnFailed(message);
                }
            }
        });
    }

    public void InitCameraLaunch(Activity activity, OnInitializeCameraCallback callback){
        ImageFileCreator loImage = new ImageFileCreator(instance, AppConstants.SUB_FOLDER_SELFIE_LOG, poSession.getUserID());
        String[] lsResult = new String[4];
        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnInit();
            }

            @Override
            public Object DoInBackground(Object args) {
                if(!loImage.IsFileCreated(true)){
                    message = loImage.getMessage();
                    return null;
                }else {

                    LocationRetriever loLrt = new LocationRetriever(instance, activity);

                    lsResult[0] = loImage.getFilePath();
                    lsResult[1] = loImage.getFileName();
                    lsResult[2] = loLrt.getLongitude();
                    lsResult[3] = loLrt.getLatitude();

                    //TODO: VALIDATE DEVICE LOCATION
                    if (loLrt.HasLocation()) {

                        Intent loIntent = loImage.getCameraIntent();
                        loIntent.putExtra("result", true);

                        return loIntent;
                    }else {

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
                    callback.OnSuccess(loResult, lsResult);
                } else {
                    callback.OnFailed(message, loResult, lsResult);
                }
            }
        });
    }

    /**
     *
     */
    public void ValidateSelfieBranch(String args, OnValidateSelfieBranch listener){
        TaskExecutor.Execute(args, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                listener.OnValidate();
            }

            @Override
            public Object DoInBackground(Object args) {
                int lnResult = poSys.ValidateSelfieBranch((String) args);
                if(lnResult != 3){
                    message = poSys.getMessage();
                }
                return lnResult;
            }

            @Override
            public void OnPostExecute(Object object) {
                int lnResult = (int) object;
                switch (lnResult) {
                    case 0:
                        listener.OnFailed(message);
                        break;
                    case 2:
                    case 5:
                        listener.OnRequireRemarks();
                        break;
                    case 3:
                    case 4:
                    case 1:
                        listener.OnSuccess();
                }
            }
        });
    }

    public void checkIfAlreadyLog(String BranchCde, OnBranchSelectedCallback callback){
        TaskExecutor.Execute(BranchCde, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {
                String BranchCD = (String) args;
                if(!poSys.ValidateExistingBranch(BranchCD)){
                    message = poSys.getMessage();
                    return false;
                }

                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                boolean isSuccess = (boolean) object;
                if(!isSuccess){
                    callback.OnFailed(message);
                } else {
                    callback.OnSuccess();
                }
            }
        });
    }

    public void TimeIn(SelfieLog.SelfieLogDetail foVal, OnLoginTimekeeperListener callback){
        TaskExecutor.Execute(foVal, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnLogin();

            }
            @Override
            public Object DoInBackground(Object args) {
                try{
                    SelfieLog.SelfieLogDetail logDetail = (SelfieLog.SelfieLogDetail) args;

                    String lsTransNo = poSys.SaveSelfieLog(logDetail);
                    if(lsTransNo == null){
                        message = poSys.getMessage();
                        return 0;
                    }

                    if(!poConn.isDeviceConnected()){
                        message = "Your selfie log has been save to local.";
                        return 2;
                    }

                    if (!poSys.UploadSelfieLog(lsTransNo)) {
                        message = poSys.getMessage();
                        return 3;
                    }

                    //Usually message is only use for storing and error message.
                    // this time message value will be branch code which will be pass for cash count entry.
                    message = logDetail.getBranchCode();
                    return 1;
                } catch (Exception e){
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return 0;
                }
            }
            @Override
            public void OnPostExecute(Object object) {
                int lnResult = (int) object;
                switch (lnResult){
                    case 0:
                    case 3:
                        callback.OnFailed(message);
                        break;
                    case 1:
                        callback.OnSuccess(message);
                        break;
                    case 2:
                        callback.SaveOffline(message);
                        break;
                }
            }
        });
    }

    public void ResendTimeIn(String args, OnLoginTimekeeperListener callback){
        TaskExecutor.Execute(args, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnLogin();
            }

            @Override
            public Object DoInBackground(Object args) {
                try{
                    String TransNox = (String) args;
                    if(!poConn.isDeviceConnected()){
                        message = poConn.getMessage();
                        return false;
                    }

                    if (!poSys.UploadSelfieLog(TransNox)) {
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
                boolean isSuccess = (boolean) object;
                if(!isSuccess){
                    callback.OnFailed(message);
                    return;
                }

                callback.OnSuccess("");
            }
        });
    }

    public void ValidateCashCount(String fsVal, OnValidateCashCount callback){
        TaskExecutor.Execute(fsVal, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnValidate();
            }

            @Override
            public Object DoInBackground(Object args) {
                String branchCd = (String) args;
                int lnResult = poCash.ValidateCashCount(branchCd);
                if(lnResult == 1){
                    message = branchCd;
                    return lnResult;
                }
                message = poCash.getMessage();
                return lnResult;
            }

            @Override
            public void OnPostExecute(Object object) {
                int lnResult = (int) object;
                switch (lnResult){
                    case 0:
                        callback.OnFailed(message);
                        break;
                    case 1:
                        callback.OnProceed(message);
                        break;
                    case 2:
                        callback.OnWarning(message);
                        break;
                    default:
                        callback.OnUnauthorize(message);
                        break;
                }
            }
        });
    }
}