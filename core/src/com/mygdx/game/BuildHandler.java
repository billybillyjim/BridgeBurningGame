package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.game.GraphicalObjects.BackgroundCliff;
import com.mygdx.game.GraphicalObjects.BridgeUnit;
import com.mygdx.game.PhysicalObjects.BridgeJoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Luke on 4/22/2016.
 */
public class BuildHandler {

    private World world;
    private Stage stage;

    private ArrayList<BridgeUnit> bridgeUnits;
    private ParticleEffect fireEffect;
    private BackgroundCliff leftCliff, rightCliff;


    Material material;

    public BuildHandler(World world, Stage stage, ParticleEffect fireEffect, BackgroundCliff leftCliff, BackgroundCliff rightCliff){
        this.world = world;
        this.stage = stage;
        this.fireEffect = fireEffect;
        material = new Material(1);
        this.leftCliff = leftCliff;
        this.rightCliff = rightCliff;
        bridgeUnits = new ArrayList<BridgeUnit>();


    }


    public BridgeUnit makeBridgeUnit(float x, float y){
        BridgeUnit unit = new BridgeUnit(material, world, x, y, fireEffect);
        unit.setCreatedByPlayer(true);
        unit.getBody().setType(BodyDef.BodyType.DynamicBody);
        stage.addActor(unit);
        makeUnitLinkStructure(unit);
        bridgeUnits.add(unit);
        return unit;
    }

   private void makeUnitLinkStructure(BridgeUnit bridgeUnit) {
        checkDistanceFromCliff(bridgeUnit);
        HashMap<BridgeUnit, Integer> unitsToConnect = findUnitsToConnect(bridgeUnit);
        Set keySet  =  unitsToConnect.keySet();


        if (!keySet.isEmpty()){
            Iterator<BridgeUnit> it = keySet.iterator();
            while(it.hasNext()) {
                BridgeUnit unit = it.next();
                addUnits(bridgeUnit, unit, unitsToConnect.get(unit));
                }


            }

    }
    private void checkDistanceFromCliff(BridgeUnit bridgeUnit){
        int distanceFromCliff = 10;
        float leftXLimit = leftCliff.getX() + leftCliff.getWidth() + distanceFromCliff;
        float leftYLimit = leftCliff.getY() + leftCliff.getHeight() + distanceFromCliff;
        float rightXLimit = rightCliff.getX()  - distanceFromCliff;
        float rightYLimit = rightCliff.getY() + rightCliff.getHeight() + distanceFromCliff;
        System.out.println("xlim = " + leftXLimit + "ylim = " + leftYLimit);
        System.out.println("b x = " + bridgeUnit.getBody().getPosition().x + "b y = " + bridgeUnit.getBody().getPosition().y);
        if(bridgeUnit.getBody().getPosition().x <= leftXLimit && bridgeUnit.getBody().getPosition().y <= leftYLimit) {
            makeUnitCliffJoint(leftCliff, bridgeUnit,
                    bridgeUnit.getBody().getPosition().x - leftCliff.getWidth(), bridgeUnit.getBody().getPosition().y - leftCliff.getHeight());
        }
        if(bridgeUnit.getBody().getPosition().x >= rightXLimit && bridgeUnit.getBody().getPosition().y <= rightYLimit) {
            makeUnitCliffJoint(rightCliff, bridgeUnit,
                    bridgeUnit.getBody().getPosition().x - rightCliff.getWidth(), bridgeUnit.getBody().getPosition().y - rightCliff.getHeight());
        }
        System.out.println("xlim = " + rightXLimit + "ylim = " + rightYLimit);
        System.out.println("b x = " + bridgeUnit.getBody().getPosition().x + "b y = " + bridgeUnit.getBody().getPosition().y);
    }

