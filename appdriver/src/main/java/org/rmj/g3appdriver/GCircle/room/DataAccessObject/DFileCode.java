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
import androidx.room.Query;
import androidx.room.Update;

import org.rmj.g3appdriver.GCircle.room.Entities.EFileCode;

@Dao
public interface DFileCode {

    @Insert
    void insert(EFileCode foVal);

    @Update
    void update(EFileCode foVal);

    @Query("SELECT * FROM EDocSys_File WHERE sFileCode =:fsVal")
    EFileCode GetFileCode(String fsVal);

    @Query("SELECT * FROM EDocSys_File ORDER BY dTimeStmp DESC LIMIT 1")
    EFileCode GetLatestFileCode();

    @Query("SELECT MAX(dTimeStmp) AS TimeStamp FROM EDocSys_File")
    String getLatestDataTime();

}
