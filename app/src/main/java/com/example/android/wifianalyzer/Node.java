package com.example.android.wifianalyzer;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public class Node extends PApplet{

  private String name;
  private String id;
  private float mass;
  private int frequency;
  private String venue;
  private int level;
  private ArrayList<Integer> hist;

  private ArrayList<Node> adjacents;
  private ArrayList<Float> naturalSpringLengths;
  public float x;
  public float y;
  public float diameter;
  private float velocityX;
  private float velocityY;
  private float forceX;
  private float forceY;
  private boolean isHighlighted;
  private Invoker canvas;
  private int[] color;

  public Node(String name, String id, float mass, int frequency, String venue, Invoker canvas, int level, ArrayList<Integer> hist, int[] color){
    this.name = name;
    this.id = id;
    this.mass = mass;
    this.frequency = frequency;
    this.venue = venue;
    this.level = level;
    this.hist = hist;
    this.color = color;

    this.adjacents = new ArrayList<Node>();
    this.naturalSpringLengths = new ArrayList<Float>();

    this.set(-1.0f, -1.0f, -1.0f); //ad-hoc
    this.setVelocities(0.0f, 0.0f);
    this.setForceToApply(0.0f, 0.0f);
    this.isHighlighted = false;
    this.canvas = canvas;
  }

  public void add(Node adjacent, float naturalSpringLength){
    this.adjacents.add(adjacent);                       //the order of elements in the two ArrayLists must be the same.
    this.naturalSpringLengths.add(naturalSpringLength); //better to capture these as like key-value pairs...
  }
  public void set(float x, float y){
    this.x = x;
    this.y = y;
  }
  public void set(float x, float y, float diameter){
    this.set(x, y);
    this.diameter = diameter;
  }

  public int[] getColor(){
    return color;
  }

  public void setDiameter(int d) {
    diameter = d;
  }

  public void setVelocities(float velocityX, float velocityY){
    this.velocityX = velocityX;
    this.velocityY = velocityY;
  }
  public void setForceToApply(float forceX, float forceY){
    this.forceX = forceX;
    this.forceY = forceY;
  }

  public String getName(){ return this.name; }
  public String getID(){
    return this.id;
  }
  public float getMass(){
    return this.mass;
  }
  public int getFrequency() {return this.frequency; }
  public String getVenue() {return this.venue;}
  public int getLevel() {return this.level;}
  public ArrayList<Integer> getHist() {return this.hist;}
  public float getX(){
    return this.x;
  }
  public float getY(){
    return this.y;
  }
  public float getDiameter(){
    return this.diameter;
  }
  public float getVelocityX(){
    return this.velocityX;
  }
  public float getVelocityY(){
    return this.velocityY;
  }
  public float getForceX(){
    return this.forceX;
  }
  public float getForceY(){
    return this.forceY;
  }
  public int getSizeOfAdjacents(){
    return this.adjacents.size();
  }
  public Node getAdjacentAt(int index){
    return this.adjacents.get(index);
  }
  public float getNaturalSpringLengthAt(int index){
    return this.naturalSpringLengths.get(index);
  }

  public void addToHistory(int data){
    hist.add(data);
    while(hist.size()>20){
      hist.remove(0);
    }
  }

  public void addAdjacent(Node node){
    adjacents.add(node);
  }

  public void removeAdjacent(int index){
    adjacents.remove(index);
  }

  public void draw(int color){
    //Log.i("test", Float.toString(this.x));
    //Log.i("test", Float.toString(this.y));
    //Log.i("test", Float.toString(this.diameter));
    //Log.i("test", "================");
    if(this.isHighlighted){
      canvas.stroke(255, 178, 102);
      canvas.fill(255, 178, 102);
    }else{
      canvas.stroke(51, 51, 255);
      canvas.fill(this.color[0], this.color[1], this.color[2],191);

    }
    canvas.ellipse(this.x, this.y, this.diameter, this.diameter);
    if(!this.isHighlighted){ //tooltip
      canvas.fill(0);
      canvas.textSize(28);
      canvas.textAlign(CENTER, BOTTOM);

      canvas.text(this.name, this.x, this.y);
      canvas.textAlign(CENTER, TOP);
      canvas.text("freq: " + this.frequency, this.x, this.y);
    }
  }

  public void highlight(){
    this.isHighlighted = true;
  }
  public void dehighlight(){
    this.isHighlighted = false;
  }
  public boolean isIntersectingWith(int x, int y){
    float r = this.diameter / 2.0f;
    if(this.x - r <= x && x <= this.x + r && this.y - r <= y && y <= this.y + r)
      return true;
    else
      return false;
  }

  //@Override
  public String toString(){
    String adjacentIDsAndNaturalLengths = "[";
    for(int i = 0; i < this.adjacents.size(); i++)
      adjacentIDsAndNaturalLengths += this.adjacents.get(i).getID() + "(" + this.naturalSpringLengths.get(i) + "),";
    adjacentIDsAndNaturalLengths += "]";
    return "ID:" + this.id +
           ",MASS:" + this.mass +
           ",ADJACENTS(NATURAL_LEGTH):" + adjacentIDsAndNaturalLengths +
           ",X:" + this.x +
           ",Y:" + this.y +
           ",DIAMETER:" + this.diameter +
           ",HIGHLIGHTED:" + this.isHighlighted;
  }

}
