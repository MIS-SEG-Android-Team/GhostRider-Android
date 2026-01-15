package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDImages;

import java.util.List;

@Dao
public interface DSSDDImages {

    @Upsert
    void Save(ESSDDImages foVal);

    @Query("SELECT COUNT(*) FROM SSDD_Images")
    int GetCount();

    @Query("SELECT COUNT(*) FROM SSDD_Images WHERE sReferNox = :fsTransNox AND sCategrID = :fsCategrID")
    int GetCountPerCategory(String fsTransNox, String fsCategrID);

    @Query("SELECT * FROM SSDD_Images WHERE sReferNox= :fsTransNox AND sCategrID= :fsCategory")
    LiveData<List<ESSDDImages>> GetCategoryImages(String fsTransNox, String fsCategory);
}
