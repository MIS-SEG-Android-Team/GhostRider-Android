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
import androidx.room.Query;

import org.rmj.g3appdriver.GCircle.room.Entities.EMobileUpdate;

import java.util.List;

@Dao
public interface DMobileRequest {

    @Query("SELECT * FROM Mobile_Update_Request")
    LiveData<List<EMobileUpdate>> getMobileRequestList();

    @Query("SELECT * FROM Mobile_Update_Request WHERE sClientID =:ClientID")
    LiveData<List<EMobileUpdate>> getMobileRequestListForClient(String ClientID);

    @Query("SELECT * FROM Mobile_Update_Request WHERE sClientID =:ClientID")
    EMobileUpdate getMobileUpdateInfoForPosting(String ClientID);

    @Query("SELECT " +
            " M.sMobileNo," +
            " M.cPrimaryx AS mobilePrimaryx," +
            " M.sRemarksx AS mobileRemarksx," +
            " I.nLatitude," +
            " I.nLongitud" +
            " FROM LR_DCP_Collection_Detail as C," +
            " Mobile_Update_Request as M," +
            " Image_Information as I" +
            " WHERE C.sClientID = :sClientID AND" +
            " C.sRemCodex = \"CNA\" AND" +
            " M.sClientID = C.sClientID AND" +
            " I.sImageNme = C.sImageNme")
    LiveData<List<CNAMobileInfo>> getCNAMobileDataList(String sClientID);

    class CNAMobileInfo {
        public String sMobileNo;
        public String mobilePrimaryx;
        public String mobileRemarksx;
        public double nLatitude;
        public double nLongitud;
    }
}
