package com.mygdx.game.GraphicalObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;


import java.util.ArrayList;

/**
 * Created by Eloa on 17/03/2016.
 */
public class Bridge {

    private ArrayList<BridgeUnit> bridgeUnits;
    private Texture img;
    private Texture img2;
    private World world;
    private Stage stage;
    private int initialX;
    private int initialY;
    private int finalX;



    public Bridge(World world, Stage stage, int intialX, int intialY, int finalX){
        bridgeUnits = new ArrayList<BridgeUnit>();
        img = new Texture("Wood.png");
        this.world = world;
        this.stage = stage;
        this.initialX = intialX;
        this.initialY = intialY;
        this.finalX = finalX;
        buildUnitsAcrossCliffs();

    }

    private void createBridgeUnit(){
        BridgeUnit unit = new BridgeUnit(img, world, 400, 200);
        stage.addActor(unit);
    }

    private void buildUnitsAcrossCliffs() {

        float numberOfBridgeUnits = getNumberOfBridgeUnits();
        ArrayList<BridgeUnit> bridgeUnitsAcross = new ArrayList<BridgeUnit>();

        for (int i = 0; i < numberOfBridgeUnits; i++) {
            BridgeUnit newUnit = new BridgeUnit();
            bridgeUnitsAcross.add(newUnit);
        }
        int i = 0;
        for (BridgeUnit unit : bridgeUnitsAcross) {
            unit.CreateTestBridge(img, world, initialX + (i * BridgeUnit.WIDTH), initialY);
            stage.addActor(unit);
            i++;
        }
    }
    /**
     * This calculates the number of bridge units necessary to connect both cliffs
     * @return
     */

    private float getNumberOfBridgeUnits() {
        float distanceBetweenCliffs = finalX - initialX;
        float numberOfBridgeUnits = distanceBetweenCliffs / BridgeUnit.WIDTH;
        //This if statement checks if the number of bridge units calculated above is enough to cover the distance. If it is not it add one more board.
        if((numberOfBridgeUnits * BridgeUnit.WIDTH) < distanceBetweenCliffs){
            numberOfBridgeUnits += 1;
        }
        return numberOfBridgeUnits;
    }

    private ArrayList<BridgeUnitLink> createBridgeUnitLinks(ArrayList<BridgeUnit> unitsAcross){
        ArrayList<BridgeUnitLink> linksAcross = new ArrayList<BridgeUnitLink>();
        //for every unit in the array of Bridge units it creates a link and adds it to the array of links.
        // It does not create a new link to the last  unit in the array of units.
        for(int i = 0; i < unitsAcross.size()-1; i++){
            BridgeUnit unit = unitsAcross.get(i);
            BridgeUnitLink link = new BridgeUnitLink();
            //link.CreateVertex(img2, world, unit.getBody().getPosition().x + BridgeUnit.WIDTH / 2, testCliffs.getSpriteLeft().getY() + testCliffs.getSpriteLeft().getHeight() + BridgeUnit.HEIGHT/2);
            linksAcross.add(link);
            stage.addActor(link);

        }

        return linksAcross;
    }


}
