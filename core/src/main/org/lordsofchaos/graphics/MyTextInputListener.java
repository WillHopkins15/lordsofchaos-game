package org.lordsofchaos.graphics;

import com.badlogic.gdx.Input;
import org.lordsofchaos.GameController;

public class MyTextInputListener implements Input.TextInputListener
{
    /**
     * Notify the GameController that user has input some text
     *
     * @param text input text
     */
    @Override
    public void input(String text) {
        GameController.setInputName(text, this);
    }

    /**
     * if the user cancels the text box, treat it as inputting no name, notify GameController
     */
    @Override
    public void canceled() {
        GameController.setInputName("", this);
    }
}