package com.example.android.wifianalyzer;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.wifianalyzer.Invoker;
import com.example.android.wifianalyzer.Signal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import processing.android.CompatUtils;
import processing.android.PFragment;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;


public class MainActivity extends AppCompatActivity {

    private static final String wifiTag = "wifi";
    private static final String bluetoothTag = "bluetooth";
    private static final String signalTag = "signal";
    private static final String permissionTag = "permission";
    private static final String locationTag = "location";
    private static final String lifecyleTag = "lifecycle";

    private BottomSheetBehavior mBottomSheetBehavior;
    private FragmentManager fm;
    private Base base;
    private SignalSummary signal_summary;

    private static final int PERMISSION_ALL = 1;
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private LocationManager locationManager;
    private LocationListener locationListener;

    private WifiManager mainWifi;
    private BluetoothAdapter mainBluetooth;
    private Map<String, Signal> wifiSignals;
    private Map<String,Signal> bluetoothSignals;
    private Map<String,Signal> newWifiSignals;
    private Map<String,Signal> newBluetoothSignals;
    private Map<String,Signal> tempBluetoothSignals;

    private Handler handler = new Handler();
    private HashMap<String,Signal> strongestSignals;
    private SignalReceiver signalReceiver;
    public Invoker invoker;

    private Node curNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        newWifiSignals = new HashMap<>();
        newBluetoothSignals = new HashMap<>();
        wifiSignals = new HashMap<>();
        bluetoothSignals = new HashMap<>();
        tempBluetoothSignals = new HashMap<>();

        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        fm = getSupportFragmentManager();
        base = (Base) getSupportFragmentManager().findFragmentById(R.id.base_fragment);
        signal_summary = (SignalSummary) getSupportFragmentManager().findFragmentById(R.id.signal_summary_fragment);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        fm.beginTransaction()
                                .show(base)
                                .hide(signal_summary)
                                .commit();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        fm.beginTransaction()
                                .hide(base)
                                .show(signal_summary)
                                .commit();
                        break;
                    default:
                        Log.i("plop", Integer.toString(newState));
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        if(!hasPermissions(this, PERMISSIONS)){
            requestLocationPermission();
        } else {
            Log.i(permissionTag,"permissions granted at start");
            start();
        }

    }

    public void start() {
        setSignalReceiver();
        startSignalSearch();
        FrameLayout frame = findViewById(R.id.container);
        invoker = new Invoker(this);
        PFragment fragment = new PFragment(invoker);
        fragment.setView(frame, this);

        // updating list of all signals every
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                wifiSignals = updateWifi();
                bluetoothSignals = updateBluetooth();
                strongestSignals = joinSignals();
                Log.i("pac", "===================");
                invoker.updateList(strongestSignals);

                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    public HashMap<String, Signal> updateWifi() {
        HashMap<String, Signal> updatedWifi = new HashMap<>();

        for (Signal signal : wifiSignals.values()) {
            if (newWifiSignals.containsKey(signal.getId())) {
                updatedWifi.put(signal.getId(), signal);
            }
        }
        for (Signal signal : newWifiSignals.values()) {
            if (updatedWifi.containsKey(signal.getId())) {
                Signal cur = updatedWifi.get(signal.getId());
                cur.update(signal.getLevel());
            } else {
                updatedWifi.put(signal.getId(), signal);
            }
        }
        return updatedWifi;
    }

    public HashMap<String, Signal> updateBluetooth() {
        HashMap<String, Signal> updatedBluetooth = new HashMap<>();
        for (Signal signal : bluetoothSignals.values()) {
            if (newBluetoothSignals.containsKey(signal.getId())) {
                updatedBluetooth.put(signal.getId(), signal);
            }
        }
        for (Signal signal : newBluetoothSignals.values()) {
            if (updatedBluetooth.containsKey(signal.getId())) {
                Signal cur = updatedBluetooth.get(signal.getId());
                cur.update(signal.getLevel());
            } else {
                updatedBluetooth.put(signal.getId(), signal);
            }
        }
        return updatedBluetooth;
    }

    public HashMap<String, Signal> joinSignals() {
        ArrayList<Signal> joinedSignals = new ArrayList<>();
        for (Signal signal : bluetoothSignals.values()) {
            joinedSignals.add(signal);
        }
        for (Signal signal : wifiSignals.values()) {
            joinedSignals.add(signal);
        }
        Collections.sort(joinedSignals);

        List<Signal> shortened = joinedSignals.subList(0,Math.min(5,joinedSignals.size()));

        Log.i("pac", "size: " + joinedSignals.size());

        HashMap<String, Signal> strongest = new HashMap<>();

        for (Signal signal : shortened) {
            strongest.put(signal.getId(), signal);
        }
        return strongest;
    }

    /* checks if permissions are granted*/
    public static boolean hasPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("permission", "Location permission has not been granted yet");
            return false;
        }
        return true;
    }

    public void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission required")
                    .setMessage("Location permission is needed to get signals in area")
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_ALL);
                        }
                    })
                    .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Enable the location permission in settings if you want to use this app", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }


    public void setSummary(Node node) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        curNode = node;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Node node = new Node("Asdf", "asdf",1234,1234, "asdf", invoker, 123, new ArrayList<Integer>());
                signal_summary.setInfo(curNode);
            }
        });
        fm.beginTransaction()
                .hide(base)
                .show(signal_summary)
                .commit();
    }

    public void setBase() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        fm.beginTransaction()
                .show(base)
                .hide(signal_summary)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){

        if (requestCode == PERMISSION_ALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                start();
            } else {
                Log.i(permissionTag, "here");
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                requestLocationPermission();
            }
        }

    }



    /*set up broadcast receiver to identify signals*/
    public void setSignalReceiver() {
        signalReceiver = new SignalReceiver();
        IntentFilter signalIntent = new IntentFilter();
        signalIntent.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        signalIntent.addAction(BluetoothDevice.ACTION_FOUND);
        signalIntent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(signalReceiver, signalIntent);
    }



    /*start seaching for signals in area signals*/
    public void startSignalSearch() {
        // for wifi
        mainWifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        mainWifi.startScan();
        // for bluetooth
        mainBluetooth = BluetoothAdapter.getDefaultAdapter();
        mainBluetooth.startDiscovery();
    }

    /*
        gets the list of found wifi access points and processes them
        starts search again
     */
    public void wifiReceived() {
        Log.i(wifiTag, "\n ========wifi search complete========== \n");

        HashMap<String, Signal> tempWifiSignals = new HashMap<>();
        List<ScanResult> wifiScanResults = mainWifi.getScanResults();
        for (ScanResult wifi: wifiScanResults) {
            Signal wifiSignal = new Signal(wifi);
            tempWifiSignals.put(wifi.BSSID, wifiSignal);
            Log.i(wifiTag,wifi.SSID + " - " + wifi.BSSID + " - " + wifi.level);

        }
        newWifiSignals = tempWifiSignals;
        Log.i(wifiTag, "Starting another search again");

        mainWifi.startScan();
    }

    /*
        gets a bluetooth device and adds its information to the foun devices
     */
    public void bluetoothReceived(Intent intent) {
        // Get the BluetoothDevice object from the Intent
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        // gets the rssi
        int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

        Signal bluetoothSignal = new Signal(device, rssi);
        tempBluetoothSignals.put(device.getAddress(), bluetoothSignal);

        Log.i(bluetoothTag,"name: " + device.getName() +
                ", address: " + device.getAddress() +
                "type: " + device.getType() +
                "bond state: " + device.getBondState()

        );

        /*ParcelUuid[] a = device.getUuids();
        for (int i = 0;i<a.length;i++) {
            Log.i("bluetooth","Uuid-" + i + ": " + a[i].toString());

        }
        */
    }

    /*
        process all bluetooth found since last search
        starts another search
     */
    public void finishBluetoothSearch() {
        Log.i(bluetoothTag, "\n=====bluetooth search complete, starting again=======\n");
        newBluetoothSignals = tempBluetoothSignals;
        tempBluetoothSignals = new HashMap<>();
        mainBluetooth.startDiscovery();
    }

    /*
        converts all received wifi objects to signal objects
        updates the list of received wifi signals
        starts another wifi scan
     */
    class SignalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                wifiReceived();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                bluetoothReceived(intent);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                finishBluetoothSearch();
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(lifecyleTag, "starting up");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(lifecyleTag, "stop");
        //unregisterReceiver(signalReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(lifecyleTag, "pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(lifecyleTag, "resume");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(lifecyleTag, "destroy");

    }
}
