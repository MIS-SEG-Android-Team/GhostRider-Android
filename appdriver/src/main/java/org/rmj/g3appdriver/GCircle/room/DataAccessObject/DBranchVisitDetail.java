package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitDetail;

import java.util.List;

@Dao
public interface DBranchVisitDetail {

    @Upsert
    void Save(EBranchVisitDetail foVal);

    @Query("SELECT * FROM Branch_Visit_Detail WHERE sTransNox = :sTransNox")
    LiveData<List<EBranchVisitDetail>> GetDetails(String sTransNox);

    @Query("SELECT * FROM Branch_Visit_Detail WHERE sTransNox = :sTransNox")
    List<EBranchVisitDetail> GetDetailsForTest(String sTransNox);
}
