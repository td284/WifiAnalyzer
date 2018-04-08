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
    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                requestAndScan();
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);

        /* may be useful if you want to scan for unique wifi sources
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

    public void requestAndScan(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        } else{
            scanWifi();
        }
    }

    public void scanWifi(){
        mainWifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        mainWifi.startScan();
        Log.i(TAG, "started scan with result");
        scanList = mainWifi.getScanResults();
        Collections.sort(scanList, new Comparator<ScanResult>(){
            public int compare(ScanResult a, ScanResult b){
                return b.level-a.level;
            }
        });
        LinearLayout ll = findViewById(R.id.wifi_container);
        ll.removeAllViews();
        for (int index = 0; index < Math.min(10, scanList.size()); index++) {
            ScanResult scanResult = scanList.get(index);
            LayoutInflater layout = getLayoutInflater();
            View v = layout.inflate(R.layout.wifi_elem,null);
            TextView tv = (TextView) v.findViewById(R.id.elem);
            tv.setText(scanResult.SSID + "\n\t\t" + scanResult.BSSID + "\n\t\tLevel: "
                    + scanResult.level + "dBm\n\t\tStrength: " + calculateStrength(scanResult.level,100)
                    + "\n\t\tFreq: " + scanResult.frequency);
            ll.addView(v);
        }
    }

    public int calculateStrength(int input, int numLevel){
        int MAX_RSSI = -30;
        int MIN_RSSI = -70;
        if(input<MIN_RSSI){
            return 0;
        } else{
            return (input-MIN_RSSI)*(numLevel - 1)/(MAX_RSSI - MIN_RSSI);
        }
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
        scanWifi();
    }

    private static String getIpAddress(WifiInfo wifiInfo) {
        int ip = wifiInfo.getIpAddress();
        return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));
    }


}
