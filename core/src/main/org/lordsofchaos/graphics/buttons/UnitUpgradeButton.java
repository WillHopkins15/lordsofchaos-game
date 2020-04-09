package org.lordsofchaos.graphics.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.lordsofchaos.EventManager;
import org.lordsofchaos.Game;
import org.lordsofchaos.GameController;
import org.lordsofchaos.graphics.Screen;

public class UnitUpgradeButton extends HoverButton {
    private String path;
    private BitmapFont font;
    private Texture infoCardTexture;
    private Sprite infoCardSprite;
    private Texture textureCooldown;
    private Sprite spriteCooldown;
    private Sprite spriteActive;
    private int currentUpgrade;
    public UnitUpgradeButton(String path, float buttonX1, float buttonY1, Screen screenLocation) {
        super(path + "1.png" , buttonX1, buttonY1, screenLocation);
        this.path = path;
        currentUpgrade = 1;
        infoCardTexture = new Texture("UI/InfoCards/infoPanelTier1.png");
        infoCardSprite = new Sprite(infoCardTexture);
        infoCardSprite.setPosition(30,150);
        font = Game.getBloxyFont();
        spriteActive = super.sprite;
        textureCooldown = new Texture(Gdx.files.internal(path + currentUpgrade + "Cooldown.png"));
        spriteCooldown = new Sprite(textureCooldown);
        spriteCooldown.setPosition(buttonX1,buttonY1);
        super.sprite = spriteCooldown;
    }
    @Override
    public void update(int x, int y,SpriteBatch batch){
        if(GameController.attackerEarnedUpgrade()) {
            super.sprite = spriteActive;
            System.out.println("Can buy upgrade");
        }
        if(GameController.getUnitUpgradeLevel() != currentUpgrade) {
            currentUpgrade = GameController.getUnitUpgradeLevel();
            createSprite(path);
        }
        if(checkHover(x,y))
            infoCardSprite.draw(batch);
        //System.out.println(currentUpgrade);
        sprite.draw(batch);
    }
    private void createSprite(String path){
        super.texture.dispose();
        super.texture = new Texture(Gdx.files.internal(path + currentUpgrade + ".png"));
        super.sprite = new Sprite(texture);
        super.sprite.setPosition(buttonX1,buttonY1);
        spriteActive = super.sprite;
        textureCooldown = new Texture(Gdx.files.internal(path + currentUpgrade + "Cooldown.png"));
        spriteCooldown = new Sprite(textureCooldown);
        spriteCooldown.setPosition(buttonX1,buttonY1);
        super.sprite = spriteCooldown;
    }
    @Override
    public void leftButtonAction() {
        if(GameController.canAttackerAffordUpgrade() && GameController.attackerEarnedUpgrade() && currentUpgrade != 4) {
            EventManager.attackerUpgrade();
            selectSound.play(Game.getSoundEffectsVolume());
            Game.createAlert(2,font,"Troops have been upgraded!",500,600,null);
        }
        else Game.playSound("ErrorSound");
    }

}
