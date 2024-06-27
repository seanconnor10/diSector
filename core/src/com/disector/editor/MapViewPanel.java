package com.disector.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import com.disector.gameworld.GameWorld;
import com.disector.Wall;
import com.disector.Sector;
import com.disector.Application;
import com.disector.renderer.EditorMapRenderer;

public class MapViewPanel extends Panel{
    Application app;
    EditorMapRenderer renderer;

    public MapViewPanel(Application app, Editor editor) {
        renderer = new EditorMapRenderer(app, 100, 100);
        this.editor = editor;
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
    void step(GameWorld game, Array<Wall> walls, Array<Sector> sectors) {
        setLocalMouse();
        Vector2 mouseWorldPos = renderer.getMouseWorldPos(localMouseX, localMouseY);
        //System.out.printf("GlobalX: %d  GlobalY: %d\nLocalX: %d LocalY: %d\nWorldX: %f  WorldY: %f\n\n", mouseX, mouseY, localMouseX, localMouseY, mouseWorldPos.x, mouseWorldPos.y);
        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            editor.moveRenderViewCamera(mouseWorldPos.x, mouseWorldPos.y);
            //game.setPos(game.player1, mouseWorldPos.x, mouseWorldPos.y);
        }
    }


    @Override
    void control() {

    }
}
