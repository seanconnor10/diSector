package com.disector.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.disector.Application;
import com.disector.Wall;

public class MapOverlayRenderer extends TopDownRenderer {
    private final Color backgroundColor  = new Color(0,0, 0.2f, 0.4f);

    public MapOverlayRenderer(Application app) {
        super(app);
    }

    @Override
    public void renderWorld() {
        //batch.begin();
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
        //batch.end();
    }


}
