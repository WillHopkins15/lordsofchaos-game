package org.lordsofchaos.database;

import java.sql.Date;

public class LeaderboardRow
{
    
    private int id;
    private String name;
    private int waves;
    private Date date;
    
    public LeaderboardRow(int id, String name, int waves, Date dateTime) {
        this.id = id;
        setName(name);
        verifyWaves(waves);
        this.date = dateTime;
    }
    
    public LeaderboardRow(int id, String name, int waves) {
        this.id = id;
        setName(name);
        verifyWaves(waves);
        this.date = Date.valueOf(java.time.LocalDate.now());
    }
    
    public LeaderboardRow(String name, int waves, Date dateTime) {
        id = -1;
        setName(name);
        verifyWaves(waves);
        this.date = dateTime;
    }
    
    public LeaderboardRow(String name, int waves) {
        id = -1;
        setName(name);
        verifyWaves(waves);
        this.date = Date.valueOf(java.time.LocalDate.now());
    }

    /**
     * Check the number of waves given to this object is possible
     *
     * @param waves number to check
     */
    private void verifyWaves(int waves) {
        if (waves >= 0)
            this.waves = waves;
        else
            this.waves = 100;
    }

    /**
     * Check the input name is not empty
     *
     * @param name name to check
     */
    public static boolean verifyName(String name) {
        return name.length() > 0;
    }

    /**
     * Sets the name of this LeaderBoardRow, first checks if name is legal, if not, gives name the value "Invalid name"
     *
     * @param name name to set
     */
    private void setName(String name) {
        if (verifyName(name))
        {
            this.name = name;
        }
        else
        {
            this.name = "Invalid name";
        }
    }
    
    public int getID() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public int getWaves() {
        return waves;
    }
    
    public Date getDateTime() {
        return date;
    }
    
    public String ToString() {
        return "id: " + getID() + ", name: " + getName() + ", waves: " + getWaves() + ", date: " + getDateTime();
    }
    
}
