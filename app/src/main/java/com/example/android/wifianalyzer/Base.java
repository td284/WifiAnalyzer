package com.example.android.wifianalyzer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class Base extends Fragment {

    private TextView signal_1_text;
    private TextView signal_2_text;
    private TextView signal_3_text;
    private TextView signal_4_text;
    private TextView signal_5_text;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base, container, false);
        signal_1_text = view.findViewById(R.id.signal_1_text);
        signal_2_text = view.findViewById(R.id.signal_2_text);
        signal_3_text = view.findViewById(R.id.signal_3_text);
        signal_4_text = view.findViewById(R.id.signal_4_text);
        signal_5_text = view.findViewById(R.id.signal_5_text);

        return view;
    }

    public void setInfo(List<Node> nodes) {
        boolean[] seen = new boolean[5];
        for (Node node : nodes) {
            int[] rgb = node.getColor();
            if (rgb[0] == 40 && rgb[1] == 133 && rgb[2] == 171) { //
                signal_1_text.setText(node.getName());
                seen[0] = true;
            } else if (rgb[0] == 245 && rgb[1] == 249 && rgb[2] == 49) {
                signal_5_text.setText(node.getName());
                seen[4] = true;
            } else if (rgb[0] == 245 && rgb[1] == 64 && rgb[2] == 45) {
                signal_3_text.setText(node.getName());
                seen[2] = true;
            } else if (rgb[0] == 66 && rgb[1] == 186 && rgb[2] == 150) {
                signal_2_text.setText(node.getName());
                seen[1] = true;
            } else if (rgb[0] == 226 && rgb[1] == 26 && rgb[2] == 199) {
                signal_4_text.setText(node.getName());
                seen[3] = true;
            }
        }
        if (!seen[0]) {
            signal_1_text.setText("");
        }
        if (!seen[1]) {
            signal_2_text.setText("");
        }
        if (!seen[2]) {
            signal_3_text.setText("");
        }
        if (!seen[3]) {
            signal_4_text.setText("");
        }
        if (!seen[4]) {
            signal_5_text.setText("");
        }

    }

}
