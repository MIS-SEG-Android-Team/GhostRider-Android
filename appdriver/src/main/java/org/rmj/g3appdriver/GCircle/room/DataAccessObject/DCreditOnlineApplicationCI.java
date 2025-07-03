package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.rmj.g3appdriver.GCircle.room.Entities.EBranchLoanApplication;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditOnlineApplicationCI;
import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;

import java.util.List;

@Dao
public interface DCreditOnlineApplicationCI {

    @Insert
    void SaveApplicationInfo(ECreditOnlineApplicationCI foCI);

    @Update
    void UpdateApplication(ECreditOnlineApplicationCI foVal);

    @Query("SELECT * FROM Credit_Online_Application_CI " +
            "WHERE cUploaded != '0' " +
            "AND cRcmdtnx1 IS NOT NULL " +
            "AND cSendStat == '0'")
    List<ECreditOnlineApplicationCI> GetCIRecommendationsForUpload();

    @Query("SELECT * FROM Credit_Online_Application_CI WHERE sTransNox=:fsVal")
    ECreditOnlineApplicationCI GetApplication(String fsVal);

    @Query("SELECT * FROM Credit_Online_Application_CI WHERE sTransNox=:TransNox")
    ECreditOnlineApplicationCI getApplication(String TransNox);

    @Query("SELECT * FROM Credit_Online_Application_CI WHERE sTransNox=:TransNox")
    LiveData<ECreditOnlineApplicationCI> getApplications(String TransNox);

    @Query("SELECT * FROM Credit_Online_Application_CI WHERE sTransNox=:TransNox")
    LiveData<ECreditOnlineApplicationCI> RetrieveApplicationData(String TransNox);

    @Query("Update Credit_Online_Application_CI SET cSendStat = '1' WHERE sTransNox =:TransNox")
    void UpdateTransactionSendStat(String TransNox);

    @Query("SELECT sAddrFndg FROM Credit_Online_Application_CI WHERE sTransNox =:TransNox")
    String getAddressForEvaluation(String TransNox);

    @Query("SELECT sAsstFndg FROM Credit_Online_Application_CI WHERE sTransNox =:TransNox")
    String getAssetsForEvaluation(String TransNox);

    @Query("SELECT sEmployID FROM User_Info_Master")
    String GetEmployeeID();

    @Query("SELECT sIncmFndg FROM Credit_Online_Application_CI WHERE sTransNox =:TransNox")
    String getIncomeForEvaluation(String TransNox);

    @Query("UPDATE Credit_Online_Application_CI SET sAddrFndg=:Findings WHERE sTransNox =:TransNox")
    void updateAddressEvaluation(String TransNox, String Findings);

    @Query("UPDATE Credit_Online_Application_CI SET sAsstFndg=:Findings WHERE sTransNox =:TransNox")
    void updateAssetEvaluation(String TransNox, String Findings);

    @Query("UPDATE Credit_Online_Application_CI SET sIncmFndg=:Findings WHERE sTransNox =:TransNox")
    void updateIncomeEvaluation(String TransNox, String Findings);

    @Query("UPDATE Credit_Online_Application_CI SET sNeighBr1 =:val WHERE sTransNox =:TransNox")
    void UpdateNeighbor1(String TransNox, String val);

    @Query("UPDATE Credit_Online_Application_CI SET sNeighBr2 =:val WHERE sTransNox =:TransNox")
    void UpdateNeighbor2(String TransNox, String val);

    @Query("UPDATE Credit_Online_Application_CI SET sNeighBr3 =:val WHERE sTransNox =:TransNox")
    void UpdateNeighbor3(String TransNox, String val);

    @Query("SELECT * FROM Image_Information WHERE sSourceNo =:args AND sDtlSrcNo =:args1 ORDER BY dCaptured DESC")
    List<EImageInfo> GetCIImagesForPosting(String args, String args1);

    @Query("UPDATE Credit_Online_Application_CI SET " +
            "dRcmdtnx1 =:DateApp, " +
            "cRcmdtnx1 =:fsResult, " +
            "sRcmdtnx1 =:fsRemarks, " +
            "cSendStat = '0', " +
            "sApproved = (SELECT sEmployID FROM User_Info_Master), " +
            "dApproved =:DateApp " +
            "WHERE sTransNox =:TransNox")
    void SaveCIApproval(String TransNox, String fsResult, String fsRemarks, String DateApp);

    @Query("UPDATE Credit_Online_Application_CI SET " +
            "cRcmdtnx2 =:fsResult, " +
            "sRcmdtnx2 =:fsRemarks, " +
            "cSendStat = '0' " +
            "WHERE sTransNox =:TransNox")
    void SaveBHApproval(String TransNox, String fsResult, String fsRemarks);

