package org.rmj.guanzongroup.pacitareward.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DPacita;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.Apps.GawadPacita.Obj.Pacita;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMBranchList extends AndroidViewModel {
    private final Pacita poSys;
    private final ConnectionUtil poConn;


    public VMBranchList(@NonNull Application application) {
        super(application);
        poSys = new Pacita(application);
        poConn = new ConnectionUtil(application);
    }
    public LiveData<List<EBranchInfo>> getBranchlist(){
        return poSys.GetBranchesList();
    }
    public LiveData<List<DPacita.RecentRecords>> getRecentRecords(){
        return poSys.GetRecentRecords();
    }
    public void importCriteria(){
        new ImportCriteriaTask().execute();
    }
    private class ImportCriteriaTask{
        private String TAG = getClass().getSimpleName();
        public void execute(){
            TaskExecutor.Execute(null, new OnDoBackgroundTaskListener() {
                @Override
                public Object DoInBackground(Object args) {
                    if (!poConn.isDeviceConnected()){
                        return poConn.getMessage();
                    }
                    if (!poSys.ImportPacitaRules()){
                        return poSys.getMessage();
                    }
                    return "";
                }

                @Override
                public void OnPostExecute(Object object) {
                    Log.d(TAG, object.toString());
                }
            });
        }
    }
}
