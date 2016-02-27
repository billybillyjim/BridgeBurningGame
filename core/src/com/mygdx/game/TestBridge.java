package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by Luke on 2/22/2016.
 */
public class TestBridge {

    Body body;
    Texture img;
    Sprite sprite;

    public void CreateTestBridge(Texture texture, World world){

        //Sets texture to image in assets folder
        img = texture;
        //Makes a sprite of that texture
        sprite = new Sprite(img);

        //sets the sprite position based on screen size
        sprite.setPosition(Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);

        //Makes a physics body
        BodyDef bodyDef = new BodyDef();
        //Defines the body to be able to have physics applied to it
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        //puts the body in a specific spot over the sprite
        bodyDef.position.set(sprite.getX(), sprite.getY());


        body = world.createBody(bodyDef);

        //Makes a shape for the body
        PolygonShape shape = new PolygonShape();
        //Sets the shape to a box
        shape.setAsBox(sprite.getWidth()/2, sprite.getHeight()/2);

        //Describes the properties of the fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = .1f;

        Fixture fixture = body.createFixture(fixtureDef);


        shape.dispose();
    }

    public Body getBody(){
        return this.body;
    }

}