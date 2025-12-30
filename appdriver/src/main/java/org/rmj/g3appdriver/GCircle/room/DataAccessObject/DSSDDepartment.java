package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDepartments;

import java.util.List;

@Dao
public interface DSSDDepartment {

    @Upsert
    void Save(ESSDDepartments eeDepartment);

    @Query("SELECT * FROM SSDD_Department")
    LiveData<List<ESSDDepartments>> GetDepartmentList();

}
