package org.rmj.guanzongroup.onlinecreditapplication.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditApp;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppInstance;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditOnlineApplication;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.Dependent;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DTownInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicantInfo;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.ArrayList;
import java.util.List;

public class VMDependent extends AndroidViewModel implements CreditAppUI {
    private static final String TAG = VMDependent.class.getSimpleName();

    private final CreditApp poApp;
    private final Dependent poModel;

    private String TransNox;

    private final MutableLiveData<List<Dependent.DependentInfo>> poList = new MutableLiveData<>();

    private String message;

    public interface OnAddDependetListener{
        void OnAdd(String args);
        void OnFailed(String message);
    }

    public VMDependent(@NonNull Application application) {
        super(application);
        this.poApp = new CreditOnlineApplication(application).getInstance(CreditAppInstance.Dependent_Info);
        this.poModel = new Dependent();
        this.poList.setValue(new ArrayList<>());
    }

    public Dependent getModel() {
        return poModel;
    }

    public LiveData<List<Dependent.DependentInfo>> GetDependents(){
        return poList;
    }

    public void setDependent(List<Dependent.DependentInfo> val){
        poList.setValue(val);
    }

    public void addDependent(Dependent.DependentInfo args, OnAddDependetListener listener){
        try{
            if(!args.isDataValid()){
                listener.OnFailed(args.getMessage());
                return;
            }

            List<Dependent.DependentInfo> loList = poList.getValue();
            loList.add(args);
            poList.setValue(loList);
            listener.OnAdd("Dependent Added!");
        } catch (Exception e){
            e.printStackTrace();
            listener.OnFailed(e.getMessage());
        }
    }

    public void removeDependent(int args){
        List<Dependent.DependentInfo> loList = poList.getValue();
        loList.remove(args);
        poList.setValue(loList);
    }

    @Override
    public void InitializeApplication(Intent params) {
        this.TransNox = params.getStringExtra("sTransNox");
    }

    @Override
    public LiveData<ECreditApplicantInfo> GetApplication() {
        return poApp.GetApplication(TransNox);
    }

    @Override
    public void ParseData(ECreditApplicantInfo args, OnParseListener listener) {
//        new ParseDataTask(listener).execute(args);
        TaskExecutor.Execute(args, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                ECreditApplicantInfo lsApp = (ECreditApplicantInfo) args;
                try {
                    Dependent loDetail = (Dependent) poApp.Parse(lsApp);
                    if(loDetail == null){
                        message = poApp.getMessage();
                        return null;
                    }
                    return loDetail;
                } catch (Exception e){
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return null;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Dependent lsResult = (Dependent) object;
                if(lsResult == null){
                    Log.e(TAG, message);
                } else {
                    listener.OnParse(lsResult);
                }
            }
        });
    }

    @Override
    public void Validate(Object args) {

    }

    @Override
    public void SaveData(OnSaveInfoListener listener) {
//        new SaveDataTask(listener).execute(poModel);
        TaskExecutor.Execute(poModel, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                Dependent lsInfo = (Dependent) args;
                lsInfo.setDependentList(poList.getValue());
                int lnResult = poApp.Validate(lsInfo);

                if(lnResult != 1){
                    message = poApp.getMessage();
                    return false;
                }

                String lsResult = poApp.Save(lsInfo);
                if(lsResult == null){
                    message = poApp.getMessage();
                    return false;
                }

                TransNox = lsInfo.getTransNox();
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean lsSuccess = (Boolean) object;
                if(!lsSuccess){
                    listener.OnFailed(message);
                } else {
                    listener.OnSave(TransNox);
                }
            }
        });
    }
    public LiveData<List<DTownInfo.TownProvinceInfo>> GetTownProvince(){
        return poApp.GetTownProvinceList();
    }
}