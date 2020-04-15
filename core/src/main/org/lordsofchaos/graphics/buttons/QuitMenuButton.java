package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.graphics.Screen;

public class QuitMenuButton extends MainMenuButton {

    public QuitMenuButton(String path, float buttonX1, float buttonY1, Screen screenLocation,
        Screen screenTarget) {
        super(path, buttonX1, buttonY1, screenLocation, screenTarget);
    }

    /**
     * When the button is left clicked reset the game to its initial state. This happens when the
     * quit button is clicked that sends the player to the main menu.
     */
    @Override
    public void leftButtonAction() {
        super.leftButtonAction();
        Game.instance.resetGame();
    }
}
