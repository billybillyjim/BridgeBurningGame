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


public class MainGame implements Screen{

    private final GameLauncher game;
    public final int METER_TO_PIXELS = 50; //50 pixels per each meter


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
        camera.setToOrtho(false, 800, 480);




        //Makes a box2d physics environment that sets gravity
        world = new World(new Vector2(0, -981f), true);
        box2DDebugRenderer = new Box2DDebugRenderer();
        bodiesInTheWorld = new Array<Body>();
        bridgeUnit = new BridgeUnit();

        /*
        bridgeUnit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("DONE CLICKED IT YOU DID");
                burnWood(x,y);
            }
        });
        */
        bridgeUnit.CreateTestBridge(img,world);



        testPivot = new BridgeUnitLink();
        testPivot.CreateVertex(img2, world);

        // ALL JOINT STUFF
        bridgeJoint = new BridgeJoint();
        bridgeJoint.CreateJoint(bridgeUnit.getBody(), testPivot.getBody());

        world.createJoint(bridgeJoint.getrJointDef());


        testCliffs = new BackgroundCliffs();
        testCliffs.CreateCliffs(img3, img4, world);


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
        game.font.draw(game.batch, Float.toString(bridgeUnit.getBody().getPosition().y), 200,200);
        game.font.draw(game.batch, Boolean.toString(testOnClick), 200, 100);
        game.font.draw(game.batch, "Bridge sprite width: " + Float.toString(bridgeUnit.getSprite().getWidth()) + "\n height: " + Float.toString(bridgeUnit.getSprite().getHeight()) , 250, 250);
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
