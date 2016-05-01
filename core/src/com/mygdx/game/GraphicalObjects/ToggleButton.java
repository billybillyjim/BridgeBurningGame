package com.mygdx.game.GraphicalObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Eloise on 4/29/16.
 */
public class ToggleButton extends Actor {
    private Texture buttonTex;
    private Sprite buttonSprite;

    public ToggleButton(Texture tex) {
        this.setName("Toggle");
        buttonTex = tex;

        buttonSprite = new Sprite(buttonTex);

        setPosition(780 - buttonSprite.getWidth(), 460 - buttonSprite.getHeight());

        setWidth(buttonSprite.getWidth());
        setHeight(buttonSprite.getHeight());

    }

    @Override
    public void draw(Batch batch, float ParentAlpha) {
        batch.draw(buttonSprite, getX(), getY());
    }

    public Sprite getSprite() {
        return buttonSprite;
    }

    public void changeTexture(Texture tex){
        buttonSprite.setTexture(tex);
    }
}


