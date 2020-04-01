package org.lordsofchaos.graphics.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.lordsofchaos.Game;
import org.lordsofchaos.GameController;
import org.lordsofchaos.graphics.Screen;

public class PathButton extends Button {
    private int pathNr;
    private static boolean selected[];
    private boolean justSelected;
    private Button buyPathButton;
    public PathButton(String path, float buttonX1, float buttonY1, Screen screenLocation,int pathNr) {
        super(path, buttonX1, buttonY1, screenLocation);
        this.pathNr = pathNr;
        super.sprite.setColor(1,1,1,0);
        super.sprite.setScale(0.5f,0.5f);
        selected = new boolean[3];
        this.justSelected = false;
    }

    @Override
    public void leftButtonAction() {
        selectSound.play(0.75f);
        for(int i = 0; i < 3; i++)
            selected[i] = false;
        selected[pathNr] = true;
        justSelected = true;
        Game.setCurrentPath(pathNr);
    }

    @Override
    public void rightButtonAction() {
        return;
    }
    @Override
    public boolean checkClick(int x, int y) {
        if (x > buttonX1 && x < buttonX2)
            return y > buttonY1 && y < buttonY2;
        return false;
    }
    public boolean checkHover(int x, int y) {
        if ((x > buttonX1  && x < buttonX2 - 20  && y > buttonY1 + 20 && y < buttonY2 ) || selected[pathNr])
            return true;
        else {
            return false;
            //System.out.println("Selected path: " + pathNr + " Selected[i]: " + selected[0] + " " + selected[1] + " " + selected[2]);
        }
    }
    public void update(int x, int y, SpriteBatch batch){
        if(checkHover(x,y)){
            super.sprite.setColor(1,1,1,1);
        }
        else{
            super.sprite.setColor(1,1,1,0);
        }
        if(selected[pathNr] && GameController.getBlockedPaths().contains(pathNr)){
            if(justSelected){
                justSelected = false;
                buyPathButton = new BuyPathButton("UI/NewArtMaybe/removeRocksButton.png",buttonX1,buttonY1 + 50,Screen.ATTACKER_SCREEN,pathNr);
            }
            buyPathButton.getSprite().draw(batch);
            if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                if (buyPathButton.checkClick(x, y))
                    buyPathButton.leftButtonAction();
            }
        }
        else{
            if(buyPathButton != null)
                buyPathButton.dispose();
        }
    }

}