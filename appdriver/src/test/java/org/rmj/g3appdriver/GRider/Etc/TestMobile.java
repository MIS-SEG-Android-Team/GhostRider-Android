package org.rmj.g3appdriver.GRider.Etc;

import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class TestMobile {

    String lsMobile = "09275408234";
    String lsregPattern = "^(09|\\+639)\\d{9}$";

    @Test
    public void TestMobile(){
        Assert.assertTrue(lsMobile.matches(lsregPattern));
    }

}
