package org.rmj.g3appdriver.GCircle.ImportData;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;

import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

public class ImportEmployeeRole {
    private static final String TAG = ImportEmployeeRole.class.getSimpleName();

    private final Application instance;
    private final EmployeeMaster poUser;
    private final ConnectionUtil poConn;

    private String message;

    public interface OnImportEmployeeRoleCallback{
        void OnRequest();
        void OnSuccess();
        void OnFailed(String message);
    }

    public ImportEmployeeRole(Application application) {
        this.instance = application;
        this.poUser = new EmployeeMaster(instance);
        this.poConn = new ConnectionUtil(instance);
    }

    public void RefreshEmployeeRole(OnImportEmployeeRoleCallback callback){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnRequest();
            }

            @Override
            public Object DoInBackground(Object args) {

                try{
                    if(!poConn.isDeviceConnected()){
                        message = poConn.getMessage();
                        return false;
                    }

                    if(!poUser.GetUserAuthorizeAccess()){
                        message = poUser.getMessage();
                        return false;
                    } else {
                        return true;
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return false;
                }

            }

            @Override
            public void OnPostExecute(Object object) {

                try{

                    Boolean isSuccess = (Boolean) object;

                    if(isSuccess){
                        callback.OnSuccess();
                    } else {
                        callback.OnFailed(message);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

}
