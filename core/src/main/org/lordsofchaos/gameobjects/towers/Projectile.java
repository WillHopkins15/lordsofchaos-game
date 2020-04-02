package org.lordsofchaos.gameobjects.towers;

import org.lordsofchaos.GameController;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.GameObject;
import org.lordsofchaos.gameobjects.troops.Troop;

public class Projectile extends GameObject
{
    
    private static String spriteName = "UI/NewArtMaybe/projectile1.png";
    protected float movementSpeed = 80f;
    private Tower tower;
    private Troop targetTroop; // projectile will damage this troop when it gets to target location
    private RealWorldCoordinates targetPosition; // projectile flies towards this location
    
    public Projectile(RealWorldCoordinates rwc, Troop targetTroop, Tower tower) {
        super(spriteName, rwc);
        this.targetTroop = targetTroop;
        this.targetPosition = predictShot(targetTroop);
        this.tower = tower;
    }
    
    public Tower getTower() {
        return tower;
    }

    /**
     * Given a troop to shoot at, aim at the next tile it's going to move to
     *
     * @param targetTroop the troops this projectile has been shot at
     */
    private RealWorldCoordinates predictShot(Troop targetTroop) {
        // distance to target troop
        float dist = distance(getRealWorldCoordinates(), targetTroop.getRealWorldCoordinates());
        
        return new RealWorldCoordinates(targetTroop.getNextTile());
    }

    /**
     * This update function controls the movement of this projectile, scaled with deltaTime so that framerate does not affect gameplay
     *
     * @param deltaTime time for last frame to execute
     */
    public void update(float deltaTime) {
        float change = movementSpeed * deltaTime;
        RealWorldCoordinates dir = new RealWorldCoordinates(targetPosition.getY() - getRealWorldCoordinates().getY(), targetPosition.getX() - getRealWorldCoordinates().getX());
        // normalise vector
        double len = Math.sqrt(Math.pow(dir.getY(), 2) + Math.pow(dir.getX(), 2)) / 10; // need to divide by 10 otherwise you can get 0,0 when normalising, because not using floats
        RealWorldCoordinates norm = new RealWorldCoordinates((int) (dir.getY() / len), (int) (dir.getX() / len));
        RealWorldCoordinates movedBy = new RealWorldCoordinates((int) (norm.getY() * change), (int) (norm.getX() * change));
        RealWorldCoordinates newPos = new RealWorldCoordinates((getRealWorldCoordinates().getY() + movedBy.getY()), getRealWorldCoordinates().getX() + movedBy.getX());
        setRealWorldCoordinates(newPos);
        
        if (distance(getRealWorldCoordinates(), targetPosition) < 20f) {
            GameController.damageTroop(tower, targetTroop, this);
        }
    }

    /**
     * Return the distance between two coordinates
     */
    private float distance(RealWorldCoordinates current, RealWorldCoordinates target) {
        RealWorldCoordinates delta = new RealWorldCoordinates(current.getY() - target.getY(), current.getX() - target.getX());
        return (float) Math.sqrt(Math.pow(delta.getY(), 2) + Math.pow(delta.getX(), 2));
    }
}
