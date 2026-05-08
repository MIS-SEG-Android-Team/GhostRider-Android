package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchVisitChecklist;

import java.util.List;

@Dao
public interface DBranchVisitChecklist {

    @Upsert
    void Save(EBranchVisitChecklist foVal);

    @Query("SELECT sDescript FROM Branch_Visit_Checklist WHERE sCategrID = :fsCategrID")
    String GetDescription(String fsCategrID);

    @Query("SELECT * FROM Branch_Visit_Checklist WHERE cRecdStat = '1' ORDER BY sDescript ASC")
    LiveData<List<EBranchVisitChecklist>> GetChecklist();

    @Query("SELECT * FROM Branch_Visit_Checklist WHERE cRecdStat = '1' ORDER BY sDescript ASC")
    List<EBranchVisitChecklist> GetChecklistForTest();
}
