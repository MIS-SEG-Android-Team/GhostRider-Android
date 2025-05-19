package org.rmj.guanzongroup.ghostrider.notifications.ViewModel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DPayslip;
import org.rmj.g3appdriver.GCircle.room.Entities.ENotificationMaster;
import org.rmj.g3appdriver.lib.Notifications.NOTIFICATION_STATUS;
import org.rmj.g3appdriver.lib.Notifications.Obj.Payslip;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMPaySlipList extends AndroidViewModel {
    private static final String TAG = VMPaySlipList.class.getSimpleName();

    private final Payslip poSys;
    private final ConnectionUtil poConn;

    private String message;

    public VMPaySlipList(@NonNull Application application) {
        super(application);
        this.poSys = new Payslip(application);
        this.poConn = new ConnectionUtil(application);
    }

    public interface OnDownloadPayslipListener {
        void OnDownload();

        void OnSuccess(Uri payslip);

        void OnFailed(String message);
    }

    public LiveData<List<DPayslip.Payslip>> GetPaySlipList() {
        return poSys.GetPaySliplist();
    }

    public void SendReadResponse(String messageID) {

        TaskExecutor.Execute(messageID, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {

                if (!poConn.isDeviceConnected()) {
                    Log.e(TAG, poConn.getMessage());
                    return false;
                }

                String lsMessageID = args.toString();

                ENotificationMaster loResult = poSys.SendResponse(lsMessageID, NOTIFICATION_STATUS.READ);

                if (loResult == null) {
                    Log.e(TAG, poSys.getMessage());
                    return false;
                }

                return true;

            }

            @Override
            public void OnPostExecute(Object object) {

            }
        });
    }

    public void DownloadPaySlip(String link, OnDownloadPayslipListener listener) {

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                listener.OnDownload();
            }

            @Override
            public Object DoInBackground(Object args) {
                //String lsLink = strings[0];

                if (!poConn.isDeviceConnected()) {
                    message = poSys.getMessage();
                    return null;
                }

                Uri loFile = poSys.DownloadPaySlip(link);

                if (loFile == null) {
                    message = poSys.getMessage();
                    return null;
                }
                return loFile;
            }

            @Override
            public void OnPostExecute(Object object) {
                Uri lsUri = (Uri) object;
                if (lsUri == null) {
                    listener.OnFailed(message);
                } else {
                    listener.OnSuccess(lsUri);
                }
            }
        });

    }
}