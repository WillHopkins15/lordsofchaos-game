package org.lordsofchaos;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import org.lordsofchaos.network.GameClient;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.TowerType;
import org.lordsofchaos.gameobjects.towers.Tower;
import org.lordsofchaos.gameobjects.troops.*;
import org.lordsofchaos.matrixobjects.Path;
import com.badlogic.gdx.Input.Buttons;
import org.lordsofchaos.graphics.*;

public class Game extends ApplicationAdapter implements InputProcessor {

	SpriteBatch batch;
	OrthographicCamera camera;
	IsometricTiledMapRenderer renderer;
	TiledMap map;
	Troop troop;
	private static float verticalSpriteOffset = 8;
	private static float horizontalSpriteOffset = 8;
	private static int player;
	private static Button towerButton;
	private static boolean buildMode = false;
	private static Button startButton;
	private static Button quitButton;
	private static Button unitButton;
	private static Button defenderButton;
	private static Button attackerButton;
	private BitmapFont unitNumber;
	int width = 1280;
	final int height = 720;
	private Screen currentScreen;
	private float elapsedTime;

	public static void main(String[] args) {
		setupClient();
	}

	private static void setupClient() {
		GameClient gc = new GameClient();
		if (gc.makeConnection()) {
			gc.runGame();
		}
		gc.close();
	}

	public static void createButtons() {
		towerButton = new Button("UI/towerButton.png", 30, 30);
		startButton = new Button("UI/startButton.png",
				Gdx.graphics.getWidth() / 2 - towerButton.getSprite().getWidth() / 2, Gdx.graphics.getHeight() / 2);
		quitButton = new Button("UI/quitButton.png",
				Gdx.graphics.getWidth() / 2 - startButton.getSprite().getWidth() / 2,
				Gdx.graphics.getHeight() / 2 - startButton.getSprite().getHeight() * 2);
		unitButton = new Button("UI/ufoButton.png", 50, 50);
		defenderButton = new Button("UI/defenderButton.png", 100, Gdx.graphics.getHeight() / 2);
		attackerButton = new Button("UI/attackerButton.png",
				Gdx.graphics.getWidth() - defenderButton.getSprite().getWidth() - 100, Gdx.graphics.getHeight() / 2);
	}

	public void isometricPov() {
		renderer.render();
		Path p = new Path(0, 0);
		troop = new TroopType1(Arrays.asList(p));

		List<Troop> troops = GameController.getTroops();
		renderer.getBatch().begin();
		for (int i = 0; i < troops.size(); i++) {
			Vector2 coordinates = realWorldCooridinateToIsometric(troops.get(i).getRealWorldCoordinates());
			renderer.getBatch().draw(troops.get(i).getSprite(), coordinates.x - horizontalSpriteOffset, coordinates.y - verticalSpriteOffset, 48, 48);
		}

		List<Tower> towers = GameController.getTowers();
		for (int i = 0; i < towers.size(); i++) {
			Vector2 coordinates = realWorldCooridinateToIsometric(towers.get(i).getRealWorldCoordinates());
			renderer.getBatch().draw(towers.get(i).getSprite(), coordinates.x - horizontalSpriteOffset, coordinates.y - verticalSpriteOffset, 48, 94);
		}

		if (player == 0) {
			// DEFENDER
			if (buildMode) {
				Texture tmpTower = new Texture(Gdx.files.internal("towers/Tower.png"));
				Sprite tmpSpriteTower2 = new Sprite(tmpTower);
				RealWorldCoordinates rwc = snap(Gdx.input.getX(), Gdx.input.getY());

				if (GameController.verifyTowerPlacement(rwc)) {
					renderer.getBatch().setColor(0, 200, 0, 0.5f);
				} else {
					renderer.getBatch().setColor(200, 0, 0, 0.5f);
				}

				Vector2 coords = realWorldCooridinateToIsometric(rwc);
				renderer.getBatch().draw(tmpSpriteTower2, coords.x - 24, coords.y - verticalSpriteOffset, 48, 94);
				renderer.getBatch().setColor(Color.WHITE);
			}

		} else {

		}

		renderer.getBatch().end();
	}

	public void defenderPOV() {
		isometricPov();
		batch.begin();
		towerButton.getSprite().draw(batch);

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
		batch.end();
		isometricPov();
	}

	public void attackerTouchDown(int x, int y, int pointer, int button) {
		if (button == Buttons.LEFT) {
			if (unitButton.checkClick(x, y)) {
				EventManager.buildPlanChange(0, 0, 1);
			}
		}
		if (button == Buttons.RIGHT) {
			if (unitButton.checkClick(x, y)) {
				EventManager.buildPlanChange(0, 0, -1);
			}
		}
	}

	public RealWorldCoordinates roundToCentreTile(RealWorldCoordinates rwc) {
		MatrixCoordinates matrixCoords = new MatrixCoordinates(rwc);
		return new RealWorldCoordinates(32 + matrixCoords.getY() * 64, 32 + matrixCoords.getX() * 64);
	}

	public RealWorldCoordinates snap(int x, int y) {
		Vector2 coords = new Vector2(x * 2, height - (y * 2));
		return roundToCentreTile(isometricToRealWorldCoordinate(coords));
	}

	public void defenderTouchDown(int x, int y, int pointer, int button) {
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
				if (GameController.verifyTowerPlacement(rwc)) {
					EventManager.towerPlaced(TowerType.type1, rwc);
					buildMode = false;
				}

			}
			if (towerButton.checkClick(x, y)) {
				System.out.println("Clicked towerButton");
				if (!buildMode)
					buildMode = true;
			}
		}
		if (button == Buttons.RIGHT) {
			if (buildMode)
				buildMode = false;
		}
	}

	@Override
	public void create() {

		batch = new SpriteBatch();
		unitNumber = new BitmapFont();
		unitNumber.setColor(Color.WHITE);
		map = new TmxMapLoader().load("maps/Isometric.tmx");
		renderer = new IsometricTiledMapRenderer(map);
		camera = new OrthographicCamera(width * 2, height * 2);
		camera.position.set(width, 0, 10);
		camera.update();
		createButtons();
		renderer.setView(camera);
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
		// diff.x -= 128;
		// diff.y -= 64;

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

				if (startButton.checkClick(x, y))
					currentScreen = Screen.CHOOSE_FACTION;
				else if (quitButton.checkClick(x, y)) {
					quitButton.dispose();
					startButton.dispose();
					Gdx.app.exit();
				}
				System.out.println(currentScreen);
			} else if (currentScreen == Screen.CHOOSE_FACTION) {
				if (defenderButton.checkClick(x, y)) {
					player = 0;
					currentScreen = Screen.GAME;
				} else if (attackerButton.checkClick(x, y)) {
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
}