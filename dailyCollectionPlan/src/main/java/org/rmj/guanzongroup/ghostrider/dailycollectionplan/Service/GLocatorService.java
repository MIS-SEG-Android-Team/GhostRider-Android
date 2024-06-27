/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.dailyCollectionPlan
 * Electronic Personnel Access Control Security System
 * project file created : 6/24/21 3:04 PM
 * project file last modified : 6/24/21 3:04 PM
 */

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.Service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import org.rmj.g3appdriver.GCircle.room.Repositories.RSysConfig;
import org.rmj.g3appdriver.lib.Location.LocationRetriever;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_CollectionList;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;

@SuppressLint("SpecifyJobSchedulerIdRange")
public class GLocatorService extends JobService {
    private static final String TAG = GLocatorService.class.getSimpleName();
    private NotificationCompat.Builder loNotif;
    private RSysConfig poConfig;

    @SuppressLint("NewApi")
    private void createServiceNotification(){
        NotificationChannel loChannel = new NotificationChannel("gRiderLocator", "DCP Location Service", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager loManager = getSystemService(NotificationManager.class);
        loManager.createNotificationChannel(loChannel);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loIntent = new Intent(GLocatorService.this, Activity_CollectionList.class);
                PendingIntent loPending  = PendingIntent.getActivities(GLocatorService.this, 34, new Intent[]{loIntent}, PendingIntent.FLAG_IMMUTABLE);
                poConfig = new RSysConfig(getApplication());

                new LocationRetriever(getApplication()).GetLocationOnBackgroud();

                createServiceNotification();

                loNotif = new NotificationCompat.Builder(GLocatorService.this, "gRiderLocator");
                loNotif.setContentTitle("Daily Collection Plan");
                loNotif.setDefaults(NotificationCompat.DEFAULT_ALL);
                loNotif.setSmallIcon(R.drawable.ic_location_tracker);
                loNotif.setContentText("DCP location service is running...");
                loNotif.setContentIntent(loPending);
                loNotif.setAutoCancel(false);
                loNotif.setPriority(NotificationCompat.PRIORITY_MAX);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(1, loNotif.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
                } else {
                    startForeground(1, loNotif.build());
                }

                Log.d("SERVICE START", "DCP LOCATION STARTED");
            }
        },1000);

        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        stopForeground(STOP_FOREGROUND_REMOVE);
        Log.d("SERVICE STOP", "DCP LOCATION STOP");
        return true;
    }
}
