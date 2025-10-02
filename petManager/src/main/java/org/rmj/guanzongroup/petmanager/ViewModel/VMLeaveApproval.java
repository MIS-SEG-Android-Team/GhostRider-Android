/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.ahMonitoring
 * Electronic Personnel Access Control Security System
 * project file created : 8/16/21 8:50 AM
 * project file last modified : 8/16/21 8:21 AM
 */

package org.rmj.guanzongroup.petmanager.ViewModel;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj.EmployeeLeave;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.pojo.LeaveApprovalInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeLeave;
import org.rmj.g3appdriver.lib.Etc.Branch;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VMLeaveApproval extends AndroidViewModel {

    public static final String TAG = VMLeaveApproval.class.getSimpleName();
    private final Application instance;
    private final Branch pobranch;
    private final EmployeeLeave poSys;
    private final ConnectionUtil poConn;

    private String message ;

    private final MutableLiveData<String> TransNox = new MutableLiveData<>();
    private final MutableLiveData<Integer> pnCredit = new MutableLiveData<>();
    private final MutableLiveData<Integer> pnWithPay = new MutableLiveData<>();
    private final MutableLiveData<Integer> pnWOPay = new MutableLiveData<>();
    private final MutableLiveData<Integer> pnNoDays = new MutableLiveData<>();

    public VMLeaveApproval(@NonNull Application application) {
        super(application);
        this.instance = application;
        this.pobranch = new Branch(instance);
        this.poSys = new EmployeeLeave(instance);
        this.poConn = new ConnectionUtil(instance);
        this.TransNox.setValue("");
    }

    public interface OnDownloadLeaveAppInfo {
        void OnDownload();
        void OnSuccessDownload(String TransNox);
        void OnFailedDownload(String message);
    }

    public interface OnConfirmLeaveAppCallback {
        void onConfirm();
        void onSuccess(String message);
        void onFailed(String message);
    }

    public void setTransNox(String transNox){
        this.TransNox.setValue(transNox);
    }

    public LiveData<String> getTransNox(){
        return TransNox;
    }

    public LiveData<EEmployeeLeave> getEmployeeLeaveInfo(String TransNox){
        return poSys.GetLeaveApplicationInfo(TransNox);
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> getUserInfo(){
        return poSys.GetUserInfo();
    }

    public LiveData<EBranchInfo> getUserBranchInfo(){
        return pobranch.getUserBranchInfo();
    }
    public void downloadLeaveApplication(String transNox, OnDownloadLeaveAppInfo callBack){
        TaskExecutor.Execute(transNox, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callBack.OnDownload();
            }
            @Override
            public Object DoInBackground(Object args) {

                try{
                    if(!poConn.isDeviceConnected()) {
                        message = poConn.getMessage();
                        return false;
                    }

                    if(!poSys.DownloadApplication(String.valueOf(args))){
                        message = poSys.getMessage();
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
                if(lsSuccess){
                    callBack.OnSuccessDownload(transNox);
                } else {
                    callBack.OnFailedDownload(message);
                }
            }
        });
    }

    public void confirmLeaveApplication(LeaveApprovalInfo infoModel, OnConfirmLeaveAppCallback callBack){
        TaskExecutor.Execute(infoModel, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callBack.onConfirm();
            }
            @Override
            public Object DoInBackground(Object args) {

                try{
                    if(!infoModel.isDataValid()){
                        message = infoModel.getMessage();
                        return false;
                    }

                    if(!poConn.isDeviceConnected()){
                        message = poConn.getMessage() + " Your approval will be automatically send if device is reconnected to internet.";
                        return false;
                    }

                    if(!poSys.UploadApproval(infoModel)){
                        message = poSys.getMessage();
                        return false;
                    }

                    String lsTransNox = poSys.SaveApproval(infoModel);
                    if(lsTransNox == null){
                        message = poSys.getMessage();
                        return false;
                    }
                    message = "Leave Update Save Succesfully!";
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
                if(lsSuccess){
                    callBack.onSuccess(message);
                } else {
                    callBack.onFailed(message);
                }
            }
        });
    }

//

    public void setCredits(int value){
        this.pnCredit.setValue(value);
    }

    public void calculateWithOPay(int fnWithOPy){
        int lnNoDays = pnNoDays.getValue();
        int lnCredt = pnCredit.getValue();
        if(lnCredt > 0) {
            int lnWOPayx;
            if (fnWithOPy <= lnNoDays) {
                lnWOPayx = Math.abs(fnWithOPy - lnNoDays);
                pnWOPay.setValue(lnWOPayx);
            } else {
                pnWithPay.setValue(lnNoDays);
                calculateWithOPay(lnNoDays);
            }
        } else {
            pnWOPay.setValue(lnNoDays);
            pnWithPay.setValue(0);
        }
    }

    public void calculateWithPay(int fnWithPay){
        int lnNoDays = pnNoDays.getValue();
        int lnCredt = pnCredit.getValue();
        if(lnCredt > 0) {
            if (fnWithPay <= lnNoDays) {
                int lnWithPay;
                lnWithPay = Math.abs(lnNoDays - fnWithPay);
                pnWithPay.setValue(lnWithPay);
            } else {
                pnWOPay.setValue(lnNoDays);
                calculateWithPay(lnNoDays);
            }
        } else {
            pnWOPay.setValue(lnNoDays);
            pnWithPay.setValue(0);
        }
    }

    public LiveData<Integer> getWithPay(){
        return pnWithPay;
    }

    public LiveData<Integer> getWOPay(){
        return pnWOPay;
    }

    public void calculateLeavePay(int fnLeaveTp, String fsDateFrm, String fsDateTo) throws ParseException {
        int lnCredt = pnCredit.getValue();
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat loDate = new SimpleDateFormat("yyyy-MM-dd");
        Date dateFrom = loDate.parse(Objects.requireNonNull(fsDateFrm));
        Date dateTo = loDate.parse(Objects.requireNonNull(fsDateTo));
        long diff = Objects.requireNonNull(dateTo).getTime() - Objects.requireNonNull(dateFrom).getTime();
        long noOfDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
        pnNoDays.setValue((int) noOfDays);
        if(fnLeaveTp == 3){
            pnWOPay.setValue(0);
            pnWithPay.setValue((int) noOfDays);
        } else if(noOfDays > lnCredt && lnCredt != 0){
            int lnDiff = (int) (noOfDays - lnCredt);
            pnWOPay.setValue(lnDiff);
            pnWithPay.setValue(lnCredt);
        } else if(lnCredt == 0){
            pnWOPay.setValue((int) noOfDays);
            pnWithPay.setValue(0);
        } else {
            pnWithPay.setValue((int) noOfDays);
            pnWOPay.setValue(0);
        }
    }

    public LeavePay CalculateLeavePay(int fnLeaveTp, int fnCredit, String fsDateFrm, String fsDateTo) throws Exception{
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat loDate = new SimpleDateFormat("yyyy-MM-dd");
        Date dateFrom = loDate.parse(Objects.requireNonNull(fsDateFrm));
        Date dateTo = loDate.parse(Objects.requireNonNull(fsDateTo));
        long diff = Objects.requireNonNull(dateTo).getTime() - Objects.requireNonNull(dateFrom).getTime();
        long noOfDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;

        // check if leave type is Birthday leave.
        // Birthday leave doesn't require leave credits to have leave with pay.
        if(fnLeaveTp == 3){
            return new LeavePay(1, 0);
        }


        if(fnCredit == 0){
            return new LeavePay(0, 0);
        }

        if(noOfDays > fnCredit){
            int lnDiff = (int) (noOfDays - fnCredit);
            return new LeavePay(fnCredit, lnDiff);
        }

        return new LeavePay((int) noOfDays, 0);

    }

    public static class LeavePay{
        private final int lnWithPay;
        private final int lnWthOPay;

        public LeavePay(int lnWithPay, int lnWthOPay) {
            this.lnWithPay = lnWithPay;
            this.lnWthOPay = lnWthOPay;
        }

        public String getWithPay() {
            return String.valueOf(lnWithPay);
        }

        public String getWithoutPay() {
            return String.valueOf(lnWthOPay);
        }
    }
}