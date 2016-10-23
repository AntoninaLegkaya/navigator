package com.navigator.interfaces;

/**
 * Created by tony on 21.05.16.
 */
public interface Observer {
    void onStarted(Object model);

    void onSucceeded(Object model);

    void onFailed(Object model);

    void onFailedProvider(Object model);

}
