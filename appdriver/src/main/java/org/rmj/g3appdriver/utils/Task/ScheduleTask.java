package org.rmj.g3appdriver.utils.Task;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduleTask {

    public static Boolean isServiceRunning(Context context, String serviceName){
        ActivityManager manageService = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Boolean isRunning = false;

        for (ActivityManager.RunningServiceInfo service : manageService.getRunningServices(Integer.MAX_VALUE)) {
            Log.d("ScheduleTask", service.service.getClassName() + " is running . . .");
            Log.d("ScheduleTask", serviceName);

            if (serviceName.equals(service.service.getClassName())) {
                isRunning = true;
                break;
            }
        }

        return isRunning;
    }

    public static void scheduleJob(int startInterval, int endInterval, TimeUnit intervalUnit, onSchedule callback){

        callback.onStart(); //todo: start task

        ScheduledExecutorService thread = Executors.newSingleThreadScheduledExecutor();
        thread.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                //todo: endtask
                callback.onEnd();
                thread.shutdown();
            }
        }, startInterval, endInterval, intervalUnit);
    }

    public interface onSchedule{
        void onStart();
        void onEnd();
    }
}
