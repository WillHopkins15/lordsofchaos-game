package org.lordsofchaos;

import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.matrixobjects.Obstacle;
import org.lordsofchaos.matrixobjects.Path;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A subclass of the Level class, specifically for levels that are being currently edited in the LevelEditor
 */
public class EditorLevel extends Level
{
    
    public EditorLevel(int width, int height) {
        super(width, height);
    }
    
    /**
     * Creates a new path, starting at the given spawn point
     *
     * @param spawn The spawn point at which to start the path
     */
    public void newPath(Path spawn) {
        paths.add(new ArrayList<>(Collections.singletonList(spawn)));
    }
    
    /**
     * Adds a single Path object to a path array
     *
     * @param p The Path object to add
     * @param i The index of the path array to add it to
     */
    public void addPath(Path p, int i) {
        getPath(i).add(p);
    }
    
    /**
     * Removes the last Path object from the path array at the given index
     *
     * @param i The index of the path array from which to remove the Path object
     * @return The Path object that was removed
     */
    public Path removePath(int i) {
        return getPath(i).remove(getPath(i).size() - 1);
    }
    
    /**
     * Adds an obstacle to the map
     *
     * @param o The obstacle to add
     */
    public void addObstcale(Obstacle o) {
        removeObstacle(o.getMatrixPosition());
        obstacles.add(o);
    }
    
    /**
     * Removes an obstacle at the given coordinate. if the tile at the coordinate is an obstacle
     *
     * @param mc The coordinate at which to remove an obstacle, if there exists one
     */
    public void removeObstacle(MatrixCoordinates mc) {
        obstacles.removeIf(obstacle -> mc.equals(obstacle.getMatrixPosition()));
    }
    
}
