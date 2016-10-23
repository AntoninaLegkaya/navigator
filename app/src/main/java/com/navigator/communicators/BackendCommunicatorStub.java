package com.navigator.communicators;


import com.navigator.interfaces.BackendCommunicator;
import com.navigator.model.LocationModel;
import com.navigator.service.LocationService;

/**
 * Created by tony on 21.05.16.
 */
public class BackendCommunicatorStub implements BackendCommunicator {


    @Override
    public String getProvider(LocationService locationService) throws InterruptedException {


        return locationService.marshmallowGPSPremissionCheck();
    }

    @Override
    public LocationModel getLocationModel(LocationService locationService, String provider) throws InterruptedException {
        return locationService.callUIThreadForLocation(provider);
    }


}
