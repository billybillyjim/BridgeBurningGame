package com.mygdx.game.GraphicalObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.ArrayList;

/**
 * Created by Eloa on 24/03/2016.
 */
public class Cable extends Actor {

    private final int widthCableSegment = 5;
    private final int heightCableSegment = 30;
    private int extraCableSegments;
    private final Texture cableTexture = new Texture("cableTex.jpg");

    private  Body[] cableSegments;
    private ArrayList<Sprite> spritesArray;
    private String leftAnchororientation; //leftAnchorOrientation
    private String rightAnchororientation; //leftAnchorOrientation



    //add xinitialloc and xfinalloc as paramaters
    public Cable(World world, BridgeUnit cableSupportLeft, BridgeUnit cableSupportRight, String leftAnchororientation, String rightAnchororientation, int extraSegments){

        float x1 = cableSupportLeft.getBody().getPosition().x;
        float y1 = cableSupportLeft.getBody().getPosition().y;
        float x2 = cableSupportRight.getBody().getPosition().x;
        float y2 = cableSupportRight.getBody().getPosition().y;
        System.out.println("x1 = " + x1 + " x2 = " + x2 + " y1 = " + y1 + " y2 = " + y2);

        spritesArray = new ArrayList<Sprite>();
        this.leftAnchororientation = leftAnchororientation;
        this.rightAnchororientation = rightAnchororientation;
        this.extraCableSegments = extraSegments;
        float numberOfRopeUnits = ((float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)))/heightCableSegment;
        numberOfRopeUnits += extraCableSegments;
        cableSegments = new Body[(int)numberOfRopeUnits];
        RevoluteJoint[] joints = new RevoluteJoint[(int) numberOfRopeUnits - 1];
        Body leftUnitBody = cableSupportLeft.getBody();
        Body rightUnitBody = cableSupportRight.getBody();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(widthCableSegment / 2, heightCableSegment / 2);

        for(int i = 0; i < cableSegments.length; i++){
            Sprite sprite = new Sprite(cableTexture);
            sprite.setSize(widthCableSegment, heightCableSegment);

            spritesArray.add(sprite);
            bodyDef.position.set((x1 + BridgeUnit.HEIGHT), y1 + BridgeUnit.WIDTH * 2 ) ;
            // if(i == ropeSegments.length - 1) bodyDef.position.set((rightPillarUnit.getSprite().getX() - BridgeUnit.HEIGHT/2), leftPillarUnit.getSprite().getY() + BridgeUnit.WIDTH * 2) ;
            cableSegments[i] = world.createBody(bodyDef);
            cableSegments[i].createFixture(shape, .2f);
        }

        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.localAnchorA.y = -heightCableSegment / 2;
        jointDef.localAnchorB.y = heightCableSegment / 2;

        for(int i = 0; i < joints.length; i++){
            jointDef.bodyA = cableSegments[i];
            jointDef.bodyB = cableSegments[i + 1];
            joints[i] = (RevoluteJoint) world.createJoint(jointDef);
        }



        jointDef.bodyA = leftUnitBody;
        if(leftAnchororientation.equals("upperLeft")){
            jointDef.localAnchorA.set(-BridgeUnit.WIDTH / 2, BridgeUnit.HEIGHT/2);
        }
        else if (leftAnchororientation.equals("lowerRight")){
            jointDef.localAnchorA.set(BridgeUnit.WIDTH / 2, -BridgeUnit.HEIGHT/2);

        }


        jointDef.bodyB = cableSegments[0];
        world.createJoint(jointDef);


        jointDef.bodyA = rightUnitBody;

        if(rightAnchororientation.equals("upperRight")){
            jointDef.localAnchorA.set(BridgeUnit.WIDTH/2, BridgeUnit.HEIGHT/2);
        }


        jointDef.localAnchorB.y = -heightCableSegment/2;
        jointDef.bodyB = cableSegments[cableSegments.length-1];
        world.createJoint(jointDef);

    }

    @Override
    public void draw(Batch batch, float ParentAlpha){
        for(int i = 0; i < cableSegments.length; i++){
            Body body = cableSegments[i];
            Sprite sprite = spritesArray.get(i);

            sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
            sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2); //sets position of sprite to the same as the body
            sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees); //set rotation of the sprite to the same as the body
            setBounds(sprite.getX(), sprite.getY(), getWidth(), getHeight());
            sprite.draw(batch);
        }

    }


}
