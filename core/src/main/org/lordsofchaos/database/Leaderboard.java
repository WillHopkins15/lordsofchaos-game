package org.lordsofchaos.database;

import org.lordsofchaos.player.Player;
import org.lordsofchaos.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import java.sql.SQLException;


public class Leaderboard {

    public static void addWinner(Player winner, int wave) throws SQLException, ClassNotFoundException {
        LeaderboardRow Winner = new LeaderboardRow(winner.getName(),wave);
        DatabaseCommunication.addRow(Winner);
    }

    public static String[][] getTop(int count) throws SQLException, ClassNotFoundException {
        List<LeaderboardRow> Top = new ArrayList<>();
        Top = DatabaseCommunication.getHighScores(count);

        String[][] displayTop = new String[count][3];

        for (int i = 0; i < count; i++) {
            displayTop[i][0] = Top.get(i).getName();
            displayTop[i][1] = Integer.toString(Top.get(i).getWaves());
            displayTop[i][2] = (Top.get(i).getDateTime()).toString();
        }

        return displayTop;

    }
}
