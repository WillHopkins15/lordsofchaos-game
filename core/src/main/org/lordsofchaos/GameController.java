package org.lordsofchaos;

import com.badlogic.gdx.Gdx;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.database.Leaderboard;
import org.lordsofchaos.database.LeaderboardRow;
import org.lordsofchaos.gameobjects.TowerType;
import org.lordsofchaos.gameobjects.towers.DefenderTower;
import org.lordsofchaos.gameobjects.towers.Projectile;
import org.lordsofchaos.gameobjects.towers.SerializableTower;
import org.lordsofchaos.gameobjects.towers.Tower;
import org.lordsofchaos.gameobjects.towers.TowerType1;
import org.lordsofchaos.gameobjects.towers.TowerType2;
import org.lordsofchaos.gameobjects.towers.TowerType3;
import org.lordsofchaos.gameobjects.troops.Troop;
import org.lordsofchaos.gameobjects.troops.TroopType1;
import org.lordsofchaos.gameobjects.troops.TroopType2;
import org.lordsofchaos.gameobjects.troops.TroopType3;
import org.lordsofchaos.graphics.MyTextInputListener;
import org.lordsofchaos.matrixobjects.MatrixObject;
import org.lordsofchaos.matrixobjects.Obstacle;
import org.lordsofchaos.matrixobjects.Path;
import org.lordsofchaos.matrixobjects.Tile;
import org.lordsofchaos.player.Attacker;
import org.lordsofchaos.player.Defender;
import org.lordsofchaos.player.Player;

public class GameController {

    public final static float DAMAGEBONUS = 1.5f; // towers do this much times damage against corresponding troop type
    protected final static String ATTACKERNAME = "blank";
    protected final static String DEFENDERNAME = "blank";
    private static final int scaleFactor = 64;
    private static final int defenderUpgradeBaseCost = 50;
    private static final int attackerUpgradeBaseCost = 50;
    private static final int unblockPathCost = 100;
    public static String levelJson = ""; // by default this will be the level we created, but can load user maps
    public static Attacker attacker;
    public static Defender defender;
    // this records if the player on the client machine is an attacker or a defender
    public static Player clientPlayerType;
    protected static int wave;
    // list of all troops currently on screen
    protected static List<Troop> troops = new ArrayList<>();
    // list of all towers in matrix
    protected static List<Tower> towers = new ArrayList<>();
    protected static List<DefenderTower> defenderTowers = new ArrayList<>(Arrays.asList(
        new DefenderTower(17, 19, true, false), new DefenderTower(17, 18, false, false),
        new DefenderTower(19, 17, true, false), new DefenderTower(18, 17, false, false),
        new DefenderTower(17, 17, true, false), new DefenderTower(18, 18, false, true),
        new DefenderTower(18, 19, false, false), new DefenderTower(19, 18, false, false),
        new DefenderTower(19, 19, true, false)
    ));
    //
    // this list gets iterated through at the end of build phase, each tower gets marked as completed, then the list clears
    protected static List<Tower> towersPlacedThisTurn = new ArrayList<Tower>();
    private static WaveState waveState;
    // timing
    private static float buildTimer = 0;
    private static float buildTimeLimit = 30;
    private static float unitSpawnTimer = 0;
    private static float unitSpawnTimeLimit = 1;
    // Height and Width of the map
    private static int height;
    private static int width;

    private static int defenderUpgradeLevel = 0;
    private static int defenderMaxUpgradeLevel = 3;
    private static int troopUpgradeThreshold = 25;
    private static int troopsMade = 0;
    private static int attackerUpgradeLevel = 0;
    private static int healthUpgrade = 0;
    private static float speedUpgrade = 0;
    private static int damageUpgrade = 0;
    // The 2 dimensional array to represent the map
    private static Level level;

    private static List<Projectile> projectiles;

    private static String inputName;

    private static int endsPhaseRequests = 0; // incremented when a player ends play phase, both players only move move when this is set to 1

