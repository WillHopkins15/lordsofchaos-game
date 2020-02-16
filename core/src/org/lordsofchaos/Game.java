package org.lordsofchaos;

import java.util.Arrays;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
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

public class Game extends ApplicationAdapter {

	SpriteBatch batch;
	OrthographicCamera camera;
	IsometricTiledMapRenderer renderer;
	TiledMap map;
	Troop troop;
	Troop troop2;

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
	
	@Override
	public void create () {

		batch = new SpriteBatch();
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

		renderer.setView(camera);

	}

	float x = 0;
	float y = 0;

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		renderer.render();

		Vector2 v2 = realWorldCooridinateToIsometric(troop.getRealWorldCoordinates());

		renderer.getBatch().begin();
		renderer.getBatch().draw(troop.getSprite(), v2.x, v2.y, 48, 48);
		renderer.getBatch().end();

		RealWorldCoordinates rwc = troop.getRealWorldCoordinates();
		//rwc.setY(rwc.getY() + 1);

		troop.setRealWorldCoordinates(rwc);

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
}