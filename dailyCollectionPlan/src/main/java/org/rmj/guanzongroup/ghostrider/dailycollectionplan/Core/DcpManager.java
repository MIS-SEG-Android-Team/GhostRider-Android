package org.rmj.guanzongroup.ghostrider.dailycollectionplan.Core;

import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.Entities.EAddressUpdate;
import org.rmj.g3appdriver.GCircle.room.Entities.EClientUpdate;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;
import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EMobileUpdate;
import org.rmj.g3appdriver.GCircle.room.Repositories.RCollectionUpdate;
import org.rmj.g3appdriver.GCircle.room.Repositories.RDailyCollectionPlan;
import org.rmj.g3appdriver.GCircle.room.Repositories.RImageInfo;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Device.Telephony;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.dev.Api.WebFileServer;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.obj.RClientUpdate;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Core.Transaction.CustomerNotAround;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Core.Transaction.OthTransaction;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Core.Transaction.Paid;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Core.Transaction.PromiseToPay;
import org.rmj.guanzongroup.ghostrider.dailycollectionplan.Service.GLocatorService;

import java.util.List;

public class DcpManager {
    private static final String TAG = DcpManager.class.getSimpleName();

    private final Application instance;

    private final RDailyCollectionPlan poDcp;
    private final AppConfigPreference poConfig;
    private final RCollectionUpdate poUpdate;
    private final GCircleApi poApis;
    private final HttpHeaders poHeaders;
    private final EmployeeSession poUser;
    private final RImageInfo poImage;
    private final Telephony poTlphny;
    private final RClientUpdate poClient;

    public interface OnActionCallback{
        void OnSuccess(String args);
        void OnFailed(String message);
    }

    public DcpManager(Application application) {
        this.instance = application;
        this.poDcp = new RDailyCollectionPlan(instance);
        this.poUpdate = new RCollectionUpdate(instance);
        this.poConfig = AppConfigPreference.getInstance(instance);
        this.poHeaders = HttpHeaders.getInstance(instance);
        this.poUser = EmployeeSession.getInstance(instance);
        this.poImage = new RImageInfo(instance);
        this.poTlphny = new Telephony(instance);
        this.poClient = new RClientUpdate(instance);
        this.poApis = new GCircleApi(instance);
    }

    public iDCPTransaction getTransaction(String fsRemCode){
        switch (fsRemCode) {
            case "PAY":
                return new Paid(instance);
            case "PTP":
                return new PromiseToPay(instance);
            case "CNA":
                return new CustomerNotAround(instance);
            default:
                return new OthTransaction(instance);
        }
    }

