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

import static org.rmj.g3appdriver.dev.Api.ApiResult.SERVER_NO_RESPONSE;
import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;
import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DDCP_Remittance;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCP_Remittance;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.Remittance;

public class RCollectionRemittance {
    private static final String TAG = RCollectionRemittance.class.getSimpleName();

    private final DDCP_Remittance poDao;

    private final GCircleApi poApi;
    private final HttpHeaders poHeaders;

    private String message;

    public RCollectionRemittance(Application instance) {
        this.poDao = GGC_GCircleDB.getInstance(instance).DCPRemitanceDao();
        this.poApi = new GCircleApi(instance);
        this.poHeaders = HttpHeaders.getInstance(instance);
    }

    public String getMessage() {
        return message;
    }

    public LiveData<String> GetTotalCollection(String fsVal){
        return poDao.GetTotalCollection(fsVal);
    }

    public LiveData<String> GetRemittedCollection(String fsVal){
        return poDao.GetRemittedCollection(fsVal);
    }

    public LiveData<String> GetCashCollection(String fsVal){
        return poDao.GetCashCollection(fsVal);
    }

    public LiveData<String> GetCheckCollection(String fsVal){
        return poDao.GetCheckCollection(fsVal);
    }

    public LiveData<String> GetEPayCollection(String fsVal){
        return poDao.GetEPayCollection(fsVal);
    }

    public LiveData<String> GetBranchRemittanceAmount(String fsVal){
        return poDao.GetBranchRemittanceAmount(fsVal);
    }
    public LiveData<String> GetBankRemittanceAmount(String fsVal){
        return poDao.GetBankRemittanceAmount(fsVal);
    }
    public LiveData<String> GetOtherRemittanceAmount(String fsVal){
        return poDao.GetOtherRemittanceAmount(fsVal);
    }

    public LiveData<String> GetCashOnHand(String fsVal){
        return poDao.GetCashOnHand(fsVal);
    }

    public LiveData<String> GetCheckOnHand(String fsVal){
        return poDao.GetCheckOnHand(fsVal);
    }

    public LiveData<String> GetEPayOnHand(String fsVal){
        return poDao.GetEPayOnHand(fsVal);
    }

    public JSONObject SaveRemittance(Remittance foVal){
        try{
            String lsEntryNo = poDao.GetRowsForID();

            EDCP_Remittance loDetail = new EDCP_Remittance();
            loDetail.setTransNox(foVal.getsTransNox());
            loDetail.setEntryNox(Integer.parseInt(lsEntryNo));
            loDetail.setTransact(AppConstants.CURRENT_DATE());
            loDetail.setPaymForm(foVal.getcPaymForm());
            loDetail.setRemitTyp(foVal.getcRemitTyp());
            loDetail.setCompnyNm(foVal.getsCompnyNm());
            loDetail.setBankAcct(foVal.getsBankAcct());
            loDetail.setReferNox(foVal.getsReferNox());
            loDetail.setAmountxx(foVal.getnAmountxx());
            loDetail.setSendStat("0");
            loDetail.setTimeStmp(AppConstants.DATE_MODIFIED());

            poDao.SaveRemittance(loDetail);

            JSONObject params = new JSONObject();
            params.put("sTransNox", loDetail.getTransNox());
            params.put("nEntryNox", loDetail.getEntryNox());
            return params;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    public boolean UploadRemittance(String transNo, String entryNo){
        try{
            EDCP_Remittance loDetail = poDao.GetCollectionRemittance(transNo, entryNo);

            if(loDetail == null){
                message = "No remittance record found.";
                Log.e(TAG, message);
                return false;
            }

            JSONObject param = new JSONObject();
            param.put("sTransNox", loDetail.getTransNox());
            param.put("nEntryNox", loDetail.getEntryNox());
            param.put("dTransact", loDetail.getTransact());
            param.put("cRemitTyp", loDetail.getRemitTyp());
            param.put("sCompnyNm", loDetail.getCompnyNm());
            param.put("sBankAcct", loDetail.getBankAcct());
            param.put("sReferNox", loDetail.getReferNox());
            param.put("cPaymType", loDetail.getPaymForm());
            param.put("nAmountxx", loDetail.getAmountxx());

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlDcpRemittance(),
                    param.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                Log.e(TAG, message);
                return false;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return false;
            }

            loDetail.setSendStat("1");
            loDetail.setDateSent(AppConstants.DATE_MODIFIED());
            loDetail.setTimeStmp(AppConstants.DATE_MODIFIED());
            poDao.Update(loDetail);

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean HasUnremitCollectedPayments(String args){
        double lnRmCllct =  poDao.ValidateRemittanceEntry(args);
        if(lnRmCllct > 0){
            return true;
        } else {
            message = "No collected payments to remit.";
            return false;
        }
    }
}
