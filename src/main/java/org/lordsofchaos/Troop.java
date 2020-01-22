package main.java.org.lordsofchaos;

import java.util.ArrayList;
import java.util.List;

public class Troop extends InteractiveObject
{
	protected float movementSpeed;
	protected int currentHealth;
	protected int maxHealth;
	protected DamageType armourType;
	protected List<Path> path;
	
	public Troop(String spriteName, Coordinates coordinates, int cost, int damage,
			float movementSpeed, int maxHealth, DamageType armourType, List<Path> path)
	{
		super(spriteName, coordinates, cost, damage);
		SetMovementSpeed(movementSpeed);
		SetCurrentHealth(maxHealth);
		SetMaxHealth(maxHealth);
		SetPath(path);
	}
	
	// Getters and setters
	public void SetMovementSpeed(float movementSpeed)
	{
		this.movementSpeed = movementSpeed;
	}
	
	public float GetMovementSpeed()
	{
		return movementSpeed;
	}
	
	public void SetMaxHealth(int health)
	{
		maxHealth = health;
	}
	
	public int GetMaxHealth()
	{
		return maxHealth;
	}
	
	public void SetCurrentHealth(int health)
	{
		currentHealth = health;
	}
	
	public int GetCurrentHealth()
	{
		return currentHealth;
	}
	
	public void SetPath(List<Path> path)
	{
		this.path = path;
	}
	
	public List<Path> GetPath()
	{
		if (path == null)
		{
			path = new ArrayList<Path>();
		}
		return path;
	}
	//
	
	public void Move()
	{
		// move along set path
	}
	
	public void Attack()
	{
		
	}

}
