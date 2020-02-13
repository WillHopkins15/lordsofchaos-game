package org.lordsofchaos;

import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;

public class EventManager
{
	// TowerBuild tells you what sort of tower has been placed and where,
	// GameController then uses this to create instances of towers
	// GameController contains a list of TowerBuilds which will be sent over the network
	public class TowerBuild
	{
		private RealWorldCoordinates rwc;
		private int towerType;
		
		public TowerBuild(int towerType, RealWorldCoordinates rwc)
		{
			this.towerType = towerType;
			this.rwc = rwc;
		}
		
		public RealWorldCoordinates getRealWorldCoordinates()
		{
			return rwc;
		}

		public int getTowerType()
		{
			return towerType;
		}
	}
	
	// 6 unit types, 3 paths
	public static int[][] unitBuildPlan = new int[6][3]; 
	
	public static int[][] getUnitBuildPlan()
	{
		return unitBuildPlan;
	}
	
	public static void towerPlaced(TowerBuild tbp)
	{
		GameController.towerPlaced(tbp);
	}
	
	public static void resetBuildPlan()
	{
		unitBuildPlan = new int[6][3]; 
	}
	
	public static void buildPlanChange(int unitType, int path, int change)
	{
		if (unitType < 0 || unitType > 5  
				|| path < 0  || path > GameController.getPaths().size())
		{
			return; // unit or path doesn't exist
		}
		
		// get the number of units currently in the matrix position
		// add the change to this position, clamping value so it can't be negative
		int current = unitBuildPlan[unitType][path];
		unitBuildPlan[unitType][path] = clamp(current, change, 0);
	}

	private static int clamp(int value, int change, int min)
	{
		int newVal = value + change;
		if (newVal < min)
		{			
			return min;
		}
		else
		{
			return newVal;
		}
	}
}

