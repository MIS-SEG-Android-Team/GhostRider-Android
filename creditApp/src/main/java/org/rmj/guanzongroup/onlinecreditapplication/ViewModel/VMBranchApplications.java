package org.rmj.guanzongroup.onlinecreditapplication.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchLoanApplication;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditOnlineApplication;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMBranchApplications extends AndroidViewModel {
    private static final String TAG = VMBranchApplications.class.getSimpleName();

    private final CreditOnlineApplication poApp;

    private String message;

    public VMBranchApplications(@NonNull Application application) {
        super(application);
        this.poApp = new CreditOnlineApplication(application);
    }

    public LiveData<List<EBranchLoanApplication>> GetBranchApplications(){
        return poApp.GetBranchApplications();
    }

    public void ImportBranchApplications(OnDownloadApplicationsListener listener){
        //new ImportApplicationsTask(listener).execute();

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                listener.OnDownload("Credit Online Application", "Downloading entries from your current branch. Please wait...");
            }

            @Override
            public Object DoInBackground(Object args) {

                try{
                    if(!poApp.DownloadBranchApplications()){
                        message = poApp.getMessage();
                        return false;
                    }

                    return true;
                } catch (Exception e){
                    e.printStackTrace();
                    message =e.getMessage();
                    return false;
                }

            }

            @Override
            public void OnPostExecute(Object object) {

                Boolean isSuccess = (Boolean) object;

                if(!isSuccess){
                    listener.OnFailed(message);
                } else {
                    listener.OnSuccess("");
                }

            }
        });
    }

}
