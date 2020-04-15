package org.lordsofchaos.graphics.buttons;

import java.sql.SQLException;
import org.lordsofchaos.Game;
import org.lordsofchaos.graphics.Screen;

public class LeaderBoardButton extends MainMenuButton {

    public LeaderBoardButton(String path, float buttonX1, float buttonY1, Screen screenLocation,
        Screen targetScreen) {
        super(path, buttonX1, buttonY1, screenLocation, targetScreen);
    }

    /**
     * On left click this button should play a sound effect and then load the leaderboard page, and
     * display the top five scores from the database
     */
    @Override
    public void leftButtonAction() {
        if (!Game.getSearchingForGame()) {
            selectSound.play(Game.getSoundEffectsVolume());
            Game.currentScreen = targetScreen;
            try {
                Game.instance.setLeaderBoardTop(5);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void rightButtonAction() {

    }
}
