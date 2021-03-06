package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.GameController;
import org.lordsofchaos.graphics.Screen;

public class PlayerButton extends MainMenuButton {

    boolean ifMultiplayer;
    int targetPlayerType;
    private int playerType;

    public PlayerButton(String path, float buttonX1, float buttonY1, Screen screenLocation,
        Screen targetScreen, int playerType) {
        super(path, buttonX1, buttonY1, screenLocation, targetScreen);
        this.playerType = playerType;
    }

    /**
     * When the button is left clicked
     */
    @Override
    public void leftButtonAction() {
        if (!ifMultiplayer) {
            selectSound.play(Game.getSoundEffectsVolume());
            Game.currentScreen = targetScreen;
            Game.player = playerType;
            boolean tmpBool = playerType == 1;
            GameController.setPlayerType(tmpBool);
        } else {
            if (playerType == targetPlayerType) {
                Game.currentScreen = targetScreen;
                Game.player = playerType;
                boolean tmpBool = playerType == 1;
                GameController.setPlayerType(tmpBool);
            }
        }
    }

    public void IsMultiplayer(int targetPlayerType) {
        ifMultiplayer = true;
        this.targetPlayerType = targetPlayerType;
    }

    public void rightButtonAction() {
        return;
    }
}
