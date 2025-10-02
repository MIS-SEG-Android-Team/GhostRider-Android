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

package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;

@Dao
public interface DEmployeeInfo {

    @Insert
    void SaveNewEmployeeSession(EEmployeeInfo employee);

    @Query("DELETE FROM User_Info_Master")
    void RemoveSessions();

    @Query("SELECT * FROM User_Info_Master")
    LiveData<EEmployeeInfo> getEmployeeInfo();

    @Query("SELECT * FROM User_Info_Master")
    EEmployeeInfo getEmployeeInfoNonLiveData();

    @Query("SELECT sUserIDxx FROM User_Info_Master")
    LiveData<String> getUserID();

    @Query("SELECT sClientID FROM User_Info_Master")
    LiveData<String> getClientID();

    @Query("SELECT sEmployID FROM User_Info_Master")
    LiveData<String> getEmployID();

    @Query("SELECT strftime('%H:%M:%S', 'now', 'localtime') - strftime('%H:%M:%S', dLoginxxx) AS Session FROM User_Info_Master")
    Integer getLoginDate();

    @Query("SELECT dLoginxxx FROM User_Info_Master")
    String getSessionTime();

    @Query("DELETE FROM User_Info_Master")
    void LogoutUser();

    @Query("DELETE FROM xxxAOEmpRole")
    void ClearAuthorizeFeatures();

    @Query("SELECT sAreaCode FROM Branch_Info WHERE sBranchCd = (SELECT sBranchCd FROM User_Info_Master)")
    String getUserAreaCode();

    @Query("SELECT sEmployID FROM User_Info_Master")
    String getEmployeeID();

    @Query("SELECT a.sUserIDxx, " +
            "a.sUserName, " +
            "a.sDeptIDxx, " +
            "a.sEmpLevID," +
            "a.sEmployID," +
            "b.sBranchCd, " +
            "b.sBranchNm, " +
            "b.sAddressx FROM User_Info_Master a " +
            "LEFT JOIN Branch_Info b " +
            "ON a.sBranchCD = b.sBranchCd")
    LiveData<EmployeeBranch> GetEmployeeBranch();

    class EmployeeBranch{
        public String sUserIDxx;
        public String sUserName;
        public String sDeptIDxx;
        public String sEmpLevID;
        public String sEmployID;
        public String sBranchCd;
        public String sBranchNm;
        public String sAddressx;
    }
}
