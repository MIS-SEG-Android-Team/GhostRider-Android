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
import androidx.room.Query;
import androidx.room.Update;

import org.rmj.g3appdriver.GCircle.room.Entities.EClientUpdate;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;

import java.util.List;

@Dao
public interface DDCPCollectionDetail {

    @Update
    void update(EDCPCollectionDetail foval);

    @Query("UPDATE LR_DCP_Collection_Detail " +
            "SET sRemCodex =:RemCode, " +
            "sRemarksx =:Remarks, " +
            "cSendStat = '0', " +
            "cTranStat = '1', " +
            "sImageNme = (SELECT a.sImageNme FROM Image_Information a LEFT JOIN LR_DCP_Collection_Detail b  ON a.sSourceNo = b.sTransNox AND a.sDtlSrcNo = sAcctNmbr WHERE a.sSourceNo = b.sTransNox AND b.nEntryNox =:EntryNox), " +
            "nLongitud = (SELECT a.nLongitud FROM Image_Information a LEFT JOIN LR_DCP_Collection_Detail b  ON a.sDtlSrcNo = sAcctNmbr WHERE a.sSourceNo = b.sTransNox), " +
            "nLatitude = (SELECT a.nLatitude FROM Image_Information a LEFT JOIN LR_DCP_Collection_Detail b  ON a.sDtlSrcNo = sAcctNmbr WHERE a.sSourceNo = b.sTransNox), " +
            "dModified =:DateModified " +
            "WHERE sTransNox = (SELECT sTransNox " +
            "FROM LR_DCP_Collection_Master ORDER BY dReferDte DESC LIMIT 1) " +
            "AND nEntryNox =:EntryNox")
    void updateCollectionDetailInfo(int EntryNox, String RemCode, String Remarks, String DateModified);

    /**
     *
     * @param TransNox transaction number of master detail
     * @param EntryNox specific entry number of collection detail
     * @param DateEntry current date time of update.
     */
    @Query("UPDATE LR_DCP_Collection_Detail " +
            "SET cSendStat='1', " +
            "cTranstat = '2', " +
            "dSendDate =:DateEntry, " +
            "dModified =:DateEntry " +
            "WHERE sTransNox =:TransNox " +
            "AND nEntryNox =:EntryNox")
    void updateCollectionDetailStatus(String TransNox, int EntryNox, String DateEntry);

    @Query("SELECT COUNT(*) FROM LR_DCP_Collection_Detail WHERE cSendStat = '0' AND sTransNox =:TransNox")
    int getUnsentCollectionDetail(String TransNox);

    @Query("SELECT cSendStat FROM LR_DCP_Collection_Master WHERE sTransNox =:TransNox")
    String getMasterSendStatus(String TransNox);

    @Query("SELECT sTransNox FROM LR_DCP_Collection_Master WHERE cSendStat IS NULL ORDER BY dReferDte DESC LIMIT 1")
    String getUnpostedDcpMaster();

    @Query("SELECT * FROM Client_Update_Request WHERE sDtlSrcNo = :AccountNox")
    LiveData<EClientUpdate> getClient_Update_Info(String AccountNox);

    @Query("SELECT * FROM LR_DCP_Collection_Detail " +
            "WHERE sTransNox = :TransNox " +
            "AND nEntryNox = :EntryNox")
    LiveData<EDCPCollectionDetail> getCollectionDetail(String TransNox, int EntryNox);

    @Query("SELECT * FROM LR_DCP_Collection_Detail WHERE cSendStat <> '1' AND sRemCodex == 'PAY'")
    List<EDCPCollectionDetail> getUnsentPaidCollection();

    @Query("SELECT * FROM LR_DCP_Collection_Detail " +
            "WHERE sTransNox = :TransNox " +
            "AND sAcctNmbr = :Acctnox " +
            "AND sRemCodex = :RemCode " +
            "AND cTranStat = 2")
    LiveData<EDCPCollectionDetail> getPostedCollectionDetail(String TransNox, String Acctnox, String RemCode);

    @Query("SELECT * FROM LR_DCP_Collection_Detail WHERE sTransNox =:TransNox AND sAcctNmbr=:Account")
    EDCPCollectionDetail getCollectionDetail(String TransNox, String Account);

    @Query("SELECT * FROM LR_DCP_Collection_Detail WHERE cSendStat <> '1'")
    List<EDCPCollectionDetail> getLRDCPCollectionForPosting();

}
