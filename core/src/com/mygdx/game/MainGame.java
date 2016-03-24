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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.GraphicalObjects.BackgroundCliffs;
import com.mygdx.game.GraphicalObjects.BridgeUnit;
import com.mygdx.game.PhysicalObjects.BridgeJoint;
import com.mygdx.game.GraphicalObjects.BridgeUnitLink;

import java.util.ArrayList;

public class MainGame extends Stage implements Screen{

    private final GameLauncher game;
    public final int METER_TO_PIXELS = 50; //50 pixels per each meter
    private final int SCREEN_WIDTH = 800;
    private final int SCREEN_HEIGHT = 480;


    private ParticleEffect fireEffect;

    private Texture img;
    private Texture img2;
    private Texture img3;
    private Texture img4;
    private World world;


    private BridgeUnit bridgeUnit;
    private BridgeUnitLink testPivot;
    private BridgeJoint bridgeJoint;
    private BackgroundCliffs cliffs;

    private Box2DDebugRenderer box2DDebugRenderer;


    //array with bridge units links
    ArrayList<BridgeUnitLink> linksAcross;

    private boolean testOnClick = false;

    private Array<Body> bodiesInTheWorld; //this array will be used to keep all the bodies created in the world

    //camera
    private OrthographicCamera camera;

    private FitViewport viewport;

    public MainGame(GameLauncher game){

        this.game = game;

        //Sets a font color
        game.font.setColor(Color.WHITE);

        //Sets texture to image in assets folder
        img = new Texture("Wood.png");
        img2 = new Texture("Pivot.png");
        img3 = new Texture("LeftCliff.png");
        img4 = new Texture("RightCliff.png");


        //create camera -- ensure that we can use target resolution (800x480) no matter actual screen size
        // it creates a world that is 800 x 480 units wide. it is the camera that controls the coordinate system that positions stuff on the screen
        //the origin (0, 0) of this coordinate system is in the lower left corner by default. It is possible to change
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        viewport = new FitViewport(SCREEN_WIDTH,SCREEN_HEIGHT,camera);
        setViewport(viewport);


        //Makes a box2d physics environment that sets gravity
        world = new World(new Vector2(0, -981f), true);
        box2DDebugRenderer = new Box2DDebugRenderer();
        bodiesInTheWorld = new Array<Body>();


        cliffs = new BackgroundCliffs();
        cliffs.CreateCliffs(img3, img4, world);

        //new bridge uni
        buildBridge();

        //Makes the fire effect
        fireEffect = new ParticleEffect();
        //Loads the effect file from the assets directory
        fireEffect.load(Gdx.files.internal("Effect5.p"),Gdx.files.internal("PixelParticle2.png"));
        //puts the effect at the given point
        fireEffect.getEmitters().first().setPosition((float)(800.0 / 1.5) , (float) (480 / 1.5));
        fireEffect.start();


    }
    @Override
    public void dispose(){
        world.dispose();
        box2DDebugRenderer.dispose();

    }
    @Override
    public void render(float delta){

        if(Gdx.input.isTouched()){
            Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
            camera.unproject(pos);
            burnWood(pos.x, pos.y);
        }

        box2DDebugRenderer.render(world, camera.combined);

        //Makes the box2d world play at a given frame rate
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);


        //sets the background color
        Gdx.gl.glClearColor(0.52f, 0.80f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        act(delta);
        draw();

        camera.update(); // is generally a good practice to update the camera once per frame
        box2DDebugRenderer.render(world, camera.combined);//let us sees the body's created my Box2D without beeing attached to a sprite.
        game.batch.setProjectionMatrix(camera.combined); //tells spriteBatch to use coordinate system set by camera

        //makes the fire effect change every frame
        fireEffect.update(Gdx.graphics.getDeltaTime());

        world.getBodies(bodiesInTheWorld); //gets all the bodies in the world and adds then to the array bodiesInTheWorld



        //draws the actual frame
        game.batch.begin();

        game.batch.draw(cliffs.getSpriteLeft(), cliffs.getSpriteLeft().getX(), cliffs.getSpriteLeft().getY());
        game.batch.draw(cliffs.getSpriteRight(), cliffs.getSpriteRight().getX(), cliffs.getSpriteRight().getY());

        game.font.draw(game.batch, Boolean.toString(testOnClick), 200, 100);

        fireEffect.draw(game.batch);

        game.batch.end();

    }

    /**
     * method responsible for setting up the bridge
     */

