/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 9/20/21, 3:50 PM
 * project file last modified : 9/20/21, 3:50 PM
 */

package org.rmj.guanzongroup.petmanager.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj.EmployeeOB;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.pojo.OBApprovalInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeBusinessTrip;
import org.rmj.g3appdriver.lib.Etc.Branch;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

public class VMObApproval extends AndroidViewModel {
    private static final String TAG = VMObApproval.class.getSimpleName();

    private final Application instance;

    private final EmployeeOB poSys;
    private final Branch poBranch;
    private final ConnectionUtil poConn;
    private String message;
    private final MutableLiveData<String> TransNox = new MutableLiveData<>();

    public interface OnDownloadBusinessTripCallback {
        void OnDownload(String title, String message);

        void OnSuccessDownload(String TransNox);

        void OnFailedDownload(String message);
    }

    public interface OnConfirmApplicationCallback {
        void OnConfirm(String title, String message);

        void OnSuccess(String message);

        void OnFailed(String message);
    }

    public VMObApproval(@NonNull Application application) {
        super(application);
        this.instance = application;
        this.poSys = new EmployeeOB(application);
        this.poBranch = new Branch(instance);
        this.poConn = new ConnectionUtil(instance);
        this.TransNox.setValue("");
    }

    public LiveData<EBranchInfo> getUserBranchInfo() {
        return poBranch.getUserBranchInfo();
    }

    public void setTransNox(String TransNox) {
        this.TransNox.setValue(TransNox);
    }

    public LiveData<String> getTransNox() {
        return TransNox;
    }

    public LiveData<EEmployeeBusinessTrip> getBusinessTripInfo(String TransNox) {
        return poSys.GetOBApplicationInfo(TransNox);
    }

    public void downloadBusinessTrip(String TransNox, OnDownloadBusinessTripCallback callback) {
//        new DownloadBusinessTripTask(instance, callback).execute(TransNox);
        TaskExecutor.Execute(TransNox, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {

            }

            @Override
            public Object DoInBackground(Object args) {
                try {
                    if (!poConn.isDeviceConnected()) {
                        message = poConn.getMessage();
                        return false;
                    }

                    if (!poSys.DownloadApplication(String.valueOf(args))) {
                        message = poSys.getMessage();
                        return false;
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

            }
        });
    }

    public void confirmOBApplication(OBApprovalInfo infoModel, OnConfirmApplicationCallback callback) {
        TaskExecutor.Execute(infoModel, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnConfirm("PET Manager", "Sending approval request. Please wait...");
            }

            @Override
            public Object DoInBackground(Object args) {
                try {
                    if (!poConn.isDeviceConnected()) {
                        message = poConn.getMessage() + " Your approval will be automatically send if device is reconnected to internet.";
                        return false;
                    }

                    if (!poSys.UploadApproval(args)) {
                        message = poSys.getMessage();
                        return false;
                    }

                    String lsTransNox = poSys.SaveApproval(args);
                    if (lsTransNox == null) {
                        message = poSys.getMessage();
                        return false;
                    }

                    message = poSys.getMessage();
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
                if (lsSuccess) {
                    callback.OnSuccess(message);
                } else {
                    callback.OnFailed(message);
                }
            }
        });
    }
}