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
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import org.rmj.g3appdriver.lib.Location.LocationRetriever;
import org.rmj.g3appdriver.utils.Task.ScheduleTask;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_CollectionList;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@SuppressLint("SpecifyJobSchedulerIdRange")
public class GLocatorService extends Service {
    private static final String TAG = GLocatorService.class.getSimpleName();
    private NotificationCompat.Builder loNotif;

    @SuppressLint("NewApi")
    private void createServiceNotification(){
        NotificationChannel loChannel = new NotificationChannel("gRiderLocator", "DCP Location Service", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager loManager = getSystemService(NotificationManager.class);
        loManager.createNotificationChannel(loChannel);
    }

    private int GetIntervalMinutes(){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");

            Date currentTime = simpleDateFormat.parse(simpleDateFormat.format(Calendar.getInstance().getTime()));
            Date endTime = simpleDateFormat.parse("04:40 PM");

            int interval = (int) (endTime.getTime() - currentTime.getTime());

            return interval  / (1000 * 60);
        }catch (Exception e){
            return 0000000;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ScheduleTask.scheduleJob(GetIntervalMinutes(), GetIntervalMinutes(), TimeUnit.MINUTES, new ScheduleTask.onSchedule() {
            @Override
            public void onStart() {
                Intent loIntent = new Intent(GLocatorService.this, Activity_CollectionList.class);
                PendingIntent loPending  = PendingIntent.getActivities(GLocatorService.this, 34, new Intent[]{loIntent}, PendingIntent.FLAG_IMMUTABLE);

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
                Log.d("SERVICE START", "DCP LOCATION STARTED WITHIN " + GetIntervalMinutes());
            }

            @Override
            public void onEnd() {
                stopSelf();
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("NewApi")
    @Override
    public void onDestroy() {
        super.onDestroy();

        stopForeground(STOP_FOREGROUND_REMOVE);
        Log.d("SERVICE STOP", "DCP LOCATION STOP");
    }
}
