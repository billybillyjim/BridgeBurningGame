package com.mygdx.game.PhysicalObjects;



import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

/**
 * Created by Luke on 2/26/2016.
 */
public class BridgeJoint {

    Body bridgeBody;
    Body vertexBody;
    WeldJointDef rJointDef;

    public void CreateJoint(Body bridgeBody, Body vertexBody){

        rJointDef = new WeldJointDef();

        this.bridgeBody = bridgeBody;
        this.vertexBody = vertexBody;

        rJointDef.bodyA = this.bridgeBody;
        rJointDef.bodyB = this.vertexBody;

        rJointDef.collideConnected = false;

        rJointDef.localAnchorA.set(0,0);
        rJointDef.localAnchorB.set(0,0);

    }

    public WeldJointDef getrJointDef() {
        return rJointDef;
    }

}
