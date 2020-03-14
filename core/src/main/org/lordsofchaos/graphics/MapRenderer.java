package org.lordsofchaos.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import org.lordsofchaos.GameController;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.matrixobjects.MatrixObject;
import org.lordsofchaos.matrixobjects.Obstacle;
import org.lordsofchaos.matrixobjects.ObstacleType;
import org.lordsofchaos.matrixobjects.Path;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class MapRenderer extends IsometricTiledMapRenderer {

    private static final int tileOffsetX = -256;
    private static final int tileOffsetY = -170;
    private static final int tileWidth = 512;
    private static final int tileHeight = 512;
    private static final int width = 20;
    private static final int height = 20;
    private MatrixObject[] map = new MatrixObject[400];
    private HashMap<String, Texture> textures = new HashMap<>();
    private HashMap<Integer, Sprite> cachedSprites = new HashMap<>();
    private HashMap<Integer, Color> colourExceptions = new HashMap<>();

    public MapRenderer() {
        super(new TmxMapLoader().load("maps/BlankMap.tmx"));
        File directory = new File("core/assets/maps/tiles");
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files)
            if (!file.isHidden()) textures.put(file.getName(), new Texture(Gdx.files.internal("maps/tiles/" + file.getName())));

    }

    public void setMap(MatrixObject[][] map) {
        for (int y = height - 1; y > -1; y--)
            for (int x = 0; x < width; x++)
                this.map[index(x, y)] = map[y][x];
            cachedSprites.clear();
    }

    public MatrixObject objectAt(int x, int y) {
        return map[index(x, y)];
    }

    public int index(int x, int y) {
        return x + width * y;
    }

    public void addObject(MatrixObject object) {
        int x = object.getMatrixPosition().getX(), y = object.getMatrixPosition().getY();
        refreshSprite(x, y);
        map[index(x, y)] = object;
    }

    public void refreshSprite(int x, int y) {
        for (int i = x - 1; i < x + 2; i++)
            for (int j = y - 1; j < y + 2; j++)
                if (index(i, j) >= 0 && index(i, j) < map.length) {
                    if (objectAt(x, y) instanceof Obstacle)
                        if (((Obstacle) objectAt(x, y)).getType() == ObstacleType.TREE) continue;
                    cachedSprites.remove(index(i, j));
                }
    }

    @Override
    public void render() {
        super.render();
        MatrixObject[] mapCpy = map.clone();
        Arrays.sort(mapCpy);
        getBatch().begin();
        for (MatrixObject object : mapCpy) {
            MatrixCoordinates matrixCoordinates = object.getMatrixPosition();
            RealWorldCoordinates realWorldCoordinates = new RealWorldCoordinates(matrixCoordinates);
            realWorldCoordinates.setX(realWorldCoordinates.getX() - GameController.getScaleFactor() / 2);
            realWorldCoordinates.setY(realWorldCoordinates.getY() - GameController.getScaleFactor() / 2);
            Vector2 coordinates = Conversions.realWorldCooridinateToIsometric(realWorldCoordinates);
            getBatch().setColor(Color.WHITE);
            int i = index(matrixCoordinates.getX(), matrixCoordinates.getY());
            if (colourExceptions.containsKey(i))
                getBatch().setColor(colourExceptions.get(i));
            getBatch().draw(sprite(object), coordinates.x + tileOffsetX, coordinates.y + tileOffsetY, tileWidth, tileHeight);
        }
        getBatch().end();
    }

    private boolean adjacentTileIs(int x, int y, String direction, String type) {
        MatrixObject tile = null;
        switch (direction) {
            case "N": if (y < height - 1) tile = map[index(x, y + 1)]; break;
            case "S": if (y > 0) tile = map[index(x, y - 1)]; break;
            case "E": if (x < width - 1) tile = map[index(x + 1, y)]; break;
            default: if (x > 0) tile = map[index(x - 1, y)]; break;
        }
        if (tile == null) return false;
        switch (type) {
            case "Path": return tile instanceof Path;
            case "Base": if (tile instanceof Obstacle) return ((Obstacle) tile).getType() == ObstacleType.BASE;
            case "River": if (tile instanceof Obstacle) return ((Obstacle) tile).getType() == ObstacleType.RIVER;
            case "RiverN": if (tile instanceof Obstacle)
                return ((Obstacle) tile).getType() == ObstacleType.RIVER && spriteName(tile).contains("N");
            case "RiverS": if (tile instanceof Obstacle)
                return ((Obstacle) tile).getType() == ObstacleType.RIVER && spriteName(tile).contains("S");
            case "RiverE": if (tile instanceof Obstacle)
                return ((Obstacle) tile).getType() == ObstacleType.RIVER && spriteName(tile).contains("E");
            case "RiverW": if (tile instanceof Obstacle)
                return ((Obstacle) tile).getType() == ObstacleType.RIVER && spriteName(tile).contains("W");
            case "Rock": if (tile instanceof Obstacle) return ((Obstacle) tile).getType() == ObstacleType.ROCK;
            default: return false;
        }
    }

    private Sprite sprite(MatrixObject object) {
        int x = object.getMatrixPosition().getX(), y = object.getMatrixPosition().getY();
        if (cachedSprites.containsKey(index(x, y))) return cachedSprites.get(index(x, y));
        Sprite sprite = new Sprite(textures.get(spriteName(object) + ".png"));
        cachedSprites.put(index(x, y), sprite);
        return sprite;
    }

    private String spriteName(MatrixObject object) {
        String s = "blank";
        int x = object.getMatrixPosition().getX(), y = object.getMatrixPosition().getY();
        if (object instanceof Path) {
            s = "path";
            if (adjacentTileIs(x, y, "N", "Path") || (x == 18 && y == 16)) s += "N";
            if (adjacentTileIs(x, y, "S", "Path")) s += "S";
            if (adjacentTileIs(x, y, "E", "Path") || (x == 16 && y == 18)) s += "E";
            if (adjacentTileIs(x, y, "W", "Path")) s += "W";
            if (s.equals("path") || s.equals("pathN") || s.equals("pathS")) s = "pathNS";
            else if (s.equals("pathE") || s.equals("pathW")) s = "pathEW";
            if (s.equals("pathNS") &&
                    adjacentTileIs(x, y, "E", "RiverW") &&
                    adjacentTileIs(x, y, "W", "RiverE")) s = "bridgeNS";
            else if (s.equals("pathEW") &&
                    adjacentTileIs(x, y, "N", "RiverS") &&
                    adjacentTileIs(x, y, "S", "RiverN")) s = "bridgeEW";
        } else if (object instanceof Obstacle) {
            Obstacle obstacle = (Obstacle) object;
            switch (obstacle.getType()) {
                case BASE: s = "dirt"; break;
                case ROCK: s = "rock"; break;
                case TREE: s = "tree" + new Random().nextInt(5); break;
                case RIVER: s = "river";
                    if (adjacentTileIs(x, y, "N", "River")) s += "N";
                    if (adjacentTileIs(x, y, "S", "River")) s += "S";
                    if (adjacentTileIs(x, y, "E", "River")) s += "E";
                    if (adjacentTileIs(x, y, "W", "River")) s += "W";
                    if (s.equals("river") || s.equals("riverN") || s.equals("riverS")) s = "riverNS";
                    else if (s.equals("riverE") || s.equals("riverW")) s = "riverEW";
                    break;
            }
        }
        return s;
    }

    public void setColourExceptions(HashMap<Integer, Color> colourExceptions) { this.colourExceptions = colourExceptions; }

}
