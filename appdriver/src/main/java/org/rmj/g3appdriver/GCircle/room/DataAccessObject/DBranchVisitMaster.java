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

    @Query("SELECT * FROM Branch_Visit_Master")
    LiveData<List<EBranchVisitMaster>> GetMasterList();

    @Query("SELECT * FROM Branch_Visit_Master")
    List<EBranchVisitMaster> GetMasterListForTest();
}
