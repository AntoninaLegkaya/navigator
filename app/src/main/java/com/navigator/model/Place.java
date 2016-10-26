package com.navigator.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by tony on 25.10.16.
 */

public class Place {
    private LatLng latLng;
    private String address;
    private int type;


    public Place(String address, int type, LatLng latLng) {
        this.address = address;
        this.type = type;
        this.latLng = latLng;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }

}
