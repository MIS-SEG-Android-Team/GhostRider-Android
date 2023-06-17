package org.rmj.guanzongroup.onlinecreditapplication.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Apps.integsys.CreditApp.CreditApp;
import org.rmj.g3appdriver.GCircle.Apps.integsys.CreditApp.CreditAppInstance;
import org.rmj.g3appdriver.GCircle.Apps.integsys.CreditApp.CreditOnlineApplication;
import org.rmj.g3appdriver.GCircle.Apps.integsys.CreditApp.OnSaveInfoListener;
import org.rmj.g3appdriver.GCircle.Apps.integsys.CreditApp.model.Business;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DTownInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBarangayInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicantInfo;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMBusinessInfo extends AndroidViewModel implements CreditAppUI {
    private static final String TAG = VMBusinessInfo.class.getSimpleName();

    private final CreditApp poApp;
    private final Business poModel;

    private String TransNox;

    private String message;

    public VMBusinessInfo(@NonNull Application application) {
        super(application);
        this.poApp = new CreditOnlineApplication(application).getInstance(CreditAppInstance.Self_Employed_Info);
        this.poModel = new Business();
    }

    public Business getModel() {
        return poModel;
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
                    Business loDetail = (Business) poApp.Parse(lsApp);
                    if (loDetail == null) {
                        message = poApp.getMessage();
                        return null;
                    }
                    return loDetail;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return null;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                Business lsResult = (Business) object;
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
//        new SaveDataTask(listener).execute(poModel);
        TaskExecutor.Execute(poModel, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                Business lsInfo = (Business) args;
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

                TransNox = lsInfo.getTransNox();
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

    public LiveData<List<DTownInfo.TownProvinceInfo>> GetTownProvinceList() {
        return poApp.GetTownProvinceList();
    }

    public LiveData<List<EBarangayInfo>> GetBarangayList(String args) {
        return poApp.GetBarangayList(args);
    }
}
//private class SaveDataTask extends AsyncTask<Business, Void, Boolean> {
//
//    private final OnSaveInfoListener listener;
//
//    public SaveDataTask(OnSaveInfoListener listener) {
//        this.listener = listener;
//    }
//
//    @Override
//    protected Boolean doInBackground(Business... info) {
//        int lnResult = poApp.Validate(info[0]);
//
//        if (lnResult != 1) {
//            message = poApp.getMessage();
//            return false;
//        }
//
//        String lsResult = poApp.Save(info[0]);
//        if (lsResult == null) {
//            message = poApp.getMessage();
//            return false;
//        }
//
//        TransNox = info[0].getTransNox();
//        return true;
//    }
//
//    @Override
//    protected void onPostExecute(Boolean isSuccess) {
//        super.onPostExecute(isSuccess);
//        if (!isSuccess) {
//            listener.OnFailed(message);
//        } else {
//            listener.OnSave(TransNox);
//        }
//    }
//}
//}
//private class ParseDataTask extends AsyncTask<ECreditApplicantInfo, Void, Business>{
//
//    private final OnParseListener listener;
//
//    public ParseDataTask(OnParseListener listener) {
//        this.listener = listener;
//    }
//
//    @Override
//    protected Business doInBackground(ECreditApplicantInfo... app) {
//        try {
//            Business loDetail = (Business) poApp.Parse(app[0]);
//            if(loDetail == null){
//                message = poApp.getMessage();
//                return null;
//            }
//            return loDetail;
//        } catch (NullPointerException e){
//            e.printStackTrace();
//            message = getLocalMessage(e);
//            return null;
//        } catch (Exception e){
//            e.printStackTrace();
//            message = getLocalMessage(e);
//            return null;
//        }
//    }
//
//    @Override
//    protected void onPostExecute(Business result) {
//        super.onPostExecute(result);
//        if(result == null){
//            Log.e(TAG, message);
//        } else {
//            listener.OnParse(result);
//        }
//    }
//}