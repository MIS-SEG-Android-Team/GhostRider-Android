package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.ECASApprovalCode;

import java.util.List;

@Dao
public interface DCASApprovalCode {

    @Upsert
    void SaveCASApprovalCode(ECASApprovalCode loVal);

    @Query("DELETE FROM CAS_Approval_Code")
    void clear();

    @Query("SELECT * FROM CAS_Approval_Code ORDER BY sDescript")
    LiveData<List<ECASApprovalCode>> getCASApprovalCodes();

    @Query("SELECT sDescript FROM CAS_Approval_Code WHERE sSourceCD = :code")
    String getDescription(String code);
}
