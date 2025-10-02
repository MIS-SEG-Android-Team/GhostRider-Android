/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.authLibrary
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.guanzongroup.authlibrary.UserInterface.ForgotPassword;

import android.app.Application;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.rmj.g3appdriver.lib.Account.AccountMaster;
import org.rmj.g3appdriver.lib.Account.Model.Auth;
import org.rmj.g3appdriver.lib.Account.Model.iAuth;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;
import java.util.concurrent.Future;

public class VMForgotPassword extends AndroidViewModel {
    public static final String TAG = VMForgotPassword.class.getSimpleName();
    private List<Future<String>> futureList;
    private final iAuth poSys;
    private ConnectionUtil conn;
    private Application instance;
    private String message;

    public VMForgotPassword(@NonNull Application application) {
        super(application);
        this.instance = application;
        this.poSys = new AccountMaster(instance).initGuanzonApp().getInstance(Auth.FORGOT_PASSWORD);

        conn = new ConnectionUtil(application);
    }

    public void RequestPassword(String Email, RequestPasswordCallback mListener){
        TaskExecutor taskExecutor = new TaskExecutor();
        taskExecutor.Execute(Email, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                mListener.OnSendRequest("Send Request", "Sending Request . . .");
            }

            @Override
            public Object DoInBackground(Object args) {
                if (!conn.isDeviceConnected()){
                    message = conn.getMessage();
                    return false;
                }

                if (poSys.DoAction((String) args) == 0){
                    message = poSys.getMessage();
                    return false;
                }else {
                    message = poSys.getMessage();
                    return true;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean result = (Boolean) object;

                if (result.equals(false)){
                    mListener.OnFailedRequest(message);
                }else {
                    mListener.OnSuccessRequest();
                }
            }
        });
    }

    public interface RequestPasswordCallback{
        void OnSendRequest(String title, String message);
        void OnSuccessRequest();
        void OnFailedRequest(String message);
    }
}