package com.mygdx.game.GraphicalObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MainGame;


/**
 * Created by Luke on 2/22/2016.
 */
public class BridgeUnit extends Actor{

    private Body body;
    private Texture img;
    private Sprite sprite;

    public final static int WIDTH = 100;
    public final static int HEIGHT = 20;

    private boolean isOnFire;

    public BridgeUnit(Texture texture, World world, float xPosition, float yPosition){

        //Sets texture to image in assets folder
        img = texture;
        //Makes a sprite of that texture
        sprite = new Sprite(img);

        this.setName("Bridge Unit");

        //sets the sprite position based on screen size
        sprite.setPosition(xPosition, yPosition);

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
        shape.setAsBox(WIDTH/2, HEIGHT/2); //it has to be divided by 2 because setAsBo method takes half width and half height as input for some reason.

        //Describes the properties of the fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 10f;
        fixtureDef.friction = 0.3f; //0 = like ice, 1 = cannot slide over it at all

        setWidth(sprite.getWidth());
        setHeight(sprite.getHeight());
        setBounds(0, 0, getWidth(), getHeight());

        /*setTouchable(Touchable.enabled);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isOnFire = true;

            }
        });
        */


        body.createFixture(fixtureDef);

        sprite.setSize(WIDTH, HEIGHT); //set sprite size to the same size of the body
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2); //set the origin over which the sprites rotates to the center of the sprite


        shape.dispose();



    }


    @Override
    public void draw(Batch batch, float ParentAlpha){
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2); //sets position of sprite to the same as the body
        sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees); //set rotation of the sprite to the same as the body
        setBounds(sprite.getX(), sprite.getY(), getWidth(), getHeight());
        sprite.draw(batch);
    }

    public Body getBody(){
        return this.body;
    }
    public Sprite getSprite(){
        return  this.sprite;
    }

    public boolean getIsOnFire(){return this.isOnFire;}

    public void setIsOnFire(boolean b){this.isOnFire = b;}

}
