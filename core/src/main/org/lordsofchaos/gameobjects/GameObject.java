package org.lordsofchaos.gameobjects;

import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;

public abstract class GameObject implements Comparable<GameObject>
{
    protected String spriteName;
    protected RealWorldCoordinates realWorldCoordinates;
    
    public GameObject(String spriteName, RealWorldCoordinates rwc) {
        setSpriteName(spriteName);
        setRealWorldCoordinates(rwc);
    }
    
    public String getSpriteName() {
        return spriteName;
    }
    
    public void setSpriteName(String spriteName) {
        this.spriteName = spriteName;
    }
    
    public RealWorldCoordinates getRealWorldCoordinates() {
        return realWorldCoordinates;
    }
    
    public void setRealWorldCoordinates(RealWorldCoordinates rwc) {
        realWorldCoordinates = rwc;
    }
    
    @Override
    public int compareTo(GameObject o) {
        int thisSum = realWorldCoordinates.getX() + realWorldCoordinates.getY();
        int otherSum = o.realWorldCoordinates.getX() + o.realWorldCoordinates.getY();
        return Integer.compare(otherSum, thisSum);
    }
    
}

