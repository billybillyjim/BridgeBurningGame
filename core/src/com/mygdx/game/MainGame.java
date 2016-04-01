package com.mygdx.game;

/**
 * Created by Luke on 2/12/2016.
 */


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.GraphicalObjects.*;

import java.util.ArrayList;

public class MainGame extends Stage implements Screen{

    private final GameLauncher game;
    public final int METER_TO_PIXELS = 50; //50 pixels per each meter
    private final int SCREEN_WIDTH = 800;
    private final int SCREEN_HEIGHT = 480;

    private ArrayList<ParticleEffect> particleEffects;


    private Texture img3;
    private Texture img4;
    //Makes a box2d physics environment that sets gravity
    public final static World WORLD = new World(new Vector2(0, -9.81f), true);

    private BackgroundCliffs cliffs;

    private Box2DDebugRenderer box2DDebugRenderer;

    //array with bridge units links
    ArrayList<BridgeUnitLink> linksAcross;

    private FireHandler fireHandler;

    private boolean testOnClick = false;

    public Array<Body> bodiesInTheWorld; //this array will be used to keep all the bodies created in the world
    public Array<Actor> actorsInTheWorld;
    public Array<BridgeUnit> bridgeUnitsInTheWorld;

    //camera
    private OrthographicCamera camera;
    private FitViewport viewport;

    public MainGame(GameLauncher game){

        this.game = game;

        //Sets a font color
        game.font.setColor(Color.WHITE);

        //Sets texture to image in assets folder

        img3 = new Texture("LeftCliff.png");
        img4 = new Texture("RightCliff.png");

        //create camera -- ensure that we can use target resolution (800x480) no matter actual screen size
        // it creates a WORLD that is 800 x 480 units wide. it is the camera that controls the coordinate system that positions stuff on the screen
        //the origin (0, 0) of this coordinate system is in the lower left corner by default. It is possible to change
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        viewport = new FitViewport(SCREEN_WIDTH,SCREEN_HEIGHT,camera);
        setViewport(viewport);

        box2DDebugRenderer = new Box2DDebugRenderer();


        cliffs = new BackgroundCliffs(img3, img4, WORLD);
        this.addActor(cliffs);

        Bridge bridge = new Bridge(WORLD, this, cliffs);

        //Lets the fireHandler know all the actors in the bridge.
        fireHandler = new FireHandler(bridge.getBridgeUnits());

        //Creates an array of particleEffects, which allows for many fires on the screen at once.
        particleEffects = new ArrayList<ParticleEffect>();

        fireHandler.burn();

    }

    @Override
    public void dispose(){
        WORLD.dispose();
        box2DDebugRenderer.dispose();

    }
    @Override
    public void render(float delta){

        if(Gdx.input.isTouched()){
            Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
            camera.unproject(pos);
            burnWood(pos.x, pos.y);
        }

        box2DDebugRenderer.render(WORLD, camera.combined);

        //Makes the box2d WORLD play at a given frame rate
        WORLD.step(Gdx.graphics.getDeltaTime(), 6, 2);


        //sets the background color
        Gdx.gl.glClearColor(0, 0, 0, .3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //The stage (this class) knows about all its actors.. the methods below are responsible to draw all actors in the stage
        act(delta);
        draw();

        camera.update(); // is generally a good practice to update the camera once per frame
        box2DDebugRenderer.render(WORLD, camera.combined);//let us sees the body's created my Box2D without beeing attached to a sprite.
        game.batch.setProjectionMatrix(camera.combined); //tells spriteBatch to use coordinate system set by camera

        //makes the fire effect change every frame
        //fireEffect.update(Gdx.graphics.getDeltaTime());
        if(!particleEffects.isEmpty()) {
            for (ParticleEffect fire : particleEffects) {
                fire.update(Gdx.graphics.getDeltaTime());
            }
        }

        //draws the actual frame
        game.batch.begin();
        //Runs through the array of particleEffects and draws each one
        for(ParticleEffect effect:particleEffects){
            effect.draw(game.batch);
        }

        game.batch.end();

    }


    @Override
    public void show() {

        Gdx.input.setInputProcessor(this);

    }


    @Override
    public void resize(int width, int height){
        getViewport().update(width, height, true);
    }
    @Override
    public void pause(){

    }
    @Override
    public void resume(){

    }

    @Override
    public void hide() {

    }
    //Currently used for both click burning and the burning of each bridgeUnit
    public void burnWood(float x, float y){

        for(Actor actor:this.getActors()){
            if(actor.getName() != null && actor.getName().equals("Bridge Unit")){

                BridgeUnit bunit = (BridgeUnit)actor;
                if(bunit.getIsOnFire()){

                    x = bunit.getX() + 50; //This is disgusting; needs fixing
                    System.out.println(x);
                    y = bunit.getY();
                    bunit.setIsOnFire(false);

                }
            }

        }

        ParticleEffect fireEffect = new ParticleEffect();
        fireEffect.load(Gdx.files.internal("Effect5.p"), Gdx.files.internal("PixelParticle2.png"));
        fireEffect.getEmitters().first().setPosition(x,y);
        particleEffects.add(fireEffect);

        for(int i = 0; i < fireEffect.getEmitters().size; i++) {
            fireEffect.getEmitters().get(i).setPosition(x,y);
        }

        fireEffect.start();

        fireEffect.reset();

    }

    public void destroyJoints(Body body){
        Array<JointEdge> jointEdges = body.getJointList();
        for(JointEdge edge : jointEdges ){
            WORLD.destroyJoint(edge.joint);
        }
    }






}
