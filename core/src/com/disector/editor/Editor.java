package com.disector.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.disector.Application;
import com.disector.Sector;
import com.disector.Wall;
import com.disector.gameworld.GameWorld;
import com.disector.maploader.MapLoader;
import com.disector.maploader.OldTextFormatMapLoader;
import com.disector.maploader.TextFileMapLoader;

import java.util.ArrayList;

public class Editor {
    private Application app;
    private GameWorld world;

    private RenderViewPanel viewPanel;
    private MapViewPanel mapPanel;
    private MenuBarPanel menuPanel;
    private ArrayList<Panel> panelList = new ArrayList<>();

    private float animProgress = 0f;
    float animCycle = 0f;

    Wall selectedWall = null;
    Sector selectedSector = null;

    public Editor(Application app, GameWorld world) {
        this.app = app;
        this.world = world;
        viewPanel = new RenderViewPanel(app, this);
        mapPanel = new MapViewPanel(app, this);
        menuPanel = new MenuBarPanel();
        panelList.add(viewPanel);
        panelList.add(menuPanel);
        panelList.add(mapPanel);
    }

    public void step(float dt) {
        animProgress += 2*dt;
        animProgress = animProgress%3;
        animCycle = (float) Math.sin(animProgress);
        temporaryControls(dt);
        Panel.mouseX = Gdx.input.getX();
        Panel.mouseY = Gdx.input.getY();
        Panel panelInFocus = mapPanel;
        panelInFocus.step(world, app.walls, app.sectors);
    }

    public void moveRenderViewCamera(float x, float y) {
        viewPanel.renderer.placeCamera(x, y);
    }

    public void resize(int w, int h) {
        int menuBarHeight = (int) menuPanel.rect.height;
        for (Panel panel : panelList) {
            if (panel.getClass().equals(RenderViewPanel.class)) {
                panel.resize(0, menuBarHeight, w/3, w/3*9/16);
            } else if (panel.getClass().equals(MenuBarPanel.class)) {
                panel.resize(0, 0, w, menuBarHeight);
            } else if (panel.getClass().equals(MapViewPanel.class)) {
                panel.resize(w/3, menuBarHeight, w-(w/3), h-menuBarHeight);
            }
        }
    }

    public void draw() {
        ScreenUtils.clear(Color.DARK_GRAY);
        SpriteBatch batch = app.batch;
        ShapeRenderer shape = app.shape;
        for (Panel panel : panelList) {
            panel.draw(batch, shape);
        }
    }

    private void temporaryControls(float dt) {
        //Temporary Load and Save
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                MapLoader mapLoader = new OldTextFormatMapLoader(app.sectors, app.walls, world);
                mapLoader.load("MAPS/SHED");
            } else {
                MapLoader mapLoader = new TextFileMapLoader(app.sectors, app.walls, world);
                mapLoader.load("MAPS/test.txt");
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            MapLoader mapLoader = new TextFileMapLoader(app.sectors, app.walls, world);
            mapLoader.save("MAPS/test.txt");
        }

        //Temporary Movement
        float mapZoom = mapPanel.renderer.getFov();
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            mapPanel.renderer.shiftCamera(new Vector2(0, 200.f * dt / (float)Math.sqrt(mapZoom) ));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mapPanel.renderer.shiftCamera(new Vector2(200.f * dt / (float)Math.sqrt(mapZoom), 0));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            mapPanel.renderer.shiftCamera(new Vector2(0, -200.f * dt / (float)Math.sqrt(mapZoom)));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mapPanel.renderer.shiftCamera(new Vector2(-200.f * dt / (float)Math.sqrt(mapZoom), 0));
        }

        //Temporary Zoom
        //float mapZoom = mapPanel.renderer.getFov();
        if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) mapZoom += mapZoom < 1 ? 0.1f : mapZoom < 4 ? 0.5f : 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) mapZoom -= mapZoom < 1 ? 0.1f : mapZoom < 4 ? 0.5f : 2;
        if (mapZoom < 0.1f) mapZoom = 0.1f;
        if (mapZoom > 20) mapZoom = 20;
        mapPanel.renderer.setFov(mapZoom);

        //GridSize
        if (Gdx.input.isKeyJustPressed(Input.Keys.G))
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                mapPanel.renderer.gridSize /= 2;
            else
                mapPanel.renderer.gridSize *= 2;
        if (mapPanel.renderer.gridSize < 4)
            mapPanel.renderer.gridSize = 4;

    }

}
