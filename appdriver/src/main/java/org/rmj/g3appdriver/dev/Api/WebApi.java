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
import android.util.Log;

import org.rmj.g3appdriver.etc.AppConfigPreference;

public abstract class WebApi {
    private static final String TAG = WebApi.class.getSimpleName();

    private final AppConfigPreference poConfig;

    private static final String PRIMARY_LIVE = "https://apps.guanzongroup.com.ph/";
    private static final String SECONDARY_LIVE = "https://restgk.guanzongroup.com.ph/";

    protected static final String GCARD = "gcard/ms/";
    protected static final String GCARDs = "gcard/mx/";
    protected static final String SECURITY = "security/";

    private final boolean isUnitTest;
    protected String LIVE;
    protected String LOCAL;

    public WebApi(Application instance) {

        this.poConfig = AppConfigPreference.getInstance(instance);
        this.isUnitTest = poConfig.getTestStatus();

        LIVE = poConfig.getAppServer();
        LOCAL = poConfig.getAppServer();

        Log.d(TAG, "Device connected to config server.");
        Log.d(TAG, "LIVE: " + poConfig.getAppServer());
        Log.d(TAG, "LOCAL: " + poConfig.getAppServer());

        /* TODO THIS IS REPLACED WITH CONFIGURED IP, FROM DEVELOPER SETTINGS -guillier 05/08/2024
        boolean isLiveData = poConfig.isBackUpServer();
        if(isLiveData){
            Log.d(TAG, "Device connected to backup server.");
            LIVE = SECONDARY_LIVE;
        } else {
            Log.d(TAG, "Device connected to primary server.");
            LIVE = PRIMARY_LIVE;
        }*/
    }

    protected boolean isUnitTest(){
        return isUnitTest;
    }
}
