package org.rmj.guanzongroup.ghostrider.epacss.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VMLoadPDF extends AndroidViewModel {

    public interface OnLoadPDF{
        void OnLoading();
        void OnSuccess(InputStream inputStream);
        void OnFailed(String message);
    }

    public VMLoadPDF(@NonNull Application application) {
        super(application);
    }

    public void DisplayPDF(String url, OnLoadPDF callback){

        TaskExecutor.Execute(url, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnLoading();
            }

            @Override
            public Object DoInBackground(Object args) {
                try{
                    URL url = new URL(args.toString());
                    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

                    if (httpConn.getResponseCode() == 200){
                        return new BufferedInputStream(httpConn.getInputStream());
                    }

                    return null;
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                if (object == null){
                    callback.OnFailed("Failed to load PDF");
                }else{
                    callback.OnSuccess((InputStream) object);
                }
            }
        });
    }
}
