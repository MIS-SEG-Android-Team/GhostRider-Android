/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.dailyCollectionPlan
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.ViewModel;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.GCircle.Apps.Dcp.obj.REMIT;
import org.rmj.g3appdriver.GCircle.room.Entities.EAddressUpdate;
import org.rmj.g3appdriver.GCircle.room.Entities.EBranchInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EMobileUpdate;
import org.rmj.g3appdriver.GCircle.room.Repositories.RBankInfo;
import org.rmj.g3appdriver.lib.Etc.Branch;
import org.rmj.g3appdriver.GCircle.room.Repositories.RCollectionUpdate;
import org.rmj.g3appdriver.GCircle.room.Repositories.RImageInfo;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.model.LRDcp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class VMCollectionLog extends AndroidViewModel {
    private static final String TAG = VMCollectionLog.class.getSimpleName();
    private final REMIT poSys;
    private final Branch poBranch;
    private final RImageInfo poImage;
    private final RCollectionUpdate poUpdate;
    private final RBankInfo poBank;

    private final MutableLiveData<EDCPCollectionMaster> poMaster = new MutableLiveData<>();
    private final MutableLiveData<List<EImageInfo>> plImageLst = new MutableLiveData<>();
    private final MutableLiveData<List<EAddressUpdate>> paAddress = new MutableLiveData<>();
    private final MutableLiveData<List<EMobileUpdate>> paMobile = new MutableLiveData<>();
    private final MutableLiveData<String> dTransact = new MutableLiveData<>();

    public VMCollectionLog(@NonNull Application application) {
        super(application);
        this.poSys = new REMIT(application);
        this.poBranch = new Branch(application);
        this.poImage = new RImageInfo(application);
        this.poUpdate = new RCollectionUpdate(application);
        this.dTransact.setValue(AppConstants.CURRENT_DATE());
        this.poBank = new RBankInfo(application);
    }

    public LiveData<EBranchInfo> getUserBranchInfo(){
        return poBranch.getUserBranchInfo();
    }

    public LiveData<EDCPCollectionMaster> getCollectionMaster(){
        return poSys.GetCollectionMasterForRemittance();
    }

    public void setCollectionMaster(EDCPCollectionMaster collectionMaster){
        try {
            this.poMaster.setValue(collectionMaster);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public LiveData<List<EImageInfo>> getUnsentImageInfoList(){
        return poImage.getUnsentImageList();
    }

    public LiveData<String> GetTotalCollection(String fsTrasact){
        return poSys.GetTotalCollection(fsTrasact);
    }

    public LiveData<String> GetTotalRemittance(String fsVal){
        return poSys.GetRemittedCollection(fsVal);
    }

    public LiveData<String> GetCashOnHand(String fsVal){
        return poSys.GetCashOnHand(fsVal);
    }

    public LiveData<String> GetCheckOnHand(String fsVal){
        return poSys.GetCheckOnHand(fsVal);
    }

    public LiveData<String> GetEPaymentOnHand(String fsVal){
        return poSys.GetEPayOnHand(fsVal);
    }

    public void setDateTransact(String fsTransact){
        try {
            @SuppressLint("SimpleDateFormat") Date loDate = new SimpleDateFormat("MMMM dd, yyyy").parse(fsTransact);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat lsFormatter = new SimpleDateFormat("yyyy-MM-dd");
            this.dTransact.setValue(lsFormatter.format(Objects.requireNonNull(loDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public LiveData<String> getDateTransact(){
        return dTransact;
    }

    public LiveData<EDCPCollectionMaster> GetCollectionMaster(String fsVal){
        return poSys.GetMasterCollectionForDate(fsVal);
    }

    public LiveData<List<EDCPCollectionDetail>> GetCollectionDetail(String fsVal){
        return poSys.GetCollectionDetailForPreview(fsVal);
    }

    public void setImageInfoList(List<EImageInfo> imageInfoList){
        this.plImageLst.setValue(imageInfoList);
    }

    public LiveData<List<EAddressUpdate>> getAllAddress(){
        return poUpdate.getAddressList();
    }

    public LiveData<List<EMobileUpdate>> getAllMobileNox(){
        return poUpdate.getMobileList();
    }

    public void setAddressList(List<EAddressUpdate> paAddress) {
        this.paAddress.setValue(paAddress);
    }

    public void setMobileList(List<EMobileUpdate> paMobile) {
        this.paMobile.setValue(paMobile);
    }
    public LiveData<String> getTotalCollectedCash(String fsVal){
        return poSys.GetCashCollection(fsVal);
    }

    public LiveData<String> getTotalCollectedCheck(String fsVal){
        return poSys.GetCheckCollection(fsVal);
    }
}
