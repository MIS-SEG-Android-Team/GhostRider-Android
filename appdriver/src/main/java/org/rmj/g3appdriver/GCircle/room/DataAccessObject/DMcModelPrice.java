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

import org.rmj.g3appdriver.GCircle.room.Entities.EMcModelPrice;

import java.util.List;

@Dao
public interface DMcModelPrice {

    @Insert
    void insert(EMcModelPrice mcModelPrice);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBulkdData(List<EMcModelPrice> mcModelPrices);

    @Update
    void update(EMcModelPrice mcModelPrice);

    @Query("SELECT * FROM MC_MODEL_PRICE WHERE sModelIDx =:fsVal")
    EMcModelPrice GetModelPrice(String fsVal);

    @Query("SELECT * FROM MC_MODEL_PRICE ORDER BY dTimeStmp DESC LIMIT 1")
    EMcModelPrice GetLatestModelPrice();

    @Query("SELECT MAX(dTimeStmp) FROM Mc_Model_Price")
    String getLatestDataTime();
}
