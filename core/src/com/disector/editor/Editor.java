package com.disector.editor;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.disector.Application;
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

    public Editor(Application app, GameWorld world) {
        this.app = app;
        this.world = world;
        viewPanel = new RenderViewPanel(app);
        mapPanel = new MapViewPanel(app);
        menuPanel = new MenuBarPanel();
        panelList.add(viewPanel);
        panelList.add(menuPanel);
        panelList.add(mapPanel);
    }

    public void step(float dt) {
        temporaryControls(dt);
        Panel.mouseX = Gdx.input.getX();
        Panel.mouseY = Gdx.input.getY();
        Panel panelInFocus = mapPanel;
        panelInFocus.step();
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
            MapLoader mapLoader = new TextFileMapLoader(app.sectors, app.walls, world);
            mapLoader.load("MAPS/test.txt");
//            MapLoader mapLoader = new OldTextFormatMapLoader(app.sectors,app.walls, world);
//            mapLoader.load("MAPS/SHED");
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
            mapPanel.renderer.gridSize *= Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 0.5f : 2.f;
        if (mapPanel.renderer.gridSize < 4)
            mapPanel.renderer.gridSize = 4;

    }

}
