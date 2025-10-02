/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 6/19/21 10:49 AM
 * project file last modified : 6/19/21 10:49 AM
 */

package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeLeave;

import java.util.List;

@Dao
public interface DEmployeeLeave {

    @Insert
    void insertApplication(EEmployeeLeave poLeave);

    @Update
    void updateApplication(EEmployeeLeave poLeave);

    @Query("DELETE FROM Employee_Leave")
    void deleteApplication();

    @Query("SELECT * FROM Employee_Leave " +
            "WHERE sEntryByx=:EmployID " +
            "AND sPurposex=:Remarks " +
            "AND cLeaveTyp=:LeaveTp " +
            "AND dDateFrom=:DateFrom " +
            "AND dDateThru=:DateThru")
    EEmployeeLeave CheckIfApplicationExist(String EmployID,
                                           String Remarks,
                                           String LeaveTp,
                                           String DateFrom,
                                           String DateThru);

    @Query("SELECT COUNT (*) FROM Employee_Leave")
    int GetRowsCountForID();

    @Query("SELECT * FROM User_Info_Master")
    EEmployeeInfo GetEmployeeInfo();

    @Query("SELECT * FROM Employee_Leave WHERE sTransNox =:TransNox")
    EEmployeeLeave GetEmployeeLeave(String TransNox);

    @Query("SELECT * FROM Employee_Leave WHERE sTransNox =:TransNox")
    LiveData<EEmployeeLeave> getEmployeeLeaveInfo(String TransNox);

    @Query("UPDATE Employee_Leave SET sTransNox =:newTransNox, cSentStat = '1', dSendDate =:DateSent WHERE sTransNox =:TransNox")
    void updateSendStatus(String DateSent, String TransNox, String newTransNox);

    @Query("SELECT * FROM Employee_Leave " +
            "WHERE cTranStat = '0' " +
            "AND (dDateFrom >= DATE('now', '-30 day') AND dDateThru >= DATE('now', '-30 day')) " +
            "ORDER BY dTransact DESC")
    LiveData<List<EEmployeeLeave>> getEmployeeLeaveForApprovalList();

    @Query("SELECT * FROM Employee_Leave " +
            "WHERE sEntryByx = (SELECT sEmployID FROM User_Info_Master)")
    LiveData<List<EEmployeeLeave>> getEmployeeLeaveList();

    @Query("SELECT * FROM Employee_Leave WHERE cTranStat <> '0'")
    LiveData<List<EEmployeeLeave>> getApproveLeaveList();

    @Query("SELECT * FROM Employee_Leave WHERE cSentStat <> '1'")
    List<EEmployeeLeave> getUnsentEmployeeLeave();
}
