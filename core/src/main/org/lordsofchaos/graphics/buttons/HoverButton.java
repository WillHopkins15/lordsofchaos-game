package org.lordsofchaos.graphics.buttons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.lordsofchaos.graphics.Screen;

abstract public class HoverButton extends Button {

    protected float scale;

    public HoverButton(String path, float buttonX1, float buttonY1, Screen screenLocation) {
        super(path, buttonX1, buttonY1, screenLocation);

    }

    @Override
    public void leftButtonAction() {

    }

    @Override
    public void rightButtonAction() {

    }

    /**
     * Checks whether the mouse is hovering over the button
     *
     * @param x X coordinate of the mouse
     * @param y Y coordinate of the mouse
     * @return Whether the mouse is hovering ouver the button or not
     */
    public boolean checkHover(int x, int y) {
        return (x > buttonX1 && x < buttonX2 && y > buttonY1 && y < buttonY2);
    }

    abstract public void update(int x, int y, SpriteBatch batch);
}
