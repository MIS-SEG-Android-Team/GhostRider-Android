/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.dailyCollectionPlan
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.GCircle.Apps.Dcp.obj.PAY;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBankInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;
import org.rmj.g3appdriver.GCircle.room.Repositories.RBankInfo;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.model.LRDcp;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.PaidDCP;
import org.rmj.g3appdriver.etc.ImageFileCreator;
import org.rmj.g3appdriver.lib.Location.GmsLocationRetriever;
import org.rmj.g3appdriver.lib.Location.HmsLocationRetriever;
import org.rmj.g3appdriver.lib.Location.LocationRetriever;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VMPaidTransaction extends AndroidViewModel {
    private static final String TAG = VMPaidTransaction.class.getSimpleName();

    private Context instance;

    private final EmployeeMaster poUser;
    private final RBankInfo poBank;
    private final ConnectionUtil poConn;
    private final AppConfigPreference poConfig;

    private final PAY poSys;

    private EDCPCollectionDetail loDetail;

    private double pnAmount = 0;

    private final MutableLiveData<Double> pnTotalx = new MutableLiveData<>();
    private final MutableLiveData<Double> pnRebate = new MutableLiveData<>();
    private final MutableLiveData<Double> pnPenlty = new MutableLiveData<>();
    private final MutableLiveData<String> psMssage = new MutableLiveData<>();

    private boolean isDuePass = true;

    private String message;

    public VMPaidTransaction(@NonNull Application application) {
        super(application);

        this.instance = application;
        this.poUser = new EmployeeMaster(application);
        this.poSys = new PAY(application);
        this.poBank = new RBankInfo(application);
        this.poConfig = AppConfigPreference.getInstance(application);
        this.poConn = new ConnectionUtil(application);
        this.pnRebate.setValue((double) 0);
        this.pnPenlty.setValue((double) 0);
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> GetUserInfo(){
        return poUser.GetEmployeeBranch();
    }

    public LiveData<EDCPCollectionDetail> GetCollectionDetail(String TransNo, int EntryNo, String Accountno){
        return poSys.GetAccountDetailForTransaction(TransNo, Accountno, String.valueOf(EntryNo));
    }

    public LiveData<String> GetPrNumber(){
        MutableLiveData<String> loPrNox = new MutableLiveData<>();
        loPrNox.setValue(poConfig.getDCP_PRNox());
        return loPrNox;
    }

    public void InitPurchaseInfo(EDCPCollectionDetail foVal){
        this.loDetail = foVal;
    }

    public void setAmount(Double fnAmount){
        this.pnAmount = fnAmount;
        if(!isDuePass) {
            calculateRebate();
        } else {
            calculatePenalty();
        }

        double lnRebate = pnRebate.getValue();
        double lnPnalty = pnPenlty.getValue();
        double lnTotal = pnAmount + lnPnalty - lnRebate;
        pnTotalx.setValue(lnTotal);
    }

    public boolean setRebate(Double fnDiscount){
        try {
            if(loDetail.getDelayAvg() != 0.00){
                psMssage.setValue("Unable to calculate rebate for accounts with late payment or not updated payment.");

                double lnPnalty = pnPenlty.getValue();
                double lnTotal = pnAmount + lnPnalty - 0.0;
                pnTotalx.setValue(lnTotal);
                return false;
            } else {
                calculateRebate();
                double lnRebate = pnRebate.getValue();
                if (fnDiscount > lnRebate) {
                    psMssage.setValue("Rebate given is greater than the supposed rebate.");
                } else {
                    psMssage.setValue("");
                }

                double lnPnalty = pnPenlty.getValue();
                double lnTotal = pnAmount + lnPnalty - lnRebate;
                pnTotalx.setValue(lnTotal);
                return true;
            }
        } catch(NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setPenalty(Double fnOthers){
        double lnRebate = pnRebate.getValue();
        double lnTotal = pnAmount + fnOthers - lnRebate;
        pnTotalx.setValue(lnTotal);
    }

    public void setIsDuePass(boolean isDuePass){
        this.isDuePass = isDuePass;
        if(!isDuePass){
            psMssage.setValue("");
        }
    }

    public LiveData<Double> getTotalAmount(){
        return pnTotalx;
    }

    public LiveData<String[]> getBankNameList(){
        return poBank.getBankNameList();
    }

    public LiveData<List<EBankInfo>> getBankInfoList(){
        return poBank.getBankInfoList();
    }

    public LiveData<String> getRebateNotice(){
        return psMssage;
    }

    private void calculateRebate(){
        try {
            if(loDetail.getDelayAvg() != 0.00){
                psMssage.setValue("Unable to calculate rebate for delay account.");

                double lnPnalty = pnPenlty.getValue();
                double lnTotal = pnAmount + lnPnalty - 0.0;
                pnTotalx.setValue(lnTotal);
            } else {
                double reb = Double.parseDouble(poConfig.getDCP_CustomerRebate());

                double lnAmortx = loDetail.getMonAmort();
                double lnAmtDue = loDetail.getAmtDuexx();

                double lnRebate = poSys.CalculateRebate(pnAmount, lnAmortx, lnAmtDue, reb);
                pnRebate.setValue(lnRebate);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void calculatePenalty(){
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                //Split the date string using '-' to get the day...
                String lsDayDuex = loDetail.getDueDatex().split("-")[2];

                //Check here if the due date is on the maximum days per month
                // if true check the maximum day of month and set it as the due date for this current month...
                if(lsDayDuex.equalsIgnoreCase("31")) {
                    LocalDate lastDayOfMonth = LocalDate.parse(AppConstants.CURRENT_DATE(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            .with(TemporalAdjusters.lastDayOfMonth());
                    lsDayDuex = String.valueOf(lastDayOfMonth.getDayOfMonth());
                }
                String lsCrtYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
                String lsCrtMnth = new SimpleDateFormat("MM", Locale.getDefault()).format(Calendar.getInstance().getTime());
                String lsDueDate = lsCrtYear + "-" + lsCrtMnth + "-" + lsDayDuex;
                DateFormat loFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date ldPurchase = loFormat.parse(loDetail.getPurchase());
                Date ldDueDatex = loFormat.parse(lsDueDate);
                double lnMonAmort = loDetail.getMonAmort();
                double lnABalance = Double.parseDouble(loDetail.getABalance());
                double lnPenalty = poSys.CalculatePenalty(ldPurchase, ldDueDatex, lnMonAmort, lnABalance, pnAmount);
                pnPenlty.setValue(lnPenalty);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public LiveData<Double> getRebate(){
        return pnRebate;
    }

    public LiveData<Double> getPenalty(){
        return pnPenlty;
    }

    public void SavePaymentInfo(PaidDCP foVal, ViewModelCallback callback){

        TaskExecutor.Execute(foVal, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnStartSaving();
            }

            @Override
            public Object DoInBackground(Object args) {
                String lsResult = poSys.SavePaidTransaction((PaidDCP) args);
                if(lsResult == null){
                    message = poSys.getMessage();
                    return false;
                }

                if(!poConn.isDeviceConnected()){
                    message = "Payment info has been save to local device.";
                    return true;
                }

                if(!poSys.UploadPaidTransaction(lsResult)){
                    message = poSys.getMessage();
                    return false;
                }
                return true;
            }

            @Override
            public void OnPostExecute(Object object) {
                Boolean isSuccess = (Boolean) object;
                if(!isSuccess){
                    callback.OnFailedResult(message);
                } else {
                    callback.OnSuccessResult();
                }
            }
        });
    }

    public void InitCameraLaunch(Activity activity, String TransNox, OnInitializeCameraCallback callback){

        ImageFileCreator loImage = new ImageFileCreator(instance, AppConstants.SUB_FOLDER_SELFIE_LOG, TransNox);
        String[] lsResult = new String[4];

        TaskExecutor.Execute(null, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnInit();
            }

            @Override
            public Object DoInBackground(Object args) {

                if(!loImage.IsFileCreated(true)){
                    message = loImage.getMessage();
                    return null;
                }

                lsResult[0] = loImage.getFilePath();
                lsResult[1] = loImage.getFileName();

                String lsCompx = android.os.Build.MANUFACTURER.toLowerCase();
                LocationRetriever.iLocationRetriever location;

                if (!lsCompx.equalsIgnoreCase("huawei")) {
                    location = new GmsLocationRetriever();
                } else {
                    location = new HmsLocationRetriever();
                }

                location.GetLocation(instance, new LocationRetriever.OnRetrieveLocationListener() {
                    @Override
                    public void OnRetrieve(String latitude, String longitude) {

                        lsResult[2] = latitude;
                        lsResult[3] = longitude;

                        Intent loIntent = loImage.getCameraIntent();
                        loIntent.putExtra("result", true);

                        callback.OnSuccess(loIntent, lsResult);
                    }

                    @Override
                    public void OnFailed(String message, String latitude, String longitude) {

                        lsResult[2] = latitude;
                        lsResult[3] = longitude;

                        Intent loIntent = loImage.getCameraIntent();
                        loIntent.putExtra("result", false);

                        callback.OnFailed(message, loIntent, lsResult);
                    }
                });

                return null;
            }

            @Override
            public void OnPostExecute(Object object) {
            }
        });
    }
}