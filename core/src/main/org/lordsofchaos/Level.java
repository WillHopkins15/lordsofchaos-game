package org.lordsofchaos;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.matrixobjects.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Level {

    private static final ArrayList<Obstacle> baseObstacles = new ArrayList<>(Arrays.asList(
            new Obstacle(17, 19, ObstacleType.BASE), new Obstacle(18, 19, ObstacleType.BASE),
            new Obstacle(19, 19, ObstacleType.BASE), new Obstacle(17, 18, ObstacleType.BASE),
            new Obstacle(18, 18, ObstacleType.BASE), new Obstacle(19, 18, ObstacleType.BASE),
            new Obstacle(17, 17, ObstacleType.BASE), new Obstacle(18, 17, ObstacleType.BASE),
            new Obstacle(19, 17, ObstacleType.BASE)
    ));

    private final int width;
    private final int height;
    private MatrixObject[] objects;
    private boolean isUpdated = true;

    protected List<List<Path>> paths;
    protected List<Obstacle> obstacles;

    public Level(int width, int height) {
        this.width = width;
        this.height = height;
        paths = new ArrayList<>();
        obstacles = new ArrayList<>();
        objects = blankMatrix(width, height);
    }

    public Level(JSONObject json) {
        width = json.getInt("width");
        height = json.getInt("height");

        objects = blankMatrix(width, height);

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
            if (typeString.equals("TREE")) type = ObstacleType.TREE;
            else if (typeString.equals("ROCK")) type = ObstacleType.ROCK;
            Obstacle o = new Obstacle(x, y, type);
            addObject(o);
            obstacles.add(o);
        }

    }

    public MatrixObject[] blankMatrix(int width, int height) {
        MatrixObject[] matrix = new MatrixObject[width * height];
        for (int x = 0; x < width; x++) for (int y = 0; y < height; y++)
            matrix[index(x, y)] = new Tile(x, y, null);
        baseObstacles.forEach(o -> matrix[index(o.getMatrixPosition())] = o);
        return matrix;
    }

    public String toJSON() {
        List<List<String>> pathStrings = new ArrayList<>();
        for (List<Path> path: paths) {
            List<String> P = new ArrayList<>();
            for (Path p: path)
                P.add(p.getMatrixPosition().getX() + ":" + p.getMatrixPosition().getY());
            pathStrings.add(P);
        }
        List<String> obstacleStrings = new ArrayList<>();
        for (Obstacle obstacle: obstacles) {
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

    public void visualise() {
        for (int y = height - 1; y > -1; y--)
            for (int x = 0; x < width; x++)
                if (objectAt(x, y) instanceof Tile) {
                    Tile t = (Tile) objectAt(x, y);
                    if (t.getTower() != null) System.out.println("T ");
                    else System.out.print("- ");
                } else if (objectAt(x, y) instanceof Path) System.out.print("@ ");
                else if (objectAt(x, y) instanceof Obstacle) System.out.print("X ");
                else System.out.print("!");
    }

    public int index(MatrixCoordinates mc) {
        return index(mc.getX(), mc.getY());
    }

    public int index(int x, int y) {
        return x + width * y;
    }

    public MatrixObject objectAt(MatrixCoordinates mc) {
        return objectAt(mc.getX(), mc.getY());
    }

    public MatrixObject objectAt(int x, int y) {
        return objects[index(x, y)];
    }

    public void addObject(MatrixObject object) {
        objects[index(object.getMatrixPosition())] = object;
        isUpdated = true;
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

    public boolean isUpdated() {
        final boolean updated = isUpdated;
        isUpdated = false;
        return updated;
    }
}
