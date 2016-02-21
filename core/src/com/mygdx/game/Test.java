package com.mygdx.game;

/**
 * Created by Luke on 2/12/2016.
 */

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;


public class Test implements ApplicationListener{
    private SpriteBatch batch;
    private BitmapFont font;
    private ParticleEffect fireEffect;
    Sprite sprite;
    Texture img;
    World world;
    Body body;
    private float bodyLocation;

    @Override
    public void create(){
        //Makes a set of sprites
        batch = new SpriteBatch();
        //Makes a font
        font = new BitmapFont();
        //Sets a font color
        font.setColor(Color.WHITE);
        //Sets texture to image in assets folder
        img = new Texture("badlogic.jpg");
        //Makes a sprite of that texture
        sprite = new Sprite(img);

        //sets the sprite position based on screen size
        sprite.setPosition(Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);

        //Makes a box2d physics environment that sets gravity
        world = new World(new Vector2(0, -98f), true);

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
        fixtureDef.density = 1f;
        //
        Fixture fixture = body.createFixture(fixtureDef);
        //Makes the fire effect
        fireEffect = new ParticleEffect();
        //Loads the effect file from the assets directory
        fireEffect.load(Gdx.files.internal("EffectAttempt2.p"),Gdx.files.internal("PixelParticle.png"));
        //puts the effect at the given point
        fireEffect.getEmitters().first().setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
        fireEffect.start();

        shape.dispose();

    }
    @Override
    public void dispose(){
        batch.dispose();
        font.dispose();
    }
    @Override
    public void render(){

        //Makes the box2d world play at a given frame rate
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
        //Makes the sprite follow the body
        sprite.setPosition(body.getPosition().x, body.getPosition().y);

        //sets the background color
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //makes the fire effect change every frame
        fireEffect.update(Gdx.graphics.getDeltaTime());
        //draws the actual frame
        batch.begin();
        batch.draw(sprite, sprite.getX(), sprite.getY());
        font.draw(batch, Float.toString(sprite.getX()), 200,200);
        fireEffect.draw(batch);
        batch.end();



    }
    @Override
    public void resize(int width, int height){

    }
    @Override
    public void pause(){

    }
    @Override
    public void resume(){

    }

}
