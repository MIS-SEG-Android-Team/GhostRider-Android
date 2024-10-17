/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 6/9/21 9:00 AM
 * project file last modified : 6/9/21 9:00 AM
 */

package org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.lib.Etc.Branch;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Apps.CashCount.CashCount;
import org.rmj.g3appdriver.GCircle.Apps.CashCount.QuickSearchNames;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMCashCountSubmit extends AndroidViewModel {
    private static final String TAG = VMCashCountSubmit.class.getSimpleName();

    private final Application instance;
    private final EmployeeMaster poEmploye;
    private final CashCount poSys;
    private final ConnectionUtil poConn;
    private final Branch poBranch;
    private String message;
    public VMCashCountSubmit(@NonNull Application application) {
        super(application);
        this.instance = application;
        this.poSys = new CashCount(application);
        this.poEmploye = new EmployeeMaster(application);
        this.poBranch = new Branch(application);
        this.poConn = new ConnectionUtil(application);
    }

    public interface OnKwikSearchCallBack{
        void onStartKwikSearch();
        void onSuccessKwikSearch(List<QuickSearchNames> infoList);
        void onKwikSearchFailed(String message);
    }

    public interface OnSaveCashCountCallBack{
        void OnSaving();
        void OnSuccess();
        void OnSaveToLocal(String message);
        void OnFailed(String message);
    }

    public interface OnDeviceConnectionCheck{
        void OnCheck(boolean isDeviceConnected);
    }

    public LiveData<EBranchInfo> getUserBranchInfo(){
        return poBranch.getUserBranchInfo();
    }

    public LiveData<EBranchInfo> getSelfieLogBranchInfo(){
        return poBranch.getSelfieLogBranchInfo();
    }

    public void GetSearchList(String name, OnKwikSearchCallBack callback){
        TaskExecutor.Execute(name, new OnTaskExecuteListener() {

            @Override
            public void OnPreExecute() {
                callback.onStartKwikSearch();
            }

            @Override
            public Object DoInBackground(Object args) {
                if(!poConn.isDeviceConnected()){
                    message = poConn.getMessage();
                    return null;
                }

                String lsName = (String) args;
                List<QuickSearchNames> loList = poSys.GetQuickSearchNames(lsName);
                if(loList == null){
                    message = poSys.getMessage();
                    return null;
                }
                return loList;
            }

            @Override
            public void OnPostExecute(Object object) {
                List<QuickSearchNames> loResult = (List<QuickSearchNames>) object;
                if(loResult == null){
                    callback.onKwikSearchFailed(message);
                    return;
                }

                callback.onSuccessKwikSearch(loResult);
            }
        });
    }

    public void SaveCashCount(JSONObject foVal, OnSaveCashCountCallBack callback) {
        TaskExecutor.Execute(foVal, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnSaving();
            }

            @Override
            public Object DoInBackground(Object args) {
                JSONObject lsentries = (JSONObject) args;
                String lsResult = poSys.SaveCashCount(lsentries);
                if (lsResult == null){
                    message = poSys.getMessage();
                    return 0;
                }

                if(!poConn.isDeviceConnected()){
                    message = "Cash count entry has been save to local device.";
                    return 2;
                }

                if(!poSys.UploadCashCount(lsResult)){
                    message = poSys.getMessage();
                    return 0;
                }

                return 1;

            }

            @Override
            public void OnPostExecute(Object object) {
                Integer lnResult = (Integer) object;
                switch (lnResult){
                    case 0:
                        callback.OnFailed(message);
                        break;
                    case 1:
                        callback.OnSuccess();
                        break;
                    case 2:
                        callback.OnSaveToLocal(message);
                        break;
                }
            }
        });
    }

    public void CheckConnectivity(OnDeviceConnectionCheck callback){
        new ConnectionCheckTask(instance, callback).execute();
    }

    private static class ConnectionCheckTask extends AsyncTask<String, Void, Boolean>{

        private final Application instance;
        private final OnDeviceConnectionCheck callback;

        public ConnectionCheckTask(Application instance, OnDeviceConnectionCheck callback) {
            this.instance = instance;
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return new ConnectionUtil(instance).isDeviceConnected();
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {
            super.onPostExecute(isConnected);
            callback.OnCheck(isConnected);
        }
    }
}