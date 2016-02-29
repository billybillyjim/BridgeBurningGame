package com.mygdx.game;

/**
 * Created by Luke on 2/12/2016.
 */


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;


public class Test implements Screen{

    final GameLauncher game;


    private ParticleEffect fireEffect;
    Sprite sprite;
    Texture img;
    Texture img2;
    Texture img3;
    Texture img4;
    World world;
    Body body;

    TestBridge testBridge;
    TestVertex testVertex;
    TestJoint testJoint;
    TestCliffs testCliffs;
    RevoluteJoint ourJoint;

    private Box2DDebugRenderer box2DDebugRenderer;


    private float bodyLocation;

    //camera
    private OrthographicCamera camera;



    public Test (GameLauncher game){

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
        
        testBridge = new TestBridge();
        testBridge.CreateTestBridge(img,world);

        testVertex = new TestVertex();
        testVertex.CreateVertex(img2,world);

        // ALL JOINT STUFF
        testJoint = new TestJoint();
        testJoint.CreateJoint(testBridge.getBody(), testVertex.getBody());

        ourJoint = (RevoluteJoint)world.createJoint(testJoint.rJointDef);


        testCliffs = new TestCliffs();
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
        box2DDebugRenderer.render(world, camera.combined);

        //Makes the box2d world play at a given frame rate
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);

        testBridge.sprite.setPosition(testBridge.body.getPosition().x, testBridge.body.getPosition().y);
        testVertex.sprite.setPosition(testVertex.body.getPosition().x, testVertex.body.getPosition().y);


        //sets the background color
        Gdx.gl.glClearColor(0.52f, 0.80f , 1 , 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update(); // is generally a good practice to update the camera once per frame
        box2DDebugRenderer.render(world, camera.combined);//let us sees the body's created my Box2D without beeing attached to a sprite.
        game.batch.setProjectionMatrix(camera.combined); //tells spriteBatch to use coordinate system set by camera
        //Makes the sprite follow the body


        testCliffs.spriteLeft.setPosition(testCliffs.bodyLeft.getPosition().x, testCliffs.bodyLeft.getPosition().y);
        testCliffs.spriteRight.setPosition(testCliffs.bodyRight.getPosition().x, testCliffs.bodyRight.getPosition().y);


        //makes the fire effect change every frame
        fireEffect.update(Gdx.graphics.getDeltaTime());
        //draws the actual frame
        game.batch.begin();
        game.batch.draw(testBridge.sprite, testBridge.sprite.getX(), testBridge.sprite.getY());
        game.batch.draw(testVertex.sprite, testVertex.sprite.getX(), testVertex.sprite.getY());
        game.batch.draw(testCliffs.spriteLeft, testCliffs.spriteLeft.getX(), testCliffs.spriteLeft.getY());
        game.batch.draw(testCliffs.spriteRight, testCliffs.spriteRight.getX(), testCliffs.spriteRight.getY());
        game.font.draw(game.batch, Float.toString(testBridge.body.getPosition().y), 200,200);
        game.font.draw(game.batch, "Bridge sprite width: " + Float.toString(testBridge.sprite.getWidth()) + "\n height: " + Float.toString(testBridge.sprite.getHeight()) , 250, 250);
        fireEffect.draw(game.batch);
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

}
