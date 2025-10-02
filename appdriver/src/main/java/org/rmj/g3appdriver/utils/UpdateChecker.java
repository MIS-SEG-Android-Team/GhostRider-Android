/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.g3appdriver.utils;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.gms.tasks.*;

public class UpdateChecker {
    private static final String TAG = UpdateChecker.class.getSimpleName();

    private Context context;
    private AppCompatActivity mActivity;

    public interface OnUpdateCheckListener{
        void OnCheck(AppUpdateInfo result, AppUpdateManager updateManager);
    }

    public UpdateChecker(Context context, AppCompatActivity activity){
        this.context = context;
        mActivity = activity;
    }

    /**
     *
     */
    public void Check_Update(final OnUpdateCheckListener listener) {
        //create instance of the update manager.
        final AppUpdateManager updateManager = AppUpdateManagerFactory.create(context);

        //returns an intent object that you use to check for an update.
        final Task<AppUpdateInfo> appUpdateInfoTask = updateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                listener.OnCheck(result, updateManager);
                Log.e(TAG, String.valueOf(result.updateAvailability()));
            }
        });
    }
    public interface InAppUpdateResult{
        int REQUEST_CODE = 1021;
    }
}
