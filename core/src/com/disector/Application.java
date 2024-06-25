package com.disector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;

import com.disector.assets.PixmapContainer;
import com.disector.editor.Editor;
import com.disector.gameworld.GameWorld;
import com.disector.inputrecorder.InputRecorder;
import com.disector.renderer.DimensionalRenderer;
import com.disector.renderer.GameMapRenderer;
import com.disector.renderer.SoftwareRenderer;

public class Application extends ApplicationAdapter {
    private static final boolean printFPS = false;

    private GameWorld gameWorld;
    private DimensionalRenderer renderer;
    private GameMapRenderer gameMapRenderer;
    private Editor editor;

    private AppFocusTarget focus;

    private float deltaTime;

    public final Array<Wall> walls = new Array<>();
    public final Array<Sector> sectors = new Array<>();

    public PixmapContainer textures;
    public Pixmap.Format pixelFormat = Pixmap.Format.RGBA4444;

    public int frameWidth = 400;
    public int frameHeight = 225;

    public SpriteBatch batch;
    public ShapeRenderer shape;

    @Override
    public void create () {
        focus = AppFocusTarget.GAME;

        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        shape.setColor(Color.WHITE);

        textures = new PixmapContainer();
        textures.loadImages();

        swapFocus(AppFocusTarget.GAME);

        InputRecorder.repopulateKeyCodeMap();
        Gdx.input.setCursorCatched(true);

        createTestMap();
    }

    @Override
    public void render () {
        updateDeltaTime();

        InputRecorder.updateKeys();

        if (Gdx.input.isKeyJustPressed(Input.Keys.F2))
            swapFocus(AppFocusTarget.GAME);
        else if (Gdx.input.isKeyJustPressed(Input.Keys.F5))
            swapFocus(AppFocusTarget.EDITOR);

        switch(focus) {
            case MENU: menu(); break;
            case GAME: game(); break;
            case EDITOR: editor(); break;
            default: System.out.println("How did you get here??"); break;
        }

    }

    @Override
    public void dispose () {
        batch.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0,0, width, height));
        shape.setProjectionMatrix(batch.getProjectionMatrix());

        switch(focus) {
            case GAME:
                if (gameMapRenderer != null) gameMapRenderer.resizeFrame(width, height);
                break;
            case EDITOR:
                if (editor != null) editor.resize(width, height);
                break;
            case MENU:
                break;
            default:
        }

    }

    private void swapFocus(AppFocusTarget target) {

        switch(focus) {
            case GAME:
                Gdx.input.setCursorCatched(false);
                break;
            case MENU:
                break;
            case EDITOR:
                //Gdx.graphics.setUndecorated(true);
                break;
            default:
        }

        switch (target) {
            case GAME:
                if (gameWorld==null) gameWorld = new GameWorld(this);
                if (renderer==null) renderer = new SoftwareRenderer(this);
                if (gameMapRenderer==null) gameMapRenderer = new GameMapRenderer(this, gameWorld);
                Gdx.input.setCursorCatched(true);
                break;
            case MENU:
                break;
            case EDITOR:
                if (gameWorld == null) {
                    System.out.println("Must instance GameWorld before Editor.");
                    break;
                }
                if (editor==null) editor = new Editor(this, gameWorld);
                break;
            default:
        }

        focus = target;
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    }

    private void game() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) //Toggle Mouse Locking
            Gdx.input.setCursorCatched( !Gdx.input.isCursorCatched() );

        gameWorld.step(deltaTime);

        renderer.placeCamera(gameWorld.getPlayerPosition(), gameWorld.getPlayerVLook(), gameWorld.getPlayerSectorIndex());
        renderer.renderWorld();
        renderer.drawFrame();

        if (gameWorld.shouldDisplayMap()) {
            gameMapRenderer.placeCamera(gameWorld.getPlayerPosition(), 0, gameWorld.getPlayerSectorIndex());
            gameMapRenderer.renderWorld();
            gameMapRenderer.drawFrame();
        }
    }

    private void menu() {

    }

    private void editor(){
        editor.step(deltaTime);
        editor.draw();
    }

    private void updateDeltaTime() {
        deltaTime = Gdx.graphics.getDeltaTime();
        if (printFPS) {
            int fps = (int) (1.f / deltaTime);
            System.out.println("Fps: " + fps);
        }
        if (deltaTime > 0.04f) deltaTime = 0.04f; //If below 25 frames/second only advance time as if it were running at 25fps
    }

    private void createTestMap() {
        walls.clear(); sectors.clear();

        Sector s = new Sector(); s.floorZ = 0; s.ceilZ = 50;
        walls.add(new Wall( 20, 20, 100, 20     )); s.walls.add(walls.size-1);
        walls.add(new Wall( 100, 20, 100, 80    )); s.walls.add(walls.size-1);
        Wall firstPortal = new Wall( 100, 80, 175, 80 );
            firstPortal.isPortal = true;
            firstPortal.linkA = 1;
            firstPortal.linkB = 0;
            walls.add(firstPortal);
            s.walls.add(walls.size-1);
        walls.add(new Wall( 175, 80, 175, -20   )); s.walls.add(walls.size-1);
        walls.add(new Wall( 175, -20, 75, -125  )); s.walls.add(walls.size-1);
        walls.add(new Wall( 75, -125, 20, -125  )); s.walls.add(walls.size-1);
        walls.add(new Wall( 20, -125, 20, -80   )); s.walls.add(walls.size-1);
        walls.add(new Wall( 20, -80, 50, -80    )); s.walls.add(walls.size-1);
        walls.add(new Wall( 50, -80, 50, -50    )); s.walls.add(walls.size-1);
        walls.add(new Wall( 50, -50, 20, 20     )); s.walls.add(walls.size-1);
        sectors.add(s);

        s = new Sector(); s.floorZ = -15; s.ceilZ = 40;
        s.walls.add(2); //Index 2 is First Portal
        walls.add(new Wall(100, 80, 90, 125 )); s.walls.add(walls.size-1);
        Wall secondPortal = new Wall(90, 125, 185, 125);
            secondPortal.isPortal = true;
            secondPortal.linkA = 2;
            secondPortal.linkB = 1;
            walls.add(secondPortal);
            s.walls.add(walls.size-1);
        walls.add(new Wall(185, 125,  175, 80 )); s.walls.add(walls.size-1);
        sectors.add(s);

        s = new Sector(); s.floorZ = -40; s.ceilZ = 20;
        s.walls.add(walls.size-2); //Add 'secondPortal'
        walls.add(new Wall( 90, 125, 90, 145    )); s.walls.add(walls.size-1);
        walls.add(new Wall( 90, 145, 135, 145   )); s.walls.add(walls.size-1);
        walls.add(new Wall( 135, 145, 135, 200   )); s.walls.add(walls.size-1);
        walls.add(new Wall( 135, 200, 150, 200   )); s.walls.add(walls.size-1);
        walls.add(new Wall( 150, 200, 150, 145   )); s.walls.add(walls.size-1);
        walls.add(new Wall( 150, 145, 185, 125  )); s.walls.add(walls.size-1);
        sectors.add(s);

    }

}
