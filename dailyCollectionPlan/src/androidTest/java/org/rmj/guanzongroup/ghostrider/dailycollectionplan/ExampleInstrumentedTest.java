/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.dailyCollectionPlan
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.dailycollectionplan;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.utils.ServiceScheduler;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Activities.Activity_CollectionList;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Service.GLocatorService;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void TestDCPService() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        //ServiceScheduler.scheduleJob(appContext, GLocatorService.class, GetServiceInterval(), AppConstants.GLocatorServiceID);
        if (ServiceScheduler.isJobRunning(AppConstants.GLocatorServiceID)){
            System.out.println("DCP Location is running...");
        }else {
            System.out.println("DCP Location stoppped...");
        }
    }

    private long GetServiceInterval(){
        try {
            SimpleDateFormat sFormat = new SimpleDateFormat("hh:mm:ss a");
            Date currentTime = Calendar.getInstance().getTime();

            String currentTimestr = sFormat.format(currentTime);
            String endTime = "11:30:00 AM";

            long interval = sFormat.parse(endTime).getTime() - sFormat.parse(currentTimestr).getTime();
            return interval;
        }catch (Exception e){
            return 0000000;
        }
    }
}