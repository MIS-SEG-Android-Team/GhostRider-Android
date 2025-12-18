package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.ECASRequests;

import java.util.List;

@Dao
public interface DCASRequests {

    @Upsert
    void SaveCASRequest(ECASRequests requests);

    @Query("UPDATE CAS_Requests SET cTranStat = :fsStat, dApproved = :fdApproved, sAppSrcNo = :fsAppSrcNo  WHERE sTransNox = :fsTransNox ")
    void UpdateRequest(String fsTransNox, String fsStat, String fdApproved, String fsAppSrcNo);

    @Query("SELECT * FROM CAS_Requests WHERE cTranStat = '0' AND sSourceCD = :sSource AND dTransact BETWEEN :dFrom AND :dTo ORDER BY sTransNox ASC, dTransact DESC")
    LiveData<List<ECASRequests>> GetRequests(String sSource, String dFrom, String dTo);

    @Query("SELECT * FROM CAS_Requests WHERE cTranStat <> '0' AND sSourceCD = :sSource AND dTransact BETWEEN :dFrom AND :dTo ORDER BY sTransNox ASC, dTransact DESC")
    LiveData<List<ECASRequests>> GetHistory(String sSource, String dFrom, String dTo);

    @Query("SELECT * FROM CAS_Requests WHERE sTransNox = :sTransNox")
    LiveData<ECASRequests> GetRequestDetail(String sTransNox);
}
