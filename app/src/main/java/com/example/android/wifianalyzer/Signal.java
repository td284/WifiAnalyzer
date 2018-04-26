package com.example.android.wifianalyzer;
import android.bluetooth.BluetoothDevice;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Signal implements Comparable {
    private String id; //mac address or BSSID
    private Integer level; //RSSI. -70...-30
    private Integer strength; // Calculated strength based on RSSI. 0..100
    private Integer freq; //frequency (only wifi)
    private String name; // SSID for wifi, name for bluetooth
    private String venue; // registered venue of access point (only wifi)
    private String type; //bluetooth or wifi


    Signal(ScanResult wifi) {
        this.id = wifi.BSSID;
        this.level = wifi.level;
        this.strength = calculateStrength(wifi.level, 100);
        this.freq = wifi.frequency;
        this.name = wifi.SSID;
        this.venue = wifi.venueName.toString();
        this.type = "wifi";
    }
    Signal(BluetoothDevice bluetooth, int rssi) {
        this.id = bluetooth.getAddress();
        this.level = rssi;
        this.strength = calculateStrength(rssi, 100);
        this.name = bluetooth.getName();
        this.type = "bluetooth";
    }
    @Override
    public int compareTo(Object o) {
        Signal otherSignal = (Signal) o;
        return otherSignal.strength - strength;
    }
    @Override
    public String toString() {
        return type + " - " + name + " - " + strength;
    }
    @Override
    public boolean equals(Object obj) {
        Signal otherSignal = (Signal) obj;
        return id.equals(otherSignal.id);
    }
    public String getId() {
        return id;
    }
    public int getLevel() {
        return level;
    }
    public int getStrength() {
        return strength;
    }
    public String getName() {
        return name;
    }
    public String getVenue() {
        return venue;
    }
    public int getFreq() {
        return freq;
    }
    public String getType() {
        return type;
    }

    public void setLevel(int level){
        this.level = level;
    }

    public void setStrength(int strength){
        this.strength = strength;
    }

    public int calculateStrength(int input, int numLevel) {
        int MAX_RSSI = -30;
        int MIN_RSSI = -80;
        if (input < MIN_RSSI) {
            return 0;
        } else if (input > MAX_RSSI) {
            return 99;
        } else {
            return (input - MIN_RSSI) * (numLevel - 1) / (MAX_RSSI - MIN_RSSI);
        }
    }
}