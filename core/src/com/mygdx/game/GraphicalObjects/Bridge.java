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
            BridgeUnit unit = new BridgeUnit(img, world, cliffs.getSpriteLeft().getX() + leftCliffWidth + (i * BridgeUnit.WIDTH), cliffs.getSpriteLeft().getY() + leftCliffHeight);
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
        float distanceBetweenCliffs =  (rightCliffX - rightCliffWidth/2) -  (leftCliffX + leftCliffWidth/2);
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
        createPillar(bridgeUnitsPillarLeft, linkLeft);
        createPillar(bridgeUnitsPillarRight, linkRight);

        BridgeUnitLink newLinkLeft = createLinkPillars(bridgeUnitsPillarLeft);
        createJointsPillars(linksAcross.get(0), bridgeUnitsPillarLeft, newLinkLeft);
        bridgeUnitsLinksPillarLeft.add(newLinkLeft);


        BridgeUnitLink newLinkRight = createLinkPillars(bridgeUnitsPillarRight);
        createJointsPillars(linksAcross.get(linksAcross.size() - 1), bridgeUnitsPillarRight, newLinkRight);
        bridgeUnitsLinksPillarRight.add(newLinkLeft);


    }

    private void createCable(ArrayList<BridgeUnit> unitsAcross, ArrayList<BridgeUnit> pillarLeft, ArrayList<BridgeUnit> pillarRight) {
        Cable mainCable = new Cable(world, pillarLeft.get(1), pillarRight.get(1),pillarLeft.get(1).getSprite().getX(), pillarRight.get(1).getSprite().getX(), "lowerRight", "upperRight" );
        Cable leftCable = new Cable(world, unitsAcross.get(0), pillarLeft.get(1), (unitsAcross.get(0).getBody().getPosition().x - BridgeUnit.WIDTH / 2),(unitsAcross.get(1).getBody().getPosition().x + BridgeUnit.WIDTH / 2), "upperLeft", "upperRight" );

        Cable rightCable = new Cable(world, pillarRight.get(1), unitsAcross.get(unitsAcross.size()-1),  unitsAcross.get(unitsAcross.size()-2).getBody().getPosition().x -  BridgeUnit.WIDTH / 2 ,(unitsAcross.get(unitsAcross.size()-1).getBody().getPosition().x + BridgeUnit.WIDTH / 2), "lowerRight", "upperRight" );
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
        BridgeUnit left1 = new BridgeUnit(img, world, linkLeft.getX(), linkLeft.getY());

        left1.getBody().setTransform(linkLeft.getX(), linkLeft.getY() + BridgeUnit.WIDTH / 2, MathUtils.PI / 2);
        stage.addActor(left1);
        pillarUnits.add(left1);
        BridgeUnit left2 = new BridgeUnit(img, world, linkLeft.getX(), linkLeft.getY());
        left2.getBody().setTransform(left1.getSprite().getX(), left1.getSprite().getY() + BridgeUnit.WIDTH * 1.5f, MathUtils.PI / 2);
        stage.addActor(left2);
        pillarUnits.add(left2);

    }

    /**
     * This method creates the link between the 2 units in the pillar
     * @param pillarUnits
     * @return
     */
    private BridgeUnitLink createLinkPillars(ArrayList<BridgeUnit> pillarUnits){
        BridgeUnit unit = pillarUnits.get(0);
        BridgeUnitLink link = new BridgeUnitLink(img2, world, unit.getSprite().getX(), unit.getSprite().getY() + BridgeUnit.WIDTH);
        stage.addActor(link);

        return link;
    }

    /**
     * this method creates the joint between the links and the bridge units of the pillars
     * @param linkBottom this is the link between at the base of the pillar
     * @param pillarUnits this is the array with the bridge units of the pillar
     * @param linkMiddle this is the link between the two bridge units of the pillar
     */
    private void createJointsPillars(BridgeUnitLink linkBottom, ArrayList<BridgeUnit> pillarUnits, BridgeUnitLink linkMiddle){
        Body linkBody = linkBottom.getBody();
        Body unitBody = pillarUnits.get(0).getBody();

        //joint between link at the base and first bridge unit of the pillar
        BridgeJoint joint = new BridgeJoint();
        joint.CreateJoint(unitBody, linkBody);
        joint.getrJointDef().localAnchorA.set(-BridgeUnit.WIDTH / 2, 0);
        world.createJoint(joint.getrJointDef());
        System.out.println("joints linkbody: " + linkBody.getJointList().size);

        //joint between first unit of the pillar and link in the middle
        Body linkBody2 = linkMiddle.getBody();
        BridgeJoint joint1 = new BridgeJoint();
        joint1.CreateJoint(unitBody, linkBody2);
        joint1.getrJointDef().localAnchorA.set(BridgeUnit.WIDTH / 2, 0);
        world.createJoint(joint1.getrJointDef());

        //joint between second unit of the pillar and link in the middle
        Body unitBody2 = pillarUnits.get(1).getBody();
        BridgeJoint joint3 = new BridgeJoint();
        joint3.CreateJoint(unitBody2, linkBody2);
        joint3.getrJointDef().localAnchorA.set(-BridgeUnit.WIDTH / 2, 0);
        world.createJoint(joint3.getrJointDef());
    }


    public ArrayList<BridgeUnit> getBridgeUnits(){

        ArrayList<BridgeUnit> bridgeUnits = new ArrayList<BridgeUnit>();
        bridgeUnits.addAll(bridgeUnitsAcross);
        bridgeUnits.addAll(bridgeUnitsPillarLeft);
        bridgeUnits.addAll(bridgeUnitsPillarRight);

        return bridgeUnits;
    }




}