    public void PostLRDCPCollection(OnActionCallback callback){
        try{
            List<EDCPCollectionDetail> loDcpList = poDcp.getLRDCPCollectionForPosting();

            String lsProdtID = poConfig.ProducID();
            String lsClntIDx = poUser.getClientId();
            String lsUserIDx = poUser.getUserID();

            if(loDcpList == null){
                callback.OnFailed("No record for posting");
            } else {
                for(int x = 0; x < loDcpList.size(); x++){
                    EDCPCollectionDetail loDcp = loDcpList.get(x);
                    JSONObject loData = new JSONObject();
                    JSONObject loJson = new JSONObject();
                    String lsRemCode = loDcp.getRemCodex();

                    String lsTransNo = loDcp.getTransNox();
                    String lsAccntNo = loDcp.getAcctNmbr();
                    Log.d(TAG, "DCP transaction number: " + lsTransNo);
                    Log.d(TAG, "DCP client account number: " + lsAccntNo);
                    Log.d(TAG, "DCP transaction remarks code: " + lsRemCode);

                    EImageInfo loImage;
                    switch (lsRemCode){
                        case "PAY":
                            loData.put("sPRNoxxxx", loDcp.getPRNoxxxx());
                            loData.put("nTranAmtx", loDcp.getTranAmtx());
                            loData.put("nDiscount", loDcp.getDiscount());
                            loData.put("nOthersxx", loDcp.getOthersxx());
                            loData.put("cTranType", loDcp.getTranType());
                            loData.put("nTranTotl", loDcp.getTranTotl());
                            break;
                        case "PTP":
                            //Required parameters for Promise to pay..
                            loData.put("cApntUnit", loDcp.getApntUnit());
                            loData.put("sBranchCd", loDcp.getBranchCd());
                            loData.put("dPromised", loDcp.getPromised());
                            loImage = poImage.getDCPImageInfoForPosting(lsTransNo, lsAccntNo);

                            if(loImage != null || loImage.getImageNme() != null) {
                                if(!poConfig.getTestStatus()) {
                                    loData.put("sImageNme", loImage.getImageNme());
                                    loData.put("sSourceCD", loImage.getSourceCD());
                                    loData.put("nLongitud", loImage.getLongitud());
                                    loData.put("nLatitude", loImage.getLatitude());
                                }
                            }
                            break;
                        case "LUn":
                        case "TA":
                        case "FO":
                            EClientUpdate loClient = poClient.getClientUpdateInfoForPosting(lsTransNo, lsAccntNo);
                            //TODO: replace JSON parameters get the parameters which is being generated by RClientUpdate...
                            loData.put("sLastName", loClient.getLastName());
                            loData.put("sFrstName", loClient.getFrstName());
                            loData.put("sMiddName", loClient.getMiddName());
                            loData.put("sSuffixNm", loClient.getSuffixNm());
                            loData.put("sHouseNox", loClient.getHouseNox());
                            loData.put("sAddressx", loClient.getAddressx());
                            loData.put("sTownIDxx", loClient.getTownIDxx());
                            loData.put("cGenderxx", loClient.getGenderxx());
                            loData.put("cCivlStat", loClient.getCivlStat());
                            loData.put("dBirthDte", loClient.getBirthDte());
                            loData.put("dBirthPlc", loClient.getBirthPlc());
                            loData.put("sLandline", loClient.getLandline());
                            loData.put("sMobileNo", loClient.getMobileNo());
                            loData.put("sEmailAdd", loClient.getEmailAdd());

                            loImage = poImage.getDCPImageInfoForPosting(lsTransNo, lsAccntNo);
                            if(loImage != null || loImage.getImageNme() != null) {
                                if(!poConfig.getTestStatus()) {
                                    loData.put("sImageNme", loImage.getImageNme());
                                    loData.put("sSourceCD", loImage.getSourceCD());
                                    loData.put("nLongitud", loImage.getLongitud());
                                    loData.put("nLatitude", loImage.getLatitude());
                                }
                            }
                            break;

                        case "CNA":
                            JSONObject paramAddress = new JSONObject();
                            JSONObject paramMobile = new JSONObject();
                            if(!poConfig.getTestStatus()) {
                                loImage = poImage.getDCPImageInfoForPosting(lsTransNo, lsAccntNo);
                                if(loImage != null || loImage.getImageNme() != null) {
                                    if(!poConfig.getTestStatus()) {
                                        loData.put("sImageNme", loImage.getImageNme());
                                        loData.put("sSourceCD", loImage.getSourceCD());
                                        loData.put("nLongitud", loImage.getLongitud());
                                        loData.put("nLatitude", loImage.getLatitude());
                                    }
                                }
                            } else {
                                paramAddress.put("nLatitude", 0.0);
                                paramAddress.put("nLongitud", 0.0);
                                paramAddress.put("sImageNme", "testCase");
                                paramMobile.put("sImageNme", "testCase");
                            }
                            String lsClientID = loDcp.getClientID();

                            if(poUpdate.getAddressUpdateInfoForPosting(lsClientID) != null){
                                EAddressUpdate loAddress = poUpdate.getAddressUpdateInfoForPosting(lsClientID);
                                paramAddress.put("cReqstCDe", loAddress.getReqstCDe());
                                paramAddress.put("cAddrssTp", loAddress.getAddrssTp());
                                paramAddress.put("sHouseNox", loAddress.getHouseNox());
                                paramAddress.put("sAddressx", loAddress.getAddressx());
                                paramAddress.put("sTownIDxx", loAddress.getTownIDxx());
                                paramAddress.put("sBrgyIDxx", loAddress.getBrgyIDxx());
                                paramAddress.put("cPrimaryx", loAddress.getPrimaryx());
                                paramAddress.put("sRemarksx", loAddress.getRemarksx());
                                loData.put("Address", paramAddress);
                            }

                            if(poUpdate.getMobileUpdateInfoForPosting(lsClientID) != null){
                                EMobileUpdate loMobile = poUpdate.getMobileUpdateInfoForPosting(lsClientID);
                                paramMobile.put("cReqstCDe", loMobile.getReqstCDe());
                                paramMobile.put("sMobileNo", loMobile.getMobileNo());
                                paramMobile.put("cPrimaryx", loMobile.getPrimaryx());
                                paramMobile.put("sRemarksx", loMobile.getRemarksx());
                                loData.put("Mobile", paramMobile);
                            }

                            break;
                    }
                    loData.put("sRemarksx", loDcp.getRemarksx());
                    loJson.put("sRemCodex", loDcp.getRemCodex());
                    loJson.put("dModified", loDcp.getModified());

                    loJson.put("sTransNox", loDcp.getTransNox());
                    loJson.put("nEntryNox", loDcp.getEntryNox());
                    loJson.put("sAcctNmbr", loDcp.getAcctNmbr());

                    loJson.put("sJsonData", loData);
                    loJson.put("dReceived", "");
                    loJson.put("sUserIDxx", poUser.getUserID());
                    loJson.put("sDeviceID", poTlphny.getDeviceID());

                    String lsResponse = WebClient.sendRequest(poApis.getUrlDcpSubmit(), loJson.toString(), poHeaders.getHeaders());

                    if(lsResponse == null) {
                        Log.e(TAG, "Posting collection Image with account no. :" + loDcp.getAcctNmbr() + ", " + loDcp.getRemCodex() + "Failed!");
                        Log.e(TAG, "Error : Server no response.");
                    } else {
                        JSONObject loResponse = new JSONObject(lsResponse);
                        String lsResult = loResponse.getString("result");
                        if(lsResult.equalsIgnoreCase("success")){
                            Log.d(TAG, "Posting collection with account no. :" + loDcp.getAcctNmbr() + ", " + loDcp.getRemCodex() + "Success!");
                            poDcp.updateCollectionDetailStatus(loDcp.getTransNox(), loDcp.getEntryNox());
                        } else {
                            JSONObject loError = loResponse.getJSONObject("error");
                            String lsMessage = getErrorMessage(loError);
                            Log.e(TAG, "Posting collection with account no. :" + loDcp.getAcctNmbr() + ", " + loDcp.getRemCodex() + "Failed!");
                            Log.e(TAG, "Error : " + lsMessage);
                        }
                    }

                    if(!poConfig.getTestStatus()) {
                        String lsClient = WebFileServer.RequestClientToken(lsProdtID, lsClntIDx, lsUserIDx);
                        String lsAccess = WebFileServer.RequestAccessToken(lsClient);
                    }

                    Thread.sleep(1000);
                }
                callback.OnSuccess("DCP Transactions Posted Successfully.");
            }

        } catch (Exception e){
            e.printStackTrace();
            callback.OnFailed("PostLRDCPCollection " + e.getMessage());
        }

    }

