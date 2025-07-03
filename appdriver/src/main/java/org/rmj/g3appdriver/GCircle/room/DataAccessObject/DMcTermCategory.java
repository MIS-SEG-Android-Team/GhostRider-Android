/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.rmj.g3appdriver.GCircle.room.Entities.EMcTermCategory;

import java.util.List;

@Dao
public interface DMcTermCategory {

    @Insert
    void insert(EMcTermCategory mcTermCategory);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBulkData(List<EMcTermCategory> categories);

    @Update
    void update(EMcTermCategory mcTermCategory);

    @Query("SELECT * FROM MC_Term_Category WHERE sMCCatIDx =:fsVal AND nAcctTerm =:fsVal1")
    EMcTermCategory GetMcTermCategory(String fsVal, String fsVal1);

    @Query("SELECT * FROM MC_Term_Category ORDER BY dTimeStmp DESC LIMIT 1")
    EMcTermCategory GetLatestMcTermCategory();

    @Query("SELECT MAX(dTimeStmp) FROM MC_Term_Category")
    String getLatestDataTime();
}
