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

    @Query("SELECT " +
                "a.sCategrID," +
                "b.sDescript, " +
                "a.sRemarksx " +
            "FROM " +
                "Branch_Visit_Detail a " +
            "LEFT JOIN " +
                "Branch_Visit_Checklist b " +
            "ON " +
                "a.sCategrID = b.sCategrID " +
            "WHERE " +
                "sTransNox = :sTransNox")
    LiveData<List<BranchVisitDetail>> GetDetails(String sTransNox);

    @Query("SELECT * FROM Branch_Visit_Detail WHERE sTransNox = :sTransNox")
    List<EBranchVisitDetail> GetDetailsForTest(String sTransNox);

    public static class BranchVisitDetail{
        public String sCategrID;
        public String sDescript;
        public String sRemarksx;
    }
}
