package com.navigator.service;

import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;

import com.navigator.MainActivity;
import com.navigator.MainFragment;
import com.navigator.R;


/**
 * Created by tony on 27.10.16.
 */

public class InitializeTask extends AsyncTask<Void, Void, MainFragment> {
    private MainActivity context;


    public InitializeTask(MainActivity context) {
        this.context = context;

    }

    @Override
    protected MainFragment doInBackground(Void... params) {

        final MainFragment fragment = MainFragment.newInstance(null);
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
