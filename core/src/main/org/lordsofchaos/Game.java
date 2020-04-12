package org.lordsofchaos;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.database.DatabaseCommunication;
import org.lordsofchaos.database.LeaderboardRow;
import org.lordsofchaos.database.Map;
import org.lordsofchaos.gameobjects.TowerType;
import org.lordsofchaos.gameobjects.towers.Projectile;
import org.lordsofchaos.gameobjects.towers.TowerType1;
import org.lordsofchaos.gameobjects.towers.TowerType2;
import org.lordsofchaos.gameobjects.towers.TowerType3;
import org.lordsofchaos.gameobjects.troops.Troop;
import org.lordsofchaos.graphics.Alert;
import org.lordsofchaos.graphics.Conversions;
import org.lordsofchaos.graphics.MapRenderer;
import org.lordsofchaos.graphics.Screen;
import org.lordsofchaos.graphics.TroopSprite;
import org.lordsofchaos.graphics.buttons.Button;
import org.lordsofchaos.graphics.buttons.ChangePageButton;
import org.lordsofchaos.graphics.buttons.EndTurnButton;
import org.lordsofchaos.graphics.buttons.HoverButton;
import org.lordsofchaos.graphics.buttons.HoverUI;
import org.lordsofchaos.graphics.buttons.LeaderBoardButton;
import org.lordsofchaos.graphics.buttons.LevelEditorButton;
import org.lordsofchaos.graphics.buttons.LevelSelectButton;
import org.lordsofchaos.graphics.buttons.LoadLevelButton;
import org.lordsofchaos.graphics.buttons.MainMenuButton;
import org.lordsofchaos.graphics.buttons.MenuButton;
import org.lordsofchaos.graphics.buttons.MultiplayerButton;
import org.lordsofchaos.graphics.buttons.PathButton;
import org.lordsofchaos.graphics.buttons.PlayerButton;
import org.lordsofchaos.graphics.buttons.QuitMenuButton;
import org.lordsofchaos.graphics.buttons.SliderButton;
import org.lordsofchaos.graphics.buttons.TowerButton;
import org.lordsofchaos.graphics.buttons.UnitButton;
import org.lordsofchaos.graphics.buttons.UnitUpgradeButton;
import org.lordsofchaos.graphics.buttons.UpgradeButton;
import org.lordsofchaos.matrixobjects.Path;
import org.lordsofchaos.matrixobjects.Tile;
import org.lordsofchaos.network.GameClient;
import org.lordsofchaos.player.Player;

public class Game extends ApplicationAdapter implements InputProcessor {

    public static int levelSelectPage = 0;
    public static int previousSelectPage = 0; // only need to reload page if the current page != previous
    public static int levelsToShow = 5; // how many levels to display per page on the level select screen
    public static int player;
    public static Screen currentScreen;
    public static Game instance;
    public static boolean multiplayer = false;
    private static boolean hasExited;
    private static Texture levelSelectRowTexture;
    private static boolean buildMode = false;
    private static boolean loading = true;
    private static Sprite healthBarSprite;
    private static Sprite healthSprite;
    private static BitmapFont hpCounter;
    private static BitmapFont coinCounter;
    private static GameClient client;
    private static SpriteBatch batch;
    private static boolean changedTurn = false;
    private static FreeTypeFontParameter fontParameterBoxy;
    private static FreeTypeFontGenerator fontGenerator;
    private static BitmapFont font;
    private static BitmapFont timerFont;
    private static ArrayList<Button> buttonList;
    private static ArrayList<Button> menuButtonList;
    private static int currentPath;
    private static boolean menuOpen;
    private static Sprite menuSprite;
    private static Texture leaderboardRowTexture;
    private static BitmapFont leaderBoardRowText;
    private static float soundTrackVolume = 1.0f;
    private static float soundEffectsVolume;
    private static Music soundTrack;
    private static Sound selectSound;
    private static Sound projectileStartSound;
    private static Sound projectileHitSound;
    private static Sound unitDiesSound;
    private static Sound errorSound;
    // alert list
    private static List<Alert> alertList;
    private static boolean doOnceDefender;
    private static boolean doOnceAttacker;
    private static ArrayList<String> multiplayerLogs;
    private static boolean searchingForGame;
    private final int HEIGHT = 720;
    private final int WIDTH = 1280;
    private MapRenderer renderer;
    private Sprite coinSprite;
    private float hpSpriteW;
    private BitmapFont unitNumber;
    private BitmapFont matchConclusionFont;
    private BitmapFont arialFont18;
    private BitmapFont boxyFont18;
    private Pixmap towerAttackPixmap;
    private Texture towerAttackTexture;
    private List<TroopSprite> unitsSprite = new ArrayList<>();
    private LevelEditor levelEditor;
    private TowerType ghostTowerType;
    // leaderboard
    private int currentbutton;
    private Sprite menuBackgroundSprite;
    private Sprite gameBackgroundSprite;
    private Sprite gameBackgroundBottomSprite;
    // leaderbaord
    private List<LeaderboardRow> leaderBoardTop;
    // sound related
    private boolean sliderClicked;
    private int selectedSlider;
    private Sprite[] upgradeBarSprite;
    private Texture clockTexture;
    private Sprite clockSprite;
    private Texture messageLogTexture;
    private Sprite messageLogSprite;
    private Texture HPUpgradeBackgroundTexture;
    private Sprite HPUpgradeBackgroundSprite;
    private Texture backgrounPanelTexture;
    private Sprite backgroundPanelSprite;

    public static void main(String[] args) {
        setupClient();
    }

    public static boolean setupClient() {
        System.out.println("Setting up client");
        client = new GameClient();
        if (!client.makeConnection()) {
            return false;
        }
        client.start();
        return true;
    }

    public static void newTurn() {
        changedTurn = true;
    }

    public static int getCurrentPath() {
        return currentPath;
    }

    public static void setCurrentPath(int newPath) {
        currentPath = newPath;
    }

    public static GameClient getClient() {
        return client;
    }

