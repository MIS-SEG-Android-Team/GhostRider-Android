/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 8/13/21 10:32 AM
 * project file last modified : 8/13/21 10:32 AM
 */

package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeBusinessTrip;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;

import java.util.List;

@Dao
public interface DEmployeeBusinessTrip {

    @Insert
    void insert(EEmployeeBusinessTrip obLeave);

    @Update()
    void update(EEmployeeBusinessTrip obLeave);
    @Query("DELETE FROM Employee_Business_Trip")
    void delete();

    @Query("SELECT * FROM User_Info_Master")
    EEmployeeInfo GetEmployeeInfo();

    @Query("SELECT COUNT (*) FROM Employee_Business_Trip")
    int GetRowsCountForID();

    @Query("SELECT * FROM Employee_Business_Trip WHERE sTransNox =:TransNox")
    EEmployeeBusinessTrip GetEmployeeBusinessTrip(String TransNox);

    @Query("SELECT * FROM Employee_Business_Trip " +
            "WHERE dDateFrom=:DateFrom " +
            "AND dDateThru=:DateThru " +
            "AND sDestinat=:Destinat " +
            "AND sRemarksx=:Remarksx")
    EEmployeeBusinessTrip CheckIfApplicationExist(String DateFrom,
                                                  String DateThru,
                                                  String Destinat,
                                                  String Remarksx);

    @Query("SELECT * FROM EMPLOYEE_BUSINESS_TRIP WHERE cSendStat != '1' AND cTranStat != '0'")
    List<EEmployeeBusinessTrip> GetUnpostedApprovals();

    @Query("SELECT * FROM Employee_Business_Trip WHERE cSendStat != '1'")
    List<EEmployeeBusinessTrip> GetUnpostedOBApplications();

    @Query("SELECT * FROM Employee_Business_Trip WHERE sTransNox =:TransNox")
    LiveData<EEmployeeBusinessTrip> getBusinessTripInfo(String TransNox);

    @Query("UPDATE Employee_Business_Trip SET cSendStat = '1', sTransNox =:newTransNox WHERE sTransNox =:TransNox")
    void updateOBSentStatus(String TransNox, String newTransNox);

    @Query("UPDATE Employee_Business_Trip SET cSendStat = '1' WHERE sTransNox=:TransNox")
    void updateObApprovalPostedStatus(String TransNox);

    @Query("SELECT * FROM Employee_Business_Trip " +
            "WHERE cTranStat = '0' " +
            "AND (dDateFrom >= DATE('now', '-30 day') AND dDateThru >= DATE('now', '-30 day')) " +
            "ORDER BY dTransact DESC")
    LiveData<List<EEmployeeBusinessTrip>> getOBListForApproval();

    @Query("SELECT * FROM Employee_Business_Trip " +
            "WHERE xEmployee = (SELECT sEmployID FROM User_Info_Master)")
    LiveData<List<EEmployeeBusinessTrip>> getOBList();

    @Query("SELECT * FROM Employee_Business_Trip WHERE cTranStat <> '0'")
    LiveData<List<EEmployeeBusinessTrip>> GetApproveBusTrip();
}
