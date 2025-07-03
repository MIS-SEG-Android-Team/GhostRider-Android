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

import org.rmj.g3appdriver.GCircle.room.Entities.ECountryInfo;

import java.util.List;

@Dao
public interface DCountryInfo {

    @Insert
    void insert(ECountryInfo countryInfo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBulkData(List<ECountryInfo> countryInfos);

    @Update
    void update(ECountryInfo countryInfo);

    @Query("SELECT * FROM Country_Info WHERE sCntryCde =:fsVal")
    ECountryInfo GetCountryInfo(String fsVal);

    @Query("SELECT * FROM Country_Info ORDER BY dTimeStmp DESC LIMIT 1")
    ECountryInfo GetLatestCountryInfo();

    @Query("SELECT * FROM Country_Info")
    LiveData<List<ECountryInfo>> getAllCountryInfo();

    @Query("SELECT MAX(dTimeStmp) FROM Country_Info")
    String getLatestDataTime();

    @Query("SELECT * FROM Country_Info WHERE sCntryCde=:ID")
    ECountryInfo getCountryInfo(String ID);
}
