package org.lordsofchaos.graphics.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.lordsofchaos.graphics.Screen;

public class HoverUI extends HoverButton {

    private Sprite infoCardSprite;
    private Texture infoCardTexture;

    public HoverUI(String path, float buttonX1, float buttonY1, Screen screenLocation,
        String infoCardPath, int infoCardX, int infoCardY) {
        super(path, buttonX1, buttonY1, screenLocation);
        infoCardTexture = new Texture(Gdx.files.internal(infoCardPath));
        infoCardSprite = new Sprite(infoCardTexture);
        infoCardSprite.setPosition(infoCardX, infoCardY);
    }

    /**
     * Function that is called every frame. If the mouse hovers over the button than the
     * tooltip/infocard will be displayed
     *
     * @param x     X coordinate of the mouse
     * @param y     Y coordinate of the mouse
     * @param batch On which sprite batch the tooltip/infocard will be displayed
     */
    @Override
    public void update(int x, int y, SpriteBatch batch) {
        if (checkHover(x, y)) {
            infoCardSprite.draw(batch);
        }
    }

    @Override
    public void leftButtonAction() {
        return;
    }
}
