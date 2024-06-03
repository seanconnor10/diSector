package com.disector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import com.disector.gameworld.GameWorld;
import com.disector.editor.EditorInterface;
import com.disector.renderer.Renderer;

public class App extends ApplicationAdapter {
    private GameWorld gameWorld;
    private Renderer renderer;
    private EditorInterface editor;

    public final Array<Wall> walls = new Array<>();
    public final Array<Sector> sectors = new Array<>();

    public int frameWidth = 400;
    public int frameHeight = 300;

    public SpriteBatch batch;

    @Override
    public void create () {
        batch = new SpriteBatch();
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
