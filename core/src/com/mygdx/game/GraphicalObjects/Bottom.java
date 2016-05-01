package com.mygdx.game.GraphicalObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by lwels on 4/30/16.
 */
public class Bottom extends Actor{

    private Body body;
    private ContactListener cListener;

    public Bottom(World world){


        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(164, -15);
        body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        body.createFixture(fixtureDef);

    }



}
