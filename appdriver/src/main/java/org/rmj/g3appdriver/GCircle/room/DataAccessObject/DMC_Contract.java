package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.EMCContractInfo;

@Dao
public interface DMC_Contract {

    @Upsert
    void Save(EMCContractInfo foVal);

    @Query("SELECT COUNT(*) FROM MC_Contract_Info")
    int CountRecord();

    @Query("SELECT * FROM MC_Contract_Info WHERE sReferNox = :fsTransNox")
    EMCContractInfo GetContractInfo(String fsTransNox);

}
