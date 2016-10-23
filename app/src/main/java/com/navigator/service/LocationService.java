package com.navigator.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.navigator.model.LocationModel;


/**
 * Created by tony on 30.03.16.
 */
public class LocationService {

    //The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    //The minimum time beetwen updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1;
//            1000 * 60 * 1; // 1 minute

    private final static boolean forceNetwork = false;
    public static int MY_PERMISSION_LOCATION = 1;

    private String TAG = LocationService.class.getSimpleName();
    ;
    private LocationManager locationManager;
    public Location location;
    ;
    public double longitude;
    public double latitude;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    private Context context;

    public LocationModel locationObject;
    private Handler handler;


    public LocationService(Context context, LocationManager locationManager) {
        this.context = context;
        this.locationManager = locationManager;

    }


    @TargetApi(Build.VERSION_CODES.M)
    public String marshmallowGPSPremissionCheck() {
        String provider = null;
        Log.i(TAG, "----------------Method:marshmallowGPSPremissionCheck()-------------------------" + "\n " + "Check all permission ");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Do Request Permissions ");
            try {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_LOCATION);
                Log.i(TAG, "\n" + " -----------------------------------------");
            } catch (Exception e) {
                Log.e(TAG, "Error while do Request Permission: " + e.getMessage() + "\n" + " -----------------------------------------");
                return null;
            }

        }


        try {

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
//                Log.i(TAG, "Getting GPS status: " + isGPSEnabled);
            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//                Log.i(TAG, " Getting Network status: " + isNetworkEnabled);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                return null;
            } else {

                if (isNetworkEnabled) {
//                        Log.i(TAG, "Network Enabled");
                    provider = LocationManager.NETWORK_PROVIDER;


                }
                // if GPS =Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
//                        Log.i(TAG, "GPS Enabled");

                    provider = LocationManager.GPS_PROVIDER;

                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }

        return provider;

    }

    public LocationModel callUIThreadForLocation(final String provider) {
        Log.i(TAG, "------------------Method: callUIThreadForLocation-----------------------" + "\n" + "Provider: " + provider);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return null;
        }
        if (provider != null) {
            locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.i(TAG, "latitude: " + latitude + "longitude: " + longitude);
                    final LocationModel locationModel = new LocationModel(longitude, latitude);
                    return locationModel;
                } else {
                    Log.i(TAG, "location: Null " + "\n" + " -----------------------------------------");

                    return null;
                }
            } else {
                Log.i(TAG, "locationManager: Null " + "\n" + " -----------------------------------------");

                return null;
            }
        }
        Log.i(TAG, "Provider: Null " + "\n" + " -----------------------------------------");
        return null;
    }

    LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            Log.i(TAG, "onLocationChanged " + "\n" + " -----------------------------------------" + "\n" + "latitude: " + latitude + "longitude: " + longitude);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}