package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.graphics.Screen;

public class ChangePageButton extends Button {

    private int change;

    public ChangePageButton(String path, float buttonX1, float buttonY1, Screen screenLocation,
        int change) {
        super(path, buttonX1, buttonY1, screenLocation);
        this.change = change;
    }

    /**
     * On left click this button should play a sound effect and then change the page of the level select screen
     */
    @Override
    public void leftButtonAction() {
        selectSound.play(Game.getSoundEffectsVolume());
        Game.changeScreen(change);
    }

    @Override
    public void rightButtonAction() {
    }
}
