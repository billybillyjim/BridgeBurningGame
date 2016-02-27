package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Created by Eloá on 26/02/2016.
 */
public class MainMenuScreen implements Screen {

    final GameLauncher game;
    private OrthographicCamera camera;

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
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        //uses BitmapFont and SpriteBatch that where created in the main class Drop;
        game.batch.begin();
        game.font.draw(game.batch, "Welcome to the Burn Bridge game!!", 100, 150);
        game.font.draw(game.batch, "Tap Anywhere to begin", 100, 100);
        game.batch.end();

        if(Gdx.input.isTouched()){
            game.setScreen(new Test(game));
            dispose();
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
