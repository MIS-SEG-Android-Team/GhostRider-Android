package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import android.database.sqlite.SQLiteQuery;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Upsert;
import androidx.sqlite.db.SupportSQLiteQuery;

import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;

import java.util.List;

@Dao
public interface DSSDDMaster {

    @Upsert
    void Save(ESSDDMaster eeMaster);

    @Query("UPDATE SSDD_Master SET sTransNox= :fsNewTransNox, cTranStat= '1' WHERE sTransNox= :fsOldTransNox ")
    void Submit(String fsNewTransNox, String fsOldTransNox);

    @Query("SELECT COUNT(*) FROM SSDD_Master")
    int GetCount();

    @Query("SELECT * FROM SSDD_Master WHERE sTransNox= :fsTransNox AND sDeptIDxx= :fsDeptIDxx")
    LiveData<ESSDDMaster> GetMaster(String fsTransNox, String fsDeptIDxx);

    @RawQuery(observedEntities = ESSDDMaster.class )
    LiveData<List<ESSDDMaster>> GetMasterList(SupportSQLiteQuery fsQuery);

}
