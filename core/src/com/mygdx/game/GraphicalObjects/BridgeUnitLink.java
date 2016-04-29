package com.mygdx.game.GraphicalObjects;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;


/**
 * Created by Luke on 2/22/2016.
 */
public class BridgeUnitLink extends Actor {


    Body body;
    Texture img;
    Sprite sprite;
    boolean isOnFire;
    public static final float RADIUS = 4f;
    boolean isBurnt;
    boolean createdByPlayer;

    public BridgeUnitLink(Texture texture, World world, float xPosition, float yPosition){
        this.setName("Bridge Unit Link");
        createdByPlayer = false;


        //Sets texture to image in assets folder
        img = texture;
        //Makes a sprite of that texture
        sprite = new Sprite(img);

        //sets the sprite position based on screen size
        sprite.setPosition(xPosition, yPosition);

        //Makes a physics body
        BodyDef bodyDef = new BodyDef();
        //Defines the body to be able to have physics applied to it
        bodyDef.type = BodyDef.BodyType.StaticBody;


        //puts the body in a specific spot over the sprite
        bodyDef.position.set(sprite.getX(), sprite.getY());


        body = world.createBody(bodyDef);




        //Makes a shape for the body
        CircleShape shape = new CircleShape();
        shape.setRadius(RADIUS);
        //Sets the shape to a box





        //Describes the properties of the fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;


        body.createFixture(fixtureDef);

        sprite.setSize(RADIUS * 2, RADIUS * 2); //set sprite size to the same size of the body
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);  //set the origin over which the sprites rotates to the center of the sprite
        //body.setUserData(sprite); //adds sprite to the user data (creates an association between the sprite and the body)


        shape.dispose();
    }

    @Override
    public void draw(Batch batch, float ParentAlpha){
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2); //sets position of sprite to the same as the body
        sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees); //set rotation of the sprite to the same as the body
        setBounds(sprite.getX(), sprite.getY(), getWidth(), getHeight());
        sprite.draw(batch);
    }

    public void changeBodyType(){
        body.setType(BodyDef.BodyType.DynamicBody);
    }


    public Body getBody(){
        return this.body;
    }
    public Sprite getSprite(){
        return this.sprite;
    }
    public boolean getIsOnFire(){return this.isOnFire;}
    public boolean getIsBurnt(){return this.isBurnt;}
    public void setIsOnFire(boolean isOnFire){
        this.isOnFire = isOnFire;
    }
    public void setIsBurnt(boolean b){this.isBurnt = b;}

    public boolean isCreatedByPlayer() {
        return createdByPlayer;
    }

    public void setCreatedByPlayer(boolean createdByPlayer) {
        this.createdByPlayer = createdByPlayer;
    }
}


