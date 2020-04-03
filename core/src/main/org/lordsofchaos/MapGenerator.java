package org.lordsofchaos;

import org.lordsofchaos.coordinatesystems.Coordinates;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.matrixobjects.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapGenerator {
    
    private static ArrayList<Coordinates> path1 = new ArrayList<Coordinates>(
            Arrays.asList(new MatrixCoordinates(0, 8), new MatrixCoordinates(1, 8), new MatrixCoordinates(2, 8),
                    new MatrixCoordinates(3, 8), new MatrixCoordinates(4, 8), new MatrixCoordinates(5, 8),
                    new MatrixCoordinates(6, 8), new MatrixCoordinates(7, 8), new MatrixCoordinates(8, 8),
                    new MatrixCoordinates(9, 8), new MatrixCoordinates(10, 8), new MatrixCoordinates(11, 8),
                    new MatrixCoordinates(12, 8), new MatrixCoordinates(12, 7), new MatrixCoordinates(12, 6),
                    new MatrixCoordinates(13, 6), new MatrixCoordinates(14, 6), new MatrixCoordinates(15, 6),
                    new MatrixCoordinates(16, 6), new MatrixCoordinates(17, 6), new MatrixCoordinates(18, 6),
                    new MatrixCoordinates(18, 7), new MatrixCoordinates(18, 8), new MatrixCoordinates(18, 9),
                    new MatrixCoordinates(18, 10), new MatrixCoordinates(18, 11), new MatrixCoordinates(18, 12),
                    new MatrixCoordinates(18, 13), new MatrixCoordinates(18, 14), new MatrixCoordinates(18, 15),
                    new MatrixCoordinates(18, 16)));
    
    private static ArrayList<Coordinates> path2 = new ArrayList<Coordinates>(Arrays.asList(new MatrixCoordinates(5, 0),
            new MatrixCoordinates(5, 1), new MatrixCoordinates(5, 2), new MatrixCoordinates(5, 3),
            new MatrixCoordinates(5, 4), new MatrixCoordinates(5, 5), new MatrixCoordinates(5, 6),
            new MatrixCoordinates(5, 7), new MatrixCoordinates(5, 8), new MatrixCoordinates(5, 9),
            new MatrixCoordinates(5, 10), new MatrixCoordinates(5, 11), new MatrixCoordinates(5, 12),
            new MatrixCoordinates(5, 13), new MatrixCoordinates(5, 14), new MatrixCoordinates(5, 15),
            new MatrixCoordinates(6, 15), new MatrixCoordinates(7, 15), new MatrixCoordinates(8, 15),
            new MatrixCoordinates(9, 15), new MatrixCoordinates(10, 15), new MatrixCoordinates(11, 15),
            new MatrixCoordinates(12, 15), new MatrixCoordinates(13, 15), new MatrixCoordinates(14, 15),
            new MatrixCoordinates(14, 14), new MatrixCoordinates(15, 14), new MatrixCoordinates(16, 14),
            new MatrixCoordinates(17, 14), new MatrixCoordinates(18, 14), new MatrixCoordinates(18, 15),
            new MatrixCoordinates(18, 16)));
    
    private static ArrayList<Coordinates> path3 = new ArrayList<Coordinates>(Arrays.asList(new MatrixCoordinates(15, 0),
            new MatrixCoordinates(15, 1), new MatrixCoordinates(15, 2), new MatrixCoordinates(15, 3),
            new MatrixCoordinates(15, 4), new MatrixCoordinates(15, 5), new MatrixCoordinates(15, 6),
            new MatrixCoordinates(15, 7), new MatrixCoordinates(15, 8), new MatrixCoordinates(15, 9),
            new MatrixCoordinates(15, 10), new MatrixCoordinates(15, 11), new MatrixCoordinates(14, 11),
            new MatrixCoordinates(13, 11), new MatrixCoordinates(12, 11), new MatrixCoordinates(11, 11),
            new MatrixCoordinates(10, 11), new MatrixCoordinates(9, 11), new MatrixCoordinates(9, 12),
            new MatrixCoordinates(9, 13), new MatrixCoordinates(9, 14), new MatrixCoordinates(9, 15),
            new MatrixCoordinates(9, 16), new MatrixCoordinates(9, 17), new MatrixCoordinates(9, 18),
            new MatrixCoordinates(10, 18), new MatrixCoordinates(11, 18), new MatrixCoordinates(12, 18),
            new MatrixCoordinates(13, 18), new MatrixCoordinates(14, 18), new MatrixCoordinates(15, 18),
            new MatrixCoordinates(16, 18)));
    
    private static ArrayList<Obstacle> baseObstacles = new ArrayList<Obstacle>(
            Arrays.asList(
                    new Obstacle(17, 19, ObstacleType.BASE), new Obstacle(18, 19, ObstacleType.BASE), new Obstacle(19, 19, ObstacleType.BASE),
                    new Obstacle(17, 18, ObstacleType.BASE), new Obstacle(18, 18, ObstacleType.BASE), new Obstacle(19, 18, ObstacleType.BASE),
                    new Obstacle(17, 17, ObstacleType.BASE), new Obstacle(18, 17, ObstacleType.BASE), new Obstacle(19, 17, ObstacleType.BASE)
            )
    );
    
    private static ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>(
            Arrays.asList(
                    // River vvv
                    new Obstacle(19, 3, ObstacleType.RIVER), new Obstacle(18, 3, ObstacleType.RIVER), new Obstacle(17, 3, ObstacleType.RIVER),
                    new Obstacle(16, 3, ObstacleType.RIVER), new Obstacle(14, 3, ObstacleType.RIVER), new Obstacle(13, 3, ObstacleType.RIVER),
                    new Obstacle(12, 3, ObstacleType.RIVER), new Obstacle(11, 3, ObstacleType.RIVER), new Obstacle(10, 3, ObstacleType.RIVER),
                    new Obstacle(10, 4, ObstacleType.RIVER), new Obstacle(10, 5, ObstacleType.RIVER), new Obstacle(10, 6, ObstacleType.RIVER),
                    new Obstacle(9, 6, ObstacleType.RIVER), new Obstacle(8, 6, ObstacleType.RIVER), new Obstacle(7, 6, ObstacleType.RIVER),
                    new Obstacle(7, 7, ObstacleType.RIVER), new Obstacle(7, 9, ObstacleType.RIVER), new Obstacle(7, 10, ObstacleType.RIVER),
                    new Obstacle(7, 11, ObstacleType.RIVER), new Obstacle(7, 12, ObstacleType.RIVER), new Obstacle(7, 13, ObstacleType.RIVER),
                    new Obstacle(8, 13, ObstacleType.RIVER), new Obstacle(10, 13, ObstacleType.RIVER), new Obstacle(11, 13, ObstacleType.RIVER),
                    new Obstacle(12, 13, ObstacleType.RIVER), new Obstacle(12, 14, ObstacleType.RIVER), new Obstacle(12, 16, ObstacleType.RIVER),
                    new Obstacle(12, 17, ObstacleType.RIVER), new Obstacle(12, 18, ObstacleType.RIVER), new Obstacle(12, 19, ObstacleType.RIVER),
                    // Trees Top-Left vvv
                    new Obstacle(0, 19, ObstacleType.TREE), new Obstacle(0, 18, ObstacleType.TREE), new Obstacle(0, 17, ObstacleType.TREE),
                    new Obstacle(0, 16, ObstacleType.TREE), new Obstacle(0, 15, ObstacleType.TREE), new Obstacle(0, 14, ObstacleType.TREE),
                    new Obstacle(0, 13, ObstacleType.TREE), new Obstacle(0, 12, ObstacleType.TREE), new Obstacle(1, 19, ObstacleType.TREE),
                    new Obstacle(1, 18, ObstacleType.TREE), new Obstacle(1, 17, ObstacleType.TREE), new Obstacle(1, 16, ObstacleType.TREE),
                    new Obstacle(1, 15, ObstacleType.TREE), new Obstacle(1, 14, ObstacleType.TREE), new Obstacle(1, 13, ObstacleType.TREE),
                    new Obstacle(2, 19, ObstacleType.TREE), new Obstacle(2, 18, ObstacleType.TREE), new Obstacle(2, 17, ObstacleType.TREE),
                    new Obstacle(2, 16, ObstacleType.TREE), new Obstacle(2, 15, ObstacleType.TREE), new Obstacle(2, 14, ObstacleType.ROCK),
                    new Obstacle(2, 13, ObstacleType.ROCK), new Obstacle(3, 19, ObstacleType.TREE), new Obstacle(3, 18, ObstacleType.TREE),
                    new Obstacle(3, 17, ObstacleType.TREE), new Obstacle(3, 16, ObstacleType.ROCK), new Obstacle(4, 19, ObstacleType.TREE),
                    new Obstacle(4, 18, ObstacleType.TREE), new Obstacle(4, 17, ObstacleType.TREE), new Obstacle(5, 19, ObstacleType.TREE),
                    new Obstacle(5, 18, ObstacleType.ROCK), new Obstacle(6, 19, ObstacleType.ROCK),
                    // Trees Bottom
                    new Obstacle(8, 0, ObstacleType.ROCK), new Obstacle(9, 0, ObstacleType.TREE), new Obstacle(10, 0, ObstacleType.TREE),
                    new Obstacle(11, 0, ObstacleType.TREE), new Obstacle(12, 0, ObstacleType.ROCK), new Obstacle(8, 1, ObstacleType.TREE),
                    new Obstacle(9, 1, ObstacleType.TREE), new Obstacle(10, 1, ObstacleType.TREE), new Obstacle(11, 1, ObstacleType.TREE),
                    new Obstacle(12, 1, ObstacleType.ROCK), new Obstacle(8, 2, ObstacleType.TREE), new Obstacle(9, 2, ObstacleType.TREE),
                    new Obstacle(10, 2, ObstacleType.TREE), new Obstacle(11, 2, ObstacleType.TREE), new Obstacle(12, 2, ObstacleType.ROCK),
                    new Obstacle(8, 3, ObstacleType.ROCK), new Obstacle(9, 3, ObstacleType.TREE), new Obstacle(9, 4, ObstacleType.TREE)
            ));
    
    public static List<List<Path>> generatePaths() {
        // to generate one path, must provide a list of matrixPositions (Coordinates)
        
        List<List<Coordinates>> coordinatesLists = new ArrayList<List<Coordinates>>();
        List<List<Path>> paths = new ArrayList<List<Path>>();
        
        coordinatesLists.add(path1); // can add as many paths as needed here
        coordinatesLists.add(path2);
        coordinatesLists.add(path3);
        
        // loop through each list of coordinates
        for (int listIndex = 0; listIndex < coordinatesLists.size(); listIndex++) {
            List<Coordinates> currentList = coordinatesLists.get(listIndex);
            
            // instantiate a path list and add it to the return list of paths
            List<Path> path = new ArrayList<Path>();
            paths.add(path);
            
            // within each list, loop through each pair of coordinates and create a new Path
            // object
            for (Coordinates coords : currentList)
                path.add(new Path(coords.getX(), coords.getY()));
        }
        paths.get(0).get(0).setSpawn(true);
        paths.get(1).get(0).setSpawn(true);
        paths.get(2).get(0).setSpawn(true);
        return paths;
    }
    
    public static MatrixObject[][] generateMap(int width, int height, List<List<Path>> paths, List<Obstacle> obstacles) {
        MatrixObject[][] map = new MatrixObject[width][height];
        //System.out.println(obstacles.contains(new MatrixCoordinates(3, 19)));
        // initialise array with empty tiles
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                map[y][x] = new Tile(x, y, null);
        if (obstacles != null)
            obstacles.forEach(o -> map[o.getMatrixPosition().getY()][o.getMatrixPosition().getX()] = o);
        // loop through the given list of paths and set the corresponding matrix element
        // to a path
        if (paths != null)
            for (List<Path> currentPath : paths)
                for (Path pathTile : currentPath) {
                    Coordinates coords = pathTile.getMatrixPosition();
                    map[coords.getY()][coords.getX()] = pathTile;
                }
        
        baseObstacles.forEach(o -> map[o.getMatrixPosition().getY()][o.getMatrixPosition().getX()] = o);


        return map;
    }
    
    public static List<Obstacle> getObstacles() {
        return obstacles;
    }
    
    public static ArrayList<Coordinates> getPath1() {
        return path1;
    }
    
}
