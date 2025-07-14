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

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBranchPerformance;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchPerformance;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.lib.Etc.Branch;
import org.rmj.g3appdriver.GCircle.room.Repositories.RBranchPerformance;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Apps.BullsEye.obj.BranchPerformance;

import java.util.List;

public class VMBranchMonitor extends AndroidViewModel {
    public static final String TAG = VMBranchMonitor.class.getSimpleName();
    private final EmployeeMaster poEmploye;
    private final RBranchPerformance poDatabse;
    private  final BranchPerformance poSys;
    private final Branch poBranch;
    private final MutableLiveData<String> psType = new MutableLiveData<>();

    public VMBranchMonitor(@NonNull Application application) {
        super(application);
        poDatabse = new RBranchPerformance(application);
        poSys = new BranchPerformance(application);
        poBranch = new Branch(application);
        psType.setValue("MC");
        poEmploye = new EmployeeMaster(application);
    }

    public LiveData<List<EBranchPerformance>>  getAllBranchPerformanceInfoByBranch(String branchCD){
        return poDatabse.getAllBranchPerformanceInfoByBranch(branchCD);
    }
    public LiveData<EEmployeeInfo> getEmployeeInfo(){
        return poEmploye.GetEmployeeInfo();
    }
    public LiveData<List<DBranchPerformance.PeriodicalPerformance>> GetMCSalesPeriodicPerformance(String BranchCd){
        return poSys.GetMCSalesPeriodicPerformance(BranchCd);
    }

    public LiveData<List<DBranchPerformance.PeriodicalPerformance>> GetSPSalesPeriodicPerformance(String BranchCd){
        return poSys.GetSPSalesPeriodicPerformance(BranchCd);
    }

    public LiveData<List<DBranchPerformance.PeriodicalPerformance>> GetJobOrderPeriodicPerformance(String BranchCd){
        return poSys.GetJobOrderPeriodicPerformance(BranchCd);
    }

    public LiveData<String> getBranchName(String brnCD){
        return poBranch.getBranchName(brnCD);
    }

    public void setType(String value){
        psType.setValue(value);
    }

    public LiveData<String> getType(){
        return psType;
    }

    public LiveData<String> GetCurrentMCSalesPerformance(String brnCD) {
        return poSys.GetCurrentMCSalesPerformance(brnCD);
    }
    public LiveData<String> GetCurrentSPSalesPerformance(String brnCD) {
        return poSys.GetCurentSPSalesPerformance(brnCD);
    }
    public LiveData<String> GetJobOrderPerformance(String brnCD) {
        return poSys.GetJobOrderPerformance(brnCD);
    }
}