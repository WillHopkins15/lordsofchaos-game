package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.LevelEditor;
import org.lordsofchaos.graphics.Screen;
import org.lordsofchaos.matrixobjects.ObstacleType;

public class ObstacleButton extends Button {

    private LevelEditor levelEditor;
    private ObstacleType obstacleType;

    public ObstacleButton(String path, float buttonX1, float buttonY1, LevelEditor levelEditor, ObstacleType obstacleType) {
        super(path, buttonX1, buttonY1, Screen.LEVEL_EDITOR);
        this.levelEditor = levelEditor;
        this.obstacleType = obstacleType;
    }

    @Override
    public void leftButtonAction() {
        selectSound.play(0.75f);
        levelEditor.setCurrentObstacleType(obstacleType);
        levelEditor.run(levelEditor.getMousePosition(), true);
    }

    @Override
    public void rightButtonAction() {

    }

}
