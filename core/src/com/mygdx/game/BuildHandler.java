package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.game.GraphicalObjects.BridgeUnit;
import com.mygdx.game.GraphicalObjects.BridgeUnitLink;
import com.mygdx.game.PhysicalObjects.BridgeJoint;

/**
 * Created by Luke on 4/22/2016.
 */
public class BuildHandler {

    private World world;
    private Stage stage;
    private final Texture img = new Texture("Pivot.png");
    private BridgeUnitLink lastBridgeUnitLink;
    private BridgeUnit lastBridgeUnit;

    Material material;

    public BuildHandler(World world, Stage stage){
        this.world = world;
        this.stage = stage;
        material = new Material(1);
    }

    public void makeBridgeUnitLink(float x, float y){
        BridgeUnitLink link = new BridgeUnitLink(img, world, x, y);
        stage.addActor(link);
        makeBridgeUnit(link, x, y);
        lastBridgeUnitLink = link;
    }

    public void makeBridgeUnit(BridgeUnitLink bridgeUnitLink, float x, float y){
        //TODO:Make this work
        if(bridgeUnitLink != null && lastBridgeUnitLink != null){
            double distance = Math.sqrt(((lastBridgeUnitLink.getX() - x)*(lastBridgeUnitLink.getX() - x)) + ((lastBridgeUnitLink.getY() - y)*(lastBridgeUnitLink.getY() - y)));
            int numOfUnits = (int)distance / BridgeUnit.WIDTH;

            for(int i = 0; i < numOfUnits; i++){
                float newX = x + lastBridgeUnitLink.getX() * (i / (float)numOfUnits);
                float newY = y + lastBridgeUnitLink.getY() * (i / (float)numOfUnits);

               // float newX = lastBridgeUnitLink.getX() + BridgeUnit.WIDTH * i;
                //float newY = lastBridgeUnitLink.getY() + BridgeUnit.HEIGHT * i;
                BridgeUnit bridgeUnit = new BridgeUnit(material, world, newX, newY);
                stage.addActor(bridgeUnit);

                BridgeJoint joint = new BridgeJoint();
                BridgeJoint joint2 = new BridgeJoint();
                if(lastBridgeUnit != null){
                    joint.CreateJoint(bridgeUnit.getBody(),bridgeUnitLink.getBody());
                    joint.getrJointDef().localAnchorA.set(BridgeUnit.WIDTH / 2, 0);
                    world.createJoint(joint.getrJointDef());
                    System.out.println(numOfUnits);
                    BridgeUnitLink bUnitLink = new BridgeUnitLink(img,world,bridgeUnit.getBody().getPosition().x - (BridgeUnit.WIDTH),bridgeUnit.getBody().getPosition().y);
                    stage.addActor(bUnitLink);
                    joint2.CreateJoint(bUnitLink.getBody(), bridgeUnit.getBody());
                    joint2.getrJointDef().localAnchorA.set(-BridgeUnit.WIDTH / 2, 0);
                    world.createJoint(joint2.getrJointDef());
                }

                lastBridgeUnit = bridgeUnit;
            }
        }

    }


}
