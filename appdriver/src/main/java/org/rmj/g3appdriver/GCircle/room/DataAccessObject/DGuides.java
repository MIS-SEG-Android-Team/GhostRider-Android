package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.EGuides;

import java.util.List;

@Dao
public interface DGuides {

    @Upsert
    void InsertGuides(EGuides eGuides);

    @Query("DELETE FROM User_Guides")
    void DeleteGuides();

    @Query("DELETE FROM User_Guides")
    void clear();

    @Query("SELECT * FROM User_Guides")
    LiveData<List<EGuides>> GetGuides();
}
