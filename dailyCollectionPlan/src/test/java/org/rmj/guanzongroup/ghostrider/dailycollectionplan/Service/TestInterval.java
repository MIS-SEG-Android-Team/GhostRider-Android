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

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.Service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(JUnit4.class)
public class TestInterval {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");

    @Test
    public void TestIntervalDiff() {
        System.out.println(GetIntervalMinutes());
    }

    private long GetIntervalMinutes(){
        try {
            Date date1 = simpleDateFormat.parse(simpleDateFormat.format(Calendar.getInstance().getTime()));
            Date date2 = simpleDateFormat.parse("2:25 PM");

            long interval = date2.getTime() - date1.getTime();

            return interval / (1000 * 60);
        }catch (Exception e){
            return 0000000;
        }
    }
}