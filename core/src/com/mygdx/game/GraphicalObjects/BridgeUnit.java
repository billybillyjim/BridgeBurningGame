package com.mygdx.game.GraphicalObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.Material;

import java.util.Random;

/**
 * Created by Luke on 2/22/2016.
 */
public class BridgeUnit extends Actor{

    private static Random random = new Random();

    private Body body;
    private Texture img;
    private Sprite sprite;

    private ParticleEffect fireEffect;

    public final static int WIDTH = 10;
    public final static int HEIGHT = 15;

    private int durability;

    private boolean isOnFire;
    private boolean isBurnt;

    public BridgeUnit(Material material, World world, float xPosition, float yPosition, ParticleEffect fireEffect){

        //Sets texture to image in assets folder
        img = new Texture(material.getImage_src());
        //Makes a sprite of that texture
        sprite = new Sprite(img);


        this.setName("Bridge Unit");

        this.durability = material.getDurability();
        this.fireEffect = fireEffect;

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
        fixtureDef.density = material.getDensity();
        fixtureDef.friction = material.getFriction(); //0 = like ice, 1 = cannot slide over it at all

        setWidth(sprite.getWidth());
        setHeight(sprite.getHeight());
        setBounds(0, 0, getWidth(), getHeight());

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

        if(isBurnt){
            isOnFire = false;

            body.applyAngularImpulse((float) Math.random(), true);
        }

        if(isOnFire && random.nextFloat() < 0.3) {
            for(int j = 0; j < fireEffect.getEmitters().size; j++) {
                ParticleEmitter emitter = fireEffect.getEmitters().get(j);
                emitter.setPosition(body.getPosition().x, body.getPosition().y);
                emitter.addParticle();
            }
        }
    }

    public void decrementDurability(){
        this.durability--;
    }

    public Body getBody(){
        return this.body;
    }
    public Sprite getSprite(){
        return  this.sprite;
    }
    public int getDurability(){return this.durability;}

    public boolean getIsOnFire(){return this.isOnFire;}
    public boolean getIsBurnt(){return this.isBurnt;}

    public void changeTexture(Texture newTexture){
        sprite.set(new Sprite(newTexture));
    }

    public void setIsOnFire(boolean b){this.isOnFire = b;}
    public void setIsBurnt(boolean b){this.isBurnt = b;}

}
