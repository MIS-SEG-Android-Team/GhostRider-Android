package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.rmj.g3appdriver.GCircle.room.Entities.ESCARqstEmp;

@Dao
public interface DSCARqstEmp {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void SaveRqstEmp(ESCARqstEmp foRqst);

    @Query("SELECT dTimeStmpx FROM SCA_Rqst_Emp ORDER BY dTimeStmpx DESC LIMIT 1")
    String GetLatestStamp();

    @Query("SELECT COUNT(*) FROM SCA_Rqst_Emp")
    int CountSCARequest();
}
