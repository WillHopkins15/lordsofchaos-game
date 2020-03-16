package org.lordsofchaos;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
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
import org.lordsofchaos.graphics.*;
import org.lordsofchaos.graphics.buttons.*;
import org.lordsofchaos.network.GameClient;
import org.lordsofchaos.player.Player;

import java.util.*;

public class Game extends ApplicationAdapter implements InputProcessor {

    public static int player;
    private static boolean buildMode = false;
    private static Texture healthBarTexture;
    private static Texture healthTexture;
    private static Sprite healthBarSprite;
    private static Sprite healthSprite;
    private static BitmapFont hpCounter;
    private static Texture coinTexture;
    private static BitmapFont coinCounter;
    private static GameClient client;
    private static SpriteBatch batch;
    private static float timerChangeTurn;
    private static boolean changedTurn = false;
    public static boolean multiplayer = false;
    final int height = 720;
    final int width = 1280;
    private MapRenderer renderer;
    private Sprite coinSprite;
    private int lastTurnTime;
    private float hpSpriteW;
    private BitmapFont unitNumber;
    public static Screen currentScreen;
    private float elapsedTime;
    private Pixmap towerAttackPixmap;
    private Texture towerAttackTexture;
    private List<TroopSprite> unitsSprite = new ArrayList<>();
    private Sound soundTrack;
    private Sound selectSound;
    private LevelEditor levelEditor;
    private static FreeTypeFontParameter fontParameter;
    private static FreeTypeFontGenerator fontGenerator;
    private static BitmapFont font;
    private static BitmapFont timerFont;
    public static Game instance;
    private TowerType ghostTowerType;
    private static ArrayList<Button> buttonList;
    private static int currentPath;
    private static boolean mouseClicked;


    public static void main(String[] args) {
        setupClient();
    }

    private static void setupClient() {
        client = new GameClient();
        if (!client.makeConnection()) return;
        client.start();
    }