    /**
     * When a player wins the game, they need to enter a name to add to the database, this function
     * is called by a MyTextInputListener object when they interact with the buttons on the box
     *
     * @param name     the name the player has entered
     * @param listener the textbox object that is calling this function
     */
    public static void setInputName(String name, MyTextInputListener listener) {
        if (LeaderboardRow.verifyName(name)) {
            inputName = name;
            waveState = WaveState.SubmitInput;
        } else {
            // if the name entered was invalid (and this includes the case
            // // where the user closed the box or pressed cancel), re-draw the text box
            Gdx.input.getTextInput(listener, "Congratulations, you won!", "",
                "Name must be at least one character!");
        }
    }

    public static List<Projectile> getProjectiles() {
        if (projectiles == null) {
            projectiles = new ArrayList<>();
        }
        return projectiles;
    }

    public static int getDefenderHealth() {
        return defender.getHealth();
    }

    public static float getBuildPhaseTimer() {
        return buildTimer;
    }

    public static WaveState getWaveState() {
        return waveState;
    }

    public static List<Tower> getTowers() {
        return towers;
    }

    public static List<DefenderTower> getDefenderTowers() {
        return defenderTowers;
    }

    public static Level getLevel() {
        return level;
    }

    public static List<Troop> getTroops() {
        return troops;
    }

    public static int getScaleFactor() {
        return scaleFactor;
    }

    public static List<List<Path>> getPaths() {
        return level.getPaths();
    }

    public static int getAttackerUpgradeCooldown() {
        return troopUpgradeThreshold - troopsMade % troopUpgradeThreshold;
    }

    public static int getDefenderUpgrade() {
        return defenderUpgradeLevel;
    }

    public static int getUnitUpgradeLevel() {
        return attackerUpgradeLevel + 1;
    }

    public static void setPlayerType(Boolean type) {
        if (type) {
            clientPlayerType = defender;
        } else {
            clientPlayerType = attacker;
        }
    }

    public static void levelSelected(String json) {
        levelJson = json;
    }

