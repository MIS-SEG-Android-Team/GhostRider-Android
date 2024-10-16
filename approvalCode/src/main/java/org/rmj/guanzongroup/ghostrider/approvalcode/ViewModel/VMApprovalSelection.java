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

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.ImportData.Obj.Import_SCARequest;
import org.rmj.g3appdriver.GCircle.ImportData.model.ImportDataCallback;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DApprovalCode;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DSCARqstEmp;
import org.rmj.g3appdriver.GCircle.room.Entities.ESCARqstEmp;
import org.rmj.g3appdriver.GCircle.room.Entities.ESCA_Request;

import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.ApprovalCode;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMApprovalSelection extends AndroidViewModel {
    public static final String TAG = VMApprovalSelection.class.getSimpleName();
    private final ApprovalCode poSys;
    private final DSCARqstEmp poDaoRstEmp;
    private final DApprovalCode poUser;
    private String lomessage;

    public VMApprovalSelection(@NonNull Application application) {
        super(application);

        this.poSys = new ApprovalCode(application);
        this.poDaoRstEmp = GGC_GCircleDB.getInstance(application).scaRqstEmpDao();
        this.poUser = GGC_GCircleDB.getInstance(application).ApprovalDao();
    }

    public LiveData<List<ESCA_Request>> getReferenceAuthList(String Type){
        return poSys.getAuthorizedFeatures(Type);
    }

    public String getLatestStamp(){
        return poSys.getLatestStamp();
    }

    public ESCARqstEmp getRqstEmp(String sSCACode){
        return poDaoRstEmp.GetEmpRqst(sSCACode, poUser.GetUserInfo().getEmployID());
    }

    public ESCARqstEmp getRqstExst(String sSCACode){
        return poDaoRstEmp.GetExstRqst(sSCACode);
    }

    public void getApprovalCodes(String latestStampRqst, onDownload callback){
        TaskExecutor.Execute(latestStampRqst, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                Import_SCARequest importScaRequest = new Import_SCARequest(getApplication());

                if (latestStampRqst != null){
                    if (!latestStampRqst.isEmpty()){
                        importScaRequest.setTimeStamp(latestStampRqst);
                    }
                }

                importScaRequest.ImportData(new ImportDataCallback() {
                    @Override
                    public void OnSuccessImportData() {
                        lomessage = "Approval Lists successfully downloaded";
                    }

                    @Override
                    public void OnFailedImportData(String message) {
                        lomessage = message;
                    }
                });

                return lomessage;
            }

            @Override
            public void OnPostExecute(Object object) {
                callback.onFinished(object.toString());
            }
        });
    }

    public interface onDownload{
        void onFinished(String message);
    }
}
