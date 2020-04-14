package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.GameController;
import org.lordsofchaos.graphics.Screen;

public class EndTurnButton extends Button {

    public EndTurnButton(String path, float buttonX1, float buttonY1, Screen screenLocation) {
        super(path, buttonX1, buttonY1, screenLocation);
    }
    /**
     * When the button is left clicked the game ends the current players turn
     */
    public void leftButtonAction() {
        if(GameController.endTurnButtonEnabled()) {
            selectSound.play(Game.getSoundEffectsVolume());
            if (Game.multiplayer) {
                Game.getClient().changePhase();
            } else {
                GameController.endPhase();
            }
            Game.setBuildMode(false);
        }
        else Game.playSound("ErrorSound");
    }

    public void rightButtonAction() {
        return;
    }
}
