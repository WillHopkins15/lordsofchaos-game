package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.GameController;
import org.lordsofchaos.graphics.Screen;

public class EndTurnButton extends Button {
    public EndTurnButton(String path, float buttonX1, float buttonY1, Screen screenLocation) {
        super(path, buttonX1, buttonY1,screenLocation);
    }
    public void leftButtonAction(){
        selectSound.play(Game.getSoundEffectsVolume());
        if (Game.instance.getClient() != null) {
            Game.instance.getClient().send("Change Phase");
        } else {
            GameController.endPhase();
        }

    }
    public void rightButtonAction(){
        return;
    }
}
