package org.lordsofchaos.graphics.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.lordsofchaos.Game;
import org.lordsofchaos.GameController;
import org.lordsofchaos.graphics.Screen;

public class PathButton extends HoverButton {

    private static boolean[] selected;
    private int pathNr;
    private boolean justSelected;
    private Button buyPathButton;

    public PathButton(String path, float buttonX1, float buttonY1, Screen screenLocation,
        int pathNr) {
        super(path, buttonX1, buttonY1, screenLocation);
        this.pathNr = pathNr;
        super.sprite.setColor(1, 1, 1, 0);
        super.sprite.setScale(0.5f, 0.5f);
        selected = new boolean[GameController.getLevel().getPaths().size()];
        selected[0] = true;
        this.justSelected = false;
    }

    public static void resetSelected() {
        for (int i = 0; i < selected.length; i++) {
            selected[i] = false;
        }
        selected[0] = true;

    }
    /**
     * When the button is left clicked it highlights the chosen path and deselects the other paths.
     * It also makes the path that was clicked the selected path.
     */
    @Override
    public void leftButtonAction() {
        selectSound.play(Game.getSoundEffectsVolume());
        for (int i = 0; i < selected.length; i++) {
            selected[i] = false;
        }
        selected[pathNr] = true;
        justSelected = true;
        Game.setCurrentPath(pathNr);
    }

    @Override
    public void rightButtonAction() {
        return;
    }

    /**
     * Checks whether the button was clicked
     * @param x X coordinate of the mouse
     * @param y Y coordinate of the mouse
     */
    @Override
    public boolean checkClick(int x, int y) {
        if (x > buttonX1 && x < buttonX2) {
            return y > buttonY1 && y < buttonY2;
        }
        return false;
    }
    /**
     * Function that is called every frame.
     * Displays the highlighting on both the current selected path and if the mouse hovers over another path.
     * Creates the button that the attacker uses to buy a new path.
     * If the button was pressed and the attacker was successful in buying the path
     * it frees up memory by disposing of the buy path button.
     * @param x X coordinate of the mouse
     * @param y Y coordinate of the mouse
     * @param batch On which SpriteBatch everything is going to be displayed
     */
    public void update(int x, int y, SpriteBatch batch) {
        if ((checkHover(x, y) || selected[pathNr])
            && GameController.getWaveState() != GameController.WaveState.Play) {
            super.sprite.setColor(1, 1, 1, 1);
        } else {
            super.sprite.setColor(1, 1, 1, 0);
        }
        if (selected[pathNr] && GameController.getBlockedPaths().contains(pathNr)) {
            if (justSelected) {
                justSelected = false;
                buyPathButton = new BuyPathButton("UI/removeRocksButton.png", buttonX1 + 20,
                    buttonY1 + 70, Screen.ATTACKER_SCREEN, pathNr);
            }
            buyPathButton.getSprite().draw(batch);
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                if (buyPathButton.checkClick(x, y)) {
                    buyPathButton.leftButtonAction();
                }
            }
        } else {
            if (buyPathButton != null) {
                buyPathButton.dispose();
            }
        }
    }

}
