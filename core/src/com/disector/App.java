package com.disector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import com.disector.gameworld.GameWorld;
import com.disector.editor.EditorInterface;
import com.disector.renderer.RendererInterface;

public class App extends ApplicationAdapter {
    GameWorld gameWorld;
    RendererInterface Renderer;
    EditorInterface Editor;

    Array<Wall> walls = new Array<>();
    Array<Sector> sectors = new Array<>();

    SpriteBatch batch;

    @Override
    public void create () {

    }

    @Override
    public void render () {
        ScreenUtils.clear(0.1f, 0, 0, 1);
    }

    @Override
    public void dispose () {
        batch.dispose();
    }

}
