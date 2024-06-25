package com.disector.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.ScreenUtils;

import com.disector.Application;
import com.disector.Wall;
import com.disector.gameworld.GameWorld;

public class GameMapRenderer extends MapRenderer {
    private final GameWorld world;

    private final Color backgroundColor  = new Color(0.f, 0.f, 0.f, 0.8f);

    public GameMapRenderer(Application app, GameWorld world) {
        super(app);
        this.world = world;
    }

    @Override
    public void renderWorld() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) camFOV += 0.25f;
        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) camFOV -= 0.25f;

        buffer.begin();

        ScreenUtils.clear(backgroundColor);

        app.shape.begin(ShapeRenderer.ShapeType.Line);
        drawWalls();
        drawPlayer();
        app.shape.end();

        buffer.end();

    }

    private void drawWalls() {
        for (Wall w : walls) {
            app.shape.setColor( w.isPortal ? Color.RED : Color.WHITE );
            line(w.x1, w.y1, w.x2, w.y2);
        }
    }

    private void drawPlayer() {
        Vector4 pos = world.getPlayerPosition();
        float radius = world.getPlayerRadius();
        app.shape.setColor(Color.GOLDENROD);
        circle(pos.x, pos.y, radius);
        app.shape.setColor(Color.PINK);
        line(pos.x, pos.y, pos.x+radius*(float)Math.cos(pos.w), pos.y+radius*(float)Math.sin(pos.w));
    }

    void circle(float x, float y, float r) {
        app.shape.circle(halfWidth+camFOV*(x-camX), halfHeight+camFOV*(y-camY), r*camFOV);
    }

    void line(float x, float y, float x2, float y2) {
        app.shape.line(
                halfWidth+camFOV*(x-camX),
                halfHeight+camFOV*(y-camY),
                halfWidth+camFOV*(x2-camX),
                halfHeight+camFOV*(y2-camY)
        );
    }

}
