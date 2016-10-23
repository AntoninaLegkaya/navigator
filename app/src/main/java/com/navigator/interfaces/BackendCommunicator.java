package com.navigator.interfaces;


import com.navigator.model.LocationModel;
import com.navigator.service.LocationService;

/**
 * Created by tony on 21.05.16.
 */
public interface BackendCommunicator {
    String getProvider(LocationService locationService) throws InterruptedException;
    LocationModel getLocationModel(LocationService locationService, String provider) throws InterruptedException;



}
