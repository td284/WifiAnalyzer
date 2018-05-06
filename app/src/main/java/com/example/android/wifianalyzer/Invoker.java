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


public class Invoker extends PApplet {
    final int CANVAS_WIDTH_DEFAULT  = 700;
    final int CANVAS_HEIGHT_DEFAULT = 600;
    private ForceDirectedGraph forceDirectedGraph;
    private MainActivity context;

    public Invoker(MainActivity context){
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
        forceDirectedGraph = new ForceDirectedGraph(this,context);
        return forceDirectedGraph;
    }

    public void addNode(String name, String id, float mass, int frequency, String venue, int level,ArrayList<Integer> hist){
        forceDirectedGraph.addNode(name, id,mass,frequency,venue,level,hist);
    }

    public void removeNode(String id){
        forceDirectedGraph.removeNode(id);
    }

    public void updateList(HashMap<String, Signal> newSignals){

        Log.i("plooop", "at new signals");

        List<String> idsToRemove = new ArrayList<>();

        for(Node node: forceDirectedGraph.nodes){

            if (newSignals.containsKey(node.getID())) {
                forceDirectedGraph.changeSize(node.getID(), newSignals.get(node.getID()).getStrength()*3);
                node.addToHistory(newSignals.get(node.getID()).getLevel());
            } else {
                idsToRemove.add(node.getID());
            }
        }
        for(String id: idsToRemove){
            removeNode(id);
        }
        for(Signal signal: newSignals.values()){
            boolean contains = false;
            for (Node node : forceDirectedGraph.nodes) {
                if (signal.getId().equals(node.getID())) {
                    contains = true;
                    break;
                }

            }
            if (!contains) {
                addNode(signal.getName(),signal.getId(),signal.getStrength()/10,
                        signal.getFreq(),signal.getVenue(),signal.getLevel(),signal.getHistory());
            }
        }

    }

}
