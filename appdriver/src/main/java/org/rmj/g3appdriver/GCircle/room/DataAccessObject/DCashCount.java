/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 6/9/21 4:27 PM
 * project file last modified : 6/9/21 4:27 PM
 */

package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECashCount;

import java.util.List;

@Dao
public interface DCashCount {

    @Insert()
    void SaveCashCount(ECashCount cashCount);

    @Query("SELECT COUNT (*) FROM Cash_Count_Master")
    int GetRowsCountForID();

    @Query("SELECT * FROM Cash_Count_Master " +
            "WHERE sBranchCd=:lsBranchCd " +
            "AND sPettyAmt=:lsPettyAmt " +
            "AND dEntryDte=:lsEntryDte " +
            "AND dTransact=:lsTransact " +
            "AND nCn0001cx=:lnCn0001cx " +
            "AND nCn0005cx=:lnCn0005cx " +
            "AND nCn0010cx=:lnCn0010cx " +
            "AND nCn0025cx=:lnCn0025cx " +
            "AND nCn0050cx=:lnCn0050cx " +
            "AND nCn0001px=:lnCn0001px " +
            "AND nCn0005px=:lnCn0005px " +
            "AND nCn0010px=:lnCn0010px " +
            "AND nNte0020p=:lnNte0020p " +
            "AND nNte0050p=:lnNte0050p " +
            "AND nNte0100p=:lnNte0100p " +
            "AND nNte0200p=:lnNte0200p " +
            "AND nNte0500p=:lnNte0500p " +
            "AND nNte1000p=:lnNte1000p")
    ECashCount CheckCashCountIfExist(String lsBranchCd,
            double lsPettyAmt,
            String lsEntryDte,
            String lsTransact,
            int lnCn0001cx,
            int lnCn0005cx,
            int lnCn0010cx,
            int lnCn0025cx,
            int lnCn0050cx,
            int lnCn0001px,
            int lnCn0005px,
            int lnCn0010px,
            int lnNte0020p,
            int lnNte0050p,
            int lnNte0100p,
            int lnNte0200p,
            int lnNte0500p,
            int lnNte1000p);

    @Query("SELECT * FROM Cash_Count_Master WHERE sTransNox =:fsVal")
    ECashCount GetCashCountDetail(String fsVal);

    @Query("SELECT * FROM Cash_Count_Master WHERE sTransNox =:TransNox")
    LiveData<ECashCount> getCashCounDetetail(String TransNox);

    @Query("SELECT * FROM Branch_Info WHERE sBranchCd =:args")
    LiveData<EBranchInfo> GetBranchForCashCount(String args);

    @Query("SELECT COUNT(*) FROM Employee_Log_Selfie WHERE dTransact =:args")
    int CheckIfHasSelfieLog(String args);

//    @Query("SELECT b.* FROM Employee_Log_Selfie a " +
//            "LEFT JOIN Branch_Info b " +
//            "ON a.sBranchCd = b.sBranchCd " +
//            "LEFT JOIN Cash_Count_Master c " +
//            "ON b.sBranchCd = c.sBranchCd " +
//            "WHERE a.dTransact =:args AND ")
    @Query("SELECT a.* FROM Branch_Info a " +
            "LEFT JOIN Employee_Log_Selfie b " +
            "ON a.sBranchCd = b.sBranchCd " +
            "WHERE b.dTransact=:args " +
            "AND b.sBranchCd NOT IN (SELECT sBranchCd FROM Cash_Count_Master WHERE dTransact=:args)")
    List<EBranchInfo> GetBranchesForCashCount(String args);

    @Query("UPDATE Cash_Count_Master SET " +
            "sTransNox =:transNox, " +
            "sSendStat = 1," +
            "dModified =:dateTime " +
            "WHERE sTransNox =:fsVal ")
    void UpdateUploadedCashCount(String transNox, String fsVal, String dateTime);

    @Query("SELECT * FROM Cash_Count_Master WHERE sSendStat == 0")
    List<ECashCount> GetUnsentCashCountEntries();

    @Query("SELECT a.sTransNox," +
            "a.nCn0001cx, " +
            "a.nCn0005cx, " +
            "a.nCn0010cx, " +
            "a.nCn0025cx, " +
            "a.nCn0050cx, " +
            "a.nCn0001px, " +
            "a.nCn0005px, " +
            "a.nCn0010px, " +
            "a.nNte0020p, " +
            "a.nNte0050p, " +
            "a.nNte0100p, " +
            "a.nNte0200p, " +
            "a.nNte0500p, " +
            "a.nNte1000p, " +
            "a.dEntryDte, " +
            "a.sSendStat, " +
            "b.sBranchNm FROM " +
            "Cash_Count_Master a LEFT JOIN " +
            "Branch_Info b ON " +
            "a.sBranchCd = b.sBranchCd")
    LiveData<List<CashCountLog>> getCashCountLog();

    @Query("SELECT * FROM Cash_Count_Master WHERE sBranchCd =:BranchCd AND dTransact=:Transact")
    ECashCount GetCashCountForBranch(String BranchCd, String Transact);

    @Query("SELECT sAreaCode FROM Branch_Info WHERE sBranchCd =:BranchCd")
    String GetBranchAreaCode(String BranchCd);

    @Query("SELECT sAreaCode FROM Branch_Info WHERE sBranchCd = (SELECT sBranchCd FROM User_Info_Master)")
    String GetUserAreaCode();

    @Query("SELECT sEmpLevID FROM User_Info_Master")
    int GetEmployeeLevel();

    class CashCountLog{
        public String sTransNox;
        public String nCn0001cx;
        public String nCn0005cx;
        public String nCn0010cx;
        public String nCn0025cx;
        public String nCn0050cx;
        public String nCn0001px;
        public String nCn0005px;
        public String nCn0010px;
        public String nNte0020p;
        public String nNte0050p;
        public String nNte0100p;
        public String nNte0200p;
        public String nNte0500p;
        public String nNte1000p;

        public String dEntryDte;

        public String sSendStat;
        public String sBranchNm;
    }
}
