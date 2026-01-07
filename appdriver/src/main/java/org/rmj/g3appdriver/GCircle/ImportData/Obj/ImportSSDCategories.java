package org.rmj.g3appdriver.GCircle.ImportData.Obj;

import android.app.Application;

import org.rmj.g3appdriver.GCircle.Apps.SSDD.SSDDEvaluation;
import org.rmj.g3appdriver.GCircle.ImportData.model.ImportDataCallback;
import org.rmj.g3appdriver.GCircle.ImportData.model.ImportInstance;

public class ImportSSDCategories implements ImportInstance {

    public static final String TAG = ImportProvinces.class.getSimpleName();
    private final SSDDEvaluation poSys;

    public ImportSSDCategories(Application application){
        this.poSys = new SSDDEvaluation(application);
    }

    @Override
    public void ImportData(ImportDataCallback callback) {
        if(!poSys.DownloadCategories()){
            callback.OnFailedImportData(poSys.GetMessage());
        } else {
            callback.OnSuccessImportData();
        }
    }
}
