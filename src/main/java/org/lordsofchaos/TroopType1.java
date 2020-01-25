package org.lordsofchaos;

import java.util.List;

public class TroopType1 extends Troop
{
	private static String spriteName = "TroopType1";
	private static float movementSpeed = 1;
	private static int maxHealth = 100;
	private static DamageType armourType = DamageType.None;
	private static int cost = 10;
	private static int damage = 10;
	
	public TroopType1(List<Path> path)
	{
		super(spriteName, cost, damage, 
				movementSpeed, maxHealth, 
				armourType, path);
	}
}
