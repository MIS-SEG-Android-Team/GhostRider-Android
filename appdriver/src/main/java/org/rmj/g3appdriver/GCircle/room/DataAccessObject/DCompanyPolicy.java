package org.rmj.g3appdriver.GCircle.room.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import org.rmj.g3appdriver.GCircle.room.Entities.EArticleDetails;
import org.rmj.g3appdriver.GCircle.room.Entities.EArticleHead;
import org.rmj.g3appdriver.GCircle.room.Entities.EPolicyContents;
import org.rmj.g3appdriver.GCircle.room.Entities.EPolicyMenus;

import java.util.List;

@Dao
public interface DCompanyPolicy {

    @Upsert
    void savePolicyMenus(EPolicyMenus foVal);

    @Upsert
    void savePolicyContents(EPolicyContents foVal);

    @Query("SELECT * FROM Policy_Menus")
    LiveData<List<EPolicyMenus>> getPolicyMenus();

    @Query("SELECT * FROM Policy_Contents WHERE sParentIDxx =:fsVal")
    LiveData<List<EPolicyContents>> getPolicyContents(String fsVal);
}
