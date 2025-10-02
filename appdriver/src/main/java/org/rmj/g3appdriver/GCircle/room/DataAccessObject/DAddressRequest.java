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

import org.rmj.g3appdriver.GCircle.room.Entities.EAddressUpdate;

import java.util.List;

@Dao
public interface DAddressRequest {

    @Query("SELECT * FROM Address_Update_Request WHERE cSendStat <> '1'")
    LiveData<List<EAddressUpdate>> getAddressRequestList();

    @Query("SELECT * FROM Address_Update_Request WHERE sClientID=:ClientID")
    EAddressUpdate getAddressUpdateInfoForPosting(String ClientID);

    @Query("SELECT " +
            " A.cAddrssTp," +
            " A.sHouseNox," +
            " A.sAddressx," +
            " T.sTownName AS townName," +
            " B.sBrgyName AS brgyName," +
            " P.sProvName AS provName," +
            " A.cPrimaryx AS addressPrimaryx," +
            " A.sRemarksx AS addressRemarksx," +
            " A.nLatitude," +
            " A.nLongitud " +
            "FROM LR_DCP_Collection_Detail as C," +
            " Address_Update_Request as A ," +
            " Barangay_Info as B," +
            " Town_Info as T," +
            " Province_Info as P " +
            "WHERE C.sRemCodex = \"CNA\" AND" +
            " C.sClientID = :sClientID AND A.sClientID = C.sClientID AND" +
            " B.sBrgyIDxx = A.sBrgyIDxx AND" +
            " T.sTownIDxx = A.sTownIDxx AND" +
            " P.sProvIDxx = T.sProvIDxx; ")
    LiveData<List<CNA_AddressInfo>> getCNA_AddressDataList(String sClientID);

    class CNA_AddressInfo {
        public String cAddrssTp;
        public String sHouseNox;
        public String sAddressx;
        public String townName;
        public String brgyName;
        public String provName;
        public String addressPrimaryx;
        public String addressRemarksx;
        public double nLatitude;
        public double nLongitud;
    }

}
