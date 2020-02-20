package org.lordsofchaos.gameobjects;

import com.badlogic.gdx.graphics.g2d.Sprite;

import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;

public abstract class GameObject implements Comparable<GameObject> {
    protected String spriteName;
    protected Sprite sprite;
    protected RealWorldCoordinates realWorldCoordinates;

    public GameObject(String spriteName, RealWorldCoordinates rwc) {
        setSpriteName(spriteName);
        setRealWorldCoordinates(rwc);
    }

    // Getters and Setters
    public void setSpriteName(String spriteName) {
        this.spriteName = spriteName;
    }

    public String getSpriteName() { 
        return spriteName;
    }

    public void setRealWorldCoordinates(RealWorldCoordinates rwc) {
        realWorldCoordinates = rwc;
    }

    public RealWorldCoordinates getRealWorldCoordinates() {
        return realWorldCoordinates;
    }

    public void initialise() {

    }

    public void remove() {

    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public int compareTo(GameObject o) {
        int thisSum = realWorldCoordinates.getX() + realWorldCoordinates.getY();
        int otherSum = o.realWorldCoordinates.getX() + o.realWorldCoordinates.getY();
        return thisSum < otherSum ? 1 : thisSum > otherSum ? -1 : 0;
    }

}

