package org.lordsofchaos;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
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
import org.lordsofchaos.gameobjects.troops.Troop;
import org.lordsofchaos.graphics.Button;
import org.lordsofchaos.graphics.Conversions;
import org.lordsofchaos.graphics.Screen;
import org.lordsofchaos.graphics.TroopSprite;
import org.lordsofchaos.network.GameClient;
import org.lordsofchaos.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game extends ApplicationAdapter implements InputProcessor
{
    
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
    //private static BitmapFont endTurnFont;
    private static Button multiplayerButton;
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
    private static SpriteBatch batch;
    private static float timerChangeTurn;
    private static boolean changedTurn = false;
    private static boolean multiplayer = false;
    final int height = 720;
    int width = 1280;
    OrthographicCamera camera;
    IsometricTiledMapRenderer renderer;
    TiledMap map;
    private Sprite coinSprite;
    private int lastTurnTime;
    private float hpSpriteW;
    private BitmapFont unitNumber;
    private Screen currentScreen;
    private float elapsedTime;
    private Pixmap towerAttackPixmap;
    private Texture towerAttackTexture;
    private List<TroopSprite> unitsSprite = new ArrayList<>();
    private Sound soundTrack;
    private Sound selectSound;
    private static FreeTypeFontParameter fontParameter;
    private static FreeTypeFontGenerator fontGenerator;
    private static BitmapFont font;
    private static BitmapFont timerFont;
    
    public static void main(String[] args) {
        setupClient();
    }
    
    private static boolean setupClient() {
        client = new GameClient();
        if (client.makeConnection()) {
            client.start();
            return true;
        }
        return false;
    }
    
    public static void newTurn() {
        changedTurn = true;
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
        multiplayerButton = new Button("UI/button.png", Gdx.graphics.getWidth() / 2 - towerButton.getSprite().getWidth() / 2, Gdx.graphics.getHeight() / 8);
    }
    
    public static void changeTurn(float targetTime, String currentPlayer) {
        timerChangeTurn += Gdx.graphics.getDeltaTime();
        //System.out.println("target: " + targetTime + " current Time: " + timerChangeTurn);
        if (timerChangeTurn < targetTime) {
            font.draw(batch, currentPlayer, Gdx.graphics.getWidth() / 2 - 230, Gdx.graphics.getHeight() - 100);
            //endTurnFont.draw(batch, currentPlayer, Gdx.graphics.getWidth() / 2 - 200, Gdx.graphics.getHeight() - 100);
            //System.out.println("Printing text!");
        } else {
            changedTurn = false;
            timerChangeTurn = 0;
        }
    }
    
    public static Texture getTowerTexture(TowerType type) {
        switch (type) {
            default:
                return towerType1Texture;
        }
    }
    
    public void isometricPov() {
        renderer.render();
        
        List<GameObject> objectsToAdd = new ArrayList<>();
        objectsToAdd.addAll(GameController.getTowers());
        objectsToAdd.addAll(GameController.getTroops());
        Collections.sort(objectsToAdd);
        
        renderer.getBatch().begin();
        
        for (GameObject object : objectsToAdd) {
            Sprite sprite = object.getSprite();
            Vector2 coordinates = Conversions.realWorldCooridinateToIsometric(object.getRealWorldCoordinates());
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
                
                Vector2 coords = Conversions.realWorldCooridinateToIsometric(rwc);
                renderer.getBatch().draw(tmpSpriteTower, coords.x - horizontalSpriteOffset,
                        coords.y - verticalSpriteOffset, 48, 94);
                renderer.getBatch().setColor(Color.WHITE);
            }
            
        }
        
        renderer.getBatch().end();
    }
    
    public void healthPercentage() {
        float result = GameController.defender.getHealth() / 100.0f;
        healthSprite.setBounds(healthSprite.getX(), healthSprite.getY(), hpSpriteW * result, healthSprite.getHeight());
    }
    public void generateFont(){
        fontParameter.size  = 30;
        timerFont = fontGenerator.generateFont(fontParameter);
        fontParameter.size = 40;
        font = fontGenerator.generateFont(fontParameter);
    }
    public void showHealth() {
        healthPercentage();
        healthBarSprite.draw(batch);
        healthSprite.draw(batch);
        String nr = GameController.defender.getHealth() + "";
        hpCounter.getData().setScale(1.5f);
        hpCounter.draw(batch, nr + " / 100", 220 - (nr.length() - 1) * 5, Gdx.graphics.getHeight() - 54);
        
    }
    

    public void showUnitHealthBar() {
        List<Troop> tmpUnits = GameController.getTroops();
        unitsSprite = new ArrayList<>();
        if (tmpUnits.size() > 0) {
            for (Troop tmpUnit : tmpUnits) unitsSprite.add(new TroopSprite(tmpUnit));
            //System.out.println("X: " + unitsSprite.get(0).getX() + " Y: " + unitsSprite.get(0).getY() + " Size:" + unitsSprite.size());
            for (TroopSprite troopSprite : unitsSprite) {
                troopSprite.getHealthBarSpriteBase().draw(batch);
                troopSprite.getHealthBarSpriteGreen().draw(batch);
                //System.out.println("Drawing Healthbar");
            }
        }
    }
    
    public void disposeUnitHealthBar() {
        List<Troop> tmpUnits = GameController.getTroops();
        if (tmpUnits.size() > 0) {
            unitsSprite = new ArrayList<>();
        }
        for (TroopSprite troopSprite : unitsSprite) troopSprite.dispose();
        
    }
    
    public void showTowerAttack() {
        List<Tower> towers = GameController.getTowers();
        towerAttackPixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGBA8888);
        towerAttackPixmap.setColor(Color.YELLOW);
        for (Tower tower : towers) {
            Troop tmpTroop = tower.getTarget();
            if (tmpTroop != null) {
                
                int troopX = (int) Conversions.realWorldCoordinatesToScreenPosition(tmpTroop.getRealWorldCoordinates()).x;
                int troopY = (int) Conversions.realWorldCoordinatesToScreenPosition(tmpTroop.getRealWorldCoordinates()).y;
                int towerX = (int) Conversions.realWorldCoordinatesToScreenPosition(tower.getRealWorldCoordinates()).x;
                int towerY = (int) Conversions.realWorldCoordinatesToScreenPosition(tower.getRealWorldCoordinates()).y;
                //System.out.println("TowerX: " + towerX + " towerY: " + towerY + " troopX: " + troopX + " troopY: " + troopY);
                towerAttackPixmap.drawLine(towerX, Gdx.graphics.getHeight() - towerY - 40, troopX, Gdx.graphics.getHeight() - troopY);
            }
        }
        //towerAttackPixmap.fill();
        towerAttackTexture = new Texture(towerAttackPixmap);
        Sprite towerAttackSprite = new Sprite(towerAttackTexture);
        towerAttackSprite.setPosition(0, 0);
        towerAttackSprite.draw(batch);
    }
    
    public void disposeAttacks() {
        towerAttackTexture.dispose();
        towerAttackPixmap.dispose();
    }
    
    public void showCoins(Player player) {
        coinSprite.setPosition(Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 72);
        String tmpCoinCounter = player.getCurrentMoney() + "";
        coinCounter.getData().setScale(2.0f);
        coinCounter.draw(batch, tmpCoinCounter, Gdx.graphics.getWidth() - 120, Gdx.graphics.getHeight() - 50);
        
        coinSprite.draw(batch);
    }
    
    public void defenderPOV() {
        
        
        towerButton.getSprite().draw(batch);
        /*
         * tmpSpriteTower.setPosition(Gdx.input.getX() - tmpSpriteTower.getWidth() / 2,
         * Gdx.graphics.getHeight() - Gdx.input.getY()); batch.setColor(0, 200, 0,
         * 0.5f); batch.draw(tmpSpriteTower, Gdx.input.getX() - 24,
         * Gdx.graphics.getHeight() - Gdx.input.getY() - 16, 48, 94);
         */
        //fix this later
        if(GameController.getWaveState() == GameController.WaveState.AttackerBuild) buildMode = false;
        showHealth();
        
        showCoins(GameController.defender);
        endTurnButton.getSprite().draw(batch);
        
    }
    
    public void attackerPOV() {
        
        unitButton.getSprite().draw(batch);
        unitNumber.getData().setScale(1.5f);
        int x = EventManager.getUnitBuildPlan()[0][0];
        String nr = "" + x;
        
        unitNumber.draw(batch, nr, unitButton.getX() + unitButton.getSprite().getWidth() - 20 - (nr.length() - 1) * 10,
                unitButton.getY() + 25);
        endTurnButton.getSprite().draw(batch);
        showHealth();
        showCoins(GameController.attacker);
    }
    
    public void attackerTouchDown(int x, int y, int pointer, int button) {
        if (GameController.getWaveState() == GameController.WaveState.AttackerBuild) {
            if (button == Input.Buttons.LEFT) {
                if (unitButton.checkClick(x, y)) {
                    selectSound.play(0.75f);
                    EventManager.buildPlanChange(0, 0, 1, false);
                } else if (endTurnButton.checkClick(x, y) && !changedTurn) {
                    selectSound.play(0.75f);
                    GameController.endPhase();
                    changedTurn = true;
                    
                }
            }
            if (button == Buttons.RIGHT) {
                if (unitButton.checkClick(x, y)) {
                    selectSound.play(0.75f);
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
        RealWorldCoordinates rwc = Conversions.isometricToRealWorldCoordinate(coords);
        //System.out.println(Conversions.realWorldCoordinatesToScreenPosition(rwc));
        return roundToCentreTile(rwc);
    }
    
    public void defenderTouchDown(int x, int y, int pointer, int button) {
        if (GameController.getWaveState() == GameController.WaveState.DefenderBuild) {
            if (button == Buttons.LEFT) {
                if (buildMode) {
                    // Place tower
                    RealWorldCoordinates rwc = snap(Gdx.input.getX(), Gdx.input.getY());
                    if (GameController.verifyTowerPlacement(TowerType.type1, rwc)) {
                        selectSound.play(0.75f);
                        EventManager.towerPlaced(TowerType.type1, rwc);
                        buildMode = false;
                    }
                    
                }
                if (towerButton.checkClick(x, y)) {
                    //System.out.println("Clicked towerButton");
                    selectSound.play(0.75f);
                    if (!buildMode && GameController.canAffordTower(TowerType.type1)) {
                        buildMode = true;
                        // Pixmap tmpCursor = new Pixmap(Gdx.files.internal("UI/invisibleCursor.png"));
                        // Gdx.graphics.setCursor(Gdx.graphics.newCursor(tmpCursor, 0, 0));
                        // tmpCursor.dispose();
                    }
                } else if (endTurnButton.checkClick(x, y) && !buildMode && !changedTurn) {
                    selectSound.play(0.75f);
                    GameController.endPhase();
                    changedTurn = true;
                    
                }
            }
            if (button == Buttons.RIGHT) {
                if (buildMode) {
                    buildMode = false;
                    // Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                } else {
                    RealWorldCoordinates tmpCoordinates = snap(Gdx.input.getX(), Gdx.input.getY());
                    
                }
                
            }
        }
    }
    
    // float x = 0;
    // float y = 0;
    public void disposeTMP(){
        disposeUnitHealthBar();
        disposeAttacks();
        /*if(font != null) {
            font.dispose();
            font = null;
        }*/
    }
    @Override
    public void create() {

        batch = new SpriteBatch();
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("UI/boxybold.ttf"));
        fontParameter = new FreeTypeFontParameter();
        font=fontGenerator.generateFont(fontParameter);
        soundTrack = Gdx.audio.newSound(Gdx.files.internal("sound/RGA-GT - Being Cool Doesn`t Make Me Fool.mp3"));
        soundTrack.loop(0.25f);
        selectSound = Gdx.audio.newSound(Gdx.files.internal("sound/click3.wav"));
       /* endTurnFont = new BitmapFont();
        endTurnFont.setColor(255, 255, 255, 1f);
        endTurnFont.getData().setScale(3f);
        */
        generateFont();
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
        //endTurnTexture = new Texture(Gdx.files.internal("UI/"))
        towerType1Texture = new Texture(Gdx.files.internal("towers/TowerType1.png"), true);
        GameController.initialise();
        hpSpriteW = healthSprite.getWidth();
        currentScreen = Screen.MAIN_MENU;
        
        Gdx.input.setInputProcessor(this);
        
    }
    
    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        /*long id = soundTrack.loop();
        Timer.schedule(new Task(){
            @Override
            public void run(){
                soundTrack.stop(id);
            }
        }, 5.0f);*/
        //batch.begin();
        if (currentScreen == Screen.MAIN_MENU) {
            batch.begin();
            startButton.getSprite().draw(batch);
            quitButton.getSprite().draw(batch);
            multiplayerButton.getSprite().draw(batch);
            batch.end();
        } else if (currentScreen == Screen.CHOOSE_FACTION) {
            batch.begin();
            defenderButton.getSprite().draw(batch);
            attackerButton.getSprite().draw(batch);
            batch.end();
        } else {
            elapsedTime = Gdx.graphics.getDeltaTime();
            GameController.update(elapsedTime);
            isometricPov();
            batch.begin();
            if (player == 0)
                defenderPOV();
            if (player == 1)
                attackerPOV();
            if (changedTurn) {
                switch (GameController.getWaveState()) {
                    case AttackerBuild:
                        changeTurn(2, "Attacker's Turn");
                        break;
                    case DefenderBuild:
                        changeTurn(2, "Defender's Turn");
                        break;
                    case Play:
                        changeTurn(2, "           Play     ");
                }
            }
            showUnitHealthBar();
            showTowerAttack();
            if(GameController.getWaveState() == GameController.WaveState.AttackerBuild ||
                        GameController.getWaveState() == GameController.WaveState.DefenderBuild){
                String timerTmp = String.format("%02d" , 30 - (int)GameController.getBuildPhaseTimer());
                //System.out.println(timerTmp);
                timerFont.draw(batch, timerTmp, Gdx.graphics.getWidth() / 2 + 200, Gdx.graphics.getHeight() - 25);
            }
            batch.end();
            disposeTMP();
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
        multiplayerButton.dispose();
        soundTrack.dispose();
        fontGenerator.dispose();
        timerFont.dispose();
        font.dispose();
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
        return false;
    }
    
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        int x = screenX;
        int y = Gdx.graphics.getHeight() - screenY;
        //System.out.println("Clicked at x = " + x + " y = " + y);
        if (button == Buttons.LEFT) {
            if (currentScreen == Screen.MAIN_MENU) {
                if (startButton.checkClick(x, y)) {
                    currentScreen = Screen.CHOOSE_FACTION;
                    selectSound.play(0.75f);
                } else if (multiplayerButton.checkClick(x, y)) {
                    selectSound.play(0.75f);
                    if (setupClient()) {
                        multiplayer = true;
                        currentScreen = Screen.CHOOSE_FACTION;
                    }
                } else if (quitButton.checkClick(x, y)) {
                    selectSound.play(0.75f);
                    quitButton.dispose();
                    startButton.dispose();
                    multiplayerButton.dispose();
                    Gdx.app.exit();
                }
                //System.out.println(currentScreen);
            } else if (currentScreen == Screen.CHOOSE_FACTION) {
                if (multiplayer) {
                    if (defenderButton.checkClick(x, y) && client.isDefender()) {
                        selectSound.play(0.75f);
                        GameController.setPlayerType(true);
                        player = 0;
                        currentScreen = Screen.GAME;
                    } else if (attackerButton.checkClick(x, y) && client.isAttacker()) {
                        selectSound.play(0.75f);
                        GameController.setPlayerType(false);
                        player = 1;
                        currentScreen = Screen.GAME;
                    }
                } else {
                    if (defenderButton.checkClick(x, y)) {
                        selectSound.play(0.75f);
                        GameController.setPlayerType(true);
                        player = 0;
                        currentScreen = Screen.GAME;
                    } else if (attackerButton.checkClick(x, y)) {
                        selectSound.play(0.75f);
                        GameController.setPlayerType(false);
                        player = 1;
                        currentScreen = Screen.GAME;
                    }
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
        return false;
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    
    @Override
    public boolean scrolled(int amount) {
        return false;
    }
    
}