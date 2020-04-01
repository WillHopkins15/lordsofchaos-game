package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.graphics.Screen;

public class MainMenuButton extends Button
{
    protected Screen targetScreen;
    
    public MainMenuButton(String path, float buttonX1, float buttonY1, Screen screenLocation, Screen targetScreen) {
        super(path, buttonX1, buttonY1, screenLocation);
        this.targetScreen = targetScreen;
    }
    
    public void leftButtonAction() {
        selectSound.play(Game.getSoundEffectsVolume());
        Game.currentScreen = targetScreen;
    }
    
    public void rightButtonAction() {
        return;
    }
}
