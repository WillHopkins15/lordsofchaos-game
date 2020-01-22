package org.lordsofchaos;

public abstract class InteractiveObject extends GameObject
{
	protected int cost;
	protected int damage;
	
	public InteractiveObject(String spriteName, Coordinates coordinates,
			int cost, int damage)
	{
		super(spriteName, coordinates);
		SetCost(cost);
		SetDamage(damage);

	}
	
	// Getters and Setters
	public void SetCost(int cost)
	{
		this.cost = cost;
	}
	
	public int GetCost()
	{
		return cost;
	}
	
	public void SetDamage(int damage)
	{
		this.damage = damage;
	}
	
	public int GetDamage()
	{
		return damage;
	}
	//

}
