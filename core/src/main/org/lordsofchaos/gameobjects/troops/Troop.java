package org.lordsofchaos.gameobjects.troops;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import java.util.ArrayList;
import java.util.List;
import org.lordsofchaos.GameController;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.DamageType;
import org.lordsofchaos.gameobjects.InteractiveObject;
import org.lordsofchaos.matrixobjects.MatrixObject;
import org.lordsofchaos.matrixobjects.Path;

public class Troop extends InteractiveObject {

    protected float movementSpeed;
    protected int currentHealth;
    protected int maxHealth;
    protected DamageType armourType;
    protected List<Path> path;
    protected Sprite sprite;
    protected boolean moved;
    protected boolean targeted;
    protected boolean atEnd;
    private String previousdir = "nothing";

    private MatrixCoordinates nextTile;

    public Troop(String spriteName, int cost, int damage,
        float movementSpeed, int maxHealth, DamageType armourType, List<Path> path) {
        super(spriteName, new RealWorldCoordinates(path.get(0).getMatrixPosition()), cost, damage);
        setMovementSpeed(movementSpeed);
        setCurrentHealth(maxHealth);
        setMaxHealth(maxHealth);
        setPath(path);
        setAtEnd(false);
        Texture texture = new Texture(Gdx.files.internal("troops/" + spriteName + ".png"));
        this.sprite = new Sprite(texture);
    }

    public MatrixCoordinates getNextTile() {
        return nextTile;
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public void setMovementSpeed(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int health) {
        maxHealth = health;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int health) {
        currentHealth = health;
    }

    public List<Path> getPath() {
        if (path == null) {
            path = new ArrayList<>();
        }
        return path;
    }

    public void setPath(List<Path> path) {
        this.path = path;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public boolean getAtEnd() {
        return atEnd;
    }

    public void setAtEnd(boolean atEnd) {
        this.atEnd = atEnd;
    }

    public DamageType getArmourType() {
        return armourType;
    }

    /**
     * This troop will move along the path it has stored, scaled with deltaTime to make movement
     * framerate-independent
     *
     * @param deltaTime time taken to execute the last frame
     */
    public void move(float deltaTime) {
        int move = (int) (movementSpeed * deltaTime);

        setMoved(false);
        // move along set path
        MatrixCoordinates currentco = new MatrixCoordinates(realWorldCoordinates);

        MatrixObject foundPath = GameController.getMatrixObject(currentco.getY(), currentco.getX());

        int index = -1;

        for (int i = 0; i < path.size(); i++) {
            if (path.get(i).getMatrixPosition().getY() == foundPath.getMatrixPosition().getY()
                && path.get(i).getMatrixPosition().getX() == foundPath.getMatrixPosition().getX()) {
                index = i;
                break;
            }
        }

        if (index != (path.size() - 1)) {
            MatrixCoordinates nexttile;
            nexttile = (getPath().get(index + 1)).getMatrixPosition();
            nextTile = nexttile;
            String direction;

            if ((currentco.getY() - nexttile.getY()) == 0) {
                //x direction 
                if ((currentco.getX() - nexttile.getX()) == 1) {
                    direction = "west";
                } else {
                    direction = "east";
                }
            } else {
                if ((currentco.getY() - nexttile.getY()) == 1) {
                    direction = "north";
                } else {
                    direction = "south";
                }
            }

            if ((!previousdir.equals(direction)) && (!previousdir.equals("nothing"))) {
                moveTroop(move, previousdir);
            }

            moveTroop(move, direction);

            MatrixCoordinates updatedco = new MatrixCoordinates(realWorldCoordinates);
            //if the path tile that the troop is on changes then it wil; be added to the new troop list;
            if (!(currentco.equals(updatedco))) {
                previousdir = direction;
                (getPath().get(index)).removeTroop(this);
                (getPath().get(index + 1)).addTroop(this);
                setMoved(true);
            }
        } else {
            (getPath().get(index)).removeTroop(this);
            setAtEnd(true);
        }
    }

    private void moveTroop(int move, String direction) {
        switch (direction) {
            case "north":
                realWorldCoordinates.setY(realWorldCoordinates.getY() - move);
                break;
            case "east":
                realWorldCoordinates.setX(realWorldCoordinates.getX() + move);
                break;
            case "south":
                realWorldCoordinates.setY(realWorldCoordinates.getY() + move);
                break;
            case "west":
                realWorldCoordinates.setX(realWorldCoordinates.getX() - move);
                break;
        }
    }
}