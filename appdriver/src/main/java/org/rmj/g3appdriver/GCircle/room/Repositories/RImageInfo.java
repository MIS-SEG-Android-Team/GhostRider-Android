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

import static org.rmj.g3appdriver.lib.Firebase.CrashReportingUtil.reportException;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import androidx.lifecycle.LiveData;

import org.json.JSONObject;
import org.rmj.apprdiver.util.WebFile;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DImageInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.dev.Api.WebFileServer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RImageInfo {
    private static final String TAG = "DB_Image_Repository";
    private final DImageInfo poDao;
    private final AppConfigPreference poConfig;
    private final EmployeeSession poSession;
    private final AppTokenManager poToken;
    private String message;

    public RImageInfo(Application instance){
        this.poDao = GGC_GCircleDB.getInstance(instance).ImageInfoDao();
        this.poConfig = AppConfigPreference.getInstance(instance);
        this.poSession = EmployeeSession.getInstance(instance);
        this.poToken = new AppTokenManager(instance);
    }

    public String getMessage() {
        return message;
    }

    public String getClientToken(){

        String lsClient = poToken.GetClientToken();

        if(lsClient == null){
            Log.e(TAG, poToken.getMessage());
            message = "No generated client token to upload image.";
            return null;
        }

        String lsAccess = poToken.GetAccessToken(lsClient);

        if(lsAccess == null){
            Log.e(TAG, message);
            message = "No generated access token to upload image.";
            return null;
        }

        return lsAccess;
    }

    public String getClientToken(String fsClientID, String fsUserID){

        String lsClient = poToken.GetClientToken(fsClientID, fsUserID);
        if(lsClient == null){
            Log.e(TAG, poToken.getMessage());
            message = "No generated client token to download image.";
            return null;
        }

        String lsAccess = poToken.GetAccessToken(lsClient);

        if(lsAccess == null){
            Log.e(TAG, message);
            message = "No generated access token to download image.";
            return null;
        }

        return lsAccess;
    }

    public LiveData<EImageInfo> getImageLocation(String sDtlSrcNo, String sImageNme) {
        return poDao.getImageLocation(sDtlSrcNo, sImageNme);
    }

    public List<EImageInfo> getUnsentSelfieLogImageList(){
        return poDao.getUnsentLoginImageInfo();
    }

    /**
     *
     * @return returns a LiveData List of all unsent image info...
     */
    public LiveData<List<EImageInfo>> getUnsentImageList(){
        return poDao.getUnsentDCPImageInfoList();
    }

    public EImageInfo getDCPImageInfoForPosting(String TransNox, String AccntNo){
        return poDao.getDCPImageInfoForPosting(TransNox, AccntNo);
    }

    public String CheckTokenAvailable(){
        try{
            String lsClient = poToken.GetClientToken();

            if(lsClient == null){
                message = poToken.getMessage();
                return null;
            }

            String lsAccess = poToken.GetAccessToken(lsClient);

            if(lsAccess == null){
                message = poToken.getMessage();
                return null;
            }

            return lsAccess;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    public String SaveCreditAppDocument(String TransNox, String FileCode, String FileName, String FileLoct){
        try{
            EImageInfo loImage = new EImageInfo();
            String lsTransNo = CreateUniqueID();
            loImage.setTransNox(lsTransNo);
            loImage.setFileCode(FileCode);
            loImage.setSourceNo(TransNox); //Credit App TransNox
            loImage.setDtlSrcNo(TransNox); //Credit App TransNox
            loImage.setSourceCD("COAD");
            loImage.setMD5Hashx(WebFileServer.createMD5Hash(FileLoct));
            loImage.setCaptured(AppConstants.DATE_MODIFIED());
            loImage.setImageNme(FileName);
            loImage.setFileLoct(FileLoct);
            loImage.setLatitude("0.0");
            loImage.setLongitud("0.0");
            poDao.SaveImageInfo(loImage);
            Log.d(TAG, "Selfie has been saved.");
            return lsTransNo;

        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    /**
     *
     * @param args image file name
     * @param args1 image file location
     * @param args2 current device location (Latitude)
     * @param args3 current device location (Longitude)
     * @return returns transaction no. if operation succeed, else null if operation fails. Call getMessage() to get error message.
     */
    public String SaveSelfieLogImage(String args, String args1, String args2, String args3){
        try{
            EImageInfo loImage = new EImageInfo();
            String lsTransNo = CreateUniqueID();
            loImage.setTransNox(lsTransNo);
            loImage.setFileCode("0021");
            loImage.setSourceNo(poSession.getClientId());
            loImage.setDtlSrcNo(poSession.getUserID());
            loImage.setSourceCD("LOGa");
            loImage.setMD5Hashx(WebFileServer.createMD5Hash(args1));
            loImage.setCaptured(AppConstants.DATE_MODIFIED());
            loImage.setImageNme(args);
            loImage.setFileLoct(args1);
            loImage.setLatitude(args2);
            loImage.setLongitud(args3);
            poDao.SaveImageInfo(loImage);
            Log.d(TAG, "Selfie has been saved.");

            return lsTransNo;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    /**
     *
     * @param args client account no.
     * @param args1 collection transaction no.
     * @param args2 image file name.
     * @param args3 image file location.
     * @param args4 current device location(Latitude)
     * @param args5 current device location(Longitude)
     * @return transaction no. if operation success, else null if operation fails. Call getMessage() get error message.
     */
    public String SaveDcpImage(String args, String args1, String args2, String args3, String args4, String args5){
        try{
            EImageInfo loImage = new EImageInfo();
            String lsTransNo = CreateUniqueID();
            loImage.setTransNox(lsTransNo);
            loImage.setDtlSrcNo(args);
            loImage.setSourceNo(args1);
            loImage.setSourceCD("DCPa");
            loImage.setImageNme(args2);
            loImage.setFileLoct(args3);
            loImage.setFileCode("0020");
            loImage.setLatitude(args4);
            loImage.setLongitud(args5);
            loImage.setMD5Hashx(WebFileServer.createMD5Hash(args3));
            loImage.setCaptured(AppConstants.DATE_MODIFIED());

            poDao.SaveImageInfo(loImage);
            Log.d(TAG, "DCP Selfie has been saved.");
            return lsTransNo;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    /**
     *
     * @param args Credit_Online_Application_CI transaction no.
     * @param args1 image file name
     * @param args2 image file location
     * @param args3 current device location (Latitude)
     * @param args4 current device location (Longitude)
     * @return transaction no. if operation success, else null if operation fails. Call getMessage() get error message.
     */
    public String SaveCIImage(String args, String args1, String args2, String args3, String args4){
        try{
            String lsTransNo;
            EImageInfo loDetail = poDao.CheckImageForCIExist(args, args);
            if(loDetail == null) {
                EImageInfo loImage = new EImageInfo();
                lsTransNo = CreateUniqueID();
                loImage.setTransNox(lsTransNo);
                loImage.setDtlSrcNo(args);
                loImage.setSourceNo(args);
                loImage.setSourceCD("COAD");
                loImage.setImageNme(args1);
                loImage.setFileLoct(args2);
                loImage.setFileCode("CI001");
                loImage.setLatitude(args3);
                loImage.setLongitud(args4);
                loImage.setMD5Hashx(WebFileServer.createMD5Hash(loImage.getFileLoct()));
                loImage.setCaptured(AppConstants.DATE_MODIFIED());
                poDao.SaveImageInfo(loImage);
                Log.d(TAG, "CI tagging image has been saved.");
                return lsTransNo;
            }

            loDetail.setDtlSrcNo(args);
            loDetail.setSourceNo(args);
            loDetail.setSourceCD("COAD");
            loDetail.setImageNme(args1);
            loDetail.setFileLoct(args2);
            loDetail.setFileCode("CI001");
            loDetail.setLatitude(args3);
            loDetail.setLongitud(args4);
            loDetail.setMD5Hashx(WebFileServer.createMD5Hash(loDetail.getFileLoct()));
            loDetail.setCaptured(AppConstants.DATE_MODIFIED());
            Log.d(TAG, "CI tagging image has been updated.");
            lsTransNo = loDetail.getTransNox();
            return lsTransNo;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    /**
     *
     * @param args Credit_Online_Application transaction no.
     * @param args1 FileCode of the document scanned.
     * @param args2 image file name
     * @param args3 image file location
     * @param args4 current device location (Latitude)
     * @param args5 current device location (Longitude)
     * @return transaction no. if operation success, else null if operation fails. Call getMessage() get error message.
     */
    public String SaveCreditAppDocumentsImage(String args, String args1, String args2, String args3, String args4, String args5){
        try{
            String lsTransNo;
            EImageInfo loDetail = poDao.CheckCreditAppDocumentIfExist(args, args1);
            if(loDetail == null) {
                lsTransNo = CreateUniqueID();
                EImageInfo loImage = new EImageInfo();
                loImage.setTransNox(lsTransNo);
                loImage.setDtlSrcNo(args);
                loImage.setSourceNo(args);
                loImage.setSourceCD("COAD");
                loImage.setFileCode(args1);
                loImage.setImageNme(args2);
                loImage.setFileLoct(args3);
                loImage.setLatitude(args4);
                loImage.setLongitud(args5);
                loImage.setMD5Hashx(WebFileServer.createMD5Hash(loImage.getFileLoct()));
                loImage.setCaptured(AppConstants.DATE_MODIFIED());
                poDao.SaveImageInfo(loImage);
                Log.d(TAG, "Document scan image has been saved.");
                return lsTransNo;
            }

            loDetail.setDtlSrcNo(args);
            loDetail.setSourceNo(args);
            loDetail.setSourceCD("COAD");
            loDetail.setFileCode(args1);
            loDetail.setImageNme(args2);
            loDetail.setFileLoct(args3);
            loDetail.setLatitude(args4);
            loDetail.setLongitud(args5);
            loDetail.setMD5Hashx(WebFileServer.createMD5Hash(loDetail.getFileLoct()));
            loDetail.setCaptured(AppConstants.DATE_MODIFIED());
            poDao.update(loDetail);
            Log.d(TAG, "Document scan image has been updated.");
            lsTransNo = loDetail.getTransNox();
            return lsTransNo;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    /**
     *
     * @param args SSDD transaction number
     * @param args1 image file name
     * @param args2 image file location
     * @param args3 current device location (Longitude)
     * @param args4 current device location (Latitude)
     * @param args5 category id / file code
     * @return returns transaction no. if operation succeed, else null if operation fails. Call getMessage() to get error message.
     */
    public String SaveSSDDImage(String args, String args1, String args2, String args3, String args4, String args5){

        try{
            EImageInfo loImage = new EImageInfo();
            String lsTransNo = CreateUniqueID();

            loImage.setTransNox(lsTransNo);
            loImage.setsClientID(poSession.getClientId());
            loImage.setDtlSrcNo(poSession.getUserID());
            loImage.setSourceCD("SSDD");
            loImage.setMD5Hashx(WebFileServer.createMD5Hash(args2));
            loImage.setCaptured(AppConstants.DATE_MODIFIED());
            loImage.setSourceNo(args);
            loImage.setImageNme(args1);
            loImage.setFileLoct(args2);
            loImage.setLongitud(args3);
            loImage.setLatitude(args4);
            loImage.setFileCode(args5);

            poDao.SaveImageInfo(loImage);
            Log.d(TAG, "SSDD Image has been saved.");

            return lsTransNo;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }

    }

    /**
     *
     * @param args1 transaction number from downloaded attachment
     * @param args2 source code
     * @param args3 source number
     * @param args4 filename
     * @param args5 file location
     * @param args6 md5 hash
     * @param args7 file code
     * @param args8 client id
     * @param args9 detail sourcen oo
     * @return returns transaction no. if operation succeed, else null if operation fails. Call getMessage() to get error message.
     */
    public String SaveCASAttachments(String args1, String args2, String args3, String args4, String args5, String args6, String args7, String args8, String args9){

        try{
            EImageInfo loImage = new EImageInfo();
            loImage.setTransNox(args1);
            loImage.setSourceCD(args2);
            loImage.setSourceNo(args3);
            loImage.setImageNme(args4);
            loImage.setFileLoct(args5);
            loImage.setMD5Hashx(args6);
            loImage.setFileCode(args7);
            loImage.setsClientID(args8);
            loImage.setDtlSrcNo(args9);
            loImage.setSendStat("1"); //consider that this method is only used for downloading attachments from cas request
            loImage.setLatitude("0.00000");
            loImage.setLongitud("0.00000");

            poDao.SaveImageInfo(loImage);
            Log.d(TAG, "SSDD Image has been saved.");

            return args1;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }

    }

    /**
     *
     * @param args Branc_Visit_Master Transaction Number
     * @param args1 image file name
     * @param args2 image file location
     * @param args3 current device location (Longitude)
     * @param args4 current device location (Latitude)
     * @param args5 category id / file code
     * @return returns transaction no. if operation succeed, else null if operation fails. Call getMessage() to get error message.
     */
    public String SaveBranchVisitImage(String args, String args1, String args2, String args3, String args4, String args5){

        try{
            EImageInfo loImage = new EImageInfo();
            String lsTransNo = CreateUniqueID();

            loImage.setTransNox(lsTransNo);
            loImage.setsClientID(poSession.getClientId());
            loImage.setDtlSrcNo(poSession.getUserID());
            loImage.setSourceCD("BVS");
            loImage.setMD5Hashx(WebFileServer.createMD5Hash(args2));
            loImage.setCaptured(AppConstants.DATE_MODIFIED());
            loImage.setSourceNo(args);
            loImage.setImageNme(args1);
            loImage.setFileLoct(args2);
            loImage.setLongitud(args3);
            loImage.setLatitude(args4);
            loImage.setFileCode(args5);

            poDao.SaveImageInfo(loImage);
            Log.d(TAG, "Branch Visit Image has been saved.");

            return lsTransNo;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }

    }

    /**
     *
     * @param fsVal transaction no of image needed to upload.
     * @return true if operation succeed, else false if operation fails. Call getMessage() to get error message.
     */
    public String UploadImage(String fsVal){
        try{
            if(poConfig.getTestStatus()){
                message = "This feature is not available for testing...";
                return fsVal;
            }

            EImageInfo loDetail = poDao.GetImageInfo(fsVal);
            if(loDetail == null){
                message = "Unable to find image to upload.";
                return null;
            }

            if(loDetail.getSendStat().equalsIgnoreCase("1")){
                message = "Image is already uploaded.";
                return fsVal;
            }

            //initialize client access, set static token for SSDD Images to be downloaded BY ANY USERS
            String lsAccess = getClientToken();
            if(lsAccess == null){
                return null;
            }

            org.json.simple.JSONObject loUpload = WebFileServer.UploadFile(
                    loDetail.getFileLoct(),
                    lsAccess,
                    loDetail.getFileCode(),
                    loDetail.getDtlSrcNo(),
                    loDetail.getImageNme(),
                    poSession.getBranchCode(),
                    loDetail.getSourceCD(),
                    loDetail.getTransNox(),
                    "");

            if (loUpload == null) {
                message = SERVER_NO_RESPONSE;
                reportException(poSession.getUserID(), message);
                return null;
            }

            JSONObject loResult = new JSONObject(loUpload.toJSONString());
            if(loResult.has("result")){
                String lsImgResult = (String) loUpload.get("result");
                if (lsImgResult.equalsIgnoreCase("error")) {
                    JSONObject loError = loResult.getJSONObject("error");
                    message = getErrorMessage(loError);
                    reportException(poSession.getUserID(), message);
                    return null;
                }
            } else {
                String lsImgResult = (String) loUpload.get("rhsult");
                if (lsImgResult.equalsIgnoreCase("error")) {
                    JSONObject loError = loResult.getJSONObject("error");
                    message = getErrorMessage(loError);
                    reportException(poSession.getUserID(), message);
                    return null;
                }
            }


            String lsTransnox = (String) loUpload.get("sTransNox");
            poDao.updateImageInfo(lsTransnox, AppConstants.DATE_MODIFIED(), loDetail.getTransNox());
            return lsTransnox;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            reportException(poSession.getUserID(), message);
            return null;
        }
    }


    /**
     *
     * @param fsVal transaction no of image needed to download.
     * @return true if operation succeed, else false if operation fails. Call getMessage() to get error message.
     */
    public Boolean DownloadImageFile(String fsVal){

        try {

            if(poConfig.getTestStatus()){
                message = "This feature is not available for testing...";
                return false;
            }

            EImageInfo loDetail = poDao.GetImageInfo(fsVal);
            if(loDetail == null){
                message = "Unable to find image to download.";
                return false;
            }

            //initialize client access, set static token for image to be downloaded BY ANY USERS
            String lsAccess = getClientToken(loDetail.getsClientID(), loDetail.getDtlSrcNo());
            if(lsAccess == null){
                message = "Unable to generate access key";
                return false;
            }

            org.json.simple.JSONObject loDownload = WebFile.DownloadFile(lsAccess, loDetail.getFileCode(), loDetail.getDtlSrcNo(), loDetail.getImageNme(), loDetail.getSourceCD(), loDetail.getTransNox(), "");
            if (loDownload == null){
                message = "No response from Web Server";
                return false;
            }
            if (loDownload.get("result").equals("success")){

                org.json.simple.JSONObject loResult = (org.json.simple.JSONObject) loDownload.get("payload");
                if (WebFile.Base64ToFile(loResult.get("data").toString(), loResult.get("hash").toString(), loDetail.getFileLoct().substring(0, loDetail.getFileLoct().lastIndexOf("/") + 1), loDetail.getImageNme() )){
                    message = "File downloaded successfully";
                    return true;
                }
                message = "Failed to download file";
                return false;
            }
            message = ((org.json.simple.JSONObject) loDownload.get("error")).get("message").toString();
            return false;

        }catch (Exception e){
            message = e.getMessage();
            return false;
        }
    }

    /**
     *
     * @param fsVal source no of image needed to download.
     * @return true if operation succeed, else false if operation fails. Call getMessage() to get error message.
     */
    public Boolean DownloadImageSource(String fsVal){

        try {

            if(poConfig.getTestStatus()){
                message = "This feature is not available for testing...";
                return false;
            }

            EImageInfo loDetail = poDao.GetImageInfo(fsVal);
            if(loDetail == null){
                message = "Unable to find image to download.";
                return false;
            }

            //initialize client access, set static token for image to be downloaded BY ANY USERS
            String lsAccess = getClientToken();
            if(lsAccess == null){
                message = "Unable to generate access key";
                return false;
            }
            Log.d("Access Key", lsAccess);

            org.json.simple.JSONObject loDownload = WebFile.DownloadFile(lsAccess, loDetail.getFileCode(), loDetail.getDtlSrcNo(), loDetail.getImageNme(), loDetail.getSourceCD(), loDetail.getSourceNo(), "");
            if (loDownload == null){
                message = "No response from Web Server";
                return false;
            }
            if (loDownload.get("result").equals("success")){

                org.json.simple.JSONObject loResult = (org.json.simple.JSONObject) loDownload.get("payload");
                if (WebFile.Base64ToFile(loResult.get("data").toString(), loResult.get("hash").toString(), loDetail.getFileLoct().substring(0, loDetail.getFileLoct().lastIndexOf("/") + 1), loDetail.getImageNme() )){
                    message = "File downloaded successfully";
                    return true;
                }
                message = "Failed to download file";
                return false;
            }
            message = ((org.json.simple.JSONObject) loDownload.get("error")).get("message").toString();
            return false;

        }catch (Exception e){
            message = e.getMessage();
            return false;
        }
    }

    private String CreateUniqueID(){
        String lsUniqIDx = "";
        try{
            String lsBranchCd = "MX01";
            String lsCrrYear = new SimpleDateFormat("yy", Locale.getDefault()).format(new Date());
            StringBuilder loBuilder = new StringBuilder(lsBranchCd);
            loBuilder.append(lsCrrYear);

            int lnLocalID = poDao.GetRowsCountForID() + 1;
            String lsPadNumx = String.format("%05d", lnLocalID);
            loBuilder.append(lsPadNumx);
            lsUniqIDx = loBuilder.toString();
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        Log.d(TAG, lsUniqIDx);
        return lsUniqIDx;
    }
}
