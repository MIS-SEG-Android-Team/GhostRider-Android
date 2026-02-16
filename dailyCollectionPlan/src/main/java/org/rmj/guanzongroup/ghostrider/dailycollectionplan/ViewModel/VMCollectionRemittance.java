/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.dailyCollectionPlan
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.obj.REMIT;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ERemittanceAccounts;
import org.rmj.g3appdriver.GCircle.room.Repositories.RRemittanceAccount;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.model.LRDcp;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.Remittance;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMCollectionRemittance extends AndroidViewModel {

    private final REMIT poSys;

    private final EmployeeMaster poUser;
    private final RRemittanceAccount poAccount;
    private final ConnectionUtil poConn;

    private String message;
    public interface OnRemitCollectionCallback{
        void OnRemit();
        void OnSuccess(String message);
        void OnFailed(String message);
    }

    public VMCollectionRemittance(@NonNull Application application) {
        super(application);
        this.poSys = new REMIT(application);
        this.poUser = new EmployeeMaster(application);
        this.poAccount = new RRemittanceAccount(application);
        this.poConn = new ConnectionUtil(application);
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> GetUserInfo(){
        return poUser.GetEmployeeBranch();
    }

    public LiveData<EDCPCollectionMaster> GetCollectionMaster(){
        return poSys.GetCollectionMasterForRemittance();
    }

    public LiveData<String> GetCashCollection(String fsVal){
        return poSys.GetCashCollection(fsVal);
    }

    public LiveData<String> GetCheckCollection(String fsVal){
        return poSys.GetCheckCollection(fsVal);
    }

    public LiveData<String> GetEPayCollection(String fsVal){
        return poSys.GetEPayCollection(fsVal);
    }

    public LiveData<String> GetTotalCollection(String fsVal){
        return poSys.GetTotalCollection(fsVal);
    }

    public LiveData<String> GetBranchRemittance(String fsVal){
        return poSys.GetBranchRemittanceAmount(fsVal);
    }

    public LiveData<String> GetBankRemittance(String fsVal){
        return poSys.GetBankRemittanceAmount(fsVal);
    }

    public LiveData<String> GetOtherRemittance(String fsVal){
        return poSys.GetOtherRemittanceAmount(fsVal);
    }

    public LiveData<String> GetCashOnHand(String fsVal){
        return poSys.GetCashOnHand(fsVal);
    }

    public LiveData<String> GetCheckOnHand(String fsVal){
        return poSys.GetCheckOnHand(fsVal);
    }

    public LiveData<String> GetEPayOnHand(String fsVal){
        return poSys.GetEPayOnHand(fsVal);
    }

    public LiveData<List<ERemittanceAccounts>> GetBankAccounts(){
        return poAccount.getRemittanceBankAccount();
    }

    public LiveData<List<ERemittanceAccounts>> GetOtherAccounts(){
        return poAccount.getRemittanceOtherAccount();
    }

    public void RemitCollection(Remittance foVal, OnRemitCollectionCallback callback){
        TaskExecutor.Execute(foVal, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnRemit();
            }

            @Override
            public Object DoInBackground(Object args) {
                try {
                    JSONObject params = poSys.SaveRemittanceEntry((Remittance) args);
                    if (params == null) {
                        message = poSys.getMessage();
                        return false;
                    }

                    if (!poConn.isDeviceConnected()) {
                        message = "Remittance has been save to local device.";
                        return true;
                    }

                    String lsTransNo = params.getString("sTransNox");
                    String lsEntryNo = params.getString("nEntryNox");
                    if (!poSys.UploadRemittance(lsTransNo, lsEntryNo)) {
                        message = poSys.getMessage();
                        return false;
                    }

                    message = "Collection remittance has been save to server.";
                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    message = e.getMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(!isSuccess){
                    callback.OnFailed(message);
                } else {
                    callback.OnSuccess(message);
                }
            }
        });
    }
}
