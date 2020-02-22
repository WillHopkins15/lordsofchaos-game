package org.lordsofchaos;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.GameObject;
import org.lordsofchaos.gameobjects.TowerType;
import org.lordsofchaos.gameobjects.towers.Tower;
import org.lordsofchaos.graphics.Button;
import org.lordsofchaos.graphics.Screen;
import org.lordsofchaos.network.GameClient;
import org.lordsofchaos.player.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game extends ApplicationAdapter implements InputProcessor {

    private static float verticalSpriteOffset = 8;
    private static float horizontalSpriteOffset = 24;
    private static int player;
    private static Button towerButton;
    private static boolean buildMode = false;
    private static Button startButton;
    private static Button quitButton;
    private static Button unitButton;
    private static Button defenderButton;
    private static Button attackerButton;
    private static Button endTurnButton;
    // move to player if possible
    private static Texture healthBarTexture;
    private static Texture healthTexture;
    private static Sprite healthBarSprite;
    private static Sprite healthSprite;
    private static BitmapFont hpCounter;
    private static Texture coinTexture;
    private static BitmapFont coinCounter;
    private static GameClient client;
    private static Texture towerUnderConstructionTexture;
    private static Texture towerType1Texture;
    final int height = 720;
    int width = 1280;
    SpriteBatch batch;
    OrthographicCamera camera;
    IsometricTiledMapRenderer renderer;
    TiledMap map;
    private Sprite coinSprite;
    private int currentHp = 50;
    private float hpSpriteW;
    private BitmapFont unitNumber;
    private Screen currentScreen;
    private float elapsedTime;
    
    public static void main(String[] args) {
        setupClient();
    }

    private static void setupClient() {
        client = new GameClient();
        if (client.makeConnection()) {
            client.start();
        }
    }

    public static void createButtons() {
        towerButton = new Button("UI/towerButton.png", 30, 50);
        startButton = new Button("UI/startButton.png",
                Gdx.graphics.getWidth() / 2 - towerButton.getSprite().getWidth() / 2, Gdx.graphics.getHeight() / 2);
        quitButton = new Button("UI/quitButton.png",
                Gdx.graphics.getWidth() / 2 - startButton.getSprite().getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - startButton.getSprite().getHeight() * 2);
        unitButton = new Button("UI/ufoButton.png", 50, 50);
        defenderButton = new Button("UI/defenderButton.png", 100, Gdx.graphics.getHeight() / 2);
        attackerButton = new Button("UI/attackerButton.png",
                Gdx.graphics.getWidth() - defenderButton.getSprite().getWidth() - 100, Gdx.graphics.getHeight() / 2);
        endTurnButton = new Button("UI/endTurnButton.png", 0, Gdx.graphics.getHeight() - 200);
    }

    public void isometricPov() {
        renderer.render();

        List<GameObject> objectsToAdd = new ArrayList<GameObject>();
        objectsToAdd.addAll(GameController.getTowers());
        objectsToAdd.addAll(GameController.getTroops());
        Collections.sort(objectsToAdd);

        renderer.getBatch().begin();

        for (int i = 0; i < objectsToAdd.size(); i++) {
            GameObject object = objectsToAdd.get(i);
            Sprite sprite = object.getSprite();
            Vector2 coordinates = realWorldCooridinateToIsometric(object.getRealWorldCoordinates());
            int w = 48;
            if (object instanceof Tower) {
                Tower tower = (Tower) object;
                if (!tower.getIsCompleted()) {
                    renderer.getBatch().setColor(0.5f, 0.5f, 0.5f, 0.5f);
                }
            }
            renderer.getBatch().draw(sprite, coordinates.x - w / 2, coordinates.y - w / 6, w,
                    w * sprite.getHeight() / sprite.getWidth());
            renderer.getBatch().setColor(Color.WHITE);
        }

        if (player == 0) {
            // DEFENDER
            if (buildMode) {
                Texture tmpTower = new Texture(Gdx.files.internal("towers/TowerType1.png"));
                Sprite tmpSpriteTower = new Sprite(tmpTower);
                RealWorldCoordinates rwc = snap(Gdx.input.getX(), Gdx.input.getY());

                if (GameController.verifyTowerPlacement(TowerType.type1, rwc)) {
                    renderer.getBatch().setColor(0, 1, 0, 0.5f);
                } else {
                    renderer.getBatch().setColor(1, 0, 0, 0.5f);
                }

                Vector2 coords = realWorldCooridinateToIsometric(rwc);
                renderer.getBatch().draw(tmpSpriteTower, coords.x - horizontalSpriteOffset,
                        coords.y - verticalSpriteOffset, 48, 94);
                renderer.getBatch().setColor(Color.WHITE);
            }

        } else {

        }

        renderer.getBatch().end();
    }

    public void healthPercentage() {
        float result = currentHp / 100.0f;
        healthSprite.setBounds(healthSprite.getX(), healthSprite.getY(), hpSpriteW * result, healthSprite.getHeight());
    }

    public void showHealth() {
        healthPercentage();
        healthBarSprite.draw(batch);
        healthSprite.draw(batch);
        String nr = currentHp + "";
        hpCounter.getData().setScale(1.5f);
        hpCounter.draw(batch, nr + " / 100", 220 - (nr.length() - 1) * 5, Gdx.graphics.getHeight() - 54);

    }

    public void showCoins(Player player) {
        coinSprite.setPosition(Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 72);
        String tmpCoinCounter = player.getCurrentMoney() + "";
        coinCounter.getData().setScale(2.0f);
        coinCounter.draw(batch, tmpCoinCounter, Gdx.graphics.getWidth() - 120, Gdx.graphics.getHeight() - 50);

        coinSprite.draw(batch);
    }

    public void defenderPOV() {
        isometricPov();
        batch.begin();

        towerButton.getSprite().draw(batch);
        /*
         * tmpSpriteTower.setPosition(Gdx.input.getX() - tmpSpriteTower.getWidth() / 2,
         * Gdx.graphics.getHeight() - Gdx.input.getY()); batch.setColor(0, 200, 0,
         * 0.5f); batch.draw(tmpSpriteTower, Gdx.input.getX() - 24,
         * Gdx.graphics.getHeight() - Gdx.input.getY() - 16, 48, 94);
         */
        showHealth();

        showCoins(GameController.defender);
        endTurnButton.getSprite().draw(batch);
        batch.end();

    }

    public void attackerPOV() {
        isometricPov();
        batch.begin();
        unitButton.getSprite().draw(batch);
        unitNumber.getData().setScale(1.5f);
        int x = EventManager.getUnitBuildPlan()[0][0];
        String nr = new String("" + x);

        unitNumber.draw(batch, nr, unitButton.getX() + unitButton.getSprite().getWidth() - 20 - (nr.length() - 1) * 10,
                unitButton.getY() + 25);
        endTurnButton.getSprite().draw(batch);
        showHealth();
        showCoins(GameController.attacker);
        batch.end();
        isometricPov();
    }

    public void attackerTouchDown(int x, int y, int pointer, int button) {
        if (GameController.getWaveState() == GameController.WaveState.AttackerBuild) {
            if (button == Buttons.LEFT) {
                if (unitButton.checkClick(x, y)) {
                    EventManager.buildPlanChange(0, 0, 1, false);
                } else if (endTurnButton.checkClick(x, y)) {
                    GameController.endPhase();
                }
            }
            if (button == Buttons.RIGHT) {
                if (unitButton.checkClick(x, y)) {
                    EventManager.buildPlanChange(0, 0, -1, false);
                }
            }
        }
    }

    public RealWorldCoordinates roundToCentreTile(RealWorldCoordinates rwc) {
        MatrixCoordinates matrixCoords = new MatrixCoordinates(rwc);
        return new RealWorldCoordinates(32 + matrixCoords.getY() * 64, 32 + matrixCoords.getX() * 64);
    }

    public RealWorldCoordinates snap(int x, int y) {
        Vector2 coords = new Vector2(x * 2, Gdx.graphics.getHeight() - (y * 2));
        return roundToCentreTile(isometricToRealWorldCoordinate(coords));
    }

    public void defenderTouchDown(int x, int y, int pointer, int button) {
        if (GameController.getWaveState() == GameController.WaveState.DefenderBuild) {
            if (button == Buttons.LEFT) {
                if (currentScreen == Screen.MAIN_MENU) {

                    if (startButton.checkClick(x, y))
                        currentScreen = Screen.GAME;
                    else if (quitButton.checkClick(x, y)) {
                        quitButton.dispose();
                        startButton.dispose();
                        Gdx.app.exit();
                    }
                }
                if (buildMode) {
                    // Place tower
                    RealWorldCoordinates rwc = snap(Gdx.input.getX(), Gdx.input.getY());
                    if (GameController.verifyTowerPlacement(TowerType.type1, rwc)) {
                        EventManager.towerPlaced(TowerType.type1, rwc);
                        buildMode = false;
                    }

                }
                if (towerButton.checkClick(x, y)) {
                    System.out.println("Clicked towerButton");
                    if (!buildMode) {
                        buildMode = true;
                        // Pixmap tmpCursor = new Pixmap(Gdx.files.internal("UI/invisibleCursor.png"));
                        // Gdx.graphics.setCursor(Gdx.graphics.newCursor(tmpCursor, 0, 0));
                        // tmpCursor.dispose();
                    }
                } else if (endTurnButton.checkClick(x, y) && !buildMode) {
                    GameController.endPhase();
                }
            }
            if (button == Buttons.RIGHT) {
                if (buildMode) {
                    buildMode = false;
                    // Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                } else {
                    RealWorldCoordinates tmpCoordinates = snap(Gdx.input.getX(), Gdx.input.getY());
                    // removeTower(tmpCoordinates.getX,tmpCoordinates.getY)

                }

            }
        }
    }

    @Override
    public void create() {

        batch = new SpriteBatch();

        unitNumber = new BitmapFont();
        unitNumber.setColor(Color.WHITE);
        hpCounter = new BitmapFont();
        hpCounter.setColor(Color.WHITE);
        coinCounter = new BitmapFont();
        coinCounter.setColor(Color.WHITE);
        map = new TmxMapLoader().load("maps/Isometric.tmx");
        renderer = new IsometricTiledMapRenderer(map);
        camera = new OrthographicCamera(width * 2, height * 2);
        camera.position.set(width, 0, 10);
        camera.update();
        //camera.setToOrtho(false, 1280, 720);
        //renderer.getBatch().enableBlending();
        renderer.setView(camera);
        createButtons();
        // move to player
        healthBarTexture = new Texture(Gdx.files.internal("UI/healthBar.png"));
        healthBarSprite = new Sprite(healthBarTexture);
        healthTexture = new Texture(Gdx.files.internal("UI/health.png"));
        healthSprite = new Sprite(healthTexture);

        healthSprite.setScale(5);
        healthSprite.setPosition(225, Gdx.graphics.getHeight() - 64);
        healthBarSprite.setScale(5);
        healthBarSprite.setPosition(170, Gdx.graphics.getHeight() - 70);
        coinTexture = new Texture(Gdx.files.internal("UI/coins.png"));
        coinSprite = new Sprite(coinTexture);
        coinSprite.setScale(1.5f);
        //
        towerType1Texture = new Texture(Gdx.files.internal("towers/TowerType1.png"), true);

        GameController.initialise();
        hpSpriteW = healthSprite.getWidth();
        currentScreen = Screen.MAIN_MENU;
        Gdx.input.setInputProcessor(this);

    }

    // float x = 0;
    // float y = 0;

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (currentScreen == Screen.MAIN_MENU) {
            batch.begin();
            startButton.getSprite().draw(batch);
            quitButton.getSprite().draw(batch);
            batch.end();
        } else if (currentScreen == Screen.CHOOSE_FACTION) {
            batch.begin();
            defenderButton.getSprite().draw(batch);
            attackerButton.getSprite().draw(batch);
            batch.end();
        } else {
            elapsedTime = Gdx.graphics.getDeltaTime();
            GameController.update(elapsedTime);
            if (player == 0)
                defenderPOV();
            if (player == 1)
                attackerPOV();
        }

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        // camera.viewportWidth = width;
        // camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        renderer.dispose();
        map.dispose();
        towerButton.dispose();
        unitButton.dispose();
        unitNumber.dispose();
        defenderButton.dispose();
        attackerButton.dispose();
    }

    public Vector2 cartesianToIsometric(float x, float y) {
        Vector2 isometric = new Vector2();
        isometric.x = x - y;
        isometric.y = (x + y) * 0.5f;
        return isometric;
    }

    public Vector2 isometricToCartesian(float x, float y) {
        Vector2 cartesian = new Vector2();
        cartesian.x = (2.0f * y + x) * 0.5f;
        cartesian.y = (2.0f * y - x) * 0.5f;
        return cartesian;
    }

    public RealWorldCoordinates isometricToRealWorldCoordinate(Vector2 vector) {

        Vector2 diff = cartesianToIsometric(1280, 1280);
        Vector2 v2 = isometricToCartesian(vector.x, vector.y - 38);
        int x = (int) (v2.x + diff.x);
        int y = (int) (v2.y + diff.y);
        return new RealWorldCoordinates(y, x);
    }

    public Vector2 realWorldCooridinateToIsometric(RealWorldCoordinates rwc) {
        Vector2 diff = cartesianToIsometric(1280, 1280);
        float x = rwc.getX() - diff.x;
        float y = rwc.getY() - diff.y;
        Vector2 v2 = cartesianToIsometric(x, y);
        v2.y += 38;
        return v2;
    }

    @Override
    public boolean keyDown(int keycode) {
        // TODO Auto-generated method stub
        if (keycode == Input.Keys.ESCAPE && currentScreen == Screen.GAME)
            currentScreen = Screen.CHOOSE_FACTION;
        else if (keycode == Input.Keys.ESCAPE && currentScreen == Screen.CHOOSE_FACTION)
            currentScreen = Screen.MAIN_MENU;
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        int x = screenX;
        int y = Gdx.graphics.getHeight() - screenY;
        System.out.println("Clicked at x = " + x + " y = " + y);
        if (button == Buttons.LEFT) {
            if (currentScreen == Screen.MAIN_MENU) {
                if (startButton.checkClick(x, y)) {
                     setupClient();
                    currentScreen = Screen.CHOOSE_FACTION;
                } else if (quitButton.checkClick(x, y)) {
                    quitButton.dispose();
                    startButton.dispose();
                    Gdx.app.exit();
                }
                System.out.println(currentScreen);
            } else if (currentScreen == Screen.CHOOSE_FACTION) {
                if (defenderButton.checkClick(x, y) && client.isDefender()) {
                    GameController.setPlayerType(true);
                    player = 0;
                    currentScreen = Screen.GAME;
                } else if (attackerButton.checkClick(x, y) && client.isAttacker()) {
                    GameController.setPlayerType(false);
                    player = 1;
                    currentScreen = Screen.GAME;
                }
            }

        }
        if (player == 1)
            attackerTouchDown(x, y, pointer, button);
        if (player == 0)
            defenderTouchDown(x, y, pointer, button);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        int x = screenX;
        int y = Gdx.graphics.getHeight() - screenY;
        /*
         * if(button == Buttons.LEFT) { if(startButton.checkClick(x, y)) { currentScreen
         * = Screen.MAIN_MENU; startButton.setPressedStatus(false); } }
         */
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }

    public static Texture getTowerTexture(TowerType type) {
        switch (type) {
            default:
                return towerType1Texture;
        }
    }

}