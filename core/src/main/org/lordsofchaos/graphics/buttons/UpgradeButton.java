package org.lordsofchaos.graphics.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.lordsofchaos.EventManager;
import org.lordsofchaos.Game;
import org.lordsofchaos.GameController;
import org.lordsofchaos.graphics.Screen;

public class UpgradeButton extends HoverButton {

    public static boolean maxLevel;
    private static boolean doOnce = true;
    private Texture infoCardTexture;
    private Sprite infoCardSprite;
    private String path;
    public UpgradeButton(String path, float buttonX1, float buttonY1, Screen screenLocation) {
        super(path, buttonX1, buttonY1, screenLocation);
        infoCardTexture = new Texture(
            "UI/InfoCards/infoPanelTier" + (GameController.getDefenderUpgrade() + 1) + ".png");
        infoCardSprite = new Sprite(infoCardTexture);
        infoCardSprite.setPosition(30, 150);
        this.path = path;
    }
    /**
     * When the button is left clicked try to upgrade defender to the next tier.
     * If it fails play an error sound.
     * If it succeeds it also updates the tooltip/infocard about the upgrade.
     */
    @Override
    public void leftButtonAction() {
        if (maxLevel) {
            Game.playSound("ErrorSound");
            return;
        }
        if (!GameController.canDefenderUpgrade()){
            Game.playSound("ErrorSound");
            return;
        }

        selectSound.play(Game.getSoundEffectsVolume());
        Game.instance
            .setGhostTowerType(null); // this alerts Game that a tower isn't being placed, janky yes
        EventManager.defenderUpgrade();
        if (GameController.getDefenderUpgrade() != 3) {
            updateTexture();
        }
    }

    @Override
    public void rightButtonAction() {
    }
    /**
     * Update the tooltip/infocard to display information about the current upgrade
     * by changing the sprite.
     */
    public void updateTexture() {
        infoCardTexture.dispose();
        infoCardTexture = new Texture(
            "UI/InfoCards/infoPanelTier" + (GameController.getDefenderUpgrade() + 1) + ".png");
        infoCardSprite = new Sprite(infoCardTexture);
        infoCardSprite.setPosition(30, 150);
    }
    /**
     * Function that is called every frame.
     * Displays the tooltip/infocard for the upgrade.
     * If defender has reached the last upgrade change the button and no longer display tooltip/infocard.
     * @param x X coordinate of the mouse
     * @param y Y coordinate of the mouse
     * @param batch On which SpriteBatch everything is going to be displayed.
     */
    @Override
    public void update(int x, int y, SpriteBatch batch) {
        if (checkHover(x, y) && !maxLevel) {
            infoCardSprite.draw(batch);
        }
        if (maxLevel && doOnce) {
            super.texture.dispose();
            super.texture = new Texture("UI/NewArtMaybe/defenderUpgradeButtonMAX.png");
            super.sprite = new Sprite(super.texture);
            super.sprite.setPosition(buttonX1, buttonY1);
        }
    }
    /**
     * Reset sprites and texture to the initial state.
     */
    public void reset(){
        doOnce = true;
        maxLevel = false;
        super.texture.dispose();
        super.texture = new Texture(Gdx.files.internal(path));
        super.sprite = new Sprite(super.texture);
        super.sprite.setPosition(buttonX1,buttonY1);
        infoCardTexture.dispose();
        infoCardTexture = new Texture(
                "UI/InfoCards/infoPanelTier" + (GameController.getDefenderUpgrade() + 1) + ".png");
        infoCardSprite = new Sprite(infoCardTexture);
        infoCardSprite.setPosition(30, 150);
    }
}
