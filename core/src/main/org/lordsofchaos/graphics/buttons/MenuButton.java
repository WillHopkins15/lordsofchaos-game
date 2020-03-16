package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.graphics.Screen;

public class MenuButton extends Button {
    protected Screen targetScreen;
    public MenuButton(String path, float buttonX1, float buttonY1,Screen screenLocation,Screen targetScreen) {
        super(path, buttonX1, buttonY1,screenLocation);
        this.targetScreen = targetScreen;
    }
    public void leftButtonAction(){
        selectSound.play(0.75f);
        Game.currentScreen = targetScreen;
    }
    public void rightButtonAction(){
        return;
    }
}
