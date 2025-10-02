package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.rmj.g3appdriver.GCircle.room.Entities.EEmpLoan;

import java.util.List;

@Dao
public interface DEmpLoan {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void SaveLoan(EEmpLoan foLoan);
    @Query("UPDATE Employee_Loan SET cSendStat = '1', sTransNox = :newTransNox WHERE sTransNox = :oldTransNox")
    void UpdateSendStat(String oldTransNox, String newTransNox);
    @Query("UPDATE Employee_Loan SET cTranStat = '1' WHERE sTransNox = :sTransNox")
    void ApproveLoanEntry(String sTransNox);
    @Query("SELECT * FROM Employee_Loan WHERE cSendStat IS NULL OR cSendStat = '0'")
    List<EEmpLoan> GetOfflineEntries();
    @Query("SELECT * FROM Employee_Loan WHERE sEmployID = :sEmpID ORDER BY sLoanIDxx ASC, dLoanDate DESC")
    LiveData<List<EEmpLoan>> GetUserEntries(String sEmpID);
    @Query("SELECT * FROM Employee_Loan WHERE cTranStat = '0' OR cTranStat IS NULL OR cTranStat = '' ORDER BY sEmployID ASC, sLoanIDxx ASC, dLoanDate DESC")
    LiveData<List<EEmpLoan>> GetForApproval();
    @Query("SELECT COUNT(sTransNox) FROM Employee_Loan")
    int GetRowsCountForID();
}
