package org.rmj.g3appdriver.GCircle.Apps.User_Guide.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Apps.User_Guide.Object.UserGuides;
import org.rmj.g3appdriver.GCircle.room.Entities.EGuides;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMGuide extends AndroidViewModel {

    private ConnectionUtil poConnection;
    private UserGuides poGuides;
    private String message;

    public interface OnDownloadGuides{
        void OnDownloading();
        void OnSuccess();
        void OnFailed(String message);
    }

    public interface OnUploadGuide{
        void OnSuccess();
        void OnFailed(String message);
    }

    public VMGuide(@NonNull Application application) {
        super(application);

        this.poConnection = new ConnectionUtil(application);
        this.poGuides = new UserGuides(application);
    }

    public void DownloadGuides(OnDownloadGuides callback){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnDownloading();
            }

            @Override
            public Object DoInBackground(Object args) {

                if (!poConnection.isDeviceConnected()){
                    message = poConnection.getMessage();
                    return false;
                }

                if (!poGuides.DownloadGuides()){
                    message = poGuides.getMessage();
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void OnPostExecute(Object object) {

                if (!(Boolean) object){
                    callback.OnFailed(message);
                }else{
                    callback.OnSuccess();
                }
            }
        });
    }

    public void UploadGuide(String filename, byte[] file, OnUploadGuide callback){

        TaskExecutor.Execute(null, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {

                if (!poConnection.isDeviceConnected()){
                    message = poConnection.getMessage();
                    return false;
                }

                if (!poGuides.UploadGuide(filename, file)){
                    message = poGuides.getMessage();
                    return false;
                }

                return true;
            }

            @Override
            public void OnPostExecute(Object object) {

                if(!(Boolean) object){
                    callback.OnFailed(message);
                }else {
                    callback.OnSuccess();
                }
            }
        });
    }

    public LiveData<List<EGuides>> GetGuides(){
        return poGuides.GetGuides();
    }
}
