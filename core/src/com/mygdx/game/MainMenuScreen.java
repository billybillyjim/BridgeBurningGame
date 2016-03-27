package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputListener;


/**
 * Created by Eloà on 26/02/2016.
 * tutorial from 'http://www.tempoparalelo.com/blog/?p=76' last retrieved 3/22/2016
 */
public class MainMenuScreen implements Screen {

    final GameLauncher game;
    private OrthographicCamera camera;
    private Skin skin;

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

    private void createSkin(){
        //Font of the skin
        BitmapFont font = new BitmapFont();
        skin = new Skin();
        skin.add("default", font);

        //Texture of the skin
        Pixmap pixmap = new Pixmap((int)Gdx.graphics.getWidth()/6,(int)Gdx.graphics.getHeight()/10, Pixmap.Format.RGB888);
        pixmap.setColor(Color.BLUE);
        pixmap.fill();
        skin.add("background",new Texture(pixmap));

        //Style of the skin
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("background", Color.GRAY);
        textButtonStyle.down = skin.newDrawable("background", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("background", Color.DARK_GRAY);
        textButtonStyle.over = skin.newDrawable("background", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Stage stage = new Stage();
        Table menuTable = new Table();
        Gdx.input.setInputProcessor(stage);// Make the stage consume events
        createSkin();

        //Create "START GAME" button with the skin
        TextButton startGameButton = new TextButton("START GAME", skin);
        menuTable.add(startGameButton);
        menuTable.row();

        //Create "OPTIONS" button
        //TextButton optionsButton = new TextButton("OPTIONS", skin);
        //menuTable.add(optionsButton);

        menuTable.setFillParent(true);
        stage.addActor(menuTable);

        Gdx.gl.glClearColor(1, 2, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.input.setInputProcessor(stage);


        startGameButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainGame(game));
                dispose();
            }
        });




        stage.act();
        stage.draw();
        if(Gdx.input.isTouched()){
            game.setScreen(new MainGame(game));
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
