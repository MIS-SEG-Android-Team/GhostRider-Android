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

package org.rmj.g3appdriver.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import org.rmj.g3appdriver.etc.LocationInfo;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoLocator {

    private static final int REQUEST_LOCATION = 1;
    private final Context poContext;
    private final Activity poActivty;
    private final LocationInfo poLocation;

    public GeoLocator(Context context, Activity activity){
        this.poContext = context;
        this.poActivty = activity;
        this.poLocation = new LocationInfo();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void GetLocation(OnGetLocationListner listner){
        LocationManager poLcation = (LocationManager) poContext.getSystemService(Context.LOCATION_SERVICE);
        if(!poLcation.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            
        } else if(poLcation.isProviderEnabled(LocationManager.GPS_PROVIDER)){

            if (ActivityCompat.checkSelfPermission(poContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (poContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(poActivty, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

            } else {
                Location location = poLcation.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                Location location1 = poLcation.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                Location location2 = poLcation.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                if (location != null) {
                    double latti = location.getLatitude();
                    double longi = location.getLongitude();
                    poLocation.setLatitude(latti);
                    poLocation.setLongitude(longi);

                } else  if (location1 != null) {
                    double latti = location1.getLatitude();
                    double longi = location1.getLongitude();
                    poLocation.setLatitude(latti);
                    poLocation.setLongitude(longi);

                } else  if (location2 != null) {
                    double latti = location2.getLatitude();
                    double longi = location2.getLongitude();
                    poLocation.setLatitude(latti);
                    poLocation.setLongitude(longi);

                    initAddress();
                    listner.OnRetrieveLocation(poLocation);
                }else{
                    listner.OnFailedRetrieve("Unable to Trace your location");
                }
            }
        }
    }
    private void initAddress(){
        Geocoder loCoderxx = new Geocoder(poContext, Locale.getDefault());
        List<Address> paAddress;

        try {
            paAddress = loCoderxx.getFromLocation(poLocation.getLatitude(),
                                                    poLocation.getLongitude(), 1);

            poLocation.setAddress(paAddress.get(0).getAddressLine(0));
            poLocation.setTownCity(paAddress.get(0).getLocality());
            poLocation.setState(paAddress.get(0).getAdminArea());
            poLocation.setCountry(paAddress.get(0).getCountryName());
            poLocation.setPostal(paAddress.get(0).getPostalCode());
            poLocation.setName(paAddress.get(0).getFeatureName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public interface OnGetLocationListner{
        void OnRetrieveLocation(LocationInfo locationInfo);
        void OnFailedRetrieve(String message);
    }
}
