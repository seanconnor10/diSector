package com.disector.editor2;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

import com.badlogic.gdx.utils.ScreenUtils;
import com.disector.Application;
import com.disector.Wall;

class NewEditorMapRenderer {
    private final Application app;

    private final ShapeRenderer shape = new ShapeRenderer();
    FrameBuffer frame;

    float camX = 0, camY = 0;
    float zoom = 1f;

    float halfWidth, halfHeight;
    int gridSize = 32;

    NewEditorMapRenderer(Application app, Rectangle startDimensions) {
        this.app = app;
        refreshPanelSize(startDimensions);
    }

    // -------------------------------------------------

    void refreshPanelSize(Rectangle r) {
        int w = Math.max( (int) r.width,  1 );
        int h = Math.max( (int) r.height, 1 );
        frame = new FrameBuffer(app.pixelFormat, w, h, false);
        halfWidth = w/2f;
        halfHeight = h/2f;
        shape.setProjectionMatrix(new Matrix4().setToOrtho2D(camX, camY, w, h));
    }

    // --------Draw Methods---------------------------

    public void render() {
        frame.begin();
        ScreenUtils.clear(Color.BLACK);
        shape.begin(ShapeRenderer.ShapeType.Line);

        drawGrid();
        drawWalls();

        shape.end();
        frame.end();
    }

    public void drawWalls() {
        for (Wall wall : app.walls) {
            shape.setColor(wall.isPortal ? Color.CORAL : Color.WHITE);
            drawLine(wall.x1, wall.y1, wall.x2, wall.y2);

            if (wall.isPortal) continue;
            //Draw Normal Notch
            shape.getColor().a *= 0.6f;
            float centerX = (wall.x1 + wall.x2) / 2f;
            float centerY = (wall.y1 + wall.y2) / 2f;
            drawLine(
                    centerX,
                    centerY,
                    centerX + (float) ( Math.cos(wall.normalAngle) * Math.min(15.0f/zoom, 15.f) ),
                    centerY + (float) ( Math.sin(wall.normalAngle) * Math.min(15.0f/zoom, 15.f) )
            );
        }
    }

    public void drawGrid() {
        shape.setColor(0, 0.2f, 0.1f, 0.5f);
        for (float worldX = gridSize*(int)((camX-(halfWidth/zoom))/gridSize); worldX<camX+(halfWidth/zoom); worldX+=gridSize) {
            drawLine(worldX, camY-halfHeight/zoom, worldX, camY+halfHeight/zoom);
        }
        for (float worldY = gridSize*(int)((camY-(halfHeight/zoom))/gridSize); worldY<camY+(halfHeight/zoom); worldY+=gridSize) {
            drawLine(camX-halfWidth/zoom, worldY, camX+halfWidth/zoom, worldY);
        }

        shape.setColor(0.1f, 0.2f, 0.3f, 0.8f);
        drawLine(-10000,0, 10000, 0);
        drawLine(0, -10000, 0, 10000);
    }

    // ----- Draw Primitives -----------------------------

    private void drawCircle(float x, float y, float r) {
        shape.circle(halfWidth+zoom*(x-camX), halfHeight+zoom*(y-camY), r*zoom);
    }

    private void drawLine(float x, float y, float x2, float y2) {
        shape.line(
                halfWidth+zoom*(x-camX),
                halfHeight+zoom*(y-camY),
                halfWidth+zoom*(x2-camX),
                halfHeight+zoom*(y2-camY)
        );
    }
}
