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
import androidx.room.Query;
import androidx.room.Update;

import org.rmj.g3appdriver.GCircle.room.Entities.EOccupationInfo;

import java.util.List;

@Dao
public interface DOccupationInfo {

    @Insert
    void insertBulkData(List<EOccupationInfo> occupationInfos);

    @Insert
    void SaveOccupation(EOccupationInfo foVal);

    @Update
    void update(EOccupationInfo foVal);

    @Query("SELECT * FROM Occupation_Info ORDER BY dTimeStmp DESC LIMIT 1")
    EOccupationInfo GetLatestDataInfo();

    @Query("SELECT * FROM Occupation_Info WHERE sOccptnID=:fsVal")
    EOccupationInfo GetOccupationInfo(String fsVal);

    @Query("SELECT * FROM Occupation_Info")
    LiveData<List<EOccupationInfo>> getAllOccupationInfo();

    @Query("SELECT MAX(dTimeStmp) FROM Occupation_Info")
    String getLatestDataTime();

    @Query("SELECT sOccptnNm FROM Occupation_Info WHERE sOccptnID=:ID")
    String getOccupationName(String ID);
}
