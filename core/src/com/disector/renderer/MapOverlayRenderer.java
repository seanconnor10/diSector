package com.disector.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.disector.Application;
import com.disector.Wall;

public class MapOverlayRenderer extends Renderer {
    private final Color backgroundColor  = new Color(0,0, 0.2f, 0.4f);

    public MapOverlayRenderer(Application app) {
        super(app);
    }

    @Override
    public void renderWorld() {
        app.shape.begin(ShapeRenderer.ShapeType.Line);
        for (Wall w : walls) {
            app.shape.setColor( w.isPortal ? Color.RED : Color.WHITE );
            app.shape.line(
                halfWidth+w.x1-camX,
                halfHeight+w.y1-camY,
                halfWidth+w.x2-camX,
                halfHeight+w.y2-camY
            );
        }
        app.shape.end();
    }

    @Override
    public void drawFrame() {
        //Avoid calling super.drawFrame since we aren't currently using the Pixmap buffer
    }

    @Override
    public void resizeFrame(int w, int h) {
        //Avoid work done in super.resizeFrame();
        frameWidth = Gdx.graphics.getWidth();
        frameHeight = Gdx.graphics.getHeight();
        halfWidth = frameWidth / 2.f;
        halfHeight = frameHeight /2.f;
    }

}
