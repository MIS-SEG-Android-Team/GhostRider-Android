/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.app
 * Electronic Personnel Access Control Security System
 * project file created : 4/30/21 11:47 AM
 * project file last modified : 4/24/21 3:19 PM
 */

package org.rmj.guanzongroup.ghostrider.epacss.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj.EmployeeLeave;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj.EmployeeOB;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.OnCheckEmployeeApplicationListener;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.model.PetMngr;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeBusinessTrip;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeLeave;
import org.rmj.g3appdriver.lib.Etc.Branch;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMAssociateDashboard extends AndroidViewModel {
    private static final String TAG = VMAssociateDashboard.class.getSimpleName();

    private final Application instance;

    private final EmployeeMaster poEmployee;
    private PetMngr poApp;
    private final Branch pobranch;
    private final ConnectionUtil poConn;

    private final AppConfigPreference poConfigx;

    private final MutableLiveData<String> psVersion = new MutableLiveData<>();
    private String message;

    public VMAssociateDashboard(@NonNull Application application) {
        super(application);
        this.instance = application;
        this.poEmployee = new EmployeeMaster(application);
        this.pobranch = new Branch(application);
        this.poConfigx = AppConfigPreference.getInstance(application);
        this.psVersion.setValue(poConfigx.getVersionInfo());
        this.poConn = new ConnectionUtil(application);
    }

    public LiveData<EEmployeeInfo> getEmployeeInfo() {
        return poEmployee.GetEmployeeInfo();
    }

    public LiveData<String> getVersionInfo() {
        return psVersion;
    }

    public LiveData<EBranchInfo> getUserBranchInfo() {
        return pobranch.getUserBranchInfo();
    }

    public LiveData<List<EEmployeeLeave>> GetLeaveForApproval() {
        return new EmployeeLeave(instance).GetLeaveApplicationsForApproval();
    }

    public LiveData<List<EEmployeeBusinessTrip>> GetOBForApproval() {
        return new EmployeeOB(instance).GetOBApplicationsForApproval();
    }

    public void CheckApplicationsForApproval(OnCheckEmployeeApplicationListener listener) {

        TaskExecutor.Execute(null, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                try {
                    if (!poConn.isDeviceConnected()) {
                        message = poConn.getMessage();
                        return false;
                    }

                    if(!new EmployeeLeave(instance).ImportApplications()){
                        Log.e(TAG, "Failed to import leave applications");
                    }

                    Thread.sleep(1000);

                    if(!new EmployeeOB(instance).ImportApplications()){
                        Log.e(TAG, "Failed to import business trip applications");
                    }

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean lsSuccess = (Boolean) object;
                if (!lsSuccess) {
                    listener.OnFailed(message);
                } else {
                    listener.OnSuccess();
                }
            }
        });
    }
}
//    private class CheckApplicationForApprovalTask extends AsyncTask<Void, Void, Boolean>{
//
//        private final OnCheckEmployeeApplicationListener mListener;
//
//        private String message;
//
//        public CheckApplicationForApprovalTask(OnCheckEmployeeApplicationListener mListener) {
//            this.mListener = mListener;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... voids) {
//            try{
//                if(!poConn.isDeviceConnected()){
//                    message = poConn.getMessage();
//                    return false;
//                }
//
//                poApp = new PetManager(instance).GetInstance(PetManager.ePetManager.LEAVE_APPLICATION);
//                if(!poApp.ImportApplications()){
//                    Log.e(TAG, poApp.getMessage());
//                }
//
//                Thread.sleep(1000);
//
//                poApp = new PetManager(instance).GetInstance(PetManager.ePetManager.BUSINESS_TRIP_APPLICATION);
//                if(!poApp.ImportApplications()){
//                    Log.e(TAG, poApp.getMessage());
//                }
//
//                return true;
//            } catch (Exception e){
//                e.printStackTrace();
//                message = e.getMessage();
//                return false;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Boolean isSuccess) {
//            super.onPostExecute(isSuccess);
//            if(!isSuccess){
//                mListener.OnFailed(message);
//            } else {
//                mListener.OnSuccess();
//            }
//        }
//    }
//}