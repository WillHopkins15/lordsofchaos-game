package org.lordsofchaos;
import org.lordsofchaos.gameobjects.towers.SerializableTower;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class BuildPhaseData implements Serializable
{
	private static final long serialVersionUID = 5084L;
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
	
	public String toString() {
		String units = Arrays.deepToString(unitBuildPlan);
		String towers = towerBuildPlan.toString();
		return "Units: " + units + " Towers: " + towers;
	}
}
