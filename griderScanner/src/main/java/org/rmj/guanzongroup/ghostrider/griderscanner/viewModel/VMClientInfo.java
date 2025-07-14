/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.griderScanner
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:18 PM
 */

package org.rmj.guanzongroup.ghostrider.griderscanner.viewModel;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.rmj.g3appdriver.dev.Database.GCircle.DataAccessObject.DCreditApplicationDocuments;
import org.rmj.g3appdriver.dev.Database.GCircle.Entities.ECreditApplicationDocuments;
import org.rmj.g3appdriver.dev.Database.GCircle.Entities.EFileCode;
import org.rmj.g3appdriver.dev.Database.GCircle.Entities.EImageInfo;
import org.rmj.g3appdriver.GCircle.room.Repositories.RCollectionUpdate;
import org.rmj.g3appdriver.GCircle.room.Repositories.RCreditApplicationDocument;
import org.rmj.g3appdriver.GCircle.room.Repositories.RDailyCollectionPlan;
import org.rmj.g3appdriver.GCircle.room.Repositories.RFileCode;
import org.rmj.g3appdriver.GCircle.room.Repositories.RImageInfo;
import org.rmj.g3appdriver.dev.HttpHeaders;
import org.rmj.g3appdriver.dev.Telephony;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.SessionManager;
import org.rmj.g3appdriver.etc.WebFileServer;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.guanzongroup.ghostrider.griderscanner.helpers.ScannerConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VMClientInfo extends AndroidViewModel {
    private static final String TAG = VMClientInfo.class.getSimpleName();
    private final RFileCode peFileCode;
    private final Application instance;
    private final LiveData<List<EFileCode>> fileCodeList;
    private LiveData<List<DCreditApplicationDocuments.ApplicationDocument>> docFileList;
    private final RCreditApplicationDocument poDocument;
    private final RImageInfo poImage;
    private List<EImageInfo> imgInfo = new ArrayList<>();
    private List<EImageInfo> imgListInfo = new ArrayList<>();
    private final MutableLiveData<String> sTransNox = new MutableLiveData<>();
    private final MutableLiveData<String> sImgName = new MutableLiveData<>();
    private final MutableLiveData<String> sFileCode = new MutableLiveData<>();
    private final MutableLiveData<Integer> nEntryNox = new MutableLiveData<>();
    private final MutableLiveData<String> sFileLoc = new MutableLiveData<>();
    private final AppConfigPreference poConfig;

    public VMClientInfo(@NonNull Application application) {
        super(application);
        this.instance = application;
        peFileCode = new RFileCode(application);
        this.fileCodeList = peFileCode.getAllFileCode();
        poDocument = new RCreditApplicationDocument(application);
        poImage = new RImageInfo(application);
        poConfig = AppConfigPreference.getInstance(instance);
    }
    public void setImgParameter(String fsTransNox, int fnEntryNox, String fsFileCode, String fsImgname, String fsFileLoc){
        try{
            this.sTransNox.setValue(fsTransNox);
            this.nEntryNox.setValue(fnEntryNox);
            this.sFileCode.setValue(fsFileCode);
            this.sImgName.setValue(fsImgname);
            this.sFileLoc.setValue(fsFileLoc);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void setsTransNox(String transNox){
        this.docFileList = poDocument.getDocument(transNox);
        this.imgListInfo =poImage.getImageListInfo(transNox).getValue();
        this.sTransNox.setValue(transNox);
    }
    public LiveData<List<DCreditApplicationDocuments.ApplicationDocument>> getDocumentInfos(String TransNox){
        return this.poDocument.getDocumentInfos(TransNox);
    }
    public LiveData<List<EFileCode>> getDocumentInfoByFile(){
        return this.poDocument.getDocumentInfoByFile();
    }

    public void initAppDocs(String transNox){
        try{
            poDocument.insertDocumentsInfo(transNox);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void saveDocumentInfoFromCamera(String TransNox, String FileCD){
        try{
            poDocument.updateDocumentsInfo(TransNox, FileCD);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void updateDocumentInfoFromServer(String TransNox, String FileCD){
        try{
            poDocument.updateDocumentsInfoByFile(TransNox, FileCD);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveImageInfo(EImageInfo foImage){
        try{
            boolean isImgExist = false;
            String tansNo = "";
            for (int i = 0; i < imgInfo.size(); i++){
                if(foImage.getSourceNo().equalsIgnoreCase(imgInfo.get(i).getSourceNo())
                        && foImage.getDtlSrcNo().equalsIgnoreCase(imgInfo.get(i).getDtlSrcNo())
                ) {
                    tansNo = imgInfo.get(i).getTransNox();
//                    File finalFile = new File(getRealPathFromURI(imgInfo.get(i).getFileLoct()));
//                    finalFile.delete();
                    isImgExist = true;
                }
            }
            if (isImgExist){
                foImage.setTransNox(tansNo);
//                poImage.updateImageInfo(foImage);
            }else{
//                foImage.setTransNox(poImage.getImageNextCode());
//                poImage.insertImageInfo(foImage);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void PostDocumentScanDetail(ECreditApplicationDocuments poDocumentsInfo, ViewModelCallBack callback) {
        try {
            new PostDocumentScanDetail(instance, poDocumentsInfo, poDocumentsInfo.getTransNox(), poDocumentsInfo.getFileCode(), poDocumentsInfo.getEntryNox(), poDocumentsInfo.getImageNme(), poDocumentsInfo.getFileLoc(), callback).execute();
        } catch (Exception e) {
        }
    }

    public class PostDocumentScanDetail extends AsyncTask<Void, Void, String> {
        private final ConnectionUtil poConn;
        private final ViewModelCallBack callback;
        private final SessionManager poUser;
        private final RDailyCollectionPlan poDcp;
        private final RImageInfo poImage;
        private final Telephony poTelephony;
        private final HttpHeaders poHeaders;
        private final RCollectionUpdate rCollect;
        private final String psTransNox;
        private final String psFileCode;
        private final int pnEntryNox;
        private final String psImageName;
        private final String psFileLoc;
        private final ECreditApplicationDocuments poDocumentsInfos;

        public PostDocumentScanDetail(Application instance, ECreditApplicationDocuments poDocumentsInfo, String TransNox, String FilCode, int EntryNox, String ImgName,
                                      String FileLoc, ViewModelCallBack callback) {
            this.poConn = new ConnectionUtil(instance);
            this.poUser = new SessionManager(instance);
            this.pnEntryNox = EntryNox;
            this.psImageName = ImgName;
            this.psTransNox = TransNox;
            this.psFileCode = FilCode;
            this.psFileLoc = FileLoc;
            this.poDocumentsInfos = poDocumentsInfo;
            this.callback = callback;
            this.poDcp = new RDailyCollectionPlan(instance);
            this.poImage = new RImageInfo(instance);
            this.poTelephony = new Telephony(instance);
            this.poHeaders = HttpHeaders.getInstance(instance);
            this.rCollect = new RCollectionUpdate(instance);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... voids) {
            String lsResult;
            try {
                if (!poConn.isDeviceConnected()) {
                    lsResult = AppConstants.NO_INTERNET();
                } else {

                    String lsClient = WebFileServer.RequestClientToken(poConfig.ProducID(), poUser.getClientId(), poUser.getUserID());
                    String lsAccess = WebFileServer.RequestAccessToken(lsClient);

                    if (lsClient.isEmpty() || lsAccess.isEmpty()) {
                        lsResult = AppConstants.LOCAL_EXCEPTION_ERROR("Failed to request generated Client or Access token.");
                    } else {
//                        JSONObject loUpload = WebFileServer.UploadFile(psFileLoc,
//                                lsAccess,
//                                psFileCode,
//                                psTransNox,
//                                psImageName,
//                                poUser.getBranchCode(),
//                                "COAD",
//                                psTransNox,
//                                "");

//                        String lsResponse = (String) loUpload.get("result");
//                        lsResult = String.valueOf(loUpload);
//
//                        if (Objects.requireNonNull(lsResponse).equalsIgnoreCase("success")) {
//                            String lsTransNo = (String) loUpload.get("sTransNox");
//                            poImage.updateImageInfo(lsTransNo, psTransNox);
//                            updateDocumentInfoFromServer(psTransNox,psFileCode);
//
//                        }
                        Thread.sleep(1000);
                    }
                }
                lsResult = "";
            } catch (Exception e) {
                e.printStackTrace();
                lsResult = AppConstants.LOCAL_EXCEPTION_ERROR(e.getMessage());
            }
            return lsResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            callback.OnStartSaving();
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
//                JSONParser loParser = new JSONParser();
//                JSONObject loJson = (JSONObject) loParser.parse(s);
//                if ("success".equalsIgnoreCase((String) loJson.get("result"))) {
//                    callback.OnSuccessResult(new String[]{ScannerConstants.FileDesc + " has been posted successfully."});
//                } else {
//                    JSONObject loError = (JSONObject) loParser.parse((String) loJson.get("error"));
//                    callback.OnFailedResult((String) loError.get("message"));
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //mac 2021.03.30
    //  download image from server
    public void DownloadDocumentFile(DCreditApplicationDocuments.ApplicationDocument poDocumentsInfo, String fsSourceNo, ViewModelCallBack callback) {
        try {
            new DownloadDocumentFile(instance, poDocumentsInfo, fsSourceNo, callback).execute();
        } catch (Exception e) {
        }
    }

    public class DownloadDocumentFile extends AsyncTask<Void, Void, String> {
        private final ConnectionUtil poConn;
        private final ViewModelCallBack callback;
        private final SessionManager poUser;
        private final RImageInfo poImage;
        private final DCreditApplicationDocuments.ApplicationDocument poFileInfo;
        private final String psSourceNo;

        public DownloadDocumentFile(Application instance, DCreditApplicationDocuments.ApplicationDocument foFileInfo, String fsSourceNo, ViewModelCallBack callback) {
            this.poConn = new ConnectionUtil(instance);
            this.poUser = new SessionManager(instance);
            this.poFileInfo = foFileInfo;
            this.psSourceNo = fsSourceNo;
            this.callback = callback;
            this.poImage = new RImageInfo(instance);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... voids) {
            String lsResult;
            try {
                if (!poConn.isDeviceConnected()) {
                    lsResult = AppConstants.NO_INTERNET();
                } else {

                    String lsClient = WebFileServer.RequestClientToken(poConfig.ProducID(), poUser.getClientId(), poUser.getUserID());
                    String lsAccess = WebFileServer.RequestAccessToken(lsClient);

                    if (lsClient.isEmpty() || lsAccess.isEmpty()) {
                        lsResult = AppConstants.LOCAL_EXCEPTION_ERROR("Failed to request generated Client or Access token.");
                    } else {
                        String imageName = poFileInfo.sTransNox + "_" + poFileInfo.nEntryNox + "_" + poFileInfo.sFileCode + ".png";
                        String root = String.valueOf(instance.getExternalFilesDir(null));
                        File fileLoc = new File(root + "/" + "/" + ScannerConstants.SubFolder  +"/" + poFileInfo.sTransNox + "/");
                        if (!fileLoc.exists()) {
                            fileLoc.mkdirs();
                        }
//                        JSONObject loDownload = WebFileServer.DownloadFile(lsAccess,
//                                poFileInfo.sFileCode,
//                                "",
//                                imageName,
//                                "COAD",
//                                psSourceNo,
//                                "");

//                        String lsResponse = (String) loDownload.get("result");
//                        if (Objects.requireNonNull(lsResponse).equalsIgnoreCase("success")) {
//                            //convert to image and save to proper file location
//                            JSONParser loParser = new JSONParser();
//                            loDownload = (JSONObject) loParser.parse(loDownload.get("payload").toString());
//                            String location = fileLoc.getAbsolutePath() + "/";
//                            if (WebFile.Base64ToFile((String) loDownload.get("data"),
//                                    (String) loDownload.get("hash"),
//                                    location,
//                                    (String) loDownload.get("filename"))){
//                                //insert entry to image info
//                                EImageInfo loImage = new EImageInfo();
//                                loImage.setTransNox((String) loDownload.get("transnox"));
//                                loImage.setSourceCD("COAD");
//                                loImage.setSourceNo(poFileInfo.sTransNox);
//                                loImage.setDtlSrcNo(poFileInfo.sTransNox);
//                                loImage.setFileCode(poFileInfo.sFileCode);
//                                loImage.setMD5Hashx((String) loDownload.get("hash"));
//                                loImage.setFileLoct(fileLoc.getAbsolutePath() +"/" + imageName);
//                                loImage.setImageNme((String) loDownload.get("filename"));
//                                loImage.setLatitude("0.0");
//                                loImage.setLongitud("0.0");
//                                loImage.setSendDate(new AppConstants().DATE_MODIFIED);
//                                loImage.setSendStat("1");
//                                //loImage....
//                                ScannerConstants.PhotoPath = loImage.getFileLoct();
////                                poImage.insertImageInfo(loImage);
////                                saveImageInfo(loImage);
//                                //end - insert entry to image info
//                                saveDocumentInfoFromCamera(poFileInfo.sTransNox, poFileInfo.sFileCode);
//                                //todo:
//                                //insert/update entry to credit_online_application_documents
//                                //end - convert to image and save to proper file location
//                                loDownload = (org.json.simple.JSONObject) loParser.parse("{\"result\":\"success\",\"convert\":\"true\",\"message\":\""+ ScannerConstants.FileDesc + " has been downloaded successfully." + "\"}");
//                                lsResult = String.valueOf(loDownload);
//                            } else{
//                                Log.e(TAG, "Unable to convert file.");
//                                loDownload = (org.json.simple.JSONObject) loParser.parse("{\"result\":\"success\",\"convert\":\"false\",\"message\":\"Unable to convert file.\"}");
//                                lsResult = String.valueOf(loDownload);
//
//                            }
//                        }else{
//                            lsResult = String.valueOf(loDownload);
//                        }

                        Thread.sleep(1000);
                    }
                }
                lsResult = "";
            } catch (Exception e) {
                e.printStackTrace();
                lsResult = AppConstants.LOCAL_EXCEPTION_ERROR(e.getMessage());
            }
            return lsResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            callback.OnStartSaving();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                org.json.JSONObject loJson = new org.json.JSONObject(s);
                if (loJson.getString("result").equalsIgnoreCase("success")) {
                    if (loJson.getString("convert").equalsIgnoreCase(String.valueOf(true))){
                        callback.OnSuccessResult(new String[]{loJson.getString("message")});
                    }else{
                        callback.onFailedResult(loJson.getString("message"));
                    }
                } else {
                    org.json.JSONObject loError = loJson.getJSONObject("error");
                    callback.OnFailedResult(loError.getString("message"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //end - mac 2021.03.30

    public void CheckFile(String fsSourceNo, ViewModelCallBack callback) {
        try {
            new CheckFile(instance, fsSourceNo, callback).execute();
        } catch (Exception e) {
        }
    }

    public class CheckFile extends AsyncTask<Void, Void, String> {
        private final ConnectionUtil poConn;
        private final ViewModelCallBack callback;
        private final SessionManager poUser;
        private final RImageInfo poImage;
        private final String psSourceNo;
        public CheckFile(Application instance, String fsSourceNo, ViewModelCallBack callback) {
            this.poConn = new ConnectionUtil(instance);
            this.poUser = new SessionManager(instance);
            this.psSourceNo = fsSourceNo;
            this.callback = callback;
            this.poImage = new RImageInfo(instance);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... voids) {
            String lsResult;
            try {
                if (!poConn.isDeviceConnected()) {
                    lsResult = AppConstants.NO_INTERNET();
                } else {

                    String lsClient = WebFileServer.RequestClientToken(poConfig.ProducID(), poUser.getClientId(), poUser.getUserID());
                    String lsAccess = WebFileServer.RequestAccessToken(lsClient);

                    if (lsClient.isEmpty() || lsAccess.isEmpty()) {
                        lsResult = AppConstants.LOCAL_EXCEPTION_ERROR("Failed to request generated Client or Access token.");
                    } else {
//                        JSONObject loDownload = WebFileServer.CheckFile(lsAccess,
//                                "",
//                                "",
//                                "COAD",
//                                psSourceNo);
//
//                        String lsResponse = (String) loDownload.get("result");
//                        lsResult = String.valueOf(loDownload);
//
//                        if (Objects.requireNonNull(lsResponse).equalsIgnoreCase("success")) {
//                            //convert to image and save to proper file location
//                            org.json.simple.JSONArray laJson = (org.json.simple.JSONArray)loDownload.get("detail");
//                            for (int x = 0; x <  laJson.size(); x++){
//
//                                org.json.simple.JSONObject obj = (org.json.simple.JSONObject) laJson.get(x);;
//                                updateDocumentInfoFromServer(psSourceNo,obj.get("sFileCode").toString());
//
//                            }
//                        }

                        Thread.sleep(1000);
                    }
                }

                return "";
            } catch (Exception e) {
                e.printStackTrace();
                lsResult = AppConstants.LOCAL_EXCEPTION_ERROR(e.getMessage());
            }
            return lsResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            callback.OnStartSaving();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                org.json.JSONObject loJson = new org.json.JSONObject(s);
                if (loJson.getString("result").equalsIgnoreCase("success")) {
                    callback.OnSuccessResult(new String[]{"File has been downloaded successfully."});
                } else {
                    org.json.JSONObject loError = loJson.getJSONObject("error");
                    callback.OnFailedResult(loError.getString("message"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