    public static void newTurn() {
        changedTurn = true;
    }
    public void buildTrue(){
        buildMode = true;
    }
    public void setGhostTowerType(TowerType newType){
        ghostTowerType = newType;
    }
    public GameClient getClient(){return client;}
    public static void createButtons() {
        buttonList = new ArrayList<Button>();
        buttonList.add(new TowerButton("UI/NewArtMaybe/towerType1Button.png", 50, 50,Screen.DEFENDER_SCREEN,TowerType.type1));
        buttonList.add(new TowerButton("UI/NewArtMaybe/towerType2Button.png", 156, 50,Screen.DEFENDER_SCREEN,TowerType.type2));
        buttonList.add(new TowerButton("UI/NewArtMaybe/towerType3Button.png", 262, 50,Screen.DEFENDER_SCREEN,TowerType.type3));
        buttonList.add( new MenuButton("UI/NewArtMaybe/playLocalButton.png",
                Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight() / 2,Screen.MAIN_MENU,Screen.CHOOSE_FACTION));
        buttonList.add(new MenuButton("UI/NewArtMaybe/exitButton.png",
                Gdx.graphics.getWidth() / 2 - 150,
                Gdx.graphics.getHeight() / 2 - 84 * 2,Screen.MAIN_MENU,null));

        // troop buttons
        buttonList.add(new UnitButton("UI/ufoButton.png", 50, 50,Screen.ATTACKER_SCREEN, currentPath,0));
        buttonList.add(new UnitButton("UI/ufo3Button.png", 156, 50,Screen.ATTACKER_SCREEN, currentPath,1));
        buttonList.add(new UnitButton("UI/ufo2Button.png", 262, 50,Screen.ATTACKER_SCREEN, currentPath,2));

        // select attacker/defender buttons
        buttonList.add(new PlayerButton("UI/NewArtMaybe/defenderButton.png", 100, Gdx.graphics.getHeight() / 2,Screen.CHOOSE_FACTION,Screen.DEFENDER_SCREEN,0));
        buttonList.add(new PlayerButton("UI/NewArtMaybe/attackerButton.png",   Gdx.graphics.getWidth() - 400, Gdx.graphics.getHeight() / 2,Screen.CHOOSE_FACTION,Screen.ATTACKER_SCREEN,1));

        buttonList.add(new EndTurnButton("UI/NewArtMaybe/endTurnButton.png", 0, Gdx.graphics.getHeight() - 200,Screen.ATTACKER_SCREEN));
        buttonList.add(new EndTurnButton("UI/NewArtMaybe/endTurnButton.png", 0, Gdx.graphics.getHeight() - 200,Screen.DEFENDER_SCREEN));
        buttonList.add(new MultiplayerButton("UI/NewArtMaybe/playOnlineButton.png", Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight() / 8,Screen.MAIN_MENU,Screen.CHOOSE_FACTION));
        buttonList.add(new LevelEditorButton("UI/button.png", 100, Gdx.graphics.getHeight() / 8, Screen.MAIN_MENU, Screen.LEVEL_EDITOR));

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


    public void isometricPov() {
        renderer.render();

        renderer.getBatch().begin();

        if (player == 0) {
            // DEFENDER
            if (buildMode) {
                Texture tmpTower = new Texture(Gdx.files.internal("towers/sprites/TowerType1.png"));
                Sprite tmpSpriteTower = new Sprite(tmpTower);
                RealWorldCoordinates rwc = snap(Gdx.input.getX(), Gdx.input.getY());

                if (GameController.verifyTowerPlacement(TowerType.type1, rwc))
                    renderer.getBatch().setColor(0, 1, 0, 0.5f);
                else renderer.getBatch().setColor(1, 0, 0, 0.5f);

                Vector2 coords = Conversions.realWorldCooridinateToIsometric(rwc);
                float horizontalSpriteOffset = 24;
                float verticalSpriteOffset = 8;
                renderer.getBatch().draw(tmpSpriteTower, coords.x - horizontalSpriteOffset,
                        coords.y - verticalSpriteOffset, 48, 48 * tmpSpriteTower.getHeight() / tmpSpriteTower.getWidth());
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
        boolean draw = false;
        for (Tower tower : towers) {
            Troop tmpTroop = tower.getTarget();
            if (tmpTroop != null) {
                draw = true;
                Vector2 troopScreenPosition = Conversions.realWorldCoordinatesToScreenPosition(tmpTroop.getRealWorldCoordinates());
                int troopX = (int) troopScreenPosition.x, troopY = (int) troopScreenPosition.y;
                Vector2 towerScreenPosition = Conversions.realWorldCoordinatesToScreenPosition(tower.getRealWorldCoordinates());
                int towerX = (int) towerScreenPosition.x, towerY = (int) towerScreenPosition.y;
                //System.out.println("TowerX: " + towerX + " towerY: " + towerY + " troopX: " + troopX + " troopY: " + troopY);
                towerAttackPixmap.drawLine(towerX, Gdx.graphics.getHeight() - towerY - 40, troopX, Gdx.graphics.getHeight() - troopY);
            }
        }
        //towerAttackPixmap.fill();
        towerAttackTexture = new Texture(towerAttackPixmap);
        Sprite towerAttackSprite = new Sprite(towerAttackTexture);
        towerAttackSprite.setPosition(0, 0);
        if (!draw) return;
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


        for (Button button : buttonList)
            if (button.getScreenLocation() == Screen.DEFENDER_SCREEN)
                button.getSprite().draw(batch);
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
        //endTurnButton.getSprite().draw(batch);

    }

    public void attackerPOV() {
        for (Button button : buttonList)
            if (button.getScreenLocation() == Screen.ATTACKER_SCREEN)
                button.getSprite().draw(batch);
        unitNumber.getData().setScale(1.5f);
        int x = EventManager.getUnitBuildPlan()[0][0];
        String nr = "" + x;
        for (Button button : buttonList) {
            if (button instanceof UnitButton) {
                int tmpPath = ((UnitButton) button).getUnitPath();
                int tmpTroopType = ((UnitButton) button).getTroopType();
                String unitNr = "" + EventManager.getUnitBuildPlan()[tmpTroopType][tmpPath];
                unitNumber.draw(batch, unitNr, button.getX() + button.getSprite().getWidth() - 20 - (unitNr.length() - 1) * 10, button.getY() + 25);
                //unitNumber.draw(batch, nr, unitButton.getX() + unitButton.getSprite().getWidth() - 20 - (nr.length() - 1) * 10, unitButton.getY() + 25);
            }
        }
        showHealth();
        showCoins(GameController.attacker);
    }

    public void attackerTouchDown(int x, int y, int pointer, int button) {
        if (GameController.getWaveState() == GameController.WaveState.AttackerBuild) {
            if (button == Input.Buttons.LEFT) {
                for (Button value : buttonList)
                    if (value.checkClick(x, y) && value.getScreenLocation() == currentScreen)
                        value.leftButtonAction();
            } else
                for (Button value : buttonList)
                    if (value.checkClick(x, y) && value.getScreenLocation() == currentScreen)
                        value.rightButtonAction();
        }
    }

    public RealWorldCoordinates roundToCentreTile(RealWorldCoordinates rwc) {
        MatrixCoordinates matrixCoords = new MatrixCoordinates(rwc);
        return new RealWorldCoordinates(32 + matrixCoords.getY() * 64, 32 + matrixCoords.getX() * 64);
    }

    public RealWorldCoordinates snap(int x, int y) {
        Vector2 coords = new Vector2(x * 2, Gdx.graphics.getHeight() - (y * 2));
        RealWorldCoordinates rwc = Conversions.isometricToRealWorldCoordinate(coords);
        return roundToCentreTile(rwc);
    }

    public void defenderTouchDown(int x, int y, int pointer, int button) {
        if (button == Buttons.LEFT) {
            System.out.println("HERE");
        }
        if (GameController.getWaveState() == GameController.WaveState.DefenderBuild) {
            if (button == Buttons.LEFT)  {
                if (buildMode) {
                    // Place tower
                    System.out.println("TEST1");
                    mouseClicked = true;
                    RealWorldCoordinates rwc = snap(Gdx.input.getX(), Gdx.input.getY());
                    if (GameController.verifyTowerPlacement(TowerType.type1, rwc)) {
                        selectSound.play(0.75f);
                        EventManager.towerPlaced(TowerType.type1, rwc);
                        buildMode = false;
                        System.out.println("TEST");
                    }

                } else {
                    System.out.println("NONTEST");
                    //buildMode = true;
                    for (int i = 0; i < buttonList.size(); i++) {
                        if (buttonList.get(i).checkClick(x, y) && buttonList.get(i).getScreenLocation() == currentScreen) {
                            buttonList.get(i).leftButtonAction();
                            buildMode = true;
                            break;
                        }
                    }
                }
            }
        }
                /*if (towerButton.checkClick(x, y)) {
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
                    if (client != null) {
                        client.send("Change Phase");
                    } else {
                        GameController.endPhase();
                    }
                    changedTurn = true;
                }
            }
            */
        if (button == Buttons.RIGHT && buildMode) buildMode = false;


        // float x = 0;
        // float y = 0;
    }

    public void disposeTMP(){
        disposeUnitHealthBar();
        disposeAttacks();
        /*if(font != null) {
            font.dispose();
            font = null;
        }*/
    }

    @Override
    public void create(){

        instance = this;
        currentPath = 0;
        player = 2;
        batch = new SpriteBatch();
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("UI/boxybold.ttf"));
        fontParameter = new FreeTypeFontParameter();
        font=fontGenerator.generateFont(fontParameter);
        soundTrack = Gdx.audio.newSound(Gdx.files.internal("sound/RGA-GT - Being Cool Doesn`t Make Me Fool.mp3"));
        //soundTrack.loop(0.25f);
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
        renderer = new MapRenderer();
        OrthographicCamera camera = new OrthographicCamera(width * 2, height * 2);
        camera.position.set(width, 0, 10);
        camera.update();
        renderer.setView(camera);
        createButtons();
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
        GameController.initialise();
        renderer.setMap(GameController.getMap());
        hpSpriteW = healthSprite.getWidth();
        currentScreen = Screen.MAIN_MENU;

        Gdx.input.setInputProcessor(this);

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (currentScreen == null) Gdx.app.exit();
        else if(currentScreen == Screen.MAIN_MENU || currentScreen == Screen.CHOOSE_FACTION) {
            batch.begin();
            for (Button button : buttonList)
                if (button.getScreenLocation() == currentScreen)
                    button.getSprite().draw(batch);
            batch.end();
        } else if (currentScreen == Screen.LEVEL_EDITOR) {
            if (levelEditor == null) levelEditor = new LevelEditor(renderer);
            levelEditor.run(new MatrixCoordinates(snap(Gdx.input.getX(), Gdx.input.getY())));
        } else {
            elapsedTime = Gdx.graphics.getDeltaTime();
            GameController.update(elapsedTime);
            isometricPov();
            batch.begin();
            if (player == 0) defenderPOV();
            else if (player == 1) attackerPOV();
            if (changedTurn) {
                switch (GameController.getWaveState()) {
                    case AttackerBuild: changeTurn(2, "Attacker's Turn");
                        break;
                    case DefenderBuild: changeTurn(2, "Defender's Turn");
                        break;
                    case Play: changeTurn(2, "           Play     ");
                }
            }
            showUnitHealthBar();
            showTowerAttack();
            if(GameController.getWaveState() == GameController.WaveState.AttackerBuild ||
                    GameController.getWaveState() == GameController.WaveState.DefenderBuild){
                String timerTmp = String.format("%02d" , 30 - (int) GameController.getBuildPhaseTimer());
                timerFont.draw(batch, timerTmp, Gdx.graphics.getWidth() / 2 + 200, Gdx.graphics.getHeight() - 25);
            }
            batch.end();
            disposeTMP();
        }

    }

    @Override
    public void dispose() {
        batch.dispose();
        renderer.dispose();
        /*towerButton.dispose();
        unitButton.dispose();
        unitNumber.dispose();
        defenderButton.dispose();
        attackerButton.dispose();
        multiplayerButton.dispose();*/
        soundTrack.dispose();
        fontGenerator.dispose();
        timerFont.dispose();
        font.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE && (currentScreen == Screen.DEFENDER_SCREEN ||  currentScreen == Screen.ATTACKER_SCREEN) )
            currentScreen = Screen.CHOOSE_FACTION;
        else if (keycode == Input.Keys.ESCAPE && currentScreen == Screen.CHOOSE_FACTION)
            currentScreen = Screen.MAIN_MENU;
        else if (keycode == Input.Keys.ESCAPE && currentScreen == Screen.LEVEL_EDITOR) {
            currentScreen = Screen.MAIN_MENU;
            renderer.setMap(GameController.getMap());
            renderer.setColourExceptions(new HashMap<>());
            levelEditor = null;
        }
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
        int y = Gdx.graphics.getHeight() - screenY;
        if (button == Buttons.LEFT && (currentScreen == Screen.MAIN_MENU || currentScreen == Screen.CHOOSE_FACTION)) {
            for (Button value : buttonList)
                if (value.checkClick(screenX, y) && value.getScreenLocation() == currentScreen)
                    value.leftButtonAction();
        }
        if (currentScreen == Screen.GAME || currentScreen == Screen.DEFENDER_SCREEN || currentScreen == Screen.ATTACKER_SCREEN) {
            if (player == 1) attackerTouchDown(screenX, y, pointer, button);
            else if (player == 0) defenderTouchDown(screenX, y, pointer, button);
        } else if (currentScreen == Screen.LEVEL_EDITOR && levelEditor != null) {
            if (button == Buttons.LEFT) levelEditor.setPlaced(true);
            else if (button == Buttons.RIGHT) levelEditor.remove();
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (currentScreen == Screen.LEVEL_EDITOR && levelEditor != null) levelEditor.setPlaced(true);
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
