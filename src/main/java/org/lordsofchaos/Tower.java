package main.java.org.lordsofchaos;

import java.util.ArrayList;
import java.util.List;

public class Tower extends InteractiveObject
{
	protected TowerType towerType;
	protected int range;
	protected DamageType damageType;
	
	protected Troop target;
	protected List<Path> inRange;
	
	public Tower(String spriteName, Coordinates coordinates,int cost, int damage, 
			TowerType towerType, int range, DamageType damageType)
	{
		super(spriteName, coordinates, cost, damage);
		SetTowerType(towerType);
		SetRange(range);
		SetDamageType(damageType);
	}
	
	// Getters and Setters
	public void SetTowerType(TowerType towerType)
	{
		this.towerType = towerType;
	}
	
	public TowerType GetTowerType()
	{
		return towerType;
	}
	
	public void SetRange(int range)
	{
		this.range = range;
		FindPathInRange();
	}
	
	public int GetRange()
	{
		return range;
	}
	
	public void SetDamageType(DamageType damageType)
	{
		this.damageType = damageType;
	}
	
	public DamageType GetDamageType()
	{
		return damageType;
	}
	//
	
	private void FindPathInRange()
	{
		// use range to find all in-range path objects in matrix
		inRange = new ArrayList<Path>();
	}
	
	private Troop FindNearestTroop()
	{
		if (inRange != null && !inRange.isEmpty())
		{
			// loop through inRange path objects to find closest troop
		}
		return null;
	}
	
	public void Shoot()
	{
		target = FindNearestTroop();
	}

}
