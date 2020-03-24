package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.graphics.Screen;

public class PathButton extends Button {
    private int pathNr;
    private static boolean selected[];
    public PathButton(String path, float buttonX1, float buttonY1, Screen screenLocation,int pathNr) {
        super(path, buttonX1, buttonY1, screenLocation);
        this.pathNr = pathNr;
        super.sprite.setColor(1,1,1,0);
        super.sprite.setScale(0.5f,0.5f);
        selected = new boolean[3];
    }

    @Override
    public void leftButtonAction() {
        selectSound.play(0.75f);
        for(int i = 0; i < 3; i++)
            selected[i] = false;
        selected[pathNr] = true;

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
    public void checkHover(int x, int y) {
        if ((x > buttonX1  && x < buttonX2 - 20  && y > buttonY1 + 20 && y < buttonY2 ) || selected[pathNr])
            super.sprite.setColor(1,1,1,1);
        else {
            super.sprite.setColor(1, 1, 1, 0);
            System.out.println("Selected path: " + pathNr + " Selected[i]: " + selected[0] + " " + selected[1] + " " + selected[2]);
        }
    }


}
