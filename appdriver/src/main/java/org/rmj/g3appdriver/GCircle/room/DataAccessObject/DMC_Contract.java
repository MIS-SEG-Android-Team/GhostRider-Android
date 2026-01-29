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

    @Query("UPDATE MC_Contract_Info SET sTransNox = :fsTransNox, sClientID = :fsClientID, sAcctNmbr = :fsAcctNmbr, sSendStat = '1'  WHERE sReferNox = :fsReferNox")
    void UpdateTransNox(String fsReferNox, String fsTransNox, String fsClientID, String fsAcctNmbr);

    @Query("SELECT * FROM MC_Contract_Info WHERE sReferNox = :fsTransNox")
    EMCContractInfo GetMContractInfo(String fsTransNox);

    @Query("SELECT * FROM MC_Contract_Info WHERE sReferNox = :fsTransNox")
    EMCContractInfo GetCreditContract(String fsTransNox);

}
