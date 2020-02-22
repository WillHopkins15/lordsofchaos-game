package org.lordsofchaos;
import org.lordsofchaos.gameobjects.towers.SerializableTower;

import java.io.Serializable;
import java.util.List;

public class BuildPhaseData implements Serializable
{
	private int[][] unitBuildPlan;
	private List<SerializableTower> towerBuildPlan;
	
	public BuildPhaseData(int[][] unitBuildPlan, List<SerializableTower> towerBuildPlan)
	{
		this.unitBuildPlan = unitBuildPlan;
		this.towerBuildPlan = towerBuildPlan;
	}
	
	public int[][] getUnitBuildPlan()
	{
		return unitBuildPlan;
	}
	
	public List<SerializableTower> getTowerBuildPlan()
	{
		return towerBuildPlan;
	}
}
