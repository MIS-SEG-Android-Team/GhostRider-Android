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

import org.rmj.g3appdriver.lib.Etc.Branch;
import org.rmj.g3appdriver.GCircle.ImportData.model.ImportDataCallback;
import org.rmj.g3appdriver.GCircle.ImportData.model.ImportInstance;

public class ImportBranch implements ImportInstance {
    private final Branch poSys;

    public ImportBranch(Application instance){
        this.poSys = new Branch(instance);
    }

    @Override
    public void ImportData(ImportDataCallback callback) {

        if(!poSys.ImportBranches()){
            callback.OnFailedImportData(poSys.getMessage());
        } else {
            callback.OnSuccessImportData();
        }
    }
}
