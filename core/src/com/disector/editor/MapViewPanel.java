package com.disector.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.disector.Application;
import com.disector.renderer.EditorMapRenderer;

public class MapViewPanel extends Panel{
    Application app;
    EditorMapRenderer renderer;

    public MapViewPanel(Application app) {
        renderer = new EditorMapRenderer(app, 100, 100);
    }

    @Override
    void resize(int x, int y, int w, int h) {
        rect.x = x;
        rect.y = y;
        rect.width = w;
        rect.height = h;
        renderer.changeSize(w, h);
    }

    @Override
    void draw(SpriteBatch batch, ShapeRenderer shape) {
        /*If moved camera/Updated Map*/ renderer.renderWorld();
        TextureRegion tex = renderer.copyPixels();
        tex.flip(false, true);
        batch.begin();
        batch.draw(tex, rect.x, Gdx.graphics.getHeight()-rect.height-rect.y, rect.width, rect.height);
        batch.end();
    }

    @Override
    void step() {
        setLocalMouse();
        Vector2 mouseWorldPos = renderer.getMouseWorldPos(localMouseX, localMouseY);
        //System.out.printf("GlobalX: %d  GlobalY: %d\nLocalX: %d LocalY: %d\nWorldX: %f  WorldY: %f\n\n", mouseX, mouseY, localMouseX, localMouseY, mouseWorldPos.x, mouseWorldPos.y);
    }


    @Override
    void control() {

    }
}
