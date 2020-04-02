package org.lordsofchaos.graphics;

import com.badlogic.gdx.Input;
import org.lordsofchaos.GameController;

public class MyTextInputListener implements Input.TextInputListener
{
    @Override
    public void input(String text) {
        GameController.setInputName(text, this);
    }
    
    @Override
    public void canceled() {
        GameController.setInputName("", this);
    }
}