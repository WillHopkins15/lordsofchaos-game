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
import org.lordsofchaos.gameobjects.GameObject;
import org.lordsofchaos.gameobjects.towers.DefenderTower;
import org.lordsofchaos.gameobjects.towers.Tower;
import org.lordsofchaos.matrixobjects.MatrixObject;
import org.lordsofchaos.matrixobjects.Obstacle;
import org.lordsofchaos.matrixobjects.ObstacleType;
import org.lordsofchaos.matrixobjects.Path;

import java.io.File;
import java.util.*;

public class MapRenderer extends IsometricTiledMapRenderer
{
    
    public static final int width = 20;
    public static final int height = 20;
    private static final int tileOffsetX = -256;
    private static final int tileOffsetY = -170;
    private static final int tileWidth = 512;
    private static final int tileHeight = 512;
    private MatrixObject[] map = new MatrixObject[400];
    private MatrixObject[] sortedMap = new MatrixObject[400];
    private HashMap<String, Texture> textures = new HashMap<>();
    private HashMap<Integer, Sprite> cachedTiles = new HashMap<>();
    private HashMap<GameObject, Sprite> cachedSprites = new HashMap<>();
    private HashMap<Integer, Color> colourExceptions = new HashMap<>();
    private boolean mapUpdated = true;
    private boolean levelEditing = false;
    
    public MapRenderer() {
        super(new TmxMapLoader().load("maps/BlankMap.tmx"));
        File tileDirectory = new File("core/assets/maps/tiles");
        File[] tileFiles = tileDirectory.listFiles();
        assert tileFiles != null;
        for (File file : tileFiles)
            if (!file.isHidden())
                textures.put(file.getName(), new Texture(Gdx.files.internal("maps/tiles/" + file.getName())));
        File towerDirectory = new File("core/assets/towers/sprites");
        File[] towerFiles = towerDirectory.listFiles();
        assert towerFiles != null;
        for (File file : towerFiles)
            if (!file.isHidden())
                textures.put(file.getName(), new Texture(Gdx.files.internal("towers/sprites/" + file.getName())));
        File troopDirectory = new File("core/assets/troops/");
        File[] troopFiles = troopDirectory.listFiles();
        assert troopFiles != null;
        for (File file : troopFiles)
            if (!file.isHidden())
                textures.put(file.getName(), new Texture(Gdx.files.internal("troops/" + file.getName())));
    }
    
    public void setMap(MatrixObject[][] map) {
        for (int y = height - 1; y > -1; y--)
            for (int x = 0; x < width; x++)
                this.map[index(x, y)] = map[y][x];
        cachedTiles.clear();
        mapUpdated = true;
    }
    
    public MatrixObject objectAt(MatrixCoordinates mc) {
        return objectAt(mc.getX(), mc.getY());
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
        mapUpdated = true;
    }
    
    public void refreshSprite(int x, int y) {
        for (int i = x - 1; i < x + 2; i++)
            for (int j = y - 1; j < y + 2; j++)
                if (index(i, j) >= 0 && index(i, j) < map.length) {
                    // if (objectAt(x, y) instanceof Obstacle)
                    // if (((Obstacle) objectAt(x, y)).getType() == ObstacleType.TREE) continue;
                    cachedTiles.remove(index(i, j));
                }
    }
    
    @Override
    public void render() {
        super.render();
        if (mapUpdated) {
            sortedMap = map.clone();
            Arrays.sort(sortedMap);
            mapUpdated = false;
        }
        
        
        getBatch().begin();
        
        for (MatrixObject object : sortedMap) {
            MatrixCoordinates matrixCoordinates = object.getMatrixPosition();
            Vector2 coordinates = Conversions.matrixCooridinateToIsometric(matrixCoordinates);
            getBatch().setColor(Color.WHITE);
            int i = index(matrixCoordinates.getX(), matrixCoordinates.getY());
            if (colourExceptions.containsKey(i)) getBatch().setColor(colourExceptions.get(i));
            getBatch().draw(sprite(object), coordinates.x + tileOffsetX, coordinates.y + tileOffsetY, tileWidth, tileHeight);
        }
        
        getBatch().end();
        
        if (levelEditing) return;
        
        List<GameObject> objectsToAdd = new ArrayList<>();
        objectsToAdd.addAll(GameController.getTowers());
        objectsToAdd.addAll(GameController.getTroops());
        objectsToAdd.addAll(GameController.getDefenderTowers());
        Collections.sort(objectsToAdd);
        
        getBatch().begin();
        
        for (GameObject object : objectsToAdd) {
            Sprite sprite = sprite(object);
            Vector2 coordinates = Conversions.realWorldCooridinateToIsometric(object.getRealWorldCoordinates());
            float w = 48f, a = sprite.getHeight() / sprite.getWidth();
            if (object instanceof DefenderTower) w = 72f;
            else if (object instanceof Tower) if (!((Tower) object).getIsCompleted())
                getBatch().setColor(0.5f, 0.5f, 0.5f, 0.5f);
            getBatch().draw(sprite, coordinates.x - w / 2, coordinates.y - w / 6, w, w * a);
            getBatch().setColor(Color.WHITE);
        }
        
        getBatch().end();
    }
    
