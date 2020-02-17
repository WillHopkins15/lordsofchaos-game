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
		setY(rwc.getY() / sf);
		setX(rwc.getX() / sf);
	}
}
