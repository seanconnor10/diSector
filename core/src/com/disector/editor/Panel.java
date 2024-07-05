package com.disector.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import com.disector.gameworld.GameWorld;
import com.disector.Wall;
import com.disector.Sector;

public abstract class Panel {
    Editor editor;
    static int mouseX, mouseY;
    int localMouseX, localMouseY;

    Rectangle rect = new Rectangle();

    abstract void resize(int x, int y, int w, int h);

    abstract void step(GameWorld game, Array<Wall> walls, Array<Sector> sectors);

    abstract void draw(SpriteBatch batch, ShapeRenderer shape);

    void setLocalMouse() {
        localMouseX = mouseX-(int)rect.x;
        localMouseY = mouseY-(int)rect.y;
    }

    void drawBackground(ShapeRenderer shape) {
        shape.rect(rect.x, Gdx.graphics.getHeight()-rect.height-rect.y, rect.width, rect.height);
    }

    void drawRect(ShapeRenderer shape, Rectangle r) {
        shape.rect(r.x, Gdx.graphics.getHeight()-r.height-r.y, r.width, r.height);
    }

    boolean localMouseInButton(Button b) {
        return b.rect.contains(localMouseX, localMouseY);
    }
}
