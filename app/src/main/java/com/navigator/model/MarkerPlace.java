package com.navigator.model;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.navigator.TheApp;
import com.navigator.customElements.MarkerPlaceInfoFrame;

/**
 * Created by tony on 24.10.16.
 */

public class MarkerPlace {

    private static final String TAG = MarkerPlace.class.getSimpleName();
    private Marker point;
    private LatLng latLng;
    private GoogleMap mMap;
    private String address;
    private Context context;
    private float icon;
    private boolean flag = true;
    private MarkerPlaceInfoFrame infoFrame;

    public MarkerPlace(Context context,float icon, LatLng latLng, GoogleMap map, String address) {

        this.context=context;
        this.icon = icon;
        this.latLng = latLng;
        this.mMap = map;
        this.address = address;
    }


    public Marker initMarkerPoint() {
        updateMarkerOptions(latLng, address);
        Log.i(TAG, "initMarkerPoint: compose infoWindow");
        final GoogleMap.InfoWindowAdapter infoWindowAdapter = new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                if (marker.getId().equals(point.getId())) {


                    if (address != null) {

                        infoFrame.setInforPlace(address);
                    } else {
                        infoFrame.setInforPlace("Could not get address");

                    }
                    infoFrame.setColor(Color.BLACK);
                    return infoFrame;

                } else
                    return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                TextView tv = new TextView(TheApp.getAppContext());

                if (address != null) {
                    tv.setText(address);
                } else {
                    tv.setText("Coul not get address");
                }
                return tv;
            }
        };
        final GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                MarkerPlaceInfoFrame infoFrame = (MarkerPlaceInfoFrame) infoWindowAdapter.getInfoWindow(marker);
                if (marker.getId().equals(point.getId())) {
                    Log.i(TAG, "Point id: " + point.getId() + " Clicked InfoWindow");



                }

            }
        };
        Log.i(TAG, "initMarkerPoint: Set Listener on infoWindow");
        mMap.setInfoWindowAdapter(infoWindowAdapter);
        mMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
        Log.i(TAG, "initMarkerPoint: Show infoWindow");
        point.showInfoWindow();
        Log.i(TAG, "initMarkerPoint: return point Marker");
        return point;
    }

    public void updateMarkerOptions(LatLng latLng, String addr) {
        Log.i(TAG, "updateMarkerOptions: add Marker Options");
        final MarkerOptions markerApointOptions = new MarkerOptions();
        markerApointOptions.position(latLng);
        markerApointOptions.icon(BitmapDescriptorFactory.defaultMarker(icon));
        this.point = mMap.addMarker(markerApointOptions);
        Log.i(TAG, "updateMarkerOptions: add Marker id: " + point.getId() + " on Map");
        setAddress(addr);
        infoFrame = new MarkerPlaceInfoFrame(context);
        if (address != null) {

            infoFrame.setInforPlace(address);
        } else {
            infoFrame.setInforPlace("Could not get address");

        }
        infoFrame.setColor(Color.BLACK);
        point.showInfoWindow();
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Marker getPoint() {
        return point;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public GoogleMap getmMap() {
        return mMap;
    }


    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
