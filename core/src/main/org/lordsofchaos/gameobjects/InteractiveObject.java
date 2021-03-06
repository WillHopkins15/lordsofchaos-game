package org.lordsofchaos.gameobjects;

import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;

public abstract class InteractiveObject extends GameObject {

    protected int cost;
    protected int damage;

    public InteractiveObject(String spriteName, RealWorldCoordinates rwc, int cost, int damage) {
        super(spriteName, rwc);
        setCost(cost);
        setDamage(damage);
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

}
