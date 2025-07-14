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

package org.rmj.g3appdriver.dev.Api;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import org.rmj.g3appdriver.GConnect.Account.ClientSession;
import org.rmj.g3appdriver.dev.Device.Telephony;
import org.rmj.g3appdriver.GCircle.Account.EmployeeSession;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.utils.SQLUtil;
import org.rmj.g3appdriver.utils.SecUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    private static final String TAG = HttpHeaders.class.getSimpleName();

    private final Context instance;
    private final Telephony poTlphony;
    private final AppConfigPreference poConfigx;

    private HttpHeaders(Context application){
        this.instance = application;
        this.poTlphony = new Telephony(application);
        this.poConfigx = AppConfigPreference.getInstance(application);
    }

    private static HttpHeaders mHeaders;

    public static HttpHeaders getInstance(Application application){
        if(mHeaders == null){
            mHeaders = new HttpHeaders(application);
        }
        return mHeaders;
    }

    public static HttpHeaders getInstance(Context application){
        if(mHeaders == null){
            mHeaders = new HttpHeaders(application);
        }
        return mHeaders;
    }

    public HashMap<String, String> getHeaders(){
        if("gRider".equalsIgnoreCase(poConfigx.ProducID())) {
            return (HashMap<String, String>) InitGCircle();
        }
        return (HashMap<String, String>) InitGConnect();
    }

    private Map<String, String> InitGCircle(){
        EmployeeSession loSession = EmployeeSession.getInstance(instance);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Calendar calendar = Calendar.getInstance();

        String lsUserIDx = loSession.getUserID();
        String lsClientx = loSession.getClientId();
        String lsLogNoxx = loSession.getLogNumber();
        String lsTokenxx = poConfigx.getAppToken();
        String lsProduct = poConfigx.ProducID();
        String lsDevcIDx = poTlphony.getDeviceID();
        String lsDateTme = SQLUtil.dateFormat(calendar.getTime(), "yyyyMMddHHmmss");
        String lsDevcMdl = Build.MODEL;

        //api mobile requirement removed
        String lsMobileN = "";

        if(lsTokenxx.isEmpty()){
            lsTokenxx = "f7qNSw8TRPWHSCga0g8YFF:APA91bG3i_lBPPWv9bbRasNzRH1XX1y0vzp6Ct8S_a-yMPDvSmud8FEVPMr26zZtBPHq2CmaIw9Rx0MZmf3sbuK44q3vQemUBoPPS4Meybw8pnTpcs3p0VbiTuoLHJtdncC6BgirJxt3";
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("g-api-id", lsProduct);
        headers.put("g-api-client", lsClientx);
        headers.put("g-api-imei", lsDevcIDx);
        headers.put("g-api-model", lsDevcMdl);
        headers.put("g-api-mobile", lsMobileN);
        headers.put("g-api-token", lsTokenxx);
        headers.put("g-api-user", lsUserIDx);
        headers.put("g-api-key", lsDateTme);
        String hash_toLower = SecUtil.md5Hex(headers.get("g-api-imei") + headers.get("g-api-key"));
        hash_toLower = hash_toLower.toLowerCase();
        headers.put("g-api-hash", hash_toLower);
        headers.put("g-api-log", lsLogNoxx);

        Log.d(TAG, headers.toString());

        return headers;
    }

    private Map<String, String> InitGConnect(){
        ClientSession loSession = ClientSession.getInstance(instance);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Calendar calendar = Calendar.getInstance();

        String lsUserIDx = loSession.getUserID();
        String lsClientx = loSession.getClientID();
        String lsLogNoxx = loSession.getLogNumber();
        String lsTokenxx = poConfigx.getAppToken();
        String lsProduct = poConfigx.ProducID();
        String lsDevcIDx = poTlphony.getDeviceID();
        String lsDateTme = SQLUtil.dateFormat(calendar.getTime(), "yyyyMMddHHmmss");
        String lsDevcMdl = Build.MODEL;

        //api mobile requirement removed
        String lsMobileN = "";

        if(lsTokenxx.isEmpty()){
            lsTokenxx = "f7qNSw8TRPWHSCga0g8YFF:APA91bG3i_lBPPWv9bbRasNzRH1XX1y0vzp6Ct8S_a-yMPDvSmud8FEVPMr26zZtBPHq2CmaIw9Rx0MZmf3sbuK44q3vQemUBoPPS4Meybw8pnTpcs3p0VbiTuoLHJtdncC6BgirJxt3";
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("g-api-id", lsProduct);
        headers.put("g-api-client", lsClientx);
        headers.put("g-api-imei", lsDevcIDx);
        headers.put("g-api-model", lsDevcMdl);
        headers.put("g-api-mobile", lsMobileN);
        headers.put("g-api-token", lsTokenxx);
        headers.put("g-api-user", lsUserIDx);
        headers.put("g-api-key", lsDateTme);
        String hash_toLower = SecUtil.md5Hex(headers.get("g-api-imei") + headers.get("g-api-key"));
        hash_toLower = hash_toLower.toLowerCase();
        headers.put("g-api-hash", hash_toLower);
        headers.put("g-api-log", lsLogNoxx);
        return headers;
    }
}