    public static void setSoundTrackVolume(float x) {
        soundTrackVolume = x;
    }

    public static float getSoundEffectsVolume() {
        return soundEffectsVolume;
    }

    public static void setSoundEffectsVolume(float x) {
        soundEffectsVolume = x;
    }

    public static boolean getMenuOpen() {
        return menuOpen;
    }

    public static void setMenuOpen(boolean bool) {
        menuOpen = bool;
    }

    public static BitmapFont getFontArial(int size) {
        fontParameterBoxy.size = size;
        return fontGenerator.generateFont(fontParameterBoxy);
    }

    public static boolean getSearchingForGame() {
        return searchingForGame;
    }

    public static void setSearchingForGame(boolean x) {
        searchingForGame = x;
    }

    public static BitmapFont getBloxyFont() {
        return font;
    }

    public static void switchPlayer() {
        if (!multiplayer) {
            if (player == 0) {
                player = 1;
                currentScreen = Screen.ATTACKER_SCREEN;
                return;
            }
            player = 0;
            currentScreen = Screen.DEFENDER_SCREEN;
        }
    }

    public static void playSound(String soundName) {
        Sound tmpSound;
        float tmpVolume = 1;
        switch (soundName) {
            case "projectileHit":
                tmpSound = projectileHitSound;
                break;
            case "projectileStart":
                tmpSound = projectileStartSound;
                break;
            case "unitDies":
                tmpSound = unitDiesSound;
                tmpVolume = 0.7f;
                break;
            case "ErrorSound":
                tmpSound = errorSound;
                tmpVolume = 0.7f;
                break;
            default:
                return;
        }
        tmpSound.play(soundEffectsVolume * tmpVolume);
    }

    public static void setBuildMode(boolean bool) {
        buildMode = bool;
    }

    public static void createButtons() {
        buttonList = new ArrayList<>();
        buttonList.add(
            new TowerButton("UI/NewArtMaybe/towerType1Button.png", 30, 50, Screen.DEFENDER_SCREEN,
                TowerType.type1));
        buttonList.add(
            new TowerButton("UI/NewArtMaybe/towerType2Button.png", 136, 50, Screen.DEFENDER_SCREEN,
                TowerType.type2));
        buttonList.add(
            new TowerButton("UI/NewArtMaybe/towerType3Button.png", 242, 50, Screen.DEFENDER_SCREEN,
                TowerType.type3));
        // main menu
        buttonList.add(new MainMenuButton("UI/NewArtMaybe/playLocalButton.png",
            Gdx.graphics.getWidth() / 2 - 150,
            Gdx.graphics.getHeight() / 2 + 105, Screen.MAIN_MENU, Screen.DEFENDER_SCREEN));

        buttonList.add(new MultiplayerButton("UI/NewArtMaybe/playOnlineButton.png",
            Gdx.graphics.getWidth() / 2 - 150,
            Gdx.graphics.getHeight() / 2 + 210, Screen.MAIN_MENU,
            player == 1 ? Screen.ATTACKER_SCREEN : Screen.DEFENDER_SCREEN));

        buttonList.add(new LevelSelectButton("UI/NewArtMaybe/selectALevelButton.png",
            Gdx.graphics.getWidth() / 2 - 150,
            Gdx.graphics.getHeight() / 2 - 5, Screen.MAIN_MENU, Screen.LEVEL_SELECT));

        buttonList.add(new LevelEditorButton("UI/NewArtMaybe/levelEditorButton.png",
            Gdx.graphics.getWidth() / 2 - 150,
            Gdx.graphics.getHeight() / 2 - 110, Screen.MAIN_MENU, Screen.LEVEL_EDITOR));

        buttonList.add(
            new MainMenuButton("UI/NewArtMaybe/exitButton.png", Gdx.graphics.getWidth() / 2 - 150,
                Gdx.graphics.getHeight() / 2 - 320, Screen.MAIN_MENU, null));

        buttonList.add(new LeaderBoardButton("UI/NewArtMaybe/leaderboardButton.png",
            Gdx.graphics.getWidth() / 2 - 150,
            Gdx.graphics.getHeight() / 2 - 215, Screen.MAIN_MENU, Screen.LEADERBOARD));

        // troop buttons
        buttonList.add(new UnitButton("UI/ufoButtonB.png", 30, 50, Screen.ATTACKER_SCREEN, 0));
        buttonList.add(new UnitButton("UI/ufo3ButtonB.png", 136, 50, Screen.ATTACKER_SCREEN, 1));
        buttonList.add(new UnitButton("UI/ufo2ButtonB.png", 242, 50, Screen.ATTACKER_SCREEN, 2));
        // select attacker/defender buttons
        buttonList.add(
            new PlayerButton("UI/NewArtMaybe/defenderButton.png", 100, Gdx.graphics.getHeight() / 2,
                Screen.CHOOSE_FACTION, Screen.DEFENDER_SCREEN, 0));
        buttonList.add(
            new PlayerButton("UI/NewArtMaybe/attackerButton.png", Gdx.graphics.getWidth() - 400,
                Gdx.graphics.getHeight() / 2, Screen.CHOOSE_FACTION, Screen.ATTACKER_SCREEN, 1));

        buttonList.add(
            new EndTurnButton("UI/NewArtMaybe/endTurnButton.png", Gdx.graphics.getWidth() - 350, 50,
                Screen.ATTACKER_SCREEN));
        buttonList.add(
            new EndTurnButton("UI/NewArtMaybe/endTurnButton.png", Gdx.graphics.getWidth() - 350, 50,
                Screen.DEFENDER_SCREEN));

        // defender upgrade button
        buttonList.add(new UpgradeButton("UI/NewArtMaybe/defenderUpgradeButton.png", 262 + 86, 50,
            Screen.DEFENDER_SCREEN));
        buttonList.add(new UnitUpgradeButton("UI/NewArtMaybe/unitsTier", 262 + 86, 50,
            Screen.ATTACKER_SCREEN));

        // attacker path buttons
        updatePathHighlighting();

        //Hover UI
        buttonList.add(
            new HoverUI("UI/healthBar1Hitbox.png", 3, Gdx.graphics.getHeight() - 112, null,
                "UI/InfoCards/infoPanelHealthbar.png", 10, Gdx.graphics.getHeight() - 275));
        buttonList.add(
            new HoverUI("UI/upgradeBarHitbox.png", 10, Gdx.graphics.getHeight() - 161, null,
                "UI/InfoCards/infoPanelUpgradebar.png", 10, Gdx.graphics.getHeight() - 275));
        buttonList.add(new HoverUI("UI/coinHitbox.png", Gdx.graphics.getWidth() - 215,
            Gdx.graphics.getHeight() - 85, null, "UI/InfoCards/infoPanelCoins.png",
            Gdx.graphics.getWidth() - 225, Gdx.graphics.getHeight() - 175));
        buttonList.add(new HoverUI("UI/timerHitbox.png", Gdx.graphics.getWidth() / 2 + 275,
            Gdx.graphics.getHeight() - 77, null, "UI/InfoCards/infoPanelTimer.png",
            Gdx.graphics.getWidth() / 2 + 235, Gdx.graphics.getHeight() - 175));
        // menu buttons
        menuButtonList = new ArrayList<>();
        // slider buttons
        menuButtonList.add(new SliderButton("UI/slider.png", 557, 410, Screen.MENU, 0));
        menuButtonList.add(new SliderButton("UI/slider.png", 557, 360, Screen.MENU, 1));

        menuButtonList.add(new MenuButton("UI/returnToGameTmp.png", 510, 470, Screen.MENU));
        menuButtonList.add(
            new QuitMenuButton("UI/NewArtMaybe/exitButton.png", 510, 250, Screen.MENU,
                Screen.MAIN_MENU));


    }

