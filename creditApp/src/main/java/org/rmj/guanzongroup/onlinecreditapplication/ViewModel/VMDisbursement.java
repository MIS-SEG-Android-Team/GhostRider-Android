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
import org.rmj.g3appdriver.GCircle.Apps.integsys.CreditApp.model.Disbursement;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicantInfo;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

public class VMDisbursement extends AndroidViewModel implements CreditAppUI {
    private static final String TAG = VMDisbursement.class.getSimpleName();

    private final CreditApp poApp;
    private final Disbursement poModel;
    private String cvlStatus;

    private String TransNox;

    private String message;

    public VMDisbursement(@NonNull Application application) {
        super(application);
        this.poApp = new CreditOnlineApplication(application).getInstance(CreditAppInstance.Disbursement_Info);
        this.poModel = new Disbursement();
    }

    public Disbursement getModel() {
        return poModel;
    }

    public String getCvlStatus() {
        return cvlStatus;
    }

    public void setCvlStatus(String args) {
        this.cvlStatus = args;
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
                    Disbursement loDetail = (Disbursement) poApp.Parse(lsApp);
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
                Disbursement lsResult = (Disbursement) object;
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
                Disbursement lsInfo = (Disbursement) args;
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
}
//
//    private class SaveDataTask extends AsyncTask<Disbursement, Void, Boolean> {
//
//        private final OnSaveInfoListener listener;
//
//        public SaveDataTask(OnSaveInfoListener listener) {
//            this.listener = listener;
//        }
//
//        @Override
//        protected Boolean doInBackground(Disbursement... info) {
//            int lnResult = poApp.Validate(info[0]);
//
//            if (lnResult != 1) {
//                message = poApp.getMessage();
//                return false;
//            }
//
//            String lsResult = poApp.Save(info[0]);
//            if(lsResult == null){
//                message = poApp.getMessage();
//                return false;
//            }
//
//            TransNox = info[0].getTransNox();
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean isSuccess) {
//            super.onPostExecute(isSuccess);
//            if (!isSuccess) {
//                listener.OnFailed(message);
//            } else {
//                listener.OnSave(TransNox);
//            }
//        }
//    }
//}
//private class ParseDataTask extends AsyncTask<ECreditApplicantInfo, Void, Disbursement>{
//
//        private final OnParseListener listener;
//
//        public ParseDataTask(OnParseListener listener) {
//            this.listener = listener;
//        }
//
//        @Override
//        protected Disbursement doInBackground(ECreditApplicantInfo... app) {
//            try {
//                Disbursement loDetail = (Disbursement) poApp.Parse(app[0]);
//                if(loDetail == null){
//                    message = poApp.getMessage();
//                    return null;
//                }
//                return loDetail;
//            } catch (NullPointerException e){
//                e.printStackTrace();
//                message = getLocalMessage(e);
//                return null;
//            } catch (Exception e){
//                e.printStackTrace();
//                message = getLocalMessage(e);
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Disbursement result) {
//            super.onPostExecute(result);
//            if(result == null){
//                Log.e(TAG, message);
//            } else {
//                listener.OnParse(result);
//            }
//        }
//    }
