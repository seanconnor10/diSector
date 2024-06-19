package com.disector.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import com.disector.Application;
import com.disector.Wall;

public class EditorMapRenderer extends MapRenderer {
    private ShapeRenderer shape;

    public EditorMapRenderer(Application app, int frameWidth, int frameHeight) {
        super(app);
        shape = new ShapeRenderer();
        changeSize(frameHeight, frameHeight);
    }

    @Override
    public void renderWorld() {
        buffer.begin();
        shape.begin(ShapeRenderer.ShapeType.Line);
        ScreenUtils.clear(Color.BLACK);
        for (Wall w : walls) {
            drawWall(w);
        }
        shape.end();
        buffer.end();
    }

    @Override
    public void resizeFrame(int w, int h) {

    }

    public void changeSize(int w, int h) {
        if (shape != null) shape.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, w, h));
        buffer = new FrameBuffer(app.pixelFormat, w, h, false);
        frameWidth = w;
        frameHeight = h;
        halfWidth = frameWidth/2;
        halfHeight = frameHeight/2;
    }

    public void drawWall(Wall wall) {
        WallTransform w = new WallTransform(wall);
        shape.setColor( wall.isPortal ? Color.CORAL : Color.WHITE );
        shape.line(w.x1, w.y1, w.x2, w.y2);
    }

    private class WallTransform {
        float x1, y1, x2, y2;
        float zoom = camFOV / 100;

        private WallTransform(Wall w) {
            x1 = halfWidth - zoom*(w.x1-camX);
            y1 = halfHeight - zoom*(w.y1-camY);
            x2 = halfWidth - zoom*(w.x2-camX);
            y2 = halfHeight - zoom*(w.y2-camY);
        }
    }

}
