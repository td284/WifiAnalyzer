package com.example.android.wifianalyzer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;

public class SignalSummary extends Fragment {

    private TextView name;
    private TextView id;
    private TextView frequency;
    private TextView strength;
    private GraphView graph;
    private View color;
    private LineGraphSeries<DataPoint> series;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signal_summary, container, false);
        name = view.findViewById(R.id.summary_name);
        id = view.findViewById(R.id.summary_id);
        frequency = view.findViewById(R.id.summary_freq);
        strength = view.findViewById(R.id.summary_strength);
        graph = view.findViewById(R.id.summary_graph);
        color = view.findViewById(R.id.summary_color);
        series = new LineGraphSeries<DataPoint>(new DataPoint[] {});
        graph.addSeries(series);
        graph.setScaleX(0.88f);
        graph.setScaleY(0.88f);
        graph.setTitle("Signal Strength over Time");
        graph.getGridLabelRenderer().setVerticalAxisTitle("dBm");
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(22);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(22);
        return view;
    }

    public void setInfo(Node node) {
        name.setText(node.getName());
        id.setText("id:\t" + node.getID());
        frequency.setText("Frequency:\t" + node.getFrequency());
        strength.setText("Strength:\t" + node.getLevel() + "dBm");
        ArrayList<Integer> history = node.getHist();

        int[] rgb = node.getColor();
        if (rgb[0] == 40 && rgb[1] == 133 && rgb[2] == 171) { //
            color.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.blue, null));

        } else if (rgb[0] == 245 && rgb[1] == 249 && rgb[2] == 49) {
            color.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.yellow, null));

        } else if (rgb[0] == 245 && rgb[1] == 64 && rgb[2] == 45) {
            color.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.red, null));

        } else if (rgb[0] == 66 && rgb[1] == 186 && rgb[2] == 150) {
            color.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.green, null));

        } else if (rgb[0] == 226 && rgb[1] == 26 && rgb[2] == 199) {
            color.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.purple, null));

        }


        DataPoint[] data = new DataPoint[history.size()+1];
        data[0] = new DataPoint(0,history.get(0));
        for(int i = 0; i < history.size(); i++){
            data[i+1] = new DataPoint(i+1,history.get(i));
        }



        series.resetData(data);
/*        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                String txt = "Time: "+(int)dataPoint.getX() +" Strength: " + (int)dataPoint.getY();
                Toast.makeText(getWindow().getContext(), txt, Toast.LENGTH_SHORT).show();

            }
        });*/

        //Log.i("data",Integer.toString(history.size()));
        /*for(int i = 0; i < hist.size(); i++){
            series.appendData(new DataPoint(i,hist.get(i)),false,20);
        }*/


        String[] xlabels = new String[data.length];
        for(int i = 0; i < xlabels.length; i++){
            xlabels[i] = Integer.toString(i);
        }
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(xlabels);
        graph.getGridLabelRenderer().setNumHorizontalLabels(1+data.length/2);



    }



}
