/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 9/2/21, 11:06 AM
 * project file last modified : 9/2/21, 11:06 AM
 */

package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeRole;

import java.util.List;

@Dao
public interface DEmployeeRole {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertEmployeeRole(EEmployeeRole role);

    @Update
    void UpdateRole(EEmployeeRole foVal);

    @Query("SELECT * FROM xxxAOEmpRole WHERE sObjectNm=:fsVal")
    EEmployeeRole GetEmployeeRole(String fsVal);

    @Query("SELECT * FROM xxxAOEmpRole " +
            "WHERE cRecdStat = '1' AND sParentxx = '' " +
            "ORDER BY sSeqnceCd ASC")
    LiveData<List<EEmployeeRole>> getEmployeeRoles();

    @Query("SELECT * FROM xxxAOEmpRole " +
            "WHERE sParentxx != '' " +
            "ORDER BY sSeqnceCd ASC")
    LiveData<List<EEmployeeRole>> getChildRoles();

}
