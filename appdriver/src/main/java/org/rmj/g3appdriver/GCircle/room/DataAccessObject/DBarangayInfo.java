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

import org.rmj.g3appdriver.GCircle.room.Entities.EBarangayInfo;

import java.util.List;

@Dao
public interface DBarangayInfo {

    @Insert
    void insert(EBarangayInfo barangayInfo);

    @Update
    void update(EBarangayInfo barangayInfo);

    @Query("SELECT * FROM Barangay_Info WHERE sBrgyIDxx =:fsVal")
    EBarangayInfo GetBarangayInfo(String fsVal);

    @Query("SELECT * FROM Barangay_Info ORDER BY dTimeStmp DESC LIMIT 1")
    EBarangayInfo GetLatestBarangayInfo();

    @Query("SELECT MAX(dTimeStmp) FROM Barangay_Info")
    String getLatestDataTime();

    @Query("SELECT * FROM Barangay_Info WHERE sTownIDxx = :TownID")
    LiveData<List<EBarangayInfo>> getAllBarangayInfoFromTown(String TownID);

    @Query("SELECT sBrgyName FROM Barangay_Info WHERE sTownIDxx = :TownID")
    LiveData<String[]> getAllBarangayNameFromTown(String TownID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBulkBarangayData(List<EBarangayInfo> barangayInfoList);

    @Query("SELECT a.sBrgyName, " +
            "b.sTownName, " +
            "c.sProvName FROM Barangay_Info a " +
            "LEFT JOIN Town_Info b ON a.sTownIDxx = b.sTownIDxx " +
            "LEFT JOIN Province_Info c ON b.sProvIDxx = c.sProvIDxx " +
            "WHERE a.sBrgyIDxx =:BrgyID")
    BrgyTownProvNames getBrgyTownProvName(String BrgyID);


    class BrgyTownProvNames{
        public String sBrgyName;
        public String sTownName;
        public String sProvName;
    }
}
