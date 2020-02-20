package org.lordsofchaos.gameobjects;

import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;

public abstract class GameObject implements Comparable {
    protected String spriteName;
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
    //

    public void initialise() {

    }

    public void remove() {

    }

    @Override
    public int compareTo(Object o) {
        GameObject gameObject = (GameObject) o;
        int thisSum = realWorldCoordinates.getX() + realWorldCoordinates.getY();
        int otherSum = gameObject.realWorldCoordinates.getX() + gameObject.realWorldCoordinates.getY();
        return thisSum < otherSum ? 1 : thisSum > otherSum ? -1 : 0;
    }

}

