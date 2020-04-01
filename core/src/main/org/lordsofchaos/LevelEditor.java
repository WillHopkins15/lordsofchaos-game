package org.lordsofchaos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.graphics.MapRenderer;
import org.lordsofchaos.graphics.buttons.Button;
import org.lordsofchaos.graphics.buttons.EditorButton;
import org.lordsofchaos.graphics.buttons.ObstacleButton;
import org.lordsofchaos.matrixobjects.*;

import java.util.*;

public class LevelEditor {
    
    private static final int MAX_SPAWNS = 4;
    private MapRenderer renderer;
    private MatrixCoordinates mousePosition;
    private MatrixObject hoveredTile;
    private boolean placed = false;
    private EditorPhase currentPhase = EditorPhase.SPAWNS;
    private ObstacleType currentObstacleType = ObstacleType.RIVER;
    private int currentPathIndex = 0;
    private Path lastPath;
    private List<ArrayList<Path>> paths = new ArrayList<>();
    private List<Path> spawns = new ArrayList<>();
    private List<Obstacle> obstacles = new ArrayList<>();
    private Color cantPlace = new Color(0.4f, 0.4f, 0.4f, 1f);
    private Color canPlace = new Color(0.6f, 1f, 0.6f, 1f);
    private Color canPlaceEndpoint = new Color(0.6f, 1f, 1f, 1f);
    private Color pathEndpoint = new Color(0.6f, 0.6f, 1f, 1f);
    private HashMap<EditorPhase, List<Button>> buttons = new HashMap<>();
    private List<MatrixCoordinates> pathEndpoints = new ArrayList<>(Arrays.asList(
            new MatrixCoordinates(18, 16), new MatrixCoordinates(16, 18)));
    private EditorButton continueButton;

    public List<Button> getButtons() {
        if (buttons.containsKey(currentPhase)) return buttons.get(currentPhase);
        return new ArrayList<>();
    }

    public LevelEditor(MapRenderer renderer) {
        this.renderer = renderer;
        renderer.setMap(MapGenerator.generateMap(20, 20, null, null));
        continueButton = new EditorButton("UI/NewArtMaybe/panel.png", Gdx.graphics.getWidth() - 320, 20, this);
        buttons.put(EditorPhase.OBSTACLES, new ArrayList<>(Arrays.asList(
                new ObstacleButton("UI/NewArtMaybe/buttonSmall.png", 20, 20, this, ObstacleType.RIVER),
                new ObstacleButton("UI/NewArtMaybe/buttonSmall.png", 140, 20, this, ObstacleType.TREE),
                new ObstacleButton("UI/NewArtMaybe/buttonSmall.png", 260, 20, this, ObstacleType.ROCK),
                continueButton
        )));
        renderer.setLevelEditing(true);
    }
    
