/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 6/9/21 2:17 PM
 * project file last modified : 6/9/21 2:17 PM
 */

package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchOpenMonitor;

import java.util.List;

@Dao
public interface  DBranchOpeningMonitor {

    @Insert
    void insert(EBranchOpenMonitor branchOpenMonitor);

    @Query("SELECT * FROM Branch_Opening WHERE dTransact =:dTransact ORDER BY dTimeStmp DESC LIMIT 5")
    LiveData<List<EBranchOpenMonitor>> getBranchOpeningForDashBoard(String dTransact);

    @Query("SELECT * FROM Branch_Opening WHERE dTransact =:dTransact ORDER BY dTimeStmp DESC")
    LiveData<List<EBranchOpenMonitor>> getBranchOpeningForDate(String dTransact);

    @Query("SELECT a.sBranchNm," +
            "b.sTimeOpen, " +
            "b.sOpenNowx " +
            "FROM Branch_Info a " +
            "LEFT JOIN Branch_Opening b " +
            "ON a.sBranchCd = b.sBranchCD " +
            "WHERE b.dTransact =:dTransact " +
            "ORDER BY b.dTimeStmp DESC LIMIT 5")
    LiveData<List<BranchOpeningInfo>> GetBranchOpeningInfoForDashBoard(String dTransact);

    @Query("SELECT a.sBranchNm," +
            "b.sTimeOpen, " +
            "b.sOpenNowx " +
            "FROM Branch_Info a " +
            "LEFT JOIN Branch_Opening b " +
            "ON a.sBranchCd = b.sBranchCD " +
            "WHERE b.dTransact =:dTransact " +
            "ORDER BY b.dTimeStmp DESC")
    LiveData<List<BranchOpeningInfo>> getBranchOpeningInfo(String dTransact);

    class BranchOpeningInfo{
        public String sBranchNm;
        public String sTimeOpen;
        public String sOpenNowx;
    }
}
