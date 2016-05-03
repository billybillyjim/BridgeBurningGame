package com.mygdx.game;

/**
 * Created by Luke on 2/12/2016.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.GraphicalObjects.*;


import java.util.*;

public class MainGame extends Stage implements Screen{

    private final GameLauncher game;

    private final int SCREEN_WIDTH = 800;
    private final int SCREEN_HEIGHT = 480;

    private ParticleEffect fireEffect;

    boolean constructionMode;

    private Music fireSound;

    private Texture leftTex;
    private Texture rightTex;
    private Texture burnImg;
    private Texture constructImg;

    private Texture background;
    private Sprite backgroundSprite;
    //Makes a box2d physics environment that sets gravity
    public final static World WORLD = new World(new Vector2(0, -98.1f), true);

    private BackgroundCliff leftCliff;
    private BackgroundCliff rightCliff;
    private BackgroundCliff bottom;

    private ToggleButton toggleButton;
    private RefreshButton refreshButton;

    private Box2DDebugRenderer box2DDebugRenderer;

    //array with bridge units links

    ArrayList<BridgeUnit> burntBridgeUnits = new ArrayList<BridgeUnit>();
    ArrayList<Body> bodiesToDestroy = new ArrayList<Body>();

    private FireHandler fireHandler;
    private BuildHandler buildHandler;

    private float timeCycle;

    private OrthographicCamera camera;
    private FitViewport viewport;

    private int level;

    public MainGame(GameLauncher game){

        this.game = game;
        constructionMode = true;


        game.font.setColor(Color.WHITE);

        level = 2;
        fireEffect = new ParticleEffect();
        fireEffect.load(Gdx.files.internal("Effect9.p"), Gdx.files.internal(""));

        leftTex = new Texture("LeftCliff.png");
        rightTex = new Texture("RightCliff.png");
        background = new Texture("BG1.png");

        backgroundSprite = new Sprite(background);

        drawCliffs();

        buildHandler = new BuildHandler(WORLD, this, fireEffect, leftCliff, rightCliff);

        //create camera -- ensure that we can use target resolution (800x480) no matter actual screen size
        // it creates a WORLD that is 800 x 480 units wide. it is the camera that controls the coordinate system that positions stuff on the screen
        //the origin (0, 0) of this coordinate system is in the lower left corner by default. It is possible to change
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        viewport = new FitViewport(SCREEN_WIDTH,SCREEN_HEIGHT,camera);
        setViewport(viewport);

        box2DDebugRenderer = new Box2DDebugRenderer();


        fireSound = Gdx.audio.newMusic(Gdx.files.internal("BurningTrueLoop.wav"));


        timeCycle = 1;




        createButtons();

    }

    @Override
    public void render(float delta) {
        fireEffect.update(delta);

        //Draw background, buttons
        game.batch.begin();
        backgroundSprite.draw(game.batch);
        game.batch.end();


        //Update the camera,
        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        fireGo();

        box2DDebugRenderer.render(WORLD, camera.combined);

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)){
            if(constructionMode) constructionMode = false;
            else if(!constructionMode) constructionMode = true;
            System.out.println("construction mode = " + constructionMode);

        }
        int numContacts = WORLD.getContactCount();

        if (numContacts > 0) {

            ArrayList<Body> bodyArrayList = new ArrayList<Body>();

            for (Contact contact : WORLD.getContactList()) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                if(fixtureB.getBody().getPosition().y < -40 && fixtureA.getBody().getType() == BodyDef.BodyType.StaticBody){
                    destroyJoints(fixtureB.getBody());
                    for(BridgeUnit bridgeUnit : buildHandler.getBridgeUnits()){
                        if(bridgeUnit.getBody().equals(fixtureB.getBody())){
                            burntBridgeUnits.add(bridgeUnit);
                            bodyArrayList.add(bridgeUnit.getBody());
                        }
                    }
                }
            }
            bodiesToDestroy.addAll(bodyArrayList);
        }

        //if(!bridgeBurned()) {
            if (Gdx.input.justTouched()) {
                Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(pos);
                Actor actor = this.hit(pos.x, pos.y, true);
                if (!constructionMode) {

                    if (actor != null && actor.getName().equals("Bridge Unit")) {
                        ((BridgeUnit) actor).setIsOnFire(true);
                    }
                    else if (actor != null && actor.getName().equals("Refresh")) {
                        reset();
                    }
                    else if (actor != null && actor.getName().equals("Toggle")){
                        constructionMode = true;
                        toggleButton.changeTexture(constructImg);
                    }
                    fireHandler = new FireHandler(buildHandler.getBridgeUnits());

                }
                else {
                    if (actor != null && actor.getName().equals("Refresh")) {
                        reset();
                    }
                    else if (actor != null && actor.getName().equals("Toggle")){
                        constructionMode = false;
                        toggleButton.changeTexture(burnImg);
                    }
                    else if (!(actor != null && actor.getName().equals("Cliff") || actor != null && actor.getName().equals("Bridge Unit"))){

                        buildHandler.makeBridgeUnit(pos.x, pos.y);

                    }

                }
            }
        if(!constructionMode){
            //Makes the box2d WORLD play at a given frame rate
            WORLD.step(Gdx.graphics.getDeltaTime(), 6, 2);
        }

        //}
        //else{
        //    System.out.println("bridge completely burned");
        //    // TODO: 4/29/16 add a restart game option
        //}

        timeCycle -= Gdx.graphics.getDeltaTime();

        act(delta);
        draw();

        game.batch.begin();
        fireEffect.draw(game.batch);
        game.batch.end();

        if(!WORLD.isLocked()){
           // destroyBodies();
        }


    }

    private void fireGo(){
        if(fireHandler == null) return;

        if(timeCycle < 0.0f){

            if(fireHandler.checkFires() && !fireSound.isPlaying()){
               //fireSound.play();
            }

            timeCycle = 1;
            fireHandler.burnAdjacents();
            burntBridgeUnits = fireHandler.burnUpBridgeUnits(burntBridgeUnits);

            burnWood();
        }
    }

    public boolean bridgeBurned(){
      /*  int sizeBurnBridge = burntBridgeUnits.size() + burntBridgeUnitLinks.size();
        int sizeBridge = bridge.getBridgeUnits().size() + bridge.getBridgeUnitLinks().size();
        int percentage = (sizeBurnBridge* 100) / sizeBridge;
        System.out.println(percentage);
        if (percentage == 100) return true;
        else return false;*/
        return false;
    }


    @Override
    public void show() {

        Gdx.input.setInputProcessor(this);

    }
    
    @Override
    public void resize(int width, int height){
        getViewport().update(width, height, true);
    }

    //Currently used for both click burning and the burning of each bridgeUnit and bridgeUnitLink
    public void burnWood(){
        for(BridgeUnit bridgeUnit : burntBridgeUnits){
            bridgeUnit.getBody().setType(BodyDef.BodyType.DynamicBody);
            destroyJoints(bridgeUnit.getBody());
            //bodiesToDestroy.add(bridgeUnit.getBody());

        }


    }

    public void destroyJoints(Body body){
        Array<JointEdge> jointEdges = body.getJointList();
        for(JointEdge edge : jointEdges ) {
            WORLD.destroyJoint(edge.joint);
        }
    }
    public void destroyBodies(){
        System.out.println("RUNNIG");
        for(Body body : bodiesToDestroy){
            WORLD.destroyBody(body);

        }
        
        bodiesToDestroy.clear();
    }

    public void drawCliffs(){
        leftTex = new Texture("LeftCliff.png");
        rightTex = new Texture("RightCliff.png");
        leftCliff = new BackgroundCliff(leftTex, 0, 0, WORLD, true);
        rightCliff = new BackgroundCliff(rightTex,800-rightTex.getWidth(), 0, WORLD, false);
        bottom = new BackgroundCliff(leftTex, leftCliff.getWidth(), -leftCliff.getHeight() - 100, WORLD, true);
        this.addActor(leftCliff);
        this.addActor(rightCliff);
        this.addActor(bottom);

    }

    @Override
    public void dispose(){
        WORLD.dispose();
        box2DDebugRenderer.dispose();

    }

    public void reset(){
        clear();
        fireHandler = null;
        buildHandler.getBridgeUnits().clear();
        burntBridgeUnits.clear();
        System.out.println(buildHandler.getBridgeUnits().isEmpty());
        Array<Body> bodies = new Array<Body>();
        WORLD.getBodies(bodies);
        for(Body body: bodies){
            destroyJoints(body);
            WORLD.destroyBody(body);
        }


        fireSound.pause();
        drawCliffs();
        //fireGo();
        constructionMode = true;
        createButtons();
        buildHandler.setLeftCliff(leftCliff);
        buildHandler.setRightCliff(rightCliff);

    }

    private void createButtons(){

        constructImg = new Texture("Construct.png");
        burnImg = new Texture("Burn.png");

        toggleButton = new ToggleButton(constructImg);
        refreshButton = new RefreshButton();
        this.addActor(refreshButton);
        this.addActor(toggleButton);


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

}
