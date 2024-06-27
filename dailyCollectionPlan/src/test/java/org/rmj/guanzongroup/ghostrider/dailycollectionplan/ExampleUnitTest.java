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

import org.junit.Test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void gettimediff() {
        try {
            SimpleDateFormat sFormat = new SimpleDateFormat("hh:mm:ss a");
            Date currentTime = Calendar.getInstance().getTime();

            String currentTimestr = sFormat.format(currentTime);
            String endTime = "01:25:00 PM";

            long difference = sFormat.parse(endTime).getTime() - sFormat.parse(currentTimestr).getTime();
            System.out.println(difference);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}