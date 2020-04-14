package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.graphics.Screen;

public class MenuButton extends Button {

    public MenuButton(String path, float buttonX1, float buttonY1, Screen screenLocation) {
        super(path, buttonX1, buttonY1, screenLocation);
    }

    /**
     * When the button is left clicked if the menu is open, close the menu
     */
    @Override
    public void leftButtonAction() {
        if (Game.getMenuOpen()) {
            selectSound.play(Game.getSoundEffectsVolume());
            Game.setMenuOpen(false);
        }
    }

    @Override
    public void rightButtonAction() {

    }
}
