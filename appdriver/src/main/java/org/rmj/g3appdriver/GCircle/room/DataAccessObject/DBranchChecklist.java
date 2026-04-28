package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchChecklist;

@Dao
public interface DBranchChecklist {

    @Upsert
    void Save(EBranchChecklist foVal);

    @Query("SELECT * FROM Branch_Checklist WHERE cRecdStat = '1' ORDER BY sDescript ASC")
    LiveData<EBranchChecklist> GetChecklist();
}
