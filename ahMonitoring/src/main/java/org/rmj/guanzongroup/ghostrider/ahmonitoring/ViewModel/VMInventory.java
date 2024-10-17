/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EInventoryDetail;
import org.rmj.g3appdriver.GCircle.room.Entities.EInventoryMaster;
import org.rmj.g3appdriver.GCircle.Apps.Inventory.RandomStockInventory;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMInventory extends AndroidViewModel {
    private static final String TAG = VMInventory.class.getSimpleName();

    private final RandomStockInventory poSys;
    private final ConnectionUtil poConn;

    private final MutableLiveData<String> psBranch = new MutableLiveData<>();
    private String message;
    public interface OnCheckLocalRecords{
        void OnCheck();
        void OnProceed();
        void OnInventoryFinished(String message);
    }

    public interface OnDownloadInventory {
        void OnRequest(String title, String message);
        void OnSuccessResult();
        void OnFailed(String message);
    }

    public interface OnSaveInventoryMaster{
        void OnSave();
        void OnSuccess();
        void OnFailed(String message);
    }

    public interface OnGetBranchListListener{
        void OnLoad();
        void OnRetrieve(List<EBranchInfo> list);
        void OnFailed(String message);
    }

    public VMInventory(@NonNull Application application) {
        super(application);
        this.poSys = new RandomStockInventory(application);
        this.poConn = new ConnectionUtil(application);
        this.psBranch.setValue("");
    }

    public void setBranchCd(String val){
        this.psBranch.setValue(val);
    }

    public LiveData<String> GetBranchCd(){
        return psBranch;
    }

    public LiveData<EBranchInfo> GetBranchInfo(String args){
        return poSys.GetBranchInfo(args);
    }

    public LiveData<EInventoryMaster> GetInventoryMaster(String fsVal){
        return poSys.GetInventoryMaster(fsVal);
    }

    public LiveData<List<EInventoryDetail>> GetInventoryItems(String fsVal){
        return poSys.GetInventoryItems(fsVal);
    }

    public void GetBranchList(OnGetBranchListListener listener){
        //new GetBranchTask(listener).execute();

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                listener.OnLoad();
            }

            @Override
            public Object DoInBackground(Object args) {
                List<EBranchInfo> loList = poSys.GetBranchesForInventory();
                if(loList == null){
                    message = poSys.getMessage();
                    return null;
                }
                return loList;
            }

            @Override
            public void OnPostExecute(Object object) {
                List<EBranchInfo> eBranchInfos = (List<EBranchInfo>) object;
                if(eBranchInfos == null){
                    listener.OnFailed(message);
                } else {
                    listener.OnRetrieve(eBranchInfos);
                }
            }
        });
    }

    public void CheckBranchInventory(String fsVal, OnCheckLocalRecords callback){

        TaskExecutor.Execute(fsVal, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnCheck();
            }

            @Override
            public Object DoInBackground(Object args) {
                String lsString = (String) args;
                if(!poSys.CheckInventoryRecord(lsString)){
                    message = poSys.getMessage();
                    return false;
                }

                if(!poConn.isDeviceConnected()){
                    message = "Unable to proceed to random stock inventory without connectivity";
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean lsSuccess = (Boolean) object;
                if(!lsSuccess){
                    callback.OnInventoryFinished(message);
                } else {
                    callback.OnProceed();
                }
            }
        });

    }

    public void DownloadInventory(String BranchCd, OnDownloadInventory poCallback){

        TaskExecutor.Execute(BranchCd, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                poCallback.OnRequest("Random Stock Inventory", "Downloading inventory details. Please wait...");
            }

            @Override
            public Object DoInBackground(Object args) {
                String lsStrings = (String) args;
                if(!poConn.isDeviceConnected()){
                    message = poConn.getMessage();
                    return false;
                }

                if(!poSys.ImportInventory(lsStrings)){
                    message = poSys.getMessage();
                    return false;
                }

                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean lsSuccess = (Boolean) object;
                if(!lsSuccess){
                    poCallback.OnFailed(message);
                } else {
                    poCallback.OnSuccessResult();
                }
            }
        });
    }

    public void PostInventory(String BranchCD, String Remarks, OnSaveInventoryMaster callback){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {

            @Override
            public void OnPreExecute() {
                callback.OnSave();
            }

            @Override
            public Object DoInBackground(Object args) {
                String lsResult = poSys.SaveMasterForPosting(BranchCD, Remarks);
                if(lsResult == null){
                    message = poSys.getMessage();
                    return false;
                }

                if(!poConn.isDeviceConnected()){
                    message = "Your inventory has been save to local device.";
                    return false;
                }

                if(!poSys.UploadInventory(BranchCD)){
                    message = poSys.getMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean lsSuccess = (Boolean) object;
                if(!lsSuccess){
                    callback.OnFailed(message);
                } else {
                    callback.OnSuccess();
                }
            }
        });
    }
}