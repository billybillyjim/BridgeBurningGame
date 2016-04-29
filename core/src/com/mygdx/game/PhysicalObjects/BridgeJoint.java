package com.mygdx.game.PhysicalObjects;



import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

/**
 * Created by Luke on 2/26/2016.
 */
public class BridgeJoint {

    Body bridgeBody;
    Body vertexBody;
    RevoluteJointDef rJointDef;

    public void CreateJoint(Body bridgeBody, Body vertexBody){

        rJointDef = new RevoluteJointDef();

        this.bridgeBody = bridgeBody;
        this.vertexBody = vertexBody;

        rJointDef.bodyA = this.bridgeBody;
        rJointDef.bodyB = this.vertexBody;

        rJointDef.collideConnected = false;

        rJointDef.localAnchorA.set(0,0);
        rJointDef.localAnchorB.set(0,0);


        rJointDef.lowerAngle = 0f;
        rJointDef.upperAngle = 0f;
        rJointDef.enableLimit = true;
        rJointDef.enableMotor = false;


    }

    public RevoluteJointDef getrJointDef() {
        return rJointDef;
    }

    public Body getBridgeBody(){return this.bridgeBody;}
    public Body getVertexBody(){return this.vertexBody;}
}
