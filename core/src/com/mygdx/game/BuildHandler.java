package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.physics.box2d.*;
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

    /**Makes a new bridgeUnit at a given x,y.
     * These units are flagged as createdByPlayer, making them nodes
     * that other BridgeUnits can build from. After the unit is made
     * this method called makeUnitLinkStructure, which creates
     * the line of bridgeUnits between the newly created one and any
     * other createdByPlayer units nearby.
     **/
    public BridgeUnit makeBridgeUnit(float x, float y){
        BridgeUnit unit = new BridgeUnit(material, world, x, y, fireEffect);
        unit.setCreatedByPlayer(true);

        stage.addActor(unit);
        makeUnitLinkStructure(unit);
        bridgeUnits.add(unit);
        return unit;
    }
    /**This method takes a bridgeUnit, checks to see if it should attach
     * directly to the cliff, and then finds any nearby BridgeUnits to 
     * attach to. Then it runs addUnits to create new BridgeUnits in between them.
     * The bridgeUnits only connect to other bridgeUnits that
     * have the createdByPlayer set to true. 
     **/
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
    /** This method checks to see if the bridgeUnit should create a joint
     * between itself and the cliff. This allows the user to build BridgeUnits
     * that attach to the cliff if they are close enough.
     **/
    private void checkDistanceFromCliff(BridgeUnit bridgeUnit){
        int distanceFromCliff = 10;
        float leftXLimit = leftCliff.getX() + leftCliff.getWidth() + distanceFromCliff;
        float leftYLimit = leftCliff.getY() + leftCliff.getHeight() + distanceFromCliff;
        float rightXLimit = rightCliff.getX()  - distanceFromCliff;
        float rightYLimit = rightCliff.getY() + rightCliff.getHeight() + distanceFromCliff;

        if(bridgeUnit.getBody().getPosition().x <= leftXLimit && bridgeUnit.getBody().getPosition().y <= leftYLimit) {
            makeUnitCliffJoint(leftCliff, bridgeUnit,
                    bridgeUnit.getBody().getPosition().x - leftCliff.getWidth(), bridgeUnit.getBody().getPosition().y - leftCliff.getHeight());
        }
        if(bridgeUnit.getBody().getPosition().x >= rightXLimit && bridgeUnit.getBody().getPosition().y <= rightYLimit) {
            makeUnitCliffJoint(rightCliff, bridgeUnit,
                    bridgeUnit.getBody().getPosition().x - rightCliff.getWidth(), bridgeUnit.getBody().getPosition().y - rightCliff.getHeight());
        }

    }
    /**This method finds the the bridge units that have been created  by the user that are within 100 units from the input unit
     *  and returns a HashMap that contains all the bridge units that the input one should be connected with and the number of units that
     *  should be created between them.
     * @param unit BridgeUnit the user has just created
     **/
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
    //Magic code that takes xy-coordinates of two points in a plane and calculate the next point in the line between them.
    //code from stack overflow "http://stackoverflow.com/questions/10825174/calculate-next-point-on-2d-linear-vector"
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

    /**
     *This methods takes two bridge units that should be connected and the number of units between them.
     * It call helper methods to determine the xy-postition of the new units and creates them. It also calls a helper
     * method to create joint between the recently create units.
     * @param unit1
     * @param unit2
     * @param numOfUnits
     */
    private void addUnits(BridgeUnit unit1, BridgeUnit unit2, int numOfUnits){
        ArrayList<BridgeUnit> units = new ArrayList<BridgeUnit>();
        units.add(unit1);
        int x1 = (int) unit1.getBody().getPosition().x;
        int y1 = (int) unit1.getBody().getPosition().y;
        int x2 = (int) unit2.getBody().getPosition().x;
        int y2 = (int) unit2.getBody().getPosition().y;


        for(int i = 0; i < numOfUnits; i++){
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

    /**
     * goes through an array of BridgeUnits and creates joint between them
     * @param units
     */
    private void makeUnitJoint(ArrayList<BridgeUnit> units){
        for(int i = 0; i < units.size()-1; i++){
            makeJoint(units.get(i).getBody(), units.get(i+1).getBody());
        }
    }

   // private ArrayList<Integer[]>


    /**
     * This method is responsible to find the location of the next bridge unit in a line.
     * @param x1 x location of first bridge unit in the line
     * @param y1 y location of first bridge unit in the line
     * @param x2 x location of the last bridge unit in the line
     * @param y2 y location of the last bridge unit in the line
     * @return xy location of the next bridge unit in the line
     */
    private int[] findNextXYPos(int x1, int y1, int x2, int y2){
        int[] nextXYPos = {};
        for(int i = 0; i < BridgeUnit.WIDTH; i++) {
            nextXYPos = getNextLinePoint(x1, y1, x2, y2);
            x1 = nextXYPos[0];
            y1 = nextXYPos[1];
        }
        return nextXYPos;
    }

    /**
     * Creates a joint between a bridge unit and the cliff
     * @param cliff
     * @param unit
     * @param x local x coordinate of where in the cliff the unit is attached to
     * @param y local y coordinate of where in the cliff the unit is attached to
     */

    public void makeUnitCliffJoint(BackgroundCliff cliff, BridgeUnit unit, float x, float y){
        BridgeJoint joint = makeJoint(cliff.getBody(), unit.getBody());
        joint.getrJointDef().localAnchorA.set(
                unit.getX() - x,
                unit.getY() - y);
        joint.getrJointDef().localAnchorB.set(x, y);
    }


    /**
     * takes 2 bodies and creates a joint between them.
     * @param unitBody
     * @param unitBody2
     * @return
     */

    private BridgeJoint makeJoint(Body unitBody, Body unitBody2){
        BridgeJoint joint = new BridgeJoint();
        joint.CreateJoint(unitBody, unitBody2);
        joint.getrJointDef().localAnchorA.set(
                unitBody2.getPosition().x - unitBody.getPosition().x,
                unitBody2.getPosition().y - unitBody.getPosition().y);

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


    public void setLeftCliff(BackgroundCliff leftCliff) {
        this.leftCliff = leftCliff;
    }

    public void setRightCliff(BackgroundCliff rightCliff) {
        this.rightCliff = rightCliff;
    }


}
