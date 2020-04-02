package org.lordsofchaos.matrixobjects;

import org.lordsofchaos.gameobjects.troops.Troop;

import java.util.ArrayList;
import java.util.List;

public class Path extends MatrixObject {
    
    private List<Troop> troops;
    private boolean isSpawn = false;
    
    public Path(int y, int x) {
        super(y, x);
        //setTroops(troops);
    }
    
    // Getters and Setters
    public List<Troop> getTroops() {
        if (troops == null) {
            troops = new ArrayList<>();
        }
        return troops;
    }
    
    public void addTroop(Troop troop) {
        getTroops().add(troop);
    }
    
    public void removeTroop(Troop troop) {
        getTroops().remove(troop);
    }
    
    public boolean isSpawn() {
        return isSpawn;
    }
    
    public void setSpawn(boolean spawn) {
        isSpawn = spawn;
    }
}
