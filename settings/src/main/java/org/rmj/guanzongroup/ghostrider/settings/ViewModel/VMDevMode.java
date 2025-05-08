/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.settings
 * Electronic Personnel Access Control Security System
 * project file created : 8/14/21 11:33 AM
 * project file last modified : 8/14/21 11:33 AM
 */

package org.rmj.guanzongroup.ghostrider.settings.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.GCircle.Etc.DevTools;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

public class VMDevMode extends AndroidViewModel {

    private final Application instance;
    private final AppConfigPreference poConfig;
    private final DevTools poTool;
    private String message;

    public interface OnChangeListener {
        void OnChanged(String args, Boolean isSuccess);
    }

    public VMDevMode(@NonNull Application application) {
        super(application);
        this.instance = application;
        this.poTool = new DevTools(instance);
        this.poConfig = AppConfigPreference.getInstance(application);
    }

    public LiveData<EEmployeeInfo> GetUserInfo(){
        return poTool.GetUserInfo();
    }

    public void SaveChanges(EEmployeeInfo args, String ipserver, OnChangeListener callback){

        TaskExecutor.Execute(args, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {

                poConfig.setAppServer(ipserver);

                EEmployeeInfo eEmployeeInfos = (EEmployeeInfo) args;

                if(!poTool.Update(eEmployeeInfos)){
                    message = poTool.getMessage();
                    return false;
                }

                return true;

            }

            @Override
            public void OnPostExecute(Object object) {

                Boolean isSuccess = (Boolean) object;

                if(!isSuccess){
                    callback.OnChanged(message, isSuccess);
                } else {
                    callback.OnChanged("Changes Saved!", isSuccess);
                }

            }
        });
    }

    public void RestoreDefault(String ipserver, OnChangeListener callback){

        TaskExecutor.Execute(ipserver, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {

                String ipServer = (String) args;
                poConfig.setAppServer(ipServer);

                if(!poTool.SetDefault()){
                    message = poTool.getMessage();
                    return false;
                }

                return true;

            }

            @Override
            public void OnPostExecute(Object object) {

                Boolean isSuccess = (Boolean) object;

                if(!isSuccess){
                    callback.OnChanged(message, isSuccess);
                } else {
                    callback.OnChanged("Account restored to default.", isSuccess);
                }

            }
        });
    }

}
