package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Eloà on 26/02/2016.
 */
public class GameLauncher extends Game {
    // this is the main class that is called when the game is launched. DesktopLauncher, AndroidLauncher and IOSLauncher refer to this class
    // to launch games

    //The SpriteBatch object is used to render objects onto the screen, such as textures;
    // and the BitmapFont object is used, along with a SpriteBatch, to render text onto the screen.
    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();

        //Use LibGDX's default font
        font = new BitmapFont();
        this.setScreen(new MainMenuScreen(this));

    }
    public void render(){
        super.render(); //important, without the super the game does not work!
    }

    public void dispose(){
        batch.dispose();
        font.dispose();
    }

}






