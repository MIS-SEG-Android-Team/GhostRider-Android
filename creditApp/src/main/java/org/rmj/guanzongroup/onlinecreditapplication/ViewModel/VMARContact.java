package org.rmj.guanzongroup.onlinecreditapplication.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditOnlineApplication;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DGanadoOnline;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplication;
import org.rmj.g3appdriver.GCircle.room.Entities.EMCContractInfo;
import org.rmj.g3appdriver.lib.Ganado.Obj.ProductInquiry;
import org.rmj.g3appdriver.lib.Ganado.pojo.InquiryInfo;
import org.rmj.g3appdriver.lib.Ganado.pojo.InstallmentInfo;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMARContact extends AndroidViewModel {

    private String message;

    private final MutableLiveData<String> lsModelIDx = new MutableLiveData<>();

    private final CreditOnlineApplication poApp;
    private final ProductInquiry poProduct;
    private final ConnectionUtil poConn;
    private final InquiryInfo poPrdctModel;

    public EBranchInfo getBranchInfo(String fsBranchCd){
        return poApp.getBranchInfoNonLive(fsBranchCd);
    }

    public interface OnDownload{
        void OnLoad(String fsTitlexx, String fsMessage);
        void OnFinished(String message);
    }

    public interface OnSearchLSerial{
        void OnSuccess(List<CreditOnlineApplication.MCSerial> laSerials);
        void OnFailed(String message);
    }

    public interface OnSubmit{
        void OnLoad(String fsTitlexx, String fsMessage);
        void OnSuccess();
        void OnFailed(String message);
    }

    public interface OnRetrieveInstallmentInfo{
        void OnRetrieve(InstallmentInfo loResult);
        void OnFailed(String message);
    }

    public interface OnCalculateNewDownpayment{
        void OnCalculate(double lnResult);
        void OnFailed(String message);
    }

    public VMARContact(@NonNull Application application) {
        super(application);

        this.poApp = new CreditOnlineApplication(application);
        this.poProduct = new ProductInquiry(application);
        this.poConn = new ConnectionUtil(application);
        this.poPrdctModel = new InquiryInfo();
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

    public LiveData<String> GetModelIDxx(){
        return lsModelIDx;
    }

    public LiveData<DGanadoOnline.CashPrice> GetCashPrice(String ModelID){
        return poProduct.GetCashPrice(ModelID);
    }

    public ECreditApplication GetApplication(String fsTransNox){
        return poApp.GetApplication(fsTransNox);
    }

    public InquiryInfo GetProductModel(){
        return poPrdctModel;
    }

    public EMCContractInfo GetCreditContract(String fsTransNox){
        return poApp.GetCreditContract(fsTransNox);
    }

    public void SetModelIDxx(String fsModelIDx){
        this.lsModelIDx.setValue(fsModelIDx);
    }

    public void SaveError(String fsSource, String fsMessage){
        poApp.SaveError(fsSource, fsMessage);
    }

    public void GetMinimumDownpayment(String ModelID, OnRetrieveInstallmentInfo listener) {

        TaskExecutor.Execute(ModelID, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                String lsModelID = (String) args;
                InstallmentInfo loResult = poProduct.GetMinimumDownpayment(lsModelID);

                if(loResult == null){
                    message = poProduct.getMessage();
                    return null;
                }

                return loResult;
            }

            @Override
            public void OnPostExecute(Object object) {
                InstallmentInfo loResult = (InstallmentInfo) object;
                if(loResult == null){
                    listener.OnFailed(message);
                    return;
                }

                listener.OnRetrieve(loResult);
            }
        });
    }

    public void CalculateNewDownpayment(String ModelID, int term, double Downpayment, OnCalculateNewDownpayment listener){

        TaskExecutor.Execute(null, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {

                double lnResult = poProduct.GetMonthlyAmortization(ModelID, term, Downpayment);
                if(lnResult == 0.0){
                    message = poApp.getMessage();
                    return 0.0;
                }
                return lnResult;
            }

            @Override
            public void OnPostExecute(Object object) {
                double lnResult = (double) object;
                if(lnResult == 0.0){
                    listener.OnFailed(message);
                    return;
                }
                listener.OnCalculate(lnResult);
            }
        });

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

    public void DownloadMContract(String fsReferNox, OnDownload foListener){

        TaskExecutor.Execute(fsReferNox, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foListener.OnLoad("MC Contract", "Finding MC Contract...");
            }

            @Override
            public Object DoInBackground(Object args) {

                if (!poConn.isDeviceConnected()){
                    message = poConn.getMessage();
                    return false;
                }

                String lsReferNox = (String) args;
                if (!poApp.DownloadMContract(lsReferNox)){
                    message = poApp.getMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {

                if ((Boolean) object){
                    foListener.OnFinished("Successfully downloaded MC Contract");
                } else {
                    foListener.OnFinished(message);
                }
            }
        });
    }

    public void SubmitMContract(EMCContractInfo foVal, OnSubmit foListener){

        TaskExecutor.Execute(foVal, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                foListener.OnLoad("MC Contract", "Submitting MC Contract...");
            }

            @Override
            public Object DoInBackground(Object args) {

                EMCContractInfo loVal = (EMCContractInfo) args;
                if (!poApp.UploadMContract(loVal)){
                    message = poApp.getMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                if ((Boolean) object){
                    foListener.OnSuccess();
                } else {
                    foListener.OnFailed(message);
                }
            }
        });
    }

    public double GetMonthlyAmortization(int args1) {
        return poProduct.GetMonthlyAmortization(lsModelIDx.getValue(), args1);
    }
}
