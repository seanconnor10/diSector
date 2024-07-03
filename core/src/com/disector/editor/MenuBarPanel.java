package com.disector.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.disector.Sector;
import com.disector.Wall;
import com.disector.gameworld.GameWorld;

public class MenuBarPanel extends Panel{
    Button saveButton;
    Button loadButton;

    static final int panelHeight = 40;

    public MenuBarPanel() {
        rect.set(0,0, Gdx.graphics.getWidth(), panelHeight);
        saveButton = new Button();
        saveButton.setRect(5,5, 90, 30);
        loadButton = new Button();
        loadButton.setRect(125, 5, 90, 30);
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
        shape.begin(ShapeRenderer.ShapeType.Filled);

        shape.setColor(Color.GOLDENROD);
        drawBackground(shape);

        shape.setColor(Color.MAROON);
        drawRect(shape, new Rectangle(0,0,Gdx.graphics.getWidth(), 2));

        shape.setColor(Color.DARK_GRAY);
        drawRect(shape, new Rectangle(0,panelHeight-2,Gdx.graphics.getWidth(), 2));

        shape.setColor(Color.TEAL);
        drawRect(shape, saveButton.rect);
        drawRect(shape, loadButton.rect);

        shape.end();

        //batch.begin();
        //Editor.font.draw(batch, saveButton.text, 10, 10);
        //batch.end();
    }
}
