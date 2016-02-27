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
import sun.security.ssl.Debug;

import java.io.Console;


public class Test implements ApplicationListener{
    private SpriteBatch batch;
    private BitmapFont font;
    private ParticleEffect fireEffect;
    Sprite sprite;
    Texture img;
    Texture img2;
    World world;
    Body body;

    TestBridge testBridge;
    TestVertex testVertex;
    TestJoint testJoint;

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
        img = new Texture("Wood.png");
        img2 = new Texture("ball.png");
        //Makes a box2d physics environment that sets gravity
        world = new World(new Vector2(0, -98f), true);


        testBridge = new TestBridge();
        testBridge.CreateTestBridge(img,world);
        testVertex = new TestVertex();
        testVertex.CreateVertex(img2,world);
        testJoint = new TestJoint();

        testJoint.CreateJoint(testBridge.getBody(), testVertex.getBody());

        world.createJoint(testJoint.rJointDef);


        //Makes the fire effect
        fireEffect = new ParticleEffect();
        //Loads the effect file from the assets directory
        fireEffect.load(Gdx.files.internal("EffectAttempt2.p"),Gdx.files.internal("PixelParticle.png"));
        //puts the effect at the given point
        fireEffect.getEmitters().first().setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
        fireEffect.start();


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
        testBridge.sprite.setPosition(testBridge.body.getPosition().x, testBridge.body.getPosition().y);
        testVertex.sprite.setPosition(testVertex.body.getPosition().x, testVertex.body.getPosition().y);

        //sets the background color
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //makes the fire effect change every frame
        fireEffect.update(Gdx.graphics.getDeltaTime());
        //draws the actual frame
        batch.begin();
        batch.draw(testBridge.sprite, testBridge.sprite.getX(), testBridge.sprite.getY());
        batch.draw(testVertex.sprite, testVertex.sprite.getX(), testVertex.sprite.getY());
        font.draw(batch, Float.toString(testBridge.sprite.getY()), 200,200);
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
