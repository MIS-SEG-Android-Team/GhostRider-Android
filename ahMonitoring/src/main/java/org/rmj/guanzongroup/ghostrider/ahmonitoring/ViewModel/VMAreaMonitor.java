/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.ahmonitoring.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchPerformance;
import org.rmj.g3appdriver.GCircle.Apps.BullsEye.OnImportPerformanceListener;
import org.rmj.g3appdriver.GCircle.Apps.BullsEye.obj.AreaPerformance;
import org.rmj.g3appdriver.GCircle.Apps.BullsEye.obj.BranchPerformance;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMAreaMonitor extends AndroidViewModel {
    public static final String TAG = VMAreaMonitor.class.getSimpleName();

    private final Application instance;
    private final AreaPerformance poSys;
    private final ConnectionUtil poConn;
    private final BranchPerformance loBranch;

    private String message;

    public VMAreaMonitor(@NonNull Application application) {
        super(application);
        this.instance = application;
        this.poSys = new AreaPerformance(application);
        this.poConn = new ConnectionUtil(application);
        this.loBranch = new BranchPerformance(instance);
    }

    public LiveData<List<EBranchPerformance>> GetTopBranchPerformerForJobOrder() {
        return poSys.GetTopBranchPerformerForJobOrder();
    }
    public LiveData<List<EBranchPerformance>> GetTopBranchPerformerForSPSales() {
        return poSys.GetTopBranchPerformerForSPSales();
    }
    public LiveData<List<EBranchPerformance>> GetTopBranchPerformerForMCSales() {
        return poSys.GetTopBranchPerformerForMCSales();
    }
    public LiveData<String> GetCurrentMCSalesPerformance(){
        return poSys.GetCurrentMCSalesPerformance();
    }

    public LiveData<String> GetCurentSPSalesPerformance() {
        return poSys.GetCurentSPSalesPerformance();
    }
    public LiveData<String> GetJobOrderPerformance() {

        return poSys.GetJobOrderPerformance();
    }

    public void ImportPerformance(OnImportPerformanceListener listener){
        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                listener.OnImport();
            }

            @Override
            public Object DoInBackground(Object args) {
                try{
                    if(!poConn.isDeviceConnected()){
                        message = poConn.getMessage();
                        return false;
                    }

                    if(!poSys.ImportData()){
                        message = poSys.getMessage();
                        return false;
                    }

                    if(!loBranch.ImportData()){
                        message = loBranch.getMessage();
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
                boolean isSuccess = (boolean) object;
                if(!isSuccess){
                    listener.OnFailed(message);
                    return;
                }

                listener.OnSuccess();
            }
        });

    }
}