    /**
     * Reset all values at the start of the game, and when a new game starts
     */
    public static void initialise() {
        attacker = new Attacker(ATTACKERNAME);
        defender = new Defender(DEFENDERNAME);
        defenderUpgradeLevel = 0;
        defenderMaxUpgradeLevel = 3;
        troopUpgradeThreshold = 25;
        troopsMade = 0;
        attackerUpgradeLevel = 0;
        healthUpgrade = 0;
        speedUpgrade = 0;
        damageUpgrade = 0;
        towersPlacedThisTurn = new ArrayList<>();
        towers = new ArrayList<>();
        troops = new ArrayList<>();
        buildTimer = 0;
        unitSpawnTimer = 0;
        waveState = WaveState.DefenderBuild;
        height = 20;
        width = 20;
        wave = 1;

        // if no level selected, load the default level
        if (levelJson.equals("")) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream("core/assets/maps/MainMap.json");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            JSONTokener tokener = new JSONTokener(inputStream);
            JSONObject json = new JSONObject(tokener);
            level = new Level(json);

        } else {
            JSONObject json = new JSONObject(levelJson);
            level = new Level(json);
        }
        unblockPath(0, true); // unblock the first path
        EventManager.initialise(3, getPaths().size());
    }

    /**
     * Collects all the information about what has changed since the last packet was sent
     */
    public static BuildPhaseData getGameState() {
        // send towerBuilds and unitBuildPlan over network
        BuildPhaseData bpd = new BuildPhaseData(EventManager.getUnitBuildPlan(),
            EventManager.getTowerBuilds(), EventManager.getRemovedTowers(),
            EventManager.getDefenderUpgradesThisTurn(),
            EventManager.getPathsUnblockedThisTurn(), GameController.getWaveState().toString(),
            GameController.defender.getHealth(), attackerUpgradeLevel);
        return bpd;
    }

    /**
     * Receive a packet from the other client, then depending on whether this client is the attacker
     * or defender apply that information to this version of the game
     *
     * @param bpd the object that contains all the required information about what has changed
     */
    public static void setGameState(BuildPhaseData bpd) {
        EventManager.recieveBuildPhaseData(bpd, clientPlayerType);
        EventManager.resetEventManager();
    }

    public static List<Integer> getBlockedPaths() {
        return level.getBlockedPaths();
    }

    /**
     * When the defender receives a new packet from the attacker, if the attacker unblocked any
     * paths, this client needs to reflect that
     */
    public static void defenderNetworkUpdates(int attackerUpgrades) {

        for (int i = 0; i < EventManager.getPathsUnblockedThisTurn().size(); i++) {
            unblockPath(EventManager.getPathsUnblockedThisTurn().get(i), true);
        }

        while (attackerUpgrades > 0) {
            upgradeTroops();
            attackerUpgrades--;
        }
    }

    /**
     * Remove the given path from the blocked paths list, so the attacker can now send troops along
     * that path
     *
     * @param index  path to unblock
     * @param isFree when true, attacker is not charged- used to initialise the first path and when
     *               defender applies path unblocking
     */
    public static void unblockPath(int index, boolean isFree) {
        if (!isFree) {
            attacker.addMoney(-unblockPathCost);
        }
        level.unblockPath(index);
    }

    /**
     * Does the attacker have enough money to unblock a path
     */
    public static boolean canAttackerUnblockPath() {
        boolean canAfford = attacker.getCurrentMoney() >= unblockPathCost;
        if (!canAfford) {
            Game.playSound("ErrorSound");
        }
        return canAfford;
    }

    /**
     * Attacker has various updates it needs to perform whenever it receives a new packet from the
     * defender
     */
    public static void attackerNetworkUpdates(int defenderUpgrades) {
        attackerPlaceTowers();
        attackerRemoveTowers();
        attackerUpdgradeDefender(defenderUpgrades);
    }

    /**
     * Any towers the defender placed but then decided to remove, need to be removed from the
     * attacker's game
     */
    private static void attackerRemoveTowers() {
        for (int i = 0; i < EventManager.getRemovedTowers().size(); i++) {
            removeTower(EventManager.getRemovedTowers().get(i));
        }
    }

    /**
     * Any towers the defender placed need to be added to the attacker's game
     */
    private static void attackerPlaceTowers() {
        for (int i = 0; i < EventManager.getTowerBuilds().size(); i++) {
            System.out.println("Attacker Placing tower " + i);
            MatrixCoordinates mc = new MatrixCoordinates(
                EventManager.getTowerBuilds().get(i).getRealWorldCoordinates());

            if (((Tile) (getMatrixObject(mc.getY(), mc.getX()))).getTower() == null) {
                createTower(EventManager.getTowerBuilds().get(i));
            }
            // check if tower has not already been added
        }
        EventManager.getTowerBuilds().clear();
    }

    /**
     * If the defender bought an upgrade(s), attacker needs to apply this change also
     */
    private static void attackerUpdgradeDefender(int defenderUpgrades) {
        while (defenderUpgrades > 0) {
            defenderUpgrade();
            defenderUpgrades--;
        }
    }

    private static void resetBuildTimer() {
        buildTimer = 0;
    }

    private static void resetUnitSpawnTimer() {
        unitSpawnTimer = 0;
    }

    private static void resetAddMoneyTimer() {
    }

    /**
     * Returns false if the end turn button should be greyed out (i.e. the game is in the play phase
     * or the build timer is less than three seconds in)
     */
    public static boolean endTurnButtonEnabled()
    {
        if (waveState == WaveState.Play)
            return false;
        else if (buildTimer < 3)
            return false;
        else
            return true;
    }

    /**
     * The three play phases are DefenderBuild, AttackerBuild, and Play Whenever the current phase
     * ends, this function is called and the game is moved on to the next phase
     */
    public static void endPhase() {
        // If wave state is defender/attacker build, don't allow end turn for first three seconds
        if (waveState != WaveState.Play && buildTimer < 3)
        {
            Game.playSound("ErrorSound");
            return;
        }
        Game.switchPlayer();
        if (waveState == WaveState.DefenderBuild) {
            endsPhaseRequests = 0;

            waveState = WaveState.AttackerBuild;

            // mark all placed towers as complete
            for (Tower tower : towersPlacedThisTurn) {
                tower.setIsCompleted();
            }
            towersPlacedThisTurn.clear();

            resetBuildTimer();
        } else if (waveState == WaveState.AttackerBuild) {
            endsPhaseRequests = 0;
            waveState = WaveState.Play;
            System.out.println("Play begins");
            wave++;
            resetBuildTimer();
        } else {

            if (endsPhaseRequests == 0) {
                endsPhaseRequests++;
                return;
            }

            defender.addMoney();
            attacker.addMoney();

            removeAllProjectiles();
            cleanUpTroops();

            waveState = WaveState.DefenderBuild;

            // check here rather than in update, because defender only wins if they survive a round at max level
            if (defenderUpgradeLevel == defenderMaxUpgradeLevel) {
                playerWins(defender);
            }

            // reset all tower cooldowns
            if (!GameController.towers.isEmpty()) {
                for (int j = 0; j < GameController.towers.size(); j++) {
                    GameController.towers.get(j).resetTimer();
                }
            }

            // make sure to reset all tower build plans, unit build plans and player upgrade counts
            EventManager.resetEventManager();
            resetAddMoneyTimer();
            resetUnitSpawnTimer();
        }

        Game.newTurn();
        System.out.println("New state " + waveState);
    }

    /**
     * After the play phase ends, destroy any remaining troops (attacker always ends play phase,
     * which means the defender may have some troops remaining on very low health)
     */
    private static void cleanUpTroops() {
        while (troops.size() > 0) {
            troopDies(troops.get(0));
        }
    }

    /**
     * When a player wins the game, need to create an input text box for them to write their name
     * in. The waveState becomes WaitingForInput so that update() is paused
     */
    private static void playerWins(Player player) {
        if (Game.getClient() != null && clientPlayerType.equals(
            player)) // if player is attacker, they should enter name to get added to leaderboard
        {
            waveState = WaveState.WaitingForInput;
            MyTextInputListener listener = new MyTextInputListener();
            Gdx.input.getTextInput(listener, "Congratulations, you won!", "", "Type name");
        } else {
            waveState = WaveState.End;
        }
    }

    /**
     * This is called by render() in Game.java every frame. This is the main game loop from which
     * all gameplay processes are carried out
     *
     * @param deltaTime the time taken to process the last frame
     */
    public static void update(float deltaTime) {

        // if frame time is too high, don't process the frame as it's likely a lag spike/ loading
        if (deltaTime > 0.2f) {
            return;
        }

        if (waveState == WaveState.WaitingForInput) {
            return;
        }
        if (waveState == WaveState.SubmitInput) {
            try {
                Leaderboard.addWinner(inputName, wave);
                waveState = WaveState.End;
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return;
        }
        if (waveState == WaveState.End) {
            return;
        } // winner has been declared, so don't let the game play
        if (waveState == WaveState.DefenderBuild) {
            buildTimer += deltaTime;
            // if time elapsed, change state to attackerBuild
            if (buildTimer > buildTimeLimit) {
                if (Game.multiplayer && clientPlayerType.equals(defender)) {
                    Game.getClient().changePhase();
                } else {
                    endPhase();
                }
            }
        } else if (waveState == WaveState.AttackerBuild) {
            buildTimer += deltaTime;
            // if time elapsed, plus wave and change state to play
            if (buildTimer > buildTimeLimit) {
                if (Game.multiplayer && clientPlayerType.equals(attacker)) {

                    if (endsPhaseRequests == 0) {
                        Game.getClient().changePhase();
                    }
                } else {
                    endPhase();
                }
            }
        } else {
            // if defender health reaches zero, game over
            if (defender.getHealth() <= 0) {
                playerWins(attacker);
            }
            // if no troops on screen and none in the spawn queue
            else if (GameController.troops.isEmpty() && unitBuildPlanEmpty()) {
                if (Game.multiplayer) {
                    // only one player should end the phase, so just use the attacker
                    if (clientPlayerType.equals(attacker)) {
                        Game.getClient().changePhase();
                    }
                } else {
                    endPhase();
                }

            } else {
                shootTroops(deltaTime);
                moveTroops(deltaTime);
                spawnTroop(deltaTime);
                moveProjectiles(deltaTime);
            }
        }
    }

    private static void moveProjectiles(float deltaTime) {
        for (int i = 0; i < getProjectiles().size(); i++) {
            getProjectiles().get(i).update(deltaTime);
        }
    }

    /**
     * Returns true if the unitBuildPlan is empty- if there are no more troops to spawn
     */
    private static Boolean unitBuildPlanEmpty() {
        int paths = EventManager.getUnitBuildPlan()[0].length;
        int types = EventManager.getUnitBuildPlan().length;

        for (int path = 0; path < paths; path++) {
            for (int type = 0; type < types; type++) {
                if (EventManager.getUnitBuildPlan()[type][path] != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * When enough time has elapsed, spawn a troop onto each path that has a troop queued up. Add it
     * to the troop list so it can receive movement updates
     */
    private static void spawnTroop(float deltaTime) {
        unitSpawnTimer += deltaTime;
        if (unitSpawnTimer > unitSpawnTimeLimit) {
            // loop through each path and spawn a troop into each
            for (int path = 0; path < getPaths().size(); path++) {
                int troop;
                Troop newTroop;
                if (EventManager.getUnitBuildPlan()[0][path] > 0) {
                    troop = 0;
                    newTroop = new TroopType1(getPaths().get(path));
                } else if (EventManager.getUnitBuildPlan()[1][path] > 0) {
                    troop = 1;
                    newTroop = new TroopType2(getPaths().get(path));
                } else if (EventManager.getUnitBuildPlan()[2][path] > 0) {
                    troop = 2;
                    newTroop = new TroopType3(getPaths().get(path));
                } else {
                    continue;
                }
                troopsMade++;

                //checks if upgrades have happened
                //if so newTroop is upgraded
                if (attackerUpgradeLevel != 0) {
                    newTroop.setCurrentHealth(newTroop.getCurrentHealth() + healthUpgrade);
                    newTroop.setMovementSpeed(newTroop.getMovementSpeed() + speedUpgrade);
                    newTroop.setDamage(newTroop.getDamage() + damageUpgrade);
                }
                // add troop to on screen troops
                GameController.troops.add(newTroop);

                // remove from build plan
                EventManager.buildPlanChange(troop, path, -1, true);

            }
            // spawn troop into each path
            resetUnitSpawnTimer();
        }
    }

    /**
     * Loop through every troop in the game and call its move function. If any troops have reached
     * the end of their path, remove the troop from the troop list
     */
    public static void moveTroops(float deltaTime) {
        // any troops that reach the end will be stored here and removed at the end
        List<Troop> troopsToRemove = new ArrayList<>();

        // move troops
        for (Troop troop : GameController.troops) {
            troop.move(deltaTime);
            if (troop.getAtEnd()) {
                troopsToRemove.add(troop);
            }
        }

        // remove any troops that have reached the end
        for (Troop troop : troopsToRemove) {
            troopReachesDefender(troop);
        }
    }

    /**
     * When a troop reaches the defender's base, damage the base and kill the troop
     *
     * @param troop the troop that has reached the base
     */
    private static void troopReachesDefender(Troop troop) {
        defender.takeDamage(troop.getDamage());
        troopDies(troop);
    }

    /**
     * Loop through all towers in the game and call their shoot function. Any towers that have
     * elapsed their shootTime will then fire a projectile at a chosen troop
     */
    public static void shootTroops(float deltaTime) {
        if (!GameController.towers.isEmpty()) {
            for (int j = 0; j < GameController.towers.size(); j++) {
                GameController.towers.get(j).shoot(deltaTime);
            }
        }
    }


    /**
     * When a troops is killed, it needs to be removed from the path it was travelling along
     *
     * @param troop the troop that died
     */
    private static void troopDies(Troop troop) {
        if (troops.contains(troop)) {
            troops.remove(troop);
            Game.playSound("unitDies");
            // look through the path this troop is on and remove it from the Path it's
            // contained in
            for (int i = 0; i < troop.getPath().size(); i++) {
                Path path = troop.getPath().get(i);
                for (int j = 0; j < troop.getPath().get(i).getTroops().size(); j++) {
                    if (troop.equals(path.getTroops().get(j))) {
                        path.removeTroop(troop);
                        break;
                    }
                }
            }
        }
    }

    public static MatrixObject getMatrixObject(int y, int x) {
        return level.objectAt(x, y);
    }

    /**
     * when a projectile hits a troop, damage the troop and kill it if its health is zero
     *
     * @param tower the tower this projectile was shot from
     * @param troop the troop that was hit
     * @param proj  the projectile that hit the troop
     */
    public static void damageTroop(Tower tower, Troop troop, Projectile proj) {
        int temp;
        if (tower.getDamageType().equals(troop.getArmourType())) {
            temp = troop.getCurrentHealth() - (tower.getDamage() + 5);
        } else {
            temp = troop.getCurrentHealth() - tower.getDamage();

        }
        troop.setCurrentHealth(temp);

        if (troop.getCurrentHealth() <= 0) {
            troopDies(troop);
        }

        getProjectiles().remove(proj);
    }

    /**
     * When a tower is shoots, it calls this function and a projectile is spawned, add this to the
     * projectiles list so it can receive movement updates
     *
     * @param tower the tower that has shot
     * @param troop the troop the tower is shooting at
     */
    public static void shootTroop(Tower tower, Troop troop) {
        Projectile projectile = new Projectile(tower.getRealWorldCoordinates(), troop, tower);
        projectiles.add(projectile);
        Game.playSound("projectileStart");
    }

    /**
     * At the end of the play phase, any lingering projectiles need to be deleted
     */
    private static void removeAllProjectiles() {
        while (!projectiles.isEmpty()) {
            deleteProj(projectiles.get(0));
        }
    }

    private static void deleteProj(Projectile projectile) {
        projectile = null;
        projectiles.remove(0);
    }

    /**
     * When the EventManager receives the towerPlaced event, createTower is passed a
     * SerializableTower, which is a bare-bones networking object that contains just enough
     * information to re-create a Tower object. createTower therefore converts a SerializableTower
     * to a Tower and places it into the matrix, and the tower lists
     *
     * @param tbp a SerializableTower sent from EventManager
     */
    public static Tower createTower(SerializableTower tbp) {
        Tower tower = null;

        MatrixCoordinates mc = new MatrixCoordinates(tbp.getRealWorldCoordinates());

        Tile tile = (Tile) level.objectAt(mc);
        switch (tbp.getTowerType()) {
            case type1:
                tower = new TowerType1(tbp.getRealWorldCoordinates());
                break;
            case type2:
                tower = new TowerType2(tbp.getRealWorldCoordinates());
                break;
            case type3:
                tower = new TowerType3(tbp.getRealWorldCoordinates());
                break;
        }

        towers.add(tower);
        towersPlacedThisTurn.add(tower);
        tile.setTower(tower);

        // we have already checked if the defender can afford this tower, so now take away money
        defender.addMoney(-tower.getCost());
        return tower;
    }

    /**
     * Given a list of Tower objects, find (if one exists) the Tower that corresponds to the given
     * SerializableTower, using coordinates to compare objects
     *
     * @param serTower the SerializableTower you want to convert
     * @param towers   the Tower list to search
     */
    public static Tower serializeableTowerToTower(SerializableTower serTower, List<Tower> towers) {
        Tower foundTower = null;
        for (Tower tower : towers) {
            if (tower.getRealWorldCoordinates().equals(serTower.getRealWorldCoordinates())) {
                foundTower = tower;
                break;
            }
        }
        return foundTower;
    }

    /**
     * When a tower is initially placed, it is added to the towersPlacedThisTurn list, and the
     * towers list. If this tower is subsequently removed, these lists need to be updated to reflect
     * that change
     *
     * @param serTower the SerializableTower that was removed, sent by the EventManager
     */
    public static boolean removeTower(SerializableTower serTower) {
        Tower tower = serializeableTowerToTower(serTower, towersPlacedThisTurn);
        return removeTower(tower);
    }

    /**
     * Remove a specified tower from the matrix and any lists it exists in, refund the defender
     *
     * @param tower the tower to remove
     */
    public static boolean removeTower(Tower tower) {
        if (towers.contains(tower) && towersPlacedThisTurn.contains(tower)) {
            towers.remove(tower);
            towersPlacedThisTurn.remove(tower);
            MatrixCoordinates mc = new MatrixCoordinates(tower.getRealWorldCoordinates());
            Tile tile = (Tile) level.objectAt(mc);
            tile.setTower(null);
            defender.addMoney(tower.getCost());
            System.out.println(
                "Tower removed at " + tower.getRealWorldCoordinates().getY() + "," + tower
                    .getRealWorldCoordinates().getX());
            return true;
        }
        return false;
    }

    /**
     * Returns true if the given MatrixCoordinates are within the bounds of the matrix
     *
     * @param mc coordinates to check
     */
    public static boolean inBounds(MatrixCoordinates mc) {
        return mc.getX() >= 0 && mc.getY() >= 0 && mc.getX() < width && mc.getY() < height;
    }

    public static boolean inBounds(int y, int x) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    /**
     * Return the price of the given TowerType
     */
    private static int getTowerTypeCost(TowerType towerType) {
        if (towerType == TowerType.type1) {
            return 10;
        } else if (towerType == TowerType.type2) {
            return 20;
        } else if (towerType == TowerType.type3) {
            return 30;
        }
        return 0;
    }

    /**
     * Return the price of the given troop type
     */
    private static int getTroopTypeCost(int troopType) {
        if (troopType == 0) {
            return 10;
        } else if (troopType == 1) {
            return 15;
        }
        if (troopType == 2) {
            return 20;
        } else {
            return 0;
        }
        // add elses for other troops here
    }

    /**
     * Called by EventManager when a the attacker attempts to add a troop to the build plan
     */
    public static boolean canAffordTroop(int troopType) {
        return attacker.getCurrentMoney() >= getTroopTypeCost(troopType);
    }

    /**
     * Called by EventManager when a tower is attempted to be placed
     */
    public static boolean canAffordTower(TowerType towerType) {
        return defender.getCurrentMoney() >= getTowerTypeCost(towerType);
    }

    /**
     * Once a purchase has been verified and added to event manager, finally need to take money from
     * attacker
     */
    public static void troopPurchased(int troopType) {
        attacker.addMoney(-getTroopTypeCost(troopType));
    }

    /**
     * If a troop is cancelled, need to refund the attacker
     */
    public static void troopCancelled(int troopType, int path) {
        // check if build plan is empty at that place, if so, don't give a refund
        if (EventManager.getUnitBuildPlan()[troopType][path] > 0) {
            attacker.addMoney(getTroopTypeCost(troopType));
        }
    }

    /**
     * Returns true if the defender can afford the given TowerType, and the location is valid (i.e.
     * not an obstacle or path tile)
     *
     * @param towerType the TowerType of the tower the user is attempting to place
     * @param rwc       the location that the defender is attempting to place a tower at
     */
    public static boolean verifyTowerPlacement(TowerType towerType, RealWorldCoordinates rwc) {
        // convert realWorldCoords to matrix
        MatrixCoordinates mc = new MatrixCoordinates(rwc);

        // check if given mc is actually within the bounds of the matrix
        if (!inBounds(mc)) {
            return false;
        }

        if (!canAffordTower(towerType)) {
            return false;
        }

        // check if this matrix position is legal
        MatrixObject mo = level.objectAt(mc);
        if (mo instanceof Path || mo instanceof Obstacle) {
            return false; // cannot place towers on path
        } else if (mo instanceof Tile) {
            Tile tile = (Tile) mo;
            return tile.getTower() == null; // else it is a tile, but a tower exists here already
        }
        return true;
    }

    public static boolean canAttackerAffordUpgrade() {
        int currentCost = (1 + attackerUpgradeLevel) * attackerUpgradeBaseCost;
        if (attacker.getCurrentMoney() >= currentCost) {
            return true;
        } else {
            Game.playSound("ErrorSound");
            return false;
        }
    }

    public static int getAttackerUpgradeLevel() {
        return attackerUpgradeLevel;
    }

    /**
     * Returns true if the attacker has spawned enough troops to warrant getting an upgrade
     */
    public static boolean attackerEarnedUpgrade() {
        if (attackerUpgradeLevel <= 3) {
            int blocksMade = (int) Math.floor(troopsMade / troopUpgradeThreshold);//25);

            // if the attacker has spawned enough troops for an upgrade, but hasn't upgraded yet
            return attackerUpgradeLevel < blocksMade;
        }
        return false;
    }

    /**
     * If enough troops have been spawned by the attacker, upgrade all troops
     */
    public static void upgradeTroops() {
        attacker.addMoney((1 + attackerUpgradeLevel) * -attackerUpgradeBaseCost);
        attackerUpgradeLevel++;
        int type = attackerUpgradeLevel % 3;
        switch (type) {
            //upgrades health
            case 0:
                healthUpgrade = healthUpgrade + 5;
                break;
            //upgrades speed
            case 1:
                speedUpgrade = speedUpgrade + 0.5f;
                break;
            //upgrades damage
            case 2:
                damageUpgrade = damageUpgrade + 3;
                break;
        }

        if (!troops.isEmpty()) {
            for (Troop troop : troops) {
                switch (type) {
                    //upgrades health
                    case 0:
                        troop.setCurrentHealth(troop.getCurrentHealth() + healthUpgrade);
                        break;
                    //upgrades speed
                    case 1:
                        troop.setMovementSpeed(troop.getMovementSpeed() + speedUpgrade);
                        break;
                    //upgrades damage
                    case 2:
                        troop.setDamage(troop.getDamage() + damageUpgrade);
                        break;
                }
            }
        }
    }

    /**
     * When the defender clicks the upgrade button, this function returns true if they can afford
     * the upgrade and they are not already at max level
     */
    public static boolean canDefenderCanUpgrade() {
        System.out.println("Checking if can upgrade");
        if (defenderUpgradeLevel == defenderMaxUpgradeLevel) {
            Game.playSound("ErrorSound");
            System.out.println("Max level");
            return false;
        }
        int cost = defenderUpgradeBaseCost * (defenderUpgradeLevel + 1);
        // check if can afford
        if (defender.getCurrentMoney() >= cost) {
            System.out.println("Took money " + cost);
            defender.addMoney(-cost);
            return true;
        } else {
            Game.playSound("ErrorSound");
            System.out.println("Can't afford upgrade");
            return false;
        }
    }
    public static boolean canDefenderUpgrade() {
        if (defenderUpgradeLevel == defenderMaxUpgradeLevel) {
            return false;
        }
        int cost = defenderUpgradeBaseCost * (defenderUpgradeLevel + 1);
        // check if can afford
        if (defender.getCurrentMoney() >= cost) {
            return true;
        } else {
            System.out.println("Can't afford upgrade");
            return false;
        }
    }
    /**
     * Upgrade the defender based off which level they are at
     */
    public static void defenderUpgrade() {
        defenderUpgradeLevel++;
        System.out.println("Defender just upgraded to " + defenderUpgradeLevel);
        if (defenderUpgradeLevel == 1 || defenderUpgradeLevel == 3) {
            Tower.upgradeTowerDamage();
        } else if (defenderUpgradeLevel == 2 || defenderUpgradeLevel == 4) {
            Tower.upgradeTowerSpeed();
        }
        if (defenderUpgradeLevel == defenderMaxUpgradeLevel) {
            Game.defenderMaxLevel(); // this hides the upgrade button
        }
    }

    public enum WaveState implements Serializable {
        DefenderBuild, AttackerBuild, Play, WaitingForInput, SubmitInput, End
    }
}
