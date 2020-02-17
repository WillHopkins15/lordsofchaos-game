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
		setY(clamp(y, 0, GameController.getMap().length));
		setX(clamp(x, 0, GameController.getMap()[0].length));
	}

	private static int clamp(int value, int min, int max)
	{
		int newVal = value;
		if (newVal < min)
		{
			return min;
		}
		else if (newVal > max)
		{
			return max;
		}
		else
		{
			return newVal;
		}
	}
}
