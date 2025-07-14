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
import org.rmj.g3appdriver.GCircle.room.Entities.EBankInfo;

import java.util.List;

@Dao
public interface DBankInfo {

    @Insert
    void SaveBankInfo(EBankInfo foVal);

    @Query("SELECT * FROM Bank_Info WHERE cRecdStat = 1")
    LiveData<List<EBankInfo>> getBankInfoList();

    @Query("SELECT * FROM Bank_Info WHERE sBankIDxx =:BankID")
    EBankInfo GetBankInfo(String BankID);

    @Query("SELECT sBankName FROM Bank_Info WHERE cRecdStat = 1")
    LiveData<String[]> getBankNameList();

    @Query("SELECT sBankName FROM Bank_Info WHERE cRecdStat = 1 AND sBankIDxx = :fsBankId")
    LiveData<String> getBankNameFromId(String fsBankId);
}
