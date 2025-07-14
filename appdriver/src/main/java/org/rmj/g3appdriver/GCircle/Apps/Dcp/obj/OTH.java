package org.rmj.g3appdriver.GCircle.Apps.Dcp.obj;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import org.rmj.g3appdriver.GCircle.Apps.Dcp.model.LRDcp;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.OtherRemCode;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;
import org.rmj.g3appdriver.etc.AppConstants;

public class OTH extends LRDcp {
    private static final String TAG = OTH.class.getSimpleName();

    public OTH(Application instance) {
        super(instance);
    }

    @Override
    public boolean SaveTransaction(Object args) {
        try{
            OtherRemCode foVal = (OtherRemCode) args;
            if(!foVal.isDataValid()){
                message = foVal.getMessage();
                return false;
            }

            String TransNox = foVal.getTransNox();
            String AccntNox = foVal.getAccountNo();
            String EntryNox = foVal.getEntryNox();
            EDCPCollectionDetail loDetail = poDao.GetCollectionDetail(
                    TransNox,
                    EntryNox,
                    AccntNox);

            if(loDetail == null){
                message = "Unable to find account detail. Please restart app and try again.";
                return false;
            }

            loDetail.setTranStat("1");
            loDetail.setRemCodex(foVal.getRemCodex());
            loDetail.setRemarksx(foVal.getRemarksx());
            loDetail.setModified(AppConstants.DATE_MODIFIED());

            String lsImageID = poImage.SaveDcpImage(
                    foVal.getAccountNo(),
                    foVal.getTransNox(),
                    foVal.getFileName(),
                    foVal.getFilePath(),
                    foVal.getLatitude(),
                    foVal.getLongtude());

            if(lsImageID == null){
                message = poImage.getMessage();
                return false;
            }

            poDao.UpdateCollectionDetail(loDetail);
            Log.d(TAG, "Collection detail has been updated.");
            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

}
