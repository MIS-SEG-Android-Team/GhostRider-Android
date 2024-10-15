/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 6/19/21 10:51 AM
 * project file last modified : 6/19/21 10:51 AM
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
import org.rmj.g3appdriver.GCircle.Apps.PetManager.pojo.LeaveApplication;
import org.rmj.g3appdriver.GCircle.Apps.PetManager.pojo.LeaveApprovalInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeLeave;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeLeave;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.dev.Api.WebClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmployeeLeave extends PetMngr {
    private static final String TAG = EmployeeLeave.class.getSimpleName();
    private final DEmployeeLeave poDao;

    public EmployeeLeave(Application instance) {
        super(instance);
        this.poDao = GGC_GCircleDB.getInstance(instance).employeeLeaveDao();
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> GetUserInfo(){
        return poUser.GetEmployeeBranch();
    }

    @Override
    public boolean ImportApplications() {
        try{
            String lsAddress = poApi.getUrlGetLeaveApplication();
            String lsHeaders = poHeaders.getHeaders().toString();

            String lsResponse = WebClient.sendRequest(
                    lsAddress,
                    new JSONObject().toString(),
                    poHeaders.getHeaders());

            Log.d(TAG, lsResponse);

            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");

            if (lsResult.equalsIgnoreCase("error")) {
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;
            }

            JSONArray jsonA = loResponse.getJSONArray("payload");
            for (int x = 0; x < jsonA.length(); x++) {
                JSONObject loJson = jsonA.getJSONObject(x);
                EEmployeeLeave loDetail = poDao.GetEmployeeLeave(loJson.getString("sTransNox"));
                if(loDetail == null){
                    EEmployeeLeave leave = new EEmployeeLeave();
                    leave.setTransNox(loJson.getString("sTransNox"));
                    leave.setTransact(loJson.getString("dTransact"));
                    leave.setEmployID(loJson.getString("xEmployee"));
                    leave.setEntryByx(loJson.getString("sEmployID"));
                    leave.setBranchNm(loJson.getString("sBranchNm"));
                    leave.setDeptName(loJson.getString("sDeptName"));
                    leave.setPositnNm(loJson.getString("sPositnNm"));
                    leave.setDateFrom(loJson.getString("dAppldFrx"));
                    leave.setDateThru(loJson.getString("dAppldTox"));
                    leave.setNoDaysxx(loJson.getInt("nNoDaysxx"));
                    leave.setPurposex(loJson.getString("sPurposex"));
                    leave.setLeaveTyp(loJson.getString("cLeaveTyp"));
                    leave.setLveCredt(loJson.getInt("nLveCredt"));
                    leave.setSentStat("1");
                    leave.setTranStat(loJson.getString("cTranStat"));
                    leave.setTimeStmp(loJson.getString("dTimeStmp"));
                    if(!leave.getTranStat().equalsIgnoreCase("0")){
                        leave.setAppvSent("1");
                    } else {
                        leave.setAppvSent("0");
                    }
                    poDao.insertApplication(leave);
                    Log.d(TAG, "New leave application save!");
                } else {
                    Date ldDate1 = SQLUtil.toDate(loDetail.getTimeStmp(), SQLUtil.FORMAT_TIMESTAMP);
                    Date ldDate2 = SQLUtil.toDate((String) loJson.get("dTimeStmp"), SQLUtil.FORMAT_TIMESTAMP);
                    if (!ldDate1.equals(ldDate2)){
                        loDetail.setTransNox(loJson.getString("sTransNox"));
                        loDetail.setTransact(loJson.getString("dTransact"));
                        loDetail.setEmployID(loJson.getString("xEmployee"));
                        loDetail.setEntryByx(loJson.getString("sEmployID"));
                        loDetail.setBranchNm(loJson.getString("sBranchNm"));
                        loDetail.setDeptName(loJson.getString("sDeptName"));
                        loDetail.setPositnNm(loJson.getString("sPositnNm"));
                        loDetail.setAppldFrx(loJson.getString("dAppldFrx"));
                        loDetail.setAppldTox(loJson.getString("dAppldTox"));
                        loDetail.setNoDaysxx(loJson.getInt("nNoDaysxx"));
                        loDetail.setPurposex(loJson.getString("sPurposex"));
                        loDetail.setLeaveTyp(loJson.getString("cLeaveTyp"));
                        loDetail.setLveCredt(loJson.getInt("nLveCredt"));
                        loDetail.setTranStat(loJson.getString("cTranStat"));
                        loDetail.setSentStat("1");
                        loDetail.setTranStat(loJson.getString("cTranStat"));
                        loDetail.setTimeStmp(loJson.getString("dTimeStmp"));
                        if(!loDetail.getTranStat().equalsIgnoreCase("0")){
                            loDetail.setAppvSent("1");
                        } else {
                            loDetail.setAppvSent("0");
                        }
                        loDetail.setTimeStmp(loJson.getString("dTimeStmp"));
                        poDao.updateApplication(loDetail);
                        Log.d(TAG, "Leave application record has been updated.");
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
    public boolean DownloadApplication(String args) {
        try{
            if(args == null){
                message = "Please enter transaction no.";
                return false;
            }

            if(args.trim().isEmpty()){
                message = "Please enter transaction no.";
                return false;
            }

            JSONObject params = new JSONObject();
            params.put("sTransNox", args);
            String lsAddress = poApi.getUrlGetLeaveApplication();
            Log.d(TAG, "Connecting to " + lsAddress + "...");
            String lsResponse = WebClient.sendRequest(
                    lsAddress,
                    params.toString(),
                    poHeaders.getHeaders());
            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if (lsResult.equalsIgnoreCase("error")) {
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;

            }

            JSONArray jsonA = loResponse.getJSONArray("payload");
            for (int x = 0; x < jsonA.length(); x++) {
                JSONObject loJson = jsonA.getJSONObject(x);
                EEmployeeLeave loDetail = poDao.GetEmployeeLeave(loJson.getString("sTransNox"));
                if(loDetail == null){
                    EEmployeeLeave leave = new EEmployeeLeave();
                    leave.setTransNox(loJson.getString("sTransNox"));
                    leave.setTransact(loJson.getString("dTransact"));
                    leave.setEmployID(loJson.getString("xEmployee"));
                    leave.setEntryByx(loJson.getString("sEmployID"));
                    leave.setBranchNm(loJson.getString("sBranchNm"));
                    leave.setDeptName(loJson.getString("sDeptName"));
                    leave.setPositnNm(loJson.getString("sPositnNm"));
                    leave.setAppldFrx(loJson.getString("dAppldFrx"));
                    leave.setAppldTox(loJson.getString("dAppldTox"));
                    leave.setNoDaysxx(loJson.getInt("nNoDaysxx"));
                    leave.setPurposex(loJson.getString("sPurposex"));
                    leave.setLeaveTyp(loJson.getString("cLeaveTyp"));
                    leave.setLveCredt(loJson.getInt("nLveCredt"));
                    leave.setTranStat(loJson.getString("cTranStat"));
                    leave.setTimeStmp(loJson.getString("dTimeStmp"));
                    poDao.insertApplication(leave);
                    Log.d(TAG, "New leave application save!");
                } else {
                    Date ldDate1 = SQLUtil.toDate(loDetail.getTimeStmp(), SQLUtil.FORMAT_TIMESTAMP);
                    Date ldDate2 = SQLUtil.toDate((String) loJson.get("dTimeStmp"), SQLUtil.FORMAT_TIMESTAMP);
                    if (!ldDate1.equals(ldDate2)){
                        loDetail.setTransNox(loJson.getString("sTransNox"));
                        loDetail.setTransact(loJson.getString("dTransact"));
                        loDetail.setEmployID(loJson.getString("xEmployee"));
                        loDetail.setEntryByx(loJson.getString("sEmployID"));
                        loDetail.setBranchNm(loJson.getString("sBranchNm"));
                        loDetail.setDeptName(loJson.getString("sDeptName"));
                        loDetail.setPositnNm(loJson.getString("sPositnNm"));
                        loDetail.setAppldFrx(loJson.getString("dAppldFrx"));
                        loDetail.setAppldTox(loJson.getString("dAppldTox"));
                        loDetail.setNoDaysxx(loJson.getInt("nNoDaysxx"));
                        loDetail.setPurposex(loJson.getString("sPurposex"));
                        loDetail.setLeaveTyp(loJson.getString("cLeaveTyp"));
                        loDetail.setLveCredt(loJson.getInt("nLveCredt"));
                        loDetail.setTranStat(loJson.getString("cTranStat"));
                        loDetail.setTimeStmp(loJson.getString("dTimeStmp"));
                        poDao.updateApplication(loDetail);
                        Log.d(TAG, "Leave application record has been updated.");
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
    public String SaveApplication(Object foArgs) {
        try{
            LeaveApplication foVal = (LeaveApplication) foArgs;

            if(!foVal.isDataValid()){
                message = foVal.getMessage();
                return null;
            }

            EEmployeeInfo loUser = poDao.GetEmployeeInfo();

            if(loUser == null){
                message = "Invalid user record. Please re-login your account and try again.";
                return null;
            }

            EEmployeeLeave loDetail = poDao.CheckIfApplicationExist(
                    loUser.getEmployID(),
                    foVal.getRemarksxx(),
                    foVal.getLeaveType(),
                    foVal.getDateFromx(),
                    foVal.getDateThrux());
            if(loDetail == null) {

                String lsTransNo = CreateUniqueID();
                EEmployeeLeave loApp = new EEmployeeLeave();
                loApp.setTransNox(lsTransNo);
                loApp.setEmployID(loUser.getEmployID());
                loApp.setEntryByx(loUser.getEmployID());
                loApp.setTransact(AppConstants.CURRENT_DATE());
                loApp.setEmployID(foVal.getEmploName());
                loApp.setBranchNm(foVal.getBranchNme());
                loApp.setDateFrom(foVal.getDateFromx());
                loApp.setDateThru(foVal.getDateThrux());
                loApp.setAppldFrx(foVal.getDateFromx());
                loApp.setAppldTox(foVal.getDateThrux());
                loApp.setNoDaysxx(foVal.getNoOfDaysx());
                loApp.setPurposex(foVal.getRemarksxx());
                loApp.setEqualHrs(foVal.getNoOfHours());
                loApp.setLeaveTyp(foVal.getLeaveType());
                loApp.setEntryDte(AppConstants.CURRENT_DATE());
                loApp.setWithOPay(0);
                loApp.setApproved("0");
                loApp.setTranStat("0");
                poDao.insertApplication(loApp);
                Log.d(TAG, "Leave application has been save to local.");
                message = "Leave application has been save to local";
                return lsTransNo;
            }

            return loDetail.getTransNox();
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    @Override
    public boolean UploadApplication(String args) {
        try{
            EEmployeeLeave loDetail = poDao.GetEmployeeLeave(args);
            if(loDetail == null){
                message = "Unable to retrieve leave application to upload.";
                return false;
            }

            JSONObject param = new JSONObject();
            param.put("sTransNox", loDetail.getTransNox());
            param.put("dTransact", loDetail.getTransact());
            param.put("sEmployID", poSession.getEmployeeID());
            param.put("dDateFrom", loDetail.getDateFrom());
            param.put("dDateThru", loDetail.getDateThru());
            param.put("nNoDaysxx", loDetail.getNoDaysxx());
            param.put("sPurposex", loDetail.getPurposex());
            param.put("cLeaveTyp", loDetail.getLeaveTyp());
            param.put("dAppldFrx", loDetail.getDateFrom());
            param.put("dAppldTox", loDetail.getDateThru());
            param.put("sEntryByx", loDetail.getEntryByx());
            param.put("dEntryDte", loDetail.getEntryDte());
            param.put("nWithOPay", loDetail.getWithOPay());
            param.put("nEqualHrs", loDetail.getEqualHrs());
            param.put("sApproved", loDetail.getApproved());
            param.put("dApproved", "");
            param.put("dSendDate", loDetail.getSendDate());
            param.put("cTranStat", loDetail.getTranStat());
            param.put("sModified", poSession.getEmployeeID());

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlSendLeaveApplication(),
                    param.toString(),
                    poHeaders.getHeaders());

            if (lsResponse == null) {
                message = SERVER_NO_RESPONSE;
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if (lsResult.equalsIgnoreCase("error")) {
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                Log.e(TAG, "Failed to save leave application to server. Message: " + message);
                message = message;
                return false;
            }

            poDao.updateSendStatus(
                    AppConstants.DATE_MODIFIED(),
                    args,
                    loResponse.getString("sTransNox"));
            Log.d("Employee Leave", "Leave info updated!");
            message = "Leave application has been save to server.";
            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    @Override
    public String SaveApproval(Object foArgs) {
        try{
            LeaveApprovalInfo foVal = (LeaveApprovalInfo) foArgs;
            EEmployeeLeave loDetail = poDao.GetEmployeeLeave(foVal.getTransNox());
            if(loDetail == null){
                message = "Cannot find business trip to approve.";
                return null;
            }
            loDetail.setTransNox(foVal.getTransNox());
            loDetail.setTransact(AppConstants.CURRENT_DATE());
            loDetail.setAppldFrx(foVal.getAppldFrx());
            loDetail.setAppldTox(foVal.getAppldTox());
            loDetail.setTranStat(foVal.getTranStat());
            loDetail.setWithPayx(foVal.getWithPayx());
            loDetail.setWithOPay(foVal.getWithOPay());
            loDetail.setApproved(foVal.getApprovex());
            loDetail.setDApproved(foVal.getApproved());
            loDetail.setSentStat("1");
            poDao.updateApplication(loDetail);
            return loDetail.getTransNox();
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    @Override
    public boolean UploadApproval(Object args) {
        try{
            LeaveApprovalInfo foVal = (LeaveApprovalInfo) args;
            EEmployeeLeave loDetail = poDao.GetEmployeeLeave(foVal.getTransNox());
            if(loDetail == null){
                message = "Unable to find leave application to upload.";
                return false;
            }

            JSONObject params = new JSONObject();
            params.put("sTransNox", foVal.getTransNox());
            params.put("dTransact", loDetail.getTransact());
            params.put("dAppldFrx", foVal.getAppldFrx());
            params.put("dAppldTox", foVal.getAppldTox());
            params.put("cTranStat", foVal.getTranStat());
            params.put("nWithPayx", foVal.getWithPayx());
            params.put("nWithOPay", foVal.getWithOPay());
            params.put("sApproved", foVal.getApproved());
            params.put("dApproved", foVal.getApproved());

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlConfirmLeaveApplication(),
                    params.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null) {
                message = SERVER_NO_RESPONSE;
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                Log.e(TAG, message);
                return false;
            }

            message = "Leave approval has been posted.";
            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    @Override
    public boolean UploadApplications() {
        try{
            List<EEmployeeLeave> loDetail = poDao.getUnsentEmployeeLeave();

            if(loDetail == null){
                message = "No leave application found.";
                return false;
            }

            if(loDetail.size() == 0){
                message = "No leave application found.";
                return false;
            }

            for(int x = 0; x < loDetail.size(); x++){
                EEmployeeLeave leave = loDetail.get(x);
                JSONObject param = new JSONObject();
                param.put("sTransNox", leave.getTransNox());
                param.put("dTransact", leave.getTransact());
                param.put("sEmployID", poSession.getEmployeeID());
                param.put("dDateFrom", leave.getDateFrom());
                param.put("dDateThru", leave.getDateThru());
                param.put("nNoDaysxx", leave.getNoDaysxx());
                param.put("sPurposex", leave.getPurposex());
                param.put("cLeaveTyp", leave.getLeaveTyp());
                param.put("dAppldFrx", leave.getDateFrom());
                param.put("dAppldTox", leave.getDateThru());
                param.put("sEntryByx", leave.getEmployID());
                param.put("dEntryDte", leave.getEntryDte());
                param.put("nWithOPay", leave.getWithOPay());
                param.put("nEqualHrs", leave.getEqualHrs());
                param.put("sApproved", leave.getApproved());
                param.put("dApproved", leave.getApproved());
                param.put("dSendDate", AppConstants.CURRENT_DATE());
                param.put("cTranStat", leave.getTranStat());
                param.put("sModified", poSession.getEmployeeID());

                Log.d(TAG, "Uploading leave application no.: " + x + " out of " + loDetail.size());
                String lsResponse = WebClient.sendRequest(
                        poApi.getUrlSendLeaveApplication(),
                        param.toString(),
                        poHeaders.getHeaders());
                if(lsResponse == null){
                    Log.e(TAG, "Sending employee leave. server no response");

                    Thread.sleep(1000);
                    continue;
                }

                JSONObject loResponse = new JSONObject(lsResponse);
                String result = loResponse.getString("result");
                if(result.equalsIgnoreCase("error")){
                    JSONObject loError = loResponse.getJSONObject("error");
                    Log.e(TAG, getErrorMessage(loError));
                    Thread.sleep(1000);
                    continue;
                }

                poDao.updateSendStatus(
                        AppConstants.DATE_MODIFIED(),
                        leave.getTransNox(),
                        loResponse.getString("sTransNox"));
                Log.d("Employee Leave", "Leave info updated!");

                Thread.sleep(1000);
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public LiveData<List<EEmployeeLeave>> GetLeaveApplicationList() {
        return poDao.getEmployeeLeaveList();
    }


    public LiveData<List<EEmployeeLeave>> GetLeaveApplicationsForApproval() {
        return poDao.getEmployeeLeaveForApprovalList();
    }


    public LiveData<List<EEmployeeLeave>> GetApproveLeaveApplications() {
        return poDao.getApproveLeaveList();
    }

    public LiveData<EEmployeeLeave> GetLeaveApplicationInfo(String args) {
        return poDao.getEmployeeLeaveInfo(args);
    }

    @Override
    public String getMessage() {
        return message;
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
