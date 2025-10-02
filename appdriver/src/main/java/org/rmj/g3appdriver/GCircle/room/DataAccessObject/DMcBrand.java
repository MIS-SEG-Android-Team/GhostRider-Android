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

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.rmj.g3appdriver.GCircle.room.Entities.EMcBrand;

import java.util.List;

@Dao
public interface DMcBrand {

    @Insert
    void insert(EMcBrand mcBrand);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBulkData(List<EMcBrand> brands);

    @Update
    void update(EMcBrand mcBrand);

    @Query("SELECT * FROM MC_Brand ORDER BY dTimeStmp DESC LIMIT 1")
    EMcBrand GetLatestBrandInfo();

    @Query("SELECT * FROM MC_Brand")
    LiveData<List<EMcBrand>> getAllMcBrand();

    @Query("SELECT MAX(dTimeStmp) FROM MC_Brand")
    String getLatestDataTime();

    @Query("SELECT * FROM MC_Brand WHERE sBrandIDx=:BrandID")
    EMcBrand getMcBrandInfo(String BrandID);
}
