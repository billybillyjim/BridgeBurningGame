package com.mygdx.game.GraphicalObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.game.PhysicalObjects.BridgeJoint;


import java.util.ArrayList;

/**
 * Created by Eloa on 17/03/2016.
 */
public class Bridge extends Actor {

    public final int PILLAR_HEIGHT = 200;

    private ArrayList<BridgeUnit> bridgeUnitsAcross;
    private ArrayList<BridgeUnit> bridgeUnitsPillarLeft;
    private ArrayList<BridgeUnit> bridgeUnitsPillarRight;

    private ArrayList<BridgeUnitLink> bridgeUnitsLinksAcross;
    private ArrayList<BridgeUnitLink> bridgeUnitsLinksPillarLeft;
    private ArrayList<BridgeUnitLink> bridgeUnitsLinksPillarRight;


    private final Texture img = new Texture("Wood.png");;
    private final Texture img2 = new Texture("Pivot.png");
    private World world;
    private Stage stage;
    private BackgroundCliffs cliffs;

    private float distanceBetweenCliffs;


    //TODO create formula to calculate over which unit/link should the pillar stand


    public Bridge(World world, Stage stage, BackgroundCliffs cliffs){
        bridgeUnitsAcross = new ArrayList<BridgeUnit>();
        this.world = world;
        this.stage = stage;
        this.cliffs = cliffs;
        buildUnitsAcrossCliffs();
        bridgeUnitsLinksAcross = createBridgeUnitLinks(bridgeUnitsAcross);
        createBridgeLinkJoint(bridgeUnitsAcross, bridgeUnitsLinksAcross);
        createBridgeUnitPillars(bridgeUnitsLinksAcross, bridgeUnitsAcross);
        createCable(bridgeUnitsAcross, bridgeUnitsPillarLeft, bridgeUnitsPillarRight);

    }


    private void buildUnitsAcrossCliffs() {
        float leftCliffWidth =  cliffs.getSpriteLeft().getWidth();
        float leftCliffHeight = cliffs.getSpriteLeft().getHeight();
        float rightCliffWidth = cliffs.getSpriteRight().getWidth();

        float numberOfBridgeUnits = getNumberOfBridgeUnits(leftCliffWidth, rightCliffWidth, cliffs.getSpriteLeft().getX(), cliffs.getSpriteRight().getX());
        System.out.println("cliff width " + leftCliffWidth + " cliff Height " + leftCliffHeight);
        for (int i = 0; i < numberOfBridgeUnits; i++) {
            BridgeUnit unit = new BridgeUnit(img, world, cliffs.getSpriteLeft().getX() + leftCliffWidth * 0.8f + (i * BridgeUnit.WIDTH), cliffs.getSpriteLeft().getY() + leftCliffHeight);
            bridgeUnitsAcross.add(unit);
            stage.addActor(unit);

        }
    }

    /**
     * This calculates the number of bridge units necessary to connect both cliffs
     * @param leftCliffWidth
     * @param rightCliffWidth
     * @return
     */

    private float getNumberOfBridgeUnits(float leftCliffWidth, float rightCliffWidth, float leftCliffX, float rightCliffX) {
        distanceBetweenCliffs =  (rightCliffX - rightCliffWidth * 0.4f) -  (leftCliffX + leftCliffWidth * 0.4f) + BridgeUnit.WIDTH;
        float numberOfBridgeUnits = distanceBetweenCliffs / BridgeUnit.WIDTH;
        //This if statement checks if the number of bridge units calculated above is enough to cover the distance. If it is not it add one more board.
        if((numberOfBridgeUnits * BridgeUnit.WIDTH) < distanceBetweenCliffs){
            numberOfBridgeUnits += 1;
        }
        return numberOfBridgeUnits;
    }

