package org.lordsofchaos;

import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.TowerType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EventManager
{
	private static int troopTypes;
	private static int pathCount;
	
	private static int[][] unitBuildPlan;
	private static List<TowerBuild> towerBuilds;
	
	// TowerBuild tells you what sort of tower has been placed and where,
	// GameController then uses this to create instances of towers
	// GameController contains a list of TowerBuilds which will be sent over the network
	public static class TowerBuild
	{
		private RealWorldCoordinates rwc;
		private TowerType towerType;
		
		public TowerBuild(TowerType towerType, RealWorldCoordinates rwc)
		{
			this.towerType = towerType;
			this.rwc = rwc;
		}
		
		public RealWorldCoordinates getRealWorldCoordinates()
		{
			return rwc;
		}

		public TowerType getTowerType()
		{
			return towerType;
		}
	}
	
	public static void recieveBuildPhaseData(BuildPhaseData bpd)
	{
		unitBuildPlan = bpd.getUnitBuildPlan();
		towerBuilds = bpd.getTowerBuildPlan();
	}
	
	public static void initialise(int givenTroopsTypes, int givenPathCount)
	{
		troopTypes = givenTroopsTypes;
		pathCount = givenPathCount;
		resetEventManager();
	}
	
	public static int[][] getUnitBuildPlan()
	{
		return unitBuildPlan;
	}
	
	public static List<TowerBuild> getTowerBuilds()
	{
		return towerBuilds;
	}
	
	public static void towerPlaced(TowerType towerType, RealWorldCoordinates rwc)
	{
		TowerBuild tbp = new TowerBuild(towerType, rwc);
		if (!towerBuilds.contains(tbp) && GameController.verifyTowerPlacement(rwc))
		{
			towerBuilds.add(tbp);
		}
	}
	
	public static void resetEventManager()
	{
		unitBuildPlan = new int[troopTypes][pathCount]; 
		towerBuilds = new ArrayList<TowerBuild>();
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
