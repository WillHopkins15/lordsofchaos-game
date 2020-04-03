package org.lordsofchaos;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.matrixobjects.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Level {

    private final int width;
    private final int height;
    private MatrixObject[] objects;
    private boolean isUpdated = true;
    private List<Integer> blockedPaths;

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

        blockedPaths = new ArrayList<>();
        if (paths.size() > 1)
            for (int i = 1; i < paths.size(); i++)
                blockPath(i);

    }

    public MatrixObject[] blankMatrix(int width, int height) {
        MatrixObject[] matrix = new MatrixObject[width * height];
        for (int x = 0; x < width; x++) for (int y = 0; y < height; y++)
            matrix[index(x, y)] = new Tile(x, y, null);
        for (int i = width - 1; i >= width - 3; i--)
            for (int j = height - 1; j >= height - 3 ; j--)
                matrix[index(i, j)] = new Obstacle(i, j, ObstacleType.BASE);
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
        for (int y = height - 1; y > -1; y--) {
            System.out.println();
            for (int x = 0; x < width; x++) {
                if (objectAt(x, y) instanceof Obstacle) System.out.print("X ");
                else if (objectAt(x, y) instanceof Path) System.out.print("@ ");
                else if (objectAt(x, y) instanceof Tile) {
                    Tile t = (Tile) objectAt(x, y);
                    if (t.getTower() != null) System.out.print("T ");
                    else System.out.print("- ");
                }
            }
        }
        System.out.println();
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

    public List<Path> getPath(int i) {
        return paths.get(i);
    }

    public List<Integer> getBlockedPaths() {
        return blockedPaths;
    }

    public void blockPath(int i) {
        MatrixCoordinates mc = getPath(i).get(0).getMatrixPosition();
        MatrixCoordinates mc1 = getPath(i).get(1).getMatrixPosition();
        MatrixCoordinates mc2 = getPath(i).get(2).getMatrixPosition();
        addObject(new Obstacle(mc.getX(), mc.getY(), new Random().nextFloat() < 0.5 ? ObstacleType.ROCK : ObstacleType.TREE));
        addObject(new Obstacle(mc1.getX(), mc1.getY(), new Random().nextFloat() < 0.5 ? ObstacleType.ROCK : ObstacleType.TREE));
        addObject(new Obstacle(mc2.getX(), mc2.getY(), new Random().nextFloat() < 0.5 ? ObstacleType.ROCK : ObstacleType.TREE));
        blockedPaths.add(i);
    }

    public void unblockPath(int i) {
        List<Path> path = getPath(i);
        addObject(path.get(0));
        addObject(path.get(1));
        addObject(path.get(2));
        blockedPaths.remove((Integer) i);
    }

    public boolean isUpdated() {
        final boolean updated = isUpdated;
        isUpdated = false;
        return updated;
    }
}
