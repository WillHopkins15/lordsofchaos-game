package org.lordsofchaos;

import java.util.Arrays;

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
import com.badlogic.gdx.Input.Keys;
import org.lordsofchaos.network.GameClient;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
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
	Troop troop2;
	private static int player;
	private static Button towerButton;
	private static boolean buildMode = false;
	private static Button startButton;
	private static Button quitButton;
	private static Button unitButton;
	private static Button defenderButton;
	private static Button attackerButton;
	private BitmapFont unitNumber;
	int width =  1280;
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
	public static void createButtons(){
		towerButton = new Button("UI/towerButton.png",30,30);
		startButton = new Button("UI/startButton.png", Gdx.graphics.getWidth() / 2 - towerButton.getSprite().getWidth() / 2,Gdx.graphics.getHeight() / 2);
		quitButton = new Button("UI/quitButton.png", Gdx.graphics.getWidth() / 2 - startButton.getSprite().getWidth() / 2,Gdx.graphics.getHeight() / 2 - startButton.getSprite().getHeight() *2);
		unitButton = new Button("UI/ufoButton.png", 50,50);
		defenderButton = new Button("UI/defenderButton.png",100,Gdx.graphics.getHeight() / 2);
		attackerButton = new Button("UI/attackerButton.png", Gdx.graphics.getWidth() - defenderButton.getSprite().getWidth() - 100,Gdx.graphics.getHeight() / 2);
	}
	public void isometricPov(){
		renderer.render();
		Vector2 v2 = realWorldCooridinateToIsometric(troop.getRealWorldCoordinates());
		renderer.getBatch().begin();
		//renderer.getBatch().draw(troop.getSprite(), v2.x, v2.y, 48, 48);

		RealWorldCoordinates rwcT = new RealWorldCoordinates(30, 30);
		//renderer.getBatch().draw(towerButton.getTexture(),realWorldCooridinateToIsometric(rwcT).x,realWorldCooridinateToIsometric(rwcT).y,200,100)
		renderer.getBatch().end();
		RealWorldCoordinates rwc = troop.getRealWorldCoordinates();
		//rwc.setY(rwc.getY() + 1);

		troop.setRealWorldCoordinates(rwc);

	}
	public  void defenderPOV(){
		isometricPov();
		batch.begin();
		towerButton.getSprite().draw(batch);
		Texture tmpTower = new Texture(Gdx.files.internal("towers/Tower.png"));
		Sprite tmpSpriteTower = new Sprite(tmpTower);
		if (buildMode) {

			tmpSpriteTower.setPosition(Gdx.input.getX() - tmpSpriteTower.getWidth() / 2, Gdx.graphics.getHeight() - Gdx.input.getY());
			batch.setColor(0, 200, 0, 0.5f);
			batch.draw(tmpSpriteTower, Gdx.input.getX() - 24, Gdx.graphics.getHeight() - Gdx.input.getY() - 16, 48, 94);
		}
		batch.end();

	}

	public  void attackerPOV(){
		isometricPov();
		batch.begin();
		unitButton.getSprite().draw(batch);
		unitNumber.getData().setScale(1.5f);
		int x = EventManager.getUnitBuildPlan()[0][0];
		String nr = new String(	""+x);

		unitNumber.draw(batch,nr,unitButton.getX() + unitButton.getSprite().getWidth() - 20 - (nr.length() - 1) * 10,unitButton.getY() + 25);
		batch.end();
		isometricPov();
	}
	public void attackerTouchDown(int x,int y, int pointer, int button){
		if(button == Buttons.LEFT){
			if(unitButton.checkClick(x,y)){
				EventManager.buildPlanChange(0,0,1);
			}
		}
		if(button == Buttons.RIGHT){
			if(unitButton.checkClick(x,y)){
				EventManager.buildPlanChange(0,0,-1);
			}
		}

	}
	public void defenderTouchDown(int x,int y, int pointer, int button){
		if(button == Buttons.LEFT) {
			if(currentScreen == Screen.MAIN_MENU){

				if(startButton.checkClick(x,y))
					currentScreen = Screen.GAME;
				else if(quitButton.checkClick(x,y)) {
					quitButton.dispose();
					startButton.dispose();
					Gdx.app.exit();
				}
			}
			if(buildMode){
				//Place tower
			}

			if(towerButton.checkClick(x,y) ) {
				System.out.println("Clicked towerButton");
				if (!buildMode)
					buildMode = true;
			}
		}
		if(button == Buttons.RIGHT){
			if(buildMode)
				buildMode = false;
		}
	}
	@Override
	public void create () {

		batch = new SpriteBatch();
		unitNumber = new BitmapFont();
		unitNumber.setColor(Color.WHITE);
		map = new TmxMapLoader().load("maps/Isometric.tmx");
		renderer = new IsometricTiledMapRenderer(map);
		camera = new OrthographicCamera(width * 2, height * 2);
		Path p = new Path(0, 0);
		troop = new TroopType1(Arrays.asList(p));
		troop2 = new TroopType2(Arrays.asList(p));
		System.out.println(troop.getRealWorldCoordinates().getX());
		System.out.println(troop.getRealWorldCoordinates().getY());
		camera.position.set(width, 0, 10);
		camera.update();
		createButtons();
		renderer.setView(camera);
		currentScreen = Screen.MAIN_MENU;
		Gdx.input.setInputProcessor(this);
	}

	//float x = 0;
	//float y = 0;

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(currentScreen == Screen.MAIN_MENU){
			batch.begin();
			startButton.getSprite().draw(batch);
			quitButton.getSprite().draw(batch);
			batch.end();
		}
		else if(currentScreen == Screen.CHOOSE_FACTION){
			batch.begin();
			defenderButton.getSprite().draw(batch);
			attackerButton.getSprite().draw(batch);
			batch.end();
		}
		else {
			elapsedTime = Gdx.graphics.getDeltaTime();
			//update(elapsedTime)
			if(player == 0)
				defenderPOV();
			if(player == 1)
				attackerPOV();
		}
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		//camera.viewportWidth = width;
		//camera.viewportHeight = height;
		camera.update();
	}
	
	@Override
	public void dispose () {
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

	public Vector2 realWorldCooridinateToIsometric(RealWorldCoordinates rwc) {
		Vector2 diff = cartesianToIsometric(1280 - 128, 1280 - 64);
		Vector2 v2 = cartesianToIsometric(rwc.getX() - diff.x, rwc.getY() - diff.y);
		return v2;
	}
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		if(keycode == Input.Keys.ESCAPE && currentScreen == Screen.GAME)
			currentScreen = Screen.CHOOSE_FACTION;
		else if(keycode == Input.Keys.ESCAPE && currentScreen == Screen.CHOOSE_FACTION)
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
		if(button == Buttons.LEFT) {
			if(currentScreen == Screen.MAIN_MENU){

				if(startButton.checkClick(x,y))
					currentScreen = Screen.CHOOSE_FACTION;
				else if(quitButton.checkClick(x,y)) {
					quitButton.dispose();
					startButton.dispose();
					Gdx.app.exit();
				}
				System.out.println(currentScreen);
			}
			else if(currentScreen == Screen.CHOOSE_FACTION){
				if(defenderButton.checkClick(x,y)){
					player = 0;
					currentScreen = Screen.GAME;
				}
				else if(attackerButton.checkClick(x,y)) {
					player = 1;
					currentScreen = Screen.GAME;
				}
			}

		}
		if(player == 1)
			attackerTouchDown(x,y,pointer,button);
		if(player == 0)
			defenderTouchDown(x,y,pointer,button);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		int x = screenX;
		int y = Gdx.graphics.getHeight() - screenY;
		/*if(button == Buttons.LEFT) {
			if(startButton.checkClick(x, y)) {
				currentScreen = Screen.MAIN_MENU;
				startButton.setPressedStatus(false);
			}
		}*/
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