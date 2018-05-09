package com.example.android.wifianalyzer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ForceDirectedGraph extends Viewport{

  private static final float TOTAL_KINETIC_ENERGY_DEFAULT = MAX_FLOAT;
  public static final float SPRING_CONSTANT_DEFAULT       = 0.1f;
  public static final float COULOMB_CONSTANT_DEFAULT      = 7500.0f;
  public static final float DAMPING_COEFFICIENT_DEFAULT   = 0.3f;
  public static final float TIME_STEP_DEFAULT             = 1.0f;

  public ArrayList<Node> nodes;
  private float totalKineticEnergy;
  private float springConstant;
  private float coulombConstant;
  private float dampingCoefficient;
  private float timeStep;
  private Invoker canvas;
  public MainActivity context;
  private Node lockedNode;
  private List<int[]> colors;

  public ForceDirectedGraph(Invoker canvas, MainActivity context){
    super();
    this.nodes = new ArrayList<Node>();
    this.totalKineticEnergy = TOTAL_KINETIC_ENERGY_DEFAULT;
    this.springConstant = SPRING_CONSTANT_DEFAULT;
    this.coulombConstant = COULOMB_CONSTANT_DEFAULT;
    this.dampingCoefficient = DAMPING_COEFFICIENT_DEFAULT;
    this.timeStep = TIME_STEP_DEFAULT;
    this.canvas = canvas;
    this.context = context;
    this.lockedNode = null;

    this.colors = new ArrayList<>();
    colors.add(new int[]{40,133,171});
    colors.add(new int[]{245,249,49});
    colors.add(new int[]{245,64,45});
    colors.add(new int[]{66,186,150});
    colors.add(new int[]{226,26,199});
  }

  public void add(Node node){
    this.nodes.add(node);
  }
  public void addEdge(String id1, String id2, float naturalSpringLength){
    Node node1 = this.getNodeWith(id1);
    Node node2 = this.getNodeWith(id2);
    node1.add(node2, naturalSpringLength);
    node2.add(node1, naturalSpringLength);
  }

  private Node getNodeWith(String id){
    Node node = null;
    for(int i = 0; i < this.nodes.size(); i++){
      Node target = this.nodes.get(i);
      if(target.getID().equals(id)){
        node = target;
        break;
      }
    }
    return node;
  }

  public void initializeNodeLocations(){
    float maxMass = 0.0f;
    for(int i = 0; i < this.nodes.size(); i++){
      float mass = this.nodes.get(i).getMass();
      if(mass > maxMass)
        maxMass = mass;
    }
    float nodeSizeRatio;
    if(this.getWidth() < this.getHeight())
      nodeSizeRatio = this.getWidth() / (maxMass * 5.0f); //ad-hoc
    else
      nodeSizeRatio = this.getHeight() / (maxMass * 5.0f); //ad-hoc
    float offset = nodeSizeRatio * maxMass;
    float minXBound = this.getX() + offset;
    float maxXBound = this.getX() + this.getWidth() - offset;
    float minYBound = this.getY() + offset;
    float maxYBound = this.getY() + this.getHeight() - offset;
    for(int i = 0; i < this.nodes.size(); i++){
      Node node = this.nodes.get(i);
      float x = random(minXBound, maxXBound);
      float y = random(minYBound, maxYBound);
      float d = node.getMass() * nodeSizeRatio;
      node.set(x, y, d);
    }
  }

  public void addNode(String name, String id, float mass, int frequency, String venue, int level, ArrayList<Integer> hist){
    Node newNode = new Node(name, id,mass,frequency,venue, canvas, level, hist,colors.remove(0));
    newNode.getHist().add(level);
    float nodeSizeRatio;
    if(this.getWidth() < this.getHeight())
      nodeSizeRatio = this.getWidth() / (mass * 5.0f); //ad-hoc
    else
      nodeSizeRatio = this.getHeight() / (mass * 5.0f); //ad-hoc
    float offset = nodeSizeRatio * mass;
    float minXBound = this.getX() + offset;
    float maxXBound = this.getX() + this.getWidth() - offset;
    float minYBound = this.getY() + offset;
    float maxYBound = this.getY() + this.getHeight() - offset;
    float x = random(minXBound, maxXBound);
    float y = random(minYBound, maxYBound);
    float d = newNode.getMass() * nodeSizeRatio;
    newNode.set(x, y, mass*30);

    for(int i = 0; i < nodes.size(); i++){
      if(nodes.get(i).getName().equals(newNode.getName())){
        newNode.addAdjacent(nodes.get(i));
        nodes.get(i).addAdjacent(newNode);
      }
    }

    nodes.add(newNode);
  }

  public void removeNode(String id){
      for(int i = 0; i < nodes.size(); i++){
          if(nodes.get(i).getID().equals(id)){
              this.colors.add(nodes.get(i).getColor());
              nodes.remove(i);
              break;
          }
      }
    for(int i = 0; i < nodes.size(); i++){
      for(int j = 0; j < nodes.get(i).getSizeOfAdjacents(); j++){
        if(nodes.get(i).getAdjacentAt(j).getID().equals(id)){
          nodes.get(i).removeAdjacent(j);
          break;
        }
      }
    }

  }

  public void changeSize(String id, int size){
    for(int i = 0; i < nodes.size(); i++){
      if(nodes.get(i).getID().equals(id)){
        nodes.get(i).setDiameter(size);
      }
    }

  }

  public void draw(){
    this.totalKineticEnergy = this.calculateTotalKineticEnergy();

    canvas.strokeWeight(1.5f);
    this.drawEdges();
    for(int i = 0; i < this.nodes.size(); i++) {
      this.nodes.get(i).draw(50 + i * 50);
    }


    canvas.fill(0);
    canvas.textAlign(LEFT, TOP);
    float offset = canvas.textAscent() + canvas.textDescent();
    /*canvas.text("Total Kinetic Energy: " + this.totalKineticEnergy, this.getX(), this.getY());
    canvas.text("Spring Constant: " + this.springConstant, this.getX(), this.getY() + offset);
    canvas.text("Coulomb Constant: " + this.coulombConstant, this.getX(), this.getY() + offset * 2.0f);
    canvas.text("Damping Coefficient: " + this.dampingCoefficient, this.getX(), this.getY() + offset * 3.0f);
    canvas.text("Time Step: " + this.timeStep, this.getX(), this.getY() + offset * 4.0f);*/
  }

  private void drawEdges(){
    canvas.stroke(51, 51, 255);
    for(int i = 0; i < this.nodes.size(); i++){
      Node node1 = this.nodes.get(i);
      for(int j = 0; j < node1.getSizeOfAdjacents(); j++){
        Node node2 = node1.getAdjacentAt(j);
        canvas.line(node1.getX(), node1.getY(), node2.getX(), node2.getY());
      }
    }
  }

  private float calculateTotalKineticEnergy(){ //ToDo:check the calculation in terms of Math...
    for(int i = 0; i < this.nodes.size(); i++){
      Node target = this.nodes.get(i);
      if(target == this.lockedNode)
        continue;

      float forceX = 0.0f;
      float forceY = 0.0f;
      for(int j = 0; j < this.nodes.size(); j++){ //Coulomb's law
        Node node = this.nodes.get(j);
        if(node != target){
          float dx = target.getX() - node.getX();
          float dy = target.getY() - node.getY();
          float distance = sqrt(dx * dx + dy * dy);
          float xUnit = dx / distance;
          float yUnit = dy / distance;

          float coulombForceX = this.coulombConstant * (target.getMass() * node.getMass()) / pow(distance, 2.0f) * xUnit;
          float coulombForceY = this.coulombConstant * (target.getMass() * node.getMass()) / pow(distance, 2.0f) * yUnit;

          forceX += coulombForceX;
          forceY += coulombForceY;
        }
      }

      for(int j = 0; j < target.getSizeOfAdjacents(); j++){ //Hooke's law
        Node node = target.getAdjacentAt(j);
        float springLength = node.getDiameter();//target.getNaturalSpringLengthAt(j);
        float dx = target.getX() - node.getX();
        float dy = target.getY() - node.getY();
        float distance = sqrt(dx * dx + dy * dy);
        float xUnit = dx / distance;
        float yUnit = dy / distance;

        float d = distance - springLength;

        float springForceX = -1 * this.springConstant * d * xUnit;
        float springForceY = -1 * this.springConstant * d * yUnit;

        forceX += springForceX;
        forceY += springForceY;
      }

      target.setForceToApply(forceX, forceY);
    }

    float totalKineticEnergy = 0.0f;
    for(int i = 0; i < this.nodes.size(); i++){
      Node target = this.nodes.get(i);
      if(target == this.lockedNode)
        continue;

      float forceX = target.getForceX();
      float forceY = target.getForceY();

      float accelerationX = forceX / target.getMass();
      float accelerationY = forceY / target.getMass();

      float velocityX = (target.getVelocityX() + this.timeStep * accelerationX) * this.dampingCoefficient;
      float velocityY = (target.getVelocityY() + this.timeStep * accelerationY) * this.dampingCoefficient;

      float x = target.getX() + this.timeStep * target.getVelocityX() + accelerationX * pow(this.timeStep, 2.0f) / 2.0f;
      float y = target.getY() + this.timeStep * target.getVelocityY() + accelerationY * pow(this.timeStep, 2.0f) / 2.0f;

      float radius = target.getDiameter() / 2.0f; //for boundary check
      if(x < this.getX() + radius)
        x = this.getX() + radius;
      else if(x > this.getX() + this.getWidth() - radius)
        x =  this.getX() + this.getWidth() - radius;
      if(y < this.getY() + radius)
        y = this.getY() + radius;
      else if(y > this.getY() + this.getHeight() - radius)
        y =  this.getX() + this.getHeight() - radius;

      target.set(x, y);
      target.setVelocities(velocityX, velocityY);
      target.setForceToApply(0.0f, 0.0f);

      totalKineticEnergy += target.getMass() * sqrt(velocityX * velocityX + velocityY * velocityY) / 2.0f;
    }
    return totalKineticEnergy;
  }

  public void onMouseMovedAt(int x, int y){
    for(int i = 0; i < this.nodes.size(); i++){
      Node node = this.nodes.get(i);
      if(node.isIntersectingWith(x, y))
        node.highlight();
      else
        node.dehighlight();
    }
  }

  public void onMousePressedAt(int x, int y){
    System.out.println("---------------------------------");
    for(int i = 0; i < this.nodes.size(); i++){
      Node node = this.nodes.get(i);
      if(node.isIntersectingWith(x, y)){

        //this.lockedNode = node;
        //this.lockedNode.setVelocities(0.0f, 0.0f);

        context.setSummary(node);
        return;
      }
    }
    context.switchToBase();
  }
  public void onMouseDraggedTo(int x, int y){
    /*if(this.lockedNode != null){
      float radius = this.lockedNode.getDiameter() / 2.0f; //for boundary check
      if(x < this.getX() + radius)
        x = (int)(this.getX() + radius);
      else if(x > this.getX() + this.getWidth() - radius)
        x =  (int)(this.getX() + this.getWidth() - radius);
      if(y < this.getY() + radius)
        y = (int)(this.getY() + radius);
      else if(y > this.getY() + this.getHeight() - radius)
        y =  (int)(this.getX() + this.getHeight() - radius);

      this.lockedNode.set(x, y);
      this.lockedNode.setVelocities(0.0f, 0.0f);
    }*/
  }
  public void onMouseReleased(){
    this.lockedNode = null;
  }

  public void dumpInformation(){
    println("--------------------");
    for(int i = 0; i < this.nodes.size(); i++)
      println(this.nodes.get(i).toString());
    println("--------------------");
  }



}