    /**
     * Takes an array of bridge units and creates a array of bridge unit links
     * @param unitsAcross as array of Bridge units
     * @return an array of bridge unit links
     */
    private ArrayList<BridgeUnitLink> createBridgeUnitLinks(ArrayList<BridgeUnit> unitsAcross){
        ArrayList<BridgeUnitLink> linksAcross = new ArrayList<BridgeUnitLink>();
        //for every unit in the array of Bridge units it creates a link and adds it to the array of links.
        // It does not create a new link to the last  unit in the array of units.
        for(int i = 0; i < unitsAcross.size()-1; i++){
            BridgeUnit unit = unitsAcross.get(i);
            BridgeUnitLink link = new BridgeUnitLink(img2, world, unit.getBody().getPosition().x + BridgeUnit.WIDTH / 2, cliffs.getSpriteLeft().getY() + cliffs.getSpriteLeft().getHeight() + BridgeUnit.HEIGHT / 2);
            linksAcross.add(link);
            stage.addActor(link);
        }

        return linksAcross;
    }

    /**
     * Creates a joint between the links and the units. This still needs a lot of work.
     * @param unitsAcross
     * @param linksAcross
     */
    private void createBridgeLinkJoint(ArrayList<BridgeUnit> unitsAcross, ArrayList<BridgeUnitLink> linksAcross){
        for(int i = 0; i < linksAcross.size(); i++){
            BridgeJoint joint = new BridgeJoint();
            BridgeJoint joint2 = new BridgeJoint();
            joint.CreateJoint(unitsAcross.get(i).getBody(), linksAcross.get(i).getBody());
            joint.getrJointDef().localAnchorA.set(BridgeUnit.WIDTH / 2, 0);
            world.createJoint(joint.getrJointDef());
            joint2.CreateJoint(unitsAcross.get(i + 1).getBody(), linksAcross.get(i).getBody());
            joint2.getrJointDef().localAnchorA.set(-BridgeUnit.WIDTH / 2, 0);
            world.createJoint(joint2.getrJointDef());

        }
    }

    /**
     * method responsible for organinizing and calling all  methods to create the pillars of the bridges
     * @param linksAcross
     */
    private void createBridgeUnitPillars(ArrayList<BridgeUnitLink> linksAcross, ArrayList<BridgeUnit> unitsAcross){
        bridgeUnitsPillarLeft = new ArrayList<BridgeUnit>(); //pillar of the left
        bridgeUnitsPillarRight = new ArrayList<BridgeUnit>();   //pillar of the right
        bridgeUnitsLinksPillarLeft = new ArrayList<BridgeUnitLink>();
        bridgeUnitsLinksPillarRight  = new ArrayList<BridgeUnitLink>();


        Sprite linkLeft = linksAcross.get(0).getSprite(); //the left pillar is created in the x location of the first unit that is across the cliff
        System.out.println("leftUnit from createBridgeUnitPillars x " + linkLeft.getX());
        Sprite linkRight = linksAcross.get(linksAcross.size()-1).getSprite(); //the right pillar is created in the x location of the last unit that is across the cliff

        int indexOfLinkBase = findBridgeUnitLinkBaseOfPillar();
        createPillar(bridgeUnitsPillarLeft, linksAcross.get(indexOfLinkBase).getSprite() );
        createPillar(bridgeUnitsPillarRight, linksAcross.get(linksAcross.size()-indexOfLinkBase).getSprite());

        createLinkPillars(bridgeUnitsPillarLeft, bridgeUnitsLinksPillarLeft);
        createJointsPillars(linksAcross.get(indexOfLinkBase), bridgeUnitsPillarLeft, bridgeUnitsLinksPillarLeft);


        createLinkPillars(bridgeUnitsPillarRight, bridgeUnitsLinksPillarRight);
        createJointsPillars(linksAcross.get(linksAcross.size()-indexOfLinkBase), bridgeUnitsPillarRight, bridgeUnitsLinksPillarRight);



    }

    private void createCable(ArrayList<BridgeUnit> unitsAcross, ArrayList<BridgeUnit> pillarLeft, ArrayList<BridgeUnit> pillarRight) {
        Cable mainCable = new Cable(world, pillarLeft.get(pillarLeft.size()-1), pillarRight.get(pillarLeft.size()-1), "lowerRight", "upperRight", 1 );
        Cable leftCable = new Cable(world, unitsAcross.get(0), pillarLeft.get(pillarLeft.size()-1), "upperLeft", "upperRight", 0 );
        Cable rightCable = new Cable(world, pillarRight.get(pillarLeft.size()-1), unitsAcross.get(unitsAcross.size()-1), "lowerRight", "upperRight", 0 );
        stage.addActor(mainCable);
        stage.addActor(leftCable);
        stage.addActor(rightCable);
    }

