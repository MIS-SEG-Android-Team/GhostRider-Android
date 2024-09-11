package org.rmj.guanzongroup.pacitareward.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.rmj.g3appdriver.GCircle.room.Entities.EPacitaEvaluation;
import org.rmj.g3appdriver.GCircle.room.Entities.EPacitaRule;
import org.rmj.g3appdriver.GCircle.Apps.GawadPacita.Obj.Pacita;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMBranchRate extends AndroidViewModel {
    private static final String TAG = VMBranchRate.class.getSimpleName();

    private final Pacita poSys;
    private ConnectionUtil poConnection;
    private String message;

    public VMBranchRate(@NonNull Application application) {
        super(application);

        poSys = new Pacita(application);
        poConnection = new ConnectionUtil(application);
    }

    public interface OnInitializeBranchEvaluationListener {
        void onInitialize(String message);
        void OnSuccess(String transactNo, String message);
        void OnError(String message);
    }

    public void InitializeEvaluation(String BranchCD, OnInitializeBranchEvaluationListener listener){
        TaskExecutor.Execute(BranchCD, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                listener.onInitialize("Loading Evaluations. Please wait . . .");
            }

            @Override
            public Object DoInBackground(Object args) {
                String lsResult = poSys.InitializePacitaEvaluation((String) args);
                try{
                    if(lsResult == null){
                        message = poSys.getMessage();
                        return null;
                    }
                }catch (Exception e){
                    message = e.getMessage();
                    return null;
                }
                return lsResult;
            }

            @Override
            public void OnPostExecute(Object object) {
                if(object == null){
                    listener.OnError(message);
                } else {
                    listener.OnSuccess((String) object, message);
                }
            }
        });
    }
    public LiveData<EPacitaEvaluation> getBranchEvaluation(String lsTransNo){
        return poSys.GetEvaluationRecord(lsTransNo);
    }
    public LiveData<List<EPacitaRule>> GetCriteria(){
        return poSys.GetPacitaRules();
    }

    public void setEvaluationResult(String TransNox, String loPayloadx){
        String[] argList = {TransNox,loPayloadx};

        TaskExecutor.Execute(argList, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                String[] array = (String[]) args;

                String sTransNo = array[0];
                String sPayloadx = array[1];

                if(!poSys.UpdateBranchRate(sTransNo, sPayloadx)){
                    message = poSys.getMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                Log.d(TAG, object.toString());
            }
        });
    }

    public interface BranchRatingsCallback{
        void onSave(String title, String message);
        void onSuccess(String message);
        void onFailed(String message);
    }
    public void saveBranchRatings(String TransNox, BranchRatingsCallback callback){
        TaskExecutor.Execute(TransNox, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.onSave("Pacita Reward", "Saving your Evaluation. Please Wait . . . ");
            }

            @Override
            public Object DoInBackground(Object args) {
                try {
                    if(!poConnection.isDeviceConnected()){
                        message = poConnection.getMessage();
                        return false;
                    }
                    if(!poSys.SaveBranchRatings((String) args)){
                        message = poSys.getMessage();
                        return  false;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    message = e.getMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean aBoolean = (Boolean) object;
                if(aBoolean){
                    callback.onSuccess("Successfully Saved Branch Evaluation");
                } else {
                    callback.onFailed(message);
                }
            }
        });
    }
}
