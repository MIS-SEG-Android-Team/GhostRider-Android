package org.rmj.guanzongroup.ghostrider.settings.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import java.util.List;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.rmj.g3appdriver.lib.Version.AppVersion;
import org.rmj.g3appdriver.lib.Version.VersionInfo;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;


public class VMAppVersion extends AndroidViewModel {

    private AppVersion poSys;
    private ConnectionUtil poconn;
    private String message;

    public interface onDownloadVersionList{
        void onDownload();
        void onSuccess(List<VersionInfo> list);
        void onFailed(String message);
    }

    public VMAppVersion(@NonNull Application application) {
        super(application);
        poSys = new AppVersion(application);
        poconn = new ConnectionUtil(application);
    }

    public void getVersionList(onDownloadVersionList listener){

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                listener.onDownload();
            }

            @Override
            public Object DoInBackground(Object args) {

                try {
                    if(!poconn.isDeviceConnected()){
                        message = poconn.getMessage();
                        return null;
                    }

                    List<VersionInfo> list = poSys.GetVersionInfo();

                    if(list == null){
                        message = poSys.getMessage();
                        return null;
                    }

                    return list;
                }catch (Exception e){
                    e.printStackTrace();
                    message = getLocalMessage(e);
                    return null;
                }

            }

            @Override
            public void OnPostExecute(Object object) {

                List<VersionInfo> versionInfos = (List<VersionInfo>) object;

                if(versionInfos == null){
                    listener.onFailed(message);
                }else {
                    listener.onSuccess(versionInfos);
                }

            }
        });
    }

}
