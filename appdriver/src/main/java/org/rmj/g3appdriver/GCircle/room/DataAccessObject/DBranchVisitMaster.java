package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Upsert;
import androidx.sqlite.db.SupportSQLiteQuery;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitMaster;

import java.util.List;

@Dao
public interface DBranchVisitMaster {

    @Upsert
    void Save(EBranchVisitMaster foVal);

    @Query("SELECT COUNT(*) FROM Branch_Visit_Master")
    int CountMaster();

    @Query("SELECT * FROM Branch_Visit_Master WHERE sUserIDxx = :fsUserIDxx AND DATE(dTransact) = :fsDate LIMIT 1")
    EBranchVisitMaster GetEntryToday(String fsUserIDxx, String fsDate);

    @Query("SELECT * FROM Branch_Visit_Master WHERE sTransNox= :fsTransNox")
    LiveData<EBranchVisitMaster> GetMasterTransaction(String fsTransNox);

    @RawQuery(observedEntities = {EBranchVisitMaster.class, EBranchInfo.class})
    LiveData<List<MasterHistory>> GetHistory(SupportSQLiteQuery fsQuery);

    @Query("SELECT * FROM Branch_Visit_Master")
    List<EBranchVisitMaster> GetMasterListForTest();

    class MasterHistory{
        public String sBranchNm;
        public String sTransNox;
        public String sBranchCd;
        public String dTransact;
        public String cSendStat;
        public String cTranStat;
    }
}
