package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.rmj.g3appdriver.GCircle.room.Entities.ELoanTypes;

import java.util.List;

@Dao
public interface DLoanTypes {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void SaveType(ELoanTypes foType);

    @Query("SELECT * FROM Loan_Type")
    LiveData<List<ELoanTypes>> GetTypes();
}
