package org.lordsofchaos;

import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.*;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.lordsofchaos.gameobjects.towers.SerializableTower;

public class EventManager {
	private static int troopTypes;
	private static int pathCount;

	private static int[][] unitBuildPlan;
	private static List<SerializableTower> towerBuilds;

	public static void recieveBuildPhaseData(BuildPhaseData bpd) {
		unitBuildPlan = bpd.getUnitBuildPlan();
		towerBuilds = bpd.getTowerBuildPlan();
	}

	public static void initialise(int givenTroopsTypes, int givenPathCount) {
		troopTypes = givenTroopsTypes;
		pathCount = givenPathCount;
		resetEventManager();
	}

	public static void towerRemoved(SerializableTower tbp) {
		if (towerBuilds.contains(tbp)) {
			towerBuilds.remove(tbp);
			// add function in gc to remove tower
		}
	}

	public static int[][] getUnitBuildPlan() {
		return unitBuildPlan;
	}

	public static List<SerializableTower> getTowerBuilds() {
		return towerBuilds;
	}

	public static void towerPlaced(TowerType towerType, RealWorldCoordinates rwc) {
		SerializableTower tbp = new SerializableTower(towerType, rwc);
		if (!towerBuilds.contains(tbp) && GameController.verifyTowerPlacement(towerType, rwc)) {
			towerBuilds.add(tbp);
			GameController.createTower(tbp);
		}
	}

	public static void resetEventManager() {
		unitBuildPlan = new int[troopTypes][pathCount];
		towerBuilds = new ArrayList<SerializableTower>();
	}

	public static void buildPlanChange(int unitType, int path, int change) {
		if (unitType < 0 || unitType > 5 || path < 0 || path > GameController.getPaths().size()) {
			return; // unit or path doesn't exist
		}

		// get the number of units currently in the matrix position
		// add the change to this position, clamping value so it can't be negative
		int current = unitBuildPlan[unitType][path];
		unitBuildPlan[unitType][path] = clamp(current, change, 0);
	}

	private static int clamp(int value, int change, int min) {
		int newVal = value + change;
		if (newVal < min) {
			return min;
		} else {
			return newVal;
		}
	}
}