package org.rmj.guanzongroup.onlinecreditapplication.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditOnlineApplication;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.LoanInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DMcModel;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplication;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMARContact extends AndroidViewModel {

    private String message;

    private final MutableLiveData<String> lsModelIDx = new MutableLiveData<>();
    private final MutableLiveData<DMcModel.McAmortInfo> poAmort = new MutableLiveData<>();

    private final CreditOnlineApplication poApp;
    private final ConnectionUtil poConn;
    private final LoanInfo poModel;

    public EBranchInfo getBranchInfo(String fsBranchCd){
        return poApp.getBranchInfoNonLive(fsBranchCd);
    }

    public interface OnSearchLSerial{
        void OnSuccess(List<CreditOnlineApplication.MCSerial> laSerials);
        void OnFailed(String message);
    }

    public VMARContact(@NonNull Application application) {
        super(application);

        this.poApp = new CreditOnlineApplication(application);
        this.poConn = new ConnectionUtil(application);
        this.poModel = new LoanInfo();
    }

    public void SetModelIDxx(String fsModelIDx){
        this.lsModelIDx.setValue(fsModelIDx);
    }

    public String CreateIDForContract(){
        return poApp.CreateContractID();
    }

    public String CreateIDForClient(){
        return poApp.CreateIDForClient();
    }

    public String CreateIDForAccountNumber(){
        return poApp.CreateIDForAccountNumber();
    }

    public LoanInfo GetModel(){
        return poModel;
    }

    public ECreditApplication GetApplication(String fsTransNox){
        return poApp.GetApplication(fsTransNox);
    }

    public LiveData<String> GetModelIDxx(){
        return lsModelIDx;
    }

    public LiveData<DMcModel.McDPInfo> GetInstallmentPlanDetail(String ModelID) {
        return poApp.GetInstallmentPlanDetail(ModelID);
    }

    public LiveData<DMcModel.McAmortInfo> GetAmortizationDetail(String args, int args1) {
        return poApp.GetMonthlyPayment(args, args1);
    }

    public boolean InitializeTermAndDownpayment(DMcModel.McDPInfo args) {
        return poApp.InitializeMcInstallmentTerms(args);
    }

    public double GetMinimumDownpayment() {
        return poApp.GetMinimumDownpayment();
    }

    public double GetMonthlyPayment(int args1) {
        return poApp.GetMonthlyAmortization(poAmort.getValue(), args1);
    }

    public void SetModelAmortization(DMcModel.McAmortInfo args) {
        this.poAmort.setValue(args);
    }

    public void GetSerials(String fsVal, boolean fByCode, OnSearchLSerial foListener){

        TaskExecutor.Execute(fsVal, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {

                Object[] laResult = new Object[2];

                if (!poConn.isDeviceConnected()){
                    message = poConn.getMessage();

                    laResult[0] = false;
                    laResult[1] = null;
                    return laResult;
                }

                List<CreditOnlineApplication.MCSerial> laSerials = poApp.DownloadSerials(fsVal, fByCode);
                if (laSerials == null){
                    message = poApp.getMessage();

                    laResult[0] = false;
                    laResult[1] = null;
                    return laResult;
                }
                laResult[0] = true;
                laResult[1] = laSerials;
                return laResult;
            }

            @Override
            public void OnPostExecute(Object object) {
                Object[] loResult = (Object[]) object;

                if (!(Boolean) loResult[0]){
                    foListener.OnFailed(message);
                } else {
                    if (loResult[1] == null){
                        foListener.OnFailed("Invalid result found!");
                        return;
                    }
                    foListener.OnSuccess((List<CreditOnlineApplication.MCSerial>) loResult[1]);
                }
            }
        });
    }
}
