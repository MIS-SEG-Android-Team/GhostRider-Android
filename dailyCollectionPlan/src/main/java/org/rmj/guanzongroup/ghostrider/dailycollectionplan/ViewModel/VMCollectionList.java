/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.dailyCollectionPlan
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.ImportParams;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.model.LRDcp;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMCollectionList extends AndroidViewModel {
    private static final String TAG = VMCollectionList.class.getSimpleName();

    private final EmployeeMaster poUser;
    private final LRDcp poSys;
    private final ConnectionUtil poConn;
    private final AppConfigPreference poConfig;

    private String message;

    public interface OnActionCallback {
        void OnLoad();
        void OnSuccess();
        void OnFailed(String message);
    }

    public interface OnCheckDcpForPosting {
        void OnLoad();
        void OnSuccess();
        void OnIncompleteDcp();
        void OnFailed(String message);
    }

    public interface OnDownloadClientList{
        void OnDownload();
        void OnSuccessDownload(List<EDCPCollectionDetail> collectionDetails);
        void OnFailedDownload(String message);
    }

    public VMCollectionList(@NonNull Application application) {
        super(application);
        this.poUser = new EmployeeMaster(application);
        this.poSys = new LRDcp(application);
        this.poConn = new ConnectionUtil(application);
        this.poConfig = AppConfigPreference.getInstance(application);
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> GetUserInfo(){
        return poUser.GetEmployeeBranch();
    }

    public LiveData<List<EDCPCollectionDetail>> GetColletionList(){
        return poSys.GetCollectionList();
    }

    public boolean IsTesting(){
        return poConfig.getTestStatus();
    }

    public void DownloadDCP(ImportParams foVal, OnActionCallback callback){
        TaskExecutor.Execute(foVal, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {
                if(!poConn.isDeviceConnected()){
                    message = poSys.getMessage();
                    return false;
                }

                if(!poSys.DownloadCollection((ImportParams) args)){
                    message = poSys.getMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(isSuccess){
                    callback.OnSuccess();
                } else {
                    callback.OnFailed(message);
                }
            }
        });
    }

    public void SearchClient(String fsVal, OnDownloadClientList callback){
        TaskExecutor.Execute(fsVal, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnDownload();
            }

            @Override
            public Object DoInBackground(Object args) {
                if(!poConn.isDeviceConnected()){
                    message = poConn.getMessage();
                    return null;
                }

                List<EDCPCollectionDetail> loList = poSys.GetSearchList((String) args);
                if(loList == null){
                    message = poSys.getMessage();
                    return null;
                }
                return loList;
            }

            @Override
            public void OnPostExecute(Object object) {
                if(object == null){
                    callback.OnFailedDownload(message);
                } else {
                    callback.OnSuccessDownload((List<EDCPCollectionDetail>) object);
                }
            }
        });
    }

    public void AddCollection(EDCPCollectionDetail foVal, OnActionCallback callback){
        TaskExecutor.Execute(foVal, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {
                if(!poSys.AddCollection((EDCPCollectionDetail) args)){
                    message = poSys.getMessage();
                    return false;
                }
                return true;
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

    public void CheckDcpForPosting(OnCheckDcpForPosting callback){
        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {
                if(!poSys.HasRemittedCollection()){
                    message = poSys.getMessage();
                    return 2;
                }
                if(poSys.HasNotVisitedCollection()){
                    message = poSys.getMessage();
                    return 0;
                }
                return 1;
            }

            @Override
            public void OnPostExecute(Object object) {
                switch ((Integer) object) {
                    case 0: //Failed
                        callback.OnIncompleteDcp();
                        break;
                    case 1: //Success
                        callback.OnSuccess();
                        break;
                    default: //Need remittance
                        callback.OnFailed(message);
                        break;
                }
            }
        });
    }

    public void PostCollectionList(String fsVal, OnActionCallback callback){
        TaskExecutor.Execute(fsVal, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {
                if(!poSys.ExportToFile()){
                    message = poSys.getMessage();
                    Log.e(TAG, message);
                }

                if(!poConn.isDeviceConnected()){
                    message = poConn.getMessage();
                    return false;
                }


                String lsResult = poSys.PostCollection((String) args);
                if(lsResult == null){
                    message = poSys.getMessage();
                    return false;
                }

                if(!poSys.PostDcpMaster(lsResult)){
                    message = poSys.getMessage();
                    return false;
                }
                return true;
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

    public void ClearDCPRecords(OnActionCallback callback){
        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {
                if(!poSys.ClearDCPData()){
                    message = poSys.getMessage();
                    return false;
                }
                return true;
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
}