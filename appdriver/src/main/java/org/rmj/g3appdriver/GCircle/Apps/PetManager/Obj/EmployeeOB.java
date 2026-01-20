/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 8/13/21 10:35 AM
 * project file last modified : 8/13/21 10:35 AM
 */

package org.rmj.g3appdriver.GCircle.Apps.PetManager.Obj;

import static org.rmj.g3appdriver.dev.Api.ApiResult.SERVER_NO_RESPONSE;
import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;
import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.apprdiver.util.SQLUtil;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.model.PetMngr;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.pojo.OBApplication;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.pojo.OBApprovalInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeBusinessTrip;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeBusinessTrip;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.dev.Api.WebClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmployeeOB extends PetMngr {
    private static final String TAG = EmployeeOB.class.getSimpleName();
    private final DEmployeeBusinessTrip poDao;

    public EmployeeOB(Application instance){
        super(instance);
        this.poDao = GGC_GCircleDB.getInstance(instance).employeeOBDao();
    }

    @Override
    public LiveData<DEmployeeInfo.EmployeeBranch> GetUserInfo() {
        return poUser.GetEmployeeBranch();
    }

    @Override
    public boolean ImportApplications() {
        try{
            String obResponse = WebClient.sendRequest(
                    poApi.getUrlGetObApplication(),
                    new JSONObject().toString(),
                    poHeaders.getHeaders());

            Log.d(TAG, obResponse);

            if (obResponse == null) {
                message = SERVER_NO_RESPONSE;
                return false;
            }

            JSONObject loResponse = new JSONObject(obResponse);
            String result = loResponse.getString("result");
            if (result.equalsIgnoreCase("error")) {
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;
            }

            JSONArray jsonA = loResponse.getJSONArray("payload");
            for (int x = 0; x < jsonA.length(); x++) {
                JSONObject loJson = jsonA.getJSONObject(x);
                EEmployeeBusinessTrip loDetail = poDao.GetEmployeeBusinessTrip(loJson.getString("sTransNox"));
                if(loDetail == null){
                    EEmployeeBusinessTrip loOB = new EEmployeeBusinessTrip();
                    loOB.setTransNox(loJson.getString("sTransNox"));
                    loOB.setTransact(loJson.getString("dTransact"));
                    loOB.setEmployee(loJson.getString("sEmployID"));
                    loOB.setFullName(loJson.getString("sCompnyNm"));
                    loOB.setBranchNm(loJson.getString("sBranchNm"));
                    loOB.setDeptName(loJson.getString("sDeptName"));
                    loOB.setDateFrom(loJson.getString("dDateFrom"));
                    loOB.setDateThru(loJson.getString("dDateThru"));
                    loOB.setDapprove("");
                    loOB.setRemarksx(loJson.getString("sRemarksx"));
                    loOB.setTranStat(loJson.getString("cTranStat"));
                    loOB.setTimeStmp(loJson.getString("dTimeStmp"));
                    poDao.insert(loOB);
                } else {
                    Date ldDate1 = SQLUtil.toDate(loDetail.getTimeStmp(), SQLUtil.FORMAT_TIMESTAMP);
                    Date ldDate2 = SQLUtil.toDate((String) loJson.get("dTimeStmp"), SQLUtil.FORMAT_TIMESTAMP);
                    if (!ldDate1.equals(ldDate2)){
                        loDetail.setTransNox(loJson.getString("sTransNox"));
                        loDetail.setTransact(loJson.getString("dTransact"));
                        loDetail.setEmployee(loJson.getString("sEmployID"));
                        loDetail.setFullName(loJson.getString("sCompnyNm"));
                        loDetail.setBranchNm(loJson.getString("sBranchNm"));
                        loDetail.setDeptName(loJson.getString("sDeptName"));
                        loDetail.setDateFrom(loJson.getString("dDateFrom"));
                        loDetail.setDateThru(loJson.getString("dDateThru"));
                        loDetail.setDapprove("");
                        loDetail.setRemarksx(loJson.getString("sRemarksx"));
                        loDetail.setTranStat(loJson.getString("cTranStat"));
                        loDetail.setTimeStmp(loJson.getString("dTimeStmp"));
                        poDao.update(loDetail);
                        Log.d(TAG, "Business trip record has been updated.");
                    }
                }
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    @Override
    public boolean DownloadApplication(String fsArgs) {
        try{
            if(fsArgs == null){
                message = "Please enter transaction no.";
                return false;
            }

            if(fsArgs.trim().isEmpty()){
                message = "Please enter transaction no.";
                return false;
            }

            JSONObject params = new JSONObject();
            params.put("sTransNox", fsArgs);

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlGetObApplication(),
                    new JSONObject().toString(),
                    poHeaders.getHeaders());
            if (lsResponse == null) {
                message = SERVER_NO_RESPONSE;
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String result = loResponse.getString("result");
            if (result.equalsIgnoreCase("success")) {
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;

            }

            JSONArray jsonA = loResponse.getJSONArray("payload");
            for (int x = 0; x < jsonA.length(); x++) {
                JSONObject loJson = jsonA.getJSONObject(x);
                EEmployeeBusinessTrip loDetail = poDao.GetEmployeeBusinessTrip(loJson.getString("sTransNox"));
                if(loDetail == null){
                    EEmployeeBusinessTrip loOB = new EEmployeeBusinessTrip();
                    loOB.setTransNox(loJson.getString("sTransNox"));
                    loOB.setTransact(loJson.getString("dTransact"));
                    loOB.setEmployee(loJson.getString("sEmployID"));
                    loOB.setFullName(loJson.getString("sCompnyNm"));
                    loOB.setBranchNm(loJson.getString("sBranchNm"));
                    loOB.setDeptName(loJson.getString("sDeptName"));
                    loOB.setDateFrom(loJson.getString("dDateFrom"));
                    loOB.setDateThru(loJson.getString("dDateThru"));
                    loOB.setDapprove("");
                    loOB.setRemarksx(loJson.getString("sRemarksx"));
                    loOB.setTranStat(loJson.getString("cTranStat"));
                    loOB.setTimeStmp(loJson.getString("dTimeStmp"));
                    poDao.insert(loOB);
                } else {
                    Date ldDate1 = SQLUtil.toDate(loDetail.getTimeStmp(), SQLUtil.FORMAT_TIMESTAMP);
                    Date ldDate2 = SQLUtil.toDate((String) loJson.get("dTimeStmp"), SQLUtil.FORMAT_TIMESTAMP);
                    if (!ldDate1.equals(ldDate2)){
                        loDetail.setTransNox(loJson.getString("sTransNox"));
                        loDetail.setTransact(loJson.getString("dTransact"));
                        loDetail.setEmployee(loJson.getString("sEmployID"));
                        loDetail.setFullName(loJson.getString("sCompnyNm"));
                        loDetail.setBranchNm(loJson.getString("sBranchNm"));
                        loDetail.setDeptName(loJson.getString("sDeptName"));
                        loDetail.setDateFrom(loJson.getString("dDateFrom"));
                        loDetail.setDateThru(loJson.getString("dDateThru"));
                        loDetail.setDapprove("");
                        loDetail.setRemarksx(loJson.getString("sRemarksx"));
                        loDetail.setTranStat(loJson.getString("cTranStat"));
                        loDetail.setTimeStmp(loJson.getString("dTimeStmp"));
                        poDao.update(loDetail);
                        Log.d(TAG, "Business trip record has been updated.");
                    }
                }
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    @Override
    public String SaveApproval(Object args) {
        try{
            OBApprovalInfo foVal = (OBApprovalInfo) args;

            EEmployeeBusinessTrip loDetail = poDao.GetEmployeeBusinessTrip(foVal.getTransNox());
            if(loDetail == null){
                message = "Cannot find business trip to approve.";
                return null;
            }

            loDetail.setTransNox(foVal.getTransNox());
            loDetail.setAppldFrx(foVal.getAppldFrx());
            loDetail.setAppldTox(foVal.getAppldTox());
            loDetail.setApproved(foVal.getApproved());
            loDetail.setDapprove(foVal.getDateAppv());
            loDetail.setTranStat(foVal.getTranStat());
            loDetail.setSendStat("1");

            poDao.update(loDetail);
            return loDetail.getTransNox();
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    @Override
    public boolean UploadApproval(Object fsVal) {
        try{
            OBApprovalInfo foVal = (OBApprovalInfo) fsVal;
            EEmployeeBusinessTrip loDetail = poDao.GetEmployeeBusinessTrip(foVal.getTransNox());
            if(loDetail == null){
                message = "No business trip application found.";
                return false;
            }

            JSONObject param = new JSONObject();
            param.put("sTransNox", foVal.getTransNox());
            param.put("dAppldFrx", foVal.getAppldFrx());
            param.put("dAppldTox", foVal.getAppldTox());
            param.put("sApproved", foVal.getApproved());
            param.put("dApproved", foVal.getDateAppv());
            param.put("cTranStat", foVal.getTranStat());

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlConfirmObApplication(),
                    param.toString(), poHeaders.getHeaders());
            if (lsResponse == null) {
                message = SERVER_NO_RESPONSE;
                return false;
            }

            Log.d(TAG,lsResponse);

            JSONObject loResponse = new JSONObject(lsResponse);
            String result = loResponse.getString("result");
            if (result.equalsIgnoreCase("success")) {
                if (foVal.getTranStat().equalsIgnoreCase("1")) {
                    message = "Business trip has been approve successfully.";
                } else {
                    message = "Business trip has been disapprove successfully.";
                }
                return true;
            } else {
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    @Override
    public boolean UploadApplications() {
        try{
            List<EEmployeeBusinessTrip> loObs = poDao.GetUnpostedOBApplications();
            if(loObs == null){
                message = "No business trip record found.";
                return false;
            }

            if(loObs.size() == 0){
                message = "No business trip record found.";
                return false;
            }

            for(int x = 0; x < loObs.size(); x++){
                EEmployeeBusinessTrip loDetail = loObs.get(x);

                JSONObject loJson = new JSONObject();
                loJson.put("sTransNox", loDetail.getTransNox());
                loJson.put("dTransact", loDetail.getTransact());
                loJson.put("sEmployID", loDetail.getEmployee());
                loJson.put("dDateFrom", loDetail.getDateFrom());
                loJson.put("dDateThru", loDetail.getDateThru());
                loJson.put("sDestinat", loDetail.getDestinat());
                loJson.put("sRemarksx", loDetail.getRemarksx());
                loJson.put("sApproved", "");
                loJson.put("dApproved", "");
                loJson.put("dAppldFrx", "");
                loJson.put("dAppldTox", "");
                loJson.put("cTranStat", "0");
                loJson.put("sModified", "");
                loJson.put("dModified", "");

                String lsResponse = WebClient.sendRequest(
                        poApi.getUrlSendObApplication(),
                        loJson.toString(),
                        poHeaders.getHeaders());

                if(lsResponse == null){
                    message = SERVER_NO_RESPONSE;
                    Log.e(TAG, message);
                    Thread.sleep(1000);
                    continue;
                }
                JSONObject loResponse = new JSONObject(lsResponse);
                String lsResult = loResponse.getString("result");
                if(lsResult.equalsIgnoreCase("success")){
                    poDao.updateOBSentStatus(loJson.getString("sTransNox"),
                            loResponse.getString("sTransNox"));
                    Thread.sleep(1000);
                } else {
                    JSONObject loError = loResponse.getJSONObject("error");
                    message = getErrorMessage(loError);
                    Log.e(TAG, message);
                    Thread.sleep(1000);
                }
            }
            return true;
        } catch(Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    @Override
    public String getMessage() {
        return message;
    }

    public LiveData<List<EEmployeeBusinessTrip>> GetOBApplicationList() {
        return poDao.getOBList();
    }

    public LiveData<List<EEmployeeBusinessTrip>> GetOBApplicationsForApproval() {
        return poDao.getOBListForApproval();
    }

    public LiveData<EEmployeeBusinessTrip> GetOBApplicationInfo(String args) {
        return poDao.getBusinessTripInfo(args);
    }

    public LiveData<List<EEmployeeBusinessTrip>> GetApproveOBApplications() {
        return poDao.GetApproveBusTrip();
    }

    @Override
    public String SaveApplication(Object args) {
        try{
            OBApplication foVal = (OBApplication) args;
            if(!foVal.isDataValid()){
                message = foVal.getMessage();
                return null;
            }

            EEmployeeInfo loUser = poDao.GetEmployeeInfo();
            if(loUser == null){
                message = "Invalid user info. Please re-login your account and try again.";
                return null;
            }

            EEmployeeBusinessTrip loDetail = poDao.CheckIfApplicationExist(
                    foVal.getDateFrom(),
                    foVal.getDateThru(),
                    foVal.getDestinat(),
                    foVal.getRemarksx());

            if(loDetail != null){
                return loDetail.getTransNox();
            }

            EEmployeeBusinessTrip detail = new EEmployeeBusinessTrip();
            String lsTransNo = CreateUniqueID();
            detail.setTransNox(lsTransNo);
            detail.setFullName(loUser.getUserName());
            detail.setDeptName(DeptCode.getDepartmentName(loUser.getDeptIDxx()));
            detail.setTransact(AppConstants.CURRENT_DATE());
            detail.setEmployee(loUser.getEmployID());
            detail.setBranchNm(foVal.getDestinat());
            detail.setDateFrom(foVal.getDateFrom());
            detail.setDateThru(foVal.getDateThru());
            detail.setRemarksx(foVal.getRemarksx());
            detail.setDestinat(foVal.getDestinat());
            detail.setAppldFrx(null);
            detail.setAppldTox(null);
            detail.setTranStat("0");
            detail.setSendStat("0");
            detail.setTimeStmp(AppConstants.DATE_MODIFIED());
            poDao.insert(detail);
            Log.d(TAG, "Business trip application save locally.");
            Log.d(TAG, "Business trip transaction no. :" + lsTransNo);
            return lsTransNo;
        }catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    @Override
    public boolean UploadApplication(String args) {
        try{
            EEmployeeBusinessTrip loDetail = poDao.GetEmployeeBusinessTrip(args);
            if(loDetail == null){
                message = "Unable to retrieve business trip application to upload.";
                Log.e(TAG, message);
                return false;
            }

            JSONObject loJson = new JSONObject();
            loJson.put("sTransNox", loDetail.getTransNox());
            loJson.put("dTransact", loDetail.getTransact());
            loJson.put("sEmployID", loDetail.getEmployee());
            loJson.put("dDateFrom", loDetail.getDateFrom());
            loJson.put("dDateThru", loDetail.getDateThru());
            loJson.put("sDestinat", loDetail.getDestinat());
            loJson.put("sRemarksx", loDetail.getRemarksx());
            loJson.put("sApproved", "");
            loJson.put("dApproved", "");
            loJson.put("dAppldFrx", "");
            loJson.put("dAppldTox", "");
            loJson.put("cTranStat", "0");
            loJson.put("sModified", "");
            loJson.put("dModified", "");

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlSendObApplication(),
                    loJson.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = "Server no response";
                return false;
            }
            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("success")){
                poDao.updateOBSentStatus(loJson.getString("sTransNox"),
                        loResponse.getString("sTransNox"));
                return true;
            } else {
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                Log.e(TAG, message);
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    private String CreateUniqueID(){
        String lsUniqIDx = "";
        try{
            String lsBranchCd = "MX01";
            String lsCrrYear = new SimpleDateFormat("yy", Locale.getDefault()).format(new Date());
            StringBuilder loBuilder = new StringBuilder(lsBranchCd);
            loBuilder.append(lsCrrYear);

            int lnLocalID = poDao.GetRowsCountForID() + 1;
            String lsPadNumx = String.format("%05d", lnLocalID);
            loBuilder.append(lsPadNumx);
            lsUniqIDx = loBuilder.toString();
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        Log.d(TAG, lsUniqIDx);
        return lsUniqIDx;
    }

}
