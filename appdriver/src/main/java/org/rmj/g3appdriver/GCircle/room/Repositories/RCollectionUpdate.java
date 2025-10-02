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

package org.rmj.g3appdriver.GCircle.room.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DAddressRequest;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DMobileRequest;
import org.rmj.g3appdriver.GCircle.room.Entities.EAddressUpdate;
import org.rmj.g3appdriver.GCircle.room.Entities.EMobileUpdate;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;

import java.util.List;

public class RCollectionUpdate {
    private static final String TAG = "DB_Collection";
    private final Application application;
    private final DAddressRequest addressDao;
    private final DMobileRequest mobileDao;

    public RCollectionUpdate(Application application){
        this.application = application;
        GGC_GCircleDB db = GGC_GCircleDB.getInstance(application);
        addressDao = db.AddressRequestDao();
        mobileDao = db.MobileRequestDao();
    }

    public LiveData<List<DMobileRequest.CNAMobileInfo>> getCNA_MobileDataList(String sClientID) {
        return mobileDao.getCNAMobileDataList(sClientID);
    }

    public LiveData<List<DAddressRequest.CNA_AddressInfo>> getCNA_AddressDataList(String sClientID) {
        return addressDao.getCNA_AddressDataList(sClientID);
    }

    public LiveData<List<EAddressUpdate>> getAddressList(){
        return addressDao.getAddressRequestList();
    }

    public LiveData<List<EMobileUpdate>> getMobileList(){
        return mobileDao.getMobileRequestList();
    }

    public LiveData<List<EMobileUpdate>> getMobileListForClient(String ClientID){
        return mobileDao.getMobileRequestListForClient(ClientID);
    }

    public EAddressUpdate getAddressUpdateInfoForPosting(String ClientID){
        return addressDao.getAddressUpdateInfoForPosting(ClientID);
    }

    public EMobileUpdate getMobileUpdateInfoForPosting(String ClientID){
        return mobileDao.getMobileUpdateInfoForPosting(ClientID);
    }
}
