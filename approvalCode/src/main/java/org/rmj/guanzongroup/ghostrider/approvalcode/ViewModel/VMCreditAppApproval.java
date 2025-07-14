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

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.ApprovalCode;
import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.model.SCA;
import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.pojo.CreditAppInfo;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;
import org.rmj.guanzongroup.ghostrider.approvalcode.Etc.ViewModelCallback;
import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.pojo.CreditApp;

public class VMCreditAppApproval extends AndroidViewModel {

    private final ConnectionUtil poConn;
    private final SCA poSys;
    private String message;

    public VMCreditAppApproval(@NonNull Application application) {
        super(application);
        this.poConn = new ConnectionUtil(application);
        this.poSys = new ApprovalCode(application).getInstance(ApprovalCode.eSCA.CREDIT_APP);
    }

    public void LoadApplication(String args, String args1, String args2, OnLoadApplicationListener listener){

        String[] params = {args, args1, args2};

        TaskExecutor.Execute(params, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                listener.OnLoad("Approval Code", "Loading Credit Application. Please wait...");
            }

            @Override
            public Object DoInBackground(Object args) {

                try {
                    if(!poConn.isDeviceConnected()){
                        message = poConn.getMessage();
                        return null;
                    }

                    String[] listargs = (String[]) args;

                    String
                            sSystemCD = listargs[0],
                            lsTransNo = listargs[1],
                            lsBrnchCd = listargs[2];

                    CreditAppInfo loApp = poSys.LoadApplication(sSystemCD, lsBrnchCd, lsTransNo);
                    if(loApp == null){
                        message = poSys.getMessage();
                        return null;
                    }

                    return loApp;
                } catch (Exception e) {
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return null;
                }

            }

            @Override
            public void OnPostExecute(Object object) {

                CreditAppInfo result = (CreditAppInfo) object;

                if(result == null){
                    listener.OnLoadFailed(message);
                } else {
                    listener.OnLoadSuccess(result);
                }

            }
        });
    }

    public void ApproveApplication(CreditApp.Application foParams, ViewModelCallback listener){

        TaskExecutor.Execute(foParams, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                listener.OnLoadData("Approval Code", "Sending approval request. Please wait...");
            }

            @Override
            public Object DoInBackground(Object args) {

                try{

                    CreditApp.Application params = (CreditApp.Application) args;

                    if(!params.isDataValid()){
                        message = params.getMessage();
                        return null;
                    }

                    if(!poConn.isDeviceConnected()){
                        message = poConn.getMessage();
                        return null;
                    }

                    String lsCode = poSys.GenerateCode(params);

                    if(lsCode == null){
                        message = poSys.getMessage();
                        return null;
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

                String result = object.toString();

                if(result == null){
                    listener.OnFailedResult(message);
                } else {
                    listener.OnSuccessResult(result);
                }

            }
        });
    }

    public interface OnLoadApplicationListener{
        void OnLoad(String Title, String Message);
        void OnLoadSuccess(CreditAppInfo args);
        void OnLoadFailed(String message);
    }
}