package org.lordsofchaos.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.lordsofchaos.Game;
import org.lordsofchaos.GameController;

public class DesktopLauncher {

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.width = 1280;
        config.height = 720;
        config.resizable = false;
        GameController.initialise();

        new LwjglApplication(new Game(), config);
    }
}
