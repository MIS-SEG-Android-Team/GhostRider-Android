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
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.OtherReference;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.Reference;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DTownInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicantInfo;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.ArrayList;
import java.util.List;

public class VMOtherInfo extends AndroidViewModel implements CreditAppUI {
    private static final String TAG = VMReviewLoanApp.class.getSimpleName();

    private final CreditApp poApp;
    private final OtherReference poModel;

    private String TransNox;

    private String message;
    private MutableLiveData<List<Reference>> loList = new MutableLiveData<>();

    public VMOtherInfo(@NonNull Application application) {
        super(application);
        this.poApp = new CreditOnlineApplication(application).getInstance(CreditAppInstance.Other_Info);
        this.poModel = new OtherReference();
        this.loList.setValue(new ArrayList<>());
    }

    public OtherReference getModel() {
        return poModel;
    }

    public LiveData<List<DTownInfo.TownProvinceInfo>> GetTownProvinceList() {
        return poApp.GetTownProvinceList();
    }

    public void setListOfReference(List<Reference> foList) {
        loList.getValue().clear();
        loList.setValue(foList);
//        Objects.requireNonNull(loList).getValue().clear();
//        Objects.requireNonNull(loList).setValue(foList);
    }

    public LiveData<List<Reference>> getReferenceList() {
        return loList;
    }

    public boolean addReference(Reference poInfo, AddPersonalInfoListener listener) {
        try {
            if (poInfo.isDataValid()) {
                List<Reference> refList = loList.getValue();
                refList.add(poInfo);
                loList.setValue(refList);
                listener.OnSuccess();
                return true;
            } else {
                listener.onFailed(poInfo.getMessage());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailed(e.getMessage());
            return false;
        }
    }

    public boolean removeReference(int position) {
        try {
            loList.getValue().remove(position);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public interface AddPersonalInfoListener {
        void OnSuccess();

        void onFailed(String message);
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
                    OtherReference loDetail = (OtherReference) poApp.Parse(lsApp);

                    if (loDetail == null) {
                        message = poApp.getMessage();
                        return null;
                    }

                    return loDetail;
                } catch (Exception e) {
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return null;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                OtherReference lsResult = (OtherReference) object;
                if (lsResult == null) {
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
//        new SaveDetailTask(listener).execute(poModel);
        TaskExecutor.Execute(poModel, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                OtherReference lsInfo = (OtherReference) args;
                int lnResult = poApp.Validate(lsInfo);

                if (lnResult != 1) {
                    message = poApp.getMessage();
                    return false;
                }

                String lsResult = poApp.Save(lsInfo);
                if (lsResult == null) {
                    message = poApp.getMessage();
                    return false;
                }

                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean lsSuccess = (Boolean) object;
                if (!lsSuccess) {
                    listener.OnFailed(message);
                } else {
                    listener.OnSave(TransNox);
                }

            }
        });
    }
}
