package com.mygdx.game.GraphicalObjects;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.MainGame;

/**
 * Created by Eloise on 4/18/16.
 */

public class RefreshButton extends Actor{
    private Texture buttonTex;
    private Sprite buttonSprite;

    public RefreshButton(Texture tex, World world) {
        this.setName("Refresh");
        buttonTex = tex;

        buttonSprite = new Sprite(buttonTex);

        setPosition(20, 460-buttonSprite.getWidth());

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
