package com.disector.renderer;

import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.disector.Application;
import com.disector.Wall;

public class GameMapRenderer extends MapRenderer {
    private final Color backgroundColor  = new Color(0,0, 0.2f, 0.4f);

    public GameMapRenderer(Application app) {
        super(app);
    }

    @Override
    public void renderWorld() {
        buffer.begin();
        ScreenUtils.clear(0,0,0,0);
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
        buffer.end();

    }

}
