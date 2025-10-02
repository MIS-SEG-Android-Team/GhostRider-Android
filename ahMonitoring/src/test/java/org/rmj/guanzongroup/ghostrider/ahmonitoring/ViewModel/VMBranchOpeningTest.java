/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 10/16/21, 9:46 AM
 * project file last modified : 10/16/21, 9:46 AM
 */

package org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel;

import android.os.Build;

import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchOpeningMonitor;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1}, manifest= Config.NONE)
public class VMBranchOpeningTest {
    private VMBranchOpening mViewModel;

    @Before
    public void setUp() {
        mViewModel = new VMBranchOpening(ApplicationProvider.getApplicationContext());
    }

    @After
    public void tearDown() {
        mViewModel = null;
    }

    @Test
    public void testGetUserInfo() {
        mViewModel.getUserInfo().observeForever(new Observer<EEmployeeInfo>() {
            @Override
            public void onChanged(EEmployeeInfo eEmployeeInfo) {
                Assert.assertNotNull(eEmployeeInfo);
            }
        });
    }

    @Test
    public void testGetDateSelected() {
        mViewModel.getDateSelected().observeForever(new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Assert.assertNotNull(s);
            }
        });
    }

    @Test
    public void testGetBranchOpeningsForDate() {
        mViewModel.getBranchOpeningsForDate("01/01/2021").observeForever(new Observer<List<DBranchOpeningMonitor.BranchOpeningInfo>>() {
            @Override
            public void onChanged(List<DBranchOpeningMonitor.BranchOpeningInfo> branchOpeningInfos) {
                Assert.assertNotNull(branchOpeningInfos);
            }
        });
    }
}
