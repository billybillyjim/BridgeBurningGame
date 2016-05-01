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

    private Texture img3;
    private Texture img4;
    private Texture burnImg;
    private Texture constructImg;

    private Texture background;
    private Sprite backgroundSprite;
    //Makes a box2d physics environment that sets gravity
    public final static World WORLD = new World(new Vector2(0, -98.1f), true);

    private BackgroundCliffs cliffs;

    private ToggleButton toggleButton;
    private RefreshButton refreshButton;

    private Box2DDebugRenderer box2DDebugRenderer;

    private Bridge bridge;
    //array with bridge units links
    ArrayList<BridgeUnitLink> burntBridgeUnitLinks = new ArrayList<BridgeUnitLink>();
    ArrayList<BridgeUnit> burntBridgeUnits = new ArrayList<BridgeUnit>();

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

        img3 = new Texture("LeftCliff.png");
        img4 = new Texture("RightCliff.png");
        background = new Texture("BG1.png");

        backgroundSprite = new Sprite(background);

        drawCliffs();

        buildHandler = new BuildHandler(WORLD, this, fireEffect);

        //create camera -- ensure that we can use target resolution (800x480) no matter actual screen size
        // it creates a WORLD that is 800 x 480 units wide. it is the camera that controls the coordinate system that positions stuff on the screen
        //the origin (0, 0) of this coordinate system is in the lower left corner by default. It is possible to change
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        viewport = new FitViewport(SCREEN_WIDTH,SCREEN_HEIGHT,camera);
        setViewport(viewport);

        box2DDebugRenderer = new Box2DDebugRenderer();


        fireSound = Gdx.audio.newMusic(Gdx.files.internal("BurningLoop2.wav"));


        timeCycle = 1;

        burntBridgeUnitLinks = new ArrayList<BridgeUnitLink>();


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
        box2DDebugRenderer.render(WORLD, camera.combined);
        game.batch.setProjectionMatrix(camera.combined);
        fireGo();

        box2DDebugRenderer.render(WORLD, camera.combined);

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)){
            if(constructionMode) constructionMode = false;
            else if(!constructionMode) constructionMode = true;
            System.out.println("construction mode = " + constructionMode);

        }

        //if(!bridgeBurned()) {
            if (Gdx.input.justTouched()) {
                Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                System.out.println("you clicked at:" + pos);
                camera.unproject(pos);
                Actor actor = this.hit(pos.x, pos.y, true);
                if (!constructionMode) {
                    if (actor != null && actor.getName().equals("Bridge Unit Link")) {
                        ((BridgeUnitLink) actor).setIsOnFire(true);
                    }
                    else if (actor != null && actor.getName().equals("Bridge Unit")) {
                        ((BridgeUnit) actor).setIsOnFire(true);
                    }
                    else if (actor != null && actor.getName().equals("Refresh")) {
                        reset();
                    }
                    else if (actor != null && actor.getName().equals("Toggle")){
                        constructionMode = true;
                        toggleButton.changeTexture(constructImg);
                    }
                    fireHandler = new FireHandler(buildHandler.getBridgeUnits(), buildHandler.getBridgeUnitLinks());

                }
                else {
                    if (actor != null && actor.getName().equals("Refresh")) {
                        reset();
                    }
                    else if (actor != null && actor.getName().equals("Toggle")){
                        constructionMode = false;
                        toggleButton.changeTexture(burnImg);
                    }
                    else {
                        System.out.println(pos.x);
                        buildHandler.makeBridgeUnitLink(pos.x, pos.y);
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
    }

    private void fireGo(){
        if(fireHandler == null) return;

        if(timeCycle < 0.0f){

            if(fireHandler.checkFires() && !fireSound.isPlaying()){
               // fireSound.play();
            }

            timeCycle = 1;
            fireHandler.burnAdjacents();
            burntBridgeUnits = fireHandler.burnUpBridgeUnits(burntBridgeUnits);
            burntBridgeUnitLinks = fireHandler.burnUpBridgeUnitLinks(burntBridgeUnitLinks);

            burnWood();
        }
    }

    public boolean bridgeBurned(){
        int sizeBurnBridge = burntBridgeUnits.size() + burntBridgeUnitLinks.size();
        int sizeBridge = bridge.getBridgeUnits().size() + bridge.getBridgeUnitLinks().size();
        int percentage = (sizeBurnBridge* 100) / sizeBridge;
        System.out.println(percentage);
        if (percentage == 100) return true;
        else return false;
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
        fireHandler = null;
        Array<Body> bodies = new Array<Body>();
        WORLD.getBodies(bodies);
        for(Body body: bodies){
            destroyJoints(body);
            WORLD.destroyBody(body);
        }


        buildHandler.getBridgeUnitLinks().clear();
        buildHandler.getBridgeUnits().clear();

        fireSound.pause();

        drawCliffs();
        fireGo();
        createButtons();

    }

    private void createButtons(){

        constructImg = new Texture("Construct.png");
        burnImg = new Texture("Burn.png");

        toggleButton = new ToggleButton(burnImg);
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
