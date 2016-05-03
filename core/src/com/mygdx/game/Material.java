package com.mygdx.game;

/**
 * Created by Eloá on 21/04/2016.
 */

import java.io.File;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.badlogic.gdx.Gdx;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Material {

    private String name;
    private String image_src;
    private int durability;
    private float density;
    private float friction;
    private int level;

    public Material(int level){
        try {

            InputStream paper = Gdx.files.internal("materials.xml").read();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(paper);
            doc.getDocumentElement().normalize();


            NodeList nodes = doc.getElementsByTagName("material");

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    if(level == Integer.parseInt(getValue("level", element))) {
                        name = getValue("name", element);
                        image_src = getValue("image_src", element);
                        durability = Integer.parseInt(getValue("durability", element));
                        density = Float.valueOf(getValue("density", element));
                        friction = Float.valueOf(getValue("friction", element));
                        this.level = Integer.parseInt(getValue("level", element));

                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        return node.getNodeValue();
    }

    public String getName() {
        return name;
    }



    public String getImage_src() {
        return image_src;
    }



    public int getDurability() {
        return durability;
    }


    public float getDensity() {
        return density;
    }


    public float getFriction() {
        return friction;
    }

    public int getLevel() {
        return level;
    }
}



