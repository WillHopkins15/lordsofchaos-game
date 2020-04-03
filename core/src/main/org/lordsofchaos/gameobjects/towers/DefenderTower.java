package org.lordsofchaos.gameobjects.towers;

import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.DamageType;
import org.lordsofchaos.gameobjects.GameObject;

public class DefenderTower extends GameObject
{
    
    private static DamageType damageType = DamageType.Light;
    
    public DefenderTower(int x, int y, boolean corner, boolean middle) {
        super(middle ? "DefenderTowerMiddle" : corner ? "DefenderTowerCorner" : "DefenderTower", new RealWorldCoordinates(new MatrixCoordinates(x, y)));
    }
    
    
}
