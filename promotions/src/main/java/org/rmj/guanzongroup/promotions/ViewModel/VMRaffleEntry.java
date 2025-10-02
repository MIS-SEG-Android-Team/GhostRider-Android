/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.promotions
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.guanzongroup.promotions.ViewModel;

import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DTownInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ERaffleBasis;
import org.rmj.g3appdriver.GCircle.room.Entities.ERaffleInfo;
import org.rmj.g3appdriver.lib.Etc.Branch;
import org.rmj.g3appdriver.GCircle.room.Repositories.RRaffleInfo;
import org.rmj.g3appdriver.lib.Etc.Town;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;
import org.rmj.guanzongroup.promotions.Etc.RaffleEntryCallback;
import org.rmj.guanzongroup.promotions.Model.RaffleEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VMRaffleEntry extends AndroidViewModel {

    public static final String TAG = VMRaffleEntry.class.getSimpleName();
    private final HttpHeaders headers;
    private final ConnectionUtil conn;
    private static LiveData<List<ERaffleBasis>> raffleBasis;
    private static LiveData<String[]> raffleBasisDesc;
    private final RRaffleInfo raffleRepo;
    private final EmployeeSession session;
    private final Town poTown;
    private final Branch poBranch;

    private final GCircleApi poApi;
    private final AppConfigPreference loConfig;

    private final Application instance;

    public VMRaffleEntry(@NonNull Application application) {
        super(application);

        this.instance = application;
        this.conn = new ConnectionUtil(application);
        this.headers = HttpHeaders.getInstance(instance);
        this.raffleRepo = new RRaffleInfo(instance);
        this.loConfig = AppConfigPreference.getInstance(instance);
        this.poApi = new GCircleApi(instance);
        this.poTown = new Town(application);
        this.poBranch = new Branch(application);

        raffleBasisDesc = raffleRepo.getAllRaffleBasisDesc();
        raffleBasis = raffleRepo.getAllRaffleBasis();
        session = EmployeeSession.getInstance(application);
    }

    public LiveData<EBranchInfo> getUserBranchInfo(){
        return poBranch.getUserBranchInfo();
    }

    public LiveData<List<DTownInfo.TownProvinceInfo>> getTownProvinceInfo(){
        return poTown.getTownProvinceInfo();
    }

    public LiveData<String[]> getDocuments(){
        return raffleBasisDesc;
    }

    public LiveData<List<ERaffleBasis>> getRaffleBasis() {
        return raffleBasis;
    }

    public void importDocuments(){

        TaskExecutor.Execute(null, new OnDoBackgroundTaskListener() {
            @Override
            public Object DoInBackground(Object args) {

                String response = "";

                try {

                    response = WebClient.sendRequest(poApi.getUrlImportRaffleBasis(), new JSONObject().toString(), headers.getHeaders());

                    Log.e(TAG, response);

                    JSONObject jsonResponse = new JSONObject(response);
                    String lsResult = jsonResponse.getString("result");

                    if (lsResult.equalsIgnoreCase("success")) {
                        JSONArray laJson = jsonResponse.getJSONArray("detail");
                        List<ERaffleBasis> raffleBasisList = new ArrayList<>();
                        for (int x = 0; x < laJson.length(); x++) {
                            ERaffleBasis info = new ERaffleBasis();
                            JSONObject json = laJson.getJSONObject(x);
                            info.setDivision(json.getString("sDivision"));
                            info.setTableNme(json.getString("sTableNme"));
                            info.setReferCde(json.getString("sReferCde"));
                            info.setReferNme(json.getString("sReferNme"));
                            info.setRecdStat(json.getString("cRecdStat"));
                            info.setModified(json.getString("dModified"));
                            raffleBasisList.add(info);
                        }

                        raffleRepo.insertBulkData(raffleBasisList);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                return response;

            }

            @Override
            public void OnPostExecute(Object object) {

                try {

                    JSONObject loJson = new JSONObject(object.toString());
                    String lsResult = loJson.getString("result");

                    if (lsResult.equalsIgnoreCase("success")) {

                    } else {
                        Log.e(TAG, loJson.toString());
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    public void submit(RaffleEntry voucher, RaffleEntryCallback callBack){
        if (voucher.isValidInfo()) {

            String lsUserIDx = session.getUserID();

            TaskExecutor.Execute(voucher, new OnTaskExecuteListener() {
                @Override
                public void OnPreExecute() {
                    callBack.OnSendingEntry("Raflle Entry", "Sending raffle entry. Please wait...");
                }

                @Override
                public Object DoInBackground(Object args) {

                    String response = "";
                    RaffleEntry vouchers = (RaffleEntry) args;

                    try {
                        ERaffleInfo info = new ERaffleInfo();
                        info.setBranchCd(vouchers.getBranchCodexx());
                        info.setTransact(getDateTransact());
                        info.setClientNm(vouchers.getCustomerName());
                        info.setAddressx(vouchers.getCustomerAddx());
                        info.setTownIDxx(vouchers.getCustomerTown());
                        info.setProvIDxx(vouchers.getCustomerProv());
                        info.setDocTypex(vouchers.getDocumentType());
                        info.setDocNoxxx(vouchers.getDocumentNoxx());
                        info.setMobileNo(vouchers.getMobileNumber());
                        info.setSendStat('0');

                        raffleRepo.insertRaffleEntry(info);

                        if(conn.isDeviceConnected()) {
                            JSONObject loJson = new JSONObject();
                            loJson.put("brc", vouchers.getBranchCodexx());
                            loJson.put("typ", vouchers.getDocumentType());
                            loJson.put("dte", getDateTransact());
                            loJson.put("nox", vouchers.getDocumentNoxx());
                            loJson.put("mob", vouchers.getMobileNumber());
                            loJson.put("nme", vouchers.getCustomerName());
                            loJson.put("add", vouchers.getCustomerAddx());
                            loJson.put("twn", vouchers.getCustomerTown());
                            loJson.put("prv", vouchers.getCustomerProv());
                            loJson.put("cid", "");
                            loJson.put("div", "");
                            loJson.put("ent", lsUserIDx);

                            String lsUrl = "https://apps.guanzongroup.com.ph/promo/fblike/encodex.php";
                            Log.e(TAG, lsUrl);

                            response = WebClient.sendRequest(lsUrl, loJson.toString(), headers.getHeaders());
                            if(response!=null){
                                JSONObject loResponse = new JSONObject(response);
                                String lsResult = loResponse.getString("result");
                                if (lsResult.equalsIgnoreCase("success")) {
                                    info.setSendStat('1');
                                    info.setTimeStmp(getDateTransact());
                                    raffleRepo.updateRaffleEntry(info);
                                }
                            } else {
                                response = AppConstants.SERVER_NO_RESPONSE();
                            }

                            Log.e(TAG, response);
                        } else {
                            return AppConstants.NO_INTERNET();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return response;

                }

                @Override
                public void OnPostExecute(Object object) {

                    try {
                        JSONObject loJson = new JSONObject(object.toString());
                        String lsResult = loJson.getString("result");

                        if (lsResult.equalsIgnoreCase("success")) {
                            callBack.OnSuccessEntry();
                        } else {
                            Log.e(TAG, loJson.toString());
                            JSONObject loError = loJson.getJSONObject("error");
                            String message = getErrorMessage(loError);
                            callBack.OnFailedEntry(message);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
        } else {
            callBack.OnFailedEntry(voucher.getMessage());
        }
    }

    String getDateTransact(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

}