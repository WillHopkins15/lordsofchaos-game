package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.GameController;
import org.lordsofchaos.graphics.Screen;

public class LoadLevelButton extends Button {

    private String mapJson;

    public LoadLevelButton(String path, float buttonX1, float buttonY1, Screen screenLocation, String mapJson) {
        super(path, buttonX1, buttonY1, screenLocation);
        this.mapJson = mapJson;
    }

    @Override
    public void leftButtonAction() {
        GameController.levelSelected(mapJson);
    }

    @Override
    public void rightButtonAction() {
    }
}
