package org.lordsofchaos.gameobjects.towers;

import com.badlogic.gdx.graphics.g2d.Sprite;

import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.DamageType;

public class TowerType1 extends Tower
{
	private static String spriteName = "TowerType1";
	private static int cost = 10;
	private static int damage = 10;
	private static int range = 3;
	private static DamageType damageType = DamageType.Fire;

	public TowerType1(RealWorldCoordinates rwc)
	{
		super(spriteName, rwc, cost, damage, range, damageType);
	}

}