    /**
     * this method actually creates a pillar
     * @param pillarUnits
     * @param linkLeft
     */

    private void createPillar(ArrayList<BridgeUnit> pillarUnits, Sprite linkLeft) {
        int numberOfUnitsInPillar = PILLAR_HEIGHT/BridgeUnit.WIDTH;
        System.out.println("num of pillars = " + numberOfUnitsInPillar);
        for(int i = 0; i < numberOfUnitsInPillar; i++){
            System.out.println("num of pillars = " + numberOfUnitsInPillar + ": " + i);
            BridgeUnit left = new BridgeUnit(img, world, linkLeft.getX(), linkLeft.getY());
            left.getBody().setTransform(linkLeft.getX(), linkLeft.getY() + BridgeUnit.WIDTH * i, MathUtils.PI / 2);
            pillarUnits.add(left);
            stage.addActor(left);
        }


    }

    private int findBridgeUnitLinkBaseOfPillar(){
        int unitLocatedAt = (int) 60 / BridgeUnit.WIDTH;
        if(unitLocatedAt == 0) unitLocatedAt = 1;


        return unitLocatedAt;
    }

    /**
     * This method creates the link between the 2 units in the pillar
     * @param pillarUnits
     * @return
     */
    private void createLinkPillars(ArrayList<BridgeUnit> pillarUnits, ArrayList<BridgeUnitLink> pillarLinks ){
        for(int i = 0; i < pillarUnits.size()-1; i++){
            BridgeUnit unit = pillarUnits.get(i);
            BridgeUnitLink link = new BridgeUnitLink(img2, world, unit.getBody().getPosition().x, unit.getBody().getPosition().y + BridgeUnit.WIDTH);
            link.changeBodyType();
            pillarLinks.add(link);
            stage.addActor(link);
        }

    }

    /**
     * this method creates the joint between the links and the bridge units of the pillars
     *
     * @param pillarUnits this is the array with the bridge units of the pillar
     * @param pillarLinks this is the link between the two bridge units of the pillar
     */
    private void createJointsPillars(BridgeUnitLink linkBottom, ArrayList<BridgeUnit> pillarUnits, ArrayList<BridgeUnitLink> pillarLinks){
        Body linkBody = linkBottom.getBody();
        Body unitBody = pillarUnits.get(0).getBody();

        //joint between link at the base and first bridge unit of the pillar
        BridgeJoint joint = new BridgeJoint();
        joint.CreateJoint(unitBody, linkBody);
        joint.getrJointDef().localAnchorA.set(-BridgeUnit.WIDTH / 2, 0);
        world.createJoint(joint.getrJointDef());

        for(int i = 0; i < pillarLinks.size(); i++){
            BridgeJoint joint2 = new BridgeJoint();
            BridgeJoint joint3 = new BridgeJoint();
            joint2.CreateJoint(pillarUnits.get(i).getBody(), pillarLinks.get(i).getBody());
            joint2.getrJointDef().localAnchorA.set(BridgeUnit.WIDTH / 2, 0);
            world.createJoint(joint2.getrJointDef());
            joint3.CreateJoint(pillarUnits.get(i + 1).getBody(), pillarLinks.get(i).getBody());
            joint3.getrJointDef().localAnchorA.set(-BridgeUnit.WIDTH / 2, 0);
            world.createJoint(joint3.getrJointDef());

        }
    }


    public ArrayList<BridgeUnit> getBridgeUnits(){

        ArrayList<BridgeUnit> bridgeUnits = new ArrayList<BridgeUnit>();
        bridgeUnits.addAll(bridgeUnitsAcross);
        bridgeUnits.addAll(bridgeUnitsPillarLeft);
        bridgeUnits.addAll(bridgeUnitsPillarRight);

        return bridgeUnits;
    }
    public ArrayList<BridgeUnitLink> getBridgeUnitLinks(){
        ArrayList<BridgeUnitLink> bridgeUnitLinks = new ArrayList<BridgeUnitLink>();
        bridgeUnitLinks.addAll(bridgeUnitsLinksAcross);
        bridgeUnitLinks.addAll(bridgeUnitsLinksPillarLeft);
        bridgeUnitLinks.addAll(bridgeUnitsLinksPillarRight);

        return bridgeUnitLinks;
    }



}
