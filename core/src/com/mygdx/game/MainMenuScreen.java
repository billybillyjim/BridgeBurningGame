package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.GraphicalObjects.StartButton;


/**
 * Created by Eloà on 26/02/2016.
 * tutorial from 'http://www.tempoparalelo.com/blog/?p=76' last retrieved 3/22/2016
 */
public class MainMenuScreen implements Screen {

    final GameLauncher game;
    private OrthographicCamera camera;
    private StartButton startButton;
    public Skin simpleSkin;
    public Skin aboutSkin;
    private Sprite splash;
    private SpriteBatch batch;

    /**
     *  The only parameter for the constructor necessary
     *  for this game is an instance of Drop, so that we
     *  can call upon its methods and fields if necessary.
     * @param game
     */

    public MainMenuScreen(final GameLauncher game){
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //Creates the background image
        batch = new SpriteBatch();
        Texture splashTexture = new Texture("splash.jpg");
        splash = new Sprite(splashTexture);
        splash.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //Background image drawn
        batch.begin();
        splash.draw(batch);
        batch.end();

        //Gdx.gl.glClearColor(0, 0, 0, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Stage stage = new Stage();
        Gdx.input.setInputProcessor(stage);// Make the stage consume events


        startButton = new StartButton(game);
        stage.addActor(startButton);

        stage.act();
        stage.draw();

        if (Gdx.input.justTouched()) {
            Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(pos);
            Actor actor = stage.hit(pos.x, pos.y, true);
            if (actor != null && actor.getName().equals("Start")) {
                game.setScreen(new MainGame(game));
                this.dispose();
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

}