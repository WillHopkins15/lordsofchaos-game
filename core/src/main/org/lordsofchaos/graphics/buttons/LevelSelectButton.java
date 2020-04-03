package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.graphics.Screen;

public class LevelSelectButton extends MainMenuButton {
    public LevelSelectButton(String path, float buttonX1, float buttonY1, Screen screenLocation, Screen targetScreen) {
        super(path, buttonX1, buttonY1, screenLocation, targetScreen);
    }

    @Override
    public void leftButtonAction() {
        selectSound.play(Game.getSoundEffectsVolume());
        Game.currentScreen = targetScreen;
        Game.previousSelectPage = -1; // reset this so that the first page an be loaded
    }
}
