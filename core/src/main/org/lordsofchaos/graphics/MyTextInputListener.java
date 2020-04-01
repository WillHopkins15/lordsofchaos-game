package org.lordsofchaos.graphics;

import com.badlogic.gdx.Input;
import org.lordsofchaos.GameController;

public class MyTextInputListener implements Input.TextInputListener
{
    @Override
    public void input(String text) {
        GameController.setInputName(text);
    }
    
    @Override
    public void canceled() {
    }
}