package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sun.prism.shader.AlphaOne_Color_Loader;


/**
 * Created by Eloà on 26/02/2016.
 * tutorial from 'http://www.tempoparalelo.com/blog/?p=76' last retrieved 3/22/2016
 */
public class MainMenuScreen implements Screen {

    final GameLauncher game;
    private OrthographicCamera camera;
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
        //Creates the background image
        batch = new SpriteBatch();
        Texture splashTexture = new Texture(Gdx.files.internal("img/splash.jpg"));
        splash = new Sprite(splashTexture);
        splash.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Stage stage = new Stage();
        Table menuTable = new Table();
        Gdx.input.setInputProcessor(stage);// Make the stage consume events
        createSkins();

        //Create "START GAME" button
        TextButton startGameButton = new TextButton("START GAME", simpleSkin);
        menuTable.add(startGameButton).expand().bottom();
        menuTable.row();
        //Create "About" button
        TextButton aboutGameButton = new TextButton("ABOUT", aboutSkin);
        menuTable.add(aboutGameButton).width(100);


        menuTable.setFillParent(true);
        stage.addActor(menuTable);

        Gdx.input.setInputProcessor(stage);

        //Background image drawn
        batch.begin();
        splash.draw(batch);
        batch.end();

        stage.act();
        stage.draw();


        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainGame(game));
                dispose();
            }
        });
    }

    private void createSkins(){
        //Font of the simpleSkin
        BitmapFont font = new BitmapFont();
        simpleSkin = new Skin();
        simpleSkin.add("default",font);

        //Texture of the simpleSkin
        Pixmap pixmap = new Pixmap((int)Gdx.graphics.getWidth()/6,(int)Gdx.graphics.getHeight()/10, Pixmap.Format.Alpha);
        pixmap.setColor(Color.CYAN);
        simpleSkin.add("background",new Texture(pixmap));

        //Style of the simpleSkin
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = simpleSkin.newDrawable("background", Color.GRAY);
        textButtonStyle.down = simpleSkin.newDrawable("background", Color.DARK_GRAY);
        textButtonStyle.checked = simpleSkin.newDrawable("background", Color.DARK_GRAY);
        textButtonStyle.over = simpleSkin.newDrawable("background", Color.LIGHT_GRAY);
        textButtonStyle.font = simpleSkin.getFont("default");
        textButtonStyle.fontColor = Color.BLACK;
        simpleSkin.add("default", textButtonStyle);

        //About button skin
        BitmapFont font2 = new BitmapFont();
        aboutSkin = new Skin();
        aboutSkin.add("default",font);

        //About texture
        Pixmap pixmap2 = new Pixmap((int)Gdx.graphics.getWidth()/6,(int)Gdx.graphics.getHeight()/10, Pixmap.Format.Alpha);
        pixmap2.setColor(Color.CYAN);
        aboutSkin.add("background",new Texture(pixmap2));

        //Style of aboutSkin
        TextButton.TextButtonStyle textButtonStyle2 = new TextButton.TextButtonStyle();
        textButtonStyle2.up = aboutSkin.newDrawable("background", Color.BLUE);
        textButtonStyle2.down = aboutSkin.newDrawable("background", Color.BROWN);
        textButtonStyle2.checked = aboutSkin.newDrawable("background", Color.FIREBRICK);
        textButtonStyle2.over = aboutSkin.newDrawable("background", Color.LIGHT_GRAY);
        textButtonStyle2.font = aboutSkin.getFont("default");
        textButtonStyle2.fontColor = Color.BLACK;
        aboutSkin.add("default", textButtonStyle2);


        pixmap.dispose();
        pixmap2.dispose();
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