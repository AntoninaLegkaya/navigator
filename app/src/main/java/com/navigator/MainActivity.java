package com.navigator;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.BinderThread;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.navigator.interfaces.ActionMapMarker;
import com.navigator.model.LocationModel;
import com.navigator.service.InitializeTask;
import com.navigator.service.LocationService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private LocationModel locationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ButterKnife.bind(this);
        Toast.makeText(TheApp.getAppContext(), "Wait, Map is initializing....", Toast.LENGTH_SHORT).show();
        if (savedInstanceState == null) {

            InitializeTask initializeTask = new InitializeTask(this);
            initializeTask.execute();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, " ------------------Method: onRequestPermissionsResult()-----------------------" + "\n");
        if (requestCode == LocationService.MY_PERMISSION_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission  granted " + "\n" + " -----------------------------------------");
            LocationManager lm = (LocationManager) TheApp.getAppContext().getSystemService(Context.LOCATION_SERVICE);
            LocationModel.getInstanceLocationModel().startGetLocation(TheApp.getAppContext(), lm);

        } else {
            Log.i(TAG, "Could not get permissions " + "\n" + "-----------------------------------------");
        }
    }
}
