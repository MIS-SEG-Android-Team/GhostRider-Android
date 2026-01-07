package org.rmj.g3appdriver.GCircle.ImportData.Obj;

import android.app.Application;

import org.rmj.g3appdriver.GCircle.Apps.SSDD.SSDDEvaluation;
import org.rmj.g3appdriver.GCircle.ImportData.model.ImportDataCallback;
import org.rmj.g3appdriver.GCircle.ImportData.model.ImportInstance;

public class ImportSSDDepartments implements ImportInstance {

    public static final String TAG = ImportProvinces.class.getSimpleName();
    private final SSDDEvaluation poSys;

    public ImportSSDDepartments(Application application){
        this.poSys = new SSDDEvaluation(application);
    }

    @Override
    public void ImportData(ImportDataCallback callback) {
        if(!poSys.DownloadDepartments()){
            callback.OnFailedImportData(poSys.GetMessage());
        } else {
            callback.OnSuccessImportData();
        }
    }
}
