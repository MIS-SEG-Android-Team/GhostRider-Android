/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.ahmonitoring;

import org.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testAPI() throws Exception{
//        HashMap param = new HashMap<String, String>();
//        param.put("email", "sampleEmail123@domain.com");
//        param.put("password", "12345678");
//        String lsResult = WebClient.httpPostJSon("http://192.168.10.22/android_sample/login.php", param.toString(), null);
//        assertNotNull(lsResult);

        String lsVal = "13,470.75";
        int lnVal = 0;
        lnVal = Integer.parseInt(lsVal.replace(",", ""));
        assertEquals(13470.75, lnVal, 13470.75);
    }

    @Test
    public void test01CalculateWithPay() {

    }
}