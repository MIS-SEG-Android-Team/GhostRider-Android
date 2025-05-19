package org.rmj.g3appdriver.lib.Location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class GmsLocationRetriever implements LocationRetriever.iLocationRetriever {
    private static final String TAG = GmsLocationRetriever.class.getSimpleName();

    @SuppressLint("MissingPermission")
    @Override
    public void GetLocation(Context instance, LocationRetriever.OnRetrieveLocationListener listener){
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(instance);
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                String nLatitude = String.valueOf(location.getLatitude());
                String nLongitde = String.valueOf(location.getLongitude());
                listener.OnRetrieve(nLatitude, nLongitde);
            } else {
                listener.OnFailed("Failed to retrieve location.", "0.0", "0.0");
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void StartLocationTracking(Context instance, LocationRetriever.BackgroundLocationTrackingListener listener){
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(instance);
        fusedLocationClient.requestLocationUpdates(GetGoogleLocationRequest(), new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                locationResult.getLastLocation();

                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                listener.OnRetrieve(latitude, longitude);
            }
        }, Looper.myLooper());
    }

    private LocationRequest GetGoogleLocationRequest(){
        return new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 300000).build();
    }
}
