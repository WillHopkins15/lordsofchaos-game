package org.lordsofchaos;

import org.lordsofchaos.coordinatesystems.Coordinates;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.matrixobjects.MatrixObject;
import org.lordsofchaos.matrixobjects.Path;
import org.lordsofchaos.matrixobjects.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapGenerator
{
    private static ArrayList<Coordinates> path1 = new ArrayList<Coordinates>(
            Arrays.asList(new MatrixCoordinates(8, 0), new MatrixCoordinates(8, 1), new MatrixCoordinates(8, 2),
                    new MatrixCoordinates(8, 3), new MatrixCoordinates(8, 4), new MatrixCoordinates(8, 5),
                    new MatrixCoordinates(8, 6), new MatrixCoordinates(8, 7), new MatrixCoordinates(8, 8),
                    new MatrixCoordinates(8, 9), new MatrixCoordinates(8, 10), new MatrixCoordinates(8, 11),
                    new MatrixCoordinates(8, 12), new MatrixCoordinates(7, 12), new MatrixCoordinates(6, 12),
                    new MatrixCoordinates(6, 13), new MatrixCoordinates(6, 14), new MatrixCoordinates(6, 15),
                    new MatrixCoordinates(6, 16), new MatrixCoordinates(6, 17), new MatrixCoordinates(6, 18),
                    new MatrixCoordinates(7, 18), new MatrixCoordinates(8, 18), new MatrixCoordinates(9, 18),
                    new MatrixCoordinates(10, 18), new MatrixCoordinates(11, 18), new MatrixCoordinates(12, 18),
                    new MatrixCoordinates(13, 18), new MatrixCoordinates(14, 18), new MatrixCoordinates(15, 18),
                    new MatrixCoordinates(16, 18), new MatrixCoordinates(17, 18), new MatrixCoordinates(18, 18)));
    
    private static ArrayList<Coordinates> path2 = new ArrayList<Coordinates>(Arrays.asList(new MatrixCoordinates(0, 5),
            new MatrixCoordinates(1, 5), new MatrixCoordinates(2, 5), new MatrixCoordinates(3, 5),
            new MatrixCoordinates(4, 5), new MatrixCoordinates(5, 5), new MatrixCoordinates(6, 5),
            new MatrixCoordinates(7, 5), new MatrixCoordinates(8, 5), new MatrixCoordinates(9, 5),
            new MatrixCoordinates(10, 5), new MatrixCoordinates(11, 5), new MatrixCoordinates(12, 5),
            new MatrixCoordinates(13, 5), new MatrixCoordinates(14, 5), new MatrixCoordinates(15, 5),
            new MatrixCoordinates(15, 6), new MatrixCoordinates(15, 7), new MatrixCoordinates(15, 8),
            new MatrixCoordinates(15, 9), new MatrixCoordinates(15, 10), new MatrixCoordinates(15, 11),
            new MatrixCoordinates(15, 12), new MatrixCoordinates(15, 12), new MatrixCoordinates(15, 14),
            new MatrixCoordinates(14, 14), new MatrixCoordinates(14, 15), new MatrixCoordinates(14, 16),
            new MatrixCoordinates(14, 17), new MatrixCoordinates(14, 18), new MatrixCoordinates(15, 18),
            new MatrixCoordinates(16, 18), new MatrixCoordinates(17, 18), new MatrixCoordinates(18, 18)));
    
    private static ArrayList<Coordinates> path3 = new ArrayList<Coordinates>(Arrays.asList(new MatrixCoordinates(0, 15),
            new MatrixCoordinates(1, 15), new MatrixCoordinates(2, 15), new MatrixCoordinates(3, 15),
            new MatrixCoordinates(4, 15), new MatrixCoordinates(5, 15), new MatrixCoordinates(6, 15),
            new MatrixCoordinates(7, 15), new MatrixCoordinates(8, 15), new MatrixCoordinates(9, 15),
            new MatrixCoordinates(10, 15), new MatrixCoordinates(11, 15), new MatrixCoordinates(11, 14),
            new MatrixCoordinates(11, 13), new MatrixCoordinates(11, 12), new MatrixCoordinates(11, 11),
            new MatrixCoordinates(11, 10), new MatrixCoordinates(11, 9), new MatrixCoordinates(12, 9),
            new MatrixCoordinates(13, 9), new MatrixCoordinates(14, 9), new MatrixCoordinates(15, 9),
            new MatrixCoordinates(16, 9), new MatrixCoordinates(17, 9), new MatrixCoordinates(18, 9),
            new MatrixCoordinates(18, 10), new MatrixCoordinates(18, 11), new MatrixCoordinates(18, 12),
            new MatrixCoordinates(18, 13), new MatrixCoordinates(18, 14), new MatrixCoordinates(18, 15),
            new MatrixCoordinates(18, 16), new MatrixCoordinates(18, 17), new MatrixCoordinates(18, 18)));
    
    private static ArrayList<Coordinates> obstacles = new ArrayList<Coordinates>(
            Arrays.asList(
                    // River vvv
                    new MatrixCoordinates(3, 19), new MatrixCoordinates(3, 18), new MatrixCoordinates(3, 17),
                    new MatrixCoordinates(3, 16), new MatrixCoordinates(3, 14), new MatrixCoordinates(3, 13),
                    new MatrixCoordinates(3, 12), new MatrixCoordinates(3, 11), new MatrixCoordinates(3, 10),
                    new MatrixCoordinates(4, 10), new MatrixCoordinates(5, 10), new MatrixCoordinates(6, 10),
                    new MatrixCoordinates(6, 9), new MatrixCoordinates(6, 8), new MatrixCoordinates(6, 7),
                    new MatrixCoordinates(7, 7), new MatrixCoordinates(9, 7), new MatrixCoordinates(10, 7),
                    new MatrixCoordinates(11, 7), new MatrixCoordinates(12, 7), new MatrixCoordinates(13, 7),
                    new MatrixCoordinates(13, 8), new MatrixCoordinates(13, 10), new MatrixCoordinates(13, 11),
                    new MatrixCoordinates(13, 12), new MatrixCoordinates(14, 12), new MatrixCoordinates(16, 12),
                    new MatrixCoordinates(17, 12), new MatrixCoordinates(18, 12), new MatrixCoordinates(19, 12),
                    // Trees Top-Left vvv
                    new MatrixCoordinates(19, 0), new MatrixCoordinates(18, 0), new MatrixCoordinates(17, 0),
                    new MatrixCoordinates(16, 0), new MatrixCoordinates(15, 0), new MatrixCoordinates(14, 0),
                    new MatrixCoordinates(13, 0), new MatrixCoordinates(12, 0), new MatrixCoordinates(19, 1),
                    new MatrixCoordinates(18, 1), new MatrixCoordinates(17, 1), new MatrixCoordinates(16, 1),
                    new MatrixCoordinates(15, 1), new MatrixCoordinates(14, 1), new MatrixCoordinates(13, 1),
                    new MatrixCoordinates(19, 2), new MatrixCoordinates(18, 2), new MatrixCoordinates(17, 2),
                    new MatrixCoordinates(16, 2), new MatrixCoordinates(15, 2), new MatrixCoordinates(14, 2),
                    new MatrixCoordinates(13, 2), new MatrixCoordinates(19, 3), new MatrixCoordinates(18, 3),
                    new MatrixCoordinates(17, 3), new MatrixCoordinates(16, 3), new MatrixCoordinates(19, 4),
                    new MatrixCoordinates(18, 4), new MatrixCoordinates(17, 4), new MatrixCoordinates(19, 5),
                    new MatrixCoordinates(18, 5), new MatrixCoordinates(19, 6),
                    // Trees Bottom
                    new MatrixCoordinates(0, 8), new MatrixCoordinates(0, 9), new MatrixCoordinates(0, 10),
                    new MatrixCoordinates(0, 11), new MatrixCoordinates(0, 12), new MatrixCoordinates(1, 8),
                    new MatrixCoordinates(1, 9), new MatrixCoordinates(1, 10), new MatrixCoordinates(1, 11),
                    new MatrixCoordinates(1, 12), new MatrixCoordinates(2, 8), new MatrixCoordinates(2, 9),
                    new MatrixCoordinates(2, 10), new MatrixCoordinates(2, 11), new MatrixCoordinates(2, 12),
                    new MatrixCoordinates(3, 8), new MatrixCoordinates(3, 9), new MatrixCoordinates(4, 9),
                    // Defender's Base
                    new MatrixCoordinates(19, 17), new MatrixCoordinates(19, 18), new MatrixCoordinates(19, 19),
                    new MatrixCoordinates(18, 17), new MatrixCoordinates(18, 18), new MatrixCoordinates(18, 19),
                    new MatrixCoordinates(17, 17), new MatrixCoordinates(17, 18), new MatrixCoordinates(17, 19)
            
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
            for (int element = 0; element < currentList.size(); element++) {
                Coordinates coords = currentList.get(element);
                Path pathElement = new Path(coords.getY(), coords.getX());
                path.add(pathElement);
            }
        }
        return paths;
    }
    
    public static MatrixObject[][] generateMap(int width, int height, List<List<Path>> paths,
                                               List<Coordinates> obstacles) {
        MatrixObject[][] map = new MatrixObject[width][height];
        
        System.out.println(obstacles.contains(new MatrixCoordinates(3, 19)));
        // initialise array with empty tiles
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[y][x] = new Tile(y, x, null, !obstacles.contains(new MatrixCoordinates(y, x)));
            }
        }
        
        // loop through the given list of paths and set the corresponding matrix element
        // to a path
        for (int path = 0; path < paths.size(); path++) {
            List<Path> currentPath = paths.get(path);
            for (int element = 0; element < currentPath.size(); element++) {
                Path pathTile = currentPath.get(element);
                Coordinates coords = pathTile.getMatrixPosition();
                map[coords.getY()][coords.getX()] = pathTile;
            }
        }
        
        return map;
    }
    
    public static List<Coordinates> getObstacles() {
        return obstacles;
    }
    
    public static ArrayList<Coordinates> getPath1() {
        return path1;
    }
    
}
