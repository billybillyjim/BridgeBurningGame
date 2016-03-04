package com.mygdx.game.GraphicalObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

/**
 * Created by Luke on 2/22/2016.
 */
public class BridgeUnitLink {

    Body body;
    Texture img;
    Sprite sprite;

    public void CreateVertex(Texture texture, World world, float xPosition, float yPosition){


        //Sets texture to image in assets folder
        img = texture;
        //Makes a sprite of that texture
        sprite = new Sprite(img);

        //sets the sprite position based on screen size
        sprite.setPosition(xPosition,
                yPosition);

        //Makes a physics body
        BodyDef bodyDef = new BodyDef();
        //Defines the body to be able to have physics applied to it
        bodyDef.type = BodyDef.BodyType.StaticBody;


        //puts the body in a specific spot over the sprite
        bodyDef.position.set(sprite.getX(), sprite.getY());


        body = world.createBody(bodyDef);



        //Makes a shape for the body
        CircleShape shape = new CircleShape();
        shape.setRadius(10);
        //Sets the shape to a box



        //Describes the properties of the fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;


        body.createFixture(fixtureDef);

        sprite.setSize(10 * 2, 10 * 2); //set sprite size to the same size of the body
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);  //set the origin over which the sprites rotates to the center of the sprite
        body.setUserData(sprite); //adds sprite to the user data (creates an association between the sprite and the body)


        shape.dispose();
    }

    public Body getBody(){
        return this.body;
    }
    public Sprite getSprite(){
        return this.sprite;
    }

}


