package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.game.GraphicalObjects.BridgeUnit;
import com.mygdx.game.GraphicalObjects.BridgeUnitLink;
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
    private final Texture img = new Texture("Pivot.png");
    private BridgeUnitLink lastBridgeUnitLink;
    private BridgeUnit lastBridgeUnit;
    private ArrayList<BridgeUnitLink> bridgeUnitLinks;
    private ArrayList<BridgeUnit> bridgeUnits;


    Material material;

    public BuildHandler(World world, Stage stage){
        this.world = world;
        this.stage = stage;
        material = new Material(1);
        lastBridgeUnit = null;
        bridgeUnitLinks = new ArrayList<BridgeUnitLink>();
        bridgeUnits = new ArrayList<BridgeUnit>();
    }

    public void makeBridgeUnitLink(float x, float y){
        BridgeUnitLink link = new BridgeUnitLink(img, world, x, y);
        link.setCreatedByPlayer(true);
        stage.addActor(link);
        //test();
        if(!bridgeUnitLinks.isEmpty()) {
         makeBridgeUnit(link, x, y);

        }

        lastBridgeUnitLink = link;
        bridgeUnitLinks.add(link);
    }

    private void makeBridgeUnit(BridgeUnitLink bridgeUnitLink, float x, float y) {

        HashMap<BridgeUnitLink, Float[]> linksToConnect = findLinksToConnect(x, y);
        System.out.println("linksToCOnnect " + linksToConnect);
        Set keySet  =  linksToConnect.keySet();
        ArrayList<BridgeUnit> localBridgeUnits = new ArrayList<BridgeUnit>();
        ArrayList<BridgeUnitLink> localBridgeUnitLinks = new ArrayList<BridgeUnitLink>();
        localBridgeUnitLinks.add(bridgeUnitLink);

        if (!keySet.isEmpty()){
            Iterator<BridgeUnitLink> it = keySet.iterator();
            while(it.hasNext()) {
                float unitX = x;
                float unitY = y;
                float[] t = {};
                BridgeUnitLink link = it.next();
                Float[] distanceVectorInfo = linksToConnect.get(link);
                System.out.println("link x = " + link.getBody().getPosition().x + " y = " + link.getBody().getPosition().y);
                System.out.println("x = " + x + " y = " + y);
                float xTemp = unitX;
                float yTemp = unitY;


                while (localBridgeUnits.size() < distanceVectorInfo[0]) {
                    //if (unitX + BridgeUnit.WIDTH >= link.getBody().getPosition().x) break;

                    for(int i = 0; i < BridgeUnit.WIDTH; i++) {
                        t = getNextLinePoint(xTemp, yTemp, (int) link.getBody().getPosition().x, (int) link.getBody().getPosition().y);
                        xTemp = t[0];
                        yTemp = t[1];

                    }
                    unitX = xTemp;
                    unitY = yTemp+2;
                    System.out.println(" coo " + unitX + ", " + unitY);
                    BridgeUnit bridgeUnit = new BridgeUnit(material, world, unitX, unitY);
                    bridgeUnit.getBody().setTransform(unitX, unitY, distanceVectorInfo[1]);
                    //bridgeUnit.getBody().setType(BodyDef.BodyType.StaticBody);
                    localBridgeUnits.add(bridgeUnit);
                    bridgeUnits.add(bridgeUnit);
                    stage.addActor(bridgeUnit);

                }


            }
            makeLinks(localBridgeUnits, localBridgeUnitLinks);




        }
    }




    private void makeLinks(ArrayList<BridgeUnit> localBrigeUnits, ArrayList<BridgeUnitLink> localBridgeUnitLinks ){

        for(int i = 0; i < localBrigeUnits.size()-1; i++){
            float x = (localBrigeUnits.get(i).getBody().getPosition().x + localBrigeUnits.get(i+1).getBody().getPosition().x)/2;
            float y = (localBrigeUnits.get(i).getBody().getPosition().y + localBrigeUnits.get(i+1).getBody().getPosition().y)/2;
            BridgeUnitLink link = new BridgeUnitLink(img, world, x, y);
            localBridgeUnitLinks.add(link);
            bridgeUnitLinks.add(link);
            stage.addActor(link);
        }
        makeJoints(localBrigeUnits, localBridgeUnitLinks);

    }

    private void makeJoints(ArrayList<BridgeUnit> localBridgeUnit, ArrayList<BridgeUnitLink> localBridgeUnitLink){
        for(int i = 0; i < localBridgeUnitLink.size() -1; i++){
            BridgeJoint joint = new BridgeJoint();
            BridgeJoint joint2 = new BridgeJoint();
            joint.CreateJoint(localBridgeUnit.get(i).getBody(), localBridgeUnitLink.get(i).getBody());
            joint.getrJointDef().localAnchorA.set(-BridgeUnit.WIDTH / 2, 0);
            world.createJoint(joint.getrJointDef());
            joint2.CreateJoint(localBridgeUnit.get(i + 1).getBody(), localBridgeUnitLink.get(i).getBody());
            joint2.getrJointDef().localAnchorA.set(BridgeUnit.WIDTH / 2, 0);
            world.createJoint(joint2.getrJointDef());

        }
    }
    private HashMap<BridgeUnitLink, Float[]> findLinksToConnect(float x, float y){
        HashMap<BridgeUnitLink, Float[]> linksToConnect = new HashMap<BridgeUnitLink, Float[]>();
        for(BridgeUnitLink link : bridgeUnitLinks){
           //int[] test =  getNextLinePoint((int)x, (int)y,(int) link.getBody().getPosition().x , (int) link.getBody().getPosition().y );
            //System.out.println("test next point:");
            if(link.isCreatedByPlayer()) {
                double distance = Math.sqrt((Math.pow(link.getBody().getPosition().x - x, 2) + (Math.pow(link.getBody().getPosition().y - y, 2))));
                if (distance <= 100) {
                    Float directionOfVector = (float) Math.atan((link.getBody().getPosition().y - y) / link.getBody().getPosition().x - x);
                    System.out.println("dir = " + directionOfVector);
                    Float numOfBridgeUnits = (float) distance / BridgeUnit.WIDTH;
                    Float[] distanceVector = {numOfBridgeUnits, directionOfVector};
                    linksToConnect.put(link, distanceVector);
                }
            }
        }



        return linksToConnect;
    }

    //code got from stack overflow "http://stackoverflow.com/questions/10825174/calculate-next-point-on-2d-linear-vector"
    public float[] getNextLinePoint(float x,float y,float x2, float y2) {
        float w = x2 - x;
        float h = y2 - y;
        float dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
        if (w<0) dx1 = -1; else if (w>0) dx1 = 1;
        if (h<0) dy1 = -1; else if (h>0) dy1 = 1;
        if (w<0) dx2 = -1; else if (w>0) dx2 = 1;
        float longest = Math.abs(w);
        float shortest = Math.abs(h);
        if (!(longest>shortest)) {
            longest = Math.abs(h);
            shortest = Math.abs(w);
            if (h<0) dy2 = -1; else if (h>0) dy2 = 1;
            dx2 = 0;
        }
        int numerator =  Math.round(longest) >> 1;
        numerator += shortest;
        if (!(numerator<longest)) {
            numerator -= longest;
            x += dx1;
            y += dy1;
        } else {
            x += dx2;
            y += dy2;
        }
        float[] res = {x, y};
        return res;
    }

    private void test(){
        float x1 = 1.5f;
        float y1 = 2.2f;
        float x2 = 5.5f;
        float y2 = 10.2f;
        float[] t = {};
        float xTemp = x1;
        float yTemp = y1;
        ArrayList<Integer> testList = new ArrayList<Integer>();
        int size = 3;

        //t = getNextLinePoint(xTemp, yTemp, (int) x2, (int) y2);
        //System.out.println(" coo " + t[0] + ", " + t[1]);
        while(testList.size() < size) {
            if(x1<x2) if(x1 + 3 >= x2)break;


           for(int i = 0; i < 2; i++) {

                t = getNextLinePoint(xTemp, yTemp, (int) x2, (int) y2);
                xTemp = t[0];
                yTemp = t[1];

                System.out.println("xtemp" + xTemp);
                System.out.println("ytemp" + yTemp);
               // if(xTemp >= x2) break;
            }
            x1 = xTemp;
            y1 = yTemp;
            System.out.println(" coo " + x1 + ", " + y1);
            testList.add(1);
        }
        System.out.println("it gets here");

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

    public void setBridgeUnits(ArrayList<BridgeUnit> bridgeUnits) {
        this.bridgeUnits = bridgeUnits;
    }

    public ArrayList<BridgeUnitLink> getBridgeUnitLinks() {
        return bridgeUnitLinks;
    }

    public void setBridgeUnitLinks(ArrayList<BridgeUnitLink> bridgeUnitLinks) {
        this.bridgeUnitLinks = bridgeUnitLinks;
    }
}
