package com.mygdx.game.GraphicalObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Eloise on 2/28/16.
 */

public class BackgroundCliffs extends Actor {

    private Body bodyLeft;
    private Body bodyRight;
    private Texture img1;
    private Texture img2;
    private Sprite spriteLeft;
    private Sprite spriteRight;
    private final int cliffWidth = 164;
    private final int cliffHeight = 172;


    public BackgroundCliffs(Texture texture1, Texture texture2, World world) {
        // Sets image to one in Texture folder
        img1 = texture1;
        img2 = texture2;

        this.setName("Cliffs");


        //Creates two Sprites with this image
        spriteLeft = new Sprite(img1, cliffWidth, cliffHeight);
        spriteRight = new Sprite(img2, cliffWidth, cliffHeight);


        //Places the cliffs on either end of the screen
        spriteLeft.setPosition(0, 0);
        spriteRight.setPosition(800 - spriteRight.getWidth(), 0);


        //Defines the body to be able to have physics applied to it
        BodyDef bodyDef1 = new BodyDef();
        bodyDef1.type = BodyDef.BodyType.StaticBody;
        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.type = BodyDef.BodyType.StaticBody;

        //puts the body in a specific spot over the sprite
        bodyDef1.position.set(spriteLeft.getX(), spriteLeft.getY());
        bodyDef2.position.set(spriteRight.getX() + spriteRight.getWidth(), spriteRight.getY());


        bodyLeft = world.createBody(bodyDef1);
        bodyRight = world.createBody(bodyDef2);


        //Makes a shape for the body, Sets the shape to a box
        PolygonShape shape1 = new PolygonShape();
        shape1.setAsBox(spriteLeft.getWidth(), spriteLeft.getHeight());
        PolygonShape shape2 = new PolygonShape();
        shape2.setAsBox(spriteRight.getWidth(), spriteRight.getHeight());

        //Describes the properties of the fixture
        FixtureDef fixtureDef1 = new FixtureDef();
        fixtureDef1.shape = shape1;
        fixtureDef1.density = 1f;

        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = shape2;
        fixtureDef2.density = 1f;


        Fixture fixture1 = bodyLeft.createFixture(fixtureDef1);
        Fixture fixture2 = bodyRight.createFixture(fixtureDef2);

        shape1.dispose();
        shape2.dispose();


    }

    @Override
    public void draw(Batch batch, float ParentAlpha){
        batch.draw(spriteLeft, getX(), getY());
        batch.draw(spriteRight, getX(), getY());
    }

    public Body getBodyLeft(){return this.bodyLeft;}
    public Body getBodyRight(){return this.bodyRight;}

    public Sprite getSpriteLeft() {
        return spriteLeft;
    }

    public Sprite getSpriteRight() {
        return spriteRight;
    }
}
