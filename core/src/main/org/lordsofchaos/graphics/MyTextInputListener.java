package org.lordsofchaos.graphics;

import com.badlogic.gdx.Input;
import org.lordsofchaos.GameController;
import org.lordsofchaos.LevelEditor;

public class MyTextInputListener implements Input.TextInputListener {

    LevelEditor levelEditor;

    public MyTextInputListener() {}

    public MyTextInputListener(LevelEditor levelEditor) {
        this.levelEditor = levelEditor;
    }

    /**
     * Notify the GameController that user has input some text
     *
     * @param text input text
     */
    @Override
    public void input(String text) {
        if (levelEditor == null) GameController.setInputName(text, this);
        else levelEditor.returnName(text);
    }

    /**
     * if the user cancels the text box, treat it as inputting no name, notify GameController
     */
    @Override
    public void canceled() {
        if (levelEditor == null) GameController.setInputName("", this);
    }
}