    public void PostDcpMaster(OnActionCallback callback){
        try{
            String lsTransNox = poDcp.getUnpostedDcpMaster();
            int lnPostdDcp = poDcp.getUnsentCollectionDetail(lsTransNox);
            String lsStatus = poDcp.getMasterSendStatus(lsTransNox);
            if (lnPostdDcp == 0 &&
                    lsStatus == null){
                JSONObject loJson = new JSONObject();
                loJson.put("sTransNox", lsTransNox);
                String lsResponse = WebClient.sendRequest(poApis.getUrlPostDcpMaster(), loJson.toString(), poHeaders.getHeaders());
                if (lsResponse == null) {
                    callback.OnFailed("Posting dcp master failed. Server no response");
                } else {
                    JSONObject loResponse = new JSONObject(lsResponse);
                    String result = loResponse.getString("result");
                    if (result.equalsIgnoreCase("success")) {
                        poDcp.updateSentPostedDCPMaster(lsTransNox);
                        callback.OnSuccess("Dcp for today has been posted.");
                        instance.stopService(new Intent(instance, GLocatorService.class));
                    } else {
                        JSONObject loError = loResponse.getJSONObject("error");
                        String lsMessage = getErrorMessage(loError);
                        callback.OnFailed("Posting dcp master failed." + lsMessage);
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            callback.OnFailed("Posting dcp master failed. " + e.getMessage());
        }
    }
}
