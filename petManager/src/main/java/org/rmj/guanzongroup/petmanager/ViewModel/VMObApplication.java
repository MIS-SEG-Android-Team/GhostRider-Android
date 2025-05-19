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

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj.EmployeeOB;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.lib.Etc.Branch;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.model.PetMngr;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.pojo.OBApplication;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMObApplication extends AndroidViewModel {

    public static final String TAG = VMObApplication.class.getSimpleName();
    private final Application instance;
    private final Branch pobranch;
    private final ConnectionUtil poConn;
    private final PetMngr poSys;
    private final LiveData<String[]> paBranchNm;
    private String message;

    public VMObApplication(@NonNull Application application) {
        super(application);
        this.instance = application;
        this.pobranch = new Branch(instance);
        this.poConn = new ConnectionUtil(instance);
        this.poSys = new EmployeeOB(instance);
        paBranchNm = pobranch.getAllMcBranchNames();
    }

    public interface OnSubmitOBLeaveListener{
        void onSuccess();
        void onFailed(String message);
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> getUserInfo(){
        return poSys.GetUserInfo();
    }

    public LiveData<EBranchInfo> getUserBranchInfo(){
        return pobranch.getUserBranchInfo();
    }

    public LiveData<String[]> getAllBranchNames(){
        return paBranchNm;
    }

    public LiveData<List<EBranchInfo>> getAllBranchInfo(){
        return pobranch.getAllMcBranchInfo();
    }

    public void saveObLeave(OBApplication infoModel, OnSubmitOBLeaveListener callback){

        TaskExecutor.Execute(infoModel, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {

            }

            @Override
            public Object DoInBackground(Object args) {

                OBApplication loApp = (OBApplication) args;
                try{
                    String lsTransNox = poSys.SaveApplication(loApp);
                    if(lsTransNox == null){
                        message = poSys.getMessage();
                        return false;
                    }

                    if(!poConn.isDeviceConnected()){
                        message = poConn.getMessage() + " Your leave application has been save to local.";
                        return false;
                    }

                    if(!poSys.UploadApplication(lsTransNox)){
                        message = poSys.getMessage();
                        return false;
                    }

                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return false;
                }

            }

            @Override
            public void OnPostExecute(Object object) {

                Boolean isSuccess = (Boolean) object;
                if(isSuccess){
                    callback.onSuccess();
                } else {
                    callback.onFailed(message);
                }

            }
        });
    }

}