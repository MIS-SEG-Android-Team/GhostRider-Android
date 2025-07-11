package org.rmj.g3appdriver.etc;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

public class GMSUtility {

    private Context context;

    public interface OnGetUpdate{
        void OnResult(int status);
    }

    public GMSUtility(Context context){
        this.context = context;
    }

    public void GetAppStatus(OnGetUpdate callback){

        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                callback.OnResult(appUpdateInfo.updateAvailability());
            }
        });
    }
}
