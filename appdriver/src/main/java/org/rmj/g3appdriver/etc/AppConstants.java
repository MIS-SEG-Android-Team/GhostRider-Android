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

package org.rmj.g3appdriver.etc;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import androidx.annotation.ColorInt;

import org.json.JSONObject;
import org.rmj.g3appdriver.R;
import org.rmj.g3appdriver.GCircle.Etc.DeptCode;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppConstants {

    public static final String SUB_FOLDER_DCP = "/DCP";
    public static final String SUB_FOLDER_CREDIT_APP = "/CreditApp";
    public static final String SUB_FOLDER_SELFIE_LOG = "/SelfieLog";
    public static final String SUB_FOLDER_SSDD_EVALUATION = "/SSDDEvaluation";
    public static final String SUB_FOLDER_CREDIT_APP_DOCUMENTS = "/COAD";
    public static final String SUB_FOLDER_EXPORTS = "/Exported Files";
    public static final String SUB_FOLDER_CI_ADDRESS = "/CI Address";

    private static final String LOCAL_MESSAGE = "We apologize for the inconvenience. An error has occurred during the processing of your request";

    public static String getLocalMessage(Exception val){
        return LOCAL_MESSAGE + "\n \n" + val.getMessage();
    }

    public static String ALL_DATA_SENT() throws Exception{
        JSONObject loJson = new JSONObject();
        loJson.put("result", "success");
        return loJson.toString();
    }

    public static String NO_INTERNET() throws Exception{
        JSONObject loJson = new JSONObject();
        JSONObject loError = new JSONObject();
        loJson.put("result", "error");
        loError.put("code", "503");
        loError.put("message", "Not connected to internet");
        loJson.put("error", loError);
        return loJson.toString();
    }

    public static String LOCAL_EXCEPTION_ERROR(String message) {
        JSONObject loJson = new JSONObject();
        JSONObject loError = new JSONObject();
        try {
            loJson.put("result", "error");
            loError.put("code", "202");
            loError.put("message", message);
            loJson.put("error", loError);
        } catch (Exception e){
            e.printStackTrace();
        }
        return loJson.toString();
    }

    public static String ERROR_SAVING_TO_LOCAL() throws Exception{
        JSONObject loJson = new JSONObject();
        JSONObject loError = new JSONObject();
        loJson.put("result", "error");
        loError.put("code", "503");
        loError.put("message", "Unable to save data to local. ");
        loJson.put("error", loError);
        return loJson.toString();
    }

    public static String SERVER_NO_RESPONSE() throws Exception{
        JSONObject loJson = new JSONObject();
        JSONObject loError = new JSONObject();
        loJson.put("result", "error");
        loError.put("code", "404");
        loError.put("message", "No response receive from server.\n Please check your internet connection \n and try again later.");
        loJson.put("error", loError);
        return loJson.toString();
    }

    public static String APPROVAL_CODE_EMPTY(String fsMessage) throws Exception{
        JSONObject loJson = new JSONObject();
        JSONObject loError = new JSONObject();
        loJson.put("result", "error");
        loError.put("code", "404");
        loError.put("message", fsMessage);
        loJson.put("error", loError);
        return loJson.toString();
    }

    public static String APPROVAL_CODE_GENERATED(String fsMessage) throws Exception{
        JSONObject loJson = new JSONObject();
        loJson.put("result", "success");
        loJson.put("code", fsMessage);
        return loJson.toString();
    }

    public static final int LOCATION_REQUEST = 1000;
    public static final int CAMERA_REQUEST = 999;
    public static final int CONTACT_REQUEST = 998;
    public static final int STORAGE_REQUEST = 997;
    public static final int GPS_REQUEST = 1001;
    public static final int DEV_MODE = 451;
    public static final int SETTINGS = 897;

    public static int LOGIN_ACTIVITY_REQUEST_CODE = 143;

    public static int INVENTORY_REQUEST_CODE = 129;

    public static int PERMISION_REQUEST_CODE = 102;

    public static int DataServiceID = 213;

    public static int PerformanceServiceID = 214;

    public static int GLocatorServiceID = 913;

    public static int INTENT_OB_APPLICATION = 101;

    public static int INTENT_LEAVE_APPLICATION = 102;

    public static int INTENT_APPLICATION_APPROVAL = 116;

    public static int INTENT_LEAVE_APPROVAL = 117;

    public static int INTENT_OB_APPROVAL = 118;

    public static int INTENT_APPROVAL_HISTORY = 119;

    public static int INTENT_SELFIE_LOGIN = 103;
    // ADD FOR HELP PURPOSE
    public static int INTENT_DOWNLOAD_DCP = 108;
    public static int INTENT_IMPORT_DCP = 109;
    public static int INTENT_ADD_COLLECTION_DCP = 110;
    public static int INTENT_TRANSACTION_DCP = 111;
    public static int INTENT_DCP_POST_COLLECTION = 112;
    public static int INTENT_DCP_REMITTANCE = 113;
    public static int INTENT_DCP_LOG = 114;
    public static int INTENT_DCP_LIST = 115;

    public static int INTENT_REIMBURSEMENT = 107;

    public static int INTENT_AREA_MONITORING = 105;

    public static int INTENT_BRANCH_MONITORING = 104;

    public static int INTENT_BRANCH_OPENING = 106;

    public static String ACTIVATE_REQUEST = "DEVICES_APPROVE";
    public static String GET_PIN_REQUEST = "DEVICES_GETPIN";
    public static String UNLOCK_REQUEST = "DEVICES_UNLOCK";
    public static String OFFLINE_PIN_REQUEST = "DEVICES_OFFLINE_PIN";
    public static String GET_DEVICE_LOG_REQUEST = "DEVICES_GETDEVICELOG";
    public static String[] CHART_MONTH_LABEL = {
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"};



    public String getPeriodName(int period){
        return CHART_MONTH_LABEL[period];
    }

    public static String[] LEAVE_TYPE = {
            "Vacation",
            "Sick",
            "Others",
            "Birthday",
            "Paternity",
            "Solo Parent",
            "Saturday"};

    public static String GetLeaveTypeIndex(String PaymentType){
        for(int x = 0; x < LEAVE_TYPE.length; x++){
            if(PaymentType.equalsIgnoreCase(LEAVE_TYPE[x])){
                return String.valueOf(x);
            }
        }
        return "";
    }

    public String DATE_MODIFIED = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());

    public static String DATE_MODIFIED(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
    }

    public static String CURRENT_DATE(){
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
    }

    public static String GCARD_DATE_TIME (){
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
    }

    public static String PERFORMANCE_CURRENT_PERIOD = new SimpleDateFormat("yyyyMM", Locale.getDefault()).format(Calendar.getInstance().getTime());
    public String CURRENT_DATE_WORD = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());

    public static String CURRENT_TIME = String.valueOf(new Timestamp(new Date().getTime()));


    public static String getLeaveStatus(String value){
        switch (value){
            case "0":
                return "OPEN";
            case "1":
                return "APPROVE/POSTED";
            case "2":
                return "HR APPROVED";
        }
        return "CANCELED";
    }

    public static int getUserIcon(String level){
        if (level.equalsIgnoreCase(String.valueOf(DeptCode.LEVEL_RANK_FILE))){
            return R.drawable.ic_user_associate;
        } else {
            return R.drawable.ic_user_supervisor;
        }
    }

    public static int getThemeTextColor(Context context) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;
        return color;
    }
}
