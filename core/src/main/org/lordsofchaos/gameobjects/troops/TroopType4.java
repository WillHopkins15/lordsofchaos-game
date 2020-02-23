package org.lordsofchaos.gameobjects.troops;

import org.lordsofchaos.gameobjects.DamageType;
import org.lordsofchaos.matrixobjects.Path;

import java.util.List;

public class TroopType4 extends Troop
{
    private static String spriteName = "TroopType4";
    private static float movementSpeed = 1;
    private static int maxHealth = 100;
    private static DamageType armourType = DamageType.None;
    private static int cost = 10;
    private static int damage = 10;
    
    public TroopType4(List<Path> path) {
        super(spriteName, cost, damage,
                movementSpeed, maxHealth,
                armourType, path);
    }
}
