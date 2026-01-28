package org.rmj.guanzongroup.onlinecreditapplication.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditOnlineApplication;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.LoanInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DGanadoOnline;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DMcModel;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicantInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EMcBrand;
import org.rmj.g3appdriver.GCircle.room.Entities.EMcModel;
import org.rmj.g3appdriver.lib.Ganado.Obj.ProductInquiry;
import org.rmj.g3appdriver.lib.Ganado.pojo.InquiryInfo;
import org.rmj.g3appdriver.lib.Ganado.pojo.InstallmentInfo;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMIntroductoryQuestion extends AndroidViewModel implements CreditAppUI {
    private static final String TAG = VMIntroductoryQuestion.class.getSimpleName();

    private final CreditOnlineApplication poApp;
    private final LoanInfo poModel;
    private final ProductInquiry poProduct;
    private final InquiryInfo poPrdctModel;

    private final MutableLiveData<String> psBrandID = new MutableLiveData<>();
    private final MutableLiveData<String> psModelID = new MutableLiveData<>();

    private String message;

    public VMIntroductoryQuestion(@NonNull Application instance) {
        super(instance);

        this.poApp = new CreditOnlineApplication(instance);
        this.poModel = new LoanInfo();
        this.poProduct = new ProductInquiry(instance);
        this.poPrdctModel = new InquiryInfo();
    }

    public LoanInfo getModel() {
        return poModel;
    }

    public void setBrandID(String args) {
        this.psBrandID.setValue(args);
    }

    public void setModelID(String args) {
        this.psModelID.setValue(args);
    }

    public LiveData<String> GetBrandID() {
        return psBrandID;
    }

    public LiveData<String> GetModelID() {
        return psModelID;
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> GetUserInfo() {
        return poApp.GetUserInfo();
    }

    public LiveData<List<EBranchInfo>> GetAllBranchInfo() {
        return poApp.getAllBranchInfo();
    }

    public LiveData<List<EMcBrand>> GetAllMcBrand() {
        return poApp.getAllMcBrand();
    }

    public LiveData<List<EMcModel>> GetAllBrandModelInfo(String args) {
        return poApp.getAllBrandModelInfo(args);
    }

    public InquiryInfo GetProductModel(){
        return poPrdctModel;
    }

    public LiveData<DGanadoOnline.CashPrice> GetCashPrice(String ModelID){
        return poProduct.GetCashPrice(ModelID);
    }

    public void GetMinimumDownpayment(String ModelID, VMARContact.OnRetrieveInstallmentInfo listener) {

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

    public void CalculateNewDownpayment(String ModelID, int term, double Downpayment, VMARContact.OnCalculateNewDownpayment listener){

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

    public double GetMonthlyAmortization(int args1) {
        return poProduct.GetMonthlyAmortization(psModelID.getValue(), args1);
    }

    @Override
    public void InitializeApplication(Intent params) {
        Log.d(TAG, "No data to initialize on introductory question");
    }

    @Override
    public LiveData<ECreditApplicantInfo> GetApplication() {
        return null;
    }

    @Override
    public void ParseData(ECreditApplicantInfo args, OnParseListener listener) {
        Log.d(TAG, "No data to parse on introductory question");
    }

    @Override
    public void Validate(Object args) {
    }

    @Override
    public void SaveData(OnSaveInfoListener listener) {

        TaskExecutor.Execute(poModel, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {

                LoanInfo loDetail = (LoanInfo) args;
                try {

                    if (!loDetail.isDataValid()) {
                        message = loDetail.getMessage();
                        return null;
                    }

                    String lsResult = poApp.CreateApplication(loDetail);

                    if (lsResult == null) {
                        message = poApp.getMessage();
                        return null;
                    }

                    return lsResult;
                } catch (Exception e) {
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return null;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                String lsResult = (String) object;
                if (lsResult == null) {
                    listener.OnFailed(message);
                } else {
                    listener.OnSave(lsResult);
                }
            }
        });
    }
}