    public static void createSound() {
        //Setting up soundtrack
        soundTrack = Gdx.audio
            .newMusic(Gdx.files.internal("sound/RGA-GT - Being Cool Doesn`t Make Me Fool.mp3"));
        soundTrack.setVolume(1.0f);
        soundTrack.play();
        soundTrack.setLooping(true);
        soundTrackVolume = 1.0f;

        //Setting up sounds
        soundEffectsVolume = 1.0f;
        selectSound = Gdx.audio.newSound(Gdx.files.internal("sound/click3.wav"));
        projectileStartSound = Gdx.audio.newSound(Gdx.files.internal("sound/projectileStart.wav"));
        projectileHitSound = Gdx.audio.newSound(Gdx.files.internal("sound/projectileHit.ogg"));
        unitDiesSound = Gdx.audio.newSound(Gdx.files.internal("sound/unitDies.wav"));
        errorSound = Gdx.audio.newSound(Gdx.files.internal("sound/ErrorSound.wav"));
    }

    public static void updatePathHighlighting() {
        List<Path> paths = new ArrayList<>();
        buttonList.removeIf(button -> button instanceof PathButton);
        for (List<Path> path : GameController.getLevel().getPaths()) {
            paths.add(path.get(0));
        }
        for (int i = 0; i < paths.size(); i++) {
            MatrixCoordinates mc = paths.get(i).getMatrixPosition();
            RealWorldCoordinates rwc = new RealWorldCoordinates(
                mc.getX() * GameController.getScaleFactor(),
                mc.getY() * GameController.getScaleFactor());
            Vector2 screenPos = Conversions.realWorldCoordinatesToScreenPosition(rwc);
            buttonList.add(
                new PathButton("UI/pathHighlight.png", screenPos.x - 41, screenPos.y - 18,
                    Screen.ATTACKER_SCREEN, i));
        }
    }

    // need to hide button once defender has bought all upgrades
    public static void defenderMaxLevel() {
        UpgradeButton.maxLevel = true;
    }

    public static void createAlert(float targetTime, BitmapFont font, String text, int x, int y,
        Screen alertScreen) {
        alertList.add(new Alert(targetTime, font, text, x, y, alertScreen));
    }

    public static void showAlert() {
        if (!alertList.isEmpty()) {
            int i = 0;
            while (i < alertList.size()) {
                if (!(alertList.get(i).getCurrentScreen() == currentScreen
                    || alertList.get(i).getCurrentScreen() == null)) {
                    i++;
                } else {
                    break;
                }
            }
            if (i == alertList.size()) {
                return;
            }
            alertList.get(i).update(Gdx.graphics.getDeltaTime(), batch, currentScreen);
            if (alertList.get(i).getDeleteStatus()) {
                alertList.remove(i);
            }

        }
    }

