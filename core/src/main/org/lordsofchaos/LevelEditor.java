package org.lordsofchaos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.database.DatabaseCommunication;
import org.lordsofchaos.graphics.MapRenderer;
import org.lordsofchaos.graphics.MyTextInputListener;
import org.lordsofchaos.graphics.buttons.Button;
import org.lordsofchaos.graphics.buttons.EditorButton;
import org.lordsofchaos.graphics.buttons.ObstacleButton;
import org.lordsofchaos.matrixobjects.*;

import java.sql.SQLException;
import java.util.*;

public class LevelEditor
{
    
    private static final Color CANT_PLACE = new Color(0.4f, 0.4f, 0.4f, 1f);
    private static final Color CAN_PLACE = new Color(0.6f, 1f, 0.6f, 1f);
    private static final Color CAN_PLACE_ENDPOINT = new Color(0.6f, 1f, 1f, 1f);
    private static final Color PATH_ENDPOINT = new Color(0.6f, 0.6f, 1f, 1f);
    private static final int MAX_SPAWNS = 4;
    private MapRenderer renderer;
    private EditorLevel level;
    private MatrixCoordinates mousePosition;
    private MatrixObject hoveredTile;
    private boolean placed = false;
    private EditorPhase currentPhase = EditorPhase.SPAWNS;
    private ObstacleType currentObstacleType = ObstacleType.RIVER;
    private int currentPathIndex = 0;
    private Path lastPath;
    private List<Path> spawns = new ArrayList<>();
    private HashMap<EditorPhase, List<Button>> buttons = new HashMap<>();
    private List<MatrixCoordinates> pathEndpoints = new ArrayList<>(Arrays.asList(new MatrixCoordinates(16, 18), new MatrixCoordinates(18, 16)));
    private HashMap<EditorPhase, String> instructions = new HashMap<>();
    private EditorButton continueButton;
    private BitmapFont font;
    private SpriteBatch batch;
    
    /**
     * Create and initialise new LevelEditor instance
     *
     * @param renderer The renderer to use for the level's rendering
     * @param batch    The batch to use to display the buttons and UI
     */
    public LevelEditor(MapRenderer renderer, SpriteBatch batch) {
        this.renderer = renderer;
        this.batch = batch;
        level = new EditorLevel(20, 20);
        renderer.setLevel(level);
        continueButton = new EditorButton("UI/NewArtMaybe/continueButton.png", Gdx.graphics.getWidth() - 320, 20, this);
        buttons.put(EditorPhase.OBSTACLES, new ArrayList<>(Arrays.asList(
                new ObstacleButton("UI/LevelEditor/river.png", 20, 20, this, ObstacleType.RIVER),
                new ObstacleButton("UI/LevelEditor/trees.png", 140, 20, this, ObstacleType.TREE),
                new ObstacleButton("UI/LevelEditor/rocks.png", 260, 20, this, ObstacleType.ROCK),
                new EditorButton("UI/NewArtMaybe/finishButton.png", Gdx.graphics.getWidth() - 320, 20, this)
        )));
        instructions.put(EditorPhase.SPAWNS, "Place 1 to 4 spawns along the bottom edges.");
        instructions.put(EditorPhase.PATHS, "Draw each path to one of the end points.");
        instructions.put(EditorPhase.OBSTACLES, "Place some obstacles on the map. (Optional)");
        renderer.setLevelEditing(true);
        font = new BitmapFont();
        font = Game.getFontArial(20);
    }
    
