package com.mygdx.game;

/**
 * Created by Luke on 2/12/2016.
 */


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
    private BackgroundCliffs testCliffs;

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


        testCliffs = new BackgroundCliffs();
        testCliffs.CreateCliffs(img3, img4, world);

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
        //The stage (this class) knows about all its actors.. the methods below are responsible to draw all actors in the stage
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


        game.batch.draw(testCliffs.getSpriteLeft(), testCliffs.getSpriteLeft().getX(), testCliffs.getSpriteLeft().getY());
        game.batch.draw(testCliffs.getSpriteRight(), testCliffs.getSpriteRight().getX(), testCliffs.getSpriteRight().getY());

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
        createBridgeUnitPillars(linksAcross);
    }

    private void buildUnitsAcrossCliffs(ArrayList<BridgeUnit> bridgeUnitsAcross) {
        float leftCliffWidth =  testCliffs.getSpriteLeft().getWidth();
        float leftCliffHeight = testCliffs.getSpriteLeft().getHeight();
        float rightCliffWidth = testCliffs.getSpriteRight().getWidth();
        float rightCliffHeight = testCliffs.getSpriteRight().getHeight();
        float numberOfBridgeUnits = getNumberOfBridgeUnits(leftCliffWidth, rightCliffWidth);
        System.out.print("cliff width" + leftCliffWidth + " cliff Height " + leftCliffHeight);
        for (int i = 0; i < numberOfBridgeUnits; i++) {
            BridgeUnit newUnit = new BridgeUnit();
            bridgeUnitsAcross.add(newUnit);
        }
        int i = 0;
        for (BridgeUnit unit : bridgeUnitsAcross) {
            unit.CreateTestBridge(img, world, testCliffs.getSpriteLeft().getX() + leftCliffWidth + (i * BridgeUnit.WIDTH), testCliffs.getSpriteLeft().getY() + leftCliffHeight);
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
            link.CreateVertex(img2, world, unit.getBody().getPosition().x + BridgeUnit.WIDTH / 2, testCliffs.getSpriteLeft().getY() + testCliffs.getSpriteLeft().getHeight() + BridgeUnit.HEIGHT/2);
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

    /**
     * method responsible for organinizing and calling all  methods to create the pillars of the bridges
     * @param linksAcross
     */
    private void createBridgeUnitPillars(ArrayList<BridgeUnitLink> linksAcross){
        ArrayList<BridgeUnit> pillarLeft = new ArrayList<BridgeUnit>(); //pillar of the left
        ArrayList<BridgeUnit> pillarRight = new ArrayList<BridgeUnit>();   //pillar of the right
        Sprite linkLeft = linksAcross.get(0).getSprite(); //the left pillar is created in the x location of the first unit that is across the cliff
        Sprite linkRight = linksAcross.get(linksAcross.size()-1).getSprite(); //the right pillar is created in the x location of the last unit that is across the cliff
        createPillar(pillarLeft, linkLeft);
        createPillar(pillarRight, linkRight);

        BridgeUnitLink newLinkLeft = createLinkPillars(pillarLeft);
        createJointsPillars(linksAcross.get(0), pillarLeft, newLinkLeft);
        BridgeUnitLink newLinkRight = createLinkPillars(pillarRight);
        createJointsPillars(linksAcross.get(linksAcross.size()-1), pillarRight, newLinkRight);

    }

    /**
     * this method actually creates a pillar
     * @param pillarUnits
     * @param linkLeft
     */

    private void createPillar(ArrayList<BridgeUnit> pillarUnits, Sprite linkLeft) {
        BridgeUnit left1 = new BridgeUnit(img, world, linkLeft.getX(), linkLeft.getY());
        left1.getBody().setTransform(linkLeft.getX(), linkLeft.getY() + BridgeUnit.WIDTH / 2, MathUtils.PI / 2);
        addActor(left1);
        pillarUnits.add(left1);
        BridgeUnit left2 = new BridgeUnit(img, world, linkLeft.getX(), linkLeft.getY());
        left2.getBody().setTransform(left1.getSprite().getX(), left1.getSprite().getY() + BridgeUnit.WIDTH * 1.5f, MathUtils.PI / 2);
        addActor(left2);
        pillarUnits.add(left2);
        System.out.println("\n left 1 x" + left1.getSprite().getX() + " left1 y:  " + left1.getSprite().getY());
        System.out.println("\n left 2 x" + left2.getSprite().getX() + " left2 y:  " + left2.getSprite().getY());
    }

    /**
     * This method creates the link between the 2 units in the pillar
     * @param pillarUnits
     * @return
     */
    private BridgeUnitLink createLinkPillars(ArrayList<BridgeUnit> pillarUnits){
        BridgeUnit unit = pillarUnits.get(0);
        BridgeUnitLink link = new BridgeUnitLink();
        link.CreateVertex(img2, world, unit.getSprite().getX(), unit.getSprite().getY() + BridgeUnit.WIDTH);
        addActor(link);

        return link;
    }

    /**
     * this method creates the joint between the links and the bridge units of the pillars
     * @param linkBottom this is the link between at the base of the pillar
     * @param pillarUnits this is the array with the bridge units of the pillar
     * @param linkMiddle this is the link between the two bridge units of the pillar
     */
    private void createJointsPillars(BridgeUnitLink linkBottom, ArrayList<BridgeUnit> pillarUnits, BridgeUnitLink linkMiddle){
        Body linkBody = linkBottom.getBody();
        Body unitBody = pillarUnits.get(0).getBody();

        //joint between link at the base and first bridge unit of the pillar
        BridgeJoint joint = new BridgeJoint();
        joint.CreateJoint(unitBody, linkBody);
        joint.getrJointDef().localAnchorA.set(-BridgeUnit.WIDTH / 2, 0);
        world.createJoint(joint.getrJointDef());
        System.out.println("joints linkbody: " + linkBody.getJointList().size);

        //joint between first unit of the pillar and link in the middle
        Body linkBody2 = linkMiddle.getBody();
        BridgeJoint joint1 = new BridgeJoint();
        joint1.CreateJoint(unitBody, linkBody2);
        joint1.getrJointDef().localAnchorA.set(BridgeUnit.WIDTH / 2, 0);
        world.createJoint(joint1.getrJointDef());

        //joint between second unit of the pillar and link in the middle
        Body unitBody2 = pillarUnits.get(1).getBody();
        BridgeJoint joint3 = new BridgeJoint();
        joint3.CreateJoint(unitBody2, linkBody2);
        joint3.getrJointDef().localAnchorA.set(-BridgeUnit.WIDTH / 2, 0);
        world.createJoint(joint3.getrJointDef());
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
