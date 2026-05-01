package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitMaster;

import java.util.List;

@Dao
public interface DBranchVisitMaster {

    @Upsert
    void Save(EBranchVisitMaster foVal);

    @Query("SELECT COUNT(*) FROM Branch_Visit_Master")
    int CountMaster();

    @Query("SELECT * FROM Branch_Visit_Master WHERE sUserIDxx = :fsUserIDxx AND dTransact = :fsDate LIMIT 1")
    EBranchVisitMaster GetEntryToday(String fsUserIDxx, String fsDate);

    @Query("SELECT * FROM Branch_Visit_Master WHERE sTransNox= :fsTransNox")
    LiveData<EBranchVisitMaster> GetMasterTransaction(String fsTransNox);

    @Query("SELECT " +
                "IFNULL((SELECT b.sBranchNm FROM Branch_Info b WHERE b.sBranchCd = a.sBranchCd), a.sBranchCd) sBranchNm, " +
                "a.sTransNox, " +
                "a.sBranchCd, " +
                "a.dTransact, " +
                "a.cSendStat, " +
                "a.cTranStat " +
            "FROM " +
                "Branch_Visit_Master a " +
            "WHERE " +
                "a.sUserIDxx = :fsUserIdxx")
    LiveData<List<MasterHistory>> GetHistory(String fsUserIdxx);

    @Query("SELECT * FROM Branch_Visit_Master")
    List<EBranchVisitMaster> GetMasterListForTest();

    public static class MasterHistory{
        public String sBranchNm;
        public String sTransNox;
        public String sBranchCd;
        public String dTransact;
        public String cSendStat;
        public String cTranStat;
    }
}
