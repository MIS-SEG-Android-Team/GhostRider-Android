/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 6/2/21 3:37 PM
 * project file last modified : 4/24/21 3:19 PM
 */

package org.rmj.g3appdriver.GCircle.Account;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class EmployeeSession {
    //LOG CAT TAG
    private static final String TAG = EmployeeSession.class.getSimpleName();

    //SHARED PREFERENCES
    private final SharedPreferences pref;

    private final Editor editor;

    //shared preference  file name
    private static final String PREF_NAME = "AndroidGhostRider";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    private static final String KEY_IS_FIRST_LAUNCHED = "isFirstLaunch";

    private static final String KEY_CLIENT_ID = "sClientID";

    private static final String KEY_LOG_NUMBER = "sLogNoxxx";

    private static final String KEY_BRANCH_CODE = "sBranchCd";

    private static final String KEY_BRANCH_NAME = "sBranchNm";

    private static final String KEY_USER_ID = "sUserIDxx";

    private static final String KEY_DEPT_ID = "sDeptIDxx";

    private static final String KEY_EMP_LVL = "nUserLevl";

    private static final String KEY_EMPLOYEE_ID = "sEmplIDxx";

    private static final String KEY_POSITION_ID = "sPositionID";

    private static final String KEY_AUTO_LOG = "cPrivatex";

    private static final String KEY_USER_NAME = "sUserName";

    private static EmployeeSession instance;

    private EmployeeSession(Context context){
        //Shared pref mode
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static EmployeeSession getInstance(Context context){
        if(instance == null){
            instance = new EmployeeSession(context);
        }

        return instance;
    }

    public void initUserSession(String UserID,
                                String UserName,
                                String Client,
                                String LogNo,
                                String Branch,
                                String BranchNm,
                                String DeptID,
                                String EmpID,
                                String Position,
                                String Level,
                                String autoLog){
        editor.putString(KEY_USER_ID, UserID);
        if(editor.commit()){
            Log.e(TAG, "User ID for this session has been set.");
        }

        editor.putString(KEY_USER_NAME, UserName);
        if(editor.commit()){
            Log.e(TAG, "User name for this session has been set.");
        }

        editor.putString(KEY_CLIENT_ID, Client);
        if(editor.commit()){
            Log.e(TAG, "Client ID for this session has been set.");
        }

        editor.putString(KEY_LOG_NUMBER, LogNo);
        if(editor.commit()){
            Log.e(TAG, "Log number for this session has been set.");
        }

        editor.putString(KEY_BRANCH_CODE, Branch);
        if(editor.commit()){
            Log.e(TAG, "Branch code for this session has been set.");
        }

        editor.putString(KEY_BRANCH_NAME, BranchNm);
        if(editor.commit()){
            Log.e(TAG, "Branch name for this session has been set.");
        }

        editor.putString(KEY_DEPT_ID, DeptID);
        if(editor.commit()){
            Log.e(TAG, "User Department ID for this session has been set.");
        }

        editor.putString(KEY_EMPLOYEE_ID, EmpID);
        if(editor.commit()){
            Log.e(TAG, "User Employee ID for this session has been set.");
        }

        editor.putString(KEY_POSITION_ID, Position);
        if(editor.commit()){
            Log.e(TAG, "User position ID for this session has been set.");
        }

        editor.putString(KEY_EMP_LVL, Level);
        if(editor.commit()){
            Log.e(TAG, "Employee Level for this session has been set.");
        }

        editor.putBoolean(KEY_IS_LOGGEDIN, true);
        if(editor.commit()){
            Log.d(TAG, "User login session has been initialized.");
        }

        editor.putString(KEY_AUTO_LOG, autoLog);
        if(editor.commit()){
            Log.d(TAG, "User login session has been initialized.");
        }
    }

    public void initUserLogout(){
        editor.putString(KEY_USER_ID, "");
        if(editor.commit()){
            Log.e(TAG, "User ID for this session has been set.");
        }

        editor.putString(KEY_USER_NAME, "");
        if(editor.commit()){
            Log.e(TAG, "User name for this session has been set.");
        }

        editor.putString(KEY_CLIENT_ID, "");
        if(editor.commit()){
            Log.e(TAG, "Client ID for this session has been set.");
        }

        editor.putString(KEY_LOG_NUMBER, "");
        if(editor.commit()){
            Log.e(TAG, "Log number for this session has been set.");
        }

        editor.putBoolean(KEY_IS_LOGGEDIN, false);
        if(editor.commit()){
            Log.d(TAG, "User login session has been initialized.");
        }
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public String getUserID(){
        return pref.getString(KEY_USER_ID, "");
    }

    public String getUserName(){
        return pref.getString(KEY_USER_NAME, "");
    }

    public String getClientId(){
        return pref.getString(KEY_CLIENT_ID, "");
    }

    public String getLogNumber(){
        return pref.getString(KEY_LOG_NUMBER, "");
    }

    public String getBranchCode() {
        return pref.getString(KEY_BRANCH_CODE, "");
    }

    public String getBranchName() {
        return pref.getString(KEY_BRANCH_NAME, "");
    }

    public String getDeptID(){
        return pref.getString(KEY_DEPT_ID, "");
    }

    public String getEmployeeID(){
        return pref.getString(KEY_EMPLOYEE_ID, "");
    }

    public String getPositionID(){
        return pref.getString(KEY_POSITION_ID, "");
    }

    public String getEmployeeLevel(){
        return pref.getString(KEY_EMP_LVL, "");
    }

    public void setDepartment(String val){
        editor.putString(KEY_DEPT_ID, val);
        if(editor.commit()){
            Log.e(TAG, "User Department ID for this session has been set.");
        }
    }

}