    /**
     * The loop called repeatedly by the Game instance, used to adjust what is to be rendered based on the mouse position
     *
     * @param mousePosition The coordinate of the tile the user's mouse is over
     * @param force         Whether the rendering should be updated despite no change in mouse position. Used by other internal methods
     */
    public void run(MatrixCoordinates mousePosition, boolean force) {
        if (this.mousePosition == null || !this.mousePosition.equals(mousePosition) || placed || force) {
            int x = mousePosition.getX(), y = mousePosition.getY();
            if (hoveredTile != null && !placed) {
                level.addObject(hoveredTile);
                hoveredTile = level.objectAt(x, y);
            } else {
                hoveredTile = level.objectAt(x, y);
                if (placed && currentPhase == EditorPhase.SPAWNS) spawns.add((Path) hoveredTile);
                else if (placed && currentPhase == EditorPhase.PATHS && hoveredTile instanceof Path) {
                    level.addPath((Path) hoveredTile, currentPathIndex);
                    if (pathEndpoints.contains(hoveredTile.getMatrixPosition())) {
                        if (currentPathIndex == spawns.size() - 1) {
                            nextStep();
                            placed = false;
                            this.mousePosition = mousePosition;
                            return;
                        } else {
                            currentPathIndex++;
                            // Print
                        }
                    } else {
                        List<Path> currentPath = level.getPath(currentPathIndex);
                        this.lastPath = currentPath.get(currentPath.size() - 2);
                    }
                } else if (placed && currentPhase == EditorPhase.OBSTACLES && hoveredTile instanceof Obstacle) {
                    level.addObstcale((Obstacle) hoveredTile);
                }
                placed = false;
            }
            this.mousePosition = mousePosition;
            HashMap<Integer, Color> exceptions = new HashMap<>();
            if (currentPhase == EditorPhase.SPAWNS) {
                darkenMap(exceptions);
                if (spawns.size() < MAX_SPAWNS) {
                    for (int i = 0; i < 2; i++)
                        for (int j = 1; j < level.getWidth() - 1; j++)
                            exceptions.put(level.index(i == 0 ? 0 : j, i == 0 ? j : 0), CAN_PLACE);
                    if (!spawns.isEmpty()) buttons.put(EditorPhase.SPAWNS, new ArrayList<>(Collections.singletonList(continueButton)));
                }
                for (Path spawn : spawns)
                    exceptions.remove(level.index(spawn.getMatrixPosition()));
                if (canPlaceAt(x, y)) {
                    Path path = new Path(x, y);
                    path.setSpawn(true);
                    level.addObject(path);
                    exceptions.remove(level.index(x, y));
                } else exceptions.put(level.index(x, y), CANT_PLACE);
            } else if (currentPhase == EditorPhase.PATHS) {
                List<Integer> placable = surroundingPlacable();
                darkenMap(exceptions);
                for (Path p : level.getPath(currentPathIndex)) exceptions.remove(level.index(p.getMatrixPosition()));
                for (Integer place : placable) exceptions.put(place, CAN_PLACE);
                for (MatrixCoordinates coordinates : pathEndpoints)
                    if (placable.contains(level.index(coordinates)))
                        exceptions.put(level.index(coordinates), CAN_PLACE_ENDPOINT);
                    else exceptions.put(level.index(coordinates), PATH_ENDPOINT);
                if (placable.contains(level.index(x, y))) {
                    level.addObject(new Path(x, y));
                    exceptions.remove(level.index(x, y));
                }
            } else if (currentPhase == EditorPhase.OBSTACLES) {
                for (List<Path> fullPath : level.getPaths())
                    for (Path path : fullPath)
                        exceptions.put(level.index(path.getMatrixPosition()), CAN_PLACE);
                if (canPlaceAt(x, y)) {
                    level.addObject(new Obstacle(x, y, currentObstacleType));
                    //exceptions.remove(renderer.index(x, y));
                }
            }
            renderer.setColourExceptions(exceptions);
        }
        renderer.render();
        batch.begin();
        font.draw(batch, instructions.get(currentPhase), 20, Gdx.graphics.getHeight() - 20);
        batch.end();
    }
    
    /**
     * Darkens the entire map to indicate where tiles cannot be placed
     *
     * @param exceptions The exceptions HashMap to write to
     */
    public void darkenMap(HashMap<Integer, Color> exceptions) {
        for (int i = 0; i < level.getWidth() * level.getHeight(); i++)
            exceptions.put(i, CANT_PLACE);
    }
    
