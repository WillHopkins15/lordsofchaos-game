package org.lordsofchaos.gameobjects.towers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import javafx.util.Pair;
import org.lordsofchaos.GameController;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.DamageType;
import org.lordsofchaos.gameobjects.InteractiveObject;
import org.lordsofchaos.gameobjects.troops.Troop;
import org.lordsofchaos.matrixobjects.Path;

public class Tower extends InteractiveObject {
    protected float shootTimer = 0;
    protected float shootTimerLimit = 3;
    protected int range;
    protected DamageType damageType;
    protected Troop target;
    protected List<Path> inRange;
    protected Sprite sprite;

    public Tower(String spriteName, RealWorldCoordinates rwc, int cost, int damage, int range, DamageType damageType, Sprite sprite) {
        super(spriteName, rwc, cost, damage);
        setRange(range);
        setDamageType(damageType);
        this.sprite = sprite;
    }

    // Getters and Setters
    public void setRange(int range) {
        this.range = range;
        findPathInRange();
    }

    public int getRange() {
        return range;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    public DamageType getDamageType() {
        return damageType;
    }
    //

    private void findPathInRange() {
        // use range to find all in-range path objects in matrix
        inRange = new ArrayList<Path>();

        // List<Coordinates> temp = new ArrayList<Coordinates>();
        MatrixCoordinates matrixco = new MatrixCoordinates(getRealWorldCoordinates());
        //System.out.println("matrixco is: " + matrixco.getY() + "," + matrixco.getX());
        MatrixCoordinates tempco;
        MatrixCoordinates defenderbase = new MatrixCoordinates(GameController.defender.getCoordinates());

        //creating the numerical bounds for the tiles that would be in range

        int y = (matrixco.getY() - getRange());
        int ylimit = (y + 1 + (range * 2));
        //System.out.println("lower bound is:" + y + "upper bound is:" + ylimit);
        int x = (matrixco.getX() - getRange());
        int xlimit = (x + 1 + (range * 2));
        //System.out.println("lower bound is:" + x + "upper bound is:" + xlimit);

        int count = 0;

        for (int a = y; a < ylimit; a++) {
            for (int b = x; b <  xlimit; b++) {
                if (GameController.getMatrixObject(a,b) instanceof Path){
                    //System.out.println("the path coords: " + a + "," + b);
                    count++;
                }
            }
        }

        if (count != 0) {

            Pair temp[] = new Pair[count];

            count = 0;

            int ydistance;
            int xdistance;

            double distancetemp;

            // loop to add the coordinates of the tiles to that are paths to a pair
            // the key of the pair is the coordinates and the value is the distance from the
            // defenders base to the tiles
            for (int a = y; a < ylimit; a++) {
                for (int b = x; b < xlimit; b++) {
                    if (GameController.getMatrixObject(a, b) instanceof Path) {
                        tempco = new MatrixCoordinates(a, b);

                        ydistance = defenderbase.getY() - tempco.getY();
                        xdistance = defenderbase.getX() - tempco.getX();

                        distancetemp = Math.sqrt((ydistance * ydistance) + (xdistance * xdistance));

                        temp[count] = new Pair(tempco, distancetemp);
                        count++;
                    }

                }
            }

            sort(temp, 0, count - 1);

            // loop to add path tiles to arraylist inRange in descending order of distance
            // to defender base
            for (int i = 0; i < temp.length; i++) {
                MatrixCoordinates tco = (MatrixCoordinates) temp[i].getKey();

                // had slight issue with the casting should be fine but could be an issue in
                // debugging
                inRange.add((Path) GameController.getMatrixObject(tco.getY(), tco.getX()));
            }

        }

    }

    public int partition(Pair tiles[], int l, int h) {

        double pivot = (double) tiles[h].getValue();
        int i = (l - 1);
        for (int j = l; j < h; j++) {

            if ((double) tiles[j].getValue() > pivot) {

                i++;

                Pair temp = tiles[i];
                tiles[i] = tiles[j];
                tiles[j] = temp;
            }
        }

        Pair temp = tiles[i + 1];
        tiles[i + 1] = tiles[h];
        tiles[h] = temp;

        return i + 1;
    }

    public void sort(Pair tiles[], int l, int h) {
        if (l < h) {

            int part = partition(tiles, l, h);

            sort(tiles, l, part - 1);
            sort(tiles, part + 1, h);
        }
    }

    private Troop findNearestTroop() {
        if (inRange != null && !inRange.isEmpty()) {
            // loop through inRange path objects to find closest troop
            int count = 0;

            while (count < inRange.size()) {
                if ((inRange.get(count).getTroops()).isEmpty()) {
                    count++;
                } else {
                    int counter = 0;
                    boolean taken = true;
                    while ((taken) && (counter < (inRange.get(count).getTroops()).size())) {
                        if (!(inRange.get(count).getTroops()).get(counter).getTargeted()) {
                            taken = false;
                            return (inRange.get(count).getTroops()).get(counter);
                        } else {
                            counter++;
                        }
                    }
                }
            }
        }
        return null;
    }

    public void resetTimer() {
        shootTimer = 0;
    }

    public void shoot(float deltaTime) {
        shootTimer += deltaTime;
        if (shootTimer > shootTimerLimit) {
            target = findNearestTroop();
            GameController.shootTroop(this, target);
            resetTimer();
        }
    }

}