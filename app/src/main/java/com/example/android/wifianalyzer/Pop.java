package com.example.android.wifianalyzer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        ArrayList<Integer> hist = getIntent().getExtras().getIntegerArrayList("hist");

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

        GraphView graph = (GraphView) findViewById(R.id.graph);
        DataPoint[] datas = new DataPoint[hist.size()];
        for(int i = 0; i < hist.size(); i++){
            datas[i] = new DataPoint(i,hist.get(i));
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(datas);
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                String txt = "x Label: "+(int)dataPoint.getX() +" Strength: " + (int)dataPoint.getY();
                Toast.makeText(getWindow().getContext(), txt, Toast.LENGTH_SHORT).show();

            }
        });

        Log.i("data",Integer.toString(hist.size()));
        /*for(int i = 0; i < hist.size(); i++){
            series.appendData(new DataPoint(i,hist.get(i)),false,20);
        }*/
        graph.addSeries(series);
        graph.setScaleX(0.9f);
        graph.setScaleY(0.9f);
        graph.setTitle("Strength vs. Time");





    }
}
