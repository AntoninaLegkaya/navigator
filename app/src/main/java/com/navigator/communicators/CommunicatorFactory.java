package com.navigator.communicators;


import com.navigator.interfaces.BackendCommunicator;

/**
 * Created by tony on 21.05.16.
 */
public class CommunicatorFactory {
    public static BackendCommunicator createBackendCommunicator() {
        return new BackendCommunicatorStub();
    }
}
