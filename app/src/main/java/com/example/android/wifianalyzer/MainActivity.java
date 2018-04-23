package com.example.android.wifianalyzer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.android.CompatUtils;
import processing.android.PFragment;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Wifi";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    WifiManager mainWifi;
    Handler handler = new Handler();
    private int num = 0;
    Map<String,Signal> wifiSignals;


    public Invoker invoker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frame = new FrameLayout(this);
        frame.setId(CompatUtils.getUniqueViewId());
        setContentView(frame, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        invoker = new Invoker(MainActivity.this);
        wifiSignals = new HashMap<>();


        PFragment fragment = new PFragment(invoker);
        fragment.setView(frame, this);
        requestAndScan();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Signal> signals = new ArrayList<>();
                for(Signal signal:wifiSignals.values()){
                    signals.add(signal);
                }
                Collections.sort(signals);
                invoker.updateList(signals.subList(0,Math.min(5,signals.size())));
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        System.out.println("onRequested");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 2000);
        mainWifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        mainWifi.startScan();

        if (invoker != null) {
            invoker.onRequestPermissionsResult(
                    requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (invoker != null) {
            invoker.onNewIntent(intent);
        }
    }




    // Wifi
    public void requestAndScan(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        } else{
            mainWifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            mainWifi.startScan();
            WifiReceiver wifiReceiver = new WifiReceiver();
            registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        }
    }

    class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> wifiScanResults = mainWifi.getScanResults();

            List<String> idsToRemove = new ArrayList<>();

            // update previously seen signals and remove ones that are no longer in range
            for (Signal oldWifi : wifiSignals.values()) {
                boolean keep = false;
                for (ScanResult wifi: wifiScanResults) {
                    if (oldWifi.getId().equals(wifi.BSSID)) {
                        keep = true;
                        oldWifi.addToHistory(WifiManager.calculateSignalLevel(wifi.level, 100));
                        oldWifi.setLevel(wifi.level);
                        oldWifi.setStrength(WifiManager.calculateSignalLevel(wifi.level, 100));
                        break;
                    }
                }
                if(!keep) {
                    idsToRemove.add(oldWifi.getId());
                    //invoker.removeNode(oldWifi.getId());
                }
            }

            for(String id: idsToRemove) {
                wifiSignals.remove(id);
            }

            // add new signals in range
            for (ScanResult wifi : wifiScanResults) {
                if (!wifiSignals.containsKey(wifi.BSSID)) {
                    wifiSignals.put(wifi.BSSID, new Signal(wifi));
                }
            }

            Log.i("wifi", "wifi search complete, starting again");
            mainWifi.startScan();
        }
    }


    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    public int calculateStrength(int input, int numLevel){
        int MAX_RSSI = -30;
        int MIN_RSSI = -70;
        if(input<MIN_RSSI){
            return 0;
        } else if(input > MAX_RSSI){
            return 99;
        }else{
            return (input-MIN_RSSI)*(numLevel - 1)/(MAX_RSSI - MIN_RSSI);
        }
    }

    private static String getIpAddress(WifiInfo wifiInfo) {
        int ip = wifiInfo.getIpAddress();
        return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));
    }








}