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

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DDCPCollectionDetail;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DDCPCollectionMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.EClientUpdate;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.etc.AppConstants;

import java.util.List;

public class RDailyCollectionPlan {
    private static final String TAG = "DB_CollectionDetail";
    private final DDCPCollectionDetail detailDao;
    private final DDCPCollectionMaster masterDao;
    private final Application application;

    public RDailyCollectionPlan(Application application){
        this.application = application;
        GGC_GCircleDB GGCGriderDB = GGC_GCircleDB.getInstance(application);
        detailDao = GGCGriderDB.DcpDetailDao();
        masterDao = GGCGriderDB.DcpMasterDao();
    }

    public void updateSentPostedDCPMaster(String TransNox){
        masterDao.updateSentPostedDCPMaster(TransNox, AppConstants.DATE_MODIFIED());
    }

    public List<EDCPCollectionDetail> getUnsentPaidCollection() throws Exception{
        return detailDao.getUnsentPaidCollection();
    }

    public LiveData<EClientUpdate> getClientUpdateInfo(String AccountNox){
        return detailDao.getClient_Update_Info(AccountNox);
    }

    public LiveData<EDCPCollectionDetail> getCollectionDetail(String TransNox, int EntryNox){
        return detailDao.getCollectionDetail(TransNox, EntryNox);
    }

    public void updateCollectionDetailInfo(EDCPCollectionDetail collectionDetail){
        detailDao.update(collectionDetail);
    }

    public void updateCollectionDetail(int EntryNox, String RemCode, String Remarks){
        detailDao.updateCollectionDetailInfo(EntryNox, RemCode, Remarks, AppConstants.DATE_MODIFIED());
    }

    public void updateCollectionDetailStatus(String TransNox, int EntryNox){
        detailDao.updateCollectionDetailStatus(TransNox, EntryNox, AppConstants.DATE_MODIFIED());
    }

    public int getUnsentCollectionDetail(String TransNox){
        return detailDao.getUnsentCollectionDetail(TransNox);
    }

    public String getMasterSendStatus(String TransNox){
        return detailDao.getMasterSendStatus(TransNox);
    }

    public String getUnpostedDcpMaster(){
        return detailDao.getUnpostedDcpMaster();
    }

    public LiveData<EDCPCollectionDetail> getPostedCollectionDetail(String TransNox, String Acctnox, String RemCode) {
        return detailDao.getPostedCollectionDetail(TransNox, Acctnox, RemCode);
    }

    public EDCPCollectionDetail getCollectionDetail(String TransNox, String Account){
        return detailDao.getCollectionDetail(TransNox, Account);
    }

    public List<EDCPCollectionDetail> getLRDCPCollectionForPosting(){
        return detailDao.getLRDCPCollectionForPosting();
    }

}
