/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.creditEvaluation
 * Electronic Personnel Access Control Security System
 * project file created : 3/11/22, 11:19 AM
 * project file last modified : 3/11/22, 11:19 AM
 */

package org.guanzongroup.com.creditevaluation.ViewModel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCreditOnlineApplicationCI;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.Apps.integsys.CreditInvestigator.Obj.CITagging;
import org.rmj.g3appdriver.utils.ConnectionUtil;

import java.util.List;

public class VMEvaluationList extends AndroidViewModel {
    private static final String TAG = VMEvaluationList.class.getSimpleName();
    private final Application instance;

    private final CITagging poSys;
    private final ConnectionUtil poConn;

    public VMEvaluationList(@NonNull Application application) {
        super(application);
        this.instance = application;
        this.poSys = new CITagging(application);
        this.poConn = new ConnectionUtil(application);
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> GetUserInfo(){
        return poSys.GetUserInfo();
    }

    public void AddForCIApplication(String fsTransno, ViewModelCallback callback) {
        try{
            new AddApplicationInfoTask(instance, callback).execute(fsTransno.trim());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public class AddApplicationInfoTask extends AsyncTask<String, Void, Boolean> {

        private final ConnectionUtil poConn;
        private final ViewModelCallback callback;

        private String message;

        public AddApplicationInfoTask(Application application, ViewModelCallback callback) {
            this.poConn = new ConnectionUtil(application);
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            callback.OnStartSaving();
        }

        @Override
        protected Boolean doInBackground(String... args) {
            try {
                if (!poConn.isDeviceConnected()) {
                    message = poConn.getMessage();
                    return false;
                }

                String transno = args[0];
                if(!poSys.AddApplication(transno)){
                    message = poSys.getMessage();
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                message = e.getMessage();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if (!isSuccess) {
                callback.OnFailedResult(message);
            } else {
                callback.OnSuccessResult();
            }
        }
    }

    public interface OnImportCallBack{
        void onStartImport();
        void onSuccessImport();
        void onImportFailed(String message);
    }
    public LiveData<DCreditOnlineApplicationCI.oDataEvaluationInfo> getForEvaluationInfo(String transNox) {
        return poSys.getForEvaluationInfo(transNox);
    }

    public LiveData<List<DCreditOnlineApplicationCI.oDataEvaluationInfo>> getForEvaluationListData() {
        return poSys.GetForEvaluationListData();
    }

    public void DownloadForCIApplications(OnImportCallBack callBack){
        new DownloadForCIApplications(callBack).execute();
    }
    private class DownloadForCIApplications extends AsyncTask<Void, Void, Boolean> {

        private final OnImportCallBack callback;

        private String message;

        public DownloadForCIApplications(OnImportCallBack callback) {
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            callback.onStartImport();
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (!poConn.isDeviceConnected()) {
                    message = poConn.getMessage();
                }

                if(!poSys.DownloadForCIApplications()){
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
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if(!isSuccess){
                callback.onImportFailed(message);
            } else {
                callback.onSuccessImport();
            }
        }
    }
}
