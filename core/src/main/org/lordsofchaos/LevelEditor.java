package org.lordsofchaos;

import com.badlogic.gdx.graphics.Color;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.graphics.MapRenderer;
import org.lordsofchaos.matrixobjects.*;

import java.util.*;

public class LevelEditor {

    private MapRenderer renderer;
    private MatrixCoordinates mousePosition;
    private MatrixObject hoveredTile;
    boolean placed = false;

    public LevelEditor(MapRenderer renderer) {
        this.renderer = renderer;
        renderer.setMap(MapGenerator.generateMap(20, 20, null, null));
    }

    public void run(MatrixCoordinates mousePosition) {
        if (hoveredTile != null && !placed) {
            if (this.mousePosition != mousePosition) {
                renderer.addObject(hoveredTile);
                hoveredTile = renderer.objectAt(mousePosition.getX(), mousePosition.getY());
            }
        } else {
            hoveredTile = renderer.objectAt(mousePosition.getX(), mousePosition.getY());
            placed = false;
        }
        this.mousePosition = mousePosition;
        if (canPlaceAt(mousePosition.getX(), mousePosition.getY())) {
            renderer.addObject(new Path(mousePosition.getY(), mousePosition.getX()));
            renderer.setColourExceptions(new HashMap<>());
        } else {
            HashMap<Integer, Color> exception = new HashMap<>();
            exception.put(renderer.index(mousePosition.getX(), mousePosition.getY()), Color.RED);
            renderer.setColourExceptions(exception);
        }
        renderer.render();
    }

    public boolean canPlaceAt(int x, int y) {
        if (renderer.objectAt(x, y) instanceof Obstacle) {
            Obstacle obstacle = (Obstacle) renderer.objectAt(x, y);
            return obstacle.getType() != ObstacleType.BASE;
        }
        return true;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }


}
