package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GraphicalObjects.BridgeUnit;
import com.mygdx.game.PhysicalObjects.BridgeJoint;
import java.util.ArrayList;


/**
 * Created by Luke on 3/21/2016.
 */
public class FireHandler {

    ArrayList<BridgeUnit> bridgeUnits;
    ArrayList<BridgeJoint> bridgeJoints;

    public FireHandler(){
        this.bridgeUnits = new ArrayList<BridgeUnit>();
        this.bridgeJoints = new ArrayList<BridgeJoint>();
    }

    public FireHandler(ArrayList<BridgeUnit> bridgeUnits){
        this.bridgeUnits = bridgeUnits;
        this.bridgeJoints = new ArrayList<BridgeJoint>();
    }

    //Checks to see if any bridgeUnit or bridgeUnitLink is on fire
    public boolean checkFires(){
        for(int i = 0; i < bridgeUnits.size(); i++){
            if(bridgeUnits.get(i).getIsOnFire()){
                return true;
            }
        }

        return false;
    }

    /**Burns all BridgeUnits adjacent to other BridgeUnits. It iterates through all the BridgeUnits, 
     * checks if they are on fire, gets their JointEdges and compares each BridgeUnit to each JointEdge. 
     * It doesn't seem to matter to the performance at least on the desktop version
    **/
    public boolean burnAdjacents(){
        //Iterates through the ArrayList of BridgeUnits
        for(int i = 0; i < bridgeUnits.size(); i++){
            //Checks to see if they are on fire

            if(bridgeUnits.get(i).getIsOnFire()){
                bridgeUnits.get(i).decrementDurability();
                //Gets the list of JointEdges
                Array<JointEdge> jointEdges = bridgeUnits.get(i).getBody().getJointList();
                //Runs through the list of bridgeUnit links
                for(BridgeUnit bridgeUnit : bridgeUnits){
                    //Runs through the list of Joint edges
                    for(JointEdge jointEdge : jointEdges){
                        //Checks to see if any of them match
                        if(jointEdge.other.equals(bridgeUnit.getBody())){
                            //Sets the bridgeUnitLink on fire if they match
                            if(bridgeUnits.get(i).getDurability() < 7){
                                bridgeUnit.setIsOnFire(true);
                            }

                        }
                    }
                }
            }
        }

        return false;
    }
    /** This method takes an array of BridgeUnits from the main game
     * and checks to see if any of them are burnt. If they are, it
     * returns an ArrayList of the burnt units.
     **/
    public ArrayList<BridgeUnit> burnUpBridgeUnits( ArrayList<BridgeUnit> burntBridgeUnits){

        for(BridgeUnit b : bridgeUnits){
            if(b.getIsBurnt()){
                //System.out.println("b is burnt!");
                if(!burntBridgeUnits.contains(b)) {
                    burntBridgeUnits.add(b);
                }
            }
        }
        return burntBridgeUnits;
    }
    //Adds a bridgeUnit to the fireHandler's ArrayList of BridgeUnits.
    public void addBridgeUnit(BridgeUnit unit){
        this.bridgeUnits.add(unit);
    }
    //Adds a bridgeJoint to the fireHandler's ArrayList of BridgeJoints
    public void addBridgeJoint(BridgeJoint joint){
        this.bridgeJoints.add(joint);
    }

    public void setBridgeUnits(ArrayList<BridgeUnit> bridgeUnits) {
        this.bridgeUnits = bridgeUnits;
    }
}
