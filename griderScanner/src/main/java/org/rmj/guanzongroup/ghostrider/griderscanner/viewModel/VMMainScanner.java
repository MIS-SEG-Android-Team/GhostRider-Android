/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.griderScanner
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.griderscanner.viewModel;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.rmj.g3appdriver.dev.Database.GCircle.Entities.EBranchLoanApplication;
import org.rmj.g3appdriver.dev.Database.GCircle.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.dev.Database.GCircle.Entities.EFileCode;
import org.rmj.g3appdriver.GCircle.room.Repositories.RBranchLoanApplication;
import org.rmj.g3appdriver.GCircle.room.Repositories.RFileCode;
import org.rmj.g3appdriver.dev.HttpHeaders;
import org.rmj.g3appdriver.dev.WebClient;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.SessionManager;
import org.rmj.g3appdriver.lib.Account.EmployeeMaster;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.WebApi;

import java.util.List;

public class VMMainScanner extends AndroidViewModel {
    private final Application instance;
    private final RFileCode peFileCode;
    private final LiveData<List<EFileCode>> collectionList;
    private final RBranchLoanApplication poCreditApp;
    private final EmployeeMaster poEmploye;
    private MutableLiveData<String> poBranchID;
    private final SessionManager poSession;
    public VMMainScanner(@NonNull Application application) {
        super(application);
        this.instance = application;
        peFileCode = new RFileCode(application);
        this.collectionList = peFileCode.getAllFileCode();
        this.poCreditApp = new RBranchLoanApplication(application);
        poEmploye = new EmployeeMaster(application);
        poSession = new SessionManager(application);
    }
    public interface OnImportCallBack{
        void onStartImport();
        void onSuccessImport();
        void onImportFailed(String message);
    }

    public LiveData<EEmployeeInfo> getEmployeeInfo(){
        return poEmploye.GetEmployeeInfo();
    }

    public LiveData<List<EBranchLoanApplication>> getBranchCreditApplication(){
        return poCreditApp.getBranchCreditApplication();
    }
    public void ImportRBranchApplications(OnImportCallBack callBack){

        JSONObject loJson = new JSONObject();
        try {
            String brnCD = poSession.getBranchCode();
            loJson.put("bycode", true);
            loJson.put("value", brnCD);
            new ImportBranchApplications(instance, callBack).execute(loJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private static class ImportBranchApplications extends AsyncTask<JSONObject, Void, String> {
        private final HttpHeaders headers;
        private final RBranchLoanApplication brnRepo;
        private final ConnectionUtil conn;
        private final WebApi webApi;
        private final AppConfigPreference loConfig;
        private final OnImportCallBack callback;

        public ImportBranchApplications(Application instance,  OnImportCallBack callback) {
            this.headers = HttpHeaders.getInstance(instance);
            this.brnRepo = new RBranchLoanApplication(instance);
            this.conn = new ConnectionUtil(instance);
            this.loConfig = AppConfigPreference.getInstance(instance);
            this.webApi = new WebApi(loConfig.getTestStatus());
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            callback.onStartImport();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(JSONObject... strings) {
            String response = "";
            try{
                if(conn.isDeviceConnected()) {
                    response = WebClient.sendRequest(webApi.getUrlBranchLoanApp(loConfig.isBackUpServer()), strings[0].toString(), headers.getHeaders());
                    JSONObject jsonResponse = new JSONObject(response);
                    String lsResult = jsonResponse.getString("result");
                    if (lsResult.equalsIgnoreCase("success")) {
                        JSONArray laJson = jsonResponse.getJSONArray("detail");
//                        if(!brnRepo.insertBranchApplicationInfos(laJson)){
//                            response = AppConstants.ERROR_SAVING_TO_LOCAL();
//                        }else {
//                            brnRepo.getBranchCreditApplication();
//                        }
                    }
                } else {
                    response = AppConstants.SERVER_NO_RESPONSE();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject loJson = new JSONObject(s);
                Log.e("VMMAinScanner", loJson.getString("result"));
                String lsResult = loJson.getString("result");
                if(lsResult.equalsIgnoreCase("success")){
                    callback.onSuccessImport();
                } else {
                    JSONObject loError = loJson.getJSONObject("error");
                    String message = getErrorMessage(loError);
                    callback.onImportFailed(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onImportFailed(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                callback.onImportFailed(e.getMessage());
            }
        }
    }

}
