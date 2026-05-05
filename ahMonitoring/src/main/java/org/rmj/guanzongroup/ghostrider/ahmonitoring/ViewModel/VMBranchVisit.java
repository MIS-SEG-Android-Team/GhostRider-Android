package org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Apps.BranchMonitoring.BranchMonitoring;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitDetail;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchVisitMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitChecklist;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitDetail;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitMaster;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMBranchVisit extends AndroidViewModel {

    private ConnectionUtil poConnection;
    private BranchMonitoring poSys;

    public interface OnImportChecklist{
        void OnLoad();
        void OnFinished(String fsMessage);
    }

    public interface OnSubmit{
        void OnLoad();
        void OnSuccess();
        void OnFailed(String fsMessage);
    }

    public VMBranchVisit(@NonNull Application application) {
        super(application);

        poConnection = new ConnectionUtil(application);
        poSys = new BranchMonitoring(application);
    }

    public void SaveError(String fsSource, String fsMessage){
        poSys.SaveError(fsSource, fsMessage);
    }

    public EBranchInfo GetBranchName(String fsBranchCd){
        return poSys.GetBranch(fsBranchCd);
    }

    public String SaveNewMaster(String fsBranchCd){
        return poSys.SaveNewMaster(fsBranchCd);
    }

    public void SaveNewDetail(String fsTransNox, String fsCategrID, String fsRemarks){
        poSys.SaveNewDetail(fsTransNox, fsCategrID, fsRemarks);
    }

    public void SubmitBranchVisit(EBranchVisitMaster foMaster, List<DBranchVisitDetail.BranchVisitDetail> faDetails, OnSubmit foListener){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foListener.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {
                Object[] result = new Object[2];

                if (!poConnection.isDeviceConnected()){
                    result[0] = false;
                    result[1] = poConnection.getMessage();
                    return result;
                }

                Object[] laRresult = poSys.SubmitBranchVisit(foMaster, faDetails);

                result[0] = laRresult[0];
                result[1] = laRresult[1];

                return result;
            }

            @Override
            public void OnPostExecute(Object object) {
                Object[] laResult = (Object[]) object;

                if (!(Boolean) laResult[0]){
                    foListener.OnFailed((String) laResult[1]);
                }else {
                    foListener.OnSuccess();
                }
            }
        });
    }

    public void UpdateRemarks(String fsRemarksx, String fsTransNox, String fsCategrID){
        poSys.UpdateRemarks(fsRemarksx, fsTransNox, fsCategrID);
    }

    public void ImportChecklist(OnImportChecklist foListener){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foListener.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {

                if (!poConnection.isDeviceConnected()){
                    return poConnection.getMessage();
                }

                if (!poSys.ImportChecklist()){
                    return poSys.GetMessage();
                }
                return "Checklist downloaded successfully";
            }

            @Override
            public void OnPostExecute(Object object) {

                String lsMessage = (String) object;
                foListener.OnFinished(lsMessage);
            }
        });
    }

    public void ImportMaster(String fsDfrom, String fsDto, OnImportChecklist foListener){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foListener.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {

                if (!poConnection.isDeviceConnected()){
                    return poConnection.getMessage();
                }

                if (!poSys.ImportBranchVisitMaster(fsDfrom, fsDto)){
                    return poSys.GetMessage();
                }

                return "Master downloaded successfully";
            }

            @Override
            public void OnPostExecute(Object object) {
                String lsMessage = (String) object;
                foListener.OnFinished(lsMessage);
            }
        });
    }

    public void ImportDetails(String fsTransNox, OnImportChecklist foListener){

        TaskExecutor.Execute(fsTransNox, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foListener.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {

                String lsTransNox = (String) args;

                if (!poConnection.isDeviceConnected()){
                    return poConnection.getMessage();
                }

                if (!poSys.ImportBranchVisitDetails(lsTransNox)){
                    return poSys.GetMessage();
                }
                return "Details downloaded successfully";
            }

            @Override
            public void OnPostExecute(Object object) {
                String lsMessage = (String) object;
                foListener.OnFinished(lsMessage);
            }
        });
    }

    public String GetCurrentDate(){
        return poSys.GetCurrentDate();
    }

    public LiveData<List<EBranchVisitChecklist>> GetChecklist(){
        return poSys.GetChecklist();
    }

    public EBranchVisitMaster GetEntryToday(){
        return poSys.GetEntryToday();
    }

    public LiveData<EBranchVisitMaster> GetMasterTransaction(String fsTransNox){
        return poSys.GetMasterTransaction(fsTransNox);
    }

    public LiveData<List<DBranchVisitMaster.MasterHistory>> GetHistory(String fsTransTat){
        return poSys.GetHistory(fsTransTat);
    }

    public LiveData<List<DBranchVisitDetail.BranchVisitDetail>> GetDetails(String fsTransNox){
        return poSys.GetDetails(fsTransNox);
    }
}
