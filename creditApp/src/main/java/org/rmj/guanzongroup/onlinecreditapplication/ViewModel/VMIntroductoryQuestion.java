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
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DMcModel;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicantInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EMcBrand;
import org.rmj.g3appdriver.GCircle.room.Entities.EMcModel;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMIntroductoryQuestion extends AndroidViewModel implements CreditAppUI {
    private static final String TAG = VMIntroductoryQuestion.class.getSimpleName();

    private final CreditOnlineApplication poApp;
    private final LoanInfo poModel;

    private final MutableLiveData<String> psBrandID = new MutableLiveData<>();
    private final MutableLiveData<String> psModelID = new MutableLiveData<>();
    private final MutableLiveData<DMcModel.McAmortInfo> poAmort = new MutableLiveData<>();

    private String message;

    public VMIntroductoryQuestion(@NonNull Application instance) {
        super(instance);
        this.poApp = new CreditOnlineApplication(instance);
        this.poModel = new LoanInfo();
    }

    public LoanInfo getModel() {
        return poModel;
    }

    public void setBrandID(String args) {
        this.psBrandID.setValue(args);
    }

    public LiveData<String> GetBrandID() {
        return psBrandID;
    }

    public void setModelID(String args) {
        this.psModelID.setValue(args);
    }

    public LiveData<String> GetModelID() {
        return psModelID;
    }

    public void setModelAmortization(DMcModel.McAmortInfo args) {
        this.poAmort.setValue(args);
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

    public LiveData<DMcModel.McDPInfo> GetInstallmentPlanDetail(String ModelID) {
        return poApp.GetInstallmentPlanDetail(ModelID);
    }

    public boolean InitializeTermAndDownpayment(DMcModel.McDPInfo args) {
        return poApp.InitializeMcInstallmentTerms(args);
    }

    public LiveData<DMcModel.McAmortInfo> GetAmortizationDetail(String args, int args1) {
        return poApp.GetMonthlyPayment(args, args1);
    }

    public double GetMinimumDownpayment() {
        return poApp.GetMinimumDownpayment();
    }

    public double GetMonthlyPayment(double args1) {
        return poApp.GetMonthlyAmortization(poAmort.getValue(), args1);
    }

    public double GetMonthlyPayment(int args1) {
        return poApp.GetMonthlyAmortization(poAmort.getValue(), args1);
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