    private HashMap<BridgeUnit, Integer> findUnitsToConnect(BridgeUnit unit){
        float x = unit.getBody().getPosition().x;
        float y = unit.getBody().getPosition().y;
        HashMap<BridgeUnit, Integer> linksToConnect = new HashMap<BridgeUnit, Integer>();
        for(BridgeUnit unit2 : bridgeUnits){
            if(unit2.isCreatedByPlayer()) {
                double distance = Math.sqrt((Math.pow(unit2.getBody().getPosition().x - x, 2) + (Math.pow(unit2.getBody().getPosition().y - y, 2))));
                if (distance <= 100) {

                    int numOfBridgeUnits = (int) distance / BridgeUnit.WIDTH;
                    linksToConnect.put(unit2, numOfBridgeUnits);
                }
            }
        }



        return linksToConnect;
    }

    //code got from stack overflow "http://stackoverflow.com/questions/10825174/calculate-next-point-on-2d-linear-vector"
    public int[] getNextLinePoint(int x,int y,int x2, int y2) {
        int w = x2 - x;
        int h = y2 - y;
        int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
        if (w<0) dx1 = -1; else if (w>0) dx1 = 1;
        if (h<0) dy1 = -1; else if (h>0) dy1 = 1;
        if (w<0) dx2 = -1; else if (w>0) dx2 = 1;
        int longest = Math.abs(w);
        int shortest = Math.abs(h);
        if (!(longest>shortest)) {
            longest = Math.abs(h);
            shortest = Math.abs(w);
            if (h<0) dy2 = -1; else if (h>0) dy2 = 1;
            dx2 = 0;
        }
        int numerator = longest >> 1;
        numerator += shortest;
        if (!(numerator<longest)) {
            numerator -= longest;
            x += dx1;
            y += dy1;
        } else {
            x += dx2;
            y += dy2;
        }
        int[] res = {x, y};
        return res;
    }




    private void addUnits(BridgeUnit unit1, BridgeUnit unit2, int numOfLinks){
        ArrayList<BridgeUnit> units = new ArrayList<BridgeUnit>();
        units.add(unit1);
        int x1 = (int) unit1.getBody().getPosition().x;
        int y1 = (int) unit1.getBody().getPosition().y;
        int x2 = (int) unit2.getBody().getPosition().x;
        int y2 = (int) unit2.getBody().getPosition().y;


        for(int i = 0; i < numOfLinks; i++){
            int[] pos = findNextXYPos(x1, y1, x2, y2);
            x1 = pos[0];
            y1 = pos[1];
            BridgeUnit unit3 = new BridgeUnit(material, world, x1, y1, fireEffect);
            stage.addActor(unit3);
            units.add(unit3);
            bridgeUnits.add(unit3);

        }
        units.add(unit2);
        makeUnitJoint(units);
    }
    private void makeUnitJoint(ArrayList<BridgeUnit> units){
        for(int i = 0; i < units.size()-1; i++){
            makeJoint(units.get(i).getBody(), units.get(i+1).getBody());
        }
    }



    private int[] findNextXYPos(int x1, int y1, int x2, int y2){
        int[] nextXYPos = {};
        for(int i = 0; i < BridgeUnit.WIDTH; i++) {
            nextXYPos = getNextLinePoint(x1, y1, x2, y2);
            x1 = nextXYPos[0];
            y1 = nextXYPos[1];
        }
        return nextXYPos;
    }

    public void makeUnitCliffJoint(BackgroundCliff cliff, BridgeUnit unit, float x, float y){
        BridgeJoint joint = makeJoint(cliff.getBody(), unit.getBody());
        joint.getrJointDef().localAnchorA.set(
                unit.getX() - x,
                unit.getY() - y);
        joint.getrJointDef().localAnchorB.set(x, y);
    }


    private BridgeJoint makeJoint(Body unitBody, Body linkBody){
        BridgeJoint joint = new BridgeJoint();
        joint.CreateJoint(unitBody, linkBody);
        joint.getrJointDef().localAnchorA.set(
                linkBody.getPosition().x - unitBody.getPosition().x,
                linkBody.getPosition().y - unitBody.getPosition().y);

        world.createJoint(joint.getrJointDef());
        return joint;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public ArrayList<BridgeUnit> getBridgeUnits() {
        return bridgeUnits;
    }





}
