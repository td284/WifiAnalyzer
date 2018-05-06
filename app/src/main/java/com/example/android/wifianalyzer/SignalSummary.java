package com.example.android.wifianalyzer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SignalSummary extends Fragment {

    private TextView name;
    private TextView id;
    private TextView frequency;
    private TextView strength;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signal_summary, container, false);
        name = view.findViewById(R.id.name);
        id = view.findViewById(R.id.id);
        frequency = view.findViewById(R.id.frequency);
        strength = view.findViewById(R.id.strength);

        return view;
    }

    public void setInfo(Node node) {
        Log.i("ploop", "setting this shit");
        name.setText("name: " + node.getName());
        id.setText("id: " + node.getID());
        frequency.setText("frequency: " + node.getFrequency());
        strength.setText("strength: " + node.getLevel());
    }
}
