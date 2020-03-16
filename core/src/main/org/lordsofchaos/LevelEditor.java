package org.lordsofchaos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.graphics.Conversions;
import org.lordsofchaos.matrixobjects.*;

import java.io.File;
import java.util.*;
import java.util.stream.IntStream;

public class LevelEditor {

    private static final int tileOffsetX = -256;
    private static final int tileOffsetY = -170;
    private static final int tileWidth = 512;
    private static final int tileHeight = 512;
    private static final int width = 20;
    private static final int height = 20;
    private MatrixObject[] map = new MatrixObject[400];
    private IsometricTiledMapRenderer renderer;
    private ArrayList<ArrayList<Path>> paths = new ArrayList<ArrayList<Path>>();
    private MatrixCoordinates mousePosition;
    private HashMap<String, Texture> textures = new HashMap<>();
    private MatrixObject hoveredTile;
    boolean placed = false;

    public LevelEditor(IsometricTiledMapRenderer renderer) {
        this.renderer = renderer;
        for (File file : new File("core/assets/maps/tiles").listFiles())
            if (!file.isHidden()) textures.put(file.getName(), new Texture(Gdx.files.internal("maps/tiles/" + file.getName())));
        MatrixObject[][] m = MapGenerator.generateMap(width, height, null, null);
        for (int y = height - 1; y > -1; y--)
            for (int x = 0; x < width; x++)
                addObject(m[y][x]);
        renderer.setMap(new TmxMapLoader().load("maps/BlankMap.tmx"));
    }

    public void addObject(MatrixObject object) {
        int x = object.getMatrixPosition().getX(), y = object.getMatrixPosition().getY();
        map[index(x, y)] = object;
    }

    public void run(MatrixCoordinates mousePosition) {
        if (hoveredTile != null && !placed) {
            if (this.mousePosition != mousePosition) {
                addObject(hoveredTile);
                hoveredTile = map[index(mousePosition.getX(), mousePosition.getY())];
            }
        } else {
            hoveredTile = map[index(mousePosition.getX(), mousePosition.getY())];
            placed = false;
        }
        this.mousePosition = mousePosition;
        if (canPlaceAt(mousePosition.getX(), mousePosition.getY()))
            addObject(new Path(mousePosition.getY(), mousePosition.getX()));
        renderer.render();
        MatrixObject[] mapCpy = map.clone();
        Arrays.sort(mapCpy);
        renderer.getBatch().begin();
        for (MatrixObject object : mapCpy) {
            MatrixCoordinates matrixCoordinates = object.getMatrixPosition();
            RealWorldCoordinates realWorldCoordinates = new RealWorldCoordinates(matrixCoordinates);
            realWorldCoordinates.setX(realWorldCoordinates.getX() - GameController.getScaleFactor() / 2);
            realWorldCoordinates.setY(realWorldCoordinates.getY() - GameController.getScaleFactor() / 2);
            Vector2 coordinates = Conversions.realWorldCooridinateToIsometric(realWorldCoordinates);
            renderer.getBatch().setColor(Color.WHITE);
            if (matrixCoordinates.getX() == mousePosition.getX() && matrixCoordinates.getY() == mousePosition.getY())
                if (!canPlaceAt(mousePosition.getX(), mousePosition.getY()))
                    renderer.getBatch().setColor(Color.RED);
            renderer.getBatch().draw(sprite(object), coordinates.x + tileOffsetX, coordinates.y + tileOffsetY, tileWidth, tileHeight);
        }
        renderer.getBatch().end();
    }

    public boolean canPlaceAt(int x, int y) {
        if (map[index(x, y)] instanceof Obstacle) {
            Obstacle obstacle = (Obstacle) map[index(x, y)];
            if (obstacle.getType() == ObstacleType.BASE) return false;
        }
        return true;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }

    private void setMap(MatrixObject[][] map) {

    }

    private void generateTMX(MatrixObject[][] map) {

    }

    private int index(int x, int y) {
        return x + width * y;
    }

    private Sprite sprite(MatrixObject object) {
        String s = "blank";
        if (object instanceof Path) {
            s = "path";
            boolean hasPathNorth, hasPathSouth, hasPathEast, hasPathWest;
            hasPathNorth = hasPathSouth = hasPathEast = hasPathWest = false;
            int x = object.getMatrixPosition().getX(), y = object.getMatrixPosition().getY();
            if (y < height - 1) hasPathNorth = map[index(x, y + 1)] instanceof Path;
            if (y > 0) hasPathSouth = map[index(x, y - 1)] instanceof Path;
            if (x < width - 1) hasPathEast = map[index(x + 1, y)] instanceof Path;
            if (x > 0) hasPathWest = map[index(x - 1, y)] instanceof Path;
            if (hasPathNorth) s += "N";
            if (hasPathSouth) s += "S";
            if (hasPathEast) s += "E";
            if (hasPathWest) s += "W";
            if (s.equals("path") || s.equals("pathN") || s.equals("pathS")) s = "pathNS";
            else if (s.equals("pathE") || s.equals("pathW")) s = "pathEW";
        } else if (object instanceof Obstacle) {
            Obstacle obstacle = (Obstacle) object;
            switch (obstacle.getType()) {
                case BASE: s = "dirt"; break;
                case ROCK: s = "rock"; break;
                case TREE: s = "tree" + new Random().nextInt(5); break;
                case RIVER:
                    s = "riverNS";

                    break;
            }
        }
        return new Sprite(textures.get(s + ".png"));
    }

}
