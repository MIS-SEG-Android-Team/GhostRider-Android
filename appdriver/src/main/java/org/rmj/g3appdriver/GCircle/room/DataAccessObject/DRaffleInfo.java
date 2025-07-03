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

import org.rmj.g3appdriver.GCircle.room.Entities.ERaffleBasis;
import org.rmj.g3appdriver.GCircle.room.Entities.ERaffleInfo;

import java.util.List;

@Dao
public interface DRaffleInfo {

    @Insert
    void insert(ERaffleInfo raffleEntry);

    @Update
    void update(ERaffleInfo raffleEntry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBulkData(List<ERaffleBasis> raffleEntries);

    @Query("SELECT * FROM FB_Raffle_Transaction_Basis " +
            "WHERE cRecdStat = '1' " +
            "AND sDivision = (SELECT cPromoDiv FROM Branch_Info " +
            "WHERE sBranchCd = (" +
            "SELECT sBranchCd FROM User_Info_Master)) " +
            "GROUP BY sReferNme " +
            "ORDER BY sReferNme")
    LiveData<List<ERaffleBasis>> getRaffleBasis();

    @Query("SELECT sReferNme FROM FB_Raffle_Transaction_Basis " +
            "WHERE cRecdStat = '1' " +
            "AND sDivision = (" +
            "SELECT cPromoDiv FROM Branch_Info " +
            "WHERE sBranchCd = (" +
            "SELECT sBranchCd FROM User_Info_Master))" +
            "GROUP BY sReferNme " +
            "ORDER BY sReferNme")
    LiveData<String[]> getRaffleBasisDesc();

}
