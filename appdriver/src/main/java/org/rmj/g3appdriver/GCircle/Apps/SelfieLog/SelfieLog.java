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

package org.rmj.g3appdriver.GCircle.Apps.SelfieLog;

import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;
import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;
import static org.rmj.g3appdriver.lib.Firebase.CrashReportingUtil.reportException;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DSelfieLog;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ESelfieLog;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.GCircle.room.Repositories.RImageInfo;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.GCircle.Etc.DeptCode;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.lib.Firebase.CrashReportingUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SelfieLog {
    private static final String TAG = SelfieLog.class.getSimpleName();

    private final DSelfieLog poDao;

    private final EmployeeMaster poUser;

    private final RImageInfo poImage;
    private final GCircleApi poApi;
    private final HttpHeaders poHeaders;
    private final EmployeeSession poSession;
    private String message;

    public SelfieLog(Application instance){
        this.poDao = GGC_GCircleDB.getInstance(instance).SelfieDao();
        this.poUser = new EmployeeMaster(instance);
        this.poImage = new RImageInfo(instance);
        this.poApi = new GCircleApi(instance);
        this.poHeaders = HttpHeaders.getInstance(instance);
        this.poSession = EmployeeSession.getInstance(instance);
    }

    public String getMessage() {
        return message;
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> GetUserInfo(){
        return poUser.GetEmployeeBranch();
    }

    public LiveData<List<ESelfieLog>> GetAllEmployeeTimeLog(String fsVal){
        return poDao.getAllEmployeeTimeLog(fsVal);
    }

    public LiveData<List<DSelfieLog.LogTime>> GetAllTimeLog(String args){
        return poDao.GetAllEmployeeTimeLog(args);
    }

    public boolean ValidateExistingBranch(String args){
        try{
            if(poSession.getEmployeeLevel().equalsIgnoreCase(String.valueOf(DeptCode.LEVEL_AREA_MANAGER))){
                message = "User is not Area Manager. Proceed Selfie Log without remarks";
                return true;
            }

            int lnExist = poDao.checkBranchCodeIfExist(args, AppConstants.CURRENT_DATE());
            if(lnExist >= 2){
                message = "Only 2 Selfie log per branch is allowed.";
                return false;
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            reportException(poSession.getUserID(), e.getLocalizedMessage());
            return false;
        }
    }

    /**
     *
     * @param args pass the branch code of the selected branch for taking selfie
     * @return return
     *      0 if an error occurred during the validation
     *      1 if user is not area manager and does not require remarks for the default branch code
     *      2 if selected branch is not included on branches in area. Require remarks.
     *      3 if all conditions are not met. proceed selfie log without remarks.
     *      4. if the selected branch is duplicate. proceed selfie log without remarks.
     *      5. if the branch isn't selected. proceed selfie log with remarks.
     */
    public int ValidateSelfieBranch(String args){
        try{
            //if current user is not area manager
            // return a value which will continue the selfie log
            // to save but not requiring a remarks if user has a default branch code
            if(poUser.getUserNonLiveData().getEmpLevID() != DeptCode.LEVEL_AREA_MANAGER){
                message = "User is not Area Manager. Proceed Selfie Log without remarks";
                return 1;
            }

            //Validate if args is empty or null return a value of
            if(args == null){
                message = "Branch isn't selected. Proceed Selfie Log with remarks";
                return 5;
            }

            if(args.isEmpty()){
                message = "Branch isn't selected. Proceed Selfie Log with remarks";
                return 5;
            }

            ESelfieLog loSelfie = poDao.CheckSelfieLogIfExist(args, AppConstants.CURRENT_DATE());
            if(loSelfie != null){
                message = "Proceed Selfie Log without remarks.";
                return 4;
            }

            EBranchInfo loBranch = poDao.GetSelfieLogBranch(args);
            String lsAreaCd = poUser.getUserAreaCode();
            if(!loBranch.getAreaCode().equalsIgnoreCase(lsAreaCd)){
                message = "Branch selected isn't included on Area. Proceed Selfie Log with remarks";
                return 2;
            }

            return 3;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            reportException(poSession.getUserID(), message);
            return 0;
        }
    }

    public String SaveSelfieLog(SelfieLogDetail foVal){
        try{
            ESelfieLog loSelfie = new ESelfieLog();
            String lsTransNo = CreateUniqueID();
            loSelfie.setTransNox(lsTransNo);
            loSelfie.setLatitude(foVal.getLatitude());
            loSelfie.setLongitud(foVal.getLongitude());
            loSelfie.setRemarksx(foVal.getRemarksx());
            loSelfie.setTransact(AppConstants.CURRENT_DATE());
            loSelfie.setEmployID(poSession.getEmployeeID());
            loSelfie.setLogTimex(AppConstants.DATE_MODIFIED());
            loSelfie.setSendStat("0");

            if(foVal.getBranchCode().isEmpty()){
                String lsBranchCd = poSession.getBranchCode();
                loSelfie.setBranchCd(lsBranchCd);
                loSelfie.setReqCCntx("2");
            } else {
                loSelfie.setBranchCd(foVal.getBranchCode());
            }

            String lsImageID = poImage.SaveSelfieLogImage(
                    foVal.getFileName(),
                    foVal.getFileLocation(),
                    foVal.getLatitude(),
                    foVal.getLongitude());
            if(lsImageID == null){
                message = poImage.getMessage();
                return null;
            }

            loSelfie.setImageIDx(lsImageID);
            poDao.SaveSelfieLog(loSelfie);
            Log.d(TAG, "Selfie log info has been save.");
            return lsTransNo;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            reportException(poSession.getUserID(), message);
            return null;
        }
    }

    public boolean UploadSelfieLog(String fsVal){
        try{
            ESelfieLog loDetail = poDao.GetSelfieLog(fsVal);

            if(loDetail == null){
                message = "Unable to find selfie log to upload.";
                Log.e(TAG, message);
                return false;
            }

            String lsImageID = loDetail.getImageIDx();

            lsImageID = poImage.UploadImage(lsImageID);
            if(lsImageID == null){
                message = poImage.getMessage();
                lsImageID = loDetail.getImageIDx();
            }

            poDao.updateSelfieLogImageID(loDetail.getTransNox(), lsImageID);

            Thread.sleep(1000);

            JSONObject params = new JSONObject();
            params.put("sEmployID", loDetail.getEmployID());
            params.put("dLogTimex", loDetail.getLogTimex());
            params.put("nLatitude", loDetail.getLatitude());
            params.put("nLongitud", loDetail.getLongitud());
            params.put("sBranchCd", loDetail.getBranchCd());
            params.put("sRemarksx", loDetail.getRemarksx());

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlPostSelfielog(),
                    params.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = "Server no response";
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                reportException(poSession.getUserID(), message);
                return false;
            }

            poDao.updateEmployeeLogStat(
                    loResponse.getString("sTransNox"),
                    fsVal,
                    lsImageID,
                    AppConstants.DATE_MODIFIED());

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            reportException(poSession.getUserID(), message);
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

    public static class SelfieLogDetail{
        private String sBranchCd = "";
        private String sRemarksx = "";
        private String nLatitude = "0.0";
        private String nLongtude = "0.0";
        private String sFileName = "";
        private String sFileLoct = "";

        public SelfieLogDetail() {
        }

        public String getBranchCode() {
            return sBranchCd;
        }

        public void setBranchCode(String sBranchCd) {
            this.sBranchCd = sBranchCd;
        }

        public String getRemarksx() {
            return sRemarksx;
        }

        public void setRemarksx(String sRemarksx) {
            this.sRemarksx = sRemarksx;
        }

        public String getLatitude() {
            return nLatitude;
        }

        public void setLatitude(String nLatitude) {
            this.nLatitude = nLatitude;
        }

        public String getLongitude() {
            return nLongtude;
        }

        public void setLongitude(String nLongtude) {
            this.nLongtude = nLongtude;
        }

        public String getFileName() {
            return sFileName;
        }

        public void setFileName(String sFileName) {
            this.sFileName = sFileName;
        }

        public String getFileLocation() {
            return sFileLoct;
        }

        public void setLocation(String sFileLoct) {
            this.sFileLoct = sFileLoct;
        }
    }
}
