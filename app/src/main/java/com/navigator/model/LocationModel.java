package com.navigator.model;

import android.content.Context;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import com.navigator.communicators.CommunicatorFactory;
import com.navigator.interfaces.BackendCommunicator;
import com.navigator.interfaces.Observer;
import com.navigator.notification.ActionObservable;
import com.navigator.service.LocationService;


/**
 * Created by tony on 20.05.16.
 */
public class LocationModel {
    private String TAG = LocationModel.class.getSimpleName();
    ;
    public double longitude;
    public double latitude;
    private final ActionObservable mObservable = new ActionObservable();
    private boolean mIsWorking;
    private LocationTask locationTask;
    private LocationManager locationManager;
    private LocationService locationService;
    private BackendCommunicator communicator;
    private static LocationModel mLocationModel;

    public LocationModel() {

    }

    public ActionObservable getmObservable() {
        return mObservable;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public LocationModel(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;


    }

    public static LocationModel getInstanceLocationModel() {

        if (mLocationModel == null) {

            mLocationModel = new LocationModel();

        }
        return mLocationModel;

    }

    public void startGetLocation(Context context, LocationManager locationManager) {
        if (mIsWorking) {
            return;
        }
        this.locationManager = locationManager;
        communicator = CommunicatorFactory.createBackendCommunicator();
        mObservable.notifyStarted();
        mIsWorking = true;
        locationTask = new LocationTask(context);
        locationTask.execute();

    }

    public void stopGetLocation() {
        if (mIsWorking) {
            locationTask.cancel(true);
            mIsWorking = false;

        }
    }

    public void registerObserver(final Observer observer) {

        mObservable.registerObserver(observer);
        if (mIsWorking) {
            observer.onStarted(this);
        }
    }

    public void unregisterObserver(final Observer observer) {
        mObservable.unregisterObserver(observer);
    }


    private class LocationTask extends AsyncTask<Void, Void, String> {

        private Context context;


        public LocationTask(Context context) {
            this.context = context;


        }

        @Override
        protected String doInBackground(final Void... params) {


            try {
                locationService = new LocationService(context, locationManager);

                final String provider = communicator.getProvider(locationService);
                Log.i(TAG, " -------------------Method: doInBackground()----------------------" + "\n" + "Get provider: " + provider);
                return provider;
            } catch (InterruptedException e) {
                Log.e(TAG, "In interrupted" + "\n" + " -----------------------------------------");
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            mIsWorking = false;
            Log.i(TAG, " --------------------Method: onPostExecute()---------------------" + "\n" + "Provider: " + s);
            if (s != null) {

                try {
                    final LocationModel updateModel = communicator.getLocationModel(locationService, s);
                    if (updateModel != null) {
                        longitude = updateModel.getLongitude();
                        setLongitude(updateModel.getLongitude());
                        setLatitude(updateModel.getLatitude());
                        Log.i(TAG, "Location gets: " + "\n" + " -----------------------------------------");
                        mObservable.notifySucceeded();
                    } else {
                        mObservable.notifyFailed();
                        Log.i(TAG, "Model location is null" + "\n" + " -----------------------------------------");

                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "Error:" + e.getMessage() + "\n" + " -----------------------------------------");
                    mObservable.notifyFailed();
                    e.printStackTrace();
                }

            } else {
                Log.e(TAG, "Could not get location: Provider is null " + "\n" + " -----------------------------------------");
                mObservable.notifyFailedProvider();
            }
        }


    }
}

