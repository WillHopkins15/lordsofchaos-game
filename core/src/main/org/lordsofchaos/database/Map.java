package org.lordsofchaos.database;

import com.badlogic.gdx.utils.Json;

public class Map
{
    
    private int id;
    private String mapName;
    private String json;
    private boolean userGenerated;
    
    public Map(int id, String mapName, Json json, boolean userGenerated) {
        this.id = id;
        if (!(mapName.length() > 0)) {
            this.mapName = "new map";
        } else {
            this.mapName = mapName;
        }
        this.json = json.toString();
        this.userGenerated = userGenerated;
    }
    
    public Map(int id, String mapName, String json, boolean userGenerated) {
        this.id = id;
        if (!(mapName.length() > 0)) {
            this.mapName = "new map";
        } else {
            this.mapName = mapName;
        }
        this.json = json;
        this.userGenerated = userGenerated;
    }
    
    // id will be set to -1 by default, which tells db to create new id
    public Map(String mapName, String json, boolean userGenerated) {
        this.id = -1;
        if (!(mapName.length() > 0)) {
            this.mapName = "new map";
        } else {
            this.mapName = mapName;
        }
        this.json = json;
        this.userGenerated = userGenerated;
    }
    
    public String getJson() {
        return json;
    }
    
    public boolean getUserGenerated() {
        return userGenerated;
    }
    
    public String getMapName() {
        return mapName;
    }
    
    public int getID() {
        return id;
    }
    
}
