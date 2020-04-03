package org.lordsofchaos.gameobjects.troops;

import org.lordsofchaos.gameobjects.DamageType;
import org.lordsofchaos.matrixobjects.Path;

import java.util.List;

public class TroopType2 extends Troop
{
    private static String spriteName = "TroopType2";
    private static float movementSpeed = 80;
    private static int maxHealth = 100;
    private static DamageType armourType = DamageType.Normal;
    private static int cost = 15;
    private static int damage = 10;
    
    public TroopType2(List<Path> path) {
        super(spriteName, cost, damage,
                movementSpeed, maxHealth,
                armourType, path);
    }
}
