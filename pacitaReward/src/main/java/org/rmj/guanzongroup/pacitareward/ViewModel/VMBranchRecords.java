package org.rmj.guanzongroup.pacitareward.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DPacita.BranchRecords;
import org.rmj.g3appdriver.GCircle.Apps.GawadPacita.Obj.Pacita;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMBranchRecords extends AndroidViewModel {
    private final Pacita posys;
    private final ConnectionUtil poConn;
    private String message;

    public VMBranchRecords(@NonNull Application application) {
        super(application);

        this.posys = new Pacita(application);
        this.poConn = new ConnectionUtil(application);
    }

    public interface BranchRecordsCallBack{
        void onInitialize(String message);
        void onSuccess(String message);
        void onError(String message);
    }

    public LiveData<List<BranchRecords>> getBranchRecords(String sBranchcd){
        return posys.GetBranchRecords(sBranchcd);
    }
    public void initializeRecords(String sBranchcd, BranchRecordsCallBack mListener){
        new OnInitializeRecords(mListener).execute(sBranchcd);
    }

    public class OnInitializeRecords{
        private BranchRecordsCallBack mListener;
        private OnInitializeRecords(BranchRecordsCallBack mListener){
            this.mListener = mListener;
        }
        public void execute(String sBranchcd){
            TaskExecutor.Execute(sBranchcd, new OnTaskExecuteListener() {
                @Override
                public void OnPreExecute() {
                    mListener.onInitialize("Loading Branch Records. Please wait . . .");
                }

                @Override
                public Object DoInBackground(Object args) {
                    Boolean importResult = posys.ImportPacitaEvaluations((String) args);

                    if (!poConn.isDeviceConnected()){
                        message = poConn.getMessage();
                        return false;
                    }
                    if (!importResult){
                        message = posys.getMessage();
                        return false;
                    }
                    return true;
                }

                @Override
                public void OnPostExecute(Object object) {
                    Boolean aBoolean = (Boolean) object;
                    if (aBoolean == false){
                        mListener.onError(message);
                    }else {
                        mListener.onSuccess(message);
                    }
                }
            });
        }
    }
}
