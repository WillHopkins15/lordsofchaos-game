package org.lordsofchaos.gameobjects.towers;

import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.DamageType;
import org.lordsofchaos.gameobjects.TowerType;

public class TowerType2 extends Tower
{
    private static String spriteName = "TowerType2";
    private static int cost = 20;
    private static int damage = 6;
    private static int range = 2;
    private static DamageType damageType = DamageType.Normal;

    public TowerType2(RealWorldCoordinates rwc) {
        super(spriteName, rwc, cost, damage, range, damageType, TowerType.type1);
    }
    
}
