package com.example.android.wifianalyzer;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;

public class SignalSummary extends Fragment {

    private TextView name;
    private TextView id;
    private TextView frequency;
    private TextView strength;
    private GraphView graph;
    private View color;
    private TextView status;
    private LineGraphSeries<DataPoint> series;
    private PointsGraphSeries<DataPoint> series2;

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
        status = view.findViewById(R.id.summary_status);
        series = new LineGraphSeries<DataPoint>(new DataPoint[] {});
        series2 = new PointsGraphSeries<DataPoint>(new DataPoint[] {});

        graph.addSeries(series);
        graph.addSeries(series2);
        graph.setScaleX(0.88f);
        graph.setScaleY(0.88f);
        graph.setTitle("Signal Strength over Time");
        graph.getGridLabelRenderer().setVerticalAxisTitle("dBm");
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(24);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(24);
        return view;
    }

    public void setInfo(Node node) {
        name.setText(node.getName());
        id.setText("id:\t" + node.getID());
        frequency.setText("Frequency:\t" + node.getFrequency());
        strength.setText("Strength:\t" + node.getLevel() + "dBm");
        GradientDrawable drawable = (GradientDrawable) status.getBackground();

        if (node.getLevel() > -45) {
            status.setText("good");
            drawable.setColor(Color.GREEN);
        } else if (node.getLevel() > -60) {
            status.setText("okay");
            drawable.setColor(Color.YELLOW);
        } else {
            status.setText("bad");
            drawable.setColor(Color.RED);
        }
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
        //
        int size = (history.size()-1)/5+1;
        DataPoint[] data = new DataPoint[size];


        for(int i = size-1; i >=0; i--){
            data[i] = new DataPoint((i+1-size)*5,history.get(history.size()-1-(size-1-i)*5));
        }
        

        series.resetData(data);
        series2.resetData(data);
        series2.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                String txt = "Time: "+(int)dataPoint.getX() +" Strength: " + (int)dataPoint.getY();
                Toast.makeText(getView().getContext(), txt, Toast.LENGTH_SHORT).show();

            }
        });
        series.setColor(Color.rgb(226,26,199));
        series2.setShape(PointsGraphSeries.Shape.POINT);
        series2.setSize(9);
        series2.setColor(Color.rgb(226,26,199));


        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-70);
        graph.getViewport().setMaxY(-20);
        graph.getGridLabelRenderer().setNumVerticalLabels(6);
        graph.getGridLabelRenderer().setNumHorizontalLabels(data.length);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph.getViewport().setDrawBorder(true);
        graph.getViewport().setBorderColor(Color.BLUE);




    }



}
