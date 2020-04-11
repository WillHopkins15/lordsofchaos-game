package org.lordsofchaos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.matrixobjects.MatrixObject;
import org.lordsofchaos.matrixobjects.Obstacle;
import org.lordsofchaos.matrixobjects.ObstacleType;
import org.lordsofchaos.matrixobjects.Path;
import org.lordsofchaos.matrixobjects.Tile;

public class Level {

    private final int width;
    private final int height;
    protected List<List<Path>> paths;
    protected List<Obstacle> obstacles;
    private MatrixObject[] objects;
    private List<MatrixCoordinates> updatedCoordinates = new ArrayList<>();
    private List<Integer> blockedPaths;


    /**
     * Create a new blank map, containing only the defender's base
     *
     * @param width  The width of the map to create
     * @param height The height of the map to create
     */
    public Level(int width, int height) {
        this.width = width;
        this.height = height;
        paths = new ArrayList<>();
        obstacles = new ArrayList<>();
        objects = blankObjects(width, height);
    }


    /**
     * Create a complete map from a JSON object
     *
     * @param json the JSON object from which to read the map data
     */
    public Level(JSONObject json) {
        width = json.getInt("width");
        height = json.getInt("height");

        objects = blankObjects(width, height);

        paths = new ArrayList<>();

        HashMap<String, Path> pathCache = new HashMap<>();
        JSONArray pathsArray = json.getJSONArray("paths");
        for (int i = 0; i < pathsArray.length(); i++) {
            paths.add(new ArrayList<>());
            JSONArray P = pathsArray.getJSONArray(i);
            for (int j = 0; j < P.length(); j++) {
                String pathString = P.getString(j);
                if (pathCache.containsKey(pathString)) {
                    paths.get(i).add(pathCache.get(pathString));
                } else {
                    String[] xy = pathString.split(":");
                    int x = Integer.parseInt(xy[0]), y = Integer.parseInt(xy[1]);
                    Path p = new Path(x, y);
                    p.setSpawn(j == 0);
                    addObject(p);
                    paths.get(i).add(p);
                    pathCache.put(pathString, p);
                }
            }
        }

        obstacles = new ArrayList<>();

        JSONArray obstaclesArray = json.getJSONArray("obstacles");
        for (int i = 0; i < obstaclesArray.length(); i++) {
            String obstacleString = obstaclesArray.getString(i);
            String[] xyt = obstacleString.split(":");
            int x = Integer.parseInt(xyt[0]), y = Integer.parseInt(xyt[1]);
            String typeString = xyt[2];
            ObstacleType type = ObstacleType.RIVER;
            if (typeString.equals("TREE")) {
                type = ObstacleType.TREE;
            } else if (typeString.equals("ROCK")) {
                type = ObstacleType.ROCK;
            }
            Obstacle o = new Obstacle(x, y, type);
            addObject(o);
            obstacles.add(o);
        }

        blockedPaths = new ArrayList<>();
        if (paths.size() > 1) {
            for (int i = 1; i < paths.size(); i++) {
                blockPath(i);
            }
        }

    }

    /**
     * Creates an array of the blank tiles, the same size as the map
     *
     * @param width  The width of the map
     * @param height The height of the map
     * @return The array of MatrixObjects
     */
    public MatrixObject[] blankObjects(int width, int height) {
        MatrixObject[] matrix = new MatrixObject[width * height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile t = new Tile(x, y, null);
                matrix[index(x, y)] = t;
                updatedCoordinates.add(t.getMatrixPosition());
            }
        }
        for (int i = width - 1; i >= width - 3; i--) {
            for (int j = height - 1; j >= height - 3; j--) {
                matrix[index(i, j)] = new Obstacle(i, j, ObstacleType.BASE);
            }
        }
        return matrix;
    }

    /**
     * @return A string of JSON data, representing the map in its current composition
     */
    public String toJSON() {
        List<List<String>> pathStrings = new ArrayList<>();
        for (List<Path> path : paths) {
            List<String> P = new ArrayList<>();
            for (Path p : path) {
                P.add(p.getMatrixPosition().getX() + ":" + p.getMatrixPosition().getY());
            }
            pathStrings.add(P);
        }
        List<String> obstacleStrings = new ArrayList<>();
        for (Obstacle obstacle : obstacles) {
            MatrixCoordinates mc = obstacle.getMatrixPosition();
            obstacleStrings.add(mc.getX() + ":" + mc.getY() + ":" + obstacle.toString());
        }
        HashMap<String, Object> object = new HashMap<>();
        object.put("obstacles", obstacleStrings);
        object.put("paths", pathStrings);
        object.put("width", width);
        object.put("height", height);
        JSONObject json = new JSONObject(object);
        return json.toString();
    }

    /**
     * Print a visual representation of the level to the console
     */
    public void visualise() {
        for (int y = height - 1; y > -1; y--) {
            for (int x = 0; x < width; x++) {
                if (objectAt(x, y) instanceof Obstacle) {
                    System.out.print("X ");
                } else if (objectAt(x, y) instanceof Path) {
                    System.out.print("@ ");
                } else if (objectAt(x, y) instanceof Tile) {
                    Tile t = (Tile) objectAt(x, y);
                    if (t.getTower() != null) {
                        System.out.print("T ");
                    } else {
                        System.out.print("- ");
                    }
                }
            }
        }
    }

    /**
     * Calculates the index of a given coordinate in the array of objects
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The calculated index
     */
    public int index(int x, int y) {
        return x + width * y;
    }

    public int index(MatrixCoordinates mc) {
        return index(mc.getX(), mc.getY());
    }

    /**
     * Returns the object in the objects array at a given coordinate
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The object at the given coordinate
     */
    public MatrixObject objectAt(int x, int y) {
        return objects[index(x, y)];
    }

    public MatrixObject objectAt(MatrixCoordinates mc) {
        return objectAt(mc.getX(), mc.getY());
    }

    /**
     * Adds the given object to the level
     *
     * @param object The object to add
     */
    public void addObject(MatrixObject object) {
        objects[index(object.getMatrixPosition())] = object;
        updatedCoordinates.add(object.getMatrixPosition());
    }

    /**
     * Blocks the path at the given index
     *
     * @param i The index of the path to block
     */
    public void blockPath(int i) {
        MatrixCoordinates mc = getPath(i).get(1).getMatrixPosition();
        MatrixCoordinates mc1 = getPath(i).get(2).getMatrixPosition();
        addObject(new Obstacle(mc.getX(), mc.getY(),
            new Random().nextFloat() < 0.5 ? ObstacleType.ROCK : ObstacleType.TREE));
        addObject(new Obstacle(mc1.getX(), mc1.getY(),
            new Random().nextFloat() < 0.5 ? ObstacleType.ROCK : ObstacleType.TREE));
        blockedPaths.add(i);
    }

    /**
     * Unblocks the path at the given index
     *
     * @param i The index of the path to unblock
     */
    public void unblockPath(int i) {
        List<Path> path = getPath(i);
        addObject(path.get(1));
        addObject(path.get(2));
        blockedPaths.remove((Integer) i);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public MatrixObject[] getObjects() {
        return objects;
    }

    public List<List<Path>> getPaths() {
        return paths;
    }

    public List<Path> getPath(int i) {
        return paths.get(i);
    }

    public List<Integer> getBlockedPaths() {
        return blockedPaths;
    }

    public List<MatrixCoordinates> getUpdatedCoordinates() {
        return updatedCoordinates;
    }

    public void resetUpdatedCoordinates() {
        updatedCoordinates = new ArrayList<>();
    }

}
