package com.mygdx.game.GraphicalObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Eloa on 03/05/2016.
 */
public class HelpButton extends Actor{
    private Texture texture;
    private Sprite buttonSprite;
    private boolean clicked;
    private Sprite helpSprite;
    private Texture helpTexture;

    public HelpButton() {
        texture = new Texture("help.png");
        this.setName("Help");
        helpTexture = new Texture("Instructions Balloon.png");



        buttonSprite = new Sprite(texture);


        setWidth(buttonSprite.getWidth());
        setHeight(buttonSprite.getHeight());
        clicked = false;


    }

    @Override
    public void draw(Batch batch, float ParentAlpha){
        batch.draw(buttonSprite, getX(), getY());
        if(clicked) {
            helpSprite = new Sprite(helpTexture);
            helpSprite.setSize(383, 244);
            helpSprite.setPosition(getX(), getY() - helpSprite.getHeight()/1.3f);
            helpSprite.draw(batch);
            toFront();

        }
    }

    public Sprite getSprite() {
        return buttonSprite;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }
}