    public void run(MatrixCoordinates mousePosition, boolean force) {
        if (this.mousePosition == null || !this.mousePosition.equals(mousePosition) || placed || force) {
            int x = mousePosition.getX(), y = mousePosition.getY();
            if (hoveredTile != null && !placed) {
                renderer.addObject(hoveredTile);
                hoveredTile = renderer.objectAt(x, y);
            } else {
                hoveredTile = renderer.objectAt(x, y);
                if (placed && currentPhase == EditorPhase.SPAWNS) spawns.add((Path) hoveredTile);
                else if (placed && currentPhase == EditorPhase.PATHS && hoveredTile instanceof Path) {
                    paths.get(currentPathIndex).add((Path) hoveredTile);
                    if (pathEndpoints.contains(hoveredTile.getMatrixPosition())) {
                        if (currentPathIndex == paths.size() - 1) {
                            nextStep();
                            placed = false;
                            this.mousePosition = mousePosition;
                            return;
                        } else {
                            currentPathIndex++;
                            // Print
                        }
                    } else {
                        this.lastPath = paths.get(currentPathIndex).get(paths.get(currentPathIndex).size() - 2);
                    }
                } else if (placed && currentPhase == EditorPhase.OBSTACLES && hoveredTile instanceof Obstacle) {
                    Obstacle obstacle = (Obstacle) hoveredTile;
                    obstacles.remove(obstacle);
                    obstacles.add(obstacle);
                }
                placed = false;
            }
            this.mousePosition = mousePosition;
            HashMap<Integer, Color> exceptions = new HashMap<>();
            if (currentPhase == EditorPhase.SPAWNS) {
                darkenMap(exceptions);
                if (spawns.size() < MAX_SPAWNS) {
                    for (int i = 0; i < 2; i++)
                        for (int j = 1; j < MapRenderer.width - 1; j++)
                            exceptions.put(renderer.index(i == 0 ? 0 : j, i == 0 ? j : 0), canPlace);
                    if (!spawns.isEmpty()) buttons.put(EditorPhase.SPAWNS, new ArrayList<>(Collections.singletonList(continueButton)));
                }
                for (Path spawn : spawns)
                    exceptions.remove(renderer.index(spawn.getMatrixPosition().getX(), spawn.getMatrixPosition().getY()));
                if (canPlaceAt(x, y)) {
                    Path path = new Path(y, x);
                    path.setSpawn(true);
                    renderer.addObject(path);
                    exceptions.remove(renderer.index(x, y));
                } else exceptions.put(renderer.index(x, y), cantPlace);
            } else if (currentPhase == EditorPhase.PATHS) {
                List<Integer> placable = surroundingPlacable();
                darkenMap(exceptions);
                for (Path p: paths.get(currentPathIndex)) exceptions.remove(renderer.index(p.getMatrixPosition().getX(), p.getMatrixPosition().getY()));
                for (Integer place : placable) exceptions.put(place, canPlace);
                for (MatrixCoordinates coordinates : pathEndpoints)
                    if (placable.contains(renderer.index(coordinates.getX(), coordinates.getY())))
                        exceptions.put(renderer.index(coordinates.getX(), coordinates.getY()), canPlaceEndpoint);
                    else exceptions.put(renderer.index(coordinates.getX(), coordinates.getY()), pathEndpoint);
                if (placable.contains(renderer.index(x, y))) {
                    renderer.addObject(new Path(y, x));
                    exceptions.remove(renderer.index(x, y));
                }
            } else if (currentPhase == EditorPhase.OBSTACLES) {
                for (List<Path> fullPath: paths) for (Path path: fullPath)
                    exceptions.put(renderer.index(path.getMatrixPosition().getX(), path.getMatrixPosition().getY()), cantPlace);
                if (canPlaceAt(x, y)) {
                    Obstacle obstacle = new Obstacle(y, x, currentObstacleType);
                    renderer.addObject(obstacle);
                    //exceptions.remove(renderer.index(x, y));
                }
            }
            renderer.setColourExceptions(exceptions);
        }
        renderer.render();
    }

    public void darkenMap(HashMap<Integer, Color> exceptions) {
        for (int i = 0; i < MapRenderer.width * MapRenderer.width; i++)
            exceptions.put(i, cantPlace);
    }

    public List<Integer> surroundingPlacable() {
        List<Integer> placeableIndices = new ArrayList<>();
        List<Path> currentPath = paths.get(currentPathIndex);
        Path lastPath = currentPath.get(currentPath.size() - 1);
        MatrixCoordinates matrixCoordinates = lastPath.getMatrixPosition();
        int x = matrixCoordinates.getX(), y = matrixCoordinates.getY();
        if (pathEndpoints.contains(matrixCoordinates)) return placeableIndices;
        for (int i = 0; i < 4; i++) {
            int testX = 0, testY = 0;
            switch (i) {
                case 0: testX = 0; testY = 1; break;
                case 1: testX = 0; testY = -1; break;
                case 2: testX = 1; testY = 0; break;
                case 3: testX = -1; testY = 0; break;
            }
            List<String> adjacentPaths = new ArrayList<>();
            String[] adjacentOrientations = {"N", "S", "E", "W", "NE", "NW", "SE", "SW"};
            List<String> cornerNE = new ArrayList<>(Arrays.asList("N", "NE", "E"));
            List<String> cornerNW = new ArrayList<>(Arrays.asList("N", "NW", "W"));
            List<String> cornerSE = new ArrayList<>(Arrays.asList("S", "SE", "E"));
            List<String> cornerSW = new ArrayList<>(Arrays.asList("S", "SW", "W"));
            for (String orientation: adjacentOrientations)
                if (renderer.adjacentTileIs(x + testX, y + testY, orientation, "Path"))
                    adjacentPaths.add(orientation);
            if (!adjacentPaths.containsAll(cornerNE) && !adjacentPaths.containsAll(cornerNW) &&
                    !adjacentPaths.containsAll(cornerSE) && !adjacentPaths.containsAll(cornerSW) &&
                    isPlaceable(x + testX, y + testY) && x + testX > 0 && y + testY > 0 &&
                    !(x + testX == this.lastPath.getMatrixPosition().getX() &&
                    y + testY == this.lastPath.getMatrixPosition().getY())) {
                placeableIndices.add(renderer.index(x + testX, y + testY));
            }
        }
        return placeableIndices;
    }

