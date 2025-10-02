package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.EErrorLogs;

import java.util.List;

@Dao
public interface DErrorLogs {

    @Upsert()
    void SaveErrorLogs(EErrorLogs eErrorLogs);

    @Query("DELETE FROM Error_Logs")
    void Clear();

    @Query("UPDATE Error_Logs SET cRead = '1' WHERE nErrorLogID= :errorID")
    void IsRead(int errorID);

    @Query("SELECT COUNT(*) FROM Error_Logs")
    int GetErrorLogCount();

    @Query("SELECT COUNT(*) FROM Error_Logs WHERE cRead = '0'")
    LiveData<Integer> GetUnreadErrorLogCount();

    @Query("SELECT * FROM Error_Logs ORDER BY dLogDate DESC")
    LiveData<List<EErrorLogs>> GetErrorLgList();

}
