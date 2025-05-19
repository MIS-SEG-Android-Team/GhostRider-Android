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

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.Model;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.MobileUpdate;

public class MobileUpdateTest extends TestCase {
    private MobileUpdate infoModel;

    private static final String STRING_ZERO = "0";
    private static final String STRING_ONE = "1";
    private static final String STRING_TWO = "2";
    private static final String FAKE_MOBILE = "09123456789";
    private static final String FAKE_REMARKS = "Fake Remarks";

    @Before
    public void setUp() {
        infoModel = new MobileUpdate();
        infoModel.setPrimaryx(STRING_TWO);
        infoModel.setMobileNo(FAKE_MOBILE);
        infoModel.setRemarksx(FAKE_REMARKS);
    }

    @Test
    public void testIsDataValid() {
        assertTrue(infoModel.isDataValid());
    }
}