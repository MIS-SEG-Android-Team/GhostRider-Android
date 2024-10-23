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

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCreditApplicationDocuments;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicationDocuments;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;

import java.util.List;

public class RCreditApplicationDocument {

    private static final String TAG = "DB_Document_Repository";
    private final DCreditApplicationDocuments documentsDao;
    private final Application application;

    public RCreditApplicationDocument(Application application){
        this.application = application;
        GGC_GCircleDB GGCGriderDB = GGC_GCircleDB.getInstance(application);
        this.documentsDao = GGCGriderDB.DocumentInfoDao();
    }

    /**
     *
     * @return returns a LiveData List of all unsent image info...
     */

    public List<ECreditApplicationDocuments> getUnsentApplicationDocumentss() throws Exception{
        return documentsDao.getUnsentApplicationDocumentss();
    }

}
