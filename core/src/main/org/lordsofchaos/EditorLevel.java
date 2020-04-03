package org.lordsofchaos;

import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.matrixobjects.Obstacle;
import org.lordsofchaos.matrixobjects.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditorLevel extends Level {

    public EditorLevel(int width, int height) {
        super(width, height);
    }

    public void newPath(Path spawn) {
        paths.add(new ArrayList<>(Collections.singletonList(spawn)));
    }

    public void addPath(Path p, int i) {
        getPath(i).add(p);
    }

    public Path removePath(int i) {
        return getPath(i).remove(getPath(i).size() - 1);
    }

    public void addObstcale(Obstacle o) {
        removeObstacle(o.getMatrixPosition());
        obstacles.add(o);
    }

    public void removeObstacle(MatrixCoordinates mc) {
        obstacles.removeIf(obstacle -> mc.equals(obstacle.getMatrixPosition()));
    }

}