    public boolean isPlaceable(int x, int y) {
        if (x < 0 || y < 0 || x >= MapRenderer.width || y >= MapRenderer.height) return false;
        if (renderer.objectAt(x, y) instanceof Obstacle)
            return ((Obstacle) renderer.objectAt(x, y)).getType() != ObstacleType.BASE;
        return true;
    }

    public boolean canPlaceAt(int x, int y) {
        if (!isPlaceable(x, y)) return false;
        if (currentPhase == EditorPhase.SPAWNS) {
            if ((x < 1 == y < 1) || x == MapRenderer.width - 1 || y == MapRenderer.height - 1 || spawns.size() == MAX_SPAWNS) return false;
            for (Path spawn : spawns) {
                int spawnX = spawn.getMatrixPosition().getX();
                int spawnY = spawn.getMatrixPosition().getY();
                if ((x == 0 && spawnX == 0 && (spawnY == y + 1 || spawnY == y - 1 || spawnY == y)) ||
                        (y == 0 && spawnY == 0 && (spawnX == x + 1 || spawnX == x - 1 || spawnX == x)))
                    return false;
            }
        } else if (currentPhase == EditorPhase.PATHS) {
            return surroundingPlacable().contains(renderer.index(x, y));
        } else if (currentPhase == EditorPhase.OBSTACLES) {
            return !(renderer.objectAt(x, y) instanceof Path);
        }
        return true;
    }

    public void nextStep() {
        if (currentPhase == EditorPhase.SPAWNS) {
            for (Path spawn: spawns) paths.add(new ArrayList<>(Collections.singletonList(spawn)));
            lastPath = spawns.get(0);
            currentPhase = EditorPhase.PATHS;
        } else if (currentPhase == EditorPhase.PATHS) {
            currentPhase = EditorPhase.OBSTACLES;
        } else if (currentPhase == EditorPhase.OBSTACLES) {
            // Complete
            System.out.println("Editing Complete.");
        }
    }

    public boolean canRemoveAt(MatrixCoordinates mc) {
        int x = mc.getX(),  y = mc.getY();
        MatrixObject object = renderer.objectAt(mc);
        if (currentPhase == EditorPhase.SPAWNS) {
            if (object instanceof Path) return ((Path) object).isSpawn();
        } else if (currentPhase == EditorPhase.PATHS) {
            List<Path> currentPath = paths.get(currentPathIndex);
            return object.equals(currentPath.get(currentPath.size() - 1));
        } else if (currentPhase == EditorPhase.OBSTACLES) {
            return object instanceof Obstacle;
        }
        return false;
    }
    
    @SuppressWarnings("SuspiciousMethodCalls")
    public void remove() {
        if (!canRemoveAt(mousePosition)) return;
        if (currentPhase == EditorPhase.SPAWNS) {
            spawns.remove(renderer.objectAt(mousePosition));
        } else if (currentPhase == EditorPhase.PATHS) {
            List<Path> currentPath = paths.get(currentPathIndex);
            currentPath.remove(currentPath.size() - 1);
            lastPath = currentPath.get(currentPath.size() - ((currentPath.size() > 1) ? 2 : 1));
        } else if (currentPhase == EditorPhase.OBSTACLES) {
            obstacles.remove(renderer.objectAt(mousePosition));
        }
        Tile tile = new Tile(mousePosition.getY(), mousePosition.getX(), null);
        hoveredTile = tile;
        renderer.addObject(tile);
        run(mousePosition, true);
    }

    public void setPlaced(boolean placed) {
        this.placed = placed && canPlaceAt(mousePosition.getX(), mousePosition.getY());
    }

    public MatrixCoordinates getMousePosition() {
        return mousePosition;
    }

    public void setCurrentObstacleType(ObstacleType currentObstacleType) {
        this.currentObstacleType = currentObstacleType;
    }

    public enum EditorPhase {
        SPAWNS,
        PATHS,
        OBSTACLES
    }
}
