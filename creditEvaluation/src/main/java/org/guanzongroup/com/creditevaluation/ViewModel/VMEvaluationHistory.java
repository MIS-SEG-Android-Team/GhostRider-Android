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
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCreditOnlineApplicationCI;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.Apps.CreditInvestigator.Obj.CITagging;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMEvaluationHistory extends AndroidViewModel {
    private static final String TAG = VMEvaluationHistory.class.getSimpleName();

    private final CITagging poSys;
    private final ConnectionUtil poConn;

    public VMEvaluationHistory(@NonNull Application application) {
        super(application);
        this.poConn = new ConnectionUtil(application);
        this.poSys = new CITagging(application);
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> GetUserInfo(){
        return poSys.GetUserInfo();
    }

    public LiveData<List<DCreditOnlineApplicationCI.oDataEvaluationInfo>> getForEvaluationListDataPreview() {
        return poSys.GetForEvaluationListDataPreview();
    }

    public void DownloadApplicationsForBHApproval(OnTransactionCallback callback) {
        new DownloadApplicationsForBHApproval(callback).execute();
    }

    private class DownloadApplicationsForBHApproval{
        private final OnTransactionCallback loCallBck;
        private String message;
        private DownloadApplicationsForBHApproval(OnTransactionCallback foCallBck) {
            this.loCallBck = foCallBck;
        }
        public void execute(){
            TaskExecutor.Execute(null, new OnTaskExecuteListener() {
                @Override
                public void OnPreExecute() {
                    loCallBck.onLoad();
                }

                @Override
                public Object DoInBackground(Object args) {
                    try {
                        if(!poConn.isDeviceConnected()) {
                            message = poConn.getMessage();
                            return false;
                        }

                        if(!poSys.DownloadApplicationsForBHApproval()){
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
                public void OnPostExecute(Object object) {
                    Boolean isSuccess = (Boolean) object;
                    if(!isSuccess) {
                        loCallBck.onFailed(message);
                    } else {
                        loCallBck.onSuccess("");
                    }
                }
            });
        }
    }

    public interface OnTransactionCallback {
        void onSuccess(String message);
        void onFailed(String message);
        void onLoad();
    }

}
