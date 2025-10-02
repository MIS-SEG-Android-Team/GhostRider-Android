/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 4/30/21 10:17 AM
 * project file last modified : 4/30/21 10:17 AM
 */

package org.rmj.g3appdriver.utils;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class ServiceScheduler {
    private static final String TAG = ServiceScheduler.class.getSimpleName();
    private static JobScheduler loScheduler;

    public static long FIFTEEN_MINUTE_PERIODIC = 900000;

    public ServiceScheduler(Context context){
        loScheduler = (JobScheduler)context.getSystemService(JOB_SCHEDULER_SERVICE);
    }

    @SuppressLint({"MissingPermission", "NewApi"})
    public static boolean isJobRunning(int JobID){
        boolean hasBeenScheduled = false ;
        for ( JobInfo jobInfo : loScheduler.getAllPendingJobs() ) {
            if (jobInfo.getId() == JobID) {
                hasBeenScheduled = true ;
                break ;
            }
        }

        return hasBeenScheduled;
    }

    @SuppressLint({"MissingPermission", "NewApi"})
    public static void scheduleJob(Context context, Class<?> jobService, long periodic, int ServiceID){
        ComponentName loComp = new ComponentName(context, jobService);
        JobInfo loJob = new JobInfo.Builder(ServiceID, loComp)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(periodic)
                .build();

        loScheduler = (JobScheduler)context.getSystemService(JOB_SCHEDULER_SERVICE);
        int liResult = loScheduler.schedule(loJob);

        if(liResult == JobScheduler.RESULT_SUCCESS){
            Log.e(TAG, jobService.getSimpleName() + " Service Scheduled");
        } else {
            Log.e(TAG, jobService.getSimpleName() + " Service Scheduled Failed.");
        }
    }

    public static void stopSchedule(int JobID){
        loScheduler.cancel(JobID);
    }
}
