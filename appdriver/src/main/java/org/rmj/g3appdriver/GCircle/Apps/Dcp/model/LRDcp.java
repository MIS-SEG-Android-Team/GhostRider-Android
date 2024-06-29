package org.rmj.g3appdriver.GCircle.Apps.Dcp.model;

import static org.rmj.g3appdriver.dev.Api.ApiResult.SERVER_NO_RESPONSE;
import static org.rmj.g3appdriver.dev.Api.ApiResult.getErrorMessage;
import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.apprdiver.util.LRUtil;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.obj.RClientUpdate;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.AddressUpdate;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.CustomerNotAround;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.ImportParams;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.LoanUnit;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.MobileUpdate;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.OtherRemCode;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.PaidDCP;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.PromiseToPay;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.pojo.Remittance;
import org.rmj.g3appdriver.GCircle.Api.GCircleApi;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DAddressUpdate;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DEmployeeInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DLRDcp;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DMobileUpdate;
import org.rmj.g3appdriver.GCircle.room.Entities.EClientUpdate;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionDetail;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCPCollectionMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.EDCP_Remittance;
import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.GCircle.room.Repositories.RCollectionRemittance;
import org.rmj.g3appdriver.GCircle.room.Repositories.RImageInfo;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.dev.Api.HttpHeaders;
import org.rmj.g3appdriver.dev.Api.WebClient;
import org.rmj.g3appdriver.dev.Device.Telephony;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.obj.DCPFileManager;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.obj.RAddressUpdate;
import org.rmj.g3appdriver.GCircle.Apps.Dcp.obj.RMobileUpdate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LRDcp {
    private static final String TAG = LRDcp.class.getSimpleName();

    private final Application instance;

    protected final DLRDcp poDao;

    protected final EmployeeMaster poUser;

    protected final AppConfigPreference poConfig;
    protected final GCircleApi poApi;
    protected final HttpHeaders poHeaders;
    protected final EmployeeSession poSession;
    protected final Telephony poDevice;
    protected final RImageInfo poImage;
    protected final RAddressUpdate poAddress;
    protected final RMobileUpdate poMobile;
    protected final RClientUpdate poClient;
    protected final DCPFileManager poReader;
    protected final RCollectionRemittance poRemit;
    protected final DLRDcp poDCP;

    protected String message;

    private String CURRENT_DATE;

    public LRDcp(Application instance) {
        this.instance = instance;
        this.poDao = GGC_GCircleDB.getInstance(instance).DcpDao();
        this.poUser = new EmployeeMaster(instance);
        this.poConfig = AppConfigPreference.getInstance(instance);
        this.poApi = new GCircleApi(instance);
        this.poHeaders = HttpHeaders.getInstance(instance);
        this.poSession = EmployeeSession.getInstance(instance);
        this.poDevice = new Telephony(instance);
        this.poImage = new RImageInfo(instance);
        this.poAddress = new RAddressUpdate(instance);
        this.poMobile = new RMobileUpdate(instance);
        this.poClient = new RClientUpdate(instance);
        this.poReader = new DCPFileManager(instance);
        this.poRemit = new RCollectionRemittance(instance);
        this.poDCP = GGC_GCircleDB.getInstance(instance).DcpDao();
        this.CURRENT_DATE = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
    }

    public String getMessage() {
        return message;
    }

    public LiveData<DEmployeeInfo.EmployeeBranch> GetUserInfo(){
        return poUser.GetEmployeeBranch();
    }

    public LiveData<EDCPCollectionMaster> GetPostedDCP(){
        return poDCP.GetLatestPostedDcp();
    }

    public boolean SaveTransaction(Object args){
        return true;
    }

    public boolean ImportFromFile(Uri uri){
        try{
            String lsContent = poReader.ReadToJson(uri);

            if(lsContent == null){
                message = poReader.getMessage();
                return false;
            }

            JSONObject loJson = new JSONObject(lsContent);
            if(!loJson.has("master")){
                message = "File doesn't have any key for DCP. Invalid file.";
                return false;
            }

            if(!loJson.has("detail")){
                message = "File doesn't have any key for DCP. Invalid file.";
                return false;
            }

            JSONObject joMaster = loJson.getJSONObject("master");
            String lsTransNo = joMaster.getString("sTransNox");

            EDCPCollectionMaster loMaster = poDao.GetMaster(lsTransNo);
            if(loMaster != null){
                message = "Dcp already exist in this local device.";
                return false;
            }

            EDCPCollectionMaster dcpMaster = new EDCPCollectionMaster();
            dcpMaster.setTransNox(joMaster.getString("sTransNox"));
            dcpMaster.setTransact(joMaster.getString("dTransact"));
            dcpMaster.setReferNox(joMaster.getString("sReferNox"));
            dcpMaster.setCollName(joMaster.getString("xCollName"));
            dcpMaster.setRouteNme(joMaster.getString("sRouteNme"));
            dcpMaster.setReferDte(joMaster.getString("dReferDte"));
            dcpMaster.setTranStat(joMaster.getString("cTranStat"));
            dcpMaster.setDCPTypex(joMaster.getString("cDCPTypex"));
            dcpMaster.setEntryNox(joMaster.getString("nEntryNox"));
            dcpMaster.setBranchNm(joMaster.getString("sBranchNm"));
            dcpMaster.setCollctID(joMaster.getString("sCollctID"));
            poDao.SaveDcpMaster(dcpMaster);

            Log.d(TAG, "Lr dcp collection master has been saved.");

            JSONArray laJson = loJson.getJSONArray("detail");
            for(int x = 0; x < laJson.length(); x++){
                JSONObject joDetail = laJson.getJSONObject(x);
                EDCPCollectionDetail dcpDetail = new EDCPCollectionDetail();
                dcpDetail.setTransNox(lsTransNo);
                dcpDetail.setEntryNox(Integer.parseInt(joDetail.getString("nEntryNox")));
                dcpDetail.setAcctNmbr(joDetail.getString("sAcctNmbr"));
                dcpDetail.setFullName(joDetail.getString("xFullName"));
                dcpDetail.setIsDCPxxx(joDetail.getString("cIsDCPxxx"));
                dcpDetail.setMobileNo(joDetail.getString("sMobileNo"));
                dcpDetail.setHouseNox(joDetail.getString("sHouseNox"));
                dcpDetail.setAddressx(joDetail.getString("sAddressx"));
                dcpDetail.setBrgyName(joDetail.getString("sBrgyName"));
                dcpDetail.setTownName(joDetail.getString("sTownName"));
                dcpDetail.setPurchase(joDetail.getString("dPurchase"));
                dcpDetail.setAmtDuexx(joDetail.getDouble("nAmtDuexx"));
                dcpDetail.setApntUnit(joDetail.getString("cApntUnit"));
                dcpDetail.setDueDatex(joDetail.getString("dDueDatex"));
                dcpDetail.setLongitud(joDetail.getDouble("nLongitud"));
                dcpDetail.setLatitude(joDetail.getDouble("nLatitude"));
                dcpDetail.setClientID(joDetail.getString("sClientID"));
                dcpDetail.setSerialID(joDetail.getString("sSerialID"));
                dcpDetail.setSerialNo(joDetail.getString("sSerialNo"));
                dcpDetail.setLastPaym(joDetail.getDouble("nLastPaym"));
                dcpDetail.setLastPaid(joDetail.getString("dLastPaym"));
                dcpDetail.setABalance(joDetail.getString("nABalance"));
                dcpDetail.setDelayAvg(joDetail.getDouble("nDelayAvg"));
                dcpDetail.setMonAmort(joDetail.getDouble("nMonAmort"));
                poDao.SaveDcpDetail(dcpDetail);
                Log.d(TAG, "DCP account no. " +dcpDetail.getAcctNmbr() + " has been saved.");
            }
            Log.d(TAG, "DCP has been imported from file successfully.");

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean ExportToFile(){
        try{
            EDCPCollectionMaster loMaster = poDao.GetColletionMasterForPosting();

            List<EDCPCollectionDetail> laCollDetl = poDao.GetCollectionDetailForPosting(loMaster.getTransNox());

            JSONArray loArray = new JSONArray();
            JSONObject params = new JSONObject();

            for(int x = 0; x < laCollDetl.size(); x++){
                EDCPCollectionDetail detail = laCollDetl.get(x);

                JSONObject loData = new JSONObject();

                String lsRemCode = detail.getRemCodex();

                EImageInfo loImage;

                switch (lsRemCode) {
                    case "PAY":
                        loData.put("sPRNoxxxx", detail.getPRNoxxxx());
                        loData.put("nTranAmtx", detail.getTranAmtx());
                        loData.put("nDiscount", detail.getDiscount());
                        loData.put("nOthersxx", detail.getOthersxx());
                        loData.put("cTranType", detail.getTranType());
                        loData.put("nTranTotl", detail.getTranTotl());
                        loData.put("sRemarksx", detail.getRemarksx());
                        params.put("sRemCodex", detail.getRemCodex());
                        params.put("dModified", detail.getModified());
                        break;

                    case "CNA":
                        String lsClientID = detail.getClientID();

                        JSONObject address = poAddress.GetAddressDetailForPosting(lsClientID);
                        if(address == null){
                            message = poAddress.getMessage();
                            Log.e(TAG, message);
                        } else {
                            loData.put("Address", address);
                        }

                        JSONObject mobile = poMobile.GetMobileDetailForPosting(lsClientID);
                        if(mobile == null){
                            message = poMobile.getMessage();
                            Log.e(TAG, message);
                        } else {
                            loData.put("Mobile", mobile);
                        }

                        break;

                    case "PTP":
                        loData.put("cApntUnit", detail.getApntUnit());
                        loData.put("sBranchCd", detail.getBranchCd());
                        loData.put("dPromised", detail.getPromised());
                        params.put("sRemCodex", detail.getRemCodex());
                        params.put("dModified", detail.getModified());

                        loImage = poDao.GetDcpImageForPosting(
                                detail.getTransNox(),
                                detail.getAcctNmbr());

                        if(loImage != null){

                            if(!poConfig.getTestStatus()){
                                loData.put("sImageNme", loImage.getImageNme());
                                loData.put("sSourceCD", loImage.getSourceCD());
                                loData.put("nLongitud", loImage.getLongitud());
                                loData.put("nLatitude", loImage.getLatitude());

                                String lsImageID = poImage.UploadImage(loImage.getTransNox());
                                if(lsImageID == null){
                                    Log.e(TAG, poImage.getMessage());
                                }
                            }
                        }
                        break;

                    case "LUn":
                    case "TA":
                    case "FO":
                        EClientUpdate loClient = poClient.getClientUpdateInfoForPosting(loMaster.getTransNox(), detail.getAcctNmbr());

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

                        loImage = poImage.getDCPImageInfoForPosting(loMaster.getTransNox(), detail.getAcctNmbr());
                        if(loImage != null || loImage.getImageNme() != null) {
                            if(!poConfig.getTestStatus()) {
                                loData.put("sImageNme", loImage.getImageNme());
                                loData.put("sSourceCD", loImage.getSourceCD());
                                loData.put("nLongitud", loImage.getLongitud());
                                loData.put("nLatitude", loImage.getLatitude());
                            }
                        }
                        break;

                    default:
                        loImage = poDao.GetDcpImageForPosting(
                                detail.getTransNox(),
                                detail.getAcctNmbr());

                        if(loImage != null){
                            Log.d(TAG, "Not visited image found.");
                            if(!poConfig.getTestStatus()){
                                loData.put("sImageNme", loImage.getImageNme());
                                loData.put("sSourceCD", loImage.getSourceCD());
                                loData.put("nLongitud", loImage.getLongitud());
                                loData.put("nLatitude", loImage.getLatitude());

                                String lsImageID = poImage.UploadImage(loImage.getTransNox());
                                if(lsImageID == null){
                                    Log.e(TAG, poImage.getMessage());
                                }
                            }
                        } else {
                            Log.e(TAG, "Not visited image not found.");
                        }
                }
                loData.put("sRemarksx", detail.getRemarksx());
                params.put("sRemCodex", detail.getRemCodex());
                params.put("dModified", detail.getModified());

                params.put("sTransNox", detail.getTransNox());
                params.put("nEntryNox", detail.getEntryNox());
                params.put("sAcctNmbr", detail.getAcctNmbr());

                params.put("sJsonData", loData);
                params.put("dReceived", "");
                params.put("sUserIDxx", poSession.getUserID());
                params.put("sDeviceID", poDevice.getDeviceID());
                Log.d(TAG, "DCP posting data: " + params);
                loArray.put(params);
            }

            String lsFileNme = loMaster.getTransNox() + "-mob.txt";

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File newDir = new File(path + "/" + "DCP Exports");
            try{
                if (!newDir.exists()) {
                    newDir.mkdir();
                }
                FileOutputStream writer = new FileOutputStream(new File(path + "/" + "DCP Exports", lsFileNme));
                writer.write(loArray.toString().getBytes());
                writer.close();
                Log.e("TAG", "Wrote to file: "+lsFileNme);
            } catch (IOException e) {
                e.printStackTrace();
                message = getLocalMessage(e);
                return false;
            }

//
//            ParcelFileDescriptor pfd = instance.getContentResolver().
//                    openFileDescriptor(uri, "w");
//            FileOutputStream fileOutputStream =
//                    new FileOutputStream(pfd.getFileDescriptor());
//            fileOutputStream.write(loArray.toString().getBytes());
//            // Let the document provider know you're done by closing the stream.
//            fileOutputStream.close();
//            pfd.close();

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean DownloadCollection(ImportParams foVal){
        try{
            JSONObject params = new JSONObject();
            String lsEmployID = poDao.GetEmployID();

            if(lsEmployID == null){
                message = "Invalid user detected. Please re-login account.";
                return false;
            }

            if(foVal != null){
                params.put("sEmployID", foVal.getEmployID());
                params.put("dTransact", foVal.getTransact());
            } else {
                params.put("sEmployID", lsEmployID);
                params.put("dTransact", CURRENT_DATE);
            }

            params.put("cDCPTypex", "1");

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlDownloadDcp(),
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

            JSONObject joMaster = loResponse.getJSONObject("master");
            String lsTransNox = joMaster.getString("sTransNox");
            EDCPCollectionMaster loMaster = poDao.GetMaster(lsTransNox);
            if(loMaster != null){
                message = "Collection already exist on local data";
                return false;
            }

            EDCPCollectionMaster dcpMaster = new EDCPCollectionMaster();
            dcpMaster.setTransNox(joMaster.getString("sTransNox"));
            dcpMaster.setTransact(joMaster.getString("dTransact"));
            dcpMaster.setReferNox(joMaster.getString("sReferNox"));
            dcpMaster.setCollName(joMaster.getString("xCollName"));
            dcpMaster.setRouteNme(joMaster.getString("sRouteNme"));
            dcpMaster.setReferDte(joMaster.getString("dReferDte"));
            dcpMaster.setTranStat(joMaster.getString("cTranStat"));
            dcpMaster.setDCPTypex(joMaster.getString("cDCPTypex"));
            dcpMaster.setEntryNox(joMaster.getString("nEntryNox"));
            dcpMaster.setBranchNm(joMaster.getString("sBranchNm"));
            dcpMaster.setCollctID(joMaster.getString("sCollctID"));
            poDao.SaveDcpMaster(dcpMaster);

            Log.d(TAG, "Lr dcp collection master has been saved.");

            JSONArray laJson = loResponse.getJSONArray("detail");
            for(int x = 0; x < laJson.length(); x++){
                JSONObject joDetail = laJson.getJSONObject(x);
                EDCPCollectionDetail dcpDetail = new EDCPCollectionDetail();
                dcpDetail.setTransNox(lsTransNox);
                dcpDetail.setEntryNox(Integer.parseInt(joDetail.getString("nEntryNox")));
                dcpDetail.setAcctNmbr(joDetail.getString("sAcctNmbr"));
                dcpDetail.setFullName(joDetail.getString("xFullName"));
                dcpDetail.setIsDCPxxx(joDetail.getString("cIsDCPxxx"));
                dcpDetail.setMobileNo(joDetail.getString("sMobileNo"));
                dcpDetail.setHouseNox(joDetail.getString("sHouseNox"));
                dcpDetail.setAddressx(joDetail.getString("sAddressx"));
                dcpDetail.setBrgyName(joDetail.getString("sBrgyName"));
                dcpDetail.setTownName(joDetail.getString("sTownName"));
                dcpDetail.setPurchase(joDetail.getString("dPurchase"));
                dcpDetail.setAmtDuexx(joDetail.getDouble("nAmtDuexx"));
                dcpDetail.setApntUnit(joDetail.getString("cApntUnit"));
                dcpDetail.setDueDatex(joDetail.getString("dDueDatex"));
                dcpDetail.setLongitud(joDetail.getDouble("nLongitud"));
                dcpDetail.setLatitude(joDetail.getDouble("nLatitude"));
                dcpDetail.setClientID(joDetail.getString("sClientID"));
                dcpDetail.setSerialID(joDetail.getString("sSerialID"));
                dcpDetail.setSerialNo(joDetail.getString("sSerialNo"));
                dcpDetail.setLastPaym(joDetail.getDouble("nLastPaym"));
                dcpDetail.setLastPaid(joDetail.getString("dLastPaym"));
                dcpDetail.setABalance(joDetail.getString("nABalance"));
                dcpDetail.setDelayAvg(joDetail.getDouble("nDelayAvg"));
                dcpDetail.setMonAmort(joDetail.getDouble("nMonAmort"));
                poDao.SaveDcpDetail(dcpDetail);
                Log.d(TAG, "DCP account no. " +dcpDetail.getAcctNmbr() + " has been saved.");
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public LiveData<List<EDCPCollectionDetail>> GetCollectionList(){
        return poDao.GetCollectionList();
    }

    public LiveData<EDCPCollectionDetail> GetAccountDetailForTransaction(String TransNox, String AccountNo, String EntryNox){
        return poDao.GetCollectionDetailForTransaction(TransNox, AccountNo, EntryNox);
    }

    public List<EDCPCollectionDetail> GetSearchList(String fsVal){
        try{
            List<EDCPCollectionDetail> loList = new ArrayList<>();

            EDCPCollectionMaster loMaster = poDao.GetColletionMasterForPosting();

            if(loMaster == null){
                message = "Unable to add client. Master collection does not exist or already posted.";
                return null;
            }

            String lsTransNox = loMaster.getTransNox();
            int lnEntryNox = poDao.GetNewEntryNox();

            if(fsVal == null){
                message = "Please enter name of client.";
                return null;
            }

            if(fsVal.trim().isEmpty()){
                message = "Please enter name of client.";
                return null;
            }

            JSONObject params = new JSONObject();
            params.put("value", fsVal);
            params.put("bycode", false);

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlGetArClient(),
                    params.toString(),
                    poHeaders.getHeaders());

            if(lsResponse == null){
                message = SERVER_NO_RESPONSE;
                return null;
            }

            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("error")){
                JSONObject loError = loResponse.getJSONObject("error");
                message = getErrorMessage(loError);
                return null;
            }

            JSONArray laJson = loResponse.getJSONArray("data");
            for(int x = 0; x < laJson.length(); x++) {
                JSONObject loJson = laJson.getJSONObject(x);
                EDCPCollectionDetail loDetail = new EDCPCollectionDetail();
                loDetail.setTransNox(lsTransNox);
                loDetail.setEntryNox(lnEntryNox);
                loDetail.setAcctNmbr(loJson.getString("sAcctNmbr"));
                loDetail.setFullName(loJson.getString("xFullName"));
                loDetail.setIsDCPxxx("0");
                loDetail.setMobileNo(loJson.getString("sMobileNo"));
                loDetail.setHouseNox(loJson.getString("sHouseNox"));
                loDetail.setAddressx(loJson.getString("sAddressx"));
                loDetail.setBrgyName(loJson.getString("sBrgyName"));
                loDetail.setTownName(loJson.getString("sTownName"));
                loDetail.setClientID(loJson.getString("sClientID"));
                loDetail.setSerialID(loJson.getString("sSerialID"));
                loDetail.setSerialNo(loJson.getString("sSerialNo"));
                loDetail.setLongitud(loJson.getDouble("nLongitud"));
                loDetail.setLatitude(loJson.getDouble("nLatitude"));
                loDetail.setDueDatex(loJson.getString("dDueDatex"));
                loDetail.setMonAmort(loJson.getDouble("nMonAmort"));
                loDetail.setLastPaym(loJson.getDouble("nLastPaym"));
                loDetail.setLastPaid(loJson.getString("dLastPaym"));
                loDetail.setAmtDuexx(loJson.getDouble("nAmtDuexx"));
                loDetail.setABalance(loJson.getString("nABalance"));
                loDetail.setDelayAvg(loJson.getDouble("nDelayAvg"));
                loList.add(loDetail);
            }

            return loList;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    public boolean AddCollection(EDCPCollectionDetail foVal){
        try{
            String TransNox = foVal.getTransNox();
            String AccoutNo = foVal.getAcctNmbr();
            EDCPCollectionDetail loDetail = poDao.GetCollectionForValidation(TransNox, AccoutNo);

            if(loDetail != null){
                message = "Account number already exist.";
                return false;
            }

            poDao.SaveDcpDetail(foVal);
            Log.d(TAG, "A new collection has been added to local device.");
            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    /**
     * this method is use by splashscreen to start location tracking service
     * @return
     */
    public boolean HasCollection(){
        EDCPCollectionMaster loMaster = poDao.GetColletionMasterForPosting();
        Log.d(TAG + "has collection", String.valueOf(loMaster != null));
        return loMaster != null;
    }

    public boolean HasRemittedCollection(){
        try{
            EDCPCollectionMaster loMaster = poDao.GetColletionMasterForPosting();

            if(loMaster == null){
                message = "No collection record found for posting.";
                return false;
            }

            String lsTransNo = loMaster.getTransNox();

            //Check if local device has record for paid collections...
            //Allow user to post collection if no paid collection was found...
            List<EDCPCollectionDetail> loPaid = poDao.GetPaidCollections(lsTransNo);
            if(loPaid == null){
                message = "No collection has paid.";
                Log.d(TAG, message);
                return true;
            }

            if(loPaid.size() == 0){
                message = "No collection has paid.";
                Log.d(TAG, message);
                return true;
            }

            //Check if local device has record for collection remittance...
            //Promp user to remit collection before posting...
            List<EDCP_Remittance> loRemit = poDao.GetCollectionRemittance(lsTransNo);
            if(loRemit == null){
                message = "Please remit collection first before posting.";
                return false;
            }

            if(loRemit.size() == 0){
                message = "Please remit collection first before posting.";
                return false;
            }

            double lnColCash = poDao.GetCollectedCashPayments(lsTransNo);
            double lnRmtCash = poDao.GetCashRemittedCollection(lsTransNo);
            double lnDiff = lnColCash - lnRmtCash;
            if(lnDiff > 0){
                message = "Please remit cash payments before posting.";
                return false;
            }

            double lnColChck = poDao.GetCollectedCheckPayments(lsTransNo);
            double lnRmtChck = poDao.GetCheckRemittedCollection(lsTransNo);

            lnDiff = lnColChck - lnRmtChck;
            if(lnDiff != 0){
                message = "Please remit check payments before posting.";
                return false;
            } else if(lnColChck != lnRmtChck){
                message = "Please remit all check payments before posting.";
                return false;
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean HasNotVisitedCollection(){
        try{
            EDCPCollectionMaster loMaster = poDao.GetColletionMasterForPosting();
            String lsTransNo = loMaster.getTransNox();
            int lnNotVstd = poDao.CheckForNotVisitedCollection(lsTransNo);
            if(lnNotVstd == 0){
                return false;
            }

            message = "Incomplete collection.";
            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public String PostCollection(String Remarks){
        try{
            EDCPCollectionMaster loMaster = poDao.GetColletionMasterForPosting();

            if(loMaster == null){
                message = "Unable to find record to post.";
                return null;
            }

            String lsTransNo = loMaster.getTransNox();
            Log.d(TAG, "Posting dcp details with transaction no. " + lsTransNo);

            List<EDCPCollectionDetail> loNoVstd = poDao.GetNotVisitedCollection(lsTransNo);
            if(loNoVstd == null){
                Log.d(TAG, "No record for not visited collection.");
            }

            if(loNoVstd.size() > 0){
                for(int x = 0; x < loNoVstd.size(); x++){
                    EDCPCollectionDetail notVstd = loNoVstd.get(x);
                    notVstd.setRemarksx(Remarks);
                    notVstd.setRemCodex("NV");
                    notVstd.setModified(AppConstants.DATE_MODIFIED());
                    poDao.UpdateCollectionDetail(notVstd);
                    Log.d(TAG, "Not visited collection has been updated.");
                }
            }

            List<EDCPCollectionDetail> loDetail = poDao.GetCollectionDetailForPosting(lsTransNo);

            if(loDetail == null){
                message = "No collection detail found for posting.";
                return null;
            }

            if(loDetail.size() == 0){
                message = "No collection detail found for posting.";
                return null;
            }

            boolean isSuccess = true;
            for(int x = 0; x < loDetail.size(); x++){
                EDCPCollectionDetail detail = loDetail.get(x);

                JSONObject params = new JSONObject();
                JSONObject loData = new JSONObject();

                String lsRemCode = detail.getRemCodex();

                EImageInfo loImage;

                switch (lsRemCode) {
                    case "PAY":
                        loData.put("sPRNoxxxx", detail.getPRNoxxxx());
                        loData.put("nTranAmtx", detail.getTranAmtx());
                        loData.put("nDiscount", detail.getDiscount());
                        loData.put("nOthersxx", detail.getOthersxx());
                        loData.put("cTranType", detail.getTranType());
                        loData.put("nTranTotl", detail.getTranTotl());
                        loData.put("sRemarksx", detail.getRemarksx());
                        params.put("sRemCodex", detail.getRemCodex());
                        params.put("dModified", detail.getModified());
                        break;

                    case "CNA":
                        String lsClientID = detail.getClientID();

                        JSONObject address = poAddress.GetAddressDetailForPosting(lsClientID);
                        if(address == null){
                            message = poAddress.getMessage();
                            Log.e(TAG, message);
                        } else {
                            loData.put("Address", address);
                        }

                        JSONObject mobile = poMobile.GetMobileDetailForPosting(lsClientID);
                        if(mobile == null){
                            message = poMobile.getMessage();
                            Log.e(TAG, message);
                        } else {
                            loData.put("Mobile", mobile);
                        }

                        break;

                    case "PTP":
                        loData.put("cApntUnit", detail.getApntUnit());
                        loData.put("sBranchCd", detail.getBranchCd());
                        loData.put("dPromised", detail.getPromised());
                        params.put("sRemCodex", detail.getRemCodex());
                        params.put("dModified", detail.getModified());

                        loImage = poDao.GetDcpImageForPosting(
                                detail.getTransNox(),
                                detail.getAcctNmbr());

                        if(loImage != null){

                            if(!poConfig.getTestStatus()){
                                loData.put("sImageNme", loImage.getImageNme());
                                loData.put("sSourceCD", loImage.getSourceCD());
                                loData.put("nLongitud", loImage.getLongitud());
                                loData.put("nLatitude", loImage.getLatitude());

                                String lsImageID = poImage.UploadImage(loImage.getTransNox());
                                if(lsImageID == null){
                                    Log.e(TAG, poImage.getMessage());
                                }
                            }
                        }
                        break;

                    case "LUn":
                    case "TA":
                    case "FO":
                        EClientUpdate loClient = poClient.getClientUpdateInfoForPosting(lsTransNo, detail.getAcctNmbr());

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

                        loImage = poImage.getDCPImageInfoForPosting(lsTransNo, detail.getAcctNmbr());
                        if(loImage != null || loImage.getImageNme() != null) {
                            if(!poConfig.getTestStatus()) {
                                loData.put("sImageNme", loImage.getImageNme());
                                loData.put("sSourceCD", loImage.getSourceCD());
                                loData.put("nLongitud", loImage.getLongitud());
                                loData.put("nLatitude", loImage.getLatitude());
                            }
                        }
                        break;

                    default:
                        loImage = poDao.GetDcpImageForPosting(
                                detail.getTransNox(),
                                detail.getAcctNmbr());

                        if(loImage != null){
                            Log.d(TAG, "Not visited image found.");
                            if(!poConfig.getTestStatus()){
                                loData.put("sImageNme", loImage.getImageNme());
                                loData.put("sSourceCD", loImage.getSourceCD());
                                loData.put("nLongitud", loImage.getLongitud());
                                loData.put("nLatitude", loImage.getLatitude());

                                String lsImageID = poImage.UploadImage(loImage.getTransNox());
                                if(lsImageID == null){
                                    Log.e(TAG, poImage.getMessage());
                                }
                            }
                        } else {
                            Log.e(TAG, "Not visited image not found.");
                        }
                }
                loData.put("sRemarksx", detail.getRemarksx());
                params.put("sRemCodex", detail.getRemCodex());
                params.put("dModified", detail.getModified());

                params.put("sTransNox", detail.getTransNox());
                params.put("nEntryNox", detail.getEntryNox());
                params.put("sAcctNmbr", detail.getAcctNmbr());

                params.put("sJsonData", loData);
                params.put("dReceived", "");
                params.put("sUserIDxx", poSession.getUserID());
                params.put("sDeviceID", poDevice.getDeviceID());
                Log.d(TAG, "DCP posting data: " + params);

                String lsResponse = WebClient.sendRequest(
                        poApi.getUrlDcpSubmit(),
                        params.toString(),
                        poHeaders.getHeaders());

                if(lsResponse == null){
                    message = SERVER_NO_RESPONSE;
                    Log.e(TAG, message);
                    Thread.sleep(1000);
                    isSuccess = false;
                    continue;
                }

                JSONObject loResponse = new JSONObject(lsResponse);
                String lsResult = loResponse.getString("result");
                if(lsResult.equalsIgnoreCase("error")){
                    JSONObject loError = loResponse.getJSONObject("error");
                    message = getErrorMessage(loError);
                    Log.e(TAG, message);
                    Thread.sleep(1000);
                    isSuccess = false;
                    continue;
                }

                detail.setSendStat("1");
                detail.setTranStat("2");
                detail.setSendDate(AppConstants.DATE_MODIFIED());
                poDao.UpdateCollectionDetail(detail);
                Log.d(TAG, "Collection detail has been posted.");
                Thread.sleep(1000);
            }

            if(!isSuccess){
                Log.d(TAG, "Failed to upload all collection details");
                return null;
            }

            //wait for a second before calling Post dcp master to avoid reload failed error message
            Thread.sleep(1000);
            Log.d(TAG, "All collection details has been uploaded. Post dcp master.");
            return lsTransNo;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    public boolean PostDcpMaster(String TransNox){
        try{
            JSONObject loJson = new JSONObject();
            loJson.put("sTransNox", TransNox);

            String lsResponse = WebClient.sendRequest(
                    poApi.getUrlPostDcpMaster(),
                    loJson.toString(),
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

            poDao.UpdatePostedDcpMaster(TransNox, AppConstants.DATE_MODIFIED());

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }

    public boolean ClearDCPData(){
        try{
            poDao.ClearMasterDCP();
            poDao.ClearDetailDCP();
            poDao.ClearDCPRemittance();
            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return false;
        }
    }
}
