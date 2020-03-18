package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.graphics.Screen;

import java.sql.SQLException;

public class LeaderBoardButton extends MenuButton {

    public LeaderBoardButton(String path, float buttonX1, float buttonY1, Screen screenLocation, Screen targetScreen) {
        super(path, buttonX1, buttonY1, screenLocation, targetScreen);
    }

    @Override
    public void leftButtonAction() {
        selectSound.play(0.75f);
        Game.currentScreen = targetScreen;
        try {
            Game.instance.setLeaderBoardTop(5);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rightButtonAction() {

    }
}
