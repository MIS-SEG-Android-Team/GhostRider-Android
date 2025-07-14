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
package org.rmj.guanzongroup.authlibrary.UserInterface.Login;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import static org.rmj.g3appdriver.lib.Firebase.CrashReportingUtil.reportException;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.rmj.g3appdriver.dev.Device.Telephony;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.lib.Account.AccountMaster;
import org.rmj.g3appdriver.lib.Account.Model.Auth;
import org.rmj.g3appdriver.lib.Account.Model.iAuth;
import org.rmj.g3appdriver.lib.Account.pojo.UserAuthInfo;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

public class VMLogin extends AndroidViewModel {
    public static final String TAG =  VMLogin.class.getSimpleName();
    private final AppConfigPreference poConfig;
    private final iAuth poSys;
    private String message;

    public VMLogin(@NonNull Application application) {
        super(application);
        this.poSys = new AccountMaster(application).initGuanzonApp().getInstance(Auth.AUTHENTICATE);
        this.poConfig = AppConfigPreference.getInstance(application);
    }

    public boolean isAgreed(){
        return poConfig.isAgreedOnTermsAndConditions();
    }

    public void setAgreedOnTerms(boolean isAgreed){
        poConfig.setAgreement(isAgreed);
    }

    public void Login(UserAuthInfo authInfo, LoginCallback callback){
        TaskExecutor.Execute(authInfo, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnAuthenticationLoad("Guanzon Circle", "Authenticating to ghostrider app. Please wait...");
            }

            @Override
            public Object DoInBackground(Object args) {
                try{
                    int lnResult = poSys.DoAction(args);
                    if(lnResult == 0){
                        message = poSys.getMessage();
                        return false;
                    }

                    if(lnResult == 2){
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
                boolean isSuccess = (boolean) object;
                if(isSuccess){
                    callback.OnSuccessLoginResult();
                } else {
                    callback.OnFailedLoginResult(message);
                }
            }
        });
    }
}