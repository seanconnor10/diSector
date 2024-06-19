package com.disector.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.disector.Application;
import com.disector.inputrecorder.InputRecorder;
import com.disector.renderer.DimensionalRenderer;
import com.disector.renderer.SoftwareRenderer;


public class RenderViewPanel extends Panel{
    DimensionalRenderer renderer;

    RenderViewPanel(Application app) {
        renderer = new SoftwareRenderer(app);
        renderer.resizeFrame(app.frameWidth, app.frameHeight);
    }

    @Override
    void resize(int x, int y, int w, int h) {
        rect.x = x;
        rect.y = y;
        rect.width = w;
        rect.height = h;
    }

    @Override
    void control() {
        boolean forwardPressed = InputRecorder.getKeyInfo("FORWARD").isDown;

        boolean cameraMoved = false;

        cameraMoved = forwardPressed;

        if (cameraMoved) renderer.renderWorld();
    }

    @Override
    void step() {
    }

    @Override
    void draw(SpriteBatch batch, ShapeRenderer shape) {
        shape.setColor(Color.FIREBRICK);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        drawBackground(shape);
        shape.end();

        renderer.renderWorld();
        TextureRegion frame = renderer.copyPixels();
        frame.flip(false, true);
        batch.begin();
        batch.draw(frame, rect.x, Gdx.graphics.getHeight()-rect.height-rect.y, rect.width, rect.height);
        batch.end();
    }
}
