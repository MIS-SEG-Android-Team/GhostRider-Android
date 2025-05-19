/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.approvalCode
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.approvalcode.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.ApprovalCode;
import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.model.SCA;
import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.pojo.ManualTimeLog;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMManualLog extends AndroidViewModel {
    public static final String TAG = VMManualLog.class.getSimpleName();
    private final SCA poSys;
    private final ConnectionUtil poConn;
    private String message;
    public VMManualLog(@NonNull Application application) {
        super(application);
        this.poSys = new ApprovalCode(application).getInstance(ApprovalCode.eSCA.MANUAL_LOG);
        this.poConn = new ConnectionUtil(application);
    }

    public LiveData<List<EBranchInfo>> GetAllBranchInfo(){
        return poSys.GetBranchList();
    }

    public void GenerateCode(ManualTimeLog model, OnGenerateApprovalCodeListener listener){
//        new GenerateCodeTask(listener).execute(model);
        TaskExecutor.Execute(model, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                listener.OnGenerate("Approval Code", "Generating approval code. Please wait...");
            }

            @Override
            public Object DoInBackground(Object args) {
                ManualTimeLog lsManualTimeLogs = (ManualTimeLog) args;
                try {
                    if (!lsManualTimeLogs.isDataValid()) {
                        message = lsManualTimeLogs.getMessage();
                        return null;
                    }

                    String lsCode = poSys.GenerateCode(lsManualTimeLogs);

                    if(lsCode == null){
                        message = poSys.getMessage();
                        return null;
                    }

                    if(!poConn.isDeviceConnected()){
                        message = poConn.getMessage();
                        Log.e(TAG, message);
                    }

                    if(!poSys.Upload(lsCode)){
                        message = poSys.getMessage();
                        Log.e(TAG, message);
                    }

                    return lsCode;
                } catch (Exception e){
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return null;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                String lsResult = (String) object;
                if(lsResult == null){
                    listener.OnFailed(message);
                } else {
                    listener.OnSuccess(lsResult);
                }
            }
        });
    }

    public interface OnGenerateApprovalCodeListener{
        void OnGenerate(String title, String message);
        void OnSuccess(String args);
        void OnFailed(String message);
    }
}