    public boolean adjacentTileIs(int x, int y, String direction, String type) {
        MatrixObject tile = null;
        if (x < 0 || y < 0 || x >= width || y >= height) return false;
        switch (direction) {
            case "N":
                if (y < height - 1) tile = map[index(x, y + 1)];
                break;
            case "S":
                if (y > 0) tile = map[index(x, y - 1)];
                break;
            case "E":
                if (x < width - 1) tile = map[index(x + 1, y)];
                break;
            case "W":
                if (x > 0) tile = map[index(x - 1, y)];
                break;
            case "NE":
                if (y < height - 1 && x < width - 1) tile = map[index(x + 1, y + 1)];
                break;
            case "NW":
                if (y < height - 1 && x > 0) tile = map[index(x - 1, y + 1)];
                break;
            case "SE":
                if (y > 0 && x < width - 1) tile = map[index(x + 1, y - 1)];
                break;
            case "SW":
                if (y > 0 && x > 0) tile = map[index(x - 1, y - 1)];
                break;
            default:
                return false;
        }
        if (tile == null) return false;
        switch (type) {
            case "Path":
                return tile instanceof Path;
            case "Base":
                if (tile instanceof Obstacle) return ((Obstacle) tile).getType() == ObstacleType.BASE;
            case "River":
                if (tile instanceof Obstacle) return ((Obstacle) tile).getType() == ObstacleType.RIVER;
            case "RiverN":
                if (tile instanceof Obstacle)
                    return ((Obstacle) tile).getType() == ObstacleType.RIVER && spriteName(tile).contains("N");
            case "RiverS":
                if (tile instanceof Obstacle)
                    return ((Obstacle) tile).getType() == ObstacleType.RIVER && spriteName(tile).contains("S");
            case "RiverE":
                if (tile instanceof Obstacle)
                    return ((Obstacle) tile).getType() == ObstacleType.RIVER && spriteName(tile).contains("E");
            case "RiverW":
                if (tile instanceof Obstacle)
                    return ((Obstacle) tile).getType() == ObstacleType.RIVER && spriteName(tile).contains("W");
            case "Rock":
                if (tile instanceof Obstacle) return ((Obstacle) tile).getType() == ObstacleType.ROCK;
            default:
                return false;
        }
    }
    
    public Sprite sprite(GameObject object) {
        if (cachedSprites.containsKey(object)) return cachedSprites.get(object);
        Sprite sprite = new Sprite(textures.get(object.getSpriteName() + ".png"));
        cachedSprites.put(object, sprite);
        return sprite;
    }
    
    private Sprite sprite(MatrixObject object) {
        int index = index(object.getMatrixPosition().getX(), object.getMatrixPosition().getY());
        if (cachedTiles.containsKey(index)) return cachedTiles.get(index);
        Sprite sprite = new Sprite(textures.get(spriteName(object) + ".png"));
        cachedTiles.put(index, sprite);
        return sprite;
    }
    
    private String spriteName(MatrixObject object) {
        String s = "blank";
        int x = object.getMatrixPosition().getX(), y = object.getMatrixPosition().getY();
        if (object instanceof Path) {
            Path path = (Path) object;
            if (path.isSpawn()) return "spawn" + (y == 0 ? "N" : "E");
            s = "path";
            if (adjacentTileIs(x, y, "N", "Path") || (x == 18 && y == 16)) s += "N";
            if (adjacentTileIs(x, y, "S", "Path")) s += "S";
            if (adjacentTileIs(x, y, "E", "Path") || (x == 16 && y == 18)) s += "E";
            if (adjacentTileIs(x, y, "W", "Path")) s += "W";
            if (s.equals("path") || s.equals("pathN") || s.equals("pathS")) s = "pathNS";
            else if (s.equals("pathE") || s.equals("pathW")) s = "pathEW";
            if (s.equals("pathNS") && adjacentTileIs(x, y, "E", "RiverW") &&
                    adjacentTileIs(x, y, "W", "RiverE")) s = "bridgeNS";
            else if (s.equals("pathEW") && adjacentTileIs(x, y, "N", "RiverS") &&
                    adjacentTileIs(x, y, "S", "RiverN")) s = "bridgeEW";
        } else if (object instanceof Obstacle) {
            Obstacle obstacle = (Obstacle) object;
            switch (obstacle.getType()) {
                case BASE:
                    return "dirt";
                case ROCK:
                    return "rock";
                case TREE:
                    return "tree4"; // + new Random().nextInt(5);
                case RIVER:
                    s = "river";
                    if (adjacentTileIs(x, y, "N", "River")) s += "N";
                    else if (adjacentTileIs(x, y, "S", "River")) s += "S";
                    if (adjacentTileIs(x, y, "E", "River")) s += "E";
                    else if (adjacentTileIs(x, y, "W", "River")) s += "W";
                    //if (s.length() > 7) s = "riverNS";
                    if (s.equals("river") || s.equals("riverN") || s.equals("riverS")) s = "riverNS";
                    else if (s.equals("riverE") || s.equals("riverW")) s = "riverEW";
            }
        }
        return s;
    }
    
    public void setLevelEditing(boolean levelEditing) {
        this.levelEditing = levelEditing;
    }
    
    public void setColourExceptions(HashMap<Integer, Color> colourExceptions) {
        this.colourExceptions = colourExceptions;
    }
    
}
