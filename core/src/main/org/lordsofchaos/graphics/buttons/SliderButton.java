package org.lordsofchaos.graphics.buttons;

import com.badlogic.gdx.Gdx;
import org.lordsofchaos.Game;
import org.lordsofchaos.graphics.Screen;

public class SliderButton extends Button {
    private float xMin;
    private float xMax;
    private float deltaX;
    private int soundType;
    public SliderButton(String path, float buttonX1, float buttonY1, Screen screenLocation, int soundType) {
        super(path, buttonX1, buttonY1, screenLocation);
        xMin = buttonX1 - 15;
        xMax = xMin + 230;
        System.out.println("!!!!buttonX1 " + buttonX1 + " buttonX2 " + buttonX2 );
        buttonX2 = buttonX2 - buttonX1 + xMax - 15;
        buttonX1 = xMax - 15;
        System.out.println("buttonX1 " + xMax + " buttonX2 " + buttonX2 );
        sprite.setPosition(buttonX1,buttonY1);
        this.soundType = soundType;
    }

    @Override
    public void leftButtonAction() {
        if(Game.getMenuOpen()) {
            sprite.setPosition(buttonX1,buttonY1);
            //System.out.println("SliderClicked");
            float x = Gdx.input.getX() - 10;
            if(x > xMin + 215) x = xMin + 215;
            if(x < xMin + 15) x = xMin + 15;
            deltaX = x - buttonX1;
            if(x > xMin && x < xMax) {
                buttonX2 += deltaX;
                buttonX1 = x;
                float newVolume = (x - xMin) / (xMax - xMin);
                if(newVolume < 0.07f) newVolume = 0;
                if(newVolume > 0.9f) newVolume = 1;
                Game.setSoundTrackVolume(newVolume);
                if(soundType == 0)
                    Game.setSoundTrackVolume(newVolume);
                else Game.setSoundEffectsVolume(newVolume);
                //System.out.println("X: " + buttonX1);
            }
        }
    }

    @Override
    public void rightButtonAction() {

    }
    public int getSoundType(){
        return soundType;
    }
}
