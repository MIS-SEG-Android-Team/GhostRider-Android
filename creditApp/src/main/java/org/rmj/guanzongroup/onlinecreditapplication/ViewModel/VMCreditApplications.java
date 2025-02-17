package org.rmj.guanzongroup.onlinecreditapplication.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditOnlineApplication;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCreditApplication;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
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

    public void ImportApplications(OnImportApplicationsListener listener){
//        new ImportApplicationsTask(listener).execute();
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

//

    public void ResendApplication(String ars, OnResendApplicationListener listener){
//        new ResendApplicationTask(listener).execute(ars);
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
//private class ImportApplicationsTask extends AsyncTask<Void, Void, Boolean>{
//
//        private final OnImportApplicationsListener listener;
//        private String message;
//
//        public ImportApplicationsTask(OnImportApplicationsListener listener) {
//            this.listener = listener;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            listener.OnImport();
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... voids) {
//            if(!poConn.isDeviceConnected()){
//                message = poApp.getMessage();
//                return false;
//            }
//
//            if(!poApp.DownloadApplications()){
//                message = poApp.getMessage();
//                return false;
//            }
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean isSuccess) {
//            super.onPostExecute(isSuccess);
//            if(!isSuccess){
//                listener.OnFailed(message);
//            } else {
//                listener.OnSuccess();
//            }
//        }
//    }
//private class ResendApplicationTask extends AsyncTask<String, Void, Boolean> {
//
//    private final VMCreditApplications.OnResendApplicationListener listener;
//
//    private String message;
//
//    public ResendApplicationTask(VMCreditApplications.OnResendApplicationListener listener) {
//        this.listener = listener;
//    }
//
//    @Override
//    protected Boolean doInBackground(String... strings) {
//        if(!poConn.isDeviceConnected()){
//            message = poConn.getMessage();
//            return false;
//        }
//
//        if(!poApp.UploadApplication(strings[0])){
//            message = poApp.getMessage();
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    protected void onPostExecute(Boolean isSuccess) {
//        super.onPostExecute(isSuccess);
//        if(!isSuccess){
//            listener.OnFailed(message);
//        } else {
//            listener.OnSuccess();
//        }
//    }
//}