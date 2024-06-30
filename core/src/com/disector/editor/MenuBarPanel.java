package com.disector.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.disector.Sector;
import com.disector.Wall;
import com.disector.gameworld.GameWorld;

public class MenuBarPanel extends Panel{
    Button saveButton;
    Button loadButton;

    public MenuBarPanel() {
        rect.set(0,0, Gdx.graphics.getWidth(), 30);
        saveButton = new Button();
        saveButton.setRect(0,0, 100, 30);
        loadButton = new Button();
        loadButton.setRect(120, 0, 100, 30);
    }

    @Override
    void resize(int x, int y, int w, int h) {
        rect.width=w;
    }

    @Override
    void step(GameWorld game, Array<Wall> walls, Array<Sector> sectors) {

    }

    @Override
    void draw(SpriteBatch batch, ShapeRenderer shape) {
        shape.setColor(Color.GOLDENROD);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        drawBackground(shape);
        shape.end();
    }
}
