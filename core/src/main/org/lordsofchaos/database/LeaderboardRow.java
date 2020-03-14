package org.lordsofchaos.database;
import java.sql.Date;

public class LeaderboardRow {

    private int id;
    private String name;
    private int waves;
    private Date date;

    private void verifyWaves(int waves)
    {
        if (waves > 0)
            this.waves = waves;
        else
            this.waves = 100;
    }

    private void verifyName(String name)
    {
        if (name.length() > 0)
            this.name = name;
        else
            this.name = "Invalid name";
    }

    public LeaderboardRow(int id, String name, int waves, Date dateTime)
    {
        this.id = id;
        verifyName(name);
        verifyWaves(waves);
        this.date = dateTime;
    }

    public LeaderboardRow(int id, String name, int waves)
    {
        this.id = id;
        verifyName(name);
        verifyWaves(waves);
        this.date = Date.valueOf(java.time.LocalDate.now());
    }

    public LeaderboardRow(String name, int waves, Date dateTime)
    {
        id = -1;
        verifyName(name);
        verifyWaves(waves);
        this.date = dateTime;
    }

    public LeaderboardRow(String name, int waves)
    {
        id = -1;
        verifyName(name);
        verifyWaves(waves);
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
