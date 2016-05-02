package com.mygdx.game.GraphicalObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Eloise on 4/30/16.
 */
public class BackgroundCliff extends Actor{
    private Body body;
    private Texture texture;
    private Sprite sprite;

    public BackgroundCliff(Texture tex, float posX, float posY, World world, boolean left){
        texture = tex;
        sprite = new Sprite(texture);

        this.setName("Cliff");
        this.setPosition(posX,posY);
        this.setWidth(sprite.getWidth());
        this.setHeight(sprite.getHeight());

        //Defines the body to be able to have physics applied to it
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        //puts the body in a specific spot over the sprite
        if(left){
            bodyDef.position.set(getX(), getY());
        }
        else{
            bodyDef.position.set(getX()+sprite.getWidth(), getY());
        }


        body = world.createBody(bodyDef);


        //Makes a shape for the body, Sets the shape to a box
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth(), sprite.getHeight());

        //Describes the properties of the fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        Fixture fixture1 = body.createFixture(fixtureDef);

        shape.dispose();

    }
    @Override
    public void draw(Batch batch, float ParentAlpha){
        batch.draw(sprite, getX(), getY());
    }

    public Body getBody() {
        return body;
    }
}
