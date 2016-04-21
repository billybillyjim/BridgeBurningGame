package com.mygdx.game.GraphicalObjects;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
/**
 * Created by Eloise on 4/18/16.
 */

public class RefreshButton extends Actor{
    private Body buttonBody;
    private Texture buttonTex;
    private Sprite buttonSprite;

    public RefreshButton(Texture tex, World world) {
        buttonTex = tex;

        buttonSprite = new Sprite(buttonTex);

        buttonSprite.setPosition(20, 460-buttonSprite.getWidth());

        BodyDef bodyDef1 = new BodyDef();
        bodyDef1.type = BodyDef.BodyType.StaticBody;

        bodyDef1.position.set(buttonSprite.getX()+buttonSprite.getWidth()/2, buttonSprite.getY()+buttonSprite.getHeight()/2);
        //TODO: make less gross ^^

        buttonBody = world.createBody(bodyDef1);

        PolygonShape shape1 = new PolygonShape();
        shape1.setAsBox(buttonSprite.getWidth()/2, buttonSprite.getHeight()/2);

        //Describes the properties of the fixture
        FixtureDef fixtureDef1 = new FixtureDef();
        fixtureDef1.shape = shape1;
        fixtureDef1.density = 1f;

        Fixture fixture1 = buttonBody.createFixture(fixtureDef1);

        shape1.dispose();
    }

    @Override
    public void draw(Batch batch, float ParentAlpha){
        batch.draw(buttonSprite, buttonSprite.getX(), buttonSprite.getY());
    }

    public Body getBody(){return this.buttonBody;}

    public Sprite getSprite() {
        return buttonSprite;
    }

}
