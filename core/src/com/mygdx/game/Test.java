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
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.BLACK);

        batch = new SpriteBatch();

        img = new Texture("badlogic.jpg");
        sprite = new Sprite(img);


        sprite.setPosition(Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);


        world = new World(new Vector2(0, -98f), true);


        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        bodyDef.position.set(sprite.getX(), sprite.getY());


        body = world.createBody(bodyDef);


        PolygonShape shape = new PolygonShape();

        shape.setAsBox(sprite.getWidth()/2, sprite.getHeight()/2);


        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        Fixture fixture = body.createFixture(fixtureDef);
        fireEffect = new ParticleEffect();
        fireEffect.load(Gdx.files.internal("EffectAttempt2.p"),Gdx.files.internal("PixelParticle.png"));
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

        world.step(Gdx.graphics.getDeltaTime(), 6, 2);

        sprite.setPosition(body.getPosition().x, body.getPosition().y);


        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        fireEffect.update(Gdx.graphics.getDeltaTime());
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
