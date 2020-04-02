package org.lordsofchaos.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Leaderboard
{

    /**
     * When a player wins the game, add their name to the leaderboard
     *
     * @param name player's name (input into a popup box by the player)
     * @param wave the wave they won the game on
     */
    public static void addWinner(String name, int wave) throws SQLException, ClassNotFoundException {
        LeaderboardRow Winner = new LeaderboardRow(name, wave);
        DatabaseCommunication.addRow(Winner);
    }

    /**
     * Gets the highest scoring players from the database, returns their information as a 2d array of strings
     *
     * @param count rows to fetch
     */
    public static String[][] getTop(int count) throws SQLException, ClassNotFoundException {
        List<LeaderboardRow> Top = new ArrayList<>();
        Top = DatabaseCommunication.getHighScores(count);
        
        String[][] displayTop = new String[count][3];
        
        for (int i = 0; i < Top.size(); i++) {
            displayTop[i][0] = Top.get(i).getName();
            displayTop[i][1] = Integer.toString(Top.get(i).getWaves());
            displayTop[i][2] = (Top.get(i).getDateTime()).toString();
        }
        
        return displayTop;
        
    }
}
