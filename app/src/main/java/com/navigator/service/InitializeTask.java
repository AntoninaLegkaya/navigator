package com.navigator.service;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.navigator.MainActivity;
import com.navigator.MainFragment;
import com.navigator.R;
import com.navigator.TheApp;
import com.navigator.layout.MapWrapperLayout;

/**
 * Created by tony on 27.10.16.
 */

public class InitializeTask extends AsyncTask<Void, Void, MainFragment> {
    private MainActivity context;
    private  MapWrapperLayout mapWrapperLayout;

    public InitializeTask(MainActivity context, MapWrapperLayout mapWrapperLayout) {
        this.context = context;
//        this.mapWrapperLayout=mapWrapperLayout;
    }

    @Override
    protected MainFragment doInBackground(Void... params) {

        final MainFragment fragment = MainFragment.newInstance(null,mapWrapperLayout);
        return fragment;
    }

    @Override
    protected void onPostExecute(MainFragment mainFragment) {
        super.onPostExecute(mainFragment);
        FragmentTransaction trans = context.getSupportFragmentManager().beginTransaction();
        trans.add(R.id.fragment, mainFragment);
        trans.commit();
    }
}
