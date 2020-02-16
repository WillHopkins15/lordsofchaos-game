package org.lordsofchaos;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
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
<<<<<<< Updated upstream
	Troop troop2;
	protected static Button towerButton;
	protected static boolean buildMode = false;
=======

>>>>>>> Stashed changes
	int width =  1280;
	final int height = 720;

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
		towerButton = new Button("UI/button.png",30,30);
	}
	@Override
	public void create () {

		batch = new SpriteBatch();
		map = new TmxMapLoader().load("maps/Isometric.tmx");
		renderer = new IsometricTiledMapRenderer(map);
		camera = new OrthographicCamera(width * 2, height * 2);
		camera.position.set(width, 0, 10);
		camera.update();
		createButtons();
		renderer.setView(camera);
		Gdx.input.setInputProcessor(this);
	}

	float x = 0;
	float y = 0;

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		renderer.render();

<<<<<<< Updated upstream
		Vector2 v2 = realWorldCooridinateToIsometric(troop.getRealWorldCoordinates());
		batch.begin();
		towerButton.getSprite().draw(batch);
		Texture tmpTower = new Texture(Gdx.files.internal("towers/Tower.png"));
		Sprite tmpSpriteTower = new Sprite(tmpTower);
		if(buildMode){

			tmpSpriteTower.setPosition(Gdx.input.getX() - tmpSpriteTower.getWidth() / 2,Gdx.graphics.getHeight() - Gdx.input.getY());
			batch.setColor(0,200,0,0.5f);
			batch.draw(tmpSpriteTower,Gdx.input.getX()  - 24,Gdx.graphics.getHeight() - Gdx.input.getY() - 16,48,94);
		}
		batch.end();
		renderer.getBatch().begin();
		renderer.getBatch().draw(troop.getSprite(), v2.x, v2.y, 48, 48);

		RealWorldCoordinates rwcT = new RealWorldCoordinates(30,30);
		//renderer.getBatch().draw(towerButton.getTexture(),realWorldCooridinateToIsometric(rwcT).x,realWorldCooridinateToIsometric(rwcT).y,200,100)
		renderer.getBatch().end();
		tmpTower.dispose();
		RealWorldCoordinates rwc = troop.getRealWorldCoordinates();
		//rwc.setY(rwc.getY() + 1);

		troop.setRealWorldCoordinates(rwc);
=======
		List<Troop> troops = GameController.getTroops();

		renderer.getBatch().begin();
		
		for (int i = 0; i < troops.size(); i++) {
			Vector2 coordinates = this.realWorldCooridinateToIsometric(troops.get(i).getRealWorldCoordinates());
			renderer.getBatch().draw(troop.getSprite(), coordinates.x, coordinates.y, 48, 48);
		}
>>>>>>> Stashed changes

		renderer.getBatch().end();
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
			if(buildMode){

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