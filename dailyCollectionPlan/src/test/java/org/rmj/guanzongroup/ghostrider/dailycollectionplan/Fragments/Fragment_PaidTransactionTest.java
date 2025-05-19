/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.dailyCollectionPlan
 * Electronic Personnel Access Control Security System
 * project file created : 5/8/21 2:09 PM
 * project file last modified : 5/8/21 2:09 PM
 */

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.Fragments;

import org.junit.Before;
import org.junit.Test;
import org.rmj.g3appdriver.etc.AppConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class Fragment_PaidTransactionTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void newInstance() {
        boolean isPass = false;
        try {
            Date loDate = new SimpleDateFormat("yyyy-MM-dd").parse(new AppConstants().CURRENT_DATE());
            if (new SimpleDateFormat("yyyy-MM-dd").parse(new AppConstants().CURRENT_DATE()).equals(loDate)) {
                isPass = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assertTrue(isPass);
    }
}