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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.GraphicalObjects.*;

import java.text.DecimalFormat;
import java.util.*;

public class MainGame extends Stage implements Screen{

    private final GameLauncher game;

    private final int SCREEN_WIDTH = 800;
    private final int SCREEN_HEIGHT = 480;

    private ArrayList<ParticleEffect> particleEffects;

    private final int maxTime = 60; //maximum time in seconds the player has to make the bridge burn

    boolean constructionMode;


    private Music fireSound;

    private Texture img3;
    private Texture img4;
    private Texture img1;



    private Texture background;
    private Sprite backgroundSprite;
    //Makes a box2d physics environment that sets gravity
    public final static World WORLD = new World(new Vector2(0, -98.1f), true);

    private BackgroundCliffs cliffs;
    private RefreshButton refreshButton;

    private Box2DDebugRenderer box2DDebugRenderer;

    private Bridge bridge;
    //array with bridge units links
    ArrayList<BridgeUnitLink> burntBridgeUnitLinks = new ArrayList<BridgeUnitLink>();
    ArrayList<BridgeUnit> burntBridgeUnits = new ArrayList<BridgeUnit>();

    private FireHandler fireHandler;
    private BuildHandler buildHandler;



    private float timeLimit;
    private float timeCycle;

    private DecimalFormat df = new DecimalFormat("#.#");

    private OrthographicCamera camera;
    private FitViewport viewport;

    private int level;

