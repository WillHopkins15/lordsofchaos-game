package org.lordsofchaos.graphics;

import org.lordsofchaos.Game;
import org.lordsofchaos.GameController;

public class EndTurnButton extends Button {
    public EndTurnButton(String path, float buttonX1, float buttonY1,Screen screenLocation) {
        super(path, buttonX1, buttonY1,screenLocation);
    }
    public void leftButtonAction(){
        selectSound.play(0.75f);
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
