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

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import org.rmj.g3appdriver.GCircle.room.Entities.EClientUpdate;

@Dao
public interface DClientUpdate {

    @Insert
    void SaveClientUpdate(EClientUpdate clientUpdate);

    @Query("SELECT COUNT (*) FROM Client_Update_Request")
    int GetRowsCountForID();

    @Query("SELECT * FROM Client_Update_Request WHERE sSourceNo =:TransNox AND sDtlSrcNo =:AcctNox")
    EClientUpdate getClientUpdateInfoForPosting(String TransNox, String AcctNox);

}
