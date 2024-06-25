package com.disector.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import com.disector.Application;
import com.disector.Wall;

public class EditorMapRenderer extends MapRenderer {
    private ShapeRenderer shape;

    public int gridSize = 32;

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
        drawGrid();
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
        w = Math.max(1, w);
        h = Math.max(1, h);
        if (shape != null) shape.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, w, h));
        buffer = new FrameBuffer(app.pixelFormat, w, h, false);
        frameWidth = w;
        frameHeight = h;
        halfWidth = frameWidth/2.f;
        halfHeight = frameHeight/2.f;
    }

    public void drawWall(Wall wall) {
        //WallTransform w = new WallTransform(wall);
        shape.setColor( wall.isPortal ? Color.CORAL : Color.WHITE );
        //shape.line(w.x1, w.y1, w.x2, w.y2);
        line(wall.x1, wall.y1, wall.x2, wall.y2);
    }

    public void drawGrid() {
        shape.setColor(0, 0.2f, 0.1f, 0.5f);
        for (float worldX = gridSize*(int)((camX-(halfWidth/camFOV))/gridSize); worldX<camX+(halfWidth/camFOV); worldX+=gridSize) {
            line(worldX, camY-halfHeight/camFOV, worldX, camY+halfHeight/camFOV);
        }
        for (float worldY = gridSize*(int)((camY-(halfHeight/camFOV))/gridSize); worldY<camY+(halfHeight/camFOV); worldY+=gridSize) {
            line(camX-halfWidth/camFOV, worldY, camX+halfWidth/camFOV, worldY);
        }
        shape.setColor(0.f, 0.2f, 0.85f, 0.7f);
        line(0,100,0,-100);
        line(-100,0,100,0);
        circle(0,0,5);
    }

    void circle(float x, float y, float r) {
        shape.circle(halfWidth+camFOV*(x-camX), halfHeight+camFOV*(y-camY), r*camFOV);
    }

    void line(float x, float y, float x2, float y2) {
        shape.line(
                halfWidth+camFOV*(x-camX),
                halfHeight+camFOV*(y-camY),
                halfWidth+camFOV*(x2-camX),
                halfHeight+camFOV*(y2-camY)
        );
    }

    private class WallTransform {
        float x1, y1, x2, y2;

        private WallTransform(Wall w) {
            x1 = halfWidth - camFOV*(w.x1-camX);
            y1 = halfHeight - camFOV*(w.y1-camY);
            x2 = halfWidth - camFOV*(w.x2-camX);
            y2 = halfHeight - camFOV*(w.y2-camY);
        }
    }

}
