/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.g3appdriver
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

/**
 * Access File Server API's
 *
 * mac 2021.02.23
 *      started creating this object
 */

package org.rmj.g3appdriver.dev.Api;

import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.rmj.apprdiver.util.WebClient;
import org.rmj.apprdiver.util.WebFile;

public class WebFileServer {
    public static final String TAG = "WebFileServer";

    /**
     * RequestClientToken
     * @param fsProdctID Product ID eg. IntegSys
     * @param fsClientID Client ID eg. GGC_BM001
     * @param fsUserIDxx App User ID
     * @return Client Token
     */
    public static String RequestClientToken(String fsProdctID, String fsClientID, String fsUserIDxx){
        JSONObject loJSON = WebClient.RequestClientToken(fsProdctID, fsClientID, fsUserIDxx);
        JSONParser loParser = new JSONParser();
        String lsToken = "";

        try{
            if (loJSON.get("result").equals("success")){
                loJSON = (JSONObject) loParser.parse(loJSON.get("payload").toString());
                lsToken = (String) loJSON.get("token");

                if (lsToken.isEmpty()) Log.e(TAG, "Client Token is empty.");
            } else {
                loJSON = (JSONObject) loParser.parse(loJSON.get("error").toString());
                Log.e(TAG, loJSON.get("message") + " " +loJSON.get("code"));
            }
        }catch (ParseException e){
            Log.e(TAG, e.getMessage());
        }

        return lsToken;
    }

    /**
     * RequestAccessToken
     * @param fsCltToken Client Token
     * @return Access Token
     */
    public static String RequestAccessToken(String fsCltToken){
        JSONObject loJSON = WebClient.RequestAccessToken(fsCltToken);
        JSONParser loParser = new JSONParser();
        String lsToken = "";

        try{
            if ("success".equals((String) loJSON.get("result"))){
                loJSON = (JSONObject) loJSON.get("payload");
                lsToken = (String) loJSON.get("token");

                if (lsToken.isEmpty()) Log.e(TAG, "Client Token is empty.");
            } else {
                loJSON = (JSONObject) loParser.parse(loJSON.get("error").toString());
                Log.e(TAG, loJSON.get("message") + " " +loJSON.get("code"));
            }
        }catch (ParseException e){
            Log.e(TAG, e.getMessage());
        }

        return lsToken;
    }

    /**
     * UploadFile
     * @param fsFileInpt Path + File Name
     * @param fsAcsToken Access Token
     * @param fsFileType File Type
     * @param fsFileOwnr Universal Identifier for this file eg. sSerialID
     * @param fsFileName File Name without path
     * @param fsScannerx Branch/Dept/Employee who owns this file
     * @param fsSourceCd Source Code
     * @param fsSourceNo Source No/Reference No
     * @param fsUniqueVl Additional Information that will make this file unique
     * @return JSONObject
     */
    public static JSONObject UploadFile(String fsFileInpt, String fsAcsToken, String fsFileType,
                                        String fsFileOwnr, String fsFileName, String fsScannerx,
                                        String fsSourceCd, String fsSourceNo, String fsUniqueVl){
        //get the md5 hash value of the file
        String lsMD5xx = WebFile.md5Hash(fsFileInpt);
        //get the 64bit encoding value of the file
        String lsImg64 = WebFile.FileToBase64(fsFileInpt);

        return WebFile.UploadFile(fsAcsToken,
                                    fsFileType,
                                    fsFileOwnr,
                                    fsFileName,
                                    fsScannerx,
                                    lsMD5xx,
                                    lsImg64,
                                    fsSourceCd,
                                    fsSourceNo,
                                    fsUniqueVl);
    }

    /**
     * CheckFile
     * @param fsAcsToken Access Token
     * @param fsSourceCd Source Code
     * @param fsSourceNo Source No/Reference No
     * @return JSONObject
     */
    public static JSONObject CheckFile(String fsAcsToken, String fsFileType, String fsBranchCd, String fsSourceCd, String fsSourceNo){
        return WebFile.CheckFile(fsAcsToken,
                                    fsFileType,
                                    fsBranchCd,
                                    fsSourceCd,
                                    fsSourceNo);
    }

    public static String createMD5Hash(String FilePath) {
            String lsResult = WebFile.md5Hash(FilePath);
            return lsResult;
    }
}