    /**
     * Calculates the tiles around the last placed Path tile where the user can place the next one.
     * Used during phase 2, the placing of the paths
     *
     * @return The list of indexes where the next Path can be placed
     */
    public List<Integer> surroundingPlacable() {
        List<Integer> placeableCoordinates = new ArrayList<>();
        List<Path> currentPath = level.getPath(currentPathIndex);
        Path lastPath = currentPath.get(currentPath.size() - 1);
        MatrixCoordinates matrixCoordinates = lastPath.getMatrixPosition();
        int x = matrixCoordinates.getX(), y = matrixCoordinates.getY();
        if (pathEndpoints.contains(matrixCoordinates)) return placeableCoordinates;
        for (int i = 0; i < 4; i++) {
            int xOffset = 0, yOffset = 0;
            if (i == 0) {
                yOffset = 1;
            } else if (i == 1) {
                yOffset = -1;
            } else if (i == 2) {
                xOffset = 1;
            } else {
                xOffset = -1;
            }
            List<String> adjacentPaths = new ArrayList<>();
            String[] adjacentOrientations = {"N", "S", "E", "W", "NE", "NW", "SE", "SW"};
            List<String> cornerNE = new ArrayList<>(Arrays.asList("N", "NE", "E"));
            List<String> cornerNW = new ArrayList<>(Arrays.asList("N", "NW", "W"));
            List<String> cornerSE = new ArrayList<>(Arrays.asList("S", "SE", "E"));
            List<String> cornerSW = new ArrayList<>(Arrays.asList("S", "SW", "W"));
            for (String orientation : adjacentOrientations)
                if (renderer.adjacentTileIs(x + xOffset, y + yOffset, orientation, "Path"))
                    adjacentPaths.add(orientation);
            if (!adjacentPaths.containsAll(cornerNE) && !adjacentPaths.containsAll(cornerNW) &&
                    !adjacentPaths.containsAll(cornerSE) && !adjacentPaths.containsAll(cornerSW) &&
                    isPlaceable(x + xOffset, y + yOffset) && x + xOffset > 0 &&
                    y + yOffset > 0 && !(x + xOffset == this.lastPath.getMatrixPosition().getX() &&
                    y + yOffset == this.lastPath.getMatrixPosition().getY())) {
                placeableCoordinates.add(level.index(x + xOffset, y + yOffset));
            }
        }
        return placeableCoordinates;
    }
    
    /**
     * Determines whether the tile at a given coordinate is part of the placeable map
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The resulting boolean
     */
    public boolean isPlaceable(int x, int y) {
        if (x < 0 || y < 0 || x >= level.getWidth() || y >= level.getHeight())
            return false;
        if (level.objectAt(x, y) instanceof Obstacle)
            return ((Obstacle) level.objectAt(x, y)).getType() != ObstacleType.BASE;
        return true;
    }
    
    /**
     * Determines whether an spawn, path or obstacle cna be placed at the given coordinate
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The resulting boolean
     */
    public boolean canPlaceAt(int x, int y) {
        if (!isPlaceable(x, y)) return false;
        if (currentPhase == EditorPhase.SPAWNS) {
            if ((x < 1 == y < 1) || x == level.getWidth() - 1 || y == level.getHeight() - 1 || spawns.size() == MAX_SPAWNS)
                return false;
            for (Path spawn : spawns) {
                int spawnX = spawn.getMatrixPosition().getX();
                int spawnY = spawn.getMatrixPosition().getY();
                if ((x == 0 && spawnX == 0 && (spawnY == y + 1 || spawnY == y - 1 || spawnY == y)) ||
                        (y == 0 && spawnY == 0 && (spawnX == x + 1 || spawnX == x - 1 || spawnX == x)))
                    return false;
            }
        } else if (currentPhase == EditorPhase.PATHS) {
            return surroundingPlacable().contains(level.index(x, y));
        } else if (currentPhase == EditorPhase.OBSTACLES) {
            return !(level.objectAt(x, y) instanceof Path);
        }
        return true;
    }
    
