package org.rmj.g3appdriver.GCircle.Apps.Dcp.obj;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.model.LRDcp;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.Remittance;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionMaster;

import java.util.List;

public class REMIT extends LRDcp {
    private static final String TAG = REMIT.class.getSimpleName();

    public REMIT(Application instance) {
        super(instance);
    }

    public LiveData<EDCPCollectionMaster> GetMasterCollectionForDate(String fsVal){
        return poDao.GetMasterCollectionForDate(fsVal);
    }

    public LiveData<List<EDCPCollectionDetail>> GetCollectionDetailForPreview(String fsVal){
        return poDao.GetCollectionDetailForPreview(fsVal);
    }

    public LiveData<EDCPCollectionMaster> GetCollectionMasterForRemittance(){
        return poDao.GetColletionMasterForRemittance();
    }

    public LiveData<String> GetTotalCollection(String fsVal){
        return poRemit.GetTotalCollection(fsVal);
    }

    public LiveData<String> GetRemittedCollection(String fsVal){
        return poRemit.GetRemittedCollection(fsVal);
    }

    public LiveData<String> GetCashCollection(String fsVal){
        return poRemit.GetCashCollection(fsVal);
    }

    public LiveData<String> GetCheckCollection(String fsVal){
        return poRemit.GetCheckCollection(fsVal);
    }

    public LiveData<String> GetEPayCollection(String fsVal){
        return poRemit.GetEPayCollection(fsVal);
    }

    public LiveData<String> GetCashOnHand(String fsVal){
        return poRemit.GetCashOnHand(fsVal);
    }

    public LiveData<String> GetCheckOnHand(String fsVal){
        return poRemit.GetCheckOnHand(fsVal);
    }

    public LiveData<String> GetEPayOnHand(String fsVal){
        return poRemit.GetEPayOnHand(fsVal);
    }

    public LiveData<String> GetBranchRemittanceAmount(String fsVal){
        return poRemit.GetBranchRemittanceAmount(fsVal);
    }

    public LiveData<String> GetBankRemittanceAmount(String fsVal){
        return poRemit.GetBankRemittanceAmount(fsVal);
    }

    public LiveData<String> GetOtherRemittanceAmount(String fsVal){
        return poRemit.GetOtherRemittanceAmount(fsVal);
    }

    public JSONObject SaveRemittanceEntry(Remittance foVal){
        if(!foVal.isDataValid()){
            message = foVal.getMessage();
            return null;
        }

        if(!poRemit.HasUnremitCollectedPayments(foVal.getsTransNox())){
            message = poRemit.getMessage();
            return null;
        }

        JSONObject params = poRemit.SaveRemittance(foVal);
        if(params == null){
            message = poRemit.getMessage();
            return null;
        }

        return params;
    }

    public boolean UploadRemittance(String fsVal, String fsVal1){
        if(!poRemit.UploadRemittance(fsVal, fsVal1)){
            message = poRemit.getMessage();
            return false;
        }

        return true;
    }
}
