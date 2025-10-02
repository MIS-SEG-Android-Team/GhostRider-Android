package org.rmj.guanzongroup.ghostrider.settings.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import org.rmj.g3appdriver.etc.GMSUtility;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;


public class VMAppVersion extends AndroidViewModel {

    public interface onDownload{
        void onDownloading(int status);
        void onSuccess();
        void onFailed(String message);
    }

    public VMAppVersion(@NonNull Application application) {
        super(application);
    }

    public void GetUpdate(GMSUtility poGMS, onDownload callback){

        TaskExecutor.Execute(null, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {

                poGMS.GetUpdate(new GMSUtility.OnGetUpdate() {
                    @Override
                    public void OnResult(int status) {

                        if (status == 2){

                            poGMS.StartUpdate(new GMSUtility.OnUpdateResult() {
                                @Override
                                public void OnDownloading(int status) {
                                    callback.onDownloading(status);
                                }

                                @Override
                                public void OnSuccess() {
                                    callback.onSuccess();
                                }

                                @Override
                                public void OnFailed(String message) {
                                    callback.onFailed(message);
                                }
                            });

                        }else {
                            callback.onFailed("Update not available");
                        }
                    }
                });
                return null;
            }

            @Override
            public void OnPostExecute(Object object) {}
        });
    }

}
