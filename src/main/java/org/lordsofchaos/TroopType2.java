package org.lordsofchaos;

import java.util.List;

public class TroopType2 extends Troop
{
	private static String spriteName = "TroopType2";
	private static float movementSpeed = 1;
	private static int maxHealth = 100;
	private static DamageType armourType = DamageType.None;
	private static int cost = 10;
	private static int damage = 10;
	
	public TroopType2(List<Path> path)
	{
		super(spriteName, cost, damage, 
				movementSpeed, maxHealth, 
				armourType, path);
	}
}
