package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.graphics.Screen;

public class LevelSelectButton extends MainMenuButton {

    public LevelSelectButton(String path, float buttonX1, float buttonY1, Screen screenLocation,
        Screen targetScreen) {
        super(path, buttonX1, buttonY1, screenLocation, targetScreen);
    }

    /**
     * On left click this button should play a sound effect and then load the level select screen,
     * and also set previousSelectPage to -1 as an initialiser
     */
    @Override
    public void leftButtonAction() {
        if (!Game.getSearchingForGame()) {
            selectSound.play(Game.getSoundEffectsVolume());
            Game.currentScreen = targetScreen;
            Game.previousSelectPage = -1; // reset this so that the first page an be loaded
        }
    }
}
