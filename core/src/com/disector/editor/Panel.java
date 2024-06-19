package com.disector.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

import com.disector.renderer.Renderer;

public abstract class Panel {
    Rectangle rect = new Rectangle();

    abstract void resize(int x, int y, int w, int h);

    abstract void step();

    abstract void control();

    abstract void draw(SpriteBatch batch, ShapeRenderer shape);

    void drawBackground(ShapeRenderer shape) {
        shape.rect(rect.x, Gdx.graphics.getHeight()-rect.height-rect.y, rect.width, rect.height);
    }
}
