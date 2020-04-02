package org.lordsofchaos;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.matrixobjects.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Map {

    private static final ArrayList<Obstacle> baseObstacles = new ArrayList<>(Arrays.asList(
            new Obstacle(19, 17, ObstacleType.BASE), new Obstacle(19, 18, ObstacleType.BASE),
            new Obstacle(19, 19, ObstacleType.BASE), new Obstacle(18, 17, ObstacleType.BASE),
            new Obstacle(18, 18, ObstacleType.BASE), new Obstacle(18, 19, ObstacleType.BASE),
            new Obstacle(17, 17, ObstacleType.BASE), new Obstacle(17, 18, ObstacleType.BASE),
            new Obstacle(17, 19, ObstacleType.BASE)
    ));

    private int width;
    private int height;
    private List<List<Path>> paths;
    private List<Obstacle> obstacles;
    private MatrixObject[][] matrix;

    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        paths = new ArrayList<>();
        obstacles = new ArrayList<>();
        matrix = blankMatrix(width, height);
    }

    public Map(JSONObject json) {
        width = json.getInt("width");
        height = json.getInt("height");

        matrix = blankMatrix(width, height);

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
                    Path p = new Path(y, x);
                    p.setSpawn(j == 0);
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
            Obstacle o = new Obstacle(y, x, type);
            obstacles.add(o);
        }

    }

    public MatrixObject[][] blankMatrix(int width, int height) {
        MatrixObject[][] matrix = new MatrixObject[height][width];
        for (int x = 0; x < width; x++) for (int y = 0; y < height; y++)
            matrix[y][x] = new Tile(y, x, null);
        baseObstacles.forEach(o -> matrix[o.getMatrixPosition().getY()][o.getMatrixPosition().getX()] = o);
        return matrix;
    }

    public MatrixObject[][] getMatrix() {
        return matrix;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
