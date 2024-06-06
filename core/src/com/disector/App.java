package com.disector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import com.disector.gameworld.GameWorld;
import com.disector.editor.EditorInterface;
import com.disector.inputrecorder.InputRecorder;
import com.disector.renderer.Renderer;
import com.disector.renderer.SoftwareRenderer;

public class App extends ApplicationAdapter {
    private GameWorld gameWorld;
    private Renderer renderer;
    private EditorInterface editor;

    private float deltaTime;

    public final Array<Wall> walls = new Array<>();
    public final Array<Sector> sectors = new Array<>();

    public int frameWidth = 400;
    public int frameHeight = 225;

    public SpriteBatch batch;

    @Override
    public void create () {
        batch = new SpriteBatch();
        renderer = new SoftwareRenderer(this);
        gameWorld = new GameWorld(this);

        InputRecorder.repopulateKeyCodeMap();
        Gdx.input.setCursorCatched(true);

        createTestMap();
    }

    @Override
    public void render () {
        //ScreenUtils.clear(0, 0, 0, 1);

        updateDeltaTime();

        InputRecorder.updateKeys();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) //Toggle Mouse Locking
            Gdx.input.setCursorCatched( !Gdx.input.isCursorCatched() );

        gameWorld.step(deltaTime);

        renderer.placeCamera(gameWorld.getPlayerPosition(), gameWorld.getPlayerVLook(), 0);
        renderer.renderWorld();
        renderer.drawFrame();
    }

    @Override
    public void dispose () {
        batch.dispose();
    }

    private void updateDeltaTime() {
        deltaTime = Gdx.graphics.getDeltaTime();
        //int fps = (int) ( 1.f / deltaTime );
        //System.out.println("Fps: " + fps);
        if (deltaTime > 0.04f) deltaTime = 0.04f; //If below 25 frames/second only advance time as if it were running at 25fps
    }

    private void createTestMap() {
        Sector s = new Sector(); s.floorZ = 0; s.ceilZ = 50;
        walls.add(new Wall( 20, 20, 100, 20     )); s.walls.add(walls.size-1);
        walls.add(new Wall( 100, 20, 100, 80    )); s.walls.add(walls.size-1);
        walls.add(new Wall( 100, 80, 175, 80    )); s.walls.add(walls.size-1);
        walls.add(new Wall( 175, 80, 175, -20   )); s.walls.add(walls.size-1);
        walls.add(new Wall( 175, -20, 75, -125  )); s.walls.add(walls.size-1);
        walls.add(new Wall( 75, -125, 20, -125  )); s.walls.add(walls.size-1);
        walls.add(new Wall( 20, -125, 20, 20    )); s.walls.add(walls.size-1);
        sectors.add(s);
    }

}