    public static void changeScreen(int change) {
        levelSelectPage += change;
        if (levelSelectPage < 0) {
            levelSelectPage = 0;
        } else {
            try {
                if (levelSelectPage > (DatabaseCommunication.numberOfMaps() / levelsToShow)) {
                    levelSelectPage = (DatabaseCommunication.numberOfMaps() / levelsToShow);
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static void selectLevelScreen()
        throws FileNotFoundException, SQLException, ClassNotFoundException {
        int pagesNeeded = (int) Math
            .ceil((double) (DatabaseCommunication.numberOfMaps() + 1) / levelsToShow);

        // if changed page, remove old load buttons
        if (previousSelectPage != levelSelectPage) {
            // Remove any old load from the button list, so they can be re-added with new jsons
            for (int i = 0; i < buttonList.size(); i++) {
                if (buttonList.get(i) instanceof LoadLevelButton) {
                    System.out.println("Removing load button");
                    buttonList.remove(i);
                    i--;
                }
                if (buttonList.get(i) instanceof ChangePageButton) {
                    System.out.println("Removing change page button");
                    buttonList.remove(i);
                    i--;
                }
            }

            // if need multiple pages, and are currently on first page, only need a forward button
            if (levelSelectPage == 0) {
                buttonList.add(new ChangePageButton("UI/NewArtMaybe/forwardPage.png",
                    Gdx.graphics.getWidth() / 2 + 400, Gdx.graphics.getHeight() / 2 + 250,
                    Screen.LEVEL_SELECT, 1));
            }
            // if need multiple pages, and are on the last page, only need a back button
            else if (levelSelectPage == pagesNeeded - 1) {
                buttonList.add(new ChangePageButton("UI/NewArtMaybe/backwardPage.png",
                    Gdx.graphics.getWidth() / 2 + 400, Gdx.graphics.getHeight() / 2 + 250,
                    Screen.LEVEL_SELECT, -1));
            }
            // otherwise, need both forward and backward buttons
            else {
                buttonList.add(new ChangePageButton("UI/NewArtMaybe/forwardPage.png",
                    Gdx.graphics.getWidth() / 2 + 400, Gdx.graphics.getHeight() / 2 + 250,
                    Screen.LEVEL_SELECT, 1));
                buttonList.add(new ChangePageButton("UI/NewArtMaybe/backwardPage.png",
                    Gdx.graphics.getWidth() / 2 + 300, Gdx.graphics.getHeight() / 2 + 250,
                    Screen.LEVEL_SELECT, -1));
            }
        }

        List<Map> maps = new ArrayList<>();
        int startIndex = levelSelectPage * levelsToShow;
        int endIndex = (levelSelectPage + 1) * levelsToShow;

        // if on first page, first map is default map, so return levelsToShow-1
        if (levelSelectPage == 0) {
            endIndex--;
            FileInputStream inputStream = null;

            inputStream = new FileInputStream("core/assets/maps/MainMap.json");

            JSONTokener tokener = new JSONTokener(inputStream);
            JSONObject json = new JSONObject(tokener);
            Map map = new Map("Level One", json.toString(), false);
            maps.add(map);
        } else {
            endIndex--;
            startIndex--;
        }
        maps.addAll(DatabaseCommunication.getMaps(startIndex, endIndex));
        batch.begin();

        List<Sprite> levelSelectSprites = new ArrayList<>();
        int yOffset = 0;
        for (int i = 0; i < maps.size(); i++, yOffset -= 100) {
            Sprite sprite = new Sprite(levelSelectRowTexture);
            sprite.setPosition(Gdx.graphics.getWidth() / 2 - 500,
                Gdx.graphics.getHeight() / 2 + 100 + yOffset);
            levelSelectSprites.add(sprite);
            String str = "Name: " + maps.get(i).getMapName();
            sprite.draw(batch);
            leaderBoardRowText.draw(batch, str, Gdx.graphics.getWidth() / 2 - 400,
                Gdx.graphics.getHeight() / 2 + 175 + yOffset);

            if (previousSelectPage != levelSelectPage) {
                buttonList.add(new LoadLevelButton("UI/NewArtMaybe/loadButton.png",
                    Gdx.graphics.getWidth() / 2 + 400, Gdx.graphics.getHeight() / 2 + 100 + yOffset,
                    Screen.LEVEL_SELECT, maps.get(i).getJson()));
            }
        }

        // render the load level buttons
        for (Button button : buttonList) {
            if (button.getScreenLocation() == currentScreen) {
                button.getSprite().draw(batch);
            }
        }
        previousSelectPage = levelSelectPage;
        batch.end();
    }

    public static void setHasExited() {
        hasExited = true;
    }

    public BitmapFont getPixelatedFont() {
        return unitNumber;
    }

    public void resetGame() {
        currentScreen = Screen.MAIN_MENU;
        GameController.initialise();
        doOnceDefender = true;
        doOnceAttacker = true;
        currentPath = 0;
        for (Button button : buttonList) {
            if (button instanceof PathButton) {
                PathButton.resetSelected();
                break;
            }
        }
        player = 0;
        for(Button button : buttonList)
            if(button instanceof UpgradeButton)
                ((UpgradeButton)button).reset();
        renderer.setLevel(GameController.getLevel());
    }

    public void buildTrue() {
        buildMode = true;
    }

    public void setGhostTowerType(TowerType newType) {
        ghostTowerType = newType;
    }

    public void setLeaderBoardTop(int count) throws SQLException, ClassNotFoundException {
        leaderBoardTop = DatabaseCommunication.getHighScores(count);
    }

    public void isometricPov() {
        renderer.render();

        renderer.getBatch().begin();

        if (player == 0) {
            // DEFENDER
            if (buildMode && ghostTowerType != null) {
                Texture tmpTower = new Texture(Gdx.files
                    .internal("towers/sprites/" + ghostTowerType.getSpriteName() + ".png"));
                Sprite tmpSpriteTower = new Sprite(tmpTower);
                RealWorldCoordinates rwc = snap(Gdx.input.getX(), Gdx.input.getY());
                if (GameController.verifyTowerPlacement(ghostTowerType, rwc)) {
                    renderer.getBatch().setColor(0, 1, 0, 0.5f);
                } else {
                    renderer.getBatch().setColor(1, 0, 0, 0.5f);
                }

                Vector2 coords = Conversions.realWorldCooridinateToIsometric(rwc);
                float horizontalSpriteOffset = 24;
                float verticalSpriteOffset = 8;
                renderer.getBatch().draw(tmpSpriteTower, coords.x - horizontalSpriteOffset,
                    coords.y - verticalSpriteOffset, 48,
                    48 * tmpSpriteTower.getHeight() / tmpSpriteTower.getWidth());
                renderer.getBatch().setColor(Color.WHITE);
            }

        }

        renderer.getBatch().end();
    }

    public void healthPercentage() {
        float result = GameController.defender.getHealth() / 100.0f;
        healthSprite.setBounds(healthSprite.getX(), healthSprite.getY(), hpSpriteW * result,
            healthSprite.getHeight());
    }

    public void generateFont() {
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("UI/boxybold.ttf"));
        fontParameterBoxy = new FreeTypeFontParameter();
        fontParameterBoxy.size = 18;
        boxyFont18 = fontGenerator.generateFont(fontParameterBoxy);
        fontParameterBoxy.size = 30;
        timerFont = fontGenerator.generateFont(fontParameterBoxy);
        fontParameterBoxy.size = 40;
        font = fontGenerator.generateFont(fontParameterBoxy);
        matchConclusionFont = fontGenerator.generateFont(fontParameterBoxy);
        fontGenerator.dispose();
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("UI/arial.ttf"));
        FreeTypeFontParameter fontParameterArial = new FreeTypeFontParameter();
        fontParameterArial.size = 18;
        leaderBoardRowText = fontGenerator.generateFont(fontParameterBoxy);
        arialFont18 = fontGenerator.generateFont(fontParameterArial);
    }

    public void createFonts() {
        unitNumber = new BitmapFont();
        unitNumber.setColor(Color.WHITE);
        hpCounter = new BitmapFont();
        hpCounter.setColor(Color.WHITE);
        coinCounter = new BitmapFont();
        coinCounter.setColor(Color.WHITE);
        leaderBoardRowText = new BitmapFont();
        matchConclusionFont = new BitmapFont();
        arialFont18 = new BitmapFont();
        boxyFont18 = new BitmapFont();
    }

    public void showHealth() {
        healthPercentage();
        healthBarSprite.draw(batch);
        healthSprite.draw(batch);
        String nr = GameController.defender.getHealth() + "";
        hpCounter.getData().setScale(1.5f);
        boxyFont18
            .draw(batch, nr + " / 100", 205 - (nr.length() - 1) * 5, Gdx.graphics.getHeight() - 51);
    }

    public void showUnitHealthBar() {
        List<Troop> tmpUnits = GameController.getTroops();
        unitsSprite = new ArrayList<>();
        if (tmpUnits.size() > 0) {
            for (Troop tmpUnit : tmpUnits) {
                unitsSprite.add(new TroopSprite(tmpUnit));
            }
            for (TroopSprite troopSprite : unitsSprite) {
                troopSprite.getHealthBarSpriteBase().draw(batch);
                troopSprite.getHealthBarSpriteGreen().draw(batch);
            }
        }
    }

    public void disposeUnitHealthBar() {
        List<Troop> tmpUnits = GameController.getTroops();
        if (tmpUnits.size() > 0) {
            unitsSprite = new ArrayList<>();
        }
        for (TroopSprite troopSprite : unitsSprite) {
            troopSprite.dispose();
        }
    }

    public void showTowerAttack() {
        towerAttackPixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
            Pixmap.Format.RGBA8888);

        boolean draw = false;

        for (Projectile proj : GameController.getProjectiles()) {
            if (proj.getTower() instanceof TowerType1) {
                towerAttackPixmap.setColor(Color.RED);
            } else if (proj.getTower() instanceof TowerType2) {
                towerAttackPixmap.setColor(Color.BLUE);
            } else if (proj.getTower() instanceof TowerType3) {
                towerAttackPixmap.setColor(Color.YELLOW);
            }

            draw = true;
            Vector2 projScreenPosition = Conversions
                .realWorldCoordinatesToScreenPosition(proj.getRealWorldCoordinates());

            int projX = (int) projScreenPosition.x, projY = (int) projScreenPosition.y;

            towerAttackPixmap.fillCircle(projX, Gdx.graphics.getHeight() - projY, 3);
        }
        towerAttackTexture = new Texture(towerAttackPixmap);
        Sprite towerAttackSprite = new Sprite(towerAttackTexture);
        towerAttackSprite.setPosition(0, 0);
        if (!draw) {
            return;
        }
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
        timerFont.draw(batch, tmpCoinCounter, Gdx.graphics.getWidth() - 135,
            Gdx.graphics.getHeight() - 35);

        coinSprite.draw(batch);
    }

    public void showMultiplayerLogs(SpriteBatch batch) {
        if (client != null) {
            if (searchingForGame) {
                if (!client.getLogMessages().isEmpty()) {
                    multiplayerLogs = client.getLogMessages();
                    messageLogSprite.draw(batch);
                    for (int i = 0; i < multiplayerLogs.size(); i++) {
                        arialFont18.draw(batch, multiplayerLogs.get(i), 400, 575 - i * 25);
                    }
                }
                multiplayerLogs.clear();
            }
        }
    }

    public void defenderPOV() {
        drawButtons();
        if (GameController.getDefenderUpgrade() == 3 && doOnceDefender) {
            doOnceDefender = false;
            createAlert(2, font, "You reached Tier 3" + '\n' + " Survive this turn!",
                Gdx.graphics.getWidth() / 2 - 230, Gdx.graphics.getHeight() - 100,
                Screen.DEFENDER_SCREEN);

        }
        showAlert();
        for (Button button : buttonList) {
            if (button.getScreenLocation() == Screen.DEFENDER_SCREEN) {
                if (button instanceof UpgradeButton) {
                    if (!UpgradeButton.maxLevel) {
                        button.getSprite().draw(batch);
                    }
                } else {
                    if (!(button instanceof SliderButton)) {
                        button.getSprite().draw(batch);
                    }
                }
            }
        }
        if (GameController.getWaveState() == GameController.WaveState.AttackerBuild) {
            buildMode = false;
        }
        showHealth();
        showCoins(GameController.defender);
    }

    private void drawButtons() {
        for (Button button : buttonList) {
            if (button instanceof HoverButton && button.getScreenLocation() == currentScreen
                || button.getScreenLocation() == null) {
                ((HoverButton) button)
                    .update(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), batch);
                button.getSprite().draw(batch);
            }
        }
    }

