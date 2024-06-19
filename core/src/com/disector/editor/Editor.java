package com.disector.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.disector.Application;

import java.util.ArrayList;

public class Editor {
    private Application app;

    private RenderViewPanel viewPanel;
    private MapViewPanel mapPanel;
    private MenuBarPanel menuPanel;
    private ArrayList<Panel> panelList = new ArrayList<>();

    public Editor(Application app) {
        this.app = app;
        viewPanel = new RenderViewPanel(app);
        mapPanel = new MapViewPanel(app);
        menuPanel = new MenuBarPanel();
        panelList.add(viewPanel);
        panelList.add(menuPanel);
        panelList.add(mapPanel);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void step(float dt) {

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

}
