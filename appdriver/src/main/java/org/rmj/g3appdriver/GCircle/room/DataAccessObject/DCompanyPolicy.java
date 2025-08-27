package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import org.rmj.g3appdriver.GCircle.room.Entities.EPolicyContents;
import org.rmj.g3appdriver.GCircle.room.Entities.EPolicyItems;
import org.rmj.g3appdriver.GCircle.room.Entities.EPolicyMenus;

import java.util.List;

@Dao
public interface DCompanyPolicy {

    @Insert
    void savePolicyMenus(EPolicyMenus foVal);

    @Insert
    void savePolicyItems(EPolicyItems foVal);

    @Insert
    void savePolicyContents(EPolicyContents foVal);

    @Query("SELECT * FROM Policy_Menus")
    List<EPolicyMenus> getPolicyMenus();

    @Query("SELECT * FROM Policy_Items WHERE sParentIDxx = :fsVal")
    List<EPolicyItems> getPolicyItems(String fsVal);

    @Query("SELECT * FROM Policy_Contents WHERE sParentIDxx =:fsVal")
    List<EPolicyContents> getPolicyContents(String fsVal);
}
