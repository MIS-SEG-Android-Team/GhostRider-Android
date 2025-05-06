package org.rmj.g3appdriver.lib.Location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.rmj.g3appdriver.GCircle.room.Entities.EGLocatorSysLog;
import org.rmj.g3appdriver.GCircle.room.Repositories.DeviceLocationRecords;
import org.rmj.g3appdriver.dev.Device.Telephony;

public class LocationRetriever {
    private static final String TAG = LocationRetriever.class.getSimpleName();
    private final Application instance;


    public LocationRetriever(Application application) {
        this.instance = application;
    }

    public interface iLocationRetriever{
        void GetLocation(Context instance, OnRetrieveLocationListener listener);
        void StartLocationTracking(Context instance, BackgroundLocationTrackingListener listener);
    }

    public interface OnRetrieveLocationListener{
        void OnRetrieve(String latitude, String longitude);
        void OnFailed(String message, String latitude, String longitude);
    }

    public interface BackgroundLocationTrackingListener{
        void OnRetrieve(double args, double args1);
    }

    public void GetLocationOnBackgroud(){

        try{

            String lsCompx = android.os.Build.MANUFACTURER;

            iLocationRetriever location;

            if (!lsCompx.equalsIgnoreCase("huawei")) {
                Log.d(TAG, "Initialize google services for location tracking.");
                location = new GmsLocationRetriever();
            } else {
                Log.d(TAG, "Initialize huawei services for location tracking.");
                location = new HmsLocationRetriever();
            }

            DeviceLocationRecords loSys = new DeviceLocationRecords(instance);

            location.StartLocationTracking(instance, (args, args1) -> {
                String lsService;
                String lsRemarks;
                if(isLocationEnabled(instance)) {
                    lsService = "1";
                    lsRemarks = "Location Retrieve.";
                } else {
                    lsService = "0";
                    lsRemarks = "Location service is not enabled.";
                }

                loSys.saveCurrentLocation(args, args1, lsService, lsRemarks);

                Log.d(TAG, loSys.getMessage());
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static boolean isLocationEnabled(Context context) {
        int locationMode;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }
}
