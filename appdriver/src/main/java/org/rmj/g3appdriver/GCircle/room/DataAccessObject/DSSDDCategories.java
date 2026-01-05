package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDCategories;

import java.util.List;

@Dao
public interface DSSDDCategories {

    @Upsert
    void Save(ESSDDCategories eeCategory);

    @Query("SELECT * FROM SSDD_Categories WHERE sCategrID= :fsCategrID")
    ESSDDCategories GetCategory(String fsCategrID);

    @Query("SELECT * FROM SSDD_Categories")
    LiveData<List<ESSDDCategories>> GetCategories();
}
