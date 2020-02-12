package org.lordsofchaos;
import java.io.Serializable;
import java.util.List;

public class BuildPhaseData
{
	private int[][] unitBuildPlan;
	private List<EventManager.TowerBuild> towerBuildPlan;
	
	public BuildPhaseData(int[][] unitBuildPlan, List<EventManager.TowerBuild> towerBuildPlan)
	{
		this.unitBuildPlan = unitBuildPlan;
		this.towerBuildPlan = towerBuildPlan;
	}
	
	public int[][] getUnitBuildPlan()
	{
		return unitBuildPlan;
	}
	
	public List<EventManager.TowerBuild> getTowerBuildPlan()
	{
		return towerBuildPlan;
	}
}
