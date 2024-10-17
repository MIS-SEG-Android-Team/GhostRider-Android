package org.rmj.guanzongroup.ghostrider.epacss.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Apps.BullsEye.obj.BranchPerformance;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj.EmployeeLeave;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj.EmployeeOB;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.OnCheckEmployeeApplicationListener;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeBusinessTrip;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeLeave;
import org.rmj.g3appdriver.GCircle.room.Entities.ERaffleStatus;
import org.rmj.g3appdriver.lib.Notifications.Obj.Notification;
import org.rmj.g3appdriver.lib.Panalo.Obj.ILOVEMYJOB;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMHomeBH extends AndroidViewModel {
    private static final String TAG = VMHomeAH.class.getSimpleName();
    private final EmployeeMaster poEmploye;
    private final BranchPerformance poSys;
    private final Notification poNotification;
    private final Application instance;
    private final ConnectionUtil poConn;
    private String message;
    private final ILOVEMYJOB poPanalo;

    public VMHomeBH(@NonNull Application application) {
        super(application);
        poEmploye = new EmployeeMaster(application);
        this.poSys = new BranchPerformance(application);
        this.poNotification = new Notification(application);
        this.poPanalo = new ILOVEMYJOB(application);
        this.poConn = new ConnectionUtil(application);
        this.instance = application;
    }

    public LiveData<Integer> GetAllUnreadNotificationCount() {
        return poNotification.GetAllUnreadNotificationCount();
    }

    public LiveData<List<EEmployeeLeave>> GetLeaveForApproval() {
        return new EmployeeLeave(instance).GetLeaveApplicationsForApproval();
    }

    public LiveData<List<EEmployeeBusinessTrip>> GetOBForApproval() {
        return new EmployeeOB(instance).GetOBApplicationsForApproval();
    }

    public LiveData<EEmployeeInfo> getEmployeeInfo() {
        return poEmploye.GetEmployeeInfo();
    }

    public LiveData<String> GetCurrentMCSalesPerformance() {
        return poSys.GetCurrentMCSalesPerformance();
    }

    public LiveData<String> GetCurentSPSalesPerformance() {
        return poSys.GetCurentSPSalesPerformance();
    }

    public LiveData<ERaffleStatus> GetRaffleStatus() {
        return poPanalo.GetRaffleStatus();
    }

    public LiveData<String> GetJobOrderPerformance() {
        return poSys.GetJobOrderPerformance();
    }

    public void CheckApplicationsForApproval(OnCheckEmployeeApplicationListener listener) {
        TaskExecutor.Execute(null, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {
                try {
                    if (!poConn.isDeviceConnected()) {
                        message = poConn.getMessage();
                        return false;
                    }

                    if(!new EmployeeLeave(instance).ImportApplications()){
                        Log.e(TAG, "Failed to import leave applications");
                    }

                    Thread.sleep(1000);

                    if(!new EmployeeOB(instance).ImportApplications()){
                        Log.e(TAG, "Failed to import business trip applications");
                    }

                    return true;
                } catch (Exception e) {
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
                    listener.OnSuccess();
                }
            }
        });
    }
}