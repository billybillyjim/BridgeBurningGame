package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;



/**
 * Created by Eloise on 4/8/16.
 */

public class EndGameScreen implements com.badlogic.gdx.Screen {
    final GameLauncher game;
    private OrthographicCamera camera;
    public Skin loserSkin;


    public EndGameScreen(final GameLauncher game){
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Stage stage = new Stage();
        Table menuTable = new Table();
        Gdx.input.setInputProcessor(stage);// Make the stage consume events
        createSkins();

        TextButton loserButton = new TextButton("YOU LOST :(", loserSkin);
        menuTable.add(loserButton);
        menuTable.row();

        TextButton newGameButton = new TextButton("New Game", loserSkin);
        menuTable.add(newGameButton);
        menuTable.row();

        menuTable.setFillParent(true);
        stage.addActor(menuTable);

        Gdx.gl.glClearColor(1, 2, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.input.setInputProcessor(stage);

        stage.act();
        stage.draw();

        newGameButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                MainGame g = new MainGame(game);
                game.setScreen(g);
                g.reset();
                dispose();
            }
        });


    }

    private void createSkins(){
        //Font of the simpleSkin
        BitmapFont font = new BitmapFont();
        loserSkin = new Skin();
        loserSkin.add("default",font);

        //Texture of the simpleSkin
        Pixmap pixmap = new Pixmap((int) Gdx.graphics.getWidth()/6,(int)Gdx.graphics.getHeight()/10, Pixmap.Format.RGB888);
        pixmap.setColor(Color.RED);
        pixmap.fill();
        loserSkin.add("background",new Texture(pixmap));

        //Style of the simpleSkin
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = loserSkin.newDrawable("background", Color.GRAY);
        textButtonStyle.down = loserSkin.newDrawable("background", Color.DARK_GRAY);
        textButtonStyle.checked = loserSkin.newDrawable("background", Color.DARK_GRAY);
        textButtonStyle.over = loserSkin.newDrawable("background", Color.LIGHT_GRAY);
        textButtonStyle.font = loserSkin.getFont("default");
        loserSkin.add("default", textButtonStyle);

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

