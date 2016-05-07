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
    private Sprite splash;
    private SpriteBatch batch;
    private Stage stage;

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
        batch = new SpriteBatch();
        Texture splashTexture = new Texture("newSplashFixed.jpg");
        splash = new Sprite(splashTexture);
        splash.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage();



        startButton = new StartButton();
        startButton.setPosition((stage.getWidth()/2) - startButton.getWidth()/2, (stage.getHeight()/4)-startButton.getHeight());

        stage.addActor(startButton);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);// Make the stage consume events
    }

    @Override
    public void render(float delta) {
        //Creates the background image

        //Background image drawn
        batch.begin();
        splash.draw(batch);
        batch.end();

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