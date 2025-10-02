package org.guanzongroup.com.creditevaluation;

import static org.junit.Assert.assertEquals;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExampleUnitTest {
    @Test
    public void test01addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test02String_replace() {
        String lsVal = "*.0";
        String lsResult = lsVal.replace("*", "0");
        assertEquals("0.0", lsResult);
    }

    @Test
    public void test02CheckDouble() {
        double lsResult;
        String sLabelx = "2.0";
        double lnVal = Double.parseDouble(sLabelx);
        if(lnVal % 1 == 0) {
            lsResult =  lnVal;
        } else {
            lnVal = lnVal * 12;
            lsResult = (double) Math.round(lnVal);
        }
        assertEquals(10.0, lsResult, 10.0);
    }
}