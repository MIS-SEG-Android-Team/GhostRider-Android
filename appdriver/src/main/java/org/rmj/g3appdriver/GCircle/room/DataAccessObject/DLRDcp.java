package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCP_Remittance;
import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;

import java.util.List;

@Dao
public interface DLRDcp {

    @Insert
    void SaveDcpMaster(EDCPCollectionMaster foVal);

    @Insert
    void SaveDcpDetail(EDCPCollectionDetail foVal);

    @Query("SELECT * FROM LR_DCP_Collection_Detail WHERE sTransNox = " +
            "(SELECT sTransNox FROM LR_DCP_Collection_Master WHERE cSendStat IS NULL) " +
            "AND cTranStat != '2' ORDER BY dModified ASC")
    LiveData<List<EDCPCollectionDetail>> GetCollectionList();

    @Query("SELECT * FROM LR_DCP_Collection_Detail WHERE sTransNox=:TransNox AND sAcctNmbr =:AccountNo AND nEntryNox =:EntryNo")
    LiveData<EDCPCollectionDetail> GetCollectionDetailForTransaction(String TransNox, String AccountNo, String EntryNo);

    @Update
    void UpdateCollectionDetail(EDCPCollectionDetail foVal);

    @Query("SELECT sEmployID FROM User_Info_Master")
    String GetEmployID();

    @Query("SELECT COUNT (*) + 1 AS nEntryNox FROM LR_DCP_Collection_Detail")
    int GetNewEntryNox();

    @Query("SELECT * FROM LR_DCP_Collection_Detail WHERE sTransNox=:TransNox AND sAcctNmbr =:AccountNo")
    EDCPCollectionDetail GetCollectionForValidation(String TransNox, String AccountNo);

    @Query("SELECT * FROM LR_DCP_Collection_Master WHERE sTransNox =:TransNox")
    EDCPCollectionMaster GetMaster(String TransNox);

    @Query("SELECT * FROM LR_DCP_Collection_Detail WHERE sTransNox=:TransNox AND nEntryNox=:EntryNo AND sAcctNmbr=:AcctNox")
    EDCPCollectionDetail GetCollectionDetail(String TransNox, String EntryNo, String AcctNox);

    @Query("SELECT * FROM LR_DCP_Collection_Master WHERE cSendStat IS NULL")
    EDCPCollectionMaster GetColletionMasterForPosting();

    @Query("SELECT * FROM LR_DCP_Collection_Master " +
            "WHERE dReferDte =:fsVal")
    LiveData<EDCPCollectionMaster> GetMasterCollectionForDate(String fsVal);

    @Query("SELECT * FROM LR_DCP_Collection_Detail WHERE sTransNox=:fsVal AND cTranStat == '2'")
    LiveData<List<EDCPCollectionDetail>> GetCollectionDetailForPreview(String fsVal);

    @Query("SELECT * FROM LR_DCP_Collection_Detail WHERE sTransNox =:TransNox AND cSendStat <> '1'")
    List<EDCPCollectionDetail> GetCollectionDetailForPosting(String TransNox);

    @Query("SELECT * FROM Image_Information WHERE sSourceNo =:TransNox AND sDtlSrcNo=:AccntNo")
    EImageInfo GetDcpImageForPosting(String TransNox, String AccntNo);

    @Query("UPDATE LR_DCP_Collection_Master SET cSendStat = '1', cTranStat = '2', dSendDate =:DatePostd, dModified =:DatePostd WHERE sTransNox =:TransNox")
    void UpdatePostedDcpMaster(String TransNox, String DatePostd);

    @Query("SELECT * FROM  LR_DCP_Collection_Master ORDER BY dSendDate, dModified DESC LIMIT 1")
    LiveData<EDCPCollectionMaster> GetLatestPostedDcp();

    @Query("SELECT * FROM LR_DCP_Collection_Detail WHERE sTransNox =:TransNox AND sRemCodex == ''")
    List<EDCPCollectionDetail> GetNotVisitedCollection(String TransNox);

    @Query("SELECT COUNT(*) FROM LR_DCP_Collection_Detail WHERE sTransNox =:TransNox AND sRemCodex = ''")
    int CheckForNotVisitedCollection(String TransNox);

    @Query("SELECT SUM(nTranTotl) FROM LR_DCP_Collection_Detail " +
            "WHERE sTransNox =:fsVal " +
            "AND sCheckNox == '' AND sCheckDte == '' AND sCheckAct == ''")
    double GetCollectedCashPayments(String fsVal);

    @Query("SELECT SUM(nTranTotl) FROM LR_DCP_Collection_Detail " +
            "WHERE sTransNox =:fsVal " +
            "AND sCheckNox <> '' AND sCheckDte <> '' AND sCheckAct <> ''")
    double GetCollectedCheckPayments(String fsVal);

    @Query("SELECT * FROM LR_DCP_Remittance WHERE sTransNox =:fsVal")
    List<EDCP_Remittance> GetCollectionRemittance(String fsVal);

    @Query("SELECT * FROM LR_DCP_Collection_Detail WHERE sTransNox =:fsVal AND sRemCodex = 'PAY'")
    List<EDCPCollectionDetail> GetPaidCollections(String fsVal);

    @Query("SELECT SUM(nAmountxx) FROM LR_DCP_Remittance WHERE sTransNox=:fsVal")
    double GetRemittedCollection(String fsVal);

    @Query("SELECT SUM(nAmountxx) FROM LR_DCP_Remittance " +
            "WHERE sTransNox =:fsVal " +
            "AND cPaymForm ='0'")
    double GetCashRemittedCollection(String fsVal);

    @Query("SELECT SUM(nAmountxx) FROM LR_DCP_Remittance " +
            "WHERE sTransNox =:fsVal " +
            "AND cPaymForm ='1'")
    double GetCheckRemittedCollection(String fsVal);

    @Query("SELECT * FROM LR_DCP_Collection_Master WHERE cSendStat IS NULL")
    LiveData<EDCPCollectionMaster> GetColletionMasterForRemittance();

    @Query("DELETE FROM LR_DCP_Collection_Master")
    void ClearMasterDCP();

    @Query("DELETE FROM LR_DCP_Collection_Detail")
    void ClearDetailDCP();

    @Query("DELETE FROM LR_DCP_Remittance")
    void ClearDCPRemittance();
}
