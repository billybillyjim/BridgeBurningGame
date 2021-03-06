package com.mygdx.game.GraphicalObjects;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Eloise on 4/18/16.
 */

public class RefreshButton extends Actor{
    private Sprite buttonSprite;
    private Texture texture;

    public RefreshButton() {
        texture = new Texture("Refresh.png");
        this.setName("Refresh");


        buttonSprite = new Sprite(texture);

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
