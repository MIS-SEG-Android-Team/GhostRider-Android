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

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DRawDao;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;

public class RRawData {

    private DRawDao rawDao;

    public RRawData(Application application){
        GGC_GCircleDB GGCGriderDB = GGC_GCircleDB.getInstance(application);
        rawDao = GGCGriderDB.RawDao();
    }

    public LiveData<DRawDao.AppLocalData> getAppLocalData(){
        return rawDao.getAppLocalData();
    }
}
