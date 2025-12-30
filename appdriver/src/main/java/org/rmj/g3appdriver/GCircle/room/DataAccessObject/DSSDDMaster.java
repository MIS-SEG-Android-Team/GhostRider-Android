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

    @Query("SELECT * FROM SSDD_Master")
    LiveData<List<ESSDDMaster>> GetMasterList();
}
