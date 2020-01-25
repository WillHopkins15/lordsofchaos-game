package org.lordsofchaos.coordinatesystems;

import org.lordsofchaos.GameController;

public class RealWorldCoordinates extends Coordinates
{
	public RealWorldCoordinates(int y, int x)
	{
		setY(y);
		setX(x);
	}
	
	public RealWorldCoordinates(MatrixCoordinates mc)
	{
		int sf = GameController.getScaleFactor();
		int y = mc.getY() * sf;
		int x = mc.getX() * sf;
		setY(y);
		setX(x);
	}
}
