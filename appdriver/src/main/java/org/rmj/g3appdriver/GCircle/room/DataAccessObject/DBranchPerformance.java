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
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchPerformance;

import java.util.List;

@Dao
public interface DBranchPerformance {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert(EBranchPerformance branchPerformance);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBulkData(List<EBranchPerformance> branchPerformances);

    @Query("SELECT sEmpLevID FROM User_Info_Master")
    int GetUserLevel();

    @Query("SELECT sDeptIDxx FROM User_Info_Master")
    String GetUserDepartment();

    @Query("SELECT * FROM MC_Branch_Performance WHERE sPeriodxx =:Period AND sBranchCd =:BranchCd")
    EBranchPerformance GetBranchPerformance(String Period, String BranchCd);

    @Query("SELECT sAreaCode FROM Branch_Info WHERE sBranchCd = (SELECT sBranchCd FROM User_Info_Master)")
    String GetAreaCode();

    @Query("SELECT sBranchCd FROM Branch_Info WHERE sBranchCd = (SELECT sBranchCd FROM User_Info_Master)")
    String GetBranchCode();

    @Query("SELECT * FROM MC_Branch_Performance WHERE sBranchCd =:BranchCd ORDER BY sPeriodxx ASC")
    LiveData<List<EBranchPerformance>>  getAllBranchPerformanceInfoByBranch(String BranchCd);

    @Query("SELECT sPeriodxx AS Period, " +
            "nMCActual AS Actual, " +
            "nMCGoalxx AS Goal " +
            "FROM MC_Branch_Performance " +
            "WHERE sBranchCd =:BranchCd " +
            "ORDER BY sPeriodxx ASC")
    LiveData<List<PeriodicalPerformance>> GetMCSalesPeriodicPerformance(String BranchCd);

    @Query("SELECT sPeriodxx AS Period, " +
            "nSPActual AS Actual, " +
            "nSPGoalxx AS Goal " +
            "FROM MC_Branch_Performance " +
            "WHERE sBranchCd =:BranchCd " +
            "ORDER BY sPeriodxx ASC")
    LiveData<List<PeriodicalPerformance>> GetSPSalesPeriodicPerformance(String BranchCd);

    @Query("SELECT sPeriodxx AS Period, " +
            "nJOActual AS Actual, " +
            "nJOGoalxx AS Goal " +
            "FROM MC_Branch_Performance " +
            "WHERE sBranchCd =:BranchCd " +
            "ORDER BY sPeriodxx ASC")
    LiveData<List<PeriodicalPerformance>> GetJobOrderPeriodicPerformance(String BranchCd);

    @Query("SELECT nMCActual || '/' || nMCGoalxx AS Performance FROM MC_Branch_Performance WHERE sBranchCd =:branchCd ORDER BY sPeriodxx DESC LIMIT 1")
    LiveData<String> GetMCSalesPerformance(String branchCd);

    @Query("SELECT nSPActual || '/' || nSPGoalxx AS Performance FROM MC_Branch_Performance WHERE sBranchCd =:branchCd ORDER BY sPeriodxx DESC LIMIT 1")
    LiveData<String> GetSPSalesPerformance(String branchCd);

    @Query("SELECT nJOActual || '/' || nJOGoalxx AS Performance FROM MC_Branch_Performance WHERE sBranchCd =:branchCd ORDER BY sPeriodxx DESC LIMIT 1")
    LiveData<String> GetJobOrderPerformance(String branchCd);

    class PeriodicalPerformance{
        public String Period;
        public String Actual;
        public String Goal;
    }
}
