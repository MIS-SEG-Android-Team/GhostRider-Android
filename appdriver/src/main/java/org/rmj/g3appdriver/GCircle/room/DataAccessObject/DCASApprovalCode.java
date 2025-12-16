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

    @Query("SELECT * FROM CAS_Approval_Code ORDER BY sDescript")
    LiveData<List<ECASApprovalCode>> getCASApprovalCodes();
}
