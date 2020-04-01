package org.lordsofchaos.gameobjects.towers;

import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.DamageType;
import org.lordsofchaos.gameobjects.TowerType;

public class TowerType1 extends Tower
{
    
    private static String spriteName = "TowerType1";
    private static int cost = 10;
    private static int damage = 6;
    private static int range = 2;
    private static DamageType damageType = DamageType.Light;
    
    public TowerType1(RealWorldCoordinates rwc) {
        super(spriteName, rwc, cost, damage, range, damageType, TowerType.type1);
    }
    
}
