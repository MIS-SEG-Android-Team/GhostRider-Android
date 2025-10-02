package org.rmj.guanzongroup.pacitareward.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.Entities.EPacitaEvaluation;
import org.rmj.g3appdriver.GCircle.room.Entities.EPacitaRule;
import org.rmj.g3appdriver.GCircle.Apps.GawadPacita.Obj.Pacita;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMBranchRecordDetails extends AndroidViewModel {
    private final Pacita posys;
    private final ConnectionUtil poConn;
    private String message;

    public VMBranchRecordDetails(@NonNull Application application) {
        super(application);
        posys = new Pacita(application);
        poConn = new ConnectionUtil(application);
    }

    public interface BranchRecordDetailsCallBack{
        void onInitialize(String message);
        void onSuccess(String message);
        void onError(String message);
    }
    public LiveData<List<EPacitaRule>> GetCriteria(){
        return posys.GetPacitaRules();
    }
    public LiveData<EPacitaEvaluation> getBranchEvaluation(String transactNo){
        return posys.GetEvaluationRecord(transactNo);
    }
    public void onEvaluationRecords(String sBranchcd, BranchRecordDetailsCallBack mListener){
        new EvaluationRecordDetails(mListener).execute(sBranchcd);
    }

    public class EvaluationRecordDetails{
        private BranchRecordDetailsCallBack mListener;
        private EvaluationRecordDetails(BranchRecordDetailsCallBack mListener){
            this.mListener = mListener;
        }
        public void execute(String sBranchcd){
            TaskExecutor.Execute(sBranchcd, new OnTaskExecuteListener() {
                @Override
                public void OnPreExecute() {
                    mListener.onInitialize("Loading Branch Record Details. Please wait . . .");
                }

                @Override
                public Object DoInBackground(Object args) {
                    if(!poConn.isDeviceConnected()){
                        message = poConn.getMessage();
                        return false;
                    }
                    return true;
                }

                @Override
                public void OnPostExecute(Object object) {
                    Boolean result = (Boolean) object;
                    if (result == false){
                        mListener.onError(message);
                    }else {
                        mListener.onSuccess(message);
                    }
                }
            });
        }
    }
}
