/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 9/9/21, 11:53 AM
 * project file last modified : 9/9/21, 11:51 AM
 */

package org.rmj.guanzongroup.petmanager.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeBusinessTrip;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeLeave;
import org.rmj.g3appdriver.lib.Etc.Branch;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj.EmployeeLeave;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj.EmployeeOB;
import org.rmj.g3appdriver.utils.ConnectionUtil;

import java.util.List;

public class VMEmployeeApplications extends AndroidViewModel {
    private static final String TAG = VMEmployeeApplications.class.getSimpleName();

    private final Application instance;
    private final EmployeeLeave poLeave;
    private final EmployeeOB poBuss;
    private final Branch poBranch;

    private final MutableLiveData<Integer> pnLeave = new MutableLiveData<>();

    public interface OnDownloadApplicationListener {
        void OnDownload(String Title, String message);
        void OnDownloadSuccess();
        void OnDownloadFailed(String message);
    }

    public VMEmployeeApplications(@NonNull Application application) {
        super(application);
        this.instance = application;
        this.poLeave = new EmployeeLeave(instance);
        this.poBranch = new Branch(instance);
        this.poBuss = new EmployeeOB(instance);
        this.pnLeave.setValue(0);
    }

    public void setApplicationType(int fnVal){
        pnLeave.setValue(fnVal);
    }

    public LiveData<Integer> GetApplicationType(){
        return pnLeave;
    }

    public LiveData<List<EEmployeeLeave>> getApproveLeaveList(){
        return poLeave.GetApproveLeaveApplications();
    }

    public LiveData<List<EEmployeeBusinessTrip>> GetApproveBusTrip(){
        return poBuss.GetApproveOBApplications();
    }

    public LiveData<List<EEmployeeLeave>> getEmployeeLeaveForApprovalList(){
        return poLeave.GetLeaveApplicationsForApproval();
    }

    public LiveData<EBranchInfo> getUserBranchInfo(){
        return poBranch.getUserBranchInfo();
    }

    public void  DownloadLeaveForApproval(OnDownloadApplicationListener listener){
        new DownloadLeaveTask(instance, listener).execute();
    }

    private static class DownloadLeaveTask extends AsyncTask<Void, Void, Boolean>{

        private final EmployeeLeave loLeave;
        private final ConnectionUtil loConn;
        private final EmployeeOB poBusTrip;
        private final OnDownloadApplicationListener mListener;

        private String message;

        public DownloadLeaveTask(Application instance, OnDownloadApplicationListener listener){
            this.loLeave = new EmployeeLeave(instance);
            this.loConn = new ConnectionUtil(instance);
            this.poBusTrip = new EmployeeOB(instance);
            this.mListener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mListener.OnDownload("PET Manager","Downloading leave applications. Please wait...");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                if (!loConn.isDeviceConnected()) {
                    message = loConn.getMessage();
                    return false;
                }

                if(!loLeave.ImportApplications()){
                    message = loLeave.getMessage();
                    Log.e(TAG, message);
                }

                Thread.sleep(1000);

                if(!poBusTrip.ImportApplications()){
                    message = poBusTrip.getMessage();
                    Log.e(TAG, message);
                }

                return true;
            } catch (Exception e){
                e.printStackTrace();
                message = getLocalMessage(e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if(isSuccess){
                mListener.OnDownloadSuccess();
            } else {
                mListener.OnDownloadFailed(message);
            }
        }
    }
}


