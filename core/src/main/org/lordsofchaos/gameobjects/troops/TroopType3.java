package org.lordsofchaos.gameobjects.troops;

import java.util.List;
import org.lordsofchaos.gameobjects.DamageType;
import org.lordsofchaos.matrixobjects.Path;

public class TroopType3 extends Troop {

    private static String spriteName = "TroopType3";
    private static float movementSpeed = 80;
    private static int maxHealth = 100;
    private static DamageType armourType = DamageType.Heavy;
    private static int cost = 20;
    private static int damage = 10;

    public TroopType3(List<Path> path) {
        super(spriteName, cost, damage,
            movementSpeed, maxHealth,
            armourType, path);
    }
}
