package org.lordsofchaos.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Leaderboard
{
    
    public static void addWinner(String name, int wave) throws SQLException, ClassNotFoundException {
        LeaderboardRow Winner = new LeaderboardRow(name, wave);
        DatabaseCommunication.addRow(Winner);
    }
    
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
