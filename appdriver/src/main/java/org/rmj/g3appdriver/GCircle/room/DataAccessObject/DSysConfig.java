/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 4/26/21 4:18 PM
 * project file last modified : 4/26/21 4:18 PM
 */

package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.rmj.g3appdriver.GCircle.room.Entities.ESysConfig;

@Dao
public interface DSysConfig {

    @Insert
    void SaveSysConfig(ESysConfig sysConfig);

    @Update
    void UpdateSysConfig(ESysConfig foVal);

    @Query("SELECT * FROM xxxSysConfig ORDER BY dTimeStmp DESC LIMIT 1")
    ESysConfig GetLatestSysConfig();

    @Query("SELECT * FROM xxxSysConfig WHERE sConfigCd =:fsVal")
    ESysConfig GetSysConfig(String fsVal);
}
