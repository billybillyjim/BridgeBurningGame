package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
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
    Array<JointEdge> bridgeJointEdges;
    Array<Actor> bridgePieces;

    public FireHandler(){
        this.bridgeUnits = new ArrayList<BridgeUnit>();
        this.bridgeUnitLinks = new ArrayList<BridgeUnitLink>();
        this.bridgeJoints = new ArrayList<BridgeJoint>();
    }

    public FireHandler(ArrayList<BridgeUnit> bridgeUnits, ArrayList<BridgeUnitLink> bridgeUnitLinks, ArrayList<BridgeJoint> bridgeJoints){

        this.bridgeUnits = bridgeUnits;
        this.bridgeUnitLinks = bridgeUnitLinks;
        this.bridgeJoints = bridgeJoints;

    }

    public FireHandler(Array<JointEdge> jointEdges){
        this.bridgeJointEdges = jointEdges;
    }

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

    public boolean burn(){
        for(int i = 0; i < bridgeUnits.size(); i++){
            System.out.println(bridgeUnits.get(i).getX());
        }


        return true;
    }

    public boolean burnAdjacent(JointEdge j){
        for(int i = 0; i < bridgeUnits.size(); i++){
            if(j.other.equals(bridgeUnits.get(i).getBody())){
                System.out.println("ADJACENT BURNED");
            }
        }

        return false;
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
