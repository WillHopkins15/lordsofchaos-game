package org.lordsofchaos;

public class Coordinates
{
	private int x;
	private int y;
	
	public Coordinates (int x, int y)
	{
		SetX(x);
		SetY(y);
	}
	
	// Getters and Setters
	public void SetX(int x)
	{
		this.x = x;
	}
	
	public void SetY(int y)
	{
		this.y = y;
	}
	
	public int GetX()
	{
		return x;
	}
	
	public int GetY()
	{
		return y;
	}
	//
}
