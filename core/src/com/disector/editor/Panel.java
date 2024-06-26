package com.disector.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public abstract class Panel {
    static int mouseX, mouseY;
    int localMouseX, localMouseY;

    Rectangle rect = new Rectangle();

    abstract void resize(int x, int y, int w, int h);

    abstract void step();

    abstract void control();

    abstract void draw(SpriteBatch batch, ShapeRenderer shape);

    void setLocalMouse() {
        localMouseX = mouseX-(int)rect.x;
        localMouseY = mouseY-(int)rect.y;
    }

    void drawBackground(ShapeRenderer shape) {
        shape.rect(rect.x, Gdx.graphics.getHeight()-rect.height-rect.y, rect.width, rect.height);
    }
}
