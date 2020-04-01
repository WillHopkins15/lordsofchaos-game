package org.lordsofchaos.gameobjects;

public enum TowerType
{
    
    type1("TowerType1"), type2("TowerType2"), type3("TowerType3");
    
    private String spriteName;
    
    TowerType(String spriteName) {
        this.spriteName = spriteName;
    }
    
    public String getSpriteName() {
        return spriteName;
    }
}
