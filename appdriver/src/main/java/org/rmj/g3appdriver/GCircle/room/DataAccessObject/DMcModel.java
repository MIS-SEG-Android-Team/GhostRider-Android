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

import org.rmj.g3appdriver.GCircle.room.Entities.EMCColor;
import org.rmj.g3appdriver.GCircle.room.Entities.EMCModelCashPrice;
import org.rmj.g3appdriver.GCircle.room.Entities.EMcModel;

import java.util.List;

@Dao
public interface DMcModel {

    @Insert
    void insert(EMcModel mcModel);

    @Insert
    void insert(EMCColor mcColor);

    @Insert
    void insert(EMCModelCashPrice mcPrice);

    @Update
    void update(EMcModel mcModel);

    @Update
    void update(EMCColor mcColor);

    @Update
    void update(EMCModelCashPrice mcPrice);

    @Query("SELECT * FROM MC_Model WHERE sModelIDx =:fsVal")
    EMcModel GetMCModel(String fsVal);

    @Query("SELECT * FROM MC_Model ORDER BY dTimeStmp DESC LIMIT 1")
    EMcModel GetLatestMCModel();

    @Query("SELECT * FROM MC_Model_Color ORDER BY dTimeStmp DESC LIMIT 1")
    EMCColor GetLatestMcColorTimeStamp();

    @Query("SELECT * FROM MC_Cash_Price WHERE sModelIDx=:ModelID")
    EMCModelCashPrice GetModelCashPriceInfo(String ModelID);

    @Query("SELECT * FROM MC_Cash_Price ORDER BY dPricexxx DESC LIMIT 1")
    EMCModelCashPrice GetLatestMcCashPrice();

    @Query("SELECT * FROM MC_Model_Color WHERE sModelIDx =:ModelID AND sColorIDx =:ColorID")
    EMCColor GetModelColor(String ModelID, String ColorID);

    @Query("SELECT * FROM Mc_Model WHERE sBrandIDx = :BrandID")
    LiveData<List<EMcModel>> getAllModeFromBrand(String BrandID);

    @Query("SELECT MAX(dTimeStmp) FROM Mc_Model")
    String getLatestDataTime();

    @Query("SELECT * FROM Mc_Model WHERE sModelIDx =:ModelID")
    EMcModel getModelInfo(String ModelID);



    @Query("SELECT  " +
            "a.nSelPrice, " +
            "a.nMinDownx, " +
            "b.nMiscChrg, " +
            "b.nRebatesx, " +
            "b.nEndMrtgg, " +
            "c.nAcctThru, " +
            "c.nFactorRt " +
            "FROM Mc_Model_Price a, MC_Category b, MC_Term_Category c, Mc_Model d " +
            "WHERE a.sMCCatIDx = b.sMCCatIDx " +
            "AND a.sMCCatIDx = c.sMCCatIDx " +
            "AND a.sModelIDx = d.sModelIDx " +
            "AND a.sModelIDx = :ModelID " +
            "AND c.nAcctThru = :Term")
    LiveData<McAmortInfo> GetMonthlyPayment(String ModelID, int Term);

    @Query("SELECT  " +
            "a.sModelIDx AS ModelIDx, " +
            "a.sModelNme AS ModelNme, " +
            "b.nMinDownx AS MinDownx, " +
            "c.nRebatesx AS Rebatesx, " +
            "c.nMiscChrg AS MiscChrg, " +
            "c.nEndMrtgg AS EndMrtgg, " +
            "b.nSelPrice AS SelPrice, " +
            "b.nLastPrce AS LastPrce " +
            "FROM Mc_Model a, Mc_Model_Price b, MC_Category c " +
            "WHERE a.sModelIDx = b.sModelIDx " +
            "AND b.cRecdStat = '1' " +
            "AND b.sMCCatIDx = c.sMCCatIDx " +
            "AND (a.sModelIDx = :ModelID)")
    LiveData<McDPInfo> getDownpayment(String ModelID);

    class McDPInfo{
        public String ModelIDx;
        public String ModelNme;
        public String Rebatesx;
        public String MiscChrg;
        public String EndMrtgg;
        public String MinDownx;
        public String SelPrice;
        public String LastPrce;
    }

    class McAmortInfo{
        public String nSelPrice;
        public String nMinDownx;
        public String nMiscChrg;
        public String nRebatesx;
        public String nEndMrtgg;
        public String nAcctThru;
        public String nFactorRt;
    }
}