    @Query("SELECT a.sTransNox, " +
            "a.sCredInvx, " +
            "a.sAddressx, " +
            "a.sAddrFndg, " +
            "a.sAssetsxx, " +
            "a.sAsstFndg, " +
            "a.sIncomexx, " +
            "a.sIncmFndg, " +
            "a.cHasRecrd, " +
            "a.sRecrdRem, " +
            "b.sCompnyNm AS sClientNm, " +
            "b.dTransact, " +
            "c.sBranchNm, " +
            "b.nDownPaym, " +
            "a.sRcmdtnx1, " +
            "CASE " +
            "WHEN a.cRcmdtnx1 IS NULL " +
            "THEN 'N/A' " +
            "WHEN a.cRcmdTnx1 > 0 " +
            "THEN 'Approved' ELSE 'Disapproved' END AS cRcmdtnx1 " +
            "FROM CREDIT_ONLINE_APPLICATION_CI a " +
            "LEFT JOIN Credit_Online_Application_List b " +
            "ON a.sTransNox = b.sTransNox " +
            "LEFT JOIN Branch_Info c " +
            "ON b.sBranchCd = c.sBranchCd " +
            "WHERE a.cRcmdtnx1 isNull OR " +
            "a.cRcmdtnx1 == '' OR a.cRcmdtnx1 == 'null'")
    LiveData<List<oDataEvaluationInfo>> getForEvaluationListData();

    @Query("SELECT a.sTransNox, " +
            "a.sCredInvx, " +
            "a.sAddressx, " +
            "a.sAddrFndg, " +
            "a.sAssetsxx, " +
            "a.sAsstFndg, " +
            "a.sIncomexx, " +
            "a.sIncmFndg, " +
            "a.cHasRecrd, " +
            "a.sRecrdRem, " +
            "b.sCompnyNm AS sClientNm, " +
            "b.dTransact, " +
            "c.sBranchNm, " +
            "b.nDownPaym, " +
            "a.sRcmdtnx1, " +
            "CASE " +
            "WHEN a.cRcmdtnx1 IS NULL " +
            "THEN 'N/A' " +
            "WHEN a.cRcmdTnx1 > 0 " +
            "THEN 'Approved' ELSE 'Disapproved' END AS cRcmdtnx1 " +
            "FROM CREDIT_ONLINE_APPLICATION_CI a " +
            "LEFT JOIN Credit_Online_Application_List b " +
            "ON a.sTransNox = b.sTransNox " +
            "LEFT JOIN Branch_Info c " +
            "ON b.sBranchCd = c.sBranchCd " +
            "WHERE a.cRcmdtnx1 notNull")
    LiveData<List<oDataEvaluationInfo>> getForEvaluationListDataPreview();

    @Query("SELECT a.sTransNox, " +
            "a.sCredInvx, " +
            "a.sAddressx, " +
            "a.sAddrFndg, " +
            "a.sAssetsxx, " +
            "a.sAsstFndg, " +
            "a.sIncomexx, " +
            "a.sIncmFndg, " +
            "a.cHasRecrd, " +
            "a.sRecrdRem, " +
            "b.sCompnyNm AS sClientNm, " +
            "b.dTransact, " +
            "c.sBranchNm, " +
            "b.nDownPaym, " +
            "a.sRcmdtnx1, " +
            "CASE " +
            "WHEN a.cRcmdtnx1 IS NULL " +
            "THEN 'N/A' " +
            "WHEN a.cRcmdTnx1 > 0 " +
            "THEN 'Approved' ELSE 'Disapproved' END AS cRcmdtnx1 " +
            "FROM CREDIT_ONLINE_APPLICATION_CI a " +
            "LEFT JOIN Credit_Online_Application_List b " +
            "ON a.sTransNox = b.sTransNox " +
            "LEFT JOIN Branch_Info c " +
            "ON b.sBranchCd = c.sBranchCd " +
            "WHERE a.sTransNox=:TransNox")
    LiveData<oDataEvaluationInfo> getForEvaluateInfo(String TransNox);

    @Insert
    void SaveNewRecord(EBranchLoanApplication foVal);

    @Query("SELECT * FROM Credit_Online_Application_List WHERE sTransNox =:TransNox")
    EBranchLoanApplication CheckIFExist(String TransNox);

    @Update
    void UpdateExistingRecord(EBranchLoanApplication foVal);

    @Query("UPDATE Credit_Online_Application_CI SET cUploaded = '1' WHERE sTransNox=:TransNox")
    void UpdateUploadedResult(String TransNox);

    class oDataEvaluationInfo {
        public String sTransNox;
        public String sCredInvx;
        public String sAddressx;
        public String sAddrFndg;
        public String sAssetsxx;
        public String sAsstFndg;
        public String sIncomexx;
        public String sIncmFndg;
        public String cHasRecrd;
        public String sRecrdRem;
        public String sClientNm;
        public String dTransact;
        public String sBranchNm;
        public String nDownPaym;
        public String sRcmdtnx1;
        public String cRcmdtnx1;
    }
}
