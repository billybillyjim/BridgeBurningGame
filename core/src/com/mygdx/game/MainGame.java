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
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GraphicalObjects.BackgroundCliffs;
import com.mygdx.game.GraphicalObjects.BridgeUnit;
import com.mygdx.game.PhysicalObjects.BridgeJoint;
import com.mygdx.game.GraphicalObjects.BridgeUnitLink;

import java.util.ArrayList;


public class MainGame implements Screen{

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


    //array with bridge units




    private boolean testOnClick = false;


    private Array<Body> bodiesInTheWorld; //this array will be used to keep all the bodies created in the world

    //camera
    private OrthographicCamera camera;



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




        //Makes a box2d physics environment that sets gravity
        world = new World(new Vector2(0, -981f), true);
        box2DDebugRenderer = new Box2DDebugRenderer();
        bodiesInTheWorld = new Array<Body>();


        /*
        bridgeUnit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("DONE CLICKED IT YOU DID");
                burnWood(x,y);
            }
        });
        */

        //initialize array of bridge units




        // ALL JOINT STUFF



        testCliffs = new BackgroundCliffs();
        testCliffs.CreateCliffs(img3, img4, world);

        //new bridge uni
        buildBridge();
        //new joint connecting left cliff to bridge
        BridgeJoint leftCliffToUnitJoint = new BridgeJoint();
      //  leftCliffToUnitJoint.CreateJoint(newUnit.getBody(), testCliffs.getBodyLeft());
        //leftCliffToUnitJoint.getrJointDef().localAnchorA.set(testCliffs.getSpriteLeft().getWidth(), testCliffs.getSpriteLeft().getHeight());
        //leftCliffToUnitJoint.getrJointDef().localAnchorB.set(-45, 20);
        //world.createJoint(leftCliffToUnitJoint.getrJointDef());

        //create new link between two brigde pieces
/*
        BridgeUnitLink link1 = new BridgeUnitLink();
        //link1.CreateVertex(img2, world, testCliffs.getSpriteLeft().getWidth() + newUnit.getSprite().getWidth(), testCliffs.getSpriteLeft().getHeight());

        //create joint between link1 and newUnit
        BridgeJoint unitToLink = new BridgeJoint();
     //   unitToLink.CreateJoint(link1.getBody(), newUnit.getBody());
        unitToLink.getrJointDef().localAnchorA.set(0, 0);
        unitToLink.getrJointDef().localAnchorB.set(0, -45);
        world.createJoint(unitToLink.getrJointDef());
*/




        //Makes the fire effect
        fireEffect = new ParticleEffect();
        //Loads the effect file from the assets directory
        fireEffect.load(Gdx.files.internal("EffectAttempt2.p"),Gdx.files.internal("PixelParticle.png"));
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
            fireEffect.getEmitters().first().setPosition(pos.x,pos.y);
            fireEffect.start();
        }

        box2DDebugRenderer.render(world, camera.combined);

        //Makes the box2d world play at a given frame rate
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);



        //sets the background color
        Gdx.gl.glClearColor(0.52f, 0.80f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update(); // is generally a good practice to update the camera once per frame
        box2DDebugRenderer.render(world, camera.combined);//let us sees the body's created my Box2D without beeing attached to a sprite.
        game.batch.setProjectionMatrix(camera.combined); //tells spriteBatch to use coordinate system set by camera
        //Makes the sprite follow the body







        //makes the fire effect change every frame
        fireEffect.update(Gdx.graphics.getDeltaTime());

        world.getBodies(bodiesInTheWorld); //gets all the bodies in the world and adds then to the array bodiesInTheWorld

        //draws the actual frame
        game.batch.begin();


        game.batch.draw(testCliffs.getSpriteLeft(), testCliffs.getSpriteLeft().getX(), testCliffs.getSpriteLeft().getY());
        game.batch.draw(testCliffs.getSpriteRight(), testCliffs.getSpriteRight().getX(), testCliffs.getSpriteRight().getY());

        game.font.draw(game.batch, Boolean.toString(testOnClick), 200, 100);

        fireEffect.draw(game.batch);

        //Used to draw all moving sprites in bodies in the world (As of now it is just drawing the Bridge sprite in its body)
        for(Body body : bodiesInTheWorld){
            if(body.getUserData() != null && body.getUserData() instanceof Sprite){
                Sprite sprite = (Sprite) body.getUserData();
                sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2); //sets position of sprite to the same as the body
                sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees); //set rotation of the sprite to the same as the body
                sprite.draw(game.batch);
            }

        }




        game.batch.end();



    }

    /**
     * method responsible for setting up the bridge
     */

    private void buildBridge() {
        ArrayList<BridgeUnit> bridgeUnitsAcross = new ArrayList<BridgeUnit>();
        float leftCliffWidth =  testCliffs.getSpriteLeft().getWidth();
        float leftCliffHeight = testCliffs.getSpriteLeft().getHeight();
        float rightCliffWidth = testCliffs.getSpriteRight().getWidth();
        float rightCliffHeight = testCliffs.getSpriteRight().getHeight();
        float numberOfBridgeUnits = getNumberOfBridgeUnits(leftCliffWidth, rightCliffWidth);
        System.out.print("predicted Number of units is: " + numberOfBridgeUnits);
        for (int i = 0; i < numberOfBridgeUnits; i++) {
            BridgeUnit newUnit = new BridgeUnit();
            bridgeUnitsAcross.add(newUnit);
        }
        int i = 0;
        for (BridgeUnit unit : bridgeUnitsAcross) {
            unit.CreateTestBridge(img, world, testCliffs.getSpriteLeft().getX() + leftCliffWidth + (i * BridgeUnit.WIDTH), testCliffs.getSpriteLeft().getY() + leftCliffHeight);
            i++;
        }
        ArrayList<BridgeUnitLink> linksAcross = createBridgeUnitLinks(bridgeUnitsAcross);
        createBridgeLinkJoint(bridgeUnitsAcross, linksAcross);
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
        for(int i = 0; i < unitsAcross.size() - 1; i++){
            BridgeUnit unit = unitsAcross.get(i);
            BridgeUnitLink link = new BridgeUnitLink();
            link.CreateVertex(img2, world, unit.getBody().getPosition().x + BridgeUnit.WIDTH/2, testCliffs.getSpriteLeft().getY() + testCliffs.getSpriteLeft().getHeight());
            linksAcross.add(link);
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
            joint.getrJointDef().localAnchorB.set(1, 1);
            world.createJoint(joint.getrJointDef());
            joint2.CreateJoint(unitsAcross.get(i+1).getBody(), linksAcross.get(i).getBody());
            joint2.getrJointDef().localAnchorB.set(1, 1);
            world.createJoint(joint2.getrJointDef());
        }
    }


    @Override
    public void show() {

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

    @Override
    public void hide() {

    }
    public void burnWood(float x, float y){
        testOnClick = true;
        fireEffect.getEmitters().first().setPosition(x,y);
        fireEffect.start();
    }

}
