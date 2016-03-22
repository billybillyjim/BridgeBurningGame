package com.mygdx.game;

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

    public FireHandler(ArrayList<BridgeUnit> bridgeUnits, ArrayList<BridgeUnitLink> bridgeUnitLinks, ArrayList<BridgeJoint> bridgeJoints){

        this.bridgeUnits = bridgeUnits;
        this.bridgeUnitLinks = bridgeUnitLinks;
        this.bridgeJoints = bridgeJoints;

    }
    public boolean checkFires(){
        for(int i = 0; i < bridgeUnits.size(); i++){
            if(bridgeUnits.get(i).getOnFire()){
                return true;
            }
        }
        for(int i = 0; i < bridgeUnitLinks.size(); i++){
            if(bridgeUnitLinks.get(i).getOnFire()){
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

    public boolean burnAdjacent(BridgeUnit unit, BridgeJoint joint){

        if(joint.getBridgeBody().equals(unit.getBody())){
            return true;
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
