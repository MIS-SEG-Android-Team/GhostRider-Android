package org.rmj.guanzongroup.onlinecreditapplication.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditOnlineApplication;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCreditApplication;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplication;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMCreditApplications extends AndroidViewModel {
    private static final String TAG = VMCreditApplications.class.getSimpleName();
    private String message;

    private final CreditOnlineApplication poApp;
    private final ConnectionUtil poConn;

    public interface OnImportApplicationsListener{
        void OnImport();
        void OnSuccess();
        void OnFailed(String message);
    }

    public interface OnResendApplicationListener{
        void OnResend();
        void OnSuccess();
        void OnFailed(String message);
    }

    public interface OnSearchLSerial{
        void OnSuccess(List<CreditOnlineApplication.MCSerial> laSerials);
        void OnFailed(String message);
    }

    public VMCreditApplications(@NonNull Application application) {
        super(application);
        this.poApp = new CreditOnlineApplication(application);
        this.poConn = new ConnectionUtil(application);
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> GetUserInfo(){
        return poApp.GetUserInfo();
    }

    public LiveData<List<DCreditApplication.ApplicationLog>> GetApplicationList(){
        return poApp.GetCreditApplications();
    }

    public EBranchInfo getBranchInfo(String fsBranchCd){
        return poApp.getBranchInfoNonLive(fsBranchCd);
    }

    public ECreditApplication GetApplication(String fsTransNox){
        return poApp.GetApplication(fsTransNox);
    }

    public void GetSerials(String fsVal, OnSearchLSerial foListener){

        TaskExecutor.Execute(fsVal, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {

                Object[] laResult = new Object[2];

                if (!poConn.isDeviceConnected()){
                    message = poConn.getMessage();

                    laResult[0] = false;
                    laResult[1] = null;
                    return laResult;
                }

                List<CreditOnlineApplication.MCSerial> laSerials = poApp.DownloadSerials(fsVal);
                if (laSerials == null){
                    message = poApp.getMessage();

                    laResult[0] = false;
                    laResult[1] = null;
                    return laResult;
                }
                laResult[0] = true;
                laResult[1] = laSerials;
                return laResult;
            }

            @Override
            public void OnPostExecute(Object object) {
                Object[] loResult = (Object[]) object;

                if (!(Boolean) loResult[0]){
                    foListener.OnFailed(message);
                } else {
                    if (loResult[1] == null){
                        foListener.OnFailed("Invalid result found!");
                        return;
                    }
                    foListener.OnSuccess((List<CreditOnlineApplication.MCSerial>) loResult[1]);
                }
            }
        });
    }

    public void ImportApplications(OnImportApplicationsListener listener){
        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                listener.OnImport();
            }

            @Override
            public Object DoInBackground(Object args) {
                if(!poConn.isDeviceConnected()){
                    message = poApp.getMessage();
                    return false;
                }

                if(!poApp.DownloadApplications()){
                    message = poApp.getMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean lsSuccess = (Boolean) object;
                if(!lsSuccess){
                    listener.OnFailed(message);
                } else {
                    listener.OnSuccess();
                }
            }
        });
    }

    public void ResendApplication(String ars, OnResendApplicationListener listener){

        TaskExecutor.Execute(ars, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {

                if(!poConn.isDeviceConnected()){
                    message = poConn.getMessage();
                    return false;
                }
                String lsString = (String) args;

                if(!poApp.UploadApplication(lsString)){
                    message = poApp.getMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean lsSuccess = (Boolean) object;
                if(!lsSuccess){
                    listener.OnFailed(message);
                } else {
                    listener.OnSuccess();
                }
            }
        });
    }
}