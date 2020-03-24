package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.graphics.Screen;

public class PathButton extends Button {
    private int pathNr;
    public PathButton(String path, float buttonX1, float buttonY1, Screen screenLocation,int pathNr) {
        super(path, buttonX1, buttonY1, screenLocation);
        this.pathNr = pathNr;
        super.sprite.setScale(0.5f,0.5f);
    }

    @Override
    public void leftButtonAction() {
        selectSound.play(0.75f);
        Game.setCurrentPath(pathNr);
    }

    @Override
    public void rightButtonAction() {
        return;
    }

}
