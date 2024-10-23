package org.rmj.guanzongroup.ganado.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DGanadoOnline;
import org.rmj.g3appdriver.GCircle.room.Entities.EGanadoOnline;
import org.rmj.g3appdriver.GCircle.room.Entities.EMCColor;
import org.rmj.g3appdriver.GCircle.room.Entities.EMcBrand;
import org.rmj.g3appdriver.GCircle.room.Entities.EMcModel;
import org.rmj.g3appdriver.lib.Ganado.Obj.Ganado;
import org.rmj.g3appdriver.lib.Ganado.Obj.ProductInquiry;
import org.rmj.g3appdriver.lib.Ganado.pojo.InquiryInfo;
import org.rmj.g3appdriver.lib.Ganado.pojo.InstallmentInfo;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;
import java.util.List;

public class VMProductInquiry extends AndroidViewModel implements GanadoUI {
    private static final String TAG = VMProductInquiry.class.getSimpleName();
    private final ProductInquiry poApp;
    private final Ganado oApp;
    private final InquiryInfo poModel;
    private final MutableLiveData<String> psBrandID = new MutableLiveData<>();
    private final MutableLiveData<String> psModelID = new MutableLiveData<>();
    private final MutableLiveData<DGanadoOnline.McAmortization> poAmort = new MutableLiveData<>();

    public interface OnRetrieveInstallmentInfo{
        void OnRetrieve(InstallmentInfo loResult);
        void OnFailed(String message);
    }

    public interface OnCalculateNewDownpayment{
        void OnCalculate(double lnResult);
        void OnFailed(String message);
    }

    private String message;

    public VMProductInquiry(@NonNull Application instance) {
        super(instance);
        this.poApp = new ProductInquiry(instance);
        this.poModel = new InquiryInfo();
        this.oApp = new Ganado(instance);

        this.poModel.setGanadoTp("1");
    }
    public LiveData<String> GetModelID() {
        return psModelID;
    }

    public LiveData<List<EMCColor>> GetModelColor(String ModelID){
        return poApp.GetModelColor(ModelID);
    }
    public LiveData<EMcModel> GetModelBrand(String BrandID, String ModelID){
        return poApp.GetModel(BrandID, ModelID);
    }
    public LiveData<DGanadoOnline.CashPrice> GetCashPrice(String ModelID){
        return poApp.GetCashPrice(ModelID);
    }
    public InquiryInfo getModel() {
        return poModel;
    }

    public void setBrandID(String args) {
        this.psBrandID.setValue(args);
    }

    public void setModelID(String args) {
        this.psModelID.setValue(args);
    }

    public void GetMinimumDownpayment(String ModelID, OnRetrieveInstallmentInfo listener) {
        TaskExecutor.Execute(null, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                InstallmentInfo loResult = poApp.GetMinimumDownpayment(ModelID);

                if(loResult == null){
                    message = poApp.getMessage();
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
                double lnResult = poApp.GetMonthlyAmortization(ModelID, term, Downpayment);
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
        return poApp.GetMonthlyAmortization(psModelID.getValue(), args1);
    }
    public Boolean ValidateDownPayment(String sModelID, double newDownPaym){
        InstallmentInfo loResult = poApp.GetMinimumDownpayment(sModelID);
        double minDownPaym = loResult.getMinimumDownpayment();

        if (newDownPaym < minDownPaym){
            return false;
        }else {
            return true;
        }
    }
    @Override
    public void InitializeApplication(Intent params) {
        Log.d(TAG, "No data to initialize on introductory question");
    }
    @Override
    public LiveData<EGanadoOnline> GetApplication() {
        return null;
    }

    @Override
    public void ParseData(EGanadoOnline args, OnParseListener listener) {
        Log.d(TAG, "No data to parse on introductory question");
    }
    @Override
    public void Validate(Object args) {
    }
    @Override
    public void SaveData(OnSaveInfoListener listener) {
        TaskExecutor.Execute(listener, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {

            }

            @Override
            public Object DoInBackground(Object args) {
                try {
                    InquiryInfo loDetail = poModel;
                    InquiryInfo.InquiryInfoValidator loValid = new InquiryInfo.InquiryInfoValidator();

                    if (!loValid.isDataValid(loDetail)) {
                        message = loValid.getMessage();
                        return null;
                    }

                    String lsResult = oApp.CreateInquiry(loDetail);

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
