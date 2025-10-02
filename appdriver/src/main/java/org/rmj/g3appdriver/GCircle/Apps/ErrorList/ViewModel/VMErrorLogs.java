package org.rmj.g3appdriver.GCircle.Apps.ErrorList.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DErrorLogs;
import org.rmj.g3appdriver.GCircle.room.Entities.EErrorLogs;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;

import java.util.List;

public class VMErrorLogs extends AndroidViewModel {

    private final DErrorLogs poError;

    public VMErrorLogs(@NonNull Application application) {
        super(application);

        this.poError = GGC_GCircleDB.getInstance(application.getApplicationContext()).errorLogsDao();
    }

    public LiveData<List<EErrorLogs>> GetErrorList(){
        return poError.GetErrorLgList();
    }

    public LiveData<Integer> GetUnreadErrorLogCount(){
        return poError.GetUnreadErrorLogCount();
    }

    public void IsRead(int errorID){
        poError.IsRead(errorID);
    }
}
