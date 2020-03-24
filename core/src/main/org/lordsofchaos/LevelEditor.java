package org.lordsofchaos;

import com.badlogic.gdx.graphics.Color;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.graphics.MapRenderer;
import org.lordsofchaos.matrixobjects.MatrixObject;
import org.lordsofchaos.matrixobjects.Obstacle;
import org.lordsofchaos.matrixobjects.ObstacleType;
import org.lordsofchaos.matrixobjects.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LevelEditor
{
    
    private static final int MAX_SPAWNS = 4;
    private MapRenderer renderer;
    private MatrixCoordinates mousePosition;
    private MatrixObject hoveredTile;
    private boolean placed = false;
    private EditorPhase editorPhase = EditorPhase.SPAWNS;
    private List<Path> spawns = new ArrayList<>();
    private Color cantPlace = new Color(0.4f, 0.4f, 0.4f, 1f);
    private Color canPlace = new Color(0.6f, 1f, 0.6f, 1f);
    
    public LevelEditor(MapRenderer renderer) {
        this.renderer = renderer;
        renderer.setMap(MapGenerator.generateMap(20, 20, null, null));
    }
    
    public void run(MatrixCoordinates mousePosition) {
        int x = mousePosition.getX(), y = mousePosition.getY();
        if (hoveredTile != null && !placed) {
            if (this.mousePosition != mousePosition) {
                renderer.addObject(hoveredTile);
                hoveredTile = renderer.objectAt(x, y);
            }
        } else {
            hoveredTile = renderer.objectAt(x, y);
            if (placed) {
                if (editorPhase == EditorPhase.SPAWNS) {
                    spawns.add((Path) hoveredTile);
                }
            }
            placed = false;
        }
        this.mousePosition = mousePosition;
        HashMap<Integer, Color> exceptions = new HashMap<>();
        if (editorPhase == EditorPhase.SPAWNS) {
            if (spawns.size() < MAX_SPAWNS)
                for (int i = 0; i < 2; i++)
                    for (int j = 1; j < MapRenderer.width; j++)
                        exceptions.put(renderer.index(i == 0 ? 0 : j, i == 0 ? j : 0), canPlace);
            for (Path spawn : spawns)
                exceptions.remove(renderer.index(spawn.getMatrixPosition().getX(), spawn.getMatrixPosition().getY()));
        }
        if (canPlaceAt(x, y)) {
            if (editorPhase == EditorPhase.SPAWNS) {
                Path path = new Path(y, x);
                path.setSpawn(true);
                renderer.addObject(path);
                exceptions.remove(renderer.index(x, y));
            }
        } else exceptions.put(renderer.index(x, y), cantPlace);
        renderer.setColourExceptions(exceptions);
        renderer.render();
    }
    
    public boolean canPlaceAt(int x, int y) {
        if (renderer.objectAt(x, y) instanceof Obstacle) {
            return ((Obstacle) renderer.objectAt(x, y)).getType() != ObstacleType.BASE;
        } else if (editorPhase == EditorPhase.SPAWNS) {
            if ((x == 0) == (y == 0) || spawns.size() == MAX_SPAWNS) return false;
            for (Path spawn : spawns) {
                int spawnX = spawn.getMatrixPosition().getX();
                int spawnY = spawn.getMatrixPosition().getY();
                if ((x == 0 && spawnX == 0 && (spawnY == y + 1 || spawnY == y - 1 || spawnY == y)) ||
                        (y == 0 && spawnY == 0 && (spawnX == x + 1 || spawnX == x - 1 || spawnX == x)))
                    return false;
            }
        }
        return true;
    }
    
    public void remove() {
    
    }
    
    public void setPlaced(boolean placed) {
        this.placed = placed && canPlaceAt(mousePosition.getX(), mousePosition.getY());
    }
    
    public enum EditorPhase
    {
        SPAWNS,
        PATHS,
        OBSTACLES
    }
}
