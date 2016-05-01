package com.mygdx.game.GraphicalObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.GameLauncher;

/**
 * Created by Eloise on 4/30/16.
 */
public class StartButton extends Actor{
    private Texture buttonTex;
    private Sprite buttonSprite;

    public StartButton(final GameLauncher game) {
        this.setName("Start");
        buttonTex = new Texture("StartButton.png");

        buttonSprite = new Sprite(buttonTex);

        setPosition((650-this.getWidth())/2, (100-this.getHeight())/2);

        setWidth(buttonSprite.getWidth());
        setHeight(buttonSprite.getHeight());

    }

    @Override
    public void draw(Batch batch, float ParentAlpha){
        batch.draw(buttonSprite, getX(), getY());
    }

    public Sprite getSprite() {
        return buttonSprite;
    }

    }


