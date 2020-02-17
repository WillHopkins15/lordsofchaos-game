package org.lordsofchaos.coordinatesystems;

import org.lordsofchaos.GameController;

public class MatrixCoordinates extends Coordinates
{
	public MatrixCoordinates(int y, int x)
	{
		setY(y);
		setX(x);
	}
	
	public MatrixCoordinates(RealWorldCoordinates rwc)
	{
		int sf = GameController.getScaleFactor();
		int y = (Math.round(rwc.getY() / sf));
		int x =  (Math.round(rwc.getX() / sf));
		setY(clamp(y, 0, GameController.getMap().length-1));
		setX(clamp(x, 0, GameController.getMap()[0].length-1));
	}

	private static int clamp(int value, int min, int max)
	{
		if (value < min)
		{
			return min;
		}
		else if (value > max)
		{
			return max;
		}
		else
		{
			return value;
		}
	}
}
