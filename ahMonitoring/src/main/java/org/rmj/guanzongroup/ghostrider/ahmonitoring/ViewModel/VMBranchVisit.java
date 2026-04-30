package org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Apps.BranchMonitoring.BranchMonitoring;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitChecklist;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitDetail;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitMaster;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMBranchVisit extends AndroidViewModel {

    private String lsMessage;

    private ConnectionUtil poConnection;
    private BranchMonitoring poSys;

    public interface OnImportChecklist{
        void OnLoad();
        void OnSuccess();
        void OnFailed(String fsMessage);
    }

    public VMBranchVisit(@NonNull Application application) {
        super(application);

        poConnection = new ConnectionUtil(application);
        poSys = new BranchMonitoring(application);
    }

    public void SaveNewMaster(String fsBranchCd){
        poSys.SaveNewMaster(fsBranchCd);
    }

    public void SaveNewDetail(String fsTransNox, String fsCategrID, String fsRemarks){
        poSys.SaveNewDetail(fsTransNox, fsCategrID, fsRemarks);
    }

    public void ImportChecklistDetails(String fsTransNox, OnImportChecklist foListener){

        TaskExecutor.Execute(fsTransNox, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foListener.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {

                try {

                    String lsTransNox = (String) args;

                    if (!poConnection.isDeviceConnected()){
                        lsMessage = poConnection.getMessage();
                        return false;
                    }
                    Thread.sleep(500);

                    if (!poSys.ImportChecklist()){
                        lsMessage = poSys.GetMessage();
                        return false;
                    }
                    Thread.sleep(1000);

                    if (!poSys.ImportBranchVisitDetails(lsTransNox)){
                        lsMessage = poSys.GetMessage();
                        return false;
                    }
                    return true;

                }catch (Exception e){
                    lsMessage = e.getMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                if ((Boolean) object){
                    foListener.OnSuccess();
                }else{
                    foListener.OnFailed(lsMessage);
                }
            }
        });
    }

    public LiveData<List<EBranchVisitChecklist>> GetChecklist(){
        return poSys.GetChecklist();
    }

    public LiveData<List<EBranchVisitDetail>> GetDetails(String fsTransNox){
        return poSys.GetDetails(fsTransNox);
    }
}
