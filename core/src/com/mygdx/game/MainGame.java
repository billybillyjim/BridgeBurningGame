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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.GraphicalObjects.*;

public class MainGame extends Stage implements Screen{

    private final GameLauncher game;
    public final int METER_TO_PIXELS = 50; //50 pixels per each meter
    private final int SCREEN_WIDTH = 800;
    private final int SCREEN_HEIGHT = 480;


    private ParticleEffect fireEffect;


    private Texture img3;
    private Texture img4;
    //Makes a box2d physics environment that sets gravity
    public final static World WORLD = new World(new Vector2(0, -9.81f), true);

    private BackgroundCliffs cliffs;

    private Box2DDebugRenderer box2DDebugRenderer;

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
        fireEffect.update(Gdx.graphics.getDeltaTime());

        //draws the actual frame
        game.batch.begin();

        fireEffect.draw(game.batch);

        game.batch.end();

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

        for(int i = 0; i < fireEffect.getEmitters().size; i++) {
            fireEffect.getEmitters().get(i).setPosition(x,y);
        }
        //fireEffect.getEmitters().first().setPosition(x,y);
        fireEffect.start();
    }

}