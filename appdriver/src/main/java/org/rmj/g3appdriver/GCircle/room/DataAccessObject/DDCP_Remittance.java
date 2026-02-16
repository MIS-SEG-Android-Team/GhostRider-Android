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

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.rmj.g3appdriver.GCircle.room.Entities.EDCP_Remittance;

@Dao
public interface DDCP_Remittance {

    @Insert
    void SaveRemittance(EDCP_Remittance remittance);

    @Query("SELECT COUNT(*) + 1 AS nEntryNox FROM LR_DCP_Remittance")
    String GetRowsForID();

    @Update
    void Update(EDCP_Remittance foVal);

    @Query("SELECT SUM(nTranTotl) FROM LR_DCP_Collection_Detail WHERE sTransNox =:fsVal")
    LiveData<String> GetTotalCollection(String fsVal);

    @Query("SELECT SUM(nAmountxx) FROM LR_DCP_Remittance WHERE sTransNox =:fsVal")
    LiveData<String> GetRemittedCollection(String fsVal);

    @Query("SELECT IFNULL((SELECT SUM(nTranTotl) FROM LR_DCP_Collection_Detail " +
            "WHERE sTransNox =:fsVal " +
            "AND cPaymForm = '0'), 0.00) AS nCashClt")
    LiveData<String> GetCashCollection(String fsVal);

    @Query("SELECT IFNULL((SELECT SUM(nTranTotl) FROM LR_DCP_Collection_Detail " +
            "WHERE sTransNox =:fsVal " +
            "AND cPaymForm = '1'), 0.00) AS nCheckClt")
    LiveData<String> GetCheckCollection(String fsVal);

    @Query("SELECT IFNULL((SELECT SUM(nTranTotl) FROM LR_DCP_Collection_Detail " +
            "WHERE sTransNox =:fsVal " +
            "AND cPaymForm = '7'), 0.00) AS nEPayClt")
    LiveData<String> GetEPayCollection(String fsVal);

    @Query("SELECT IFNULL((SELECT SUM(nAmountxx) FROM LR_DCP_Remittance " +
            "WHERE sTransNox =:fsVal " +
            "AND cRemitTyp = '0'), 0.00) AS nBrnchRmt")
    LiveData<String> GetBranchRemittanceAmount(String fsVal);

    @Query("SELECT IFNULL((SELECT SUM(nAmountxx) FROM LR_DCP_Remittance " +
            "WHERE sTransNox =:fsVal " +
            "AND cRemitTyp = '1'), 0.00) AS nBankRmtc")
    LiveData<String> GetBankRemittanceAmount(String fsVal);

    @Query("SELECT IFNULL((SELECT SUM(nAmountxx) FROM LR_DCP_Remittance " +
            "WHERE sTransNox =:fsVal " +
            "AND cRemitTyp = '2'), 0.00) AS nOtherRmt")
    LiveData<String> GetOtherRemittanceAmount(String fsVal);

    @Query("SELECT (SELECT SUM(nTranTotl) FROM LR_DCP_Collection_Detail WHERE sTransNox =:fsVal " +
            "AND cPaymForm = '0') - " +
            "IFNULL((SELECT SUM(nAmountxx) FROM LR_DCP_Remittance WHERE sTransNox =:fsVal " +
            "AND cPaymForm = '0'), 0.00) AS CASH_ON_HAND")
    LiveData<String> GetCashOnHand(String fsVal);

    @Query("SELECT IFNULL(" +
            "(SELECT " +
            "(SELECT SUM(nTranTotl) FROM LR_DCP_Collection_Detail WHERE sTransNox =:fsVal " +
            "AND cPaymForm = '1') - " +
            "IFNULL((SELECT SUM(nAmountxx) FROM LR_DCP_Remittance WHERE sTransNox =:fsVal " +
            "AND cPaymForm = '1'), 0.00)), 0.00) AS CASH_ON_HAND")
    LiveData<String> GetCheckOnHand(String fsVal);

    @Query("SELECT IFNULL(" +
            "(SELECT " +
            "(SELECT SUM(nTranTotl) FROM LR_DCP_Collection_Detail WHERE sTransNox =:fsVal " +
            "AND cPaymForm = '7') - " +
            "IFNULL((SELECT SUM(nAmountxx) FROM LR_DCP_Remittance WHERE sTransNox =:fsVal " +
            "AND cPaymForm = '7'), 0.00)), 0.00) AS CASH_ON_HAND")
    LiveData<String> GetEPayOnHand(String fsVal);

    @Query("SELECT * FROM LR_DCP_Remittance WHERE sTransNox=:TransNo AND nEntryNox =:EntryNo")
    EDCP_Remittance GetCollectionRemittance(String TransNo, String EntryNo);

    @Query("SELECT (SELECT SUM(nTranTotl) FROM LR_DCP_Collection_Detail WHERE sTransNox =:args) - " +
            "IFNULL((SELECT SUM(nAmountxx) FROM LR_DCP_Remittance WHERE sTransNox =:args), 0.00) " +
            "AS RMN_Collection")
    double ValidateRemittanceEntry(String args);
}
