/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.app
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.guanzongroup.ghostrider.epacss.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj.EmployeeLoan;
import org.rmj.g3appdriver.GCircle.room.Entities.ETokenInfo;
import org.rmj.g3appdriver.GCircle.room.Repositories.AppTokenManager;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.lib.Etc.Barangay;
import org.rmj.g3appdriver.lib.Etc.Branch;
import org.rmj.g3appdriver.lib.Etc.Province;
import org.rmj.g3appdriver.lib.Etc.Town;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnLoadApplicationListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;
import org.rmj.guanzongroup.ghostrider.epacss.BuildConfig;

public class VMSplashScreen extends AndroidViewModel {
    private static final String TAG = VMSplashScreen.class.getSimpleName();

    private final Application instance;

    private final ConnectionUtil poConn;
    private final AppConfigPreference poConfig;
    private final EmployeeMaster poUser;
    private final EmployeeSession poSession;

    private String message;

    public VMSplashScreen(@NonNull Application application) {
        super(application);

        this.instance = application;
        this.poConn = new ConnectionUtil(instance);
        this.poConfig = AppConfigPreference.getInstance(instance);
        this.poUser = new EmployeeMaster(instance);
        this.poSession = EmployeeSession.getInstance(instance);
        this.poConfig.setPackageName(BuildConfig.APPLICATION_ID);
        this.poConfig.setProductID("gRider");
        this.poConfig.setUpdateLocally(false);
        this.poConfig.setTestCase(false);
        this.poConfig.setupAppVersionInfo(BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME, "");

        ETokenInfo loToken = new ETokenInfo();
        loToken.setTokenInf("temp_token");
        CheckConnection();
    }

    public void SaveFirebaseToken(String fsVal){
        TaskExecutor.Execute(fsVal, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                return new AppTokenManager(instance).SaveFirebaseToken((String) args);
            }
            @Override
            public void OnPostExecute(Object object) {
            }
        });
    }

    private void CheckConnection(){
        TaskExecutor.Execute(null, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                if(!poConn.isDeviceConnected()){
                    message = poConn.getMessage();
                    return false;
                }

                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                boolean isSuccess = (boolean) object;
                if(isSuccess){
                    Toast.makeText(instance, "Device connected", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(instance, "Offline mode", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void InitializeData(OnInitializeCallback mListener){
        TaskExecutor loTask = new TaskExecutor();
        loTask.setOnLoadApplicationListener(new OnLoadApplicationListener() {
            @Override
            public Object DoInBackground() {
                try{
                    if(poConn.isDeviceConnected()){
                        if(!new Branch(instance).ImportBranches()){
                            Log.e(TAG, "Unable to import branches");
                        }
                        loTask.publishProgress(1);

                        Thread.sleep(1000);
                        Log.d(TAG, "Initializing province data.");
                        if(!new Province(instance).ImportProvince()){
                            Log.e(TAG, "Unable to import province");
                        }
                        loTask.publishProgress(2);

                        Thread.sleep(1000);
                        Log.d(TAG, "Initializing town data.");
                        if(!new Town(instance).ImportTown()){
                            Log.e(TAG, "Unable to import town");
                        }
                        loTask.publishProgress(3);

                        Thread.sleep(1000);
                        Log.d(TAG, "Initializing barangay data.");
                        if(!new Barangay(instance).ImportBarangay()){
                            Log.e(TAG, "Unable to import barangay");
                        }
                        loTask.publishProgress(4);

                        Thread.sleep(1000);
                        if (!new EmployeeLoan(instance).ImportLoanTypes()){
                            Log.e(TAG, "Unable to import loan types");
                        }
                        loTask.publishProgress(5);

                        if(!poSession.isLoggedIn()){
                            return 2;
                        }

                        if(!poUser.IsSessionValid()){
                            return 2;
                        }

                        return 1;
                    } else {
                        if(!poConfig.isAppFirstLaunch()){
                            message = "Offline Mode.";
                            return 1;
                        }

                        message = "Please connect you device to internet to download data.";
                        return 0;
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return 0;
                }
            }

            @Override
            public void OnProgress(int progress) {
                String lsArgs;
                if(poConfig.isAppFirstLaunch()){
                    lsArgs = "Importing Data...";
                } else {
                    lsArgs = "Updating Data...";
                }
                if(progress < 5) {
                    mListener.OnProgress(lsArgs, progress);
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                int result = (int) object;
                switch (result){
                    case 0:
                        mListener.OnFailed(message);
                        break;
                    case 1:
                        mListener.OnSuccess();
                        break;
                    default:
                        mListener.OnNoSession();
                        break;
                }
            }
        });
        loTask.Execute();
    }

    public interface OnInitializeCallback {
        void OnProgress(String args, int progress);
        void OnSuccess();
        void OnNoSession();
        void OnFailed(String message);
    }
}
