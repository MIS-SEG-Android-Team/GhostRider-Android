package org.rmj.g3appdriver.etc;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

public class GMSUtility {

    private final Activity context;

    public interface OnGetUpdate{
        void OnResult(int status);
    }

    public interface OnUpdateResult{
        void OnDownloading(int status);
        void OnSuccess();
        void OnFailed(String message);
    }

    public GMSUtility(Activity context){
        this.context = context;
    }

    public void GetUpdate(OnGetUpdate callback){

        try{

            AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);
            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                callback.OnResult(appUpdateInfo.updateAvailability());
            });

        }catch (Exception e){
            callback.OnResult(1);
            e.printStackTrace();
        }
    }

    public void StartUpdate(OnUpdateResult callback){

        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){

                try {

                    appUpdateManager.startUpdateFlow(
                            appUpdateInfo,
                            context,
                            AppUpdateOptions.newBuilder(
                                    AppUpdateType.FLEXIBLE
                            ).build());

                    appUpdateManager.registerListener(new InstallStateUpdatedListener() {
                        @Override
                        public void onStateUpdate(@NonNull InstallState installState) {

                            if (installState.installStatus() == InstallStatus.DOWNLOADING ||
                                    installState.installStatus() == InstallStatus.INSTALLING){

                                int bytesDownloaded = (int) installState.bytesDownloaded() * 100;
                                int totalBytesToDownload = (int) installState.totalBytesToDownload();

                                callback.OnDownloading(bytesDownloaded / totalBytesToDownload);

                            } else if (installState.installStatus() == InstallStatus.DOWNLOADED ||
                                    installState.installStatus() == InstallStatus.INSTALLED) {

                                appUpdateManager.unregisterListener(this);
                                callback.OnSuccess();
                            }else {

                                appUpdateManager.unregisterListener(this);
                                if (installState.installStatus() == InstallStatus.FAILED){

                                    callback.OnFailed("Error code: " + installState.installErrorCode() + " on update");
                                }else {
                                    callback.OnFailed("Failed to update");
                                }
                            }
                        }
                    });

                }catch (Exception e){
                    callback.OnFailed(e.getMessage());
                }
            }else {
                callback.OnFailed("Could not start update. Go to Google Play");
            }
        });
    }
}
