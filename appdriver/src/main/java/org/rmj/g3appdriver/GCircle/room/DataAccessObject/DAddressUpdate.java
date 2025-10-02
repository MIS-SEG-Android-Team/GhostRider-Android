package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import org.rmj.g3appdriver.GCircle.room.Entities.EAddressUpdate;

import java.util.List;

@Dao
public interface DAddressUpdate {

    @Insert
    void SaveAddressUpdate(EAddressUpdate foVal);

    @Query("DELETE FROM Address_Update_Request WHERE sTransNox =:fsVal")
    void DeleteAddressUpdate(String fsVal);

    @Query("SELECT * FROM Address_Update_Request WHERE sClientID =:fsVal")
    EAddressUpdate GetAddressInfo(String fsVal);

    @Query("SELECT COUNT (*) FROM Address_Update_Request")
    int GetRowsCountForID();

    @Query("SELECT " +
            "a.sTransNox, " +
            "a.cAddrssTp, " +
            "IFNULL(a.sHouseNox, '') AS sHouseNox, " +
            "IFNULL(a.sAddressx, '') AS sAddressx," +
            "b.sBrgyName, " +
            "c.sTownName, " +
            "d.sProvName, " +
            "a.cPrimaryx " +
            "FROM Address_Update_Request a " +
            "LEFT JOIN Barangay_Info b ON a.sBrgyIDxx = b.sBrgyIDxx " +
            "LEFT JOIN Town_Info c ON b.sTownIDxx = c.sTownIDxx " +
            "LEFT JOIN Province_Info d ON c.sProvIDxx = d.sProvIDxx " +
            "WHERE a.sClientID =:fsVal")
    LiveData<List<AddressUpdateInfo>> GetAddressUpdateInfo(String fsVal);

    class AddressUpdateInfo{
        public String sTransNox;
        public String cAddrssTp;
        public String sHouseNox;
        public String sAddressx;
        public String sBrgyName;
        public String sTownName;
        public String sProvName;
        public String cPrimaryx;
    }
}
