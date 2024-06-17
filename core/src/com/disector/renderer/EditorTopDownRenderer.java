package com.disector.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import com.disector.Application;
import com.disector.Wall;

public class EditorTopDownRenderer extends TopDownRenderer{
    private ShapeRenderer shape;

    public EditorTopDownRenderer(Application app, int frameWidth, int frameHeight) {
        super(app);
        shape = new ShapeRenderer();
        shape.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, frameWidth, frameHeight));
        buffer = new FrameBuffer(app.pixelFormat, frameWidth, frameHeight, false);
    }

    @Override
    public void renderWorld() {
        buffer.begin();
        shape.begin(ShapeRenderer.ShapeType.Line);
        ScreenUtils.clear(Color.PURPLE);
        for (Wall w : walls) {
            drawWall(w);
        }
        shape.end();
        buffer.end();
    }
    @Override
    public void resizeFrame(int w, int h) {

    }

    public void drawWall(Wall wall) {
        WallTransform w = new WallTransform(wall);
        shape.line(w.x1, w.y1, w.x2, w.y2);
    }

    private class WallTransform {
        float x1, y1, x2, y2;

        private WallTransform(Wall w) {
            x1 = w.x1-camX;
            y1 = w.y1-camY;
            x2 = w.x2-camX;
            y2 = w.y2-camY;
        }
    }

}
