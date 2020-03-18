package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.graphics.Screen;

public class LevelEditorButton extends MenuButton {

    public LevelEditorButton(String path, float buttonX1, float buttonY1, Screen screenLocation, Screen targetScreen) {
        super(path, buttonX1, buttonY1, screenLocation, targetScreen);
    }

    @Override
    public void leftButtonAction() {
        selectSound.play(0.75f);
        Game.currentScreen = targetScreen;
    }

    @Override
    public void rightButtonAction() {

    }
}
