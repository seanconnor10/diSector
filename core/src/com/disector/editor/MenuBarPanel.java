package com.disector.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.disector.Sector;
import com.disector.Wall;
import com.disector.gameworld.GameWorld;

public class MenuBarPanel extends Panel{
    Button saveButton;
    Button loadButton;

    static final int panelHeight = 40;

    public MenuBarPanel(Editor editor) {
        this.editor = editor;
        rect.set(0,0, Gdx.graphics.getWidth(), panelHeight);
        saveButton = new Button("Save");
        saveButton.setRect(5,5, 90, 30);
        loadButton = new Button("Load");
        loadButton.setRect(125, 5, 90, 30);
    }

    @Override
    void resize(int x, int y, int w, int h) {
        rect.width=w;
    }

    @Override
    void step(GameWorld game, Array<Wall> walls, Array<Sector> sectors) {
        setLocalMouse();

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (localMouseInButton(loadButton)) {
                editor.app.loadMapOldFormat("MAPS/OLD-BANK");
            }
        }
    }

    @Override
    void draw(SpriteBatch batch, ShapeRenderer shape) {
        shape.begin(ShapeRenderer.ShapeType.Filled);

        shape.setColor(Color.LIGHT_GRAY);
        drawBackground(shape);

        shape.setColor(Color.TEAL);
        drawRect(shape, new Rectangle(0,0,Gdx.graphics.getWidth(), 2));

        shape.setColor(Color.GOLDENROD);
        drawRect(shape, new Rectangle(0,panelHeight-2, Gdx.graphics.getWidth(), 2));

        shape.setColor(Color.DARK_GRAY);
        drawRect(shape, saveButton.rect);
        drawRect(shape, loadButton.rect);

        shape.end();

        batch.begin();
        drawButtonText(saveButton, batch);
        drawButtonText(loadButton, batch);
        batch.end();
    }

    private void drawButtonText(Button b, SpriteBatch batch) {
        Editor.font.draw(batch, b.text, 4+rect.x+b.rect.x, Gdx.graphics.getHeight()-rect.y-b.rect.y-4);
    }
}
