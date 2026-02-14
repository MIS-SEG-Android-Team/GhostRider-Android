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

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DToken;
import org.rmj.g3appdriver.GCircle.room.Entities.ETokenInfo;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.dev.Api.WebFileServer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppTokenManager {
    private static final String TAG = AppTokenManager.class.getSimpleName();

    private final DToken poDao;

    private final AppConfigPreference poConfig;
    private String message;

    public AppTokenManager(Application instance){
        this.poDao = GGC_GCircleDB.getInstance(instance).dToken();
        this.poConfig = AppConfigPreference.getInstance(instance);
    }

    public String getMessage() {
        return message;
    }

    public boolean SaveFirebaseToken(String fsVal){
        try{
            ETokenInfo loDetail = poDao.GetFirebaseToken(fsVal);

            if(loDetail == null) {
                String lsTokenID = CreateUniqueID();
                ETokenInfo loToken = new ETokenInfo();
                loToken.setTokenIDx(lsTokenID);
                loToken.setTokenInf(fsVal);
                loToken.setTokenTpe("0");
                loToken.setDescript("Firebase token.");
                loToken.setGeneratd(AppConstants.DATE_MODIFIED());
                loToken.setTimeStmp(AppConstants.DATE_MODIFIED());
                poDao.SaveToken(loToken);
                Log.d(TAG, "Firebase token save!");
            } else {
                loDetail.setTokenInf(fsVal);
                loDetail.setTokenTpe("0");
                loDetail.setDescript("Firebase token.");
                loDetail.setGeneratd(AppConstants.DATE_MODIFIED());
                loDetail.setTimeStmp(AppConstants.DATE_MODIFIED());
                poDao.UpdateToken(loDetail);
                Log.d(TAG, "Firebase token updated!");
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
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

            int lnLocalID = poDao.GetTokenRowsForID() + 1;
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

    public String GetClientToken(){
        try{

            ETokenInfo loClientx = poDao.GetClientToken();

            if(loClientx == null){
                Log.d(TAG, "Client token is not available.");
                Log.d(TAG, "Generating client token...");

                String lsClient = WebFileServer.RequestClientToken(poConfig.ProducID(),
                        poDao.GetClientID(),
                        poDao.GetUserID());

                if(lsClient == null){
                    message = "Unable to generate client token.";
                    return null;
                }

                if(lsClient.isEmpty()){
                    message = "Unable to generate client token.";
                    return null;
                }

                String lsExpiryx = TokenExpiry();

                ETokenInfo loToken = new ETokenInfo();
                String lsTokenIDx = CreateUniqueID();
                loToken.setTokenIDx(lsTokenIDx);
                loToken.setTokenInf(lsClient);
                loToken.setDescript("Image upload client token");
                loToken.setTokenTpe("1");
                loToken.setGeneratd(AppConstants.DATE_MODIFIED());
                loToken.setExpirexx(lsExpiryx);
                loToken.setTimeStmp(AppConstants.DATE_MODIFIED());

                poDao.SaveToken(loToken);
                Log.d(TAG, "New client token has been saved.");
                return loToken.getTokenInf();
            }
            return loClientx.getTokenInf();

        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public String GetAccessToken(String fsVal){
        try{
            ETokenInfo loAccessx = poDao.GetAccessToken();

            if(loAccessx == null){
                Log.d(TAG, "Access token is not available.");
                Log.d(TAG, "Generating access token...");

                String lsAccess = WebFileServer.RequestAccessToken(fsVal);

                if(lsAccess == null){
                    message = "Unable to generate access token.";
                    return null;
                }
                if(lsAccess.isEmpty()){
                    message = "Unable to generate access token.";
                    return null;
                }

                String lsExpiryx = TokenExpiry();

                ETokenInfo loToken = new ETokenInfo();
                String lsTokenIDx = CreateUniqueID();
                loToken.setTokenIDx(lsTokenIDx);
                loToken.setTokenInf(lsAccess);
                loToken.setDescript("Image upload access token");
                loToken.setTokenTpe("2");
                loToken.setGeneratd(AppConstants.DATE_MODIFIED());
                loToken.setExpirexx(lsExpiryx);
                loToken.setTimeStmp(AppConstants.DATE_MODIFIED());

                poDao.SaveToken(loToken);
                Log.d(TAG, "New access token has been saved.");
                return loToken.getTokenInf();
            }

            //Check here if client token is still valid...
            String lsExpiryx = loAccessx.getExpirexx();
            Date loExpiryx = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lsExpiryx);
            Date loCurrent = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(AppConstants.DATE_MODIFIED());

            int lnResult = loCurrent.compareTo(loExpiryx);

            // If result is less than 0 current date is before the due date
            // If result is equal to 0 current date is equal to due date
            // if result is more than 0 current date is after the due date
            if (lnResult >= 0) {

                // Access token might be expired already so we generate another access token...
                return refreshAccessToken(fsVal);
            }

            return loAccessx.getTokenInf();
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    public String GetClientToken(String fsClientID, String fsUserID){
        try{

            Log.d(TAG, "Client token is not available.");
            Log.d(TAG, "Generating client token...");

            String lsClient = WebFileServer.RequestClientToken(
                    poConfig.ProducID(),
                    fsClientID,
                    fsUserID);

            if(lsClient == null){
                message = "Unable to generate client token.";
                return null;
            }

            if(lsClient.isEmpty()){
                message = "Unable to generate client token.";
                return null;
            }
            return lsClient;

        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    public String refreshClientToken(){
        try{
            ETokenInfo loClientx = poDao.GetClientToken();

            String lsClient = WebFileServer.RequestClientToken(poConfig.ProducID(),
                    poDao.GetClientID(),
                    poDao.GetUserID());

            if(lsClient == null){
                message = "Unable to generate client token.";
                return null;
            }

            if(lsClient.isEmpty()){
                message = "Unable to generate client token.";
                return null;
            }

            String lsExpiryx = TokenExpiry();

            loClientx.setTokenInf(lsClient);
            loClientx.setGeneratd(AppConstants.DATE_MODIFIED());
            loClientx.setExpirexx(lsExpiryx);
            loClientx.setModified(AppConstants.DATE_MODIFIED());
            loClientx.setTimeStmp(AppConstants.DATE_MODIFIED());
            poDao.UpdateToken(loClientx);
            Log.d(TAG, "Client token has been updated.");

            return lsClient;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    public String refreshAccessToken(String fsVal){
        try{
            ETokenInfo loAccessx = poDao.GetAccessToken();

            String lsAccess = WebFileServer.RequestAccessToken(fsVal);

            if(lsAccess == null){
                message = "Unable to generate access token.";
                return null;
            }

            if(lsAccess.isEmpty()){
                message = "Unable to generate access token.";
                return null;
            }

            String lsExpiryx = TokenExpiry();

            loAccessx.setTokenInf(lsAccess);
            loAccessx.setGeneratd(AppConstants.DATE_MODIFIED());
            loAccessx.setExpirexx(lsExpiryx);
            loAccessx.setModified(AppConstants.DATE_MODIFIED());
            loAccessx.setTimeStmp(AppConstants.DATE_MODIFIED());
            poDao.UpdateToken(loAccessx);
            Log.d(TAG, "Client token has been updated.");

            return lsAccess;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    private String TokenExpiry(){
        Calendar currentTimeNow = Calendar.getInstance();
        Log.d(TAG, "Current time now : " + currentTimeNow.getTime());
        currentTimeNow.add(Calendar.MINUTE, 25);
        Date tenMinsFromNow = currentTimeNow.getTime();
        Log.d(TAG, "After adding 25 mins with Caleder add() method : " + tenMinsFromNow);
        String lsExpiryx = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(tenMinsFromNow);
        return lsExpiryx;
    }
}
