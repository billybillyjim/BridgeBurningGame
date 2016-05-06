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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
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
    public final World WORLD = new World(new Vector2(0, -98.1f), true);

    private BackgroundCliff leftCliff;
    private BackgroundCliff rightCliff;
    private BackgroundCliff bottom;

    private ToggleButton toggleButton;
    private RefreshButton refreshButton;
    private HelpButton helpButton;

    private Box2DDebugRenderer box2DDebugRenderer;

    //array with bridge units links

    ArrayList<BridgeUnit> burntBridgeUnits = new ArrayList<BridgeUnit>();
    ArrayList<Body> bodiesToDestroy = new ArrayList<Body>();

    private FireHandler fireHandler;
    private BuildHandler buildHandler;

    private float timeCycle;

    private OrthographicCamera camera;
    private Viewport viewport;



    public MainGame(GameLauncher game){

        this.game = game;
        constructionMode = true;


        game.font.setColor(Color.WHITE);


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
        viewport = new StretchViewport(SCREEN_WIDTH,SCREEN_HEIGHT,camera);
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



        if (Gdx.input.isKeyJustPressed(Input.Keys.M)){
            if(constructionMode) constructionMode = false;
            else if(!constructionMode) constructionMode = true;
            System.out.println("construction mode = " + constructionMode);

        }

        checkContacts();

        if (Gdx.input.justTouched()) {
            Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(pos);
            Actor actor = this.hit(pos.x, pos.y, true);
            click(actor, pos);
        }
        
       if(!constructionMode){
           //Makes the box2d WORLD play at a given frame rate
           WORLD.step(Gdx.graphics.getDeltaTime(), 6, 2);

       }

        timeCycle -= Gdx.graphics.getDeltaTime();

       

        buildHandler.bringUserMadeToFront();
        helpButton.toFront();
        act(delta);
        draw();
        //box2DDebugRenderer.render(WORLD, camera.combined);

        game.batch.begin();
        fireEffect.draw(game.batch);
        game.batch.end();

        if(!WORLD.isLocked()){
           //destroyBodies();
        }



    }

    private void click(Actor actor, Vector3 pos) {

        if (!constructionMode) {


            if (actor != null && actor.getName().equals("Bridge Unit")) {
                ((BridgeUnit) actor).setIsOnFire(true);
            } else if (actor != null && actor.getName().equals("Refresh")) {
                reset();
            } else if (actor != null && actor.getName().equals("Toggle")) {
                constructionMode = true;
                toggleButton.changeTexture(constructImg);
            } else if (actor != null && actor.getName().equals("Help")) {
                if (helpButton.isClicked()) helpButton.setClicked(false);
                else helpButton.setClicked(true);
                System.out.println("help  " + helpButton.isClicked());
            }
            if (fireHandler == null) fireHandler = new FireHandler(buildHandler.getBridgeUnits());

        } else {
            if (actor != null && actor.getName().equals("Refresh")) {
                reset();
            } else if (actor != null && actor.getName().equals("Toggle")) {
                constructionMode = false;
                toggleButton.changeTexture(burnImg);
            } else if (actor != null && actor.getName().equals("Help")) {
                if (helpButton.isClicked()) helpButton.setClicked(false);
                else helpButton.setClicked(true);
                System.out.println("help  " + helpButton.isClicked());
            } else if (!(actor != null && actor.getName().equals("Cliff") || actor != null && actor.getName().equals("Bridge Unit"))) {

                buildHandler.makeBridgeUnit(pos.x, pos.y);

            }
        }
    }
    private void checkContacts(){
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
    }
/**This method checks for a fireHandler to deal with fire spread. If one exists,
 * and the timecycle has completed, it plays the fire sound if no sound is playing
 * and resets the time cycle. Then it burns the adjacent BridgeUnits and makes a list
 * of BridgeUnits that are burnt. Lastly it calls burnWood()
 */
    private void fireGo(){
        if(fireHandler == null) return;

        if(timeCycle < 0.0f){

            if(fireHandler.checkFires() && !fireSound.isPlaying()){
               fireSound.setLooping(true);
                //fireSound.play();
            }

            timeCycle = 1;
            fireHandler.burnAdjacents();
            burntBridgeUnits = fireHandler.burnUpBridgeUnits(burntBridgeUnits);

            burnWood();
        }
    }



    @Override
    public void show() {
        //Allows user input
        Gdx.input.setInputProcessor(this);

    }
    
    @Override
    public void resize(int width, int height){
        //Stretches the screen to needed size.
        getViewport().update(width, height, true);
    }

    /**Iterates through the burntBridgeUnits ArrayList,
     * sets them to dynamic and destroys their joints
    **/
    public void burnWood(){
        for(BridgeUnit bridgeUnit : burntBridgeUnits){
            bridgeUnit.getBody().setType(BodyDef.BodyType.DynamicBody);
            destroyJoints(bridgeUnit.getBody());
            //bodiesToDestroy.add(bridgeUnit.getBody());
        }
    }
    
    /**Takes a given body and destros all their joints
     **/
    public void destroyJoints(Body body){
        Array<JointEdge> jointEdges = body.getJointList();
        for(JointEdge edge : jointEdges ) {
            WORLD.destroyJoint(edge.joint);
        }
    }
    /**Iterates through the bodiesToDestroy ArrayList
    * and destroys them
    **/
    public void destroyBodies(){
        for(Body body : bodiesToDestroy){
            WORLD.destroyBody(body);
        }
        bodiesToDestroy.clear();
    }
    /**Draws the cliffs
     * This method makes textures and creates three new cliffs,
     * the one on each side of the screen and the bottom on which
     * is not seen on screen. This is called on startup and every time
     * the game is reset.
     **/
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
    /** This method is run when the reset button is clicked.
    * First it clears the Screen, then it empties all the
    * ArrayLists that contain bodies. This prevents memory
    * management crashes. It also pauses and destroys all the bodies
    * in the world. After everything is cleared it rebuilds the cliffs and
    * buttons and returns to construction mode.
    **/
    public void reset(){
        clear();
        fireHandler = null;
        buildHandler.getBridgeUnits().clear();
        burntBridgeUnits.clear();
        bodiesToDestroy.clear();

        Array<Body> bodies = new Array<Body>();
        WORLD.getBodies(bodies);
        for(Body body: bodies){
            destroyJoints(body);
            WORLD.destroyBody(body);
        }


        fireSound.pause();
        drawCliffs();
        constructionMode = true;
        createButtons();
        buildHandler.setLeftCliff(leftCliff);
        buildHandler.setRightCliff(rightCliff);


    }
    
    //This method creates the refresh and Burn/Build button.
    private void createButtons(){

        constructImg = new Texture("Construct.png");
        burnImg = new Texture("Burn.png");

        toggleButton = new ToggleButton(constructImg);
        refreshButton = new RefreshButton();
        helpButton = new HelpButton();
        helpButton.setPosition(refreshButton.getX() + 1.5f * helpButton.getWidth(), refreshButton.getY());
        addActor(helpButton);
        addActor(refreshButton);
        addActor(toggleButton);


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
