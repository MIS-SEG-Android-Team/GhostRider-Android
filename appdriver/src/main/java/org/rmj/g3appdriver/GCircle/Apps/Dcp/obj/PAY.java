package org.rmj.g3appdriver.GCircle.Apps.Dcp.obj;

import static org.rmj.g3appdriver.dev.Api.ApiResult.SERVER_NO_RESPONSE;
import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;
import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;
import org.rmj.apprdiver.util.LRUtil;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.model.LRDcp;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.PaidDCP;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.etc.AppConstants;

import java.util.Date;

public class PAY extends LRDcp {
    private static final String TAG = PAY.class.getSimpleName();

    public PAY(Application instance) {
        super(instance);
    }

    public String SavePaidTransaction(Object args) {
        try{
            PaidDCP foVal = (PaidDCP) args;
            EDCPCollectionDetail loDetail = poDao.GetCollectionDetail(
                    foVal.getTransNo(),
                    foVal.getEntryNo(),
                    foVal.getAccntNo());

            if(loDetail == null){
                message = "No account record found.";
                return null;
            }

            if(!foVal.isDataValid()){
                message = foVal.getMessage();
                return null;
            }

            if(foVal.getBankNme() != null){
                loDetail.setBankIDxx(foVal.getBankNme());
                loDetail.setCheckDte(foVal.getCheckDt());
                loDetail.setCheckNox(foVal.getCheckNo());
                loDetail.setCheckAct(foVal.getAccntNo());
                loDetail.setPaymForm("1");
            }
            loDetail.setRemCodex("PAY");
            loDetail.setTranType(foVal.getPayment());
            loDetail.setPRNoxxxx(foVal.getPrNoxxx());
            loDetail.setTranAmtx(foVal.getAmountx());
            loDetail.setDiscount(foVal.getDscount());
            loDetail.setOthersxx(foVal.getOthersx());
            loDetail.setTranTotl(foVal.getTotAmnt());
            loDetail.setRemarksx(foVal.getRemarks());
            loDetail.setTranStat("2");
            loDetail.setModified(AppConstants.DATE_MODIFIED());

            String lsImageID = poImage.SaveDcpImage(
                    foVal.getAccntNo(),
                    foVal.getTransNox(),
                    foVal.getFileName(),
                    foVal.getFilePath(),
                    foVal.getLatitude(),
                    foVal.getLongtude());

            if(lsImageID == null){
                message = poImage.getMessage();
                return null;
            }

            poDao.UpdateCollectionDetail(loDetail);
            poConfig.setDCP_PRNox(loDetail.getPRNoxxxx());

            Log.d(TAG, "Client payment has been save.");

            JSONObject loJson = new JSONObject();
            loJson.put("sTransNox", foVal.getTransNo());
            loJson.put("nEntryNox", foVal.getEntryNo());
            loJson.put("sAcctNmbr", foVal.getAccntNo());

            return loJson.toString();
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }


    public boolean UploadPaidTransaction(String fsVal){
        try{
            JSONObject loJson = new JSONObject(fsVal);

            EDCPCollectionDetail loDetail = poDao.GetCollectionDetail(
                    loJson.getString("sTransNox"),
                    loJson.getString("nEntryNox"),
                    loJson.getString("sAcctNmbr"));

            if(loDetail == null){
                message = "Unable to find record for uploading.";
                return false;
            }

            JSONObject loData = new JSONObject();
            loData.put("sPRNoxxxx", loDetail.getPRNoxxxx());
            loData.put("nTranAmtx", loDetail.getTranAmtx());
            loData.put("nDiscount", loDetail.getDiscount());
            loData.put("nOthersxx", loDetail.getOthersxx());
            loData.put("cTranType", loDetail.getTranType());
            loData.put("nTranTotl", loDetail.getTranTotl());
            loData.put("sBankIDxx", loDetail.getBankIDxx());
            loData.put("sCheckDte", loDetail.getCheckDte());
            loData.put("sCheckNox", loDetail.getCheckNox());
            loData.put("sCheckAct", loDetail.getCheckAct());

            JSONObject params = new JSONObject();
            params.put("sTransNox", loDetail.getTransNox());
            params.put("nEntryNox", loDetail.getEntryNox());
            params.put("sAcctNmbr", loDetail.getAcctNmbr());
            params.put("sRemCodex", loDetail.getRemCodex());
            params.put("dModified", loDetail.getModified());
            params.put("sJsonData", loData);
            params.put("dReceived", "");
            loData.put("sRemarksx", loDetail.getRemarksx());
            params.put("sUserIDxx", poSession.getUserID());
            params.put("sDeviceID", poDevice.getDeviceID());

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlDcpSubmit(),
                    params.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
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
            loDetail.setSendDate(AppConstants.DATE_MODIFIED());
            loDetail.setModified(AppConstants.DATE_MODIFIED());
            poDao.UpdateCollectionDetail(loDetail);
            return true;

        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    /**
     *
     * @param args Date Purchase
     * @param args1 Due Date
     * @param args2 Monthly Amortization
     * @param args3 Balance
     * @param args4 Amount Paid
     * @return Calculated penalty
     */
    public double CalculatePenalty(Date args, Date args1, double args2, double args3, double args4){
        return LRUtil.getPenalty(args, args1, args2, args3, args4);
    }

    public double CalculateRebate(double args, double args1, double args2, double args3){
        return LRUtil.getRebate(args, args, args, args);
    }
}
