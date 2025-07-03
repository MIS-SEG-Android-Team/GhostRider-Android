package org.rmj.g3appdriver.GConnect.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Update;

import org.rmj.g3appdriver.GConnect.room.Entities.ERedeemItemInfo;
import org.rmj.g3appdriver.GConnect.room.Entities.ERedeemablesInfo;

import java.util.List;

@Dao
public interface DRedeemablesInfo {

    @Insert
    void insert(ERedeemItemInfo foVal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBulkData(List<ERedeemablesInfo> foVal);

    @Update
    void update(ERedeemablesInfo redeemablesInfo);

    @Query("SELECT COUNT(sTransNox) FROM Redeemables")
    LiveData<Integer> countRedeemables();

    @Query("SELECT * FROM Redeem_Item WHERE sTransNox =:TransNox AND sPromoIDx=:PromoIDx")
    List<ERedeemItemInfo> getRedeemableIfExist(String TransNox, String PromoIDx);

    @Query("UPDATE Redeem_Item SET " +
            "nItemQtyx =:ItemQty " +
            "WHERE sTransNox=:TransNox AND sTransNox =:PromoIDx")
    void UpdateExistingItemOnCart(String TransNox, String PromoIDx, int ItemQty);

    @Query("SELECT COUNT(*) FROM Redeemables")
    int GetRedeemablesCount();

    @Query("SELECT COUNT(sPromoIDx) FROM Redeem_Item WHERE sGcardNox =:GCardNox AND cTranStat != 0 GROUP BY sReferNox")
    LiveData<Integer> getOrdersCount(String GCardNox);

    @Query("SELECT * FROM Redeem_Item WHERE sGcardNox =:GCardNox AND cTranStat != 0 GROUP BY sReferNox")
    LiveData<List<ERedeemItemInfo>> getOrdersList(String GCardNox);

    @Query("SELECT * FROM Redeemables WHERE strftime('%Y-%m-%d','now') BETWEEN dDateFrom AND IFNULL(dDateThru, strftime('%Y-%m-%d','now'))")
    LiveData<List<ERedeemablesInfo>> getRedeemablesList();

    @Query("SELECT * FROM Redeemables WHERE nPointsxx =:fsVal AND strftime('%Y-%m-%d','now') BETWEEN dDateFrom AND IFNULL(dDateThru, strftime('%Y-%m-%d','now'))")
    LiveData<List<ERedeemablesInfo>> getRedeemablesList(String fsVal);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT c.sReferNox, c.dOrderedx, b.sBranchNm, b.sAddressx, SUM(c.nPointsxx) as TotAmnt " +
            "FROM Redeem_Item c " +
            "LEFT JOIN BranchInfo b ON c.sBranchCd = b.sBranchCd " +
            "LEFT JOIN Redeemables a ON a.sTransNox = c.sPromoIDx " +
            "WHERE c.sGCardNox =:GCardNox " +
            "AND c.cPlcOrder = '1' " +
            "AND c.cTranStat = '1' " +
            "GROUP BY c.sReferNox")
    LiveData<List<TransactionOrder>> getTransactionOrderList(String GCardNox);


    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT " +
            "a.sPromoIDx as itemID, " +
            "b.sPromoDsc as itemName, " +
            "a.nItemQtyx as itemQtyx, " +
            "a.nPointsxx as itemPnts " +
            "FROM Redeem_Item a " +
            "LEFT JOIN Redeemables b " +
            "ON a.sPromoIDx = b.sTransNox " +
            "WHERE a.cTranStat = '1' " +
            "AND a.cPlcOrder = '1' " +
            "AND a.sGCardNox =:GCardNox " +
            "AND a.sReferNox =:ReferNox ")
    LiveData<List<OrderItems>> getOrderItems(String ReferNox, String GCardNox);

    class OrderItems {
        public String itemID;
        public String itemName;
        public String itemQtyx;
        public String itemPnts;
    }
    class TransactionOrder {
        public String TransNox;
        public String TotAmnt;
        public String Branchx;
        public String Address;
        public String dOrderx;

    }
}
