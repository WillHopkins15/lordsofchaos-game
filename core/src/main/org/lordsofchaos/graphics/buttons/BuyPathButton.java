package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.GameController;
import org.lordsofchaos.graphics.Screen;

public class BuyPathButton extends Button {
    private int pathNr;
    public BuyPathButton(String path, float buttonX1, float buttonY1, Screen screenLocation, int pathNr) {
        super(path, buttonX1, buttonY1, screenLocation);
        this.pathNr = pathNr;
        super.sprite.setScale(0.50f,0.50f);
    }

    @Override
    public void leftButtonAction() {
        if(GameController.canAttackerUnblockPath(pathNr)) {
            GameController.unblockPath(pathNr);
            System.out.println("Unblocked path: " + pathNr);
        }
        else System.out.println("Can't unblock path");
    }

    @Override
    public void rightButtonAction() {

    }
}