    private void buildBridge() {
        ArrayList<BridgeUnit> bridgeUnitsAcross = new ArrayList<BridgeUnit>();
        buildUnitsAcrossCliffs(bridgeUnitsAcross);
        linksAcross = createBridgeUnitLinks(bridgeUnitsAcross);
        createBridgeLinkJoint(bridgeUnitsAcross, linksAcross);
    }

    private void buildUnitsAcrossCliffs(ArrayList<BridgeUnit> bridgeUnitsAcross) {
        float leftCliffWidth =  cliffs.getSpriteLeft().getWidth();
        float leftCliffHeight = cliffs.getSpriteLeft().getHeight();
        float rightCliffWidth = cliffs.getSpriteRight().getWidth();
        float rightCliffHeight = cliffs.getSpriteRight().getHeight();
        float numberOfBridgeUnits = getNumberOfBridgeUnits(leftCliffWidth, rightCliffWidth);
        System.out.println("cliff width " + leftCliffWidth + " cliff Height " + leftCliffHeight);
        for (int i = 0; i < numberOfBridgeUnits; i++) {
            BridgeUnit newUnit = new BridgeUnit();
            bridgeUnitsAcross.add(newUnit);
        }
        int i = 0;
        for (BridgeUnit unit : bridgeUnitsAcross) {
            unit.CreateTestBridge(img, world, cliffs.getSpriteLeft().getX() + leftCliffWidth + (i * BridgeUnit.WIDTH), cliffs.getSpriteLeft().getY() + leftCliffHeight);
            addActor(unit);
            i++;
        }
    }

    /**
     * This calculates the number of bridge units necessary to connect both cliffs
     * @param leftCliffWidth
     * @param rightCliffWidth
     * @return
     */

    private float getNumberOfBridgeUnits(float leftCliffWidth, float rightCliffWidth) {
        float distanceBetweenCliffs = SCREEN_WIDTH - (leftCliffWidth + rightCliffWidth);
        float numberOfBridgeUnits = distanceBetweenCliffs / BridgeUnit.WIDTH;
        //This if statement checks if the number of bridge units calculated above is enough to cover the distance. If it is not it add one more board.
        if((numberOfBridgeUnits * BridgeUnit.WIDTH) < distanceBetweenCliffs){
            numberOfBridgeUnits += 1;
        }
        return numberOfBridgeUnits;
    }

    /**
     * Takes an array of bridge units and creates a array of bridge unit links
     * @param unitsAcross as array of Bridge units
     * @return an array of bridge unit links
     */
    private ArrayList<BridgeUnitLink> createBridgeUnitLinks(ArrayList<BridgeUnit> unitsAcross){
        ArrayList<BridgeUnitLink> linksAcross = new ArrayList<BridgeUnitLink>();
        //for every unit in the array of Bridge units it creates a link and adds it to the array of links.
        // It does not create a new link to the last  unit in the array of units.
        for(int i = 0; i < unitsAcross.size()-1; i++){
            BridgeUnit unit = unitsAcross.get(i);
            BridgeUnitLink link = new BridgeUnitLink();
            link.CreateVertex(img2, world, unit.getBody().getPosition().x + BridgeUnit.WIDTH / 2, cliffs.getSpriteLeft().getY() + cliffs.getSpriteLeft().getHeight() + BridgeUnit.HEIGHT/2);
            linksAcross.add(link);
            addActor(link);
        }

        return linksAcross;
    }

    /**
     * Creates a joint between the links and the units. This still needs a lot of work.
     * @param unitsAcross
     * @param linksAcross
     */
    private void createBridgeLinkJoint(ArrayList<BridgeUnit> unitsAcross, ArrayList<BridgeUnitLink> linksAcross){
        for(int i = 0; i < linksAcross.size(); i++){
            BridgeJoint joint = new BridgeJoint();
            BridgeJoint joint2 = new BridgeJoint();
            joint.CreateJoint(unitsAcross.get(i).getBody(), linksAcross.get(i).getBody());
            joint.getrJointDef().localAnchorA.set(BridgeUnit.WIDTH / 2, 0);
            world.createJoint(joint.getrJointDef());
            joint2.CreateJoint(unitsAcross.get(i + 1).getBody(), linksAcross.get(i).getBody());
            joint2.getrJointDef().localAnchorA.set(-BridgeUnit.WIDTH / 2, 0);
            world.createJoint(joint2.getrJointDef());

        }
    }


    @Override
    public void show() {
        System.out.println("show called");
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
    public void burnWood(float x, float y){
        testOnClick = true;
        for(int i = 0; i < fireEffect.getEmitters().size; i++) {
            fireEffect.getEmitters().get(i).setPosition(x,y);
        }
        //fireEffect.getEmitters().first().setPosition(x,y);
        fireEffect.start();
    }

}