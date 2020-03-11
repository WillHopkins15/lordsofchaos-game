package org.lordsofchaos.database;
import java.sql.Date;

public class LeaderboardRow {

    private int id;
    private String name;
    private int waves;
    private Date date;

    public LeaderboardRow(int id, String name, int waves, Date dateTime)
    {
        this.id = id;
        this.name = name;
        this.waves = waves;
        this.date = dateTime;
    }

    public LeaderboardRow(int id, String name, int waves)
    {
        this.id = id;
        this.name = name;
        this.waves = waves;
        this.date = Date.valueOf(java.time.LocalDate.now());
    }

    public LeaderboardRow(String name, int waves, Date dateTime)
    {
        id = -1;
        this.name = name;
        this.waves = waves;
        this.date = dateTime;
    }

    public LeaderboardRow(String name, int waves)
    {
        id = -1;
        this.name = name;
        this.waves = waves;
        this.date = Date.valueOf(java.time.LocalDate.now());
    }

    public int getID()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public int getWaves()
    {
        return waves;
    }

    public Date getDateTime()
    {
        return date;
    }

    public String ToString()
    {
        return "id: " + getID() + ", name: " + getName() + ", waves: " + getWaves() + ", date: " + getDateTime();
    }

}
