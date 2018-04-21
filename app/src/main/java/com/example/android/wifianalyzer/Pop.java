package com.example.android.wifianalyzer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by guanyuchen on 4/21/18.
 */

public class Pop extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height*0.6));

        String name = getIntent().getExtras().getString("name");
        String id = getIntent().getExtras().getString("id");
        Float mass = getIntent().getExtras().getFloat("mass");
        int frequency = getIntent().getExtras().getInt("frequency");

        Log.i("data",name);
        Log.i("data",id);
        Log.i("data",Float.toString(mass));
        Log.i("data",Integer.toString(frequency));
    }
}
