/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.g3appdriver.GCircle.Account;

import static org.rmj.g3appdriver.dev.Api.ApiResult.SERVER_NO_RESPONSE;
import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;
import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.apprdiver.util.SQLUtil;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBarcode;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBarcodeDetail;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeBusinessTrip;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeLeave;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DGuides;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeRole;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeRole;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Device.Telephony;
import org.rmj.g3appdriver.etc.AppConfigPreference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EmployeeMaster {
    private static final String TAG = EmployeeMaster.class.getSimpleName();

    private final Application instance;

    private final DEmployeeInfo poDao;
    private final DEmployeeRole roleDao;
    private final DGuides guidesDao;
    private final DBarcode barcodeDao;
    private final DBarcodeDetail barcodeDetailDao;

    private DEmployeeLeave leaveDao;
    private DEmployeeBusinessTrip btripDao;

    private final LiveData<EEmployeeInfo> employeeInfo;
    private final EmployeeSession poSession;
    private final GCircleApi webApi;
    private final HttpHeaders headers;
    private final AppConfigPreference poConfig;
    private final Telephony poDevID;
    
    private String message;

    public EmployeeMaster(Application application){
        this.instance = application;
        this.poDao = GGC_GCircleDB.getInstance(instance).EmployeeDao();
        this.leaveDao = GGC_GCircleDB.getInstance(instance).employeeLeaveDao();
        this.btripDao = GGC_GCircleDB.getInstance(instance).employeeOBDao();
        this.roleDao = GGC_GCircleDB.getInstance(instance).employeeRoleDao();
        this.guidesDao = GGC_GCircleDB.getInstance(instance).userguideDao();
        this.barcodeDao = GGC_GCircleDB.getInstance(instance).barcodeDao();
        this.barcodeDetailDao = GGC_GCircleDB.getInstance(instance).barcodeDetailDao();
        this.employeeInfo = poDao.getEmployeeInfo();
        this.poSession = EmployeeSession.getInstance(instance);
        this.poConfig = AppConfigPreference.getInstance(instance);
        this.webApi = new GCircleApi(application);
        this.headers = HttpHeaders.getInstance(instance);
        this.poDevID = new Telephony(instance);
    }
    
    public String getMessage(){
        return message;
    }

    public LiveData<EEmployeeInfo> getUserInfo(){
        return employeeInfo;
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> GetEmployeeBranch(){
        return poDao.GetEmployeeBranch();
    }

    public EEmployeeInfo getUserNonLiveData() { return poDao.getEmployeeInfoNonLiveData(); }

    public String getUserAreaCode(){
        return poDao.getUserAreaCode();
    }

    public LiveData<EEmployeeInfo> GetEmployeeInfo(){
        return employeeInfo;
    }

    public LiveData<String> getUserID(){
        return poDao.getUserID();
    }

    public LiveData<String> getClientID(){
        return poDao.getClientID();
    }

    public LiveData<String> getEmployID(){
        return poDao.getEmployID();
    }

    public String getEmployeeID(){
        return poDao.getEmployeeID();
    }

    public LiveData<List<EEmployeeRole>> getEmployeeRoles(){
        return roleDao.getEmployeeRoles();
    }

    public LiveData<List<EEmployeeRole>> getChildRoles(){
        return roleDao.getChildRoles();
    }

    public void LogoutUserSession(){
        poDao.LogoutUser();
        poDao.ClearAuthorizeFeatures();

        leaveDao.deleteApplication();
        btripDao.delete();

        guidesDao.clear();
        barcodeDao.clearBarcode();
        barcodeDetailDao.clear();

        poSession.initUserLogout();
    }
    
    public boolean AuthenticateUser(UserAuthInfo foVal){
        try{
            JSONObject params = new JSONObject();
            params.put("user", foVal.getEmail());
            params.put("pswd", foVal.getPassword());

            String lsResponse = WebClient.sendRequest(
                    webApi.getUrlAuthEmployee(),
                    params.toString(),
                    headers.getHeaders());
            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                return false;
            } else {
                Log.d(TAG, lsResponse);
                JSONObject loResponse = new JSONObject(lsResponse);
                String lsResult = loResponse.getString("result");
                if (lsResult.equalsIgnoreCase("success")) {
                    EEmployeeInfo employeeInfo = new EEmployeeInfo();
                    employeeInfo.setClientID(loResponse.getString("sClientID"));
                    employeeInfo.setBranchCD(loResponse.getString("sBranchCD"));
                    employeeInfo.setBranchNm(loResponse.getString("sBranchNm"));
                    employeeInfo.setLogNoxxx(loResponse.getString("sLogNoxxx"));
                    employeeInfo.setUserIDxx(loResponse.getString("sUserIDxx"));
                    employeeInfo.setEmailAdd(loResponse.getString("sEmailAdd"));
                    employeeInfo.setUserName(loResponse.getString("sUserName"));
                    employeeInfo.setUserLevl(loResponse.getString("nUserLevl"));
                    employeeInfo.setSlfieLog(loResponse.getString("cSlfieLog"));
                    employeeInfo.setDeptIDxx(loResponse.getString("sDeptIDxx"));
                    employeeInfo.setPositnID(loResponse.getString("sPositnID"));
                    employeeInfo.setEmpLevID(loResponse.getInt("sEmpLevID"));
                    employeeInfo.setAllowUpd(loResponse.getString("cAllowUpd"));
                    employeeInfo.setEmployID(loResponse.getString("sEmployID"));
                    employeeInfo.setDeviceID(poDevID.getDeviceID());
                    employeeInfo.setModelIDx(Build.MODEL);
                    employeeInfo.setMobileNo(poConfig.getMobileNo());
                    employeeInfo.setLoginxxx(AppConstants.DATE_MODIFIED());
                    employeeInfo.setSessionx(AppConstants.CURRENT_DATE());
                    poDao.SaveNewEmployeeSession(employeeInfo);

                    String lsClientx = loResponse.getString("sClientID");
                    String lsUserIDx = loResponse.getString("sUserIDxx");
                    String lsUserNme = loResponse.getString("sUserName");
                    String lsLogNoxx = loResponse.getString("sLogNoxxx");
                    String lsBranchx = loResponse.getString("sBranchCD");
                    String lsBranchN = loResponse.getString("sBranchNm");
                    String lsDeptIDx = loResponse.getString("sDeptIDxx");
                    String lsEmpIDxx = loResponse.getString("sEmployID");
                    String lsPostIDx = loResponse.getString("sPositnID");
                    String lsEmpLvlx = loResponse.getString("sEmpLevID");
                    poSession.initUserSession(lsUserIDx, lsUserNme, lsClientx, lsLogNoxx, lsBranchx, lsBranchN, lsDeptIDx, lsEmpIDxx, lsPostIDx, lsEmpLvlx, "1");
                    message = "Login success";
                    return true;
                } else {
                    JSONObject loError = loResponse.getJSONObject("error");
                    String lsMessage = getErrorMessage(loError);;
                    Log.e(TAG, lsMessage);
                    message = getErrorMessage(loError);
                    return false;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean GetUserAuthorizeAccess(){
        try{
            JSONObject params = new JSONObject();
            String lsResponse = WebClient.sendRequest(
                    webApi.getRequestUserAccess(),
                    params.toString(),
                    headers.getHeaders());
            if (lsResponse == null) {
                message = SERVER_NO_RESPONSE;
                return false;
            } else {
                JSONObject loResponse = new JSONObject(lsResponse);
                if (loResponse.getString("result").equalsIgnoreCase("success")) {
                    JSONArray loArr = loResponse.getJSONArray("payload");
                    for(int x = 0; x < loArr.length(); x++){
                        JSONObject loJson = loArr.getJSONObject(x);
                        EEmployeeRole loRole = roleDao.GetEmployeeRole(loJson.getString("sObjectNm"));
                        if(loRole == null){
                            EEmployeeRole loDetail = new EEmployeeRole();
                            loDetail.setObjectNm(loJson.getString("sObjectNm"));
                            loDetail.setObjectDs(loJson.getString("sObjectDs"));
                            loDetail.setObjectTP(loJson.getString("cObjectTP"));
                            loDetail.setParentxx(loJson.getString("sParentxx"));
                            loDetail.setHasChild(loJson.getString("cHasChild"));
                            loDetail.setSeqnceCd(loJson.getString("sSeqnceCD"));
                            loDetail.setRecdStat(loJson.getString("cRecdStat"));
                            loDetail.setTimeStmp(loJson.getString("dTimeStmp"));
                            roleDao.InsertEmployeeRole(loDetail);
                            Log.d(TAG, "Authorize feature has been save.");
                        } else {
                            Date ldDate1 = SQLUtil.toDate(loRole.getTimeStmp(), SQLUtil.FORMAT_TIMESTAMP);
                            Date ldDate2 = SQLUtil.toDate((String) loJson.get("dTimeStmp"), SQLUtil.FORMAT_TIMESTAMP);

                            if (!ldDate1.equals(ldDate2)){
                                //create update statement

                                loRole.setObjectNm(loJson.getString("sObjectNm"));
                                loRole.setObjectTP(loJson.getString("cObjectTP"));
                                loRole.setObjectDs(loJson.getString("sObjectDs"));
                                loRole.setParentxx(loJson.getString("sParentxx"));
                                loRole.setHasChild(loJson.getString("cHasChild"));
                                loRole.setSeqnceCd(loJson.getString("sSeqnceCD"));
                                loRole.setRecdStat(loJson.getString("cRecdStat"));
                                loRole.setTimeStmp(loJson.getString("dTimeStmp"));
                                roleDao.UpdateRole(loRole);
                                Log.d(TAG, "Authorize feature has been updated.");
                            }
                        }
                    }
                    message = "Authorize features for user has been imported successfully.";
                    return true;
                } else {
                    JSONObject loError = loResponse.getJSONObject("error");
                    String lsMessage = getErrorMessage(loError);;
                    Log.e(TAG, lsMessage);
                    message = lsMessage;
                    return false;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean IsSessionValid(){
        try{
            String lsLoginxx = poDao.getSessionTime();
            Date loLogin = new SimpleDateFormat("yyyy-MM-dd").parse(lsLoginxx);
            Date loCrrnt = new SimpleDateFormat("yyyy-MM-dd").parse(AppConstants.CURRENT_DATE());
            int lnResult = loLogin.compareTo(loCrrnt);

            if(lnResult != 0){
                return false;
            }

            int lnSession = poDao.getLoginDate();

            if(lnSession > 0){
                poDao.LogoutUser();
                poDao.ClearAuthorizeFeatures();
                poSession.initUserLogout();
                return false;
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public static class UserAuthInfo {
        private final String Email;
        private final String Password;
        private final String MobileNo;
        private String message;

        public UserAuthInfo(String email, String password, String mobileNo) {
            Email = email;
            Password = password;
            MobileNo = mobileNo;
        }

        public String getEmail() {
            return Email;
        }

        public String getPassword() {
            return Password;
        }

        public String getMobileNo() {
            return MobileNo;
        }

        public String getMessage(){
            return message;
        }

        public boolean isAuthInfoValid(){
            if(!isEmailValid()){
                return false;
            }
            if(!isPasswordValid()){
                return false;
            }
            return isMobileNoValid();
        }

        private boolean isEmailValid(){
            if(Email.isEmpty()){
                message = "Please enter your email";
                return false;
            }
            return true;
        }

        private boolean isPasswordValid(){
            if(Password.isEmpty()) {
                message = "Please enter password";
                return false;
            }
            return true;
        }

        private boolean isMobileNoValid(){
            if(MobileNo.isEmpty()){
                message = "Please enter mobile no.";
                return false;
            }
            if(MobileNo.length()!=11){
                message = "Mobile number must be 11 characters";
                return false;
            }
            if(!MobileNo.substring(0, 2).equalsIgnoreCase("09")){
                message = "Mobile number must start with '09'";
                return false;
            }
            return true;
        }
    }
}
