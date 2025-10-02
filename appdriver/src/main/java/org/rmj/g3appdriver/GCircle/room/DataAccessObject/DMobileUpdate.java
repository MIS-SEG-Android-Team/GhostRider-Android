package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import org.rmj.g3appdriver.GCircle.room.Entities.EMobileUpdate;

import java.util.List;

@Dao
public interface DMobileUpdate {

    @Insert
    void SaveMobileUpdate(EMobileUpdate foVal);

    @Query("DELETE FROM Mobile_Update_Request WHERE sTransNox =:fsVal")
    void DeleteMobileUpdate(String fsVal);

    @Query("SELECT COUNT (*) FROM Mobile_Update_Request")
    int GetRowsCountForID();

    @Query("SELECT * FROM Mobile_Update_Request WHERE sClientID =:fsVal")
    EMobileUpdate GetMobileUpdate(String fsVal);

    @Query("SELECT sTransNox, " +
            "sMobileNo, " +
            "cPrimaryx " +
            "FROM Mobile_Update_Request WHERE sClientID =:fsVal")
    LiveData<List<MobileUpdateInfo>> GetMobileUpdateInfo(String fsVal);

    class MobileUpdateInfo{
        public String sTransNox;
        public String sMobileNo;
        public String cPrimaryx;
    }
}
