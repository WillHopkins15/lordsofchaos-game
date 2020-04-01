package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.LevelEditor;
import org.lordsofchaos.graphics.Screen;

public class EditorButton extends Button
{
    
    private LevelEditor levelEditor;
    
    public EditorButton(String path, float buttonX1, float buttonY1, LevelEditor levelEditor) {
        super(path, buttonX1, buttonY1, Screen.LEVEL_EDITOR);
        this.levelEditor = levelEditor;
    }
    
    @Override
    public void leftButtonAction() {
        selectSound.play(0.75f);
        levelEditor.nextStep();
    }
    
    @Override
    public void rightButtonAction() {
    
    }
    
}
