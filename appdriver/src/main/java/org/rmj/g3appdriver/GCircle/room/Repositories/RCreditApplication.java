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

package org.rmj.g3appdriver.GCircle.room.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCreditApplication;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplication;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import java.util.List;

public class RCreditApplication {

    private final DCreditApplication creditApplicationDao;
    private final LiveData<List<ECreditApplication>> allCreditApplication;

    private final Application app;

    public RCreditApplication(Application application){
        this.app = application;
        GGC_GCircleDB GGCGriderDB = GGC_GCircleDB.getInstance(application);
        creditApplicationDao = GGCGriderDB.CreditApplicationDao();
        allCreditApplication = creditApplicationDao.GetAllCreditApplication();
    }

    public List<ECreditApplication> getUnsentLoanApplication() throws Exception{
        return creditApplicationDao.getUnsentLoanApplication();
    }

}
