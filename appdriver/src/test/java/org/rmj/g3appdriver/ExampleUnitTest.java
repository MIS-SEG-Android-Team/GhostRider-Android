/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.g3appdriver;

import org.junit.Test;
import org.rmj.g3appdriver.etc.AppConstants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private boolean isSuccess = false;

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test01MonthlyDue() throws Exception{
        String lsSampleD = "2022-08-10";
        String lsDayOfMn = lsSampleD.split("-")[2];
        String lsCrtYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
        String lsCrtMnth = new SimpleDateFormat("MM", Locale.getDefault()).format(Calendar.getInstance().getTime());
        String lsDueDate = lsCrtYear + "-" + lsCrtMnth + "-" + lsDayOfMn;
        SimpleDateFormat loFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date loDueDate = loFormatter.parse(lsDueDate);
        Date loCrtDate = loFormatter.parse(AppConstants.CURRENT_DATE());
        Log.d("TESTING", lsDueDate + " is equal to " + AppConstants.CURRENT_DATE());
        int lnResult = loCrtDate.compareTo(loDueDate);
        assertEquals(-1, lnResult);
    }

    @Test
    public void test02GetLastDayOfMonth() {
        String date = "2022-10-03";
        LocalDate lastDayOfMonth = LocalDate.parse(AppConstants.CURRENT_DATE(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .with(TemporalAdjusters.lastDayOfMonth());
        String lsDay = String.valueOf(lastDayOfMonth.getDayOfMonth());
        Log.d("TESTING", lsDay);
        assertNotNull(lastDayOfMonth.toString());
    }

    @Test
    public void test03GetLastDayofMonth(){
        Calendar loCalendar = Calendar.getInstance();

        int lastDay = loCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        loCalendar.set(Calendar.DAY_OF_MONTH, lastDay);

        // Format the result
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        String lastDayString = outputFormat.format(loCalendar.getTime());

        int lnResult = LocalDate.parse(AppConstants.CURRENT_DATE(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).compareTo(
                LocalDate.parse(lastDayString, DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        System.out.println("Last day of month: " + lnResult);
    }

    @Test
    public void testConnectionLogic(){
        boolean isBackUp = false;
        String lsAddress1 = "address1";
        String lsAddress2 = "address2";

        if(isBackUp){
            if(isReachable(lsAddress1)){
                isSuccess = true;
            } else {
                if(isReachable(lsAddress2)){
                    isSuccess = true;
                } else {

                }
            }
        } else {

        }
    }

    private boolean isReachable(String fsVal){
        return fsVal.equalsIgnoreCase("address1");
    }
}