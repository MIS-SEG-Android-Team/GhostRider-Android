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
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import org.rmj.g3appdriver.GCircle.room.Entities.ECodeApproval;
import org.rmj.g3appdriver.GCircle.room.Entities.EEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ESCARqstEmp;
import org.rmj.g3appdriver.GCircle.room.Entities.ESCA_Request;

import java.util.List;

@Dao
public interface DApprovalCode {

    @Insert
    void SaveSCARequest(ESCA_Request foVal);

    @Insert
    void insert(ECodeApproval codeApproval);

    @Query("DELETE FROM xxxSCA_Request")
    void clear();

    @Update
    void update(ECodeApproval codeApproval);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBulkData(List<ESCA_Request> requestList);

    @Query("SELECT * FROM XXXSCA_REQUEST WHERE sSCACodex =:TransNox")
    ESCA_Request GetApprovalCode(String TransNox);

    @Query("SELECT * FROM User_Info_Master")
    EEmployeeInfo GetUserInfo();

    @Query("SELECT * FROM System_Code_Approval WHERE sApprCode =:fsVal AND cSendxxxx != '1'")
    ECodeApproval GetCodeApproval(String fsVal);

    @RawQuery(observedEntities = ESCA_Request.class)
    LiveData<List<ESCA_Request>> getAuthorizedFeatures(SupportSQLiteQuery sqLiteQuery);

    @Query("SELECT sSCATitle FROM xxxSCA_Request WHERE sSCACodex =:AppCode")
    LiveData<String> getApprovalDesc(String AppCode);

    @Query("SELECT * FROM System_Code_Approval WHERE cSendxxxx = '0'")
    List<ECodeApproval> GetSystemApprovalForUploading();

    @Query("UPDATE System_Code_Approval SET sTransNox=:NTransNo,  cSendxxxx = '1' WHERE sTransNox =:TransNox")
    void UpdateUploaded(String TransNox, String NTransNo);
}
