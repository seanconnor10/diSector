package com.disector.renderer;

import com.disector.Application;
import com.disector.Wall;

public class EditorTopDownRenderer extends TopDownRenderer{
    public EditorTopDownRenderer(Application app) {
        super(app);
    }

    @Override
    public void renderWorld() {
        buffer.begin();
        for (Wall w : walls) {
            drawWall(w);
        }
        buffer.end();
    }

    public void drawWall(Wall wall) {
        WallTransform w = new WallTransform(wall);
        app.shape.line(w.x1, w.y1, w.x2, w.y2);
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
