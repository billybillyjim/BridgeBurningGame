package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GraphicalObjects.Bridge;
import com.mygdx.game.GraphicalObjects.BridgeUnit;
import com.mygdx.game.GraphicalObjects.BridgeUnitLink;
import com.mygdx.game.PhysicalObjects.BridgeJoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luke on 3/21/2016.
 */
public class FireHandler {

    ArrayList<BridgeUnit> bridgeUnits;
    ArrayList<BridgeUnitLink> bridgeUnitLinks;
    ArrayList<BridgeJoint> bridgeJoints;


    public FireHandler(){
        this.bridgeUnits = new ArrayList<BridgeUnit>();
        this.bridgeUnitLinks = new ArrayList<BridgeUnitLink>();
        this.bridgeJoints = new ArrayList<BridgeJoint>();
    }

    public FireHandler(ArrayList<BridgeUnit> bridgeUnits, ArrayList<BridgeUnitLink> bridgeUnitLinks){

        this.bridgeUnits = bridgeUnits;
        this.bridgeUnitLinks = bridgeUnitLinks;
        this.bridgeJoints = new ArrayList<BridgeJoint>();

    }

    public FireHandler(ArrayList<BridgeUnit> bridgeUnits, ArrayList<BridgeUnitLink> bridgeUnitLinks, ArrayList<BridgeJoint> bridgeJoints){

        this.bridgeUnits = bridgeUnits;
        this.bridgeUnitLinks = bridgeUnitLinks;
        this.bridgeJoints = bridgeJoints;

    }
    //Checks to see if any bridgeUnit or bridgeUnitLink is on fire
    public boolean checkFires(){
        for(int i = 0; i < bridgeUnits.size(); i++){
            if(bridgeUnits.get(i).getIsOnFire()){
                return true;
            }
        }
        for(int i = 0; i < bridgeUnitLinks.size(); i++){
            if(bridgeUnitLinks.get(i).getIsOnFire()){
                return true;
            }
        }
        return false;
    }

    //Burns all BridgeUnitLinks adjacent to BridgeUnits. Currently it has to iterate through all the Bridge Units, check if they are on fire,
    //get their JointEdges and compare each BridgeUnitLink to each JointEdge. I'm not sure if there is a less wasteful method but it doesn't
    //seem to matter to the performance at least on the desktop version
    public boolean burnAdjacents(){
        //Iterates through the ArrayList of BridgeUnits
        for(int i = 0; i < bridgeUnits.size(); i++){
            //Checks to see if they are on fire
            if(bridgeUnits.get(i).getIsOnFire()){
                //Gets the list of JointEdges
                Array<JointEdge> jointEdges = bridgeUnits.get(i).getBody().getJointList();

                //Runs through the list of bridgeUnit links
                for(BridgeUnitLink bridgeUnitLink : bridgeUnitLinks){
                    //Runs through the list of Joint edges
                    for(JointEdge jointEdge : jointEdges){
                        //Checks to see if any of them match
                        if(jointEdge.other.equals(bridgeUnitLink.getBody())){
                            //Sets the bridgeUnitLink on fire if they match

                            bridgeUnitLink.setIsOnFire(true);
                        }
                    }
                }
                jointEdges.clear();
            }
        }
        /*for(int i = 0; i < bridgeUnitLinks.size(); i++){
            if(bridgeUnitLinks.get(i).getIsOnFire()){
                Array<JointEdge> jointEdges = bridgeUnits.get(i).getBody().getJointList();
                for(JointEdge e : jointEdges){
                    System.out.println(e + " " + i);
                }
                //Runs through the list of bridgeUnit links
                for(BridgeUnit bridgeUnit : bridgeUnits){
                    //Runs through the list of Joint edges
                    for(JointEdge jointEdge : jointEdges){
                        //Checks to see if any of them match
                        if(jointEdge.other.equals(bridgeUnit.getBody())){
                            //Sets the bridgeUnitLink on fire if they match
                            bridgeUnit.setIsOnFire(true);
                        }
                    }
                }
                jointEdges.clear();
            }
        }
        */

        return false;
    }
    public void updateBridgeUnitArray(ArrayList<BridgeUnit> bridgeUnits){
        this.bridgeUnits = bridgeUnits;
    }

    public void addBridgeUnit(BridgeUnit unit){
        this.bridgeUnits.add(unit);
    }

    public void addBridgeUnitLink(BridgeUnitLink link){
        this.bridgeUnitLinks.add(link);
    }

    public void addBridgeJoint(BridgeJoint joint){
        this.bridgeJoints.add(joint);
    }
}