    public MainGame(GameLauncher game){

        this.game = game;
        constructionMode = true;

        game.font.setColor(Color.WHITE);

        level = 2;


        img3 = new Texture("LeftCliff.png");
        img4 = new Texture("RightCliff.png");
        background = new Texture("BG1.png");

        backgroundSprite = new Sprite(background);

        drawCliffs();

        buildHandler = new BuildHandler(WORLD, this);

        //create camera -- ensure that we can use target resolution (800x480) no matter actual screen size
        // it creates a WORLD that is 800 x 480 units wide. it is the camera that controls the coordinate system that positions stuff on the screen
        //the origin (0, 0) of this coordinate system is in the lower left corner by default. It is possible to change
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        viewport = new FitViewport(SCREEN_WIDTH,SCREEN_HEIGHT,camera);
        setViewport(viewport);

        box2DDebugRenderer = new Box2DDebugRenderer();



       // bridge = new Bridge(WORLD, this, cliffs, level);

        //Lets the fireHandler know all the actors in the bridge.
        //fireHandler = new FireHandler(bridge.getBridgeUnits(), bridge.getBridgeUnitLinks());

        fireSound = Gdx.audio.newMusic(Gdx.files.internal("BurningLoop2.wav"));


        timeLimit = maxTime;
        timeCycle = 1;

        burntBridgeUnitLinks = new ArrayList<BridgeUnitLink>();



    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)){
            if(constructionMode) constructionMode = false;
            else if(!constructionMode) constructionMode = true;
            System.out.println("construction mode = " + constructionMode);

        }


        if(!constructionMode) {
            if (Gdx.input.justTouched()) {
                Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(pos);

                Actor actor = this.hit(pos.x, pos.y, true);
                if (actor != null && actor.getName().equals("Bridge Unit Link")) {
                    ((BridgeUnitLink) actor).setIsOnFire(true);


                } else if (actor != null && actor.getName().equals("Bridge Unit")) {

                    ((BridgeUnit) actor).setIsOnFire(true);

                }
                fireHandler = new FireHandler(buildHandler.getBridgeUnits(), buildHandler.getBridgeUnitLinks());


                //fireHandler.burnAdjacents();
               // burnWood();


            }
        } else {

            if (Gdx.input.justTouched()) {
                Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(pos);
                System.out.println(pos.x);
                buildHandler.makeBridgeUnitLink(pos.x, pos.y);
            }
        }
        timeLimit -= Gdx.graphics.getDeltaTime();
        timeCycle -= Gdx.graphics.getDeltaTime();

        box2DDebugRenderer.render(WORLD, camera.combined);


        //Makes the box2d WORLD play at a given frame rate
        WORLD.step(Gdx.graphics.getDeltaTime(), 6, 2);
        drawButtons();


        //sets the background color
        Gdx.gl.glClearColor(1, 1, 1, .3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //draws the actual frame
        game.batch.begin();

        backgroundSprite.draw(game.batch);
        game.font.draw(game.batch, df.format(timeLimit), this.getWidth() / 2, this.getHeight() - 20);
        //game.font.draw(game.batch, percentageOfBridgeBurned(), this.getWidth() / 2, this.getHeight() - 40);
        //Runs through the array of particleEffects and draws each one


        game.batch.end();

        //The stage (this class) knows about all its actors.. the methods below are responsible to draw all actors in the stage
        act(delta);
        draw();

        camera.update(); // is generally a good practice to update the camera once per frame
        box2DDebugRenderer.render(WORLD, camera.combined);//let us sees the body's created my Box2D without beeing attached to a sprite.
        game.batch.setProjectionMatrix(camera.combined); //tells spriteBatch to use coordinate system set by camera
        fireGo();

    }
    private void fireGo(){
        if(fireHandler == null) return;
        if(timeCycle < 0.0f){

            if(fireHandler.checkFires() && !fireSound.isPlaying()){
                fireSound.play();
            }

            timeCycle = 1;
            fireHandler.burnAdjacents();
            burntBridgeUnits = fireHandler.burnUpBridgeUnits(burntBridgeUnits);
            burntBridgeUnitLinks = fireHandler.burnUpBridgeUnitLinks(burntBridgeUnitLinks);

            //burntBridgeUnits = fireHandler.burnUp();
            burnWood();

        }

        if(timeLimit < 0.0f){
           game.setScreen(new EndGameScreen(game));

        }

    }

    public String percentageOfBridgeBurned(){
        int sizeBurnBridge = burntBridgeUnits.size() + burntBridgeUnitLinks.size();
        int sizeBridge = bridge.getBridgeUnits().size() + bridge.getBridgeUnitLinks().size();
        int percentage = (sizeBurnBridge* 100) / sizeBridge;


        return percentage + "%";
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

            destroyJoints(bridgeUnit.getBody());
            //particleEffects.remove(particleEffects.get(burntBridgeUnits.indexOf(bridgeUnit)));
        }

        for(BridgeUnitLink bridgeUnitLink : burntBridgeUnitLinks){
            bridgeUnitLink.changeBodyType();
        }

    }

    public void destroyJoints(Body body){
        Array<JointEdge> jointEdges = body.getJointList();
        for(JointEdge edge : jointEdges ) {

            WORLD.destroyJoint(edge.joint);

        }
    }

    public void drawCliffs(){
        img3 = new Texture("LeftCliff.png");
        img4 = new Texture("RightCliff.png");
        cliffs = new BackgroundCliffs(img3, img4, WORLD);
        this.addActor(cliffs);

    }

    @Override
    public void dispose(){
        WORLD.dispose();
        box2DDebugRenderer.dispose();

    }

    public void reset(){
        clear();
        Array<Body> bodies = new Array<Body>();
        WORLD.getBodies(bodies);
        for(Body body: bodies){
            destroyJoints(body);
            WORLD.destroyBody(body);
        }

        timeLimit = maxTime;
        bridge = new Bridge(WORLD, this, cliffs, level);
        fireHandler.updateBridgeUnitArray(bridge.getBridgeUnits());
        fireHandler.updateBridgeUnitLinkArray(bridge.getBridgeUnitLinks());
        drawCliffs();
        fireGo();
        //TODO: stop fire first!! allow fire spreading
    }

    private void drawButtons(){
        img1 = new Texture("Refresh.png");
        refreshButton = new RefreshButton(img1, WORLD);
        this.addActor(refreshButton);

        refreshButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                reset();
            }
        });
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)){

            reset();
          }


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
