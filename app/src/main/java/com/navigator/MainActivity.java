package com.navigator;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
//    @Bind(R.id.previousActivity)
//    Button mPrevButton;
//
//    @OnClick(R.id.previousActivity)
//    public void submit(View view) {
//        Log.i(TAG, "----------------------Button Previous Press-----------------------------");
//        Intent previousActivity = new Intent(this,MapsActivity.class);
//        startActivity(previousActivity);
//        //push from top to bottom
//        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
//        //slide from left to right
//        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.add(R.id.fragment, MainFragment.newInstance(null));
            trans.commit();
        }

    }



}
