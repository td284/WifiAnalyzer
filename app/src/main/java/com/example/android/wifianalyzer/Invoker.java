package com.example.android.wifianalyzer;

/**
 * Created by guanyuchen on 4/14/18.
 */
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;


public class Invoker extends PApplet{
    final int CANVAS_WIDTH_DEFAULT  = 700;
    final int CANVAS_HEIGHT_DEFAULT = 1000;
    Handler handler = new Handler();
    public ForceDirectedGraph forceDirectedGraph;
    public Context context;

    public Invoker(Context context){
        this.context = context;
    }

    public void settings() {
        size(CANVAS_WIDTH_DEFAULT, CANVAS_HEIGHT_DEFAULT);
    }

    public void setup(){
        int canvasWidth = CANVAS_WIDTH_DEFAULT;
        int canvasHeight = CANVAS_HEIGHT_DEFAULT;
        forceDirectedGraph = createForceDirectedGraphFrom();
        forceDirectedGraph.set(0.0f, 0.0f, (float)canvasWidth, (float)canvasHeight);
        forceDirectedGraph.initializeNodeLocations();
    }

    public void draw(){
        background(255);
        forceDirectedGraph.draw();
        strokeWeight(1.5f);
    }

    public void mouseMoved(){
        if(forceDirectedGraph.isIntersectingWith(mouseX, mouseY))
            forceDirectedGraph.onMouseMovedAt(mouseX, mouseY);
    }
    public void mousePressed(){
        if(forceDirectedGraph.isIntersectingWith(mouseX, mouseY))
            forceDirectedGraph.onMousePressedAt(mouseX, mouseY);
    }
    public void mouseDragged(){
        if(forceDirectedGraph.isIntersectingWith(mouseX, mouseY))
            forceDirectedGraph.onMouseDraggedTo(mouseX, mouseY);
    }
    public void mouseReleased(){
        if(forceDirectedGraph.isIntersectingWith(mouseX, mouseY))
            forceDirectedGraph.onMouseReleased();
    }

    public ForceDirectedGraph createForceDirectedGraphFrom(){
        ForceDirectedGraph forceDirectedGraph = new ForceDirectedGraph(this,context);
        return forceDirectedGraph;
    }

    public void addNode(String name, String id, float mass, int frequency){
        forceDirectedGraph.addNode(name, id,mass,frequency);
    }

    public void removeNode(String id){
        forceDirectedGraph.removeNode(id);
    }

    public void updateList(List<Signal> newList){
        List<String> idsToRemove = new ArrayList<>();

        for(Node node: forceDirectedGraph.nodes){
            boolean keep = false;
            for(int j = 0; j < newList.size(); j++){
                if(node.getID().equals(newList.get(j).getId())){
                    keep = true;
                    forceDirectedGraph.changeSize(node.getID(), WifiManager.calculateSignalLevel(newList.get(j).getLevel(),100)*3);
                    break;
                }
            }
            if(!keep) {
                idsToRemove.add(node.getID());
            }
        }
        for(String id: idsToRemove){
            removeNode(id);
        }

        for(int k = 0; k < newList.size(); k++){
            boolean add = true;
            for(int i = 0; i < forceDirectedGraph.nodes.size(); i++){
                if(newList.get(k).getId().equals(forceDirectedGraph.nodes.get(i).getID())) {
                    add = false;
                    break;
                }
            }
            if(add) {
                addNode(newList.get(k).getName(),newList.get(k).getId(),newList.get(k).getStrength()/10,newList.get(k).getFreq());
            }
        }
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
}
