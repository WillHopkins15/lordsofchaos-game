package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.GameController;
import org.lordsofchaos.graphics.Screen;

public class MultiplayerButton extends MainMenuButton
{
    boolean findingGame = false;
    
    public MultiplayerButton(String path, float buttonX1, float buttonY1, Screen screenLocation, Screen targetScreen) {
        super(path, buttonX1, buttonY1, screenLocation, targetScreen);
    }
    
    @Override
    public void leftButtonAction() {
        if(!Game.getSearchingForGame()) {
        selectSound.play(Game.getSoundEffectsVolume());
        Game.multiplayer = true;
        Game.setSearchingForGame(true);
        if (!findingGame) {
            new Thread(() -> {
                findingGame = true;
                if (Game.setupClient()) {
                    if (Game.getClient().isDefender()) {
                        GameController.setPlayerType(true);
                        Game.player = 0;
                        targetScreen = Screen.DEFENDER_SCREEN;
                    } else if (Game.getClient().isAttacker()) {
                        GameController.setPlayerType(false);
                        Game.player = 1;
                        targetScreen = Screen.ATTACKER_SCREEN;
                    }
                    Game.currentScreen = targetScreen;
                } else {
                    Game.multiplayer = false;
                }
                //close the messageBox
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
                findingGame = false;
                Game.setSearchingForGame(false);
            }).start();
        }
        }
    }
    
    @Override
    public void rightButtonAction() {
    
    }
}