    /**
     * Continues the editor to the next stage
     */
    public void nextStep() {
        if (currentPhase == EditorPhase.SPAWNS) {
            for (Path spawn : spawns)
                level.newPath(spawn);
            lastPath = spawns.get(currentPathIndex);
            currentPhase = EditorPhase.PATHS;
        } else if (currentPhase == EditorPhase.PATHS) {
            currentPhase = EditorPhase.OBSTACLES;
        } else if (currentPhase == EditorPhase.OBSTACLES) {
            // Complete - Write to file
            MyTextInputListener listener = new MyTextInputListener(this);
            Gdx.input.getTextInput(listener, "Name your level", "", "Type name");
        }
    }
    
    /**
     * The name of the level when received from the text input listener
     *
     * @param name The name of the level
     */
    public void returnName(String name) {
        org.lordsofchaos.database.Map map = new org.lordsofchaos.database.Map(name, level.toJSON(), true);
        try {
            DatabaseCommunication.addMap(map);
            Game.setHasExited();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Determines whether the object at the given coordinate can be removed
     *
     * @param mc The coordinate
     * @return The boolean stating whether it can be removed
     */
    public boolean canRemoveAt(MatrixCoordinates mc) {
        MatrixObject object = level.objectAt(mc);
        if (currentPhase == EditorPhase.SPAWNS) {
            if (object instanceof Path) return ((Path) object).isSpawn();
        } else if (currentPhase == EditorPhase.PATHS) {
            List<Path> currentPath = level.getPath(currentPathIndex);
            return object.equals(currentPath.get(currentPath.size() - 1)) && currentPath.size() > 1;
        } else if (currentPhase == EditorPhase.OBSTACLES) {
            return object instanceof Obstacle;
        }
        return false;
    }
    
    /**
     * Removes a spawn, path or obstacle at the current mouse porition
     */
    public void remove() {
        if (!canRemoveAt(mousePosition)) return;
        if (currentPhase == EditorPhase.SPAWNS) {
            spawns.removeIf(spawn -> mousePosition.equals(spawn.getMatrixPosition()));
        } else if (currentPhase == EditorPhase.PATHS) {
            level.removePath(currentPathIndex);
            List<Path> currentPath = level.getPath(currentPathIndex);
            if (currentPathIndex == 0 && currentPath.size() == 1) {
                lastPath = spawns.get(currentPathIndex);
            } else {
                if (currentPath.size() == 1) {
                    Path p = level.removePath(--currentPathIndex);
                    level.addObject(new Tile(p.getMatrixPosition().getX(), p.getMatrixPosition().getY(), null));
                    currentPath = level.getPath(currentPathIndex);
                }
                lastPath = currentPath.get(currentPath.size() - 2);
            }
        } else if (currentPhase == EditorPhase.OBSTACLES) {
            level.removeObstacle(mousePosition);
        }
        Tile tile = new Tile(mousePosition.getX(), mousePosition.getY(), null);
        hoveredTile = tile;
        level.addObject(tile);
        run(mousePosition, true);
    }
    
    /**
     *
     */
    public void setPlaced() {
        this.placed = canPlaceAt(mousePosition.getX(), mousePosition.getY());
    }
    
    public MatrixCoordinates getMousePosition() {
        return mousePosition;
    }
    
    public void setCurrentObstacleType(ObstacleType currentObstacleType) {
        this.currentObstacleType = currentObstacleType;
    }
    
    public List<Button> getButtons() {
        if (buttons.containsKey(currentPhase)) return buttons.get(currentPhase);
        return new ArrayList<>();
    }
    
    public enum EditorPhase
    {
        SPAWNS,
        PATHS,
        OBSTACLES
    }
    
}
