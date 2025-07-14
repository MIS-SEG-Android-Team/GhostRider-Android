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

package org.rmj.g3appdriver.GCircle.ImportData.Obj;

import android.app.Application;
import android.util.Log;

import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.ApprovalCode;
import org.rmj.g3appdriver.GCircle.ImportData.model.ImportDataCallback;
import org.rmj.g3appdriver.GCircle.ImportData.model.ImportInstance;

public class Import_SCARequest implements ImportInstance {
    public static final String TAG = Import_SCARequest.class.getSimpleName();
    private final ApprovalCode poSys;
    private String stamp = "";

    public Import_SCARequest(Application instance) {
        this.poSys = new ApprovalCode(instance);
    }

    public void setTimeStamp(String stamp){
        this.stamp = stamp;
    }

    @Override
    public void ImportData(ImportDataCallback callback) {
        if(!poSys.ImportSCARequest()){
            callback.OnFailedImportData(poSys.getMessage());
        } else {
            callback.OnSuccessImportData();
        }

        if (!poSys.ImportSCARequestEmp(stamp)){
            callback.OnFailedImportData(poSys.getMessage());
        }else {
            callback.OnSuccessImportData();
        }
    }

}
