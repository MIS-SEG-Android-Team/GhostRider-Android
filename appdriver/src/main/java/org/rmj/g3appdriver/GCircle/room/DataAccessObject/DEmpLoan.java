package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.rmj.g3appdriver.GCircle.room.Entities.EEmpLoan;

@Dao
public interface DEmpLoan {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void SaveLoan(EEmpLoan foLoan);

    @Query("SELECT * FROM Employee_Loan WHERE sTransNox = :sTransNox ")
    EEmpLoan GetLoanTransnox(String sTransNox);
}
