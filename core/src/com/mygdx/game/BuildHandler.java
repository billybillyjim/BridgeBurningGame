package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.physics.box2d.Body;
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
    private ArrayList<BridgeUnitLink> bridgeUnitLinks;
    private ArrayList<BridgeUnit> bridgeUnits;
    private ParticleEffect fireEffect;


    Material material;

    public BuildHandler(World world, Stage stage, ParticleEffect fireEffect){
        this.world = world;
        this.stage = stage;
        this.fireEffect = fireEffect;
        material = new Material(1);

        bridgeUnitLinks = new ArrayList<BridgeUnitLink>();
        bridgeUnits = new ArrayList<BridgeUnit>();
    }

    public BridgeUnitLink makeBridgeUnitLink(float x, float y){
        BridgeUnitLink link = new BridgeUnitLink(img, world, x, y);
        link.setCreatedByPlayer(true);
        link.getBody().setType(BodyDef.BodyType.DynamicBody);
        stage.addActor(link);
        makeUnitLinkStructure(link);
        bridgeUnitLinks.add(link);
        return link;
    }

   private void makeUnitLinkStructure(BridgeUnitLink bridgeUnitLink) {

        HashMap<BridgeUnitLink, Integer> linksToConnect = findLinksToConnect(bridgeUnitLink);
        Set keySet  =  linksToConnect.keySet();


        if (!keySet.isEmpty()){
            Iterator<BridgeUnitLink> it = keySet.iterator();
            while(it.hasNext()) {
                BridgeUnitLink link = it.next();
                addLinks(bridgeUnitLink, link, linksToConnect.get(link));
                }


            }

    }

    private HashMap<BridgeUnitLink, Integer> findLinksToConnect(BridgeUnitLink link){
        float x = link.getBody().getPosition().x;
        float y = link.getBody().getPosition().y;
        HashMap<BridgeUnitLink, Integer> linksToConnect = new HashMap<BridgeUnitLink, Integer>();
        for(BridgeUnitLink link2 : bridgeUnitLinks){
            if(link2.isCreatedByPlayer()) {
                double distance = Math.sqrt((Math.pow(link2.getBody().getPosition().x - x, 2) + (Math.pow(link2.getBody().getPosition().y - y, 2))));
                if (distance <= 100) {

                    int numOfBridgeUnits = (int) distance / BridgeUnit.WIDTH;
                    linksToConnect.put(link2, numOfBridgeUnits);
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


    private void addLinks(BridgeUnitLink link1, BridgeUnitLink link2, int numOfLinks){
        ArrayList<BridgeUnitLink> links = new ArrayList<BridgeUnitLink>();
        links.add(link1);
        int x1 = (int) link1.getBody().getPosition().x;
        int y1 = (int) link1.getBody().getPosition().y;
        int x2 = (int) link2.getBody().getPosition().x;
        int y2 = (int) link2.getBody().getPosition().y;


        for(int i = 0; i < numOfLinks; i++){
            int[] pos = findNextXYPos(x1, y1, x2, y2);
            x1 = pos[0];
            y1 = pos[1];
            BridgeUnitLink link = new BridgeUnitLink(img, world, x1, y1);
            link.getBody().setType(BodyDef.BodyType.DynamicBody);
            links.add(link);
            bridgeUnitLinks.add(link);

        }
        links.add(link2);
        addBridgeUnits(links);
    }

    private void addBridgeUnits(ArrayList<BridgeUnitLink> links){
        for(int i = 0; i < links.size()-1; i++){
            addBridgeUnit(links.get(i), links.get(i+1));
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

    /**
     * Must be correct distance apart
     * @param link1
     * @param link2
     */

    private void addBridgeUnit(BridgeUnitLink link1, BridgeUnitLink link2){
        float x1 = link1.getBody().getPosition().x;
        float y1 = link1.getBody().getPosition().y;
        float x2 = link2.getBody().getPosition().x;
        float y2 = link2.getBody().getPosition().y;
        BridgeUnit newBUnit = new BridgeUnit(material, world, (x1+x2)/2, (y1+y2)/2, fireEffect);
        stage.addActor(newBUnit);
        bridgeUnits.add(newBUnit);
        makeJoint(newBUnit.getBody(), link1.getBody());
        makeJoint(newBUnit.getBody(), link2.getBody());

    }

    private void makeJoint(Body unitBody, Body linkBody){
        BridgeJoint joint = new BridgeJoint();
        joint.CreateJoint(unitBody, linkBody);
        joint.getrJointDef().localAnchorA.set(
                linkBody.getPosition().x - unitBody.getPosition().x,
                linkBody.getPosition().y - unitBody.getPosition().y);
        world.createJoint(joint.getrJointDef());
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


    public ArrayList<BridgeUnitLink> getBridgeUnitLinks() {
        return bridgeUnitLinks;
    }


}
