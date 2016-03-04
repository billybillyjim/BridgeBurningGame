package com.mygdx.game.PhysicalObjects;



import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

/**
 * Created by Luke on 2/26/2016.
 */
public class BridgeJoint {

    RevoluteJointDef rJointDef;

    public void CreateJoint(Body bridgeBody, Body vertexBody){

        rJointDef = new RevoluteJointDef();

        rJointDef.bodyA = vertexBody;
        rJointDef.bodyB = bridgeBody;

        rJointDef.collideConnected = false;

        rJointDef.localAnchorA.set(0,0);
        rJointDef.localAnchorB.set(27,20);


    }

    public RevoluteJointDef getrJointDef() {
        return rJointDef;
    }
}
