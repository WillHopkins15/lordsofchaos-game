package org.lordsofchaos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lordsofchaos.coordinatesystems.Coordinates;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.matrixobjects.MatrixObject;
import org.lordsofchaos.matrixobjects.Path;
import org.lordsofchaos.matrixobjects.Tile;

public class MapGenerator
{
    private static ArrayList<Coordinates> path1 = new ArrayList<Coordinates>(Arrays.asList
            (
                    new MatrixCoordinates(0,0),
                    new MatrixCoordinates(1,0),
                    new MatrixCoordinates(2,0),
                    new MatrixCoordinates(3,0),
                    new MatrixCoordinates(3,1),
                    new MatrixCoordinates(3,2),
                    new MatrixCoordinates(4,2),
                    new MatrixCoordinates(4,3),
                    new MatrixCoordinates(5,3),
                    new MatrixCoordinates(5,4),
                    new MatrixCoordinates(6,4),
                    new MatrixCoordinates(7,4),
                    new MatrixCoordinates(8,4)
                    ));
    
    public static List<List<Path>> generatePaths()
    {
        // to generate one path, must provide a list of matrixPositions (Coordinates)
        
        List<List<Coordinates>> coordinatesLists = new ArrayList<List<Coordinates>>();
        List<List<Path>> paths = new ArrayList<List<Path>>();
        
        coordinatesLists.add(path1); // can add as many paths as needed here
        
        // loop through each list of coordinates
        for (int listIndex = 0; listIndex < coordinatesLists.size(); listIndex++)
        {
            List<Coordinates> currentList = coordinatesLists.get(listIndex);
            
            // instantiate a path list and add it to the return list of paths
            List<Path> path = new ArrayList<Path>();
            paths.add(path);
            
            // within each list, loop through each pair of coordinates and create a new Path object
            for (int element = 0; element < currentList.size(); element++)
            {
                Coordinates coords = currentList.get(element);
                Path pathElement = new Path(coords.getY(), coords.getX(), null);
                path.add(pathElement);
            }
        }
        return paths;
    }
    
    
    public static MatrixObject[][] generateMap(int width, int height, List<List<Path>> paths) {
        MatrixObject[][] map = new MatrixObject[width][height];
        
        // initialise array with empty tiles
        for (int x = 0 ; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                map[y][x] = new Tile(y, x, null);
            }
        }
        
        // loop through the given list of paths and set the corresponding matrix element to a path
        for (int path = 0; path < paths.size(); path++)
        {
            List<Path> currentPath = paths.get(path);
            for (int element = 0; element < currentPath.size(); element++)
            {
                Path pathTile = currentPath.get(element);
                Coordinates coords = pathTile.getMatrixPosition();
                map[coords.getY()][coords.getX()] = pathTile;
            }
        }
        return map;
    }

    public static ArrayList<Coordinates> getPath1() {
        return path1;
    }

}
