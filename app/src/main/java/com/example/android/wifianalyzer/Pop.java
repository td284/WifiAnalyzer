package com.example.android.wifianalyzer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

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

        getWindow().setLayout(width,(int)(height*0.6));
        getWindow().setGravity(Gravity.BOTTOM);

        String name = getIntent().getExtras().getString("name");
        String id = getIntent().getExtras().getString("id");
        Float mass = getIntent().getExtras().getFloat("mass");
        int frequency = getIntent().getExtras().getInt("frequency");
        String venue = getIntent().getExtras().getString("venue");
        int level = getIntent().getExtras().getInt("strength");

        String freq = frequency>3000?"5GHz":"2.4Ghz";



        Log.i("data",name);
        Log.i("data",id);
        Log.i("data",Float.toString(mass));
        Log.i("data",Integer.toString(frequency));
        Log.i("data",venue);
        Log.i("data",Integer.toString(level));

        TextView tv = (TextView)findViewById(R.id.textView);
        tv.setTextSize(16);
        tv.setText(name+"\t\t"+venue+"\n"+"Strength: "+level+" dBm"+"\t BSSID: " +id+"\nFrequency: "+freq);


    }
}
