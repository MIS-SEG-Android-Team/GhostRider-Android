package org.rmj.guanzongroup.onlinecreditapplication.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppDocuments;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.CreditAppDocs;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCreditApplicationDocuments;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMApplicantDocuments extends AndroidViewModel {
    private static final String TAG = VMApplicantDocuments.class.getSimpleName();

    private final CreditAppDocuments poApp;

    private final ConnectionUtil poConn;

    public interface OnInitializeCreditAppDocuments {
        void OnSuccess();

        void OnFailed(String message);
    }

    public interface OnSaveCreditAppDocument {
        void OnSave(String title, String message);

        void OnSuccess(String message);

        void OnFailed(String message);
    }

    private String message;

    public VMApplicantDocuments(@NonNull Application application) {
        super(application);
        this.poApp = new CreditAppDocuments(application);
        this.poConn = new ConnectionUtil(application);
    }

    public void InitializeDocuments(String TransNox, OnInitializeCreditAppDocuments listener) {
//        new InitializeDocumentsTask(listener).execute(TransNox);
        TaskExecutor.Execute(TransNox, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                String lsTransnox = (String) args;
                try {
                    if (!poApp.InitializeApplicantDocuments(lsTransnox)) {
                        message = poApp.getMessage();
                        return false;
                    }

                    return true;
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
                Boolean lsSuccess = (Boolean) object;
                if (!lsSuccess) {
                    listener.OnFailed(message);
                } else {
                    listener.OnSuccess();
                }
            }
        });
    }

    public LiveData<List<DCreditApplicationDocuments.ApplicationDocument>> GetCreditAppDocuments(String args) {
        return poApp.GetApplicantDocumentsList(args);
    }

    public void SaveDocumentScan(CreditAppDocs args, OnSaveCreditAppDocument listener) {

        TaskExecutor.Execute(args, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                CreditAppDocs lsDocumentInfo = (CreditAppDocs) args;
                try{
                String lsResult = poApp.SaveDocumentInfo(lsDocumentInfo);
                if(lsResult == null){
                    message = poApp.getMessage();
                    return false;
                }

                if(!poConn.isDeviceConnected()){
                    return true;
                }

                if(!poApp.UploadDocument(lsResult)){
                    message = poApp.getMessage();
                    return false;
                }
                return true;
            } catch (Exception e){
                e.printStackTrace();
                message = getLocalMessage(e);
                return false;
            }
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean lsSuccess = (Boolean) object;
                if (!lsSuccess) {
                    listener.OnFailed(message);
                } else {
                    listener.OnSuccess(message);
                }
            }
        });
    }
}
