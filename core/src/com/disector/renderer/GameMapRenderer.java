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

    private final Color backgroundColor  = new Color(0.f, 0.f, 0.f, 0.65f);
    private final Color PORTAL_COLOR = new Color(0xD0FFD0FF);
    private final Color WALL_COLOR = new Color(0x7050D0FF);

    public boolean mapRotates = true;

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
            //Avoid drawing portals without a difference in floor height
            float floorDifference = 0f;

            if (w.isPortal) {
                floorDifference = Math.abs( sectors.get(w.linkA).floorZ - sectors.get(w.linkB).floorZ );
                if (floorDifference == 0f)
                    continue;
            }


            app.shape.setColor( w.isPortal ? PORTAL_COLOR : WALL_COLOR );

            if (w.isPortal) {
                float portalShadeLERPFactor = floorDifference / 100;
                portalShadeLERPFactor = Math.max(0.3f, Math.min(1.0f, portalShadeLERPFactor));
                app.shape.getColor().a *= portalShadeLERPFactor;
                //app.shape.getColor().r *= 1.f + portalShadeLERPFactor;
                app.shape.getColor().g *= 1.0f - portalShadeLERPFactor;
            }

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
        if (mapRotates)
            lineROTATED(x, y, x2, y2);
        else
            lineSTATIC(x, y, x2, y2);
    }

    private void lineSTATIC(float x, float y, float x2, float y2) {
        app.shape.line(
                halfWidth+camFOV*(x-camX),
                halfHeight+camFOV*(y-camY),
                halfWidth+camFOV*(x2-camX),
                halfHeight+camFOV*(y2-camY)
        );
    }

    private void lineROTATED(float x, float y, float x2, float y2) {
        final double pi4 = Math.PI/2.0;
        float cos = (float) Math.cos(pi4-camR);
        float sin = (float) Math.sin(pi4-camR);
        app.shape.line(
                halfWidth+camFOV*( cos*(x-camX) - sin*(y-camY) ),
                halfHeight+camFOV*( cos*(y-camY) + sin*(x-camX) ),
                halfWidth+camFOV*( cos*(x2-camX) - sin*(y2-camY) ),
                halfHeight+camFOV*( cos*(y2-camY) + sin*(x2-camX) )
        );
    }

}
