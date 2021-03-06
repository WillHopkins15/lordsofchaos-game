package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.EventManager;
import org.lordsofchaos.Game;
import org.lordsofchaos.GameController;
import org.lordsofchaos.graphics.Screen;

public class BuyPathButton extends Button {

    private int pathNr;

    public BuyPathButton(String path, float buttonX1, float buttonY1, Screen screenLocation,
        int pathNr) {
        super(path, buttonX1, buttonY1, screenLocation);
        this.pathNr = pathNr;
        //super.sprite.setScale(0.50f,0.50f);
    }

    /**
     * When the button is left clicked the attacker will try to buy the selected path
     */
    @Override
    public void leftButtonAction() {
        if (GameController.canAttackerUnblockPath()) {
            EventManager.unblockPath(pathNr);
            selectSound.play(Game.getSoundEffectsVolume());
        } else {
            System.out.println("Can't unblock path");
        }
    }

    @Override
    public void rightButtonAction() {

    }
}
