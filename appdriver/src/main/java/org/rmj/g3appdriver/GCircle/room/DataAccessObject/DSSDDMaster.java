package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;

import java.util.List;

@Dao
public interface DSSDDMaster {

    @Upsert
    void Save(ESSDDMaster eeMaster);

    @Query("SELECT COUNT(*) FROM SSDD_Master")
    int GetCount();

    @Query("SELECT * FROM SSDD_Master WHERE sTransNox= :fsTransNox AND sDeptIDxx= :fsDeptIDxx")
    LiveData<ESSDDMaster> GetMaster(String fsTransNox, String fsDeptIDxx);

    @Query("SELECT * FROM SSDD_Master WHERE dTransact BETWEEN :dFrom AND :dTo AND sDeptIDxx = :sDeptIDxx AND cTranStat = :cTranStat ORDER BY dTransact DESC")
    LiveData<List<ESSDDMaster>> GetMasterList(String dFrom, String dTo, String sDeptIDxx, String cTranStat);

}
