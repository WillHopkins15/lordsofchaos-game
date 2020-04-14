package org.lordsofchaos.graphics.buttons;

import com.badlogic.gdx.Gdx;
import org.lordsofchaos.Game;
import org.lordsofchaos.graphics.Screen;

public class SliderButton extends Button {

    private float xMin;
    private float xMax;
    private float deltaX;
    private int soundType;

    public SliderButton(String path, float buttonX1, float buttonY1, Screen screenLocation,
        int soundType) {
        super(path, buttonX1, buttonY1, screenLocation);
        xMin = super.buttonX1 - 15;
        xMax = xMin + 230;
        buttonX2 = buttonX2 - super.buttonX1 + xMax - 15;
        super.buttonX1 = xMax - 15;
        sprite.setPosition(super.buttonX1, buttonY1);
        this.soundType = soundType;
    }
    /**
     * When the button is left clicked move the slider around.
     * Also makes sure that the slider doesn't go out of bounds and sets the new volumes for
     * music
     */
    @Override
    public void leftButtonAction() {
        if (Game.getMenuOpen()) {
            sprite.setPosition(super.buttonX1, buttonY1);
            float x = Gdx.input.getX() - 10;
            if (x > xMin + 215) {
                x = xMin + 215;
            }
            if (x < xMin + 15) {
                x = xMin + 15;
            }
            deltaX = x - super.buttonX1;
            if (x > xMin && x < xMax) {
                buttonX2 += deltaX;
                super.buttonX1 = x;
                float newVolume = (x - xMin) / (xMax - xMin);
                if (newVolume < 0.07f) {
                    newVolume = 0;
                }
                if (newVolume > 0.9f) {
                    newVolume = 1;
                }
                Game.setSoundTrackVolume(newVolume);
                if (soundType == 0) {
                    Game.setSoundTrackVolume(newVolume);
                } else {
                    Game.setSoundEffectsVolume(newVolume);
                }
            }
        }
    }

    @Override
    public void rightButtonAction() {

    }
    /**
     * Returns whether the slider changes the volume of music or sound effects.
     */
    public int getSoundType() {
        return soundType;
    }
}
