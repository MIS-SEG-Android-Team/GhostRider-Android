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

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ESelfieLog;

import java.util.List;

@Dao
public interface DSelfieLog {

    @Insert
    void SaveSelfieLog(ESelfieLog log_selfie);

    @Query("SELECT COUNT (*) FROM Employee_Log_Selfie")
    int GetRowsCountForID();

    @Query("SELECT * FROM Employee_Log_Selfie WHERE sTransNox =:TransNo")
    ESelfieLog GetSelfieLog(String TransNo);

    @Query("UPDATE Employee_Log_Selfie " +
            "SET sTransNox =:TransNox, " +
            "sImageIDx =:sImageID, " +
            "cSendStat = '1', " +
            "dSendDate =:DateSent " +
            "WHERE sTransNox =:OldTransNox")
    void updateEmployeeLogStat(String TransNox, String OldTransNox, String sImageID, String DateSent);

    @Query("UPDATE Employee_Log_Selfie SET sImageIDx =:ImageID WHERE sTransNox =:TransNox")
    void updateSelfieLogImageID(String TransNox, String ImageID);

    @Query("UPDATE Employee_Log_Selfie " +
            "SET cSendStat = '1', " +
            "sImageIDx =:NewImgID " +
            "WHERE sImageIDx =:OldImgID")
    void UpdateUploadedSelfieImageToLog(String OldImgID, String NewImgID);

    @Query("SELECT * FROM Employee_Log_Selfie WHERE cSendStat <> '1'")
    List<ESelfieLog> GetSelfieLogsForUpload();

    @Query("SELECT * FROM Employee_Log_Selfie " +
            "WHERE sEmployID = (SELECT sEmployID FROM User_Info_Master) " +
            "AND dTransact =:fsVal " +
            "ORDER BY dLogTimex DESC")
    LiveData<List<ESelfieLog>> getAllEmployeeTimeLog(String fsVal);

    @Query("SELECT COUNT(*) FROM Employee_Log_Selfie WHERE sBranchCd=:BranchCd AND dTransact=:Transact")
    int checkBranchCodeIfExist(String BranchCd, String Transact);

    @Query("SELECT * FROM Employee_Log_Selfie WHERE sBranchCd=:BranchCd AND dTransact=:Transact")
    ESelfieLog CheckSelfieLogIfExist(String BranchCd, String Transact);

    @Query("SELECT * FROM Branch_Info WHERE sBranchCd =:args")
    EBranchInfo GetSelfieLogBranch(String args);

    @Query("SELECT " +
            "a.sTransNox, " +
            "a.sBranchCd, " +
            "(SELECT sBranchNm FROM Branch_Info WHERE sBranchCd = a.sBranchCd) AS sBranchNm, " +
            "a.dLogTimex, " +
            "a.cSendStat AS cSlfSentx," +
            "b.cSendStat AS cImgSentx, " +
            "b.sFileLoct " +
            "FROM Employee_Log_Selfie a " +
            "LEFT JOIN Image_Information b " +
            "ON a.sImageIDx = b.sTransNox " +
            "WHERE a.dTransact =:args " +
            "ORDER BY a.dLogTimex DESC")
    LiveData<List<LogTime>> GetAllEmployeeTimeLog(String args);

    @Query("SELECT * FROM Image_Information " +
            "WHERE sSourceCD = 'LOGa' " +
            "AND cSendStat = '0'")
    List<EImageInfo> GetSelfieImagesForUpload();

    class LogTime{
        public String sTransNox;
        public String sBranchCd;
        public String sBranchNm;
        public String dLogTimex;
        public String cSlfSentx;
        public String cImgSentx;
        public String sFileLoct;
    }
}