    public void attackerPOV() {
        drawButtons();
        if (GameController.getDefenderUpgrade() == 3 && doOnceAttacker && player == 1) {
            doOnceAttacker = false;
            createAlert(2, font, "Defender reached Tier 3" + '\n' + " Destroy the castle now!",
                Gdx.graphics.getWidth() / 2 - 300, Gdx.graphics.getHeight() - 100,
                Screen.ATTACKER_SCREEN);

        }
        showAlert();
        for (Button button : buttonList) {
            if (button.getScreenLocation() == Screen.ATTACKER_SCREEN
                && !(button instanceof SliderButton)) {
                button.getSprite().draw(batch);
                if (button instanceof UnitUpgradeButton) {
                    ((UnitUpgradeButton) button).showCooldown(batch);
                }
            }
        }
        unitNumber.getData().setScale(1.5f);
        for (Button button : buttonList) {
            if (button instanceof UnitButton) {
                int tmpPath = currentPath;
                int tmpTroopType = ((UnitButton) button).getTroopType();
                String unitNr = "" + EventManager.getUnitBuildPlan()[tmpTroopType][tmpPath];
                unitNumber.draw(batch, unitNr,
                    button.getX() + button.getSprite().getWidth() - 20 - (unitNr.length() - 1) * 10,
                    button.getY() + 25);
            }
        }
        showHealth();
        showCoins(GameController.attacker);
    }

