/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 5/14/21 4:04 PM
 * project file last modified : 5/14/21 4:04 PM
 */

package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.rmj.g3appdriver.GCircle.room.Entities.ERelation;

import java.util.List;

@Dao
public interface DRelation {
    @Insert
    void insert(ERelation eRelation);

    @Update
    void update(ERelation foVal);

    @Query("SELECT * FROM RELATION WHERE sRelatnID=:fsVal")
    ERelation GetRelationInfo(String fsVal);

    @Query("SELECT * FROM Relation ORDER BY dTimeStmp DESC LIMIT 1")
    ERelation GetLatestRelationInfo();

    @Query("SELECT * FROM Relation ")
    LiveData<List<ERelation>> getRelation();

}

