package com.example.android.wifianalyzer;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.wifi.*;
import android.Manifest;
import android.util.Log;
import android.widget.*;
import android.view.*;
import java.util.*;
import android.content.pm.*;
import android.content.Context;

//http://www.includehelp.com/code-snippets/android-application-to-display-available-wifi-network-and-connect-with-specific-network.aspx
//https://stackoverflow.com/questions/7050101/wifi-scan-results-broadcast-receiver-not-working/7050155
//https://stackoverflow.com/questions/17167084/android-scanning-wifi-network-selectable-list
//http://developer.radiusnetworks.com/2015/09/29/is-your-beacon-app-ready-for-android-6.html
//https://stackoverflow.com/questions/32151603/scan-results-available-action-return-empty-list-in-android-6-0/32151901#32151901
//https://www.tutorialspoint.com/android/android_wi_fi.htm
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Wifi";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    WifiManager mainWifi;
    List<ScanResult> scanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        } else{
            mainWifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            mainWifi.startScan();
            Log.i(TAG, "started scan with result");
            scanList = mainWifi.getScanResults();
            for (ScanResult scanResult : scanList) {
                LayoutInflater layout = getLayoutInflater();
                View v = layout.inflate(R.layout.wifi_elem,null);
                TextView tv = (TextView) v.findViewById(R.id.elem);
                tv.setText(scanResult.SSID + "\n\t\tlevel: " + scanResult.level + "dBm\n\t\tfreq: " + scanResult.frequency);
                LinearLayout ll = findViewById(R.id.wifi_container);
                ll.addView(v);
            }
        }

        /*
        wifiListAdapter = new WifiListAdapter(ConnectToInternetActivity.this, mItems);
        lv.setAdapter(wifiListAdapter);
        int size = results.size();
        HashMap<String, Integer> signalStrength = new HashMap<String, Integer>();
        try {
            for (int i = 0; i < size; i++) {
                ScanResult result = results.get(i);
                if (!result.SSID.isEmpty()) {
                    String key = result.SSID + " "
                            + result.capabilities;
                    if (!signalStrength.containsKey(key)) {
                        signalStrength.put(key, i);
                        mItems.add(result);
                        wifiListAdapter.notifyDataSetChanged();
                    } else {
                        int position = signalStrength.get(key);
                        ScanResult updateItem = mItems.get(position);
                        if (calculateSignalStength(wifiManager, updateItem.level) >
                                calculateSignalStength(wifiManager, result.level)) {
                            mItems.set(position, updateItem);
                            wifiListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/



    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        System.out.println("onRequested");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 2000);
        mainWifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        mainWifi.startScan();
        Log.i(TAG, "started scan with result");
        scanList = mainWifi.getScanResults();
        for (ScanResult scanResult : scanList) {
            LayoutInflater layout = getLayoutInflater();
            View v = layout.inflate(R.layout.wifi_elem,null);
            TextView tv = (TextView) v.findViewById(R.id.elem);
            tv.setText(scanResult.SSID + "\n\t\tlevel: " + scanResult.level + "dBm\n\t\tfreq: " + scanResult.frequency);
            LinearLayout ll = findViewById(R.id.wifi_container);
            ll.addView(v);
        }
    }

    private static String getIpAddress(WifiInfo wifiInfo) {
        String result;
        int ip = wifiInfo.getIpAddress();

        result = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));

        return result;
    }


}