    public void attackerTouchDown(int x, int y, int pointer, int button) {
        if (GameController.getWaveState() == GameController.WaveState.AttackerBuild) {
            if (button == Input.Buttons.LEFT) {
                for (Button value : buttonList) {
                    if (value.checkClick(x, y) && value.getScreenLocation() == currentScreen) {
                        value.leftButtonAction();
                    }
                }
            } else {
                for (Button value : buttonList) {
                    if (value.checkClick(x, y) && value.getScreenLocation() == currentScreen) {
                        value.rightButtonAction();
                    }
                }
            }
        }
    }

    public RealWorldCoordinates roundToCentreTile(RealWorldCoordinates rwc) {
        MatrixCoordinates matrixCoords = new MatrixCoordinates(rwc);
        return new RealWorldCoordinates(32 + matrixCoords.getX() * 64,
            32 + matrixCoords.getY() * 64);
    }

    public RealWorldCoordinates snap(int x, int y) {
        Vector2 coords = new Vector2(x * 2, Gdx.graphics.getHeight() - (y * 2));
        RealWorldCoordinates rwc = Conversions.isometricToRealWorldCoordinate(coords);
        return roundToCentreTile(rwc);
    }

    public void defenderTouchDown(int x, int y, int button) {
        if (GameController.getWaveState() == GameController.WaveState.DefenderBuild) {
            if (button == Buttons.LEFT) {
                if (buildMode && ghostTowerType != null) {
                    // Place tower
                    RealWorldCoordinates rwc = snap(Gdx.input.getX(), Gdx.input.getY());
                    if (GameController.verifyTowerPlacement(ghostTowerType, rwc)) {
                        selectSound.play(soundEffectsVolume);
                        EventManager.towerPlaced(ghostTowerType, rwc);
                        if (!(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))) {
                            buildMode = false;
                        }
                    } else {
                        playSound("ErrorSound");
                    }

                } else {
                    for (Button value : buttonList) {
                        if (value.checkClick(x, y) && value.getScreenLocation() == currentScreen) {
                            value.leftButtonAction();
                            buildMode = true;
                            return;
                        }
                    }
                }
            } else {
                RealWorldCoordinates rwc = snap(Gdx.input.getX(), Gdx.input.getY());
                MatrixCoordinates mc = new MatrixCoordinates(rwc);
                if (renderer.getLevel().objectAt(mc) instanceof Tile) {
                    Tile t = (Tile) renderer.getLevel().objectAt(mc);
                    if (t.getTower() != null && !t.getTower().getIsCompleted() && !buildMode) {
                        EventManager.towerRemoved(t.getTower());
                        return;
                    }
                }
            }
        }
        if (button == Buttons.RIGHT && buildMode) {
            buildMode = false;
        }
    }

    public void disposeTMP() {
        disposeUnitHealthBar();
        disposeAttacks();
    }

    @Override
    public void create() {
        instance = this;
        createFonts();
        generateFont();
        createSound();
        createButtons();
        player = 0;
        searchingForGame = false;
        multiplayerLogs = new ArrayList<>();
        multiplayerLogs.add("Searching for Game!");
        alertList = new ArrayList<>();
        menuOpen = false;
        selectedSlider = -1;
        sliderClicked = false;
        currentPath = 0;
        batch = new SpriteBatch();
        doOnceDefender = true;
        doOnceAttacker = true;
        selectSound = Gdx.audio.newSound(Gdx.files.internal("sound/click3.wav"));
        menuBackgroundSprite = new Sprite(new Texture("maps/Background.png"));
        gameBackgroundSprite = new Sprite(new Texture("maps/BackgroundMap.png"));
        gameBackgroundBottomSprite = new Sprite(new Texture("maps/BackgroundMapBottom.png"));
        renderer = new MapRenderer();
        OrthographicCamera camera = new OrthographicCamera(WIDTH * 2, HEIGHT * 2);
        camera.position.set(WIDTH, 0, 10);
        camera.update();
        renderer.setView(camera);

        Texture healthBarTexture = new Texture(Gdx.files.internal("UI/healthBar1.png"));
        healthBarSprite = new Sprite(healthBarTexture);
        Texture healthTexture = new Texture(Gdx.files.internal("UI/health.png"));
        healthSprite = new Sprite(healthTexture);

        healthSprite.setScale(5);
        healthSprite.setPosition(210, Gdx.graphics.getHeight() - 64);
        //healthBarSprite.setScale(5);
        healthBarSprite.setPosition(170, Gdx.graphics.getHeight() - 70);
        Texture coinTexture = new Texture(Gdx.files.internal("UI/coins.png"));
        coinSprite = new Sprite(coinTexture);
        coinSprite.setScale(1.5f);

        Texture menuTexture = new Texture(Gdx.files.internal("UI/menu_4.png"));
        menuSprite = new Sprite(menuTexture);
        menuSprite.setPosition(Gdx.graphics.getWidth() / 3.2f, Gdx.graphics.getHeight() / 3 - 10);

        renderer.setLevel(GameController.getLevel());
        hpSpriteW = healthSprite.getWidth();
        healthBarSprite.setPosition(3, Gdx.graphics.getHeight() - 112);

        leaderboardRowTexture = new Texture(
            Gdx.files.internal("UI/NewArtMaybe/leaderboardRow.png"));
        levelSelectRowTexture = new Texture(
            Gdx.files.internal("UI/NewArtMaybe/leaderboardRow.png"));

        clockTexture = new Texture(Gdx.files.internal("UI/clockS.png"));
        clockSprite = new Sprite(clockTexture);
        clockSprite.setPosition(Gdx.graphics.getWidth() / 2 + 275, Gdx.graphics.getHeight() - 73);
        currentScreen = Screen.MAIN_MENU;
        //Upgrade bar
        upgradeBarSprite = new Sprite[4];
        Texture[] upgradeBarTexture = new Texture[4];
        for (int i = 0; i < 4; i++) {
            upgradeBarTexture[i] = new Texture("UI/" + i + "Upgrade.png");
            upgradeBarSprite[i] = new Sprite(upgradeBarTexture[i]);

            upgradeBarSprite[i].scale(1.5f);
            upgradeBarSprite[i].setPosition(100, 570);
        }
        messageLogTexture = new Texture(Gdx.files.internal("UI/messageLogS.png"));
        messageLogSprite = new Sprite(messageLogTexture);
        messageLogSprite.setPosition(385, 300);

        HPUpgradeBackgroundTexture = new Texture(Gdx.files.internal("UI/HP+UpgradeBackground.png"));
        HPUpgradeBackgroundSprite = new Sprite(HPUpgradeBackgroundTexture);
        HPUpgradeBackgroundSprite.setPosition(0, Gdx.graphics.getHeight() - 200);

        backgrounPanelTexture = new Texture("UI/NewArtMaybe/panelB.png");
        backgroundPanelSprite = new Sprite(backgrounPanelTexture);
        backgroundPanelSprite
            .setPosition(Gdx.graphics.getWidth() - backgroundPanelSprite.getWidth() - 10,
                Gdx.graphics.getHeight() - backgroundPanelSprite.getHeight() - 10);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (hasExited) {
            exitLevelEditor();
        }

        if (currentScreen == null) {
            Gdx.app.exit();
        } else if (currentScreen == Screen.MAIN_MENU || currentScreen == Screen.CHOOSE_FACTION) {
            batch.begin();
            menuBackgroundSprite.draw(batch);

            for (Button button : buttonList) {
                if (button.getScreenLocation() == currentScreen) {
                    button.getSprite().draw(batch);
                }
            }
            showMultiplayerLogs(batch);
            batch.end();
        } else if (currentScreen == Screen.LEVEL_EDITOR) {
            if (levelEditor == null) {
                levelEditor = new LevelEditor(renderer, batch);
            }
            levelEditor.run(new MatrixCoordinates(snap(Gdx.input.getX(), Gdx.input.getY())), false);
            batch.begin();
            for (Button button : levelEditor.getButtons()) {
                button.getSprite().draw(batch);
            }
            batch.end();
        } else if (currentScreen == Screen.LEADERBOARD) {
            batch.begin();
            try {
                leaderBoardTop = DatabaseCommunication.getHighScores(5);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            List<Sprite> leaderboardRowSprites = new ArrayList<>();
            int yOffset = 0;
            for (int i = 0; i < leaderBoardTop.size(); i++, yOffset -= 100) {
                Sprite sprite = new Sprite(leaderboardRowTexture);
                sprite.setPosition(Gdx.graphics.getWidth() / 2 - 500,
                    Gdx.graphics.getHeight() / 2 + 100 + yOffset);
                leaderboardRowSprites.add(sprite);
            }

            int i = 0;
            yOffset = 0;
            for (Sprite sprite : leaderboardRowSprites) {
                String str =
                    "name: " + leaderBoardTop.get(i).getName() + ", waves: " + leaderBoardTop.get(i)
                        .getWaves() + ", date:  " + leaderBoardTop.get(i).getDateTime();

                leaderBoardRowText.draw(batch, str, Gdx.graphics.getWidth() / 2 - 400,
                    Gdx.graphics.getHeight() / 2 + 175 + yOffset);
                sprite.draw(batch);

                yOffset -= 100;
                i++;
            }
            batch.end();
        } else if (currentScreen == Screen.LEVEL_SELECT) {
            try {
                selectLevelScreen();
            } catch (FileNotFoundException | SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            if (loading) {
                loading = false;
                return;
            }
            if (multiplayer) {
                if (!client.isConnected()) {
                    return;
                }
            }
            float elapsedTime = Gdx.graphics.getDeltaTime();
            GameController.update(elapsedTime);
            batch.begin();
            gameBackgroundSprite.setSize(1280, 720);
            gameBackgroundSprite.draw(batch);
            batch.end();
            isometricPov();
            batch.begin();
            backgroundPanelSprite.draw(batch);
            gameBackgroundBottomSprite.setSize(1280, 720);
            gameBackgroundBottomSprite.draw(batch);
            if (player == 0) {
                defenderPOV();
            } else if (player == 1) {
                attackerPOV();
            }
            if (changedTurn) {
                switch (GameController.getWaveState()) {
                    case AttackerBuild:
                        createAlert(2, font, "Attacker's Turn", Gdx.graphics.getWidth() / 2 - 230,
                            Gdx.graphics.getHeight() - 100, null);
                        break;
                    case DefenderBuild:
                        createAlert(2, font, "Defender's Turn", Gdx.graphics.getWidth() / 2 - 230,
                            Gdx.graphics.getHeight() - 100, null);
                        break;
                    case Play:
                        createAlert(2, font, "           Play     ",
                            Gdx.graphics.getWidth() / 2 - 230, Gdx.graphics.getHeight() - 100,
                            null);
                        break;
                }
                changedTurn = false;
            }
            showUnitHealthBar();
            showTowerAttack();
            upgradeBarSprite[GameController.getDefenderUpgrade()].draw(batch);

            if (GameController.getWaveState() == GameController.WaveState.End ||
                GameController.getWaveState() == GameController.WaveState.WaitingForInput ||
                GameController.getWaveState() == GameController.WaveState.SubmitInput) {
                if (GameController.getDefenderUpgrade() == 3) {
                    if (player == 0) {
                        matchConclusionFont
                            .draw(batch, "You Won!", Gdx.graphics.getWidth() / 2 - 150,
                                Gdx.graphics.getHeight() - 100);
                    } else {
                        matchConclusionFont
                            .draw(batch, "You Lost!", Gdx.graphics.getWidth() / 2 - 150,
                                Gdx.graphics.getHeight() - 100);
                    }
                } else {
                    if (GameController.getDefenderHealth() == 0) {
                        if (player == 1) {
                            matchConclusionFont
                                .draw(batch, "You Won!", Gdx.graphics.getWidth() / 2 - 150,
                                    Gdx.graphics.getHeight() - 100);
                        } else {
                            matchConclusionFont
                                .draw(batch, "You Lost!", Gdx.graphics.getWidth() / 2 - 150,
                                    Gdx.graphics.getHeight() - 100);
                        }
                    }
                }
            }
            if (GameController.getWaveState() == GameController.WaveState.AttackerBuild ||
                GameController.getWaveState() == GameController.WaveState.DefenderBuild ||
                GameController.getWaveState() == GameController.WaveState.Play) {
                String timerTmp = String
                    .format("%02d", 30 - (int) GameController.getBuildPhaseTimer());
                if (GameController.getWaveState() == GameController.WaveState.Play) {
                    timerTmp = "Play";
                }
                timerFont.draw(batch, timerTmp, Gdx.graphics.getWidth() / 2 + 325,
                    Gdx.graphics.getHeight() - 35);
                clockSprite.draw(batch);
            }

            if (!alertList.isEmpty()) {
                showAlert();
            }
            if (menuOpen) {
                int x = Gdx.input.getX();
                int y = Gdx.graphics.getHeight() - Gdx.input.getY();
                menuSprite.draw(batch);
                for (Button button : menuButtonList) {
                    button.getSprite().draw(batch);
                    if (button instanceof SliderButton) {

                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                            if (button.checkClick(x, y) && !sliderClicked) {
                                button.leftButtonAction();
                                sliderClicked = true;
                                selectedSlider = ((SliderButton) button).getSoundType();
                                if (((SliderButton) button).getSoundType() == 0) {
                                    soundTrack.setVolume(soundTrackVolume);
                                }
                            } else if (((SliderButton) button).getSoundType() == selectedSlider
                                && sliderClicked) {
                                button.leftButtonAction();
                                if (((SliderButton) button).getSoundType() == 0) {
                                    soundTrack.setVolume(soundTrackVolume);
                                }
                            }
                        }
                    } else {
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !sliderClicked) {
                            if (button.checkClick(x, y)) {
                                button.leftButtonAction();
                                setMenuOpen(false);
                            }
                        }
                    }
                }
            }
            batch.end();
            disposeTMP();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        renderer.dispose();
        soundTrack.dispose();
        if (fontGenerator != null) {
            fontGenerator.dispose();
        }
        timerFont.dispose();
        font.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE && (currentScreen == Screen.DEFENDER_SCREEN
            || currentScreen == Screen.ATTACKER_SCREEN)) {
            if (menuOpen) {
                menuOpen = false;
            } else {
                menuOpen = true;
                buildMode = false;
            }
        } else if (keycode == Input.Keys.ESCAPE && currentScreen == Screen.MENU) {
            resetGame();
        } else if (keycode == Input.Keys.ESCAPE && currentScreen == Screen.LEADERBOARD
            || currentScreen == Screen.LEVEL_SELECT) {
            currentScreen = Screen.MAIN_MENU;
        } else if (keycode == Input.Keys.ESCAPE && currentScreen == Screen.LEVEL_EDITOR) {
            exitLevelEditor();
        }
        return false;
    }

    public void exitLevelEditor() {
        hasExited = false;
        currentScreen = Screen.MAIN_MENU;
        renderer.setLevel(GameController.getLevel());
        renderer.setColourExceptions(new HashMap<>());
        renderer.setLevelEditing(false);
        levelEditor = null;
    }

    // called by the load level buttons on the level select
    public void levelSelected(String mapJson) {
        GameController.levelSelected(mapJson);
        GameController
            .initialise(); // need to re-initialise to load in the new level that was selected (if one was selected)
        currentScreen = Screen.MAIN_MENU;
        renderer.setLevel(GameController.getLevel());
        updatePathHighlighting();
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
        currentbutton = button;
        int y = Gdx.graphics.getHeight() - screenY;
        if (button == Buttons.LEFT && (currentScreen == Screen.MAIN_MENU
            || currentScreen == Screen.CHOOSE_FACTION
            || currentScreen == Screen.LEVEL_SELECT)) {
            for (Button value : buttonList) {
                if (value.checkClick(screenX, y) && value.getScreenLocation() == currentScreen) {
                    value.leftButtonAction();
                    return false;
                }
            }
        }

        if (currentScreen == Screen.DEFENDER_SCREEN || currentScreen == Screen.ATTACKER_SCREEN) {
            if (player == 1 && !menuOpen) {
                attackerTouchDown(screenX, y, pointer, button);
            } else if (player == 0 && !menuOpen) {
                defenderTouchDown(screenX, y, button);
            }
        } else if (currentScreen == Screen.LEVEL_EDITOR && levelEditor != null) {
            for (Button b : levelEditor.getButtons()) {
                if (b.checkClick(screenX, y)) {
                    if (button == Buttons.LEFT) {
                        b.leftButtonAction();
                    }
                    return false;
                }
            }
            if (button == Buttons.LEFT) {
                levelEditor.setPlaced();
            } else if (button == Buttons.RIGHT) {
                levelEditor.remove();
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        sliderClicked = false;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (currentScreen == Screen.LEVEL_EDITOR && levelEditor != null && (currentbutton
            == Buttons.LEFT)) {
            levelEditor.setPlaced();
        }
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
