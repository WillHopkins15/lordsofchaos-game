package main.java.org.lordsofchaos;

public class Tile extends MatrixObject
{
	private Tower tower;
	
	public Tile(Tower tower)
	{
		SetTower(tower);
	}
	
	// Getters and setters
	public void SetTower(Tower tower)
	{
		this.tower = tower;
	}
	
	public Tower GetTower()
	{
		return tower;
	}
	//
}
