package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDetail;

import java.util.List;

@Dao
public interface DSSDDetail {

    @Upsert
    void Save(ESSDDetail eeSSDDetail);

    @Query("SELECT * FROM SSDD_Detail WHERE sTransNox= :fsTransNox")
    LiveData<List<ESSDDetail>> GetDetails(String fsTransNox);

    @Query("SELECT * FROM SSDD_Detail WHERE sTransNox= :fsTransNox AND sCategrID= :fsCategory")
    LiveData<ESSDDetail> GetCategoryDetail(String fsTransNox, String fsCategory);
}
