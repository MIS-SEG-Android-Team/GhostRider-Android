package org.rmj.g3appdriver;

import android.os.Build;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class TestDate {

    Calendar loCalendar = Calendar.getInstance();

    @Test
    public void TestLocalDateFormat(){

        loCalendar.set(2026, 2, 26);
        System.out.println(loCalendar.get(Calendar.MONTH));
        System.out.println(loCalendar.get(Calendar.DATE));
        System.out.println(loCalendar.get(Calendar.YEAR));

        LocalDate localDate = LocalDate.now();
        LocalDate currentDate = LocalDate.parse(localDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        LocalDate compDate = LocalDate.parse("2026/02/28", DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        System.out.println(currentDate.isAfter(compDate));
        System.out.println(compDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
    }

    @Test
    public void TestCalendarSimple(){

        try{

            Calendar loCalendar = Calendar.getInstance();

            Date currentDate = loCalendar.getTime();
            Date compDate = new SimpleDateFormat("yyyy/MM/dd").parse("2026/02/28");

            System.out.println(currentDate.before(compDate));
            System.out.println(new SimpleDateFormat("MMMM dd, yyyy").format(compDate));